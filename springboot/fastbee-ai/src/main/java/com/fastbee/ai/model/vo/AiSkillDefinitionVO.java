package com.fastbee.ai.model.vo;

import lombok.Data;

/**
 * AI 技能定义信息。
 */
@Data
public class AiSkillDefinitionVO {

    /**
     * 技能分类。
     */
    private String skillCategory;

    /**
     * 技能编码。
     */
    private String skillCode;

    /**
     * 技能名称。
     */
    private String skillName;

    /**
     * 对应会话模式。
     */
    private String chatMode;

    /**
     * 技能说明。
     */
    private String description;

    /**
     * 当前是否启用。
     */
    private Boolean enabled;

    /**
     * 是否为内置技能。
     */
    private Boolean builtin;

    /**
     * 展示排序。
     */
    private Integer sortNum;
}
