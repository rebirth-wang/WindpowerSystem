package com.fastbee.ai.service.impl;

import static com.fastbee.ai.support.AiI18nMessageUtils.message;

import java.util.Date;
import java.util.List;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fastbee.ai.domain.AiKnowledgeBase;
import com.fastbee.ai.domain.AiKnowledgeDocument;
import com.fastbee.ai.model.vo.AiKnowledgeRefreshResultVO;
import com.fastbee.ai.service.IAiKnowledgeBaseService;
import com.fastbee.ai.service.IAiKnowledgeDocumentService;
import com.fastbee.ai.service.IAiKnowledgeRefreshService;
import com.fastbee.ai.service.IAiKnowledgeVectorService;
import com.fastbee.ai.support.AiSecuritySupport;
import com.fastbee.common.exception.ServiceException;

/**
 * AI 知识更新服务实现。
 */
@Service
public class AiKnowledgeRefreshServiceImpl implements IAiKnowledgeRefreshService {

    @Resource
    private IAiKnowledgeBaseService aiKnowledgeBaseService;

    @Resource
    private IAiKnowledgeDocumentService aiKnowledgeDocumentService;

    @Resource
    private IAiKnowledgeVectorService aiKnowledgeVectorService;

    @Override
    public AiKnowledgeRefreshResultVO refreshDocument(Long documentId) {
        AiKnowledgeDocument document = aiKnowledgeDocumentService.selectAiKnowledgeDocument(documentId);
        if (document == null) {
            throw new ServiceException(message("ai.knowledge.document.not.exists.or.no.permission"));
        }
        AiKnowledgeRefreshResultVO result = initResult("DOCUMENT", documentId);
        try {
            aiKnowledgeVectorService.removeDocumentIndex(documentId);
            int segmentCount = aiKnowledgeVectorService.indexDocument(documentId);
            updateDocumentStatus(documentId, "SUCCESS", segmentCount, message("ai.knowledge.document.refresh.success"));
            result.setSuccess(Boolean.TRUE);
            result.setIndexedDocumentCount(1);
            result.setIndexedSegmentCount(segmentCount);
            result.setMessage(message("ai.knowledge.document.refresh.success"));
        } catch (Exception ex) {
            String failedReason = trimReason(ex.getMessage());
            updateDocumentStatus(documentId, "FAILED", 0, failedReason);
            result.setSuccess(Boolean.FALSE);
            result.setFailedDocumentCount(1);
            result.setFailedReason(failedReason);
            result.setMessage(message("ai.knowledge.document.refresh.failed"));
        }
        result.setFinishedTime(new Date());
        return result;
    }

    @Override
    public AiKnowledgeRefreshResultVO rebuildKnowledgeBase(Long knowledgeBaseId) {
        AiKnowledgeBase query = new AiKnowledgeBase();
        query.setKnowledgeBaseId(knowledgeBaseId);
        AiKnowledgeBase knowledgeBase = aiKnowledgeBaseService.selectAiKnowledgeBase(query);
        if (knowledgeBase == null) {
            throw new ServiceException(message("ai.knowledge.base.not.exists.or.no.permission"));
        }
        AiKnowledgeDocument documentQuery = new AiKnowledgeDocument();
        documentQuery.setKnowledgeBaseId(knowledgeBaseId);
        List<AiKnowledgeDocument> documents = aiKnowledgeDocumentService.listAiKnowledgeDocument(documentQuery);

        AiKnowledgeRefreshResultVO result = initResult("KNOWLEDGE_BASE", knowledgeBaseId);
        result.setIndexedDocumentCount(0);
        result.setFailedDocumentCount(0);
        result.setIndexedSegmentCount(0);
        if (documents.isEmpty()) {
            result.setSuccess(Boolean.TRUE);
            result.setMessage(message("ai.knowledge.base.rebuild.empty"));
            result.setFinishedTime(new Date());
            return result;
        }

        StringBuilder failedReasonBuilder = new StringBuilder();
        for (AiKnowledgeDocument document : documents) {
            try {
                aiKnowledgeVectorService.removeDocumentIndex(document.getDocumentId());
                int segmentCount = aiKnowledgeVectorService.indexDocument(document.getDocumentId());
                updateDocumentStatus(document.getDocumentId(), "SUCCESS", segmentCount, message("ai.knowledge.base.rebuild.document.success"));
                result.setIndexedDocumentCount(result.getIndexedDocumentCount() + 1);
                result.setIndexedSegmentCount(result.getIndexedSegmentCount() + segmentCount);
            } catch (Exception ex) {
                String failedReason = trimReason(ex.getMessage());
                updateDocumentStatus(document.getDocumentId(), "FAILED", 0, failedReason);
                result.setFailedDocumentCount(result.getFailedDocumentCount() + 1);
                appendFailedReason(failedReasonBuilder, document.getDocumentId(), failedReason);
            }
        }
        result.setSuccess(result.getFailedDocumentCount() == 0);
        result.setFailedReason(failedReasonBuilder.toString());
        result.setMessage(result.getSuccess()
                ? message("ai.knowledge.base.rebuild.completed")
                : message("ai.knowledge.base.rebuild.completed.with.failed"));
        result.setFinishedTime(new Date());
        return result;
    }

    private AiKnowledgeRefreshResultVO initResult(String targetType, Long targetId) {
        AiKnowledgeRefreshResultVO result = new AiKnowledgeRefreshResultVO();
        result.setTargetType(targetType);
        result.setTargetId(targetId);
        result.setStartedTime(new Date());
        return result;
    }

    private void updateDocumentStatus(Long documentId, String parseStatus, Integer chunkCount, String remark) {
        AiKnowledgeDocument updateDocument = new AiKnowledgeDocument();
        updateDocument.setDocumentId(documentId);
        updateDocument.setParseStatus(parseStatus);
        updateDocument.setChunkCount(chunkCount);
        updateDocument.setRemark(trimReason(remark));
        updateDocument.setUpdateBy(AiSecuritySupport.resolveUsername());
        updateDocument.setUpdateTime(AiSecuritySupport.now());
        aiKnowledgeDocumentService.updateById(updateDocument);
    }

    private void appendFailedReason(StringBuilder builder, Long documentId, String failedReason) {
        if (builder.length() > 0) {
            builder.append("；");
        }
        builder.append(message("ai.knowledge.base.rebuild.failed.reason.item", documentId, failedReason));
    }

    private String trimReason(String reason) {
        if (reason == null) {
            return "";
        }
        String actualReason = reason.trim();
        return actualReason.length() > 500 ? actualReason.substring(0, 500) : actualReason;
    }
}
