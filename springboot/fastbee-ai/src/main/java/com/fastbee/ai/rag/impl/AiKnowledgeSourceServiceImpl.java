package com.fastbee.ai.rag.impl;

import static com.fastbee.ai.support.AiI18nMessageUtils.message;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fastbee.ai.config.properties.FastBeeAiProperties;
import com.fastbee.ai.domain.AiKnowledgeBase;
import com.fastbee.ai.domain.AiKnowledgeDocument;
import com.fastbee.ai.model.vo.AiKnowledgeBaseVO;
import com.fastbee.ai.rag.IAiKnowledgeDocumentParser;
import com.fastbee.ai.rag.IAiKnowledgeSourceService;
import com.fastbee.ai.rag.model.AiKnowledgeDocumentReference;
import com.fastbee.ai.rag.model.AiKnowledgeSegment;
import com.fastbee.ai.rag.model.AiKnowledgeSourceDefinition;
import com.fastbee.ai.service.IAiKnowledgeBaseService;
import com.fastbee.ai.service.IAiKnowledgeDocumentService;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.utils.StringUtils;

/**
 * AI 知识源抽象服务实现。
 */
@Service
public class AiKnowledgeSourceServiceImpl implements IAiKnowledgeSourceService {

    @Resource
    private IAiKnowledgeBaseService aiKnowledgeBaseService;

    @Resource
    private IAiKnowledgeDocumentService aiKnowledgeDocumentService;

    @Resource
    private IAiKnowledgeDocumentParser aiKnowledgeDocumentParser;

    @Resource
    private FastBeeAiProperties fastBeeAiProperties;

    @Override
    public List<AiKnowledgeSourceDefinition> listKnowledgeSources() {
        AiKnowledgeBase query = new AiKnowledgeBase();
        return aiKnowledgeBaseService.listAiKnowledgeBaseVO(query).stream()
                .map(this::buildSourceDefinition)
                .collect(Collectors.toList());
    }

    @Override
    public List<AiKnowledgeDocumentReference> listKnowledgeDocuments(Long knowledgeBaseId) {
        if (knowledgeBaseId == null) {
            return Collections.emptyList();
        }
        AiKnowledgeBase knowledgeBase = requireKnowledgeBase(knowledgeBaseId);
        AiKnowledgeDocument query = new AiKnowledgeDocument();
        query.setKnowledgeBaseId(knowledgeBaseId);
        return aiKnowledgeDocumentService.listAiKnowledgeDocument(query).stream()
                .map(document -> buildDocumentReference(knowledgeBase, document))
                .collect(Collectors.toList());
    }

    @Override
    public List<AiKnowledgeSegment> listKnowledgeSegments(Long documentId) {
        if (documentId == null) {
            return Collections.emptyList();
        }
        AiKnowledgeDocument document = aiKnowledgeDocumentService.selectAiKnowledgeDocument(documentId);
        if (document == null) {
            throw new ServiceException(message("ai.knowledge.document.not.exists.or.no.permission"));
        }
        AiKnowledgeBase knowledgeBase = requireKnowledgeBase(document.getKnowledgeBaseId());
        AiKnowledgeDocumentReference documentReference = buildDocumentReference(knowledgeBase, document);
        String documentContent = loadDocumentContent(documentReference);
        return aiKnowledgeDocumentParser.split(documentReference, documentContent);
    }

    private AiKnowledgeBase requireKnowledgeBase(Long knowledgeBaseId) {
        AiKnowledgeBase query = new AiKnowledgeBase();
        query.setKnowledgeBaseId(knowledgeBaseId);
        AiKnowledgeBase knowledgeBase = aiKnowledgeBaseService.selectAiKnowledgeBase(query);
        if (knowledgeBase == null) {
            throw new ServiceException(message("ai.knowledge.base.not.exists.or.no.permission"));
        }
        return knowledgeBase;
    }

    private AiKnowledgeSourceDefinition buildSourceDefinition(AiKnowledgeBaseVO knowledgeBase) {
        AiKnowledgeSourceDefinition definition = new AiKnowledgeSourceDefinition();
        definition.setKnowledgeBaseId(knowledgeBase.getKnowledgeBaseId());
        definition.setSourceCode(knowledgeBase.getKbCode());
        definition.setSourceName(knowledgeBase.getKbName());
        definition.setSourceType(knowledgeBase.getKbType());
        definition.setVectorStoreType(knowledgeBase.getVectorStoreType());
        definition.setPublishStatus(knowledgeBase.getPublishStatus());
        definition.setActiveVersionId(knowledgeBase.getActiveVersionId());
        definition.setDocumentCount(knowledgeBase.getDocumentCount());
        definition.setEnabled("0".equals(knowledgeBase.getStatus()));
        definition.setStatus(knowledgeBase.getStatus());
        return definition;
    }

    private AiKnowledgeDocumentReference buildDocumentReference(AiKnowledgeBase knowledgeBase, AiKnowledgeDocument document) {
        AiKnowledgeDocumentReference reference = new AiKnowledgeDocumentReference();
        reference.setDocumentId(document.getDocumentId());
        reference.setDocumentCode("DOC_" + document.getDocumentId());
        reference.setKnowledgeBaseId(document.getKnowledgeBaseId());
        reference.setSourceCode(knowledgeBase.getKbCode());
        reference.setSourceName(knowledgeBase.getKbName());
        reference.setFileName(document.getFileName());
        reference.setFilePath(document.getFilePath());
        reference.setFileSize(document.getFileSize());
        reference.setChecksum(document.getChecksum());
        reference.setParseStatus(document.getParseStatus());
        reference.setChunkCount(document.getChunkCount());
        reference.setParsedSnapshotPath(document.getParsedSnapshotPath());
        reference.setParsedSummary(document.getParsedSummary());
        reference.setSortNum(document.getSortNum());
        reference.setSourceOrigin(document.getSourceOrigin());
        reference.setAppVersion(document.getAppVersion());
        reference.setPublishStatus(knowledgeBase.getPublishStatus());
        reference.setEnabled("0".equals(document.getStatus()));
        reference.setStatus(document.getStatus());
        reference.setCreateTime(document.getCreateTime());
        reference.setUpdateTime(document.getUpdateTime());
        return reference;
    }

    private String loadDocumentContent(AiKnowledgeDocumentReference documentReference) {
        if (StringUtils.isBlank(documentReference.getFilePath())) {
            throw new ServiceException(message("ai.knowledge.document.file.path.required.segment"));
        }
        String filePath = documentReference.getFilePath().trim();
        if (!isSupportedTextFile(documentReference)) {
            throw new ServiceException(message("ai.knowledge.document.type.unsupported.parse", documentReference.getFileName()));
        }
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new ServiceException(message("ai.knowledge.document.file.not.exists", filePath));
        }
        try {
            String content = Files.readString(path, StandardCharsets.UTF_8);
            int maxDocumentChars = Math.max(2000, fastBeeAiProperties.getRag().getMaxDocumentChars());
            if (content.length() > maxDocumentChars) {
                return content.substring(0, maxDocumentChars);
            }
            return content;
        } catch (IOException ex) {
            throw new ServiceException(message("ai.knowledge.document.read.failed", ex.getMessage()));
        }
    }

    private boolean isSupportedTextFile(AiKnowledgeDocumentReference documentReference) {
        String fileName = Objects.toString(documentReference.getFileName(), "");
        String filePath = Objects.toString(documentReference.getFilePath(), "");
        String suffixSource = StringUtils.isNotBlank(fileName) ? fileName : filePath;
        int dotIndex = suffixSource.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex >= suffixSource.length() - 1) {
            return true;
        }
        String suffix = suffixSource.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
        return "txt".equals(suffix)
                || "md".equals(suffix)
                || "json".equals(suffix)
                || "csv".equals(suffix)
                || "log".equals(suffix)
                || "yaml".equals(suffix)
                || "yml".equals(suffix)
                || "xml".equals(suffix)
                || "sql".equals(suffix)
                || "properties".equals(suffix);
    }
}
