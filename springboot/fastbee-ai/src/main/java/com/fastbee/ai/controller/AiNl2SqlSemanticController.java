package com.fastbee.ai.controller;

import jakarta.annotation.Resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fastbee.ai.model.vo.AiSemanticContextVO;
import com.fastbee.ai.service.IAiSemanticNormalizationService;
import com.fastbee.common.core.domain.AjaxResult;

/**
 * AI NL2SQL 语义预览控制器。
 */
@Api(tags = "AI NL2SQL Semantic")
@RestController
@RequestMapping("/ai/nl2sql/semantic")
public class AiNl2SqlSemanticController {

    @Resource
    private IAiSemanticNormalizationService aiSemanticNormalizationService;

    /**
     * 预览当前问题命中的问数语义上下文。
     *
     * @param question 用户问题
     * @return 语义上下文
     */
    @ApiOperation("Preview semantic context")
    @PreAuthorize("@ss.hasPermi('ai:chat:query')")
    @GetMapping("/context")
    public AjaxResult context(@RequestParam("question") String question) {
        AiSemanticContextVO context = aiSemanticNormalizationService.buildNl2SqlContext(question);
        return AjaxResult.success(context);
    }
}
