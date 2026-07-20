package com.fastbee.ai.controller;

import static com.fastbee.ai.support.AiI18nMessageUtils.message;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fastbee.ai.domain.AiKnowledgeDocument;
import com.fastbee.ai.model.vo.AiKnowledgeDocumentParseResultVO;
import com.fastbee.ai.model.vo.AiKnowledgeDocumentVO;
import com.fastbee.ai.service.IAiKnowledgeDocumentService;
import com.fastbee.ai.service.IAiKnowledgeTemplateService;
import com.fastbee.ai.support.AiSecuritySupport;
import com.fastbee.common.annotation.Log;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.core.page.TableDataInfo;
import com.fastbee.common.enums.BusinessType;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.core.controller.BaseController;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.common.utils.file.FileUtils;

/**
 * AI 知识文档控制器。
 */
@Api(tags = "AI 知识文档管理")
@RestController
@RequestMapping("/ai/knowledge/document")
public class AiKnowledgeDocumentController extends BaseController {

    @Resource
    private IAiKnowledgeDocumentService aiKnowledgeDocumentService;

    @Resource
    private IAiKnowledgeTemplateService aiKnowledgeTemplateService;

    /**
     * 查询知识文档列表。
     *
     * @param aiKnowledgeDocument 查询条件
     * @return 分页结果
     */
    @ApiOperation("查询知识文档列表")
    @PreAuthorize("@ss.hasPermi('ai:knowledge:query')")
    @GetMapping("/list")
    public TableDataInfo list(AiKnowledgeDocument aiKnowledgeDocument) {
        Page<AiKnowledgeDocumentVO> page = aiKnowledgeDocumentService.pageAiKnowledgeDocumentVO(aiKnowledgeDocument);
        return getDataTable(page.getRecords(), page.getTotal());
    }

    /**
     * 查询知识文档详情。
     *
     * @param documentId 文档 ID
     * @return 文档详情
     */
    @ApiOperation("查询知识文档详情")
    @PreAuthorize("@ss.hasPermi('ai:knowledge:query')")
    @GetMapping("/{documentId}")
    public AjaxResult getInfo(@PathVariable("documentId") Long documentId) {
        return success(aiKnowledgeDocumentService.selectAiKnowledgeDocumentVO(documentId));
    }

    /**
     * 修改知识文档。
     *
     * @param aiKnowledgeDocument 文档信息
     * @return 操作结果
     */
    @ApiOperation("修改知识文档")
    @PreAuthorize("@ss.hasPermi('ai:knowledge:edit')")
    @Log(title = "AI 知识文档", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AiKnowledgeDocument aiKnowledgeDocument) {
        return toAjax(aiKnowledgeDocumentService.updateAiKnowledgeDocument(aiKnowledgeDocument));
    }

    /**
     * 修改知识文档状态。
     *
     * @param aiKnowledgeDocument 文档信息
     * @return 操作结果
     */
    @ApiOperation("修改知识文档状态")
    @PreAuthorize("@ss.hasPermi('ai:knowledge:edit')")
    @Log(title = "AI 知识文档", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody AiKnowledgeDocument aiKnowledgeDocument) {
        return toAjax(aiKnowledgeDocumentService.updateAiKnowledgeDocumentStatus(aiKnowledgeDocument));
    }

    /**
     * 重新解析知识文档。
     *
     * @param documentId 文档 ID
     * @return 解析结果
     */
    @ApiOperation("重新解析知识文档")
    @PreAuthorize("@ss.hasPermi('ai:knowledge:edit')")
    @Log(title = "AI 知识文档", businessType = BusinessType.UPDATE)
    @PostMapping("/reparse/{documentId}")
    public AjaxResult reparse(@PathVariable("documentId") Long documentId) {
        AiKnowledgeDocumentParseResultVO result = aiKnowledgeTemplateService.reparseDocument(documentId);
        return AjaxResult.success(message("ai.knowledge.document.reparse.success"), result);
    }

    /**
     * 查询知识文档结构化快照预览。
     *
     * @param documentId 文档 ID
     * @param previewSize 预览条数
     * @return 快照预览
     */
    @ApiOperation("查询知识文档结构化快照预览")
    @PreAuthorize("@ss.hasPermi('ai:knowledge:query')")
    @GetMapping("/{documentId}/snapshot")
    public AjaxResult snapshot(@PathVariable("documentId") Long documentId,
                               @RequestParam(value = "previewSize", required = false) Integer previewSize) {
        AiSecuritySupport.requireAdminAccount("查看知识文档快照");
        return AjaxResult.success(aiKnowledgeTemplateService.loadDocumentSnapshot(documentId, previewSize));
    }

    /**
     * 删除知识文档。
     *
     * @param documentIds 文档 ID 集合
     * @return 操作结果
     */
    @ApiOperation("删除知识文档")
    @PreAuthorize("@ss.hasPermi('ai:knowledge:remove')")
    @Log(title = "AI 知识文档", businessType = BusinessType.DELETE)
    @DeleteMapping("/{documentIds}")
    public AjaxResult remove(@PathVariable Long[] documentIds) {
        return toAjax(aiKnowledgeDocumentService.deleteAiKnowledgeDocumentByIds(documentIds));
    }

    /**
     * 下载知识文档原文件。
     *
     * @param documentId 文档 ID
     * @param response 响应对象
     */
    @ApiOperation("下载知识文档原文件")
    @PreAuthorize("@ss.hasPermi('ai:knowledge:query')")
    @PostMapping("/download")
    public void download(@RequestParam("documentId") Long documentId, HttpServletResponse response) {
        AiSecuritySupport.requireAdminAccount("下载知识文档");
        AiKnowledgeDocument document = aiKnowledgeDocumentService.selectAiKnowledgeDocument(documentId);
        if (document == null) {
            throw new ServiceException(message("ai.knowledge.document.not.exists.or.deleted"));
        }
        if (StringUtils.isBlank(document.getFilePath())) {
            throw new ServiceException(message("ai.knowledge.document.file.path.required"));
        }
        if (!Files.exists(Paths.get(document.getFilePath()))) {
            throw new ServiceException(message("ai.knowledge.document.file.not.exists", document.getFilePath()));
        }
        try {
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            FileUtils.setAttachmentResponseHeader(response, document.getFileName());
            FileUtils.writeBytes(document.getFilePath(), response.getOutputStream());
        } catch (IOException ex) {
            throw new ServiceException(message("ai.knowledge.document.download.failed", ex.getMessage()));
        } catch (Exception ex) {
            throw new ServiceException(message("ai.knowledge.document.download.failed", ex.getMessage()));
        }
    }
}
