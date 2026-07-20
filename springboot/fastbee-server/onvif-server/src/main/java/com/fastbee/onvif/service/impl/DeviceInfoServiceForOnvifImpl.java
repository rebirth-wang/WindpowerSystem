package com.fastbee.onvif.service.impl;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fastbee.media.enums.ChannelStreamType;
import com.fastbee.media.model.CommonChannel;
import com.fastbee.media.service.IDeviceInfoService;
import com.fastbee.onvif.domain.OnvifDeviceChannel;
import com.fastbee.onvif.service.IOnvifService;

/**
 * ONVIF 设备信息查询服务实现
 * 通过 OnvifClient 直接调用 ONVIF SOAP 获取设备信息
 * 对应 ONVIF Device Service - GetDeviceInformation + GetCapabilities + GetProfiles
 *
 * @author fastbee
 */
@Slf4j
@Service(ChannelStreamType.DEVICEINFO_SERVICE + ChannelStreamType.ONVIF)
public class DeviceInfoServiceForOnvifImpl implements IDeviceInfoService {

    @Autowired
    private IOnvifService onvifService;

    /**
     * 获取 ONVIF 设备详细信息
     * 通过 OnvifClient 直接调用，完成后将结果持久化到 OnvifDeviceChannel
     *
     * @param channel 通用通道信息（包含 channelId）
     * @return 设备信息 Map（manufacturer/model/firmwareVersion/serialNumber/hardwareId/profileToken）
     */
    @Override
    public Object getDeviceInfo(com.fastbee.media.domain.CommonChannel channel) {
        log.info("[ONVIF 设备信息] 查询设备信息, channelId: {}", channel.getId());
        Map<String, Object> result = onvifService.getDeviceInfo(Math.toIntExact(channel.getId()));
        log.info("[ONVIF 设备信息] 查询完成, channelId: {}, 结果: {}", channel.getId(), result);
        return result;
    }
}
