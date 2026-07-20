package com.fastbee.ai.controller;

import jakarta.annotation.Resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fastbee.ai.service.IAiKnowledgeRefreshService;
import com.fastbee.common.core.domain.AjaxResult;

/**
 * AI 知识更新控制器。
 */
@Api(tags = "AI 知识更新")
@RestController
@RequestMapping("/ai/knowledge/refresh")
public class AiKnowledgeRefreshController {

    @Resource
    private IAiKnowledgeRefreshService aiKnowledgeRefreshService;

    /**
     * 刷新单个知识文档。
     *
     * @param documentId 文档 ID
     * @return 刷新结果
     */
    @ApiOperation("刷新单个知识文档")
    @PreAuthorize("@ss.hasPermi('ai:knowledge:edit')")
    @PostMapping("/document/{documentId}")
    public AjaxResult refreshDocument(@PathVariable("documentId") Long documentId) {
        return AjaxResult.success(aiKnowledgeRefreshService.refreshDocument(documentId));
    }

    /**
     * 全量重建知识库索引。
     *
     * @param knowledgeBaseId 知识库 ID
     * @return 重建结果
     */
    @ApiOperation("全量重建知识库索引")
    @PreAuthorize("@ss.hasPermi('ai:knowledge:edit')")
    @PostMapping("/base/{knowledgeBaseId}")
    public AjaxResult rebuildKnowledgeBase(@PathVariable("knowledgeBaseId") Long knowledgeBaseId) {
        return AjaxResult.success(aiKnowledgeRefreshService.rebuildKnowledgeBase(knowledgeBaseId));
    }
}
