package com.fastbee.media.constant;

public interface MediaConstant {
    interface REDIS {
        String SERVER_STREAMINFO = "media:streamInfo:";
        String PUSH_STREAMINFO = "media:push:streamInfo:";
        String MEDIA_STREAMSESSION = "media:streamSession:";
        String MEDIA_SERVERS_ONLINE = "media:server:online";
        String SEND_RTP_PORT = "SEND_RTP_PORT:";
        String SEND_RTP_INFO_CALLID = "SEND_RTP_PORT:CALL_ID:";
        String SEND_RTP_INFO_STREAM = "SEND_RTP_PORT:STREAM:";
        String SEND_RTP_INFO_CHANNEL = "SEND_RTP_PORT:CHANNEL:";
    }
    interface PREFIX {
        String PREFIX_ISUP = "isup_";
        String PREFIX_ISUP_PLAY = "isup_play";
        String PREFIX_ISUP_PLAYBACK = "isup_playback";
        String PREFIX_GB = "gb_";
        String PREFIX_GB_PLAY = "gb_play";
        String PREFIX_GB_PLAY_RECORD = "gb_playrecord";
        String PREFIX_GB_TALK = "gb_talk";
    }
}
