package com.fastbee.media.enums;

/**
 * 支持的通道数据类型
 */

public class ChannelStreamType {

    public final static int GB28181 = 1;
    public final static int STREAM_PUSH = 2;
    public final static int STREAM_PROXY = 3;
    public final static int ONVIF = 4;
    public final static int JT_1078 = 5;
    public final static int ISUP = 6;
    public final static int EZVIZ = 7;
    public final static int ICC_DAHUA = 8;

    public final static String DEVICEINFO_SERVICE = "deviceInfoService";
    public final static String PLAY_SERVICE = "playService";
    public final static String PLAYBACK_SERVICE = "playbackService";
    public final static String DOWNLOAD_SERVICE = "downloadService";
    public final static String PTZ_SERVICE = "ptzService";
    public final static String TALK_SERVICE = "talkService";


    public static String getDateTypeDesc(Integer dataType) {
        if (dataType == null) {
            return "未知";
        }

        switch (dataType) {
            case ChannelStreamType.GB28181:
                return "国标28181";
            case ChannelStreamType.STREAM_PUSH:
                return "推流设备";
            case ChannelStreamType.STREAM_PROXY:
                return "拉流代理";
            case ChannelStreamType.ONVIF:
                return "onvif";
            case ChannelStreamType.JT_1078:
                return "部标设备";
            case ChannelStreamType.ISUP:
                return "海康ISUP协议设备";
            case ChannelStreamType.EZVIZ:
                return "萤石云设备";
            case ChannelStreamType.ICC_DAHUA:
                return "大华ICC设备";
            default:
                return "未知";
        }
    }

    public static Integer getDataTypeByTransport(String transport) {
        if (transport == null) {
            return null;
        }

        switch (transport) {
            case "GB28181":
                return ChannelStreamType.GB28181;
            case "STREAM_PUSH":
                return ChannelStreamType.STREAM_PUSH;
            case "STREAM_PROXY":
                return ChannelStreamType.STREAM_PROXY;
            case "ONVIF":
                return ChannelStreamType.ONVIF;
            case "JT_1078":
                return ChannelStreamType.JT_1078;
            case "ISUP":
                return ChannelStreamType.ISUP;
            case "EZVIZ":
                return ChannelStreamType.EZVIZ;
            case "ICC_DAHUA":
                return ChannelStreamType.ICC_DAHUA;
            default:
                return null;
        }
    }

}
