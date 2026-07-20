package com.fastbee.controller.datacenter;

import jakarta.annotation.Resource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.core.page.TableDataInfo;
import com.fastbee.common.extend.core.controller.BaseController;
import com.fastbee.iot.model.vo.ReportRecordsVO;
import com.fastbee.iot.service.IReportRecordsService;

/**
 * 报表记录Controller
 *
 * @author zzy
 * @date 2025-07-09
 */
@RestController
@RequestMapping("/data/center/report/records")
@Api(tags = "报表记录")
public class ReportRecordsController extends BaseController {
    /** 代码生成区域 可直接覆盖**/
    @Resource
    private IReportRecordsService reportRecordsService;

    /**
     * 查询报表记录列表
     */
    @PreAuthorize("@ss.hasPermi('dataCenter:reportRecords:list')")
    @GetMapping("/list")
    @ApiOperation("查询报表记录列表")
    public TableDataInfo list(ReportRecordsVO reportRecordsVO) {
        Page<ReportRecordsVO> voPage = reportRecordsService.pageReportRecordsVO(reportRecordsVO);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 删除报表记录
     */
    @PreAuthorize("@ss.hasPermi('dataCenter:records:remove')")
    @DeleteMapping("/{ids}")
    @ApiOperation("删除报表记录")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(reportRecordsService.deleteWithCacheByIds(ids, true));
    }

}
