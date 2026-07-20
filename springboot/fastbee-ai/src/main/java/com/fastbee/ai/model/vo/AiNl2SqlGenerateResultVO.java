package com.fastbee.ai.model.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * AI 智能问数生成结果。
 */
@Data
public class AiNl2SqlGenerateResultVO {

    /**
     * 原始问题。
     */
    private String question;

    /**
     * 生成的 SQL。
     */
    private String generatedSql;

    /**
     * 结果解释。
     */
    private String summary;

    /**
     * 置信度。
     */
    private Double confidence;

    /**
     * 问数执行模式。
     */
    private String queryMode;

    /**
     * 涉及的数据表。
     */
    private List<String> tables = new ArrayList<>();

    /**
     * 原始模型输出。
     */
    private String modelResponse;

    /**
     * 结构化输出 JSON。
     */
    private String structuredPayload;

    /**
     * 结构化解析状态。
     */
    private String parseStatus;

    /**
     * 结构化解析错误码。
     */
    private String parseErrorCode;

    /**
     * 结构化解析错误说明。
     */
    private String parseErrorMessage;

    /**
     * 是否来自严格结构化输出。
     */
    private Boolean structuredOutput;

    /**
     * 结构化生成结果。
     */
    private AiNl2SqlStructuredResultVO generationResult;

    /**
     * 问数执行计划。
     */
    private AiQueryPlanVO queryPlan;

    /**
     * SQL 查询结果。
     */
    private AiNl2SqlQueryResultVO queryResult;

    /**
     * Redis 实时值结果。
     */
    private AiRedisRealtimeQueryResultVO realtimeResult;

    /**
     * TSDB 查询结果。
     */
    private AiTsdbQueryResultVO tsdbResult;

    /**
     * 混合问数结果。
     */
    private AiHybridQueryResultVO hybridResult;

    /**
     * 问数审计轨迹。
     */
    private AiNl2SqlAuditTrailVO auditTrail;
}
