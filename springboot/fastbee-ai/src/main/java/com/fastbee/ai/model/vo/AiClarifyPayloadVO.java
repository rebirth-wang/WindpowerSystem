package com.fastbee.ai.model.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * AI 澄清协议载体。
 */
@Data
public class AiClarifyPayloadVO {

    /**
     * 澄清类型。
     */
    private String clarifyType;

    /**
     * 澄清节点标识。
     */
    private String clarifyKey;

    /**
     * 澄清标题。
     */
    private String clarifyTitle;

    /**
     * 展示给用户的澄清内容。
     */
    private String displayContent;

    /**
     * 对应技能名。
     */
    private String toolName;

    /**
     * 结构化工具结果。
     */
    private String toolResult;

    /**
     * 续跑令牌。
     */
    private String resumeToken;

    /**
     * 原始问题。
     */
    private String resumeQuestion;

    /**
     * 澄清候选项。
     */
    private List<AiClarifyOptionVO> options = new ArrayList<>();
}
