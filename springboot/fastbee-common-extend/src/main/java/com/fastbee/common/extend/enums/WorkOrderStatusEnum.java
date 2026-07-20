package com.fastbee.common.extend.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 工单状态枚举
 * @author fastb
 * @date 2024-05-22 10:01
 * @version 1.0
 */
@Getter
@AllArgsConstructor
public enum WorkOrderStatusEnum {

    CLOSED_ORDER(1, "已结单"),
    PENDING(2, "待处理"),
    ORDER_DISPATCHED(3, "已派单"),
    ORDER_RECEIVED(4, "已接单"),
    MANDATORY_BILLING(5, "强制结单");

    private final Integer status;

    private final String description;

    public static WorkOrderStatusEnum getByStatus(Integer status) {
        for (WorkOrderStatusEnum workOrderStatusEnum : WorkOrderStatusEnum.values()) {
            if (workOrderStatusEnum.getStatus().equals(status)) {
                return workOrderStatusEnum;
            }
        }
        return null;
    }

    public static Integer checkNextStatus(Integer status) {
        if (PENDING.status.equals(status)) {
            return ORDER_DISPATCHED.status;
        } else if (ORDER_DISPATCHED.status.equals(status)) {
            return ORDER_RECEIVED.status;
        } else if (ORDER_RECEIVED.status.equals(status)) {
            return CLOSED_ORDER.status;
        }
        return null;
    }

    public static String checkShowButton(Integer status) {
        if (PENDING.status.equals(status)) {
            return "派单";
        } else if (ORDER_DISPATCHED.status.equals(status)) {
            return "接单";
        } else if (ORDER_RECEIVED.status.equals(status)) {
            return "结单";
        }
        return null;
    }

    public static Boolean checkUpdate(Integer status) {
        return ORDER_DISPATCHED.status.equals(status);
    }

    public static Boolean checkDelete(Integer status) {
        return ORDER_RECEIVED.status.equals(status);
    }

}
