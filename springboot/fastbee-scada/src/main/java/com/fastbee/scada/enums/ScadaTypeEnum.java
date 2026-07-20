package com.fastbee.scada.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 组态类型枚举
 * @author fastb
 * @date 2024-05-22 10:01
 * @version 1.0
 */
@Getter
@AllArgsConstructor
public enum ScadaTypeEnum {

    PRODUCT_TEMPLATE(1, "模版组态"),
    SCENE_MODEL(2, "场景组态"),
    PUBLIC(3, "公共组态");



    private final Integer type;

    private final String name;

    public static ScadaTypeEnum getByType(Integer type) {
        for (ScadaTypeEnum scadaTypeEnum : ScadaTypeEnum.values()) {
            if (scadaTypeEnum.getType().equals(type)) {
                return scadaTypeEnum;
            }
        }
        return null;
    }

}
