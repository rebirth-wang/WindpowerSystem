package com.fastbee.controller.deviceConfig;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.core.page.TableDataInfo;
import com.fastbee.common.extend.core.controller.BaseController;
import com.fastbee.common.extend.utils.poi.ExcelUtil;
import com.fastbee.iot.domain.CommandPreferences;
import com.fastbee.iot.model.vo.CommandPreferencesVO;
import com.fastbee.iot.service.ICommandPreferencesService;

/**
 * 指令偏好设置Controller
 *
 * @author kerwincui
 * @date 2024-06-29
 */
@RestController
@RequestMapping("/iot/preferences")
@Api(tags = "指令偏好设置")
public class CommandPreferencesController extends BaseController
{
    @Resource
    private ICommandPreferencesService commandPreferencesService;

    /**
     * 查询指令偏好设置列表
     */
    @PreAuthorize("@ss.hasPermi('order:preferences:list')")
    @GetMapping("/list")
    @ApiOperation("查询指令偏好设置列表")
    public TableDataInfo list(CommandPreferences commandPreferences)
    {
        Page<CommandPreferencesVO> voPage = commandPreferencesService.pageCommandPreferencesVO(commandPreferences);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 导出指令偏好设置列表
     */
    @ApiOperation("导出指令偏好设置列表")
    @PreAuthorize("@ss.hasPermi('order:preferences:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, CommandPreferences commandPreferences)
    {
        Page<CommandPreferencesVO> voPage = commandPreferencesService.pageCommandPreferencesVO(commandPreferences);
        ExcelUtil<CommandPreferencesVO> util = new ExcelUtil<CommandPreferencesVO>(CommandPreferencesVO.class);
        util.exportExcel(response, voPage.getRecords(), "指令偏好设置数据");
    }

    /**
     * 获取指令偏好设置详细信息
     */
    @PreAuthorize("@ss.hasPermi('order:preferences:query')")
    @GetMapping(value = "/{id}")
    @ApiOperation("获取指令偏好设置详细信息")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(commandPreferencesService.queryByIdWithCache(id));
    }

    /**
     * 新增指令偏好设置
     */
    @PreAuthorize("@ss.hasPermi('order:preferences:add')")
    @PostMapping
    @ApiOperation("新增指令偏好设置")
    public AjaxResult add(@RequestBody CommandPreferences commandPreferences)
    {
        return toAjax(commandPreferencesService.insertWithCache(commandPreferences));
    }

    /**
     * 修改指令偏好设置
     */
    @PreAuthorize("@ss.hasPermi('order:preferences:edit')")
    @PutMapping
    @ApiOperation("修改指令偏好设置")
    public AjaxResult edit(@RequestBody CommandPreferences commandPreferences)
    {
        return toAjax(commandPreferencesService.updateWithCache(commandPreferences));
    }

    /**
     * 删除指令偏好设置
     */
    @PreAuthorize("@ss.hasPermi('order:preferences:remove')")
    @DeleteMapping("/{ids}")
    @ApiOperation("删除指令偏好设置")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(commandPreferencesService.deleteWithCacheByIds(ids, true));
    }
}
