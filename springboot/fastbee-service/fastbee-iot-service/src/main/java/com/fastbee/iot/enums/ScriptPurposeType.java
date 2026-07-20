package com.fastbee.iot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
/** 脚本用途(1=数据流，2=触发器，3=执行动作) */
public enum ScriptPurposeType {
    DATA_STREAM(1),
    TRIGGER(2),
    ACTION(3);
    private int code;
}
