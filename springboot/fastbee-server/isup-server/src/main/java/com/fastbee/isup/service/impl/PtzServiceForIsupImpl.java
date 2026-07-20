package com.fastbee.isup.service.impl;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.fastbee.media.domain.CommonChannel;
import com.fastbee.media.enums.ChannelStreamType;
import com.fastbee.media.model.*;
import com.fastbee.media.service.IPTZService;

@Slf4j
@Profile("isup")
@Service(ChannelStreamType.PTZ_SERVICE + ChannelStreamType.ISUP)
public class PtzServiceForIsupImpl implements IPTZService {

    @Override
    public Boolean ptz(CommonChannel channel, PtzInput imput) {
        return null;
    }

    @Override
    public Boolean frontEndCommand(CommonChannel channel, Integer cmdCode, Integer parameter1, Integer parameter2, Integer combindCode) {
        return null;
    }

    @Override
    public Boolean ptz(CommonChannel channel, FrontEndControlCodeForPTZ frontEndControlCode) {
        return null;
    }

    @Override
    public Boolean preset(CommonChannel channel, FrontEndControlCodeForPreset frontEndControlCode) {
        return null;
    }

    @Override
    public Boolean fi(CommonChannel channel, FrontEndControlCodeForFI frontEndControlCode) {
        return null;
    }

    @Override
    public Boolean tour(CommonChannel channel, FrontEndControlCodeForTour frontEndControlCode) {
        return null;
    }

    @Override
    public Boolean scan(CommonChannel channel, FrontEndControlCodeForScan frontEndControlCode) {
        return null;
    }

    @Override
    public Boolean auxiliary(CommonChannel channel, FrontEndControlCodeForAuxiliary frontEndControlCode) {
        return null;
    }

    @Override
    public Boolean wiper(CommonChannel channel, FrontEndControlCodeForWiper frontEndControlCode) {
        return null;
    }

    @Override
    public List<Preset> queryPresetList(CommonChannel channel) {
        return List.of();
    }
}
