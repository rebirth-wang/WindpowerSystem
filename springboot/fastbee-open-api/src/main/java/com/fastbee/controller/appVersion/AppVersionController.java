package com.fastbee.controller.appVersion;

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
import com.fastbee.common.extend.utils.poi.ExcelUtil;
import com.fastbee.iot.domain.AppVersion;
import com.fastbee.iot.model.vo.AppVersionVO;
import com.fastbee.iot.service.IAppVersionService;

/**
 * APP版本Controller
 *
 * @author fastbee
 * @date 2025-08-11
 */
@RestController
@RequestMapping("/iot/version")
@Api(tags = "APP版本")
public class AppVersionController extends BaseController {
    /** 代码生成区域 可直接覆盖**/
    @Resource
    private IAppVersionService appVersionService;

    /**
     * 查询APP版本列表
     */
    @PreAuthorize("@ss.hasPermi('iot:version:list')")
    @GetMapping("/list")
    @ApiOperation("查询APP版本列表")
    public TableDataInfo list(AppVersion appVersion) {
        Page<AppVersionVO> voPage = appVersionService.pageAppVersionVO(appVersion);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 导出APP版本列表
     */
    @ApiOperation("导出APP版本列表")
    @PreAuthorize("@ss.hasPermi('iot:version:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, AppVersion appVersion) {
        Page<AppVersionVO> voPage = appVersionService.pageAppVersionVO(appVersion);
        ExcelUtil<AppVersionVO> util = new ExcelUtil<AppVersionVO>(AppVersionVO.class);
        util.exportExcel(response, voPage.getRecords(), "APP版本数据");
    }

    /**
     * 获取APP版本详细信息
     */
    @PreAuthorize("@ss.hasPermi('iot:version:query')")
    @GetMapping(value = "/{id}")
    @ApiOperation("获取APP版本详细信息")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(appVersionService.queryByIdWithCache(id));
    }

    /**
     * 新增APP版本
     */
    @PreAuthorize("@ss.hasPermi('iot:version:add')")
    @PostMapping
    @ApiOperation("新增APP版本")
    public AjaxResult add(@RequestBody AppVersion appVersion) {
        try {
            Boolean result = appVersionService.insertWithCache(appVersion);
            return toAjax(result);
        } catch (RuntimeException e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 修改APP版本
     */
    @PreAuthorize("@ss.hasPermi('iot:version:edit')")
    @PutMapping
    @ApiOperation("修改APP版本")
    public AjaxResult edit(@RequestBody AppVersion appVersion) {
        return toAjax(appVersionService.updateWithCache(appVersion));
    }

    /**
     * 删除APP版本
     */
    @PreAuthorize("@ss.hasPermi('iot:version:remove')")
    @DeleteMapping("/{ids}")
    @ApiOperation("删除APP版本")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(appVersionService.deleteWithCacheByIds(ids, true));
    }

    /**
     * 查询最新版本APP信息
     */
    @ApiOperation("查询最新版本APP信息")
    @GetMapping("/latest")
    public AjaxResult getLatestAppVersion(AppVersion appVersion) {
        AppVersion latestVersion = appVersionService.selectLatestAppVersion(appVersion);
        if (latestVersion != null) {
            return AjaxResult.success(latestVersion);
        } else {
            return AjaxResult.error("未找到最新版本信息");
        }
    }

    /**
     * 查询最新版本APP信息
     */
    @ApiOperation("查询最新版本APP信息")
    @GetMapping("/apkLatest")
    public AjaxResult selectLatestApkVersion(AppVersion appVersion) {
        AppVersion latestVersion = appVersionService.selectLatestApkVersion(appVersion);
        if (latestVersion != null) {
            return AjaxResult.success(latestVersion);
        } else {
            return AjaxResult.error("未找到apk最新版本信息");
        }
    }

}
