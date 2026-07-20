package com.fastbee.ai.controller;

import java.util.Arrays;
import java.util.List;

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

import com.fastbee.ai.domain.AiModel;
import com.fastbee.ai.model.vo.AiModelVO;
import com.fastbee.ai.service.AiRuntimeModelSnapshotService;
import com.fastbee.ai.service.IAiModelService;
import com.fastbee.common.annotation.Log;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.core.page.TableDataInfo;
import com.fastbee.common.enums.BusinessType;
import com.fastbee.common.extend.core.controller.BaseController;
import com.fastbee.common.extend.utils.SecurityUtils;

/**
 * AI 模型管理控制器。
 */
@Api(tags = "AI 模型管理")
@RestController
@RequestMapping("/ai/model")
public class AiModelController extends BaseController {

    @Resource
    private IAiModelService aiModelService;

    @Resource
    private AiRuntimeModelSnapshotService aiRuntimeModelSnapshotService;

    /**
     * 查询 AI 模型列表。
     *
     * @param aiModel 查询条件
     * @return 列表结果
     */
    @ApiOperation("查询 AI 模型列表")
    @PreAuthorize("@ss.hasPermi('ai:model:list')")
    @GetMapping("/list")
    public TableDataInfo list(AiModel aiModel) {
        Page<AiModelVO> page = aiModelService.pageAiModelVO(aiModel);
        return getDataTable(page.getRecords(), page.getTotal());
    }

    /**
     * 查询聊天页模型分组选项。
     *
     * @return 分组选项
     */
    @ApiOperation("查询聊天页模型分组选项")
    @PreAuthorize("@ss.hasPermi('ai:chat:list')")
    @GetMapping("/grouped")
    public AjaxResult grouped() {
        return AjaxResult.success(aiModelService.listGroupedOptions());
    }

    /**
     * 查询 AI 模型详情。
     *
     * @param modelId 模型 ID
     * @return 详情
     */
    @ApiOperation("查询 AI 模型详情")
    @PreAuthorize("@ss.hasPermi('ai:model:query')")
    @GetMapping("/{modelId}")
    public AjaxResult getInfo(@PathVariable("modelId") Long modelId) {
        AiModel aiModel = new AiModel();
        aiModel.setModelId(modelId);
        return success(aiModelService.selectAiModelVO(aiModel));
    }

    /**
     * 查询运行时模型快照缓存统计。
     *
     * @return 缓存统计
     */
    @ApiOperation("查询运行时模型快照缓存统计")
    @PreAuthorize("@ss.hasPermi('ai:model:query')")
    @GetMapping("/cacheStats")
    public AjaxResult cacheStats() {
        return success(aiRuntimeModelSnapshotService.getSnapshotCacheStats());
    }

    /**
     * 新增 AI 模型。
     *
     * @param aiModel 模型信息
     * @return 操作结果
     */
    @ApiOperation("新增 AI 模型")
    @PreAuthorize("@ss.hasPermi('ai:model:add')")
    @Log(title = "AI 模型", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AiModel aiModel) {
        return toAjax(aiModelService.insertAiModel(aiModel));
    }

    /**
     * 修改 AI 模型。
     *
     * @param aiModel 模型信息
     * @return 操作结果
     */
    @ApiOperation("修改 AI 模型")
    @PreAuthorize("@ss.hasPermi('ai:model:edit')")
    @Log(title = "AI 模型", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AiModel aiModel) {
        AiModel old = aiModelService.getById(aiModel.getModelId());
        if (old != null) {
            SecurityUtils.checkUserOperatePermission(old.getTenantId(), old.getCreateBy());
        }
        return toAjax(aiModelService.updateAiModel(aiModel));
    }

    /**
     * 修改 AI 模型状态。
     *
     * @param aiModel 模型信息
     * @return 操作结果
     */
    @ApiOperation("修改 AI 模型状态")
    @PreAuthorize("@ss.hasPermi('ai:model:edit')")
    @Log(title = "AI 模型", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody AiModel aiModel) {
        AiModel old = aiModelService.getById(aiModel.getModelId());
        if (old != null) {
            SecurityUtils.checkUserOperatePermission(old.getTenantId(), old.getCreateBy());
        }
        return toAjax(aiModelService.updateAiModelStatus(aiModel));
    }

    /**
     * 删除 AI 模型。
     *
     * @param modelIds 模型 ID 集合
     * @return 操作结果
     */
    @ApiOperation("删除 AI 模型")
    @PreAuthorize("@ss.hasPermi('ai:model:remove')")
    @Log(title = "AI 模型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{modelIds}")
    public AjaxResult remove(@PathVariable Long[] modelIds) {
        List<AiModel> modelList = aiModelService.listByIds(Arrays.asList(modelIds));
        for (AiModel model : modelList) {
            SecurityUtils.checkUserOperatePermission(model.getTenantId(), model.getCreateBy());
        }
        return toAjax(aiModelService.deleteAiModelByIds(modelIds));
    }
}
