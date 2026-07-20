package com.fastbee.isup.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.fastbee.media.domain.CommonChannel;
import com.fastbee.media.enums.ChannelStreamType;
import com.fastbee.media.service.ITalkService;

@Slf4j
@Profile("isup")
@Service(ChannelStreamType.TALK_SERVICE + ChannelStreamType.ISUP)
public class TalkServiceForIsupImpl implements ITalkService {

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
