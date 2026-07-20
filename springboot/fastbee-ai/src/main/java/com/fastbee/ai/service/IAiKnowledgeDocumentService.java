package com.fastbee.ai.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.ai.domain.AiKnowledgeDocument;
import com.fastbee.ai.model.vo.AiKnowledgeDocumentVO;

/**
 * AI 知识文档服务接口。
 */
public interface IAiKnowledgeDocumentService extends IService<AiKnowledgeDocument> {

    /**
     * 查询知识文档列表。
     *
     * @param aiKnowledgeDocument 查询条件
     * @return 文档列表
     */
    List<AiKnowledgeDocument> listAiKnowledgeDocument(AiKnowledgeDocument aiKnowledgeDocument);

    /**
     * 查询知识文档展示列表。
     *
     * @param aiKnowledgeDocument 查询条件
     * @return 文档展示列表
     */
    List<AiKnowledgeDocumentVO> listAiKnowledgeDocumentVO(AiKnowledgeDocument aiKnowledgeDocument);

    /**
     * 分页查询知识文档列表。
     *
     * @param aiKnowledgeDocument 查询条件
     * @return 分页结果
     */
    Page<AiKnowledgeDocument> pageAiKnowledgeDocument(AiKnowledgeDocument aiKnowledgeDocument);

    /**
     * 分页查询知识文档展示列表。
     *
     * @param aiKnowledgeDocument 查询条件
     * @return 分页结果
     */
    Page<AiKnowledgeDocumentVO> pageAiKnowledgeDocumentVO(AiKnowledgeDocument aiKnowledgeDocument);

    /**
     * 查询知识文档详情。
     *
     * @param documentId 文档 ID
     * @return 文档信息
     */
    AiKnowledgeDocument selectAiKnowledgeDocument(Long documentId);

    /**
     * 查询知识文档展示详情。
     *
     * @param documentId 文档 ID
     * @return 文档展示信息
     */
    AiKnowledgeDocumentVO selectAiKnowledgeDocumentVO(Long documentId);

    /**
     * 修改知识文档。
     *
     * @param aiKnowledgeDocument 文档信息
     * @return 影响行数
     */
    int updateAiKnowledgeDocument(AiKnowledgeDocument aiKnowledgeDocument);

    /**
     * 修改知识文档状态。
     *
     * @param aiKnowledgeDocument 文档信息
     * @return 影响行数
     */
    int updateAiKnowledgeDocumentStatus(AiKnowledgeDocument aiKnowledgeDocument);

    /**
     * 删除知识文档。
     *
     * @param documentIds 文档 ID 集合
     * @return 影响行数
     */
    int deleteAiKnowledgeDocumentByIds(Long[] documentIds);
}
