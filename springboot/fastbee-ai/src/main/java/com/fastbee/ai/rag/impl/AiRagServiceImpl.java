package com.fastbee.ai.rag.impl;

import static com.fastbee.ai.support.AiI18nMessageUtils.message;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fastbee.ai.rag.IAiRagService;
import com.fastbee.ai.rag.model.AiRagQueryRequest;
import com.fastbee.ai.rag.model.AiRagReference;
import com.fastbee.ai.rag.model.AiRagWorkflowResult;
import com.fastbee.ai.rag.model.VectorSearchResult;
import com.fastbee.ai.service.IAiKnowledgeVectorService;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.utils.StringUtils;

/**
 * AI RAG 流程服务实现。
 */
@Service
public class AiRagServiceImpl implements IAiRagService {

    @Resource
    private IAiKnowledgeVectorService aiKnowledgeVectorService;

    @Override
    public AiRagWorkflowResult retrieve(AiRagQueryRequest request) {
        if (request == null || request.getKnowledgeBaseId() == null) {
            throw new ServiceException(message("ai.knowledge.base.id.required"));
        }
        if (StringUtils.isBlank(request.getQueryText())) {
            throw new ServiceException(message("ai.rag.query.required"));
        }
        List<VectorSearchResult> searchResults = aiKnowledgeVectorService.search(
                request.getKnowledgeBaseId(), request.getQueryText(), request.getTopK());
        List<AiRagReference> references = buildReferences(searchResults);

        AiRagWorkflowResult result = new AiRagWorkflowResult();
        result.setKnowledgeBaseId(request.getKnowledgeBaseId());
        result.setQueryText(request.getQueryText().trim());
        result.setBusinessType(StringUtils.defaultIfBlank(request.getBusinessType(), "GENERAL"));
        result.setMatchedCount(references.size());
        result.setEmptyResult(references.isEmpty());
        result.setReferences(references);
        result.setAnswerInstruction(buildAnswerInstruction(result));
        result.setContextPrompt(buildContextPrompt(result));
        return result;
    }

    private List<AiRagReference> buildReferences(List<VectorSearchResult> searchResults) {
        List<AiRagReference> references = new ArrayList<>();
        if (searchResults == null || searchResults.isEmpty()) {
            return references;
        }
        int orderNum = 1;
        for (VectorSearchResult searchResult : searchResults) {
            AiRagReference reference = new AiRagReference();
            reference.setOrderNum(orderNum++);
            reference.setDocumentId(searchResult.getDocumentId());
            reference.setSegmentCode(searchResult.getSegmentCode());
            reference.setSegmentTitle(searchResult.getSegmentTitle());
            reference.setSourceCode(searchResult.getSourceCode());
            reference.setSourceName(searchResult.getSourceName());
            reference.setScore(searchResult.getScore());
            reference.setContentPreview(searchResult.getContentPreview());
            reference.setContent(searchResult.getContent());
            references.add(reference);
        }
        return references;
    }

    private String buildAnswerInstruction(AiRagWorkflowResult result) {
        StringBuilder builder = new StringBuilder();
        builder.append("你是 FastBee 的 AI 知识助手。");
        builder.append("当前业务场景为：").append(result.getBusinessType()).append("。");
        builder.append("请优先基于提供的知识片段回答用户问题，不要凭空编造。");
        builder.append("如果知识片段不足以支撑结论，请明确说明“知识不足，需要补充资料”。");
        builder.append("回答时尽量引用片段编号，便于后续审计与回放。");
        return builder.toString();
    }

    private String buildContextPrompt(AiRagWorkflowResult result) {
        StringBuilder builder = new StringBuilder();
        builder.append("【用户问题】\n");
        builder.append(result.getQueryText()).append("\n\n");
        builder.append("【回答要求】\n");
        builder.append(result.getAnswerInstruction()).append("\n\n");
        builder.append("【知识片段】\n");
        if (result.getReferences() == null || result.getReferences().isEmpty()) {
            builder.append("暂无可用知识片段。\n");
            return builder.toString();
        }
        for (AiRagReference reference : result.getReferences()) {
            builder.append("[").append(reference.getOrderNum()).append("] ");
            builder.append(reference.getSourceName()).append(" / ");
            builder.append(reference.getSegmentCode()).append("\n");
            builder.append(reference.getContent()).append("\n\n");
        }
        return builder.toString();
    }
}
