package com.fastbee.iot.model.modbus;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 产品轮训任务列对象 iot_product_modbus_job
 *
 * @author zhuangpeng.li
 * @date 2024-09-04
 */

@Data
public class ProductModbusJobVO implements Serializable{
    private static final long serialVersionUID=1L;

    /** 任务id */
    private Long taskId;

    /** 任务名称 */
    private String jobName;

    /** 产品id */
    private Long productId;

    /** 指令 */
    private String command;

    /** 创建者 */
    private String createBy;

    /** 创建时间 */
    private Date createTime;

    /** 备注信息 */
    private String remark;

    /**
     * 状态（0正常 1暂停）
     */
    private Integer status;

    /**
     * 定时时间展示
     */
    private String remarkStr;

    /** 默认的子设备地址 */
    private String address;

}
