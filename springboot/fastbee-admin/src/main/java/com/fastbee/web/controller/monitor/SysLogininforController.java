package com.fastbee.web.controller.monitor;

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
import com.fastbee.common.extend.utils.poi.ExcelUtil;
import com.fastbee.system.domain.SysLogininfor;
import com.fastbee.system.domain.vo.SysLogininforVO;
import com.fastbee.system.service.ISysLogininforService;
import com.fastbee.system.service.sys.SysPasswordService;

/**
 * 系统访问记录
 *
 * @author ruoyi
 */
@Api(tags = "日志管理：登录日志")
@RestController
@RequestMapping("/monitor/logininfor")
public class SysLogininforController extends BaseController
{
    @Resource
    private ISysLogininforService logininforService;

    @Resource
    private SysPasswordService passwordService;

    @ApiOperation("获取列表登录信息")
    @PreAuthorize("@ss.hasPermi('monitor:logininfor:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysLogininfor logininfor)
    {
        Page<SysLogininforVO> voPage = logininforService.pageSysLogininforVO(logininfor);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    @ApiOperation("导出登录日志列表")
    @Log(title = "登录日志", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('monitor:logininfor:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysLogininfor logininfor)
    {
        Page<SysLogininforVO> voPage = logininforService.pageSysLogininforVO(logininfor);
        ExcelUtil<SysLogininforVO> util = new ExcelUtil<SysLogininforVO>(SysLogininforVO.class);
        util.exportExcel(response, voPage.getRecords(), "系统访问记录数据");
    }

    @ApiOperation("批量删除登录日志")
    @PreAuthorize("@ss.hasPermi('monitor:logininfor:remove')")
    @Log(title = "登录日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{infoIds}")
    public AjaxResult remove(@PathVariable Long[] infoIds)
    {
        return toAjax(logininforService.deleteLogininforByIds(infoIds));
    }

    @ApiOperation("清空登录日志信息")
    @PreAuthorize("@ss.hasPermi('monitor:logininfor:remove')")
    @Log(title = "登录日志", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clean")
    public AjaxResult clean()
    {
        logininforService.cleanLogininfor();
        return success();
    }

    @ApiOperation("账户解锁")
    @PreAuthorize("@ss.hasPermi('monitor:logininfor:unlock')")
    @Log(title = "账户解锁", businessType = BusinessType.OTHER)
    @GetMapping("/unlock/{userName}")
    public AjaxResult unlock(@PathVariable("userName") String userName)
    {
        passwordService.clearLoginRecordCache(userName);
        return success();
    }

    @ApiOperation("获取一周内每天用户统计")
    @PreAuthorize("@ss.hasPermi('monitor:logininfor:list')")
    @GetMapping("/userCount")
    public AjaxResult userCount()
    {
        return toAjax(logininforService.getUserCount());
    }
}
