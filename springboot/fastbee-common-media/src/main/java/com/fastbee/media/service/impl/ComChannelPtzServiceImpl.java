package com.fastbee.media.service.impl;

import java.util.List;
import java.util.Map;

import javax.sip.message.Response;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fastbee.media.domain.CommonChannel;
import com.fastbee.media.enums.ChannelStreamType;
import com.fastbee.media.model.*;
import com.fastbee.media.service.IComChannelPtzService;
import com.fastbee.media.service.IPTZService;

@Slf4j
@Service
public class ComChannelPtzServiceImpl implements IComChannelPtzService {

    @Autowired
    private Map<String, IPTZService> sourcePtzServiceMap;

    @Override
    public Boolean ptz(CommonChannel channel, PtzInput input) {
        return getPtzService(channel).ptz(channel, input);
    }

    @Override
    public Boolean frontEndCommand(CommonChannel channel, Integer cmdCode, Integer parameter1, Integer parameter2, Integer combindCode2) {
        return getPtzService(channel).frontEndCommand(channel, cmdCode, parameter1, parameter2, combindCode2);
    }

    @Override
    public Boolean ptz(CommonChannel channel, FrontEndControlCodeForPTZ frontEndControlCode) {
        return getPtzService(channel).ptz(channel, frontEndControlCode);
    }

    @Override
    public Boolean preset(CommonChannel channel, FrontEndControlCodeForPreset frontEndControlCode) {
        return getPtzService(channel).preset(channel, frontEndControlCode);
    }

    @Override
    public Boolean fi(CommonChannel channel, FrontEndControlCodeForFI frontEndControlCode) {
        return getPtzService(channel).fi(channel, frontEndControlCode);
    }

    @Override
    public Boolean tour(CommonChannel channel, FrontEndControlCodeForTour frontEndControlCode) {
        return getPtzService(channel).tour(channel, frontEndControlCode);
    }

    @Override
    public Boolean scan(CommonChannel channel, FrontEndControlCodeForScan frontEndControlCode) {
        return getPtzService(channel).scan(channel, frontEndControlCode);
    }

    @Override
    public Boolean auxiliary(CommonChannel channel, FrontEndControlCodeForAuxiliary frontEndControlCode) {
        return getPtzService(channel).auxiliary(channel, frontEndControlCode);
    }

    @Override
    public Boolean wiper(CommonChannel channel, FrontEndControlCodeForWiper frontEndControlCode) {
        return getPtzService(channel).wiper(channel, frontEndControlCode);
    }

    @Override
    public List<Preset> queryPresetList(CommonChannel channel) {
        return getPtzService(channel).queryPresetList(channel);
    }

    private IPTZService getPtzService(CommonChannel channel) {
        if (channel == null) {
            throw new PlayException(Response.BUSY_HERE, "channel not found");
        }
        log.info("[common channel] ptz control, type: {}, device: {}",
                ChannelStreamType.getDateTypeDesc(channel.getDataType()), channel.getDeviceId());
        Integer dataType = channel.getDataType();
        IPTZService ptzService = sourcePtzServiceMap.get(ChannelStreamType.PTZ_SERVICE + dataType);
        if (ptzService == null) {
            log.error("[common channel] data type {} does not support ptz", dataType);
            throw new PlayException(Response.BUSY_HERE, "channel not support");
        }
        return ptzService;
    }
}
