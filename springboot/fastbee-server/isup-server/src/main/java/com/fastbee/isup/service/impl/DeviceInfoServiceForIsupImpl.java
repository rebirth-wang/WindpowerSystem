package com.fastbee.isup.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.fastbee.media.domain.CommonChannel;
import com.fastbee.media.enums.ChannelStreamType;
import com.fastbee.media.service.IDeviceInfoService;

@Slf4j
@Profile("isup")
@Service(ChannelStreamType.DEVICEINFO_SERVICE + ChannelStreamType.ISUP)
public class DeviceInfoServiceForIsupImpl implements IDeviceInfoService {
    @Override
    public Object getDeviceInfo(CommonChannel channel) {
        return null;
    }
}
