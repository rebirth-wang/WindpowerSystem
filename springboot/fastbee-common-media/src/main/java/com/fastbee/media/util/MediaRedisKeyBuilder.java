package com.fastbee.media.util;

import com.fastbee.media.constant.MediaConstant;

public class MediaRedisKeyBuilder {
    public static String buildMediaInfoCacheKey(String mediaServerId, String type, String app, String streamId) {
        return MediaConstant.REDIS.SERVER_STREAMINFO + "_" + type.toUpperCase() + "_" + app + "_" + streamId + "_" + mediaServerId;
    }

    public static String buildPushMediaInfoCacheKey(String app, String stream) {
        return MediaConstant.REDIS.PUSH_STREAMINFO + app + "_" + stream;
    }

    public static String buildVideoStreamCacheKey(String deviceId, String channelId, String sessionid, String type) {
        return MediaConstant.REDIS.MEDIA_STREAMSESSION + deviceId + ":" + channelId + ":" + sessionid + ":" + type;
    }

}
