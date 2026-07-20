package com.fastbee.ai.controller;

import static com.fastbee.ai.support.AiI18nMessageUtils.message;

import jakarta.annotation.Resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fastbee.ai.service.IAiKnowledgeVectorService;
import com.fastbee.common.core.domain.AjaxResult;

/**
 * AI 知识向量检索控制器。
 */
@Api(tags = "AI 知识向量检索")
@RestController
@RequestMapping("/ai/knowledge/vector")
public class AiKnowledgeVectorController {

    @Resource
    private IAiKnowledgeVectorService aiKnowledgeVectorService;

    /**
     * 为指定知识文档建立向量索引。
     *
     * @param documentId 文档 ID
     * @return 建立结果
     */
    @ApiOperation("为指定知识文档建立向量索引")
    @PreAuthorize("@ss.hasPermi('ai:knowledge:edit')")
    @PostMapping("/document/{documentId}/index")
    public AjaxResult indexDocument(@PathVariable("documentId") Long documentId) {
        int indexedSegments = aiKnowledgeVectorService.indexDocument(documentId);
        return AjaxResult.success(message("ai.knowledge.vector.index.success", indexedSegments));
    }

    /**
     * 执行知识检索。
     *
     * @param knowledgeBaseId 知识库 ID
     * @param query 查询内容
     * @param topK 返回条数
     * @return 检索结果
     */
    @ApiOperation("执行知识检索")
    @PreAuthorize("@ss.hasPermi('ai:knowledge:query')")
    @GetMapping("/search")
    public AjaxResult search(@RequestParam("knowledgeBaseId") Long knowledgeBaseId,
                             @RequestParam("query") String query,
                             @RequestParam(value = "topK", required = false) Integer topK) {
        return AjaxResult.success(aiKnowledgeVectorService.search(knowledgeBaseId, query, topK));
    }
}
