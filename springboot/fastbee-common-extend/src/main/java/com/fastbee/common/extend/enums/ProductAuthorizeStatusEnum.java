package com.fastbee.common.extend.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 产品授权码状态枚举
 *
 * @author fastbee
 */
@Getter
@AllArgsConstructor
public enum ProductAuthorizeStatusEnum {

    UNUSED(1, "未使用"),
    IN_USE(2, "使用中");

    private final Integer status;
    private final String description;
}
