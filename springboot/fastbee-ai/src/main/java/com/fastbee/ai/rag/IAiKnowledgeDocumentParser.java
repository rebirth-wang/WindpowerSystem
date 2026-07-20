package com.fastbee.ai.rag;

import java.util.List;

import com.fastbee.ai.rag.model.AiKnowledgeDocumentReference;
import com.fastbee.ai.rag.model.AiKnowledgeSegment;

/**
 * AI 知识文档解析器。
 */
public interface IAiKnowledgeDocumentParser {

    /**
     * 将文档内容切分为可检索分段。
     *
     * @param documentReference 文档引用
     * @param documentContent 文档内容
     * @return 分段列表
     */
    List<AiKnowledgeSegment> split(AiKnowledgeDocumentReference documentReference, String documentContent);
}
