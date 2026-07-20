package com.fastbee.bridge.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BridgeType {
    http(3L, "http桥接"),
    mqtt(4L, "mqtt桥接"),
    database(5L, "数据库桥接"),
    mq(6L, "消息组件桥接"),
    other(7L, "其他类型桥接");
    private final Long value;
    private final String text;
}
