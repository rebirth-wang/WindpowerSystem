package com.fastbee.ai.controller;

import jakarta.annotation.Resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fastbee.ai.service.IAiPlatformDocKnowledgeService;
import com.fastbee.common.core.domain.AjaxResult;

/**
 * 平台文档知识上下文控制器。
 */
@Api(tags = "平台文档知识")
@RestController
@RequestMapping("/ai/platform/knowledge")
public class AiPlatformDocKnowledgeController {

    @Resource
    private IAiPlatformDocKnowledgeService aiPlatformDocKnowledgeService;

    /**
     * 预览平台文档知识上下文。
     *
     * @param question 用户问题
     * @return 平台文档知识上下文
     */
    @ApiOperation("预览平台文档知识上下文")
    @PreAuthorize("@ss.hasPermi('ai:knowledge:query')")
    @GetMapping("/context")
    public AjaxResult context(@RequestParam(value = "question", required = false) String question) {
        return AjaxResult.success(aiPlatformDocKnowledgeService.buildPlatformContext(question));
    }
}
