package com.fastbee.ai.controller;

import jakarta.annotation.Resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fastbee.ai.rag.IAiRagService;
import com.fastbee.ai.rag.model.AiRagQueryRequest;
import com.fastbee.common.core.domain.AjaxResult;

/**
 * AI RAG 控制器。
 */
@Api(tags = "AI RAG")
@RestController
@RequestMapping("/ai/rag")
public class AiRagController {

    @Resource
    private IAiRagService aiRagService;

    /**
     * 执行一次检索增强流程。
     *
     * @param request 检索请求
     * @return 流程结果
     */
    @ApiOperation("执行一次检索增强流程")
    @PreAuthorize("@ss.hasPermi('ai:knowledge:query')")
    @PostMapping("/retrieve")
    public AjaxResult retrieve(@RequestBody AiRagQueryRequest request) {
        return AjaxResult.success(aiRagService.retrieve(request));
    }
}
