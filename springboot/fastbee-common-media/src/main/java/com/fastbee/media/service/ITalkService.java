package com.fastbee.media.service;

import com.fastbee.media.domain.CommonChannel;

public interface ITalkService {
    Object getBroadcastUrl(CommonChannel channel);
    String broadcast(CommonChannel channel);
    Boolean broadcastStop(CommonChannel channel);
    Boolean broadcastInUse(CommonChannel channel);
}
