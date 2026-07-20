package com.fastbee.common.extend.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 场景脚本用途枚举
 * 1=数据流, 2=触发器, 3=执行动作
 *
 * @author fastbee
 * @date 2024-01-01
 */
@Getter
@AllArgsConstructor
public enum ScenePurposeEnum {

    /**
     * 数据流
     */
    DATA_FLOW(1, "数据流"),

    /**
     * 触发器
     */
    TRIGGER(2, "触发器"),

    /**
     * 执行动作
     */
    ACTION(3, "执行动作");

    private final Integer purpose;
    private final String description;

    public static ScenePurposeEnum getByPurpose(Integer purpose) {
        if (purpose == null) return null;
        for (ScenePurposeEnum e : values()) {
            if (e.purpose.equals(purpose)) return e;
        }
        return null;
    }
}
