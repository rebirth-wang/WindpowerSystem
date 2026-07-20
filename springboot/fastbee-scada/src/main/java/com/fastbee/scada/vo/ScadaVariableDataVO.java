package com.fastbee.scada.vo;

import lombok.Data;

/**
 * 组态变量VO类
 * @author fastb
 * @date 2023-11-14 10:46
 */
@Data
public class ScadaVariableDataVO {

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备编号
     */
    private String serialNumber;

    /**
     * 模型名称
     */
    private String modelName;

    /**
     * 物模型标识
     */
    private String identifier;

    /**
     * 产品id
     */
    private Long productId;

    /**
     * 设备在线状态
     */
    private Integer status;

    /**
     * 物模型单位
     */
    private String unit;

    /**
     * 物模型类别
     */
    private Integer type;

    /**
     * 从机id
     */
    private Integer slaveId;

    /**
     * 场景来源id
     */
    private String sceneModelDeviceId;

    /**
     * 场景来源名称
     */
    private String sceneModelDeviceName;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 场景变量类型
     */
    private Integer variableType;

    /**
     * 场景来源id
     */
    private Long sceneModelId;

    /**
     * 从机名称
     */
    private String slaveName;

    /**
     * 值
     */
    private String value;

    /**
     * 更新时间
     */
    private String ts;

    /**
     * 物模型数据定义
     */
    private String specs;

}
