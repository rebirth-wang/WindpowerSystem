package com.fastbee.ai.model.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * TSDB 问数结果。
 */
@Data
public class AiTsdbQueryResultVO {

    /**
     * 原始问题。
     */
    private String question;

    /**
     * 执行模式。
     */
    private String queryMode;

    /**
     * 查询类型：LATEST / HISTORY / AGGREGATE。
     */
    private String queryType;

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
     * 查询开始时间。
     */
    private String beginTime;

    /**
     * 查询结束时间。
     */
    private String endTime;

    /**
     * 时间窗口说明。
     */
    private String timeWindowLabel;

    /**
     * 单位。
     */
    private String unit;

    /**
     * 最新值。
     */
    private String latestValue;

    /**
     * 最新值时间。
     */
    private String latestTime;

    /**
     * 统计方式。
     */
    private String statisticOperation;

    /**
     * 统计结果。
     */
    private String statisticValue;

    /**
     * 统计原始样本值。
     */
    private List<String> statisticSamples = new ArrayList<>();

    /**
     * 历史序列点位。
     */
    private List<AiTsdbHistoryPointVO> historyPoints = new ArrayList<>();

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
