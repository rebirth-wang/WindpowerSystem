package com.fastbee.ai.model.vo;

import lombok.Data;

/**
 * AI 澄清续跑选择结果。
 */
@Data
public class AiResumeSelectionVO {

    /**
     * 澄清节点标识。
     */
    private String clarifyKey;

    /**
     * 续跑对应模式。
     */
    private String effectiveChatMode;

    /**
     * 用户选择值。
     */
    private String selectedValue;

    /**
     * 用户选择展示文本。
     */
    private String selectedLabel;

    /**
     * 原始问题。
     */
    private String resumeQuestion;
}
