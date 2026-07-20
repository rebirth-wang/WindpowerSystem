package com.fastbee.common.extend.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EnableEnum {
    ENABLE(1),
    DISABLE(0);
    private final Integer type;
}
