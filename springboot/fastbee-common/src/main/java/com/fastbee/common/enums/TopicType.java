//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.enums;

public enum TopicType {
    PROPERTY_POST(0, 1, "/property/post", "订阅属性"),
    EVENT_POST(0, 2, "/event/post", "订阅事件"),
    FUNCTION_POST(0, 3, "/function/post", "订阅功能"),
    INFO_POST(0, 4, "/info/post", "订阅设备信息"),
    NTP_POST(0, 5, "/ntp/post", "订阅时钟同步"),
    SERVICE_INVOKE_REPLY(0, 8, "/service/reply", "订阅功能调用返回结果"),
    MESSAGE_POST(0, 26, "/message/post", "订阅设备上报消息"),
    FUNCTION_GET(1, 17, "/function/get", "发布功能"),
    PROPERTY_GET(1, 12, "/property/get", "发布设备属性读取"),
    PROPERTY_SET(1, 15, "/property/set", "设置设备属性读取"),
    STATUS_POST(1, 11, "/status/post", "发布状态"),
    NTP_GET(1, 15, "/ntp/get", "发布时钟同步"),
    INFO_GET(1, 18, "/info/get", "发布设备信息"),
    DEV_INFO_POST(3, 19, "/info/post", "设备端发布设备信息"),
    DEV_EVENT_POST(3, 20, "/event/post", "设备端发布事件"),
    DEV_PROPERTY_POST(3, 22, "/property/post", "设备端发布属性"),
    WS_SERVICE_INVOKE(2, 16, "/ws/service", "WS服务调用"),
    WS_OTA_STATUS(2, 18, "/ws/ota/status", "OTA升级状态"),
    HTTP_FIRMWARE_SET(1, 14, "/http/upgrade/set", "http推送发布OTA升级"),
    FETCH_FIRMWARE_SET(1, 19, "/fetch/upgrade/set", "分包拉取方式发布OTA升级"),
    FETCH_UPGRADE_REPLY(0, 9, "/fetch/upgrade/reply", "分包拉取方式OTA升级回调"),
    HTTP_UPGRADE_REPLY(0, 9, "/http/upgrade/reply", "http推送方式OTA升级回调");

    Integer type;
    Integer order;
    String topicSuffix;
    String msg;

    public static TopicType getType(String topicSuffix) {
        for(TopicType var4 : values()) {
            if (var4.topicSuffix.equals(topicSuffix)) {
                return var4;
            }
        }

        return PROPERTY_POST;
    }

    public Integer getType() {
        return this.type;
    }

    public Integer getOrder() {
        return this.order;
    }

    public String getTopicSuffix() {
        return this.topicSuffix;
    }

    public String getMsg() {
        return this.msg;
    }

    private TopicType(Integer type, Integer order, String topicSuffix, String msg) {
        this.type = type;
        this.order = order;
        this.topicSuffix = topicSuffix;
        this.msg = msg;
    }
}
