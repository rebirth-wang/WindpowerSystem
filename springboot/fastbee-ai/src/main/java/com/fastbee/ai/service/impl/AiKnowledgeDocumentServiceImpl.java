package com.fastbee.ai.service.impl;

import static com.fastbee.ai.support.AiI18nMessageUtils.message;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import com.fastbee.ai.convert.AiKnowledgeDocumentConvert;
import com.fastbee.ai.domain.AiKnowledgeDocument;
import com.fastbee.ai.mapper.AiKnowledgeDocumentMapper;
import com.fastbee.ai.model.vo.AiKnowledgeDocumentVO;
import com.fastbee.ai.service.IAiKnowledgeDocumentService;
import com.fastbee.ai.service.IAiKnowledgeVectorService;
import com.fastbee.ai.support.AiSecuritySupport;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.utils.StringUtils;

/**
 * AI 知识文档服务实现。
 */
@Service
public class AiKnowledgeDocumentServiceImpl extends ServiceImpl<AiKnowledgeDocumentMapper, AiKnowledgeDocument> implements IAiKnowledgeDocumentService {

    @Resource
    private IAiKnowledgeVectorService aiKnowledgeVectorService;

    @Override
    public List<AiKnowledgeDocument> listAiKnowledgeDocument(AiKnowledgeDocument aiKnowledgeDocument) {
        LambdaQueryWrapper<AiKnowledgeDocument> lqw = buildQueryWrapper(aiKnowledgeDocument);
        lqw.orderByAsc(AiKnowledgeDocument::getSortNum)
                .orderByDesc(AiKnowledgeDocument::getUpdateTime)
                .orderByDesc(AiKnowledgeDocument::getCreateTime);
        return baseMapper.selectList(lqw);
    }

    @Override
    public List<AiKnowledgeDocumentVO> listAiKnowledgeDocumentVO(AiKnowledgeDocument aiKnowledgeDocument) {
        return AiKnowledgeDocumentConvert.INSTANCE.convertAiKnowledgeDocumentVOList(listAiKnowledgeDocument(aiKnowledgeDocument));
    }

    @Override
    public AiKnowledgeDocument selectAiKnowledgeDocument(Long documentId) {
        AiKnowledgeDocument query = new AiKnowledgeDocument();
        query.setDocumentId(documentId);
        return baseMapper.selectOne(buildQueryWrapper(query));
    }

    @Override
    public AiKnowledgeDocumentVO selectAiKnowledgeDocumentVO(Long documentId) {
        AiKnowledgeDocument document = selectAiKnowledgeDocument(documentId);
        if (document == null) {
            return null;
        }
        return AiKnowledgeDocumentConvert.INSTANCE.convertAiKnowledgeDocumentVO(document);
    }

    @Override
    public Page<AiKnowledgeDocument> pageAiKnowledgeDocument(AiKnowledgeDocument aiKnowledgeDocument) {
        AiKnowledgeDocument actualQuery = aiKnowledgeDocument == null ? new AiKnowledgeDocument() : aiKnowledgeDocument;
        LambdaQueryWrapper<AiKnowledgeDocument> lqw = buildQueryWrapper(actualQuery);
        lqw.orderByAsc(AiKnowledgeDocument::getSortNum)
                .orderByDesc(AiKnowledgeDocument::getUpdateTime)
                .orderByDesc(AiKnowledgeDocument::getCreateTime);
        return baseMapper.selectPage(new Page<>(actualQuery.getPageNum(), actualQuery.getPageSize()), lqw);
    }

    @Override
    public Page<AiKnowledgeDocumentVO> pageAiKnowledgeDocumentVO(AiKnowledgeDocument aiKnowledgeDocument) {
        return AiKnowledgeDocumentConvert.INSTANCE.convertAiKnowledgeDocumentVOPage(pageAiKnowledgeDocument(aiKnowledgeDocument));
    }

    @Override
    public int updateAiKnowledgeDocument(AiKnowledgeDocument aiKnowledgeDocument) {
        AiKnowledgeDocument old = requireKnowledgeDocument(aiKnowledgeDocument.getDocumentId());
        AiKnowledgeDocument updateDocument = new AiKnowledgeDocument();
        updateDocument.setDocumentId(old.getDocumentId());
        updateDocument.setSortNum(aiKnowledgeDocument.getSortNum() == null ? old.getSortNum() : aiKnowledgeDocument.getSortNum());
        updateDocument.setSourceOrigin(StringUtils.isBlank(aiKnowledgeDocument.getSourceOrigin())
                ? old.getSourceOrigin() : aiKnowledgeDocument.getSourceOrigin());
        updateDocument.setAppVersion(aiKnowledgeDocument.getAppVersion() == null ? old.getAppVersion() : aiKnowledgeDocument.getAppVersion());
        updateDocument.setStatus(StringUtils.isBlank(aiKnowledgeDocument.getStatus()) ? old.getStatus() : aiKnowledgeDocument.getStatus());
        updateDocument.setRemark(aiKnowledgeDocument.getRemark() == null ? old.getRemark() : aiKnowledgeDocument.getRemark());
        updateDocument.setUpdateBy(AiSecuritySupport.resolveUsername());
        updateDocument.setUpdateTime(AiSecuritySupport.now());
        return baseMapper.updateById(updateDocument);
    }

    @Override
    public int updateAiKnowledgeDocumentStatus(AiKnowledgeDocument aiKnowledgeDocument) {
        AiKnowledgeDocument old = requireKnowledgeDocument(aiKnowledgeDocument.getDocumentId());
        if (StringUtils.isBlank(aiKnowledgeDocument.getStatus())) {
            throw new ServiceException(message("ai.knowledge.document.status.required"));
        }
        AiKnowledgeDocument updateDocument = new AiKnowledgeDocument();
        updateDocument.setDocumentId(old.getDocumentId());
        updateDocument.setStatus(aiKnowledgeDocument.getStatus());
        updateDocument.setUpdateBy(AiSecuritySupport.resolveUsername());
        updateDocument.setUpdateTime(AiSecuritySupport.now());
        return baseMapper.updateById(updateDocument);
    }

    @Override
    public int deleteAiKnowledgeDocumentByIds(Long[] documentIds) {
        if (documentIds == null || documentIds.length == 0) {
            return 0;
        }
        List<AiKnowledgeDocument> documentList = baseMapper.selectBatchIds(Arrays.asList(documentIds));
        int rows = baseMapper.deleteBatchIds(Arrays.asList(documentIds));
        for (AiKnowledgeDocument document : documentList) {
            if (document != null && document.getDocumentId() != null) {
                aiKnowledgeVectorService.removeDocumentIndex(document.getDocumentId());
            }
        }
        return rows;
    }

    private LambdaQueryWrapper<AiKnowledgeDocument> buildQueryWrapper(AiKnowledgeDocument query) {
        AiKnowledgeDocument actualQuery = query == null ? new AiKnowledgeDocument() : query;
        Map<String, Object> params = actualQuery.getParams() == null ? Collections.emptyMap() : actualQuery.getParams();
        LambdaQueryWrapper<AiKnowledgeDocument> lqw = Wrappers.lambdaQuery();
        lqw.eq(actualQuery.getDocumentId() != null, AiKnowledgeDocument::getDocumentId, actualQuery.getDocumentId());
        lqw.eq(actualQuery.getKnowledgeBaseId() != null, AiKnowledgeDocument::getKnowledgeBaseId, actualQuery.getKnowledgeBaseId());
        lqw.like(StringUtils.isNotBlank(actualQuery.getFileName()), AiKnowledgeDocument::getFileName, actualQuery.getFileName());
        lqw.eq(StringUtils.isNotBlank(actualQuery.getParseStatus()), AiKnowledgeDocument::getParseStatus, actualQuery.getParseStatus());
        lqw.eq(actualQuery.getSortNum() != null, AiKnowledgeDocument::getSortNum, actualQuery.getSortNum());
        lqw.eq(StringUtils.isNotBlank(actualQuery.getSourceOrigin()), AiKnowledgeDocument::getSourceOrigin, actualQuery.getSourceOrigin());
        lqw.eq(StringUtils.isNotBlank(actualQuery.getAppVersion()), AiKnowledgeDocument::getAppVersion, actualQuery.getAppVersion());
        lqw.eq(StringUtils.isNotBlank(actualQuery.getStatus()), AiKnowledgeDocument::getStatus, actualQuery.getStatus());
        if (Objects.nonNull(params.get("beginTime")) && Objects.nonNull(params.get("endTime"))) {
            lqw.between(AiKnowledgeDocument::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }

    private AiKnowledgeDocument requireKnowledgeDocument(Long documentId) {
        if (documentId == null) {
            throw new ServiceException(message("ai.knowledge.document.id.required"));
        }
        AiKnowledgeDocument document = baseMapper.selectById(documentId);
        if (document == null) {
            throw new ServiceException(message("ai.knowledge.document.not.exists.or.deleted"));
        }
        return document;
    }
}
