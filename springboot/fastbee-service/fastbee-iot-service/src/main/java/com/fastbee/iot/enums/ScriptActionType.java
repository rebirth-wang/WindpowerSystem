package com.fastbee.iot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
/** 脚本动作(1=消息重发，2=消息通知，3=Http推送，4=Mqtt桥接，5=数据库存储) */
public enum ScriptActionType {
    RETRY(1),
    NOTIFY(2),
    HTTP_PUSH(3),
    MQTT_BRIDGE(4),
    DB_STORE(5);
    private int code;
}
