package com.fastbee.ai.model.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * 协议知识条目。
 */
@Data
public class AiProtocolKnowledgeItemVO {

    /**
     * 模块名称。
     */
    private String moduleName;

    /**
     * 字段编码。
     */
    private String fieldCode;

    /**
     * 字段名称。
     */
    private String fieldName;

    /**
     * 数据类型。
     */
    private String dataType;

    /**
     * 示例值。
     */
    private String sampleValue;

    /**
     * 值映射。
     */
    private String valueMappings;

    /**
     * 别名列表。
     */
    private List<String> aliases = new ArrayList<>();

    /**
     * 备注说明。
     */
    private String remark;

    /**
     * 匹配分值。
     */
    private Integer matchScore = 0;
}
