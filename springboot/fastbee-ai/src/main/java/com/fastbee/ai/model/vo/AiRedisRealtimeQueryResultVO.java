package com.fastbee.ai.model.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * Redis 实时值问数结果。
 */
@Data
public class AiRedisRealtimeQueryResultVO {

    /**
     * 原始问题。
     */
    private String question;

    /**
     * 执行模式。
     */
    private String queryMode;

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
     * 命中的语义名称。
     */
    private String semanticName;

    /**
     * 真实物模型标识符。
     */
    private String identifier;

    /**
     * 当前值。
     */
    private String currentValue;

    /**
     * 值显示名称。
     */
    private String displayName;

    /**
     * 单位。
     */
    private String unit;

    /**
     * 上报时间。
     */
    private String reportTime;

    /**
     * 是否命中缓存。
     */
    private Boolean cacheHit = Boolean.FALSE;

    /**
     * 是否已完成权限校验。
     */
    private Boolean accessValidated = Boolean.FALSE;

    /**
     * 结果摘要。
     */
    private String summary;

    /**
     * 说明信息。
     */
    private List<String> messages = new ArrayList<>();

    /**
     * 权限校验信息。
     */
    private List<String> permissionChecks = new ArrayList<>();
}
