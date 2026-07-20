package com.fastbee.ai.rag.model;

import java.util.List;

import lombok.Data;

/**
 * AI RAG 流程结果。
 */
@Data
public class AiRagWorkflowResult {

    /**
     * 知识库 ID。
     */
    private Long knowledgeBaseId;

    /**
     * 用户问题。
     */
    private String queryText;

    /**
     * 业务场景。
     */
    private String businessType;

    /**
     * 命中数量。
     */
    private Integer matchedCount;

    /**
     * 是否为空结果。
     */
    private Boolean emptyResult;

    /**
     * 组装后的回答要求。
     */
    private String answerInstruction;

    /**
     * 组装后的上下文提示词。
     */
    private String contextPrompt;

    /**
     * 引用片段。
     */
    private List<AiRagReference> references;
}
