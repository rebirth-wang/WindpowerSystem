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

import com.fastbee.ai.domain.AiProvider;
import com.fastbee.ai.model.vo.AiProviderVO;
import com.fastbee.ai.service.IAiProviderService;
import com.fastbee.common.annotation.Log;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.core.page.TableDataInfo;
import com.fastbee.common.enums.BusinessType;
import com.fastbee.common.extend.core.controller.BaseController;
import com.fastbee.common.extend.utils.SecurityUtils;

/**
 * AI 厂商管理控制器。
 */
@Api(tags = "AI 厂商管理")
@RestController
@RequestMapping("/ai/provider")
public class AiProviderController extends BaseController {

    @Resource
    private IAiProviderService aiProviderService;

    /**
     * 查询 AI 厂商列表。
     *
     * @param aiProvider 查询条件
     * @return 列表结果
     */
    @ApiOperation("查询 AI 厂商列表")
    @PreAuthorize("@ss.hasPermi('ai:provider:list')")
    @GetMapping("/list")
    public TableDataInfo list(AiProvider aiProvider) {
        Page<AiProviderVO> page = aiProviderService.pageAiProviderVO(aiProvider);
        return getDataTable(page.getRecords(), page.getTotal());
    }

    /**
     * 查询已启用厂商列表。
     *
     * @return 厂商列表
     */
    @ApiOperation("查询已启用厂商列表")
    @PreAuthorize("@ss.hasPermi('ai:provider:list')")
    @GetMapping("/enabled")
    public AjaxResult enabledList() {
        return AjaxResult.success(aiProviderService.listEnabledProviderVOs());
    }

    /**
     * 查询 AI 厂商详情。
     *
     * @param providerId 厂商 ID
     * @return 详情
     */
    @ApiOperation("查询 AI 厂商详情")
    @PreAuthorize("@ss.hasPermi('ai:provider:query')")
    @GetMapping("/{providerId}")
    public AjaxResult getInfo(@PathVariable("providerId") Long providerId) {
        AiProvider aiProvider = new AiProvider();
        aiProvider.setProviderId(providerId);
        return success(aiProviderService.selectAiProviderVO(aiProvider));
    }

    /**
     * 新增 AI 厂商。
     *
     * @param aiProvider 厂商信息
     * @return 操作结果
     */
    @ApiOperation("新增 AI 厂商")
    @PreAuthorize("@ss.hasPermi('ai:provider:add')")
    @Log(title = "AI 厂商", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AiProvider aiProvider) {
        return toAjax(aiProviderService.insertAiProvider(aiProvider));
    }

    /**
     * 修改 AI 厂商。
     *
     * @param aiProvider 厂商信息
     * @return 操作结果
     */
    @ApiOperation("修改 AI 厂商")
    @PreAuthorize("@ss.hasPermi('ai:provider:edit')")
    @Log(title = "AI 厂商", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AiProvider aiProvider) {
        AiProvider old = aiProviderService.getById(aiProvider.getProviderId());
        if (old != null) {
            SecurityUtils.checkUserOperatePermission(old.getTenantId(), old.getCreateBy());
        }
        return toAjax(aiProviderService.updateAiProvider(aiProvider));
    }

    /**
     * 修改 AI 厂商状态。
     *
     * @param aiProvider 厂商信息
     * @return 操作结果
     */
    @ApiOperation("修改 AI 厂商状态")
    @PreAuthorize("@ss.hasPermi('ai:provider:edit')")
    @Log(title = "AI 厂商", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody AiProvider aiProvider) {
        AiProvider old = aiProviderService.getById(aiProvider.getProviderId());
        if (old != null) {
            SecurityUtils.checkUserOperatePermission(old.getTenantId(), old.getCreateBy());
        }
        return toAjax(aiProviderService.updateAiProviderStatus(aiProvider));
    }

    /**
     * 删除 AI 厂商。
     *
     * @param providerIds 厂商 ID 集合
     * @return 操作结果
     */
    @ApiOperation("删除 AI 厂商")
    @PreAuthorize("@ss.hasPermi('ai:provider:remove')")
    @Log(title = "AI 厂商", businessType = BusinessType.DELETE)
    @DeleteMapping("/{providerIds}")
    public AjaxResult remove(@PathVariable Long[] providerIds) {
        List<AiProvider> providerList = aiProviderService.listByIds(Arrays.asList(providerIds));
        for (AiProvider provider : providerList) {
            SecurityUtils.checkUserOperatePermission(provider.getTenantId(), provider.getCreateBy());
        }
        return toAjax(aiProviderService.deleteAiProviderByIds(providerIds));
    }
}
