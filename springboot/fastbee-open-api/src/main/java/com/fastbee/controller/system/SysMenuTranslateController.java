package com.fastbee.controller.system;

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
import com.fastbee.system.domain.SysMenuTranslate;
import com.fastbee.system.domain.vo.SysMenuTranslateVO;
import com.fastbee.system.service.ISysMenuTranslateService;

/**
 * 菜单名称翻译Controller
 *
 * @author fastbee
 * @date 2025-12-26
 */
@RestController
@RequestMapping("/iot/menuTranslate")
@Api(tags = "菜单名称翻译")
public class SysMenuTranslateController extends BaseController {
    /** 代码生成区域 可直接覆盖**/
    @Resource
    private ISysMenuTranslateService sysMenuTranslateService;

    /**
     * 查询菜单名称翻译列表
     */
    @PreAuthorize("@ss.hasPermi('iot:translate:list')")
    @GetMapping("/list")
    @ApiOperation("查询菜单名称翻译列表")
    public TableDataInfo list(SysMenuTranslate sysMenuTranslate) {
        Page<SysMenuTranslateVO> voPage = sysMenuTranslateService.pageSysMenuTranslateVO(sysMenuTranslate);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 导出菜单名称翻译列表
     */
    @ApiOperation("导出菜单名称翻译列表")
    @PreAuthorize("@ss.hasPermi('iot:translate:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysMenuTranslate sysMenuTranslate) {
        Page<SysMenuTranslateVO> voPage = sysMenuTranslateService.pageSysMenuTranslateVO(sysMenuTranslate);
        ExcelUtil<SysMenuTranslateVO> util = new ExcelUtil<SysMenuTranslateVO>(SysMenuTranslateVO.class);
        util.exportExcel(response, voPage.getRecords(), "菜单名称翻译数据");
    }

    /**
     * 获取菜单名称翻译详细信息
     */
    @PreAuthorize("@ss.hasPermi('iot:translate:query')")
    @GetMapping(value = "/{id}")
    @ApiOperation("获取菜单名称翻译详细信息")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(sysMenuTranslateService.queryByIdWithCache(id));
    }

    /**
     * 新增菜单名称翻译
     */
    @PreAuthorize("@ss.hasPermi('iot:translate:add')")
    @PostMapping
    @ApiOperation("新增菜单名称翻译")
    public AjaxResult add(@RequestBody SysMenuTranslate sysMenuTranslate) {
        return toAjax(sysMenuTranslateService.insertWithCache(sysMenuTranslate));
    }

    /**
     * 修改菜单名称翻译
     */
    @PreAuthorize("@ss.hasPermi('iot:translate:edit')")
    @PutMapping
    @ApiOperation("修改菜单名称翻译")
    public AjaxResult edit(@RequestBody SysMenuTranslate sysMenuTranslate) {
        return toAjax(sysMenuTranslateService.updateWithCache(sysMenuTranslate));
    }

    /**
     * 删除菜单名称翻译
     */
    @PreAuthorize("@ss.hasPermi('iot:translate:remove')")
    @DeleteMapping("/{ids}")
    @ApiOperation("删除菜单名称翻译")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(sysMenuTranslateService.deleteWithCacheByIds(ids, true));
    }
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
