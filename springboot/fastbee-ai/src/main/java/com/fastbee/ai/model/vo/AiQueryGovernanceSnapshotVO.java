package com.fastbee.ai.model.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * AI 问数治理快照。
 */
@Data
public class AiQueryGovernanceSnapshotVO {

    /**
     * 本次执行模式。
     */
    private String queryMode;

    /**
     * 是否已完成访问校验。
     */
    private Boolean accessValidated = Boolean.FALSE;

    /**
     * 设备 ID。
     */
    private Long deviceId;

    /**
     * 设备编号。
     */
    private String serialNumber;

    /**
     * 设备名称。
     */
    private String deviceName;

    /**
     * 产品 ID。
     */
    private Long productId;

    /**
     * 产品名称。
     */
    private String productName;

    /**
     * 物模型标识符。
     */
    private String identifier;

    /**
     * 权限校验说明。
     */
    private List<String> permissionChecks = new ArrayList<>();
}
