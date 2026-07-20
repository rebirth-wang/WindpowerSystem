package com.fastbee.media.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fastbee.media.domain.CommonChannel;
import com.fastbee.media.model.*;
import com.fastbee.media.service.IComChannelControlService;
import com.fastbee.media.service.IComChannelPtzService;

/**
 * Compatibility facade for the old control service API.
 */
@Service
public class ComChannelControlServiceImpl implements IComChannelControlService {

    private final IComChannelPtzService comChannelPtzService;

    public ComChannelControlServiceImpl(IComChannelPtzService comChannelPtzService) {
        this.comChannelPtzService = comChannelPtzService;
    }

    @Override
    public Boolean ptz(CommonChannel channel, FrontEndControlCodeForPTZ frontEndControlCode) {
        return comChannelPtzService.ptz(channel, frontEndControlCode);
    }

    @Override
    public Boolean preset(CommonChannel channel, FrontEndControlCodeForPreset frontEndControlCode) {
        return comChannelPtzService.preset(channel, frontEndControlCode);
    }

    @Override
    public Boolean fi(CommonChannel channel, FrontEndControlCodeForFI frontEndControlCode) {
        return comChannelPtzService.fi(channel, frontEndControlCode);
    }

    @Override
    public Boolean tour(CommonChannel channel, FrontEndControlCodeForTour frontEndControlCode) {
        return comChannelPtzService.tour(channel, frontEndControlCode);
    }

    @Override
    public Boolean scan(CommonChannel channel, FrontEndControlCodeForScan frontEndControlCode) {
        return comChannelPtzService.scan(channel, frontEndControlCode);
    }

    @Override
    public Boolean auxiliary(CommonChannel channel, FrontEndControlCodeForAuxiliary frontEndControlCode) {
        return comChannelPtzService.auxiliary(channel, frontEndControlCode);
    }

    @Override
    public Boolean wiper(CommonChannel channel, FrontEndControlCodeForWiper controlCode) {
        return comChannelPtzService.wiper(channel, controlCode);
    }

    @Override
    public List<Preset> queryPresetList(CommonChannel channel) {
        return comChannelPtzService.queryPresetList(channel);
    }
}
