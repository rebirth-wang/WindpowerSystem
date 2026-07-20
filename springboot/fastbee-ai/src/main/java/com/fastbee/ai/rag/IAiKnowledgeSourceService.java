package com.fastbee.ai.rag;

import java.util.List;

import com.fastbee.ai.rag.model.AiKnowledgeDocumentReference;
import com.fastbee.ai.rag.model.AiKnowledgeSegment;
import com.fastbee.ai.rag.model.AiKnowledgeSourceDefinition;

/**
 * AI 知识源抽象服务。
 */
public interface IAiKnowledgeSourceService {

    /**
     * 查询可用知识源。
     *
     * @return 知识源列表
     */
    List<AiKnowledgeSourceDefinition> listKnowledgeSources();

    /**
     * 查询知识源下的文档。
     *
     * @param knowledgeBaseId 知识库 ID
     * @return 文档列表
     */
    List<AiKnowledgeDocumentReference> listKnowledgeDocuments(Long knowledgeBaseId);

    /**
     * 查询文档分段结果。
     *
     * @param documentId 文档 ID
     * @return 分段列表
     */
    List<AiKnowledgeSegment> listKnowledgeSegments(Long documentId);
}
