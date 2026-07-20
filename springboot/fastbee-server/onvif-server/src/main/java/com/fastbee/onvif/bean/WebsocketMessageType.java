package com.fastbee.onvif.bean;

import com.fasterxml.jackson.annotation.JsonValue;
import gov.nist.javax.sip.header.ParameterNames;

public enum WebsocketMessageType {
    DISCOVERY(1, "discovery"),
    CAMERA_INFO(2, "camera_info"),
    INFO(4, ParameterNames.INFO),
    PTZ_START(5, "ptz_start"),
    PTZ_STOP(6, "ptz_stop"),
    SNAPSHOT(7, "snapshot"),
    TALK_START(8, "talk_start"),
    TALK_STOP(9, "talk_stop"),
    PLAYBACK_START(10, "playback_start"),
    PLAYBACK_STOP(11, "playback_stop"),
    PLAYBACK_QUERY(12, "playback_query");

    private int code;
    private String describe;

    WebsocketMessageType(int code, String describe) {
        this.code = code;
        this.describe = describe;
    }

    @JsonValue
    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescribe() {
        return this.describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }
}
