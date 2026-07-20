package com.fastbee.common.extend.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 场景执行条件枚举
 * 1=或(任意条件), 2=且(所有条件), 3=非(不满足)
 *
 * @author fastbee
 * @date 2024-01-01
 */
@Getter
@AllArgsConstructor
public enum SceneCondEnum {

    /**
     * 或 - 任意条件满足即触发
     */
    ANY(1, "任意条件"),

    /**
     * 且 - 所有条件满足才触发
     */
    ALL(2, "所有条件"),

    /**
     * 非 - 不满足条件时触发
     */
    NOT(3, "不满足条件");

    private final Integer cond;
    private final String description;

    public static SceneCondEnum getByCond(Integer cond) {
        if (cond == null) return null;
        for (SceneCondEnum e : values()) {
            if (e.cond.equals(cond)) return e;
        }
        return null;
    }
}
