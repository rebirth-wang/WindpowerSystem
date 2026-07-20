package com.fastbee.controller.datacenter;

import java.util.Arrays;
import java.util.List;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;

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

import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.core.page.TableDataInfo;
import com.fastbee.common.extend.core.controller.BaseController;
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.common.extend.utils.poi.ExcelUtil;
import com.fastbee.iot.domain.Report;
import com.fastbee.iot.model.vo.ReportVO;
import com.fastbee.iot.service.IReportService;

/**
 * 报表管理Controller
 *
 * @author zzy
 * @date 2025-07-09
 */
@RestController
@RequestMapping("/data/center/report")
@Api(tags = "报表管理")
public class ReportController extends BaseController {

    @Resource
    private IReportService reportService;

    /**
     * 查询报表管理列表
     */
    @PreAuthorize("@ss.hasPermi('dataCenter:report:list')")
    @GetMapping("/list")
    @ApiOperation("查询报表管理列表")
    public TableDataInfo list(Report report) {
        Page<ReportVO> voPage = reportService.pageReportVO(report);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 导出报表管理列表
     */
    @ApiOperation("导出报表管理列表")
    @PreAuthorize("@ss.hasPermi('dataCenter:report:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, Report report) {
        Page<ReportVO> voPage = reportService.pageReportVO(report);
        ExcelUtil<ReportVO> util = new ExcelUtil<ReportVO>(ReportVO.class);
        util.exportExcel(response, voPage.getRecords(), "报表管理数据");
    }

    /**
     * 获取报表管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('dataCenter:report:query')")
    @GetMapping(value = "/{id}")
    @ApiOperation("获取报表管理详细信息")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        Report report = new Report();
        report.setId(id);
        return success(reportService.queryByIdWithCache(report));
    }

    /**
     * 新增报表管理
     */
    @PreAuthorize("@ss.hasPermi('dataCenter:report:add')")
    @PostMapping
    @ApiOperation("新增报表管理")
    public AjaxResult add(@RequestBody ReportVO reportVO) {
        return toAjax(reportService.insertWithCache(reportVO));
    }

    /**
     * 修改报表管理
     */
    @PreAuthorize("@ss.hasPermi('dataCenter:report:edit')")
    @PutMapping
    @ApiOperation("修改报表管理")
    public AjaxResult edit(@RequestBody ReportVO reportVO) {
        SecurityUtils.checkUserOperatePermission(reportVO.getTenantId(), reportVO.getCreateBy());
        return toAjax(reportService.updateWithCache(reportVO));
    }

    /**
     * 删除报表管理
     */
    @PreAuthorize("@ss.hasPermi('dataCenter:report:remove')")
    @DeleteMapping("/{ids}")
    @ApiOperation("删除报表管理")
    public AjaxResult remove(@PathVariable Long[] ids) {
        List<Report> reportList = reportService.listByIds(Arrays.asList(ids));
        for (Report report : reportList) {
            SecurityUtils.checkUserOperatePermission(report.getTenantId(), report.getCreateBy());
        }
        return toAjax(reportService.deleteWithCacheByIds(ids, true));
    }

}
