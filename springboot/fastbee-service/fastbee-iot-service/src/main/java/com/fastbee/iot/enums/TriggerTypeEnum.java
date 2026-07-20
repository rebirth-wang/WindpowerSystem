package com.fastbee.iot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TriggerTypeEnum {
    DEV_TRIGGER(1),
    PRODUCT_TRIGGER(2),
    SCHEDULED_TRIGGER(7),
    CUSTOM_TRIGGER(8);
    private int code;
}
