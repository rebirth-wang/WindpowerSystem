package com.fastbee.ai.model.vo;

import lombok.Data;

/**
 * AI 会话观测统计对象。
 */
@Data
public class AiChatObservabilityStatsVO {

    /**
     * 统计范围说明。
     */
    private String scopeLabel;

    /**
     * 统计窗口天数。
     */
    private Integer windowDays;

    /**
     * 统计窗口文案。
     */
    private String windowLabel;

    /**
     * 纳入口径请求数。
     */
    private long requestCount;

    /**
     * 自动识别请求数。
     */
    private long autoRequestCount;

    /**
     * 自动识别命中数。
     */
    private long autoHitCount;

    /**
     * 自动识别后被纠偏的请求数。
     */
    private long autoCorrectedCount;

    /**
     * 自动识别命中率。
     */
    private double autoHitRate;

    /**
     * 手工改模式请求数。
     */
    private long manualModeRequestCount;

    /**
     * 手工改模式率。
     */
    private double manualModeRate;

    /**
     * 会话锁定请求数。
     */
    private long pinnedRequestCount;

    /**
     * 本轮指定请求数。
     */
    private long overrideRequestCount;

    /**
     * 直接指定请求数。
     */
    private long directManualRequestCount;

    /**
     * 纠偏重试次数。
     */
    private long correctionRetryCount;

    /**
     * 纠偏成功次数。
     */
    private long correctionSuccessCount;

    /**
     * 纠偏成功率。
     */
    private double correctionSuccessRate;
}
