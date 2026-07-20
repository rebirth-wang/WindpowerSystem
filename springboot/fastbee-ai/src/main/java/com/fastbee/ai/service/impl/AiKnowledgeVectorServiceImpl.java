package com.fastbee.ai.service.impl;

import static com.fastbee.ai.support.AiI18nMessageUtils.message;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fastbee.ai.config.properties.FastBeeAiProperties;
import com.fastbee.ai.rag.IAiKnowledgeSourceService;
import com.fastbee.ai.rag.VectorStoreAdapter;
import com.fastbee.ai.rag.model.AiKnowledgeSegment;
import com.fastbee.ai.rag.model.VectorSearchRequest;
import com.fastbee.ai.rag.model.VectorSearchResult;
import com.fastbee.ai.rag.model.VectorStoreDocument;
import com.fastbee.ai.service.IAiKnowledgeVectorService;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.utils.StringUtils;

/**
 * AI 知识向量检索服务实现。
 */
@Service
public class AiKnowledgeVectorServiceImpl implements IAiKnowledgeVectorService {

    @Resource
    private IAiKnowledgeSourceService aiKnowledgeSourceService;

    @Resource
    private List<VectorStoreAdapter> vectorStoreAdapters;

    @Resource
    private FastBeeAiProperties fastBeeAiProperties;

    @Override
    public int indexDocument(Long documentId) {
        List<AiKnowledgeSegment> segments = aiKnowledgeSourceService.listKnowledgeSegments(documentId);
        if (segments.isEmpty()) {
            return 0;
        }
        List<VectorStoreDocument> documents = new ArrayList<>();
        for (AiKnowledgeSegment segment : segments) {
            VectorStoreDocument document = new VectorStoreDocument();
            document.setVectorId(segment.getSegmentCode());
            document.setKnowledgeBaseId(segment.getKnowledgeBaseId());
            document.setDocumentId(segment.getDocumentId());
            document.setSegmentCode(segment.getSegmentCode());
            document.setSegmentTitle(segment.getSegmentTitle());
            document.setContent(segment.getContent());
            document.setSourceCode(segment.getSourceCode());
            document.setSourceName(segment.getSourceName());
            documents.add(document);
        }
        resolveAdapter().upsertDocuments(documents);
        return documents.size();
    }

    @Override
    public void removeDocumentIndex(Long documentId) {
        resolveAdapter().deleteByDocumentId(documentId);
    }

    @Override
    public List<VectorSearchResult> search(Long knowledgeBaseId, String queryText, Integer topK) {
        if (knowledgeBaseId == null) {
            throw new ServiceException(message("ai.knowledge.base.id.required"));
        }
        if (StringUtils.isBlank(queryText)) {
            throw new ServiceException(message("ai.knowledge.vector.query.required"));
        }
        VectorSearchRequest request = new VectorSearchRequest();
        request.setKnowledgeBaseId(knowledgeBaseId);
        request.setQueryText(queryText.trim());
        request.setTopK(topK == null || topK <= 0 ? fastBeeAiProperties.getRag().getTopK() : topK);
        request.setStoreType(resolveStoreType());
        return resolveAdapter().search(request);
    }

    private VectorStoreAdapter resolveAdapter() {
        String targetStoreType = resolveStoreType();
        if (vectorStoreAdapters != null) {
            for (VectorStoreAdapter adapter : vectorStoreAdapters) {
                if (targetStoreType.equalsIgnoreCase(adapter.getStoreType())) {
                    return adapter;
                }
            }
        }
        throw new ServiceException(message("ai.knowledge.vector.adapter.not.found", targetStoreType));
    }

    private String resolveStoreType() {
        String storeType = fastBeeAiProperties.getRag().getVectorStoreType();
        return StringUtils.isBlank(storeType) ? "MEMORY" : storeType.trim().toUpperCase(Locale.ROOT);
    }
}
