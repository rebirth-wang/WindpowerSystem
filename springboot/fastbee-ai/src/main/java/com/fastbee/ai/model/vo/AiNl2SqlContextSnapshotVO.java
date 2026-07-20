package com.fastbee.ai.model.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * AI 智能问数会话私有上下文快照。
 */
@Data
public class AiNl2SqlContextSnapshotVO {

    /**
     * 最近一次问数问题。
     */
    private String question;

    /**
     * 最近一次问数执行模式。
     */
    private String queryMode;

    /**
     * 最近一次问数语义来源。
     */
    private String runtimeSource;

    /**
     * 最近一次问数主命中语义。
     */
    private String primarySemantic;

    /**
     * 最近一次问数命中的知识库编码。
     */
    private List<String> knowledgeBases = new ArrayList<>();

    /**
     * 最近一次问数命中的静态字段键。
     */
    private List<String> matchedFieldKeys = new ArrayList<>();

    /**
     * 最近一次问数命中的运行时字段键。
     */
    private List<String> runtimeFieldKeys = new ArrayList<>();

    /**
     * 最近一次问数候选标识符。
     */
    private List<String> candidateIdentifiers = new ArrayList<>();

    /**
     * 最近一次问数命中的设备 ID。
     */
    private Long deviceId;

    /**
     * 最近一次问数命中的设备名称。
     */
    private String deviceName;

    /**
     * 最近一次问数命中的设备编号。
     */
    private String serialNumber;

    /**
     * 最近一次问数命中的产品 ID。
     */
    private Long productId;

    /**
     * 最近一次问数命中的产品名称。
     */
    private String productName;

    /**
     * 最近一次问数命中的物模型标识符。
     */
    private String identifier;

    /**
     * 最近一次问数识别到的时间范围文本。
     */
    private String timeRangeText;

    /**
     * 最近一次问数结果摘要。
     */
    private String lastSummary;
}
