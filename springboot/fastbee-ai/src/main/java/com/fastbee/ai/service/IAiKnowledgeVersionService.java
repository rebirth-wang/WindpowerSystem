package com.fastbee.ai.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.ai.domain.AiKnowledgeVersion;
import com.fastbee.ai.model.vo.AiKnowledgeVersionQualityCheckVO;
import com.fastbee.ai.model.vo.AiKnowledgeVersionVO;

/**
 * AI 知识库版本服务接口。
 */
public interface IAiKnowledgeVersionService extends IService<AiKnowledgeVersion> {

    /**
     * 查询知识库版本列表。
     *
     * @param aiKnowledgeVersion 查询条件
     * @return 版本列表
     */
    List<AiKnowledgeVersion> listAiKnowledgeVersion(AiKnowledgeVersion aiKnowledgeVersion);

    /**
     * 查询知识库版本展示列表。
     *
     * @param aiKnowledgeVersion 查询条件
     * @return 版本展示列表
     */
    List<AiKnowledgeVersionVO> listAiKnowledgeVersionVO(AiKnowledgeVersion aiKnowledgeVersion);

    /**
     * 分页查询知识库版本。
     *
     * @param aiKnowledgeVersion 查询条件
     * @return 分页结果
     */
    Page<AiKnowledgeVersion> pageAiKnowledgeVersion(AiKnowledgeVersion aiKnowledgeVersion);

    /**
     * 分页查询知识库版本展示列表。
     *
     * @param aiKnowledgeVersion 查询条件
     * @return 分页结果
     */
    Page<AiKnowledgeVersionVO> pageAiKnowledgeVersionVO(AiKnowledgeVersion aiKnowledgeVersion);

    /**
     * 查询知识库版本详情。
     *
     * @param versionId 版本ID
     * @return 版本信息
     */
    AiKnowledgeVersion selectAiKnowledgeVersion(Long versionId);

    /**
     * 查询知识库版本展示详情。
     *
     * @param versionId 版本 ID
     * @return 版本展示信息
     */
    AiKnowledgeVersionVO selectAiKnowledgeVersionVO(Long versionId);

    /**
     * 新增知识库版本。
     *
     * @param aiKnowledgeVersion 版本信息
     * @return 影响行数
     */
    int insertAiKnowledgeVersion(AiKnowledgeVersion aiKnowledgeVersion);

    /**
     * 修改知识库版本。
     *
     * @param aiKnowledgeVersion 版本信息
     * @return 影响行数
     */
    int updateAiKnowledgeVersion(AiKnowledgeVersion aiKnowledgeVersion);

    /**
     * 删除知识库版本。
     *
     * @param versionIds 版本ID集合
     * @return 影响行数
     */
    int deleteAiKnowledgeVersionByIds(Long[] versionIds);

    /**
     * 根据当前知识库下已启用且解析成功的文档构建草稿版本。
     *
     * @param knowledgeBaseId 知识库 ID
     * @return 构建后的草稿版本
     */
    AiKnowledgeVersion buildDraftAiKnowledgeVersion(Long knowledgeBaseId);

    /**
     * 预检知识库版本质量。
     *
     * @param versionId 版本 ID
     * @return 预检结果
     */
    AiKnowledgeVersionQualityCheckVO previewAiKnowledgeVersionQuality(Long versionId);

    /**
     * 发布知识库版本。
     *
     * @param versionId 版本 ID
     * @return 影响行数
     */
    int publishAiKnowledgeVersion(Long versionId);

    /**
     * 回滚到指定知识库版本。
     *
     * @param versionId 目标版本 ID
     * @return 影响行数
     */
    int rollbackAiKnowledgeVersion(Long versionId);
}
