package com.fastbee.common.extend.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通知发送状态枚举
 * @author fastbee
 * @date 2024-01-01
 */
@Getter
@AllArgsConstructor
public enum NotifySendStatusEnum {

    /**
     * 发送失败
     */
    FAILED(0, "发送失败"),
    
    /**
     * 发送成功
     */
    SUCCESS(1, "发送成功");

    private final Integer status;
    private final String description;

    /**
     * 根据状态码获取枚举
     * @param status 状态码
     * @return 通知发送状态枚举
     */
    public static NotifySendStatusEnum getByStatus(Integer status) {
        if (status == null) {
            return null;
        }
        for (NotifySendStatusEnum sendStatus : NotifySendStatusEnum.values()) {
            if (sendStatus.getStatus().equals(status)) {
                return sendStatus;
            }
        }
        return null;
    }
}
