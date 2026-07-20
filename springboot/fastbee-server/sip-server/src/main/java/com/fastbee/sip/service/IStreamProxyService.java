package com.fastbee.sip.service;

import jakarta.validation.constraints.NotNull;

import com.fastbee.media.model.StreamInfo;
import com.fastbee.media.model.StreamProxy;

public interface IStreamProxyService {

    StreamInfo start(String deviceId, String channelId, Boolean record);

    StreamInfo startProxy(@NotNull StreamProxy streamProxy);

    void stop(String deviceId, String channelId);

    void stopProxy(StreamProxy streamProxy);

    Boolean pulling(String deviceId, String channelId);
}
