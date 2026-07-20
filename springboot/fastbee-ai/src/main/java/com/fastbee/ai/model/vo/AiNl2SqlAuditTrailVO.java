package com.fastbee.ai.model.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * AI 智能问数审计轨迹。
 */
@Data
public class AiNl2SqlAuditTrailVO {

    /**
     * 原始问题。
     */
    private String question;

    /**
     * 模型原始输出。
     */
    private String rawModelResponse;

    /**
     * 结构化解析结果 JSON。
     */
    private String structuredPayload;

    /**
     * 最终执行 SQL。
     */
    private String executedSql;

    /**
     * 执行状态。
     */
    private String executionStatus;

    /**
     * 失败原因。
     */
    private String failedReason;

    /**
     * 执行模式。
     */
    private String queryMode;

    /**
     * 语义来源。
     */
    private String runtimeSource;

    /**
     * 主语义。
     */
    private String primarySemantic;

    /**
     * 命中的知识库编码。
     */
    private List<String> knowledgeBases = new ArrayList<>();

    /**
     * 命中的版本号。
     */
    private List<String> versionNos = new ArrayList<>();

    /**
     * 命中的静态字段键。
     */
    private List<String> matchedFieldKeys = new ArrayList<>();

    /**
     * 命中的运行时字段键。
     */
    private List<String> runtimeFieldKeys = new ArrayList<>();

    /**
     * 候选标识符。
     */
    private List<String> candidateIdentifiers = new ArrayList<>();

    /**
     * 是否走了语义回退。
     */
    private Boolean semanticFallbackUsed = Boolean.FALSE;

    /**
     * 是否走了执行降级。
     */
    private Boolean executionFallbackUsed = Boolean.FALSE;

    /**
     * 降级原因。
     */
    private String degradedReason;

    /**
     * 路由原因。
     */
    private String routeReason;

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
