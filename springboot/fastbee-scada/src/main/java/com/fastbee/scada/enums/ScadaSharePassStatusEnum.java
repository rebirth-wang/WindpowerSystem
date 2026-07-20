package com.fastbee.scada.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 组态类型枚举
 * @author fastb
 * @date 2024-05-22 10:01
 * @version 1.0
 */
@Getter
@AllArgsConstructor
public enum ScadaSharePassStatusEnum {

    CLOSE(0, "关闭密码"),
    EDIT(1, "修改密码"),
    NO_CHANGE(2, "保持不变");



    private final Integer status;

    private final String desc;

    public static ScadaSharePassStatusEnum getByStatus(Integer status) {
        for (ScadaSharePassStatusEnum scadaTypeEnum : ScadaSharePassStatusEnum.values()) {
            if (scadaTypeEnum.getStatus().equals(status)) {
                return scadaTypeEnum;
            }
        }
        return null;
    }

}
