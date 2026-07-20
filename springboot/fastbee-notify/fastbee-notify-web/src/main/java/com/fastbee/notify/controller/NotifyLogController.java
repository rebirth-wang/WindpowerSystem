package com.fastbee.notify.controller;

import java.util.Arrays;
import java.util.List;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.fastbee.common.annotation.Log;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.core.page.TableDataInfo;
import com.fastbee.common.enums.BusinessType;
import com.fastbee.common.extend.core.controller.BaseController;
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.common.extend.utils.poi.ExcelUtil;
import com.fastbee.notify.domain.NotifyLog;
import com.fastbee.notify.service.INotifyLogService;
import com.fastbee.notify.vo.NotifyLogVO;

/**
 * 通知日志Controller
 *
 * @author fastbee
 * @date 2023-12-16
 */
@Api(tags = "通知日志")
@RestController
@RequestMapping("/notify/log")
public class NotifyLogController extends BaseController
{
    @Resource
    private INotifyLogService notifyLogService;

    /**
     * 查询通知日志列表
     */
    @PreAuthorize("@ss.hasPermi('notify:log:list')")
    @ApiOperation(value = "查询通知日志列表")
    @GetMapping("/list")
    public TableDataInfo list(NotifyLog notifyLog)
    {
        Page<NotifyLogVO> voPage = notifyLogService.pageNotifyLogVO(notifyLog);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 导出通知日志列表
     */
    @PreAuthorize("@ss.hasPermi('notify:log:export')")
    @Log(title = "通知日志", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出通知日志列表")
    @PostMapping("/export")
    public void export(HttpServletResponse response, NotifyLog notifyLog)
    {
        Page<NotifyLogVO> voPage = notifyLogService.pageNotifyLogVO(notifyLog);
        ExcelUtil<NotifyLogVO> util = new ExcelUtil<NotifyLogVO>(NotifyLogVO.class);
        util.exportExcel(response, voPage.getRecords(), "通知日志数据");
    }

    /**
     * 获取通知日志详细信息
     */
    @PreAuthorize("@ss.hasPermi('notify:log:query')")
    @ApiOperation(value = "获取通知日志详细信息")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        NotifyLog notifyLog = new NotifyLog();
        notifyLog.setId(id);
        return success(notifyLogService.selectNotifyLogById(notifyLog));
    }

    /**
     * 新增通知日志
     */
    @PreAuthorize("@ss.hasPermi('notify:log:add')")
    @Log(title = "通知日志", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody NotifyLog notifyLog)
    {
        return toAjax(notifyLogService.insertNotifyLog(notifyLog));
    }

    /**
     * 修改通知日志
     */
    @PreAuthorize("@ss.hasPermi('notify:log:edit')")
    @Log(title = "通知日志", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody NotifyLog notifyLog)
    {
        return toAjax(notifyLogService.updateNotifyLog(notifyLog));
    }

    /**
     * 删除通知日志
     */
    @PreAuthorize("@ss.hasPermi('notify:log:remove')")
    @Log(title = "通知日志", businessType = BusinessType.DELETE)
    @ApiOperation(value = "批量删除通知日志")
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        List<NotifyLog> notifyLogList = notifyLogService.listByIds(Arrays.asList(ids));
        for (NotifyLog notifyLog : notifyLogList) {
            SecurityUtils.checkUserOperatePermission(notifyLog.getTenantId(), notifyLog.getCreateBy());
        }
        return toAjax(notifyLogService.deleteNotifyLogByIds(ids));
    }
}
