package com.fastbee.ai.controller;

import static com.fastbee.ai.support.AiI18nMessageUtils.message;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fastbee.ai.model.vo.AiKnowledgeTemplateUploadResultVO;
import com.fastbee.ai.service.IAiKnowledgeTemplateService;
import com.fastbee.ai.support.AiSecuritySupport;
import com.fastbee.common.annotation.Log;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.enums.BusinessType;

/**
 * AI 知识库模板控制器。
 */
@Api(tags = "AI 知识库模板")
@RestController
@RequestMapping("/ai/knowledge/template")
public class AiKnowledgeTemplateController {

    private static final String TEMPLATE_MODE_ENTERPRISE_EXPORT = "ENTERPRISE_EXPORT";

    private static final String TEMPLATE_MODE_ENTERPRISE_DOC_EXPORT = "ENTERPRISE_DOC_EXPORT";

    @Resource
    private IAiKnowledgeTemplateService aiKnowledgeTemplateService;

    /**
     * 下载知识库模板。
     *
     * @param response        响应对象
     * @param knowledgeBaseId 知识库 ID
     * @param kbType          知识库类型
     * @param templateMode    模板模式
     * @param sourceStrategy  导出源策略
     */
    @ApiOperation("下载知识库模板")
    @PreAuthorize("@ss.hasPermi('ai:knowledge:query')")
    @PostMapping("/download")
    public void downloadTemplate(HttpServletResponse response,
                                 @RequestParam(value = "knowledgeBaseId", required = false) Long knowledgeBaseId,
                                 @RequestParam(value = "kbType", required = false) String kbType,
                                 @RequestParam(value = "templateMode", required = false) String templateMode,
                                 @RequestParam(value = "sourceStrategy", required = false) String sourceStrategy) {
        if (isEnterpriseTemplateMode(templateMode)) {
            AiSecuritySupport.requireAdminAccount("导出企业版模板");
        }
        aiKnowledgeTemplateService.downloadTemplate(response, knowledgeBaseId, kbType, templateMode, sourceStrategy);
    }

    private boolean isEnterpriseTemplateMode(String templateMode) {
        return TEMPLATE_MODE_ENTERPRISE_EXPORT.equals(templateMode)
                || TEMPLATE_MODE_ENTERPRISE_DOC_EXPORT.equals(templateMode);
    }

    /**
     * 上传知识库模板并自动解析。
     *
     * @param knowledgeBaseId 知识库 ID
     * @param sourceOrigin    来源类型
     * @param appVersion      平台版本
     * @param sortNum         排序号
     * @param file            模板文件
     * @return 解析结果
     * @throws Exception 上传异常
     */
    @ApiOperation("上传并解析知识库模板")
    @PreAuthorize("@ss.hasPermi('ai:knowledge:edit')")
    @Log(title = "AI 知识库模板", businessType = BusinessType.IMPORT)
    @PostMapping("/upload")
    public AjaxResult uploadTemplate(@RequestParam("knowledgeBaseId") Long knowledgeBaseId,
                                     @RequestParam(value = "sourceOrigin", required = false) String sourceOrigin,
                                     @RequestParam(value = "appVersion", required = false) String appVersion,
                                     @RequestParam(value = "sortNum", required = false) Integer sortNum,
                                     @RequestParam("file") MultipartFile file) throws Exception {
        AiKnowledgeTemplateUploadResultVO result = aiKnowledgeTemplateService.uploadAndParseTemplate(
                knowledgeBaseId, sourceOrigin, appVersion, sortNum, file);
        return AjaxResult.success(message("ai.knowledge.template.upload.parse.success"), result);
    }
}
