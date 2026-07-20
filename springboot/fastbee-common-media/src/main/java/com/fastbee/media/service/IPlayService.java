package com.fastbee.media.service;

import com.fastbee.media.domain.CommonChannel;
import com.fastbee.media.model.Platform;
import com.fastbee.media.model.StreamInfo;

/**
 * 资源能力接入-实时录像
 */
public interface IPlayService {

    StreamInfo play(CommonChannel channel, Platform platform, Boolean record);

    Boolean stopPlay(CommonChannel channel);

    Boolean closeStream(CommonChannel channel, String stream, Boolean check);

}
