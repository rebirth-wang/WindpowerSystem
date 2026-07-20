package com.fastbee.ai.controller;

import static com.fastbee.ai.support.AiI18nMessageUtils.message;

import jakarta.annotation.Resource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fastbee.ai.domain.AiKnowledgeVersion;
import com.fastbee.ai.model.vo.AiKnowledgeVersionQualityCheckVO;
import com.fastbee.ai.model.vo.AiKnowledgeVersionVO;
import com.fastbee.ai.service.IAiKnowledgeVersionService;
import com.fastbee.ai.support.AiSecuritySupport;
import com.fastbee.common.annotation.Log;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.core.page.TableDataInfo;
import com.fastbee.common.enums.BusinessType;
import com.fastbee.common.extend.core.controller.BaseController;

/**
 * AI 知识库版本控制器。
 */
@Api(tags = "AI 知识库版本")
@RestController
@RequestMapping("/ai/knowledge/version")
public class AiKnowledgeVersionController extends BaseController {

    @Resource
    private IAiKnowledgeVersionService aiKnowledgeVersionService;

    /**
     * 查询知识库版本列表。
     *
     * @param aiKnowledgeVersion 查询条件
     * @return 分页结果
     */
    @ApiOperation("查询知识库版本列表")
    @PreAuthorize("@ss.hasPermi('ai:knowledge:query')")
    @GetMapping("/list")
    public TableDataInfo list(AiKnowledgeVersion aiKnowledgeVersion) {
        Page<AiKnowledgeVersionVO> page = aiKnowledgeVersionService.pageAiKnowledgeVersionVO(aiKnowledgeVersion);
        return getDataTable(page.getRecords(), page.getTotal());
    }

    /**
     * 获取知识库版本详情。
     *
     * @param versionId 版本 ID
     * @return 版本详情
     */
    @ApiOperation("获取知识库版本详情")
    @PreAuthorize("@ss.hasPermi('ai:knowledge:query')")
    @GetMapping("/{versionId}")
    public AjaxResult getInfo(@PathVariable("versionId") Long versionId) {
        return success(aiKnowledgeVersionService.selectAiKnowledgeVersionVO(versionId));
    }

    /**
     * 新增知识库版本。
     *
     * @param aiKnowledgeVersion 版本信息
     * @return 操作结果
     */
    @ApiOperation("新增知识库版本")
    @PreAuthorize("@ss.hasPermi('ai:knowledge:add')")
    @Log(title = "AI 知识库版本", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AiKnowledgeVersion aiKnowledgeVersion) {
        AiSecuritySupport.requireAdminAccount("新增知识库版本");
        return toAjax(aiKnowledgeVersionService.insertAiKnowledgeVersion(aiKnowledgeVersion));
    }

    /**
     * 修改知识库版本。
     *
     * @param aiKnowledgeVersion 版本信息
     * @return 操作结果
     */
    @ApiOperation("修改知识库版本")
    @PreAuthorize("@ss.hasPermi('ai:knowledge:edit')")
    @Log(title = "AI 知识库版本", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AiKnowledgeVersion aiKnowledgeVersion) {
        AiSecuritySupport.requireAdminAccount("修改知识库版本");
        return toAjax(aiKnowledgeVersionService.updateAiKnowledgeVersion(aiKnowledgeVersion));
    }

    /**
     * 根据当前知识库文档构建草稿版本。
     *
     * @param knowledgeBaseId 知识库 ID
     * @return 构建结果
     */
    @ApiOperation("构建知识库草稿版本")
    @PreAuthorize("@ss.hasPermi('ai:knowledge:edit')")
    @Log(title = "AI 知识库版本", businessType = BusinessType.INSERT)
    @PostMapping("/build/{knowledgeBaseId}")
    public AjaxResult buildDraft(@PathVariable("knowledgeBaseId") Long knowledgeBaseId) {
        AiSecuritySupport.requireAdminAccount("构建知识库版本");
        AiKnowledgeVersion version = aiKnowledgeVersionService.buildDraftAiKnowledgeVersion(knowledgeBaseId);
        return AjaxResult.success(message("ai.knowledge.version.draft.build.success"),
                aiKnowledgeVersionService.selectAiKnowledgeVersionVO(version.getVersionId()));
    }

    /**
     * 预检知识库版本质量。
     *
     * @param versionId 版本 ID
     * @return 预检结果
     */
    @ApiOperation("预检知识库版本质量")
    @PreAuthorize("@ss.hasPermi('ai:knowledge:query')")
    @GetMapping("/quality/{versionId}")
    public AjaxResult previewQuality(@PathVariable("versionId") Long versionId) {
        AiSecuritySupport.requireAdminAccount("预检知识库版本质量");
        AiKnowledgeVersionQualityCheckVO result = aiKnowledgeVersionService.previewAiKnowledgeVersionQuality(versionId);
        return AjaxResult.success(message("ai.knowledge.version.quality.preview.success"), result);
    }

    /**
     * 发布知识库版本。
     *
     * @param versionId 目标版本 ID
     * @return 操作结果
     */
    @ApiOperation("发布知识库版本")
    @PreAuthorize("@ss.hasPermi('ai:knowledge:edit')")
    @Log(title = "AI 知识库版本", businessType = BusinessType.UPDATE)
    @PutMapping("/publish/{versionId}")
    public AjaxResult publish(@PathVariable("versionId") Long versionId) {
        AiSecuritySupport.requireAdminAccount("发布知识库版本");
        return toAjax(aiKnowledgeVersionService.publishAiKnowledgeVersion(versionId));
    }

    /**
     * 回滚到指定知识库版本。
     *
     * @param versionId 目标版本 ID
     * @return 操作结果
     */
    @ApiOperation("回滚到指定知识库版本")
    @PreAuthorize("@ss.hasPermi('ai:knowledge:edit')")
    @Log(title = "AI 知识库版本", businessType = BusinessType.UPDATE)
    @PutMapping("/rollback/{versionId}")
    public AjaxResult rollback(@PathVariable("versionId") Long versionId) {
        AiSecuritySupport.requireAdminAccount("回滚知识库版本");
        return toAjax(aiKnowledgeVersionService.rollbackAiKnowledgeVersion(versionId));
    }

    /**
     * 删除知识库版本。
     *
     * @param versionIds 版本 ID 集合
     * @return 操作结果
     */
    @ApiOperation("删除知识库版本")
    @PreAuthorize("@ss.hasPermi('ai:knowledge:remove')")
    @Log(title = "AI 知识库版本", businessType = BusinessType.DELETE)
    @DeleteMapping("/{versionIds}")
    public AjaxResult remove(@PathVariable Long[] versionIds) {
        AiSecuritySupport.requireAdminAccount("删除知识库版本");
        return toAjax(aiKnowledgeVersionService.deleteAiKnowledgeVersionByIds(versionIds));
    }
}
