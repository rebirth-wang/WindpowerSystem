package com.fastbee.sip.server.cluster;

import java.util.List;
import java.util.Set;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fastbee.sip.domain.SipDevice;
import com.fastbee.sip.service.ISipDeviceService;

/**
 * SIP 集群宕机实例清理定时任务
 * <p>
 * 每 60 秒检查活跃实例集合，对比数据库中 server_id，
 * 将归属于已下线实例的设备状态标记为离线。
 */
@Slf4j
@Component
public class SipClusterCleanupTask {

    @Autowired
    private SipServerRegistry sipServerRegistry;

    @Autowired
    private ISipDeviceService sipDeviceService;

    @Scheduled(fixedDelay = 60_000)
    public void cleanupOfflineInstances() {
        try {
            // 1. 获取当前活跃的实例 ID 集合
            Set<String> activeServerIds = sipServerRegistry.getActiveServerIds();
            if (activeServerIds.isEmpty()) {
                // Redis 不可用或无任何实例，跳过本次清理（避免误下线所有设备）
                log.warn("[SIP集群清理] 活跃实例集合为空，跳过本次清理");
                return;
            }

            // 2. 查询数据库中所有已有 serverId 的设备
            List<SipDevice> allDevices = sipDeviceService.list(
                    new LambdaQueryWrapper<SipDevice>()
                            .isNotNull(SipDevice::getServerId)
                            .ne(SipDevice::getServerId, "")
            );

            if (allDevices == null || allDevices.isEmpty()) {
                return;
            }

            // 3. 找出归属于已下线实例的设备，标记离线
            int offlineCount = 0;
            for (SipDevice device : allDevices) {
                String sid = device.getServerId();
                if (sid != null && !sid.isEmpty() && !activeServerIds.contains(sid)) {
                    log.info("[SIP集群清理] 实例 {} 已下线，标记设备离线: {}", sid, device.getDeviceSipId());
                    SipDevice offlineDevice = new SipDevice();
                    offlineDevice.setDeviceSipId(device.getDeviceSipId());
                    offlineDevice.setOnlineStatus("OFF");
                    sipDeviceService.updateSipDeviceStatus(offlineDevice);
                    offlineCount++;
                }
            }

            if (offlineCount > 0) {
                log.info("[SIP集群清理] 本次共标记 {} 台设备离线", offlineCount);
            }
        } catch (Exception e) {
            log.error("[SIP集群清理] 清理任务执行异常", e);
        }
    }
}
