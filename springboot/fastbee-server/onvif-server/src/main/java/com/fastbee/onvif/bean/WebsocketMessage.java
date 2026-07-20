package com.fastbee.onvif.bean;

import com.alibaba.fastjson2.JSONObject;

public class WebsocketMessage {
    private WebsocketMessageType messageType;
    private int channelId;
    private String sn;
    private boolean success;
    private String msg;
    private JSONObject data;

    public WebsocketMessageType getMessageType() {
        return this.messageType;
    }

    public void setMessageType(WebsocketMessageType messageType) {
        this.messageType = messageType;
    }

    public JSONObject getData() {
        return this.data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getSn() {
        return this.sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public int getChannelId() {
        return this.channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }
}
