package com.fastbee.isup.scheduled;

import java.util.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fastbee.isup.model.ChanStatusObj;
import com.fastbee.isup.model.IsupDevInfo;
import com.fastbee.isup.model.xml.DeviceInfo;
import com.fastbee.isup.sdk.isapi.ISAPIService;
import com.fastbee.isup.service.DeviceCacheService;
import com.fastbee.isup.service.IIsupDeviceService;

/**
 * @author oldwei
 * 2021-9-16 14:10
 */
@Slf4j
@Profile("isup")
@Component
@RequiredArgsConstructor
public class ScheduledTask {

    private final DeviceCacheService deviceCacheService;
    private final ISAPIService isapiService;
    private final IIsupDeviceService isupDeviceService;
    // Cron表达式范例：
    //
    //每隔5秒执行一次：*/5 * * * * ?
    //
    //每隔1分钟执行一次：0 */1 * * * ?
    //
    //每天23点执行一次：0 0 23 * * ?
    //
    //每天凌晨1点执行一次：0 0 1 * * ?
    //
    //每月1号凌晨1点执行一次：0 0 1 1 * ?
    //
    //每月最后一天23点执行一次：0 0 23 L * ?
    //
    //每周星期天凌晨1点实行一次：0 0 1 ? * L
    //
    //在26分、29分、33分执行一次：0 26,29,33 * * * ?
    //
    //每天的0点、13点、18点、21点都执行一次：0 0 0,13,18,21 * * ?

    /**
     * TODO 迁移到设备回调中
     * 同步海康设备信息
     * 定期同步设备的通道信息和在线状态
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void syncHikDevice() {
        // 获取所有已注册的设备
        Map<Integer, String> loginIdMap = getAllRegisteredDevices();
        if (loginIdMap.isEmpty()) {
            log.debug("没有在线的设备需要同步");
            return;
        }
        loginIdMap.forEach(this::syncDeviceChannels);
        syncDeviceStatus(loginIdMap.values());
    }

    /**
     * 获取所有已注册的设备
     */
    private Map<Integer, String> getAllRegisteredDevices() {
        Map<Integer, String> result = new HashMap<>();
        deviceCacheService.listAll().stream()
                .filter(d -> d.getLoginId() != null && d.getLoginId() > -1)
                .forEach(d -> result.putIfAbsent(d.getLoginId(), d.getDeviceId()));
        return result;
    }

    /**
     * 同步设备在线状态，1在线2离线
     * @param deviceIds 设备编号
     */
    void syncDeviceStatus(Collection<String> deviceIds) {

    }

    /**
     * 同步单个设备的通道信息
     * 统一处理所有设备类型
     *
     * @param lLoginID 登录句柄
     * @param deviceId 设备ID
     */
    private void syncDeviceChannels(Integer lLoginID, String deviceId) {
        try {
            // 获取设备信息
            DeviceInfo deviceInfo = isapiService.getDevInfo(lLoginID);
            if (deviceInfo == null) {
                log.warn("获取设备信息失败，设备ID: {}, 登录句柄: {}", deviceId, lLoginID);
                return;
            }
            Integer deviceType = deviceInfo.getDeviceType();
            log.debug("同步设备: {}, 类型: {}", deviceId, deviceInfo.getDeviceType());
            // 获取或创建设备对象
            IsupDevInfo device = deviceCacheService.getByDeviceId(deviceId).orElse(new IsupDevInfo());
            device.setDeviceId(deviceId);
            device.setDeviceType(deviceInfo.getDeviceType());
            device.setModel(deviceInfo.getModel());
            device.setManufacturer(deviceInfo.getManufacturer());
            device.setIsOnline(1);
            device.setLoginId(lLoginID);
            // 获取通道列表
            ChanStatusObj chanStatus = isupDeviceService.GetWorkingstatus(lLoginID);
            if (chanStatus == null || chanStatus.getChanStatusList() == null || chanStatus.getChanStatusList().getChanStatus().isEmpty()) {
                log.warn("未能获取设备通道信息，设备ID: {}, 类型: {}", deviceId, deviceType);
            } else {
                // 更新通道列表
                isupDeviceService.updateDeviceChannels(device, chanStatus);
            }
            // 保存设备
            deviceCacheService.saveOrUpdate(device);
        } catch (Exception e) {
            log.error("同步设备通道失败，设备ID: {}, 登录句柄: {}", deviceId, lLoginID, e);
        }
    }
}
