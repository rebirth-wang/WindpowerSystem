package com.fastbee.ai.model.template;

import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * 通用知识模板行对象。
 */
@Data
public class AiGeneralKnowledgeTemplateRow {

    @Excel(name = "section_name")
    private String sectionName;

    @Excel(name = "title")
    private String title;

    @Excel(name = "content")
    private String content;

    @Excel(name = "tags")
    private String tags;

    @Excel(name = "aliases")
    private String aliases;

    @Excel(name = "remark")
    private String remark;
}
