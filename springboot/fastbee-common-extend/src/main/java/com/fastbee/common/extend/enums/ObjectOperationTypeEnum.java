package com.fastbee.common.extend.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 对象操作类型枚举类
 * @author zzy
 * @date 2024-05-22 10:01
 * @version 1.0
 */
@Getter
@AllArgsConstructor
public enum ObjectOperationTypeEnum {

    WORK_ORDER(1, "工单");

    private final Integer type;

    private final String description;

}
