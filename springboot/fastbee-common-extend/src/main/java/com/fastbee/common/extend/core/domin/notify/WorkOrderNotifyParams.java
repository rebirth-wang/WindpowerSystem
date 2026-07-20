package com.fastbee.common.extend.core.domin.notify;

import lombok.Data;

/**
 * @author fastb
 * @version 1.0
 * @description: TODO
 * @date 2023-12-26 11:03
 */
@Data
public class WorkOrderNotifyParams {

    /**
     * 派单人
     */
    private String createBy;
    /**
     * 工单名称
     */
    private String name;
    /**
     * 工单编号
     */
    private String number;
    /**
     * 工单类型
     */
    private String workOrderType;
    /**
     * 设备名称
     */
    private String deviceName;
    /**
     * 设备编号
     */
    private String serialNumber;

    private String sendEmail;

    private Long tenantId;
}
