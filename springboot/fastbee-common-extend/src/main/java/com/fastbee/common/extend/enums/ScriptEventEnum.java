package com.fastbee.common.extend.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ScriptEventEnum {
    // 1=设备上报，2=平台下发，3=设备上线，4=设备离线，5=Http接入，6=Mqtt接入
    /**
     * 设备上报
     */
    DEVICE_REPORT(1, "设备上报"),
    /**
     * 平台下发
     */
    DEVICE_INVOKE(2, "平台下发"),
    /**
     * 设备上线
     */
    DEVICE_ONLINE(3, "设备上线"),
    /**
     * 设备离线
     */
    DEVICE_OFFLINE(4, "设备离线"),
    /**
     * Http接入
     */
    HTTP_BRIDGE(5, "Http接入"),
    /**
     * Mqtt接入
     */
    MQTT_BRIDGE(6, "Mqtt接入");

    private final Integer type;
    private final String desc;
}
