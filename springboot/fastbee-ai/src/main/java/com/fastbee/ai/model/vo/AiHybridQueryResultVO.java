package com.fastbee.ai.model.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * Hybrid 多源问数结果。
 */
@Data
public class AiHybridQueryResultVO {

    /**
     * 原始问题。
     */
    private String question;

    /**
     * 计划执行模式。
     */
    private String queryMode;

    /**
     * 实际执行模式。
     */
    private String finalQueryMode;

    /**
     * 查询类型：CURRENT / HISTORY / AGGREGATE。
     */
    private String queryType;

    /**
     * 路由原因。
     */
    private String routeReason;

    /**
     * 是否触发回退。
     */
    private Boolean fallbackUsed = Boolean.FALSE;

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
     * 命中的物模型标识符。
     */
    private String identifier;

    /**
     * 时间窗口标签。
     */
    private String timeWindowLabel;

    /**
     * 查询开始时间。
     */
    private String beginTime;

    /**
     * 查询结束时间。
     */
    private String endTime;

    /**
     * 单位。
     */
    private String unit;

    /**
     * 当前值。
     */
    private String currentValue;

    /**
     * 当前值时间。
     */
    private String currentTime;

    /**
     * 历史序列。
     */
    private List<AiTsdbHistoryPointVO> historyPoints = new ArrayList<>();

    /**
     * 统计方式。
     */
    private String statisticOperation;

    /**
     * 统计值。
     */
    private String statisticValue;

    /**
     * 统计样本。
     */
    private List<String> statisticSamples = new ArrayList<>();

    /**
     * 结果摘要。
     */
    private String summary;

    /**
     * 是否已完成权限校验。
     */
    private Boolean accessValidated = Boolean.FALSE;

    /**
     * 辅助说明。
     */
    private List<String> messages = new ArrayList<>();

    /**
     * 权限校验信息。
     */
    private List<String> permissionChecks = new ArrayList<>();
}
