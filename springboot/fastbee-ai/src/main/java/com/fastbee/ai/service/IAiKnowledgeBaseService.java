package com.fastbee.ai.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.ai.domain.AiKnowledgeBase;
import com.fastbee.ai.model.vo.AiKnowledgeBaseVO;

/**
 * AI 知识库服务接口。
 */
public interface IAiKnowledgeBaseService extends IService<AiKnowledgeBase> {

    /**
     * 查询知识库列表。
     *
     * @param aiKnowledgeBase 查询条件
     * @return 知识库列表
     */
    List<AiKnowledgeBase> listAiKnowledgeBase(AiKnowledgeBase aiKnowledgeBase);

    /**
     * 查询知识库展示列表。
     *
     * @param aiKnowledgeBase 查询条件
     * @return 知识库展示列表
     */
    List<AiKnowledgeBaseVO> listAiKnowledgeBaseVO(AiKnowledgeBase aiKnowledgeBase);

    /**
     * 查询知识库详情。
     *
     * @param aiKnowledgeBase 查询条件
     * @return 知识库信息
     */
    AiKnowledgeBase selectAiKnowledgeBase(AiKnowledgeBase aiKnowledgeBase);

    /**
     * 查询知识库展示详情。
     *
     * @param aiKnowledgeBase 查询条件
     * @return 知识库展示信息
     */
    AiKnowledgeBaseVO selectAiKnowledgeBaseVO(AiKnowledgeBase aiKnowledgeBase);

    /**
     * 分页查询知识库。
     *
     * @param aiKnowledgeBase 查询条件
     * @return 分页结果
     */
    Page<AiKnowledgeBase> pageAiKnowledgeBase(AiKnowledgeBase aiKnowledgeBase);

    /**
     * 分页查询知识库展示列表。
     *
     * @param aiKnowledgeBase 查询条件
     * @return 分页结果
     */
    Page<AiKnowledgeBaseVO> pageAiKnowledgeBaseVO(AiKnowledgeBase aiKnowledgeBase);

    /**
     * 新增知识库。
     *
     * @param aiKnowledgeBase 知识库信息
     * @return 影响行数
     */
    int insertAiKnowledgeBase(AiKnowledgeBase aiKnowledgeBase);

    /**
     * 修改知识库。
     *
     * @param aiKnowledgeBase 知识库信息
     * @return 影响行数
     */
    int updateAiKnowledgeBase(AiKnowledgeBase aiKnowledgeBase);

    /**
     * 修改知识库状态。
     *
     * @param aiKnowledgeBase 知识库信息
     * @return 影响行数
     */
    int updateAiKnowledgeBaseStatus(AiKnowledgeBase aiKnowledgeBase);

    /**
     * 删除知识库。
     *
     * @param knowledgeBaseIds 知识库 ID 集合
     * @return 影响行数
     */
    int deleteAiKnowledgeBaseByIds(Long[] knowledgeBaseIds);
}
