package com.fastbee.ai.controller;

import jakarta.annotation.Resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fastbee.ai.model.vo.AiProtocolKnowledgeContextVO;
import com.fastbee.ai.service.IAiProtocolKnowledgeService;
import com.fastbee.common.core.domain.AjaxResult;

/**
 * AI 协议知识上下文控制器。
 */
@Api(tags = "AI 协议知识上下文")
@RestController
@RequestMapping("/ai/protocol/knowledge")
public class AiProtocolKnowledgeController {

    @Resource
    private IAiProtocolKnowledgeService aiProtocolKnowledgeService;

    /**
     * 预览当前问题命中的协议知识上下文。
     *
     * @param question 用户问题
     * @return 协议知识上下文
     */
    @ApiOperation("预览协议知识上下文")
    @PreAuthorize("@ss.hasPermi('ai:chat:query')")
    @GetMapping("/context")
    public AjaxResult context(@RequestParam("question") String question) {
        AiProtocolKnowledgeContextVO context = aiProtocolKnowledgeService.buildProtocolContext(question);
        return AjaxResult.success(context);
    }
}
