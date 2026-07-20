package com.fastbee.iot.data.job;

import java.util.List;
import java.util.Objects;

import jakarta.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fastbee.base.service.ISessionStore;
import com.fastbee.base.session.Session;
import com.fastbee.common.enums.DeviceStatus;
import com.fastbee.common.extend.core.domin.mq.DeviceStatusBo;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.iot.data.service.IMqttMessagePublish;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.model.vo.DeviceStatusVO;
import com.fastbee.iot.service.IDeviceService;
import com.fastbee.iot.service.IDeviceUpdateService;
import com.fastbee.mq.producer.MessageProducer;
import com.fastbee.mqtt.manager.SessionManger;

/**
 * 定时同步设备状态 -- netty版本mqtt使用
 * @author gsb
 * @date 2024/4/11 10:33
 */
@Slf4j
@Component
public class SyncDeviceStatusJob {

    @Resource
    private IDeviceService deviceService;
    @Resource
    private IMqttMessagePublish mqttMessagePublish;
    @Value("${server.broker.enabled}")
    private Boolean enabled;
    @Resource
    private ISessionStore sessionStore;

    @Value("${server.modbus-tcp.poll}")
    private Long poll;

    @Resource
    private IDeviceUpdateService deviceUpdateService;

    @Value("${server.http.keep-alive}")
    private Long httpkeepalive;

    @Value("${server.coap.keep-alive}")
    private Long coapkeepalive;

    /**
     * 定期同步设备状态
     *  1.将异常在线设备变更为离线状态
     *  2.将离线设备但实际在线设备变更为在线
     */
    public void syncDeviceStatus() {
        if (enabled) {
            //获取所有已激活并不是禁用的设备
            List<DeviceStatusVO> deviceStatusVOList = deviceService.selectDeviceActive();
            if (!CollectionUtils.isEmpty(deviceStatusVOList)) {
                for (DeviceStatusVO statusVO : deviceStatusVOList) {
                    Device device = new Device();
                    device.setSerialNumber(statusVO.getSerialNumber());
                    device.setRssi(statusVO.getRssi());
                    device.setProductId(statusVO.getProductId());
                    device.setIsShadow(statusVO.getIsShadow());
                    if (!"HTTP".equals(statusVO.getTransport()) && !"COAP".equals(statusVO.getTransport())) {
                        Session session = SessionManger.getSession(statusVO.getSerialNumber());
                        if (!Objects.isNull(session) && statusVO.getStatus() == DeviceStatus.OFFLINE.getType()) {
                            log.warn("设备上线: " + statusVO.getSerialNumber());
                            device.setStatus(DeviceStatus.ONLINE.getType());
                            deviceUpdateService.updateDeviceStatus(device);
                            mqttMessagePublish.pushDeviceStatus(device, DeviceStatus.ONLINE);
                        }
                        if (Objects.isNull(session) && statusVO.getStatus() == DeviceStatus.ONLINE.getType()) {
                            log.warn("设备下线: " + statusVO.getSerialNumber());
                            device.setStatus(DeviceStatus.OFFLINE.getType());
                            deviceUpdateService.updateDeviceStatus(device);
                            mqttMessagePublish.pushDeviceStatus(device, DeviceStatus.OFFLINE);
                        }
                    } else if("HTTP".equals(statusVO.getTransport())) {
                        if(statusVO.getActiveTime().getTime() + httpkeepalive * 1.5  < System.currentTimeMillis() && statusVO.getStatus() == DeviceStatus.ONLINE.getType()) {
                            log.warn("设备下线: " + statusVO.getSerialNumber());
                            device.setStatus(DeviceStatus.OFFLINE.getType());
                            deviceUpdateService.updateDeviceStatus(device);
                            mqttMessagePublish.pushDeviceStatus(device, DeviceStatus.OFFLINE);
                        }
                    } else if("COAP".equals(statusVO.getTransport())) {
                        if(statusVO.getActiveTime().getTime() + coapkeepalive * 1.5  < System.currentTimeMillis() && statusVO.getStatus() == DeviceStatus.ONLINE.getType()) {
                            log.warn("设备下线: " + statusVO.getSerialNumber());
                            device.setStatus(DeviceStatus.OFFLINE.getType());
                            deviceUpdateService.updateDeviceStatus(device);
                            mqttMessagePublish.pushDeviceStatus(device, DeviceStatus.OFFLINE);
                        }
                    }
                }
            }
        }
        //定时检测modbusTCP的状态
        List<DeviceStatusVO> modbusTcpDeviceList = deviceService.selectModbusTcpDevice();
        if (!CollectionUtils.isEmpty(modbusTcpDeviceList)) {
            for (DeviceStatusVO statusVO : modbusTcpDeviceList) {
                Session session = SessionManger.getSession(statusVO.getSerialNumber());
                if (Objects.isNull(session)) continue;
                //离线判断4分钟
                long deterTime = (long) ((double) poll /1000 * 1.5);
                long lastAccessTime = session.getLastAccessTime();
                long time = (System.currentTimeMillis() - lastAccessTime) / 1000;
                if (time > deterTime) {
                    //处理设备离线
                    DeviceStatusBo statusBo = new DeviceStatusBo()
                            .setStatus(DeviceStatus.OFFLINE)
                            .setSerialNumber(statusVO.getSerialNumber())
                            .setTimestamp(DateUtils.getNowDate());
                    MessageProducer.sendStatusMsg(statusBo);
                    sessionStore.cleanSession(statusVO.getSerialNumber());
                }

            }
        }
    }
}
