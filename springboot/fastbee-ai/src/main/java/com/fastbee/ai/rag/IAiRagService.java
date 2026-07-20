package com.fastbee.ai.rag;

import com.fastbee.ai.rag.model.AiRagQueryRequest;
import com.fastbee.ai.rag.model.AiRagWorkflowResult;

/**
 * AI RAG 流程服务。
 */
public interface IAiRagService {

    /**
     * 执行一次检索增强流程。
     *
     * @param request 检索请求
     * @return 流程结果
     */
    AiRagWorkflowResult retrieve(AiRagQueryRequest request);
}
