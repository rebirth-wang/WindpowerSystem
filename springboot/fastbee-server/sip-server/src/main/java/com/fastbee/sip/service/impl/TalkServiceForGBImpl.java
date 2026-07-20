package com.fastbee.sip.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.fastbee.media.domain.CommonChannel;
import com.fastbee.media.enums.ChannelStreamType;
import com.fastbee.media.service.ITalkService;

@Slf4j
@Service(ChannelStreamType.TALK_SERVICE + ChannelStreamType.GB28181)
public class TalkServiceForGBImpl implements ITalkService {

    @Override
    public Object getBroadcastUrl(CommonChannel channel) {
        return null;
    }

    @Override
    public String broadcast(CommonChannel channel) {
        return "";
    }

    @Override
    public Boolean broadcastStop(CommonChannel channel) {
        return true;
    }

    @Override
    public Boolean broadcastInUse(CommonChannel channel) {
        return false;
    }
}
