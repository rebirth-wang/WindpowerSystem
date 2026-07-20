package com.fastbee.ai.model.vo;

import lombok.Data;

/**
 * AI 设备控制会话上下文快照。
 */
@Data
public class AiDeviceControlContextSnapshotVO {

    /**
     * 最近一次成功控制的设备编号。
     */
    private String serialNumber;

    /**
     * 最近一次成功控制的设备名称。
     */
    private String deviceName;

    /**
     * 最近一次成功控制的产品 ID。
     */
    private Long productId;

    /**
     * 最近一次成功控制的产品名称。
     */
    private String productName;

    /**
     * 最近一次成功控制的物模型标识符。
     */
    private String identifier;

    /**
     * 最近一次成功控制的物模型名称。
     */
    private String thingModelName;

    /**
     * 最近一次成功控制的物模型类型。
     */
    private Integer thingModelType;

    /**
     * 最近一次成功控制的动作文本。
     */
    private String actionText;

    /**
     * 最近一次成功控制的动作归一值。
     */
    private String actionValue;

    /**
     * 最近一次成功控制的业务子类型。
     */
    private String businessType;

    /**
     * 最近一次控制是否已确认高风险。
     */
    private String riskConfirmed;
}
