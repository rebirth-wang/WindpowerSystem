package com.fastbee.common.extend.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 告警日志状态枚举
 * @author fastbee
 * @date 2024-01-01
 */
@Getter
@AllArgsConstructor
public enum AlertLogStatusEnum {

    /**
     * 待处理
     */
    PENDING(1, "待处理"),
    
    /**
     * 处理中
     */
    PROCESSING(2, "处理中"),
    
    /**
     * 已处理
     */
    PROCESSED(3, "已处理");

    private final Integer status;
    private final String description;

    /**
     * 根据状态码获取枚举
     * @param status 状态码
     * @return 告警日志状态枚举
     */
    public static AlertLogStatusEnum getByStatus(Integer status) {
        if (status == null) {
            return null;
        }
        for (AlertLogStatusEnum alertStatus : AlertLogStatusEnum.values()) {
            if (alertStatus.getStatus().equals(status)) {
                return alertStatus;
            }
        }
        return null;
    }
}
