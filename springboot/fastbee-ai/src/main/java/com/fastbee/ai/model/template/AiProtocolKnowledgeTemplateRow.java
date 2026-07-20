package com.fastbee.ai.model.template;

import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * 协议知识模板行对象。
 */
@Data
public class AiProtocolKnowledgeTemplateRow {

    @Excel(name = "module_name")
    private String moduleName;

    @Excel(name = "field_code")
    private String fieldCode;

    @Excel(name = "field_name")
    private String fieldName;

    @Excel(name = "data_type")
    private String dataType;

    @Excel(name = "sample_value")
    private String sampleValue;

    @Excel(name = "value_mappings")
    private String valueMappings;

    @Excel(name = "aliases")
    private String aliases;

    @Excel(name = "remark")
    private String remark;
}
