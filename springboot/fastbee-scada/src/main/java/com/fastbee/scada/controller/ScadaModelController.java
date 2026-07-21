package com.fastbee.scada.controller;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import jakarta.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.fastbee.common.annotation.Log;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.core.page.TableDataInfo;
import com.fastbee.common.enums.BusinessType;
import com.fastbee.common.extend.core.controller.BaseController;
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.common.extend.utils.poi.ExcelUtil;
import com.fastbee.scada.domain.ScadaModel;
import com.fastbee.scada.service.IScadaModelService;
import com.fastbee.scada.vo.ScadaModelVO;

/**
 * 模型管理Controller
 *
 * @author kerwincui
 * @date 2023-11-10
 */
@RestController
@RequestMapping("/scada/model")
public class ScadaModelController extends BaseController
{
    @Resource
    private IScadaModelService scadaModelService;

    /**
     * 查询模型管理列表
     */
    @PreAuthorize("@ss.hasPermi('scada:model:list')")
    @GetMapping("/list")
    public TableDataInfo list(ScadaModel scadaModel)
    {
        Page<ScadaModelVO> voPage = scadaModelService.pageScadaModelVO(scadaModel);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 导出模型管理列表
     */
    @PreAuthorize("@ss.hasPermi('scada:model:export')")
    @Log(title = "模型管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ScadaModel scadaModel)
    {
        Page<ScadaModelVO> voPage = scadaModelService.pageScadaModelVO(scadaModel);
        ExcelUtil<ScadaModelVO> util = new ExcelUtil<ScadaModelVO>(ScadaModelVO.class);
        util.exportExcel(response, voPage.getRecords(), "三维配置数据");
    }

    /**
     * 获取模型管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('scada:model:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        ScadaModel scadaModel = new ScadaModel();
        scadaModel.setId(id);
        return success(scadaModelService.selectScadaModelById(scadaModel));
    }

    /**
     * 新增模型管理
     */
    @PreAuthorize("@ss.hasPermi('scada:model:add')")
    @Log(title = "模型管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ScadaModel scadaModel)
    {
        return toAjax(scadaModelService.insertScadaModel(scadaModel));
    }

    /**
     * 修改模型管理
     */
    @PreAuthorize("@ss.hasPermi('scada:model:edit')")
    @Log(title = "模型管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ScadaModel scadaModel)
    {
        SecurityUtils.checkUserOperatePermission(scadaModel.getTenantId(), scadaModel.getCreateBy());
        return toAjax(scadaModelService.updateScadaModel(scadaModel));
    }

    /**
     * 删除模型管理
     */
    @PreAuthorize("@ss.hasPermi('scada:model:remove')")
    @Log(title = "模型管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        List<ScadaModel> scadaModelList = scadaModelService.listByIds(Arrays.asList(ids));
        for (ScadaModel scadaModel : scadaModelList) {
            SecurityUtils.checkUserOperatePermission(scadaModel.getTenantId(), scadaModel.getCreateBy());
        }
        return toAjax(scadaModelService.deleteScadaModelByIds(ids));
    }
}
