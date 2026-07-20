package com.fastbee.common.extend.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 产品发布状态枚举
 *
 * @author fastbee
 */
@Getter
@AllArgsConstructor
public enum ProductPublishStatusEnum {

    UNPUBLISHED(1, "未发布"),
    PUBLISHED(2, "已发布");

    private final Integer status;
    private final String description;

    public static ProductPublishStatusEnum getByStatus(Integer status) {
        if (status == null) {
            return null;
        }
        for (ProductPublishStatusEnum item : values()) {
            if (item.status.equals(status)) {
                return item;
            }
        }
        return null;
    }
}
