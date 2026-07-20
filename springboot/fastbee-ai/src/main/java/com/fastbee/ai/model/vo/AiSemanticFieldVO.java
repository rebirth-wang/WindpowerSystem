package com.fastbee.ai.model.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * AI 语义字段归一结果。
 */
@Data
public class AiSemanticFieldVO {

    /**
     * 知识库 ID。
     */
    private Long knowledgeBaseId;

    /**
     * 版本 ID。
     */
    private Long versionId;

    /**
     * 版本号。
     */
    private String versionNo;

    /**
     * 知识库编码。
     */
    private String kbCode;

    /**
     * 表名。
     */
    private String tableName;

    /**
     * 表说明。
     */
    private String tableComment;

    /**
     * 字段名。
     */
    private String columnName;

    /**
     * 表字段联合键。
     */
    private String tableColumnKey;

    /**
     * 语义名称。
     */
    private String semanticName;

    /**
     * 业务对象编码。
     */
    private String businessObjectCode;

    /**
     * 业务对象名称。
     */
    private String businessObjectName;

    /**
     * 主事实表。
     */
    private String primaryTable;

    /**
     * 表角色。
     */
    private String tableRole;

    /**
     * 语义类型。
     */
    private String semanticType;

    /**
     * 语义来源类型。
     */
    private String sourceType;

    /**
     * 语义来源编码。
     */
    private String sourceCode;

    /**
     * 数据源类型。
     */
    private String dataSourceType;

    /**
     * 归一后的语义来源。
     */
    private String semanticSource;

    /**
     * 默认统计口径。
     */
    private String defaultMetricRule;

    /**
     * 指标口径编码。
     */
    private String metricRuleCode;

    /**
     * 指标口径名称。
     */
    private String metricRuleName;

    /**
     * 默认数据源。
     */
    private String defaultDataSource;

    /**
     * 澄清规则。
     */
    private String clarifyRule;

    /**
     * 别名列表。
     */
    private List<String> aliases = new ArrayList<>();

    /**
     * 查询提示。
     */
    private List<String> queryHints = new ArrayList<>();

    /**
     * 关联提示。
     */
    private List<String> relationHints = new ArrayList<>();

    /**
     * 枚举或字典值映射。
     */
    private List<AiSemanticValueMappingVO> valueMappings = new ArrayList<>();

    /**
     * 匹配分数。
     */
    private Integer matchScore;
}
