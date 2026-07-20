package com.fastbee.iot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
// 1=属性，2=功能，3=事件，4=设备升级，5=设备上线，6=设备下线
public enum DeviceEventType {
    DEV_PROPERTY(1),
    DEV_FUNCTION(2),
    DEV_EVENT(3),
    DEV_UPGRADE(4),
    DEV_ONLINE(5),
    DEV_OFFLINE(6);
    private Integer code;
}
