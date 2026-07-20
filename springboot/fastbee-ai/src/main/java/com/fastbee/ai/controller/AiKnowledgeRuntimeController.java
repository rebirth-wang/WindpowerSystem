package com.fastbee.ai.controller;

import jakarta.annotation.Resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fastbee.ai.service.IAiKnowledgeRuntimeService;
import com.fastbee.common.annotation.Log;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.enums.BusinessType;

/**
 * AI 知识库运行时治理控制器。
 */
@Api(tags = "AI 知识库运行时治理")
@RestController
@RequestMapping("/ai/knowledge/runtime")
public class AiKnowledgeRuntimeController {

    @Resource
    private IAiKnowledgeRuntimeService aiKnowledgeRuntimeService;

    /**
     * 查询知识库运行时状态。
     *
     * @param knowledgeBaseId 知识库 ID
     * @return 运行时状态
     */
    @ApiOperation("查询知识库运行时状态")
    @PreAuthorize("@ss.hasPermi('ai:knowledge:query')")
    @GetMapping("/{knowledgeBaseId}")
    public AjaxResult getRuntimeStatus(@PathVariable("knowledgeBaseId") Long knowledgeBaseId) {
        return AjaxResult.success(aiKnowledgeRuntimeService.getRuntimeStatus(knowledgeBaseId));
    }

    /**
     * 重建知识库运行时语义包。
     *
     * @param knowledgeBaseId 知识库 ID
     * @return 重建结果
     */
    @ApiOperation("重建知识库运行时语义包")
    @PreAuthorize("@ss.hasPermi('ai:knowledge:edit')")
    @Log(title = "AI 知识库运行时", businessType = BusinessType.UPDATE)
    @PostMapping("/rebuild/{knowledgeBaseId}")
    public AjaxResult rebuildRuntime(@PathVariable("knowledgeBaseId") Long knowledgeBaseId) {
        return AjaxResult.success(aiKnowledgeRuntimeService.rebuildNl2SqlRuntime(knowledgeBaseId));
    }
}
