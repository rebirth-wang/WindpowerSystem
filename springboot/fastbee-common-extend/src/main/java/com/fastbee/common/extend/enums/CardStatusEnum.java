package com.fastbee.common.extend.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 物联网卡状态枚举
 * @author fastb
 * @date 2024-05-22 10:01
 * @version 1.0
 */
@Getter
@AllArgsConstructor
public enum CardStatusEnum {

    NORMAL(0, "正常"),
    PENDING_ACTIVATION(1, "待激活"),
    SHUTDOWN(2, "停机"),
    ACCOUNT_TERMINATION(3, "销号"),
    UNKNOWN(99, "未知");

    private final int status;
    private final String desc;

}
