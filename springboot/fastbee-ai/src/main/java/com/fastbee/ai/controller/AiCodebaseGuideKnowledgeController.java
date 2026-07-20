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
import org.springframework.web.multipart.MultipartFile;

import com.fastbee.ai.model.vo.AiKnowledgeTemplateUploadResultVO;
import com.fastbee.ai.service.IAiCodebaseGuideKnowledgeService;
import com.fastbee.ai.support.AiSecuritySupport;
import com.fastbee.common.annotation.Log;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.enums.BusinessType;

/**
 * 源码导航知识控制器。
 */
@Api(tags = "AI 源码导航知识")
@RestController
@RequestMapping("/ai/knowledge/codebase")
public class AiCodebaseGuideKnowledgeController {

    @Resource
    private IAiCodebaseGuideKnowledgeService aiCodebaseGuideKnowledgeService;

    /**
     * 重建源码导航知识。
     *
     * @param knowledgeBaseId 知识库 ID
     * @return 构建结果
     */
    @ApiOperation("重建源码导航知识")
    @PreAuthorize("@ss.hasPermi('ai:knowledge:edit')")
    @Log(title = "AI 源码导航知识", businessType = BusinessType.UPDATE)
    @PostMapping("/rebuild/{knowledgeBaseId}")
    public AjaxResult rebuild(@PathVariable("knowledgeBaseId") Long knowledgeBaseId) {
        AiSecuritySupport.requireAdminAccount("重建源码导航知识");
        AiKnowledgeTemplateUploadResultVO result = aiCodebaseGuideKnowledgeService.rebuildCodebaseGuide(knowledgeBaseId);
        return AjaxResult.success(message("ai.codebase.guide.rebuild.success"), result);
    }

    /**
     * 上传源码导航安全摘要 JSON。
     *
     * @param knowledgeBaseId 知识库 ID
     * @param sourceOrigin    来源类型
     * @param appVersion      平台版本
     * @param sortNum         排序号
     * @param file            源码导航安全摘要 JSON
     * @return 导入结果
     */
    @ApiOperation("上传源码导航安全摘要")
    @PreAuthorize("@ss.hasPermi('ai:knowledge:edit')")
    @Log(title = "AI 源码导航知识", businessType = BusinessType.IMPORT)
    @PostMapping("/upload")
    public AjaxResult upload(@RequestParam("knowledgeBaseId") Long knowledgeBaseId,
                             @RequestParam(value = "sourceOrigin", required = false) String sourceOrigin,
                             @RequestParam(value = "appVersion", required = false) String appVersion,
                             @RequestParam(value = "sortNum", required = false) Integer sortNum,
                             @RequestParam("file") MultipartFile file) {
        AiSecuritySupport.requireAdminAccount("上传源码导航安全摘要");
        AiKnowledgeTemplateUploadResultVO result = aiCodebaseGuideKnowledgeService.uploadCodebaseGuideSnapshot(
                knowledgeBaseId, sourceOrigin, appVersion, sortNum, file);
        return AjaxResult.success(message("ai.codebase.guide.snapshot.upload.success"), result);
    }

    /**
     * 预览源码导航知识上下文。
     *
     * @param question 用户问题
     * @return 源码导航上下文
     */
    @ApiOperation("预览源码导航知识上下文")
    @PreAuthorize("@ss.hasPermi('ai:knowledge:query')")
    @GetMapping("/context")
    public AjaxResult context(@RequestParam(value = "question", required = false) String question) {
        return AjaxResult.success(aiCodebaseGuideKnowledgeService.buildCodebaseContext(question));
    }
}
