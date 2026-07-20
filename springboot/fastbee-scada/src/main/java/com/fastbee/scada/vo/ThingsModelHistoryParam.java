package com.fastbee.scada.vo;

import lombok.Data;

import java.util.List;

/**
 * @author fastb
 * @version 1.0
 * @description: TODO
 * @date 2024-02-23 16:47
 */
@Data
public class ThingsModelHistoryParam {

    private String serialNumber;

    /** 创建时间 */
    private String beginTime;
    /** 创建时间 */
    private String endTime;

    private List<ThingsModelSim> thingsModelList;

    private Integer scadaType;

    private Long sceneModelId;

    /**
     * 场景数据来源id
     */
    private Long sceneModelDeviceId;

    @Data
    public static class ThingsModelSim{

        private String identifier;

        /**
         * 物模型类型
         */
        private Integer type;

        /**
         * 从机id
         */
        private Integer slaveId;

        /**
         * 从机名称
         */
        private String slaveName;

        /**
         * 物模型单位
         */
        private String unit;

        /**
         * 数据来源id
         */
        private Long sceneModelDeviceId;
    }

}
