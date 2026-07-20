package com.fastbee.scada.vo;

import lombok.Data;

/**
 * @author fastb
 * @version 1.0
 * @description: 组态相关查询共用类
 * @date 2024-06-20 16:53
 */
@Data
public class ScadaQueryParam {

    private Integer page;

    private Integer size;

    private Integer pageStart;

    /**
     * 组态guid
     */
    private String scadaGuid;

    /**
     * 组态guid
     */
    private String guid;

    /**
     * 设备编号
     */
    private String serialNumber;

    /**
     * 组态类型
     */
    private Integer type;

    /**
     * 变量名称
     */
    private String modelName;

    /**
     * 场景id
     */
    private Long sceneModelId;

    /**
     * 场景数据来源名称
     */
    private Long sceneModelDeviceId;
}
