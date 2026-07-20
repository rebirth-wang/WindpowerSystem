package com.fastbee.ai.controller;

import jakarta.annotation.Resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fastbee.ai.rag.IAiKnowledgeSourceService;
import com.fastbee.common.core.domain.AjaxResult;

/**
 * AI 知识源抽象控制器。
 */
@Api(tags = "AI 知识源")
@RestController
@RequestMapping("/ai/knowledge/source")
public class AiKnowledgeSourceController {

    @Resource
    private IAiKnowledgeSourceService aiKnowledgeSourceService;

    /**
     * 查询知识源列表。
     *
     * @return 知识源列表
     */
    @ApiOperation("查询知识源列表")
    @PreAuthorize("@ss.hasPermi('ai:knowledge:query')")
    @GetMapping("/list")
    public AjaxResult listSources() {
        return AjaxResult.success(aiKnowledgeSourceService.listKnowledgeSources());
    }

    /**
     * 查询知识源下的文档列表。
     *
     * @param knowledgeBaseId 知识库 ID
     * @return 文档列表
     */
    @ApiOperation("查询知识源下的文档列表")
    @PreAuthorize("@ss.hasPermi('ai:knowledge:query')")
    @GetMapping("/{knowledgeBaseId}/documents")
    public AjaxResult listDocuments(@PathVariable("knowledgeBaseId") Long knowledgeBaseId) {
        return AjaxResult.success(aiKnowledgeSourceService.listKnowledgeDocuments(knowledgeBaseId));
    }

    /**
     * 查询知识文档分段结果。
     *
     * @param documentId 文档 ID
     * @return 分段结果
     */
    @ApiOperation("查询知识文档分段结果")
    @PreAuthorize("@ss.hasPermi('ai:knowledge:query')")
    @GetMapping("/document/{documentId}/segments")
    public AjaxResult listSegments(@PathVariable("documentId") Long documentId) {
        return AjaxResult.success(aiKnowledgeSourceService.listKnowledgeSegments(documentId));
    }
}
