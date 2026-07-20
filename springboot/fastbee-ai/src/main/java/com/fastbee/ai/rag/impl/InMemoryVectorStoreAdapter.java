package com.fastbee.ai.rag.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.fastbee.ai.rag.VectorStoreAdapter;
import com.fastbee.ai.rag.model.VectorSearchRequest;
import com.fastbee.ai.rag.model.VectorSearchResult;
import com.fastbee.ai.rag.model.VectorStoreDocument;
import com.fastbee.common.utils.StringUtils;

/**
 * 内存向量存储适配器。
 *
 * <p>首版使用轻量关键词匹配模拟向量检索行为，先把知识检索接口、适配层
 * 和后续可替换能力跑通，后续接入真实向量库时只需补充新的适配器实现。</p>
 */
@Service
public class InMemoryVectorStoreAdapter implements VectorStoreAdapter {

    private final Map<String, VectorStoreDocument> documentStore = new ConcurrentHashMap<>();

    @Override
    public String getStoreType() {
        return "MEMORY";
    }

    @Override
    public void upsertDocuments(List<VectorStoreDocument> documents) {
        if (documents == null || documents.isEmpty()) {
            return;
        }
        for (VectorStoreDocument document : documents) {
            if (document == null || StringUtils.isBlank(document.getVectorId())) {
                continue;
            }
            documentStore.put(document.getVectorId(), document);
        }
    }

    @Override
    public List<VectorSearchResult> search(VectorSearchRequest request) {
        if (request == null || request.getKnowledgeBaseId() == null || StringUtils.isBlank(request.getQueryText())) {
            return new ArrayList<>();
        }
        String normalizedQuery = request.getQueryText().toLowerCase(Locale.ROOT).trim();
        List<VectorSearchResult> results = new ArrayList<>();
        for (VectorStoreDocument document : documentStore.values()) {
            if (!request.getKnowledgeBaseId().equals(document.getKnowledgeBaseId())) {
                continue;
            }
            double score = calculateScore(normalizedQuery, document.getContent());
            if (score <= 0) {
                continue;
            }
            VectorSearchResult result = new VectorSearchResult();
            result.setKnowledgeBaseId(document.getKnowledgeBaseId());
            result.setDocumentId(document.getDocumentId());
            result.setSegmentCode(document.getSegmentCode());
            result.setSegmentTitle(document.getSegmentTitle());
            result.setScore(score);
            result.setSourceCode(document.getSourceCode());
            result.setSourceName(document.getSourceName());
            result.setContent(document.getContent());
            result.setContentPreview(buildPreview(document.getContent(), normalizedQuery));
            results.add(result);
        }
        results.sort(Comparator.comparing(VectorSearchResult::getScore).reversed()
                .thenComparing(VectorSearchResult::getSegmentCode));
        int topK = request.getTopK() == null || request.getTopK() <= 0 ? 5 : request.getTopK();
        if (results.size() > topK) {
            return new ArrayList<>(results.subList(0, topK));
        }
        return results;
    }

    @Override
    public void deleteByDocumentId(Long documentId) {
        if (documentId == null) {
            return;
        }
        documentStore.entrySet().removeIf(entry -> documentId.equals(entry.getValue().getDocumentId()));
    }

    private double calculateScore(String normalizedQuery, String content) {
        if (StringUtils.isBlank(content)) {
            return 0D;
        }
        String normalizedContent = content.toLowerCase(Locale.ROOT);
        double score = 0D;
        if (normalizedContent.contains(normalizedQuery)) {
            score += 10D;
        }
        String[] keywords = normalizedQuery.split("[,，;；\\s]+");
        for (String keyword : keywords) {
            if (keyword == null || keyword.length() < 2) {
                continue;
            }
            if (normalizedContent.contains(keyword)) {
                score += 1D;
            }
        }
        return score;
    }

    private String buildPreview(String content, String normalizedQuery) {
        if (StringUtils.isBlank(content)) {
            return "";
        }
        String normalizedContent = content.toLowerCase(Locale.ROOT);
        int index = normalizedContent.indexOf(normalizedQuery);
        if (index < 0) {
            return content.length() > 200 ? content.substring(0, 200) : content;
        }
        int start = Math.max(0, index - 40);
        int end = Math.min(content.length(), index + normalizedQuery.length() + 120);
        return content.substring(start, end).trim();
    }
}
