package com.fastbee.ai.controller;

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

import com.fastbee.ai.domain.AiKnowledgeBase;
import com.fastbee.ai.model.vo.AiKnowledgeBaseVO;
import com.fastbee.ai.service.IAiKnowledgeBaseService;
import com.fastbee.common.annotation.Log;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.core.page.TableDataInfo;
import com.fastbee.common.enums.BusinessType;
import com.fastbee.common.extend.core.controller.BaseController;

/**
 * AI 知识库管理控制器。
 */
@Api(tags = "AI 知识库管理")
@RestController
@RequestMapping("/ai/knowledge")
public class AiKnowledgeController extends BaseController {

    @Resource
    private IAiKnowledgeBaseService aiKnowledgeBaseService;

    /**
     * 查询 AI 知识库列表。
     *
     * @param aiKnowledgeBase 查询条件
     * @return 列表结果
     */
    @ApiOperation("查询 AI 知识库列表")
    @PreAuthorize("@ss.hasPermi('ai:knowledge:list')")
    @GetMapping("/list")
    public TableDataInfo list(AiKnowledgeBase aiKnowledgeBase) {
        Page<AiKnowledgeBaseVO> page = aiKnowledgeBaseService.pageAiKnowledgeBaseVO(aiKnowledgeBase);
        return getDataTable(page.getRecords(), page.getTotal());
    }

    /**
     * 查询 AI 知识库详情。
     *
     * @param knowledgeBaseId 知识库 ID
     * @return 详情
     */
    @ApiOperation("查询 AI 知识库详情")
    @PreAuthorize("@ss.hasPermi('ai:knowledge:query')")
    @GetMapping("/{knowledgeBaseId}")
    public AjaxResult getInfo(@PathVariable("knowledgeBaseId") Long knowledgeBaseId) {
        AiKnowledgeBase aiKnowledgeBase = new AiKnowledgeBase();
        aiKnowledgeBase.setKnowledgeBaseId(knowledgeBaseId);
        return success(aiKnowledgeBaseService.selectAiKnowledgeBaseVO(aiKnowledgeBase));
    }

    /**
     * 新增 AI 知识库。
     *
     * @param aiKnowledgeBase 知识库信息
     * @return 操作结果
     */
    @ApiOperation("新增 AI 知识库")
    @PreAuthorize("@ss.hasPermi('ai:knowledge:add')")
    @Log(title = "AI 知识库", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AiKnowledgeBase aiKnowledgeBase) {
        return toAjax(aiKnowledgeBaseService.insertAiKnowledgeBase(aiKnowledgeBase));
    }

    /**
     * 修改 AI 知识库。
     *
     * @param aiKnowledgeBase 知识库信息
     * @return 操作结果
     */
    @ApiOperation("修改 AI 知识库")
    @PreAuthorize("@ss.hasPermi('ai:knowledge:edit')")
    @Log(title = "AI 知识库", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AiKnowledgeBase aiKnowledgeBase) {
        return toAjax(aiKnowledgeBaseService.updateAiKnowledgeBase(aiKnowledgeBase));
    }

    /**
     * 修改 AI 知识库状态。
     *
     * @param aiKnowledgeBase 知识库信息
     * @return 操作结果
     */
    @ApiOperation("修改 AI 知识库状态")
    @PreAuthorize("@ss.hasPermi('ai:knowledge:edit')")
    @Log(title = "AI 知识库", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody AiKnowledgeBase aiKnowledgeBase) {
        return toAjax(aiKnowledgeBaseService.updateAiKnowledgeBaseStatus(aiKnowledgeBase));
    }

    /**
     * 删除 AI 知识库。
     *
     * @param knowledgeBaseIds 知识库 ID 集合
     * @return 操作结果
     */
    @ApiOperation("删除 AI 知识库")
    @PreAuthorize("@ss.hasPermi('ai:knowledge:remove')")
    @Log(title = "AI 知识库", businessType = BusinessType.DELETE)
    @DeleteMapping("/{knowledgeBaseIds}")
    public AjaxResult remove(@PathVariable Long[] knowledgeBaseIds) {
        return toAjax(aiKnowledgeBaseService.deleteAiKnowledgeBaseByIds(knowledgeBaseIds));
    }
}
