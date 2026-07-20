package com.fastbee.scada.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fastbee.common.annotation.Log;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.core.page.TableDataInfo;
import com.fastbee.common.enums.BusinessType;
import com.fastbee.common.extend.core.controller.BaseController;
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.common.extend.utils.poi.ExcelUtil;
import com.fastbee.scada.domain.ScadaEchart;
import com.fastbee.scada.service.IScadaEchartService;
import com.fastbee.scada.vo.ScadaEchartVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * 图表管理Controller
 *
 * @author kerwincui
 * @date 2023-11-10
 */
@Api(tags = "图表管理")
@RestController
@RequestMapping("/scada/echart")
public class ScadaEchartController extends BaseController
{
    @Resource
    private IScadaEchartService scadaEchartService;

    /**
     * 查询图表管理列表
     */
    @ApiOperation("查询图表管理列表")
    @PreAuthorize("@ss.hasPermi('scada:echart:list')")
    @GetMapping("/list")
    public TableDataInfo list(ScadaEchart scadaEchart)
    {
        Page<ScadaEchartVO> voPage = scadaEchartService.pageScadaEchartVO(scadaEchart);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 导出图表管理列表
     */
    @ApiOperation("导出图表管理列表")
    @PreAuthorize("@ss.hasPermi('scada:echart:export')")
    @Log(title = "图表管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ScadaEchart scadaEchart)
    {
        Page<ScadaEchartVO> voPage = scadaEchartService.pageScadaEchartVO(scadaEchart);
        ExcelUtil<ScadaEchartVO> util = new ExcelUtil<ScadaEchartVO>(ScadaEchartVO.class);
        util.exportExcel(response, voPage.getRecords(), "图管理数据");
    }

    /**
     * 获取图表管理详细信息
     */
    @ApiOperation("获取图表管理详细信息")
    @PreAuthorize("@ss.hasPermi('scada:echart:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        ScadaEchart scadaEchart = new ScadaEchart();
        scadaEchart.setId(id);
        return success(scadaEchartService.selectScadaEchartById(scadaEchart));
    }

    /**
     * 新增图表管理
     */
    @ApiOperation("新增图表")
    @PreAuthorize("@ss.hasPermi('scada:echart:add')")
    @Log(title = "图表管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ScadaEchartVO scadaEchartVO)
    {
        return toAjax(scadaEchartService.insertScadaEchart(scadaEchartVO));
    }

    /**
     * 修改图表管理
     */
    @ApiOperation("修改图表")
    @PreAuthorize("@ss.hasPermi('scada:echart:edit')")
    @Log(title = "图表管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ScadaEchartVO scadaEchartVO)
    {
        SecurityUtils.checkUserOperatePermission(scadaEchartVO.getTenantId(), scadaEchartVO.getCreateBy());
        return toAjax(scadaEchartService.updateScadaEchart(scadaEchartVO));
    }

    /**
     * 删除图表管理
     */
    @ApiOperation("删除图表")
    @PreAuthorize("@ss.hasPermi('scada:echart:remove')")
    @Log(title = "图表管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        List<ScadaEchart> scadaEchartList = scadaEchartService.listByIds(Arrays.asList(ids));
        for (ScadaEchart scadaEchart : scadaEchartList) {
            SecurityUtils.checkUserOperatePermission(scadaEchart.getTenantId(), scadaEchart.getCreateBy());
        }
        return toAjax(scadaEchartService.deleteScadaEchartByIds(ids));
    }
}
