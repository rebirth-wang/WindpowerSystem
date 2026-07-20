package com.fastbee.controller.deviceConfig;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.core.page.TableDataInfo;
import com.fastbee.common.extend.core.controller.BaseController;
import com.fastbee.common.extend.utils.poi.ExcelUtil;
import com.fastbee.iot.domain.ThingsModelTemplateTranslate;
import com.fastbee.iot.model.vo.ThingsModelTemplateTranslateVO;
import com.fastbee.iot.service.IThingsModelTemplateTranslateService;

/**
 * 物模型模板翻译Controller
 *
 * @author fastbee
 * @date 2025-12-26
 */
@RestController
@RequestMapping("/iot/thingsModelTranslate")
@Api(tags = "物模型模板翻译")
public class ThingsModelTemplateTranslateController extends BaseController {
    /** 代码生成区域 可直接覆盖**/
    @Resource
    private IThingsModelTemplateTranslateService thingsModelTemplateTranslateService;

    /**
     * 查询物模型模板翻译列表
     */
    @PreAuthorize("@ss.hasPermi('iot:translate:list')")
    @GetMapping("/list")
    @ApiOperation("查询物模型模板翻译列表")
    public TableDataInfo list(ThingsModelTemplateTranslate thingsModelTemplateTranslate) {
        Page<ThingsModelTemplateTranslateVO> voPage = thingsModelTemplateTranslateService.pageThingsModelTemplateTranslateVO(thingsModelTemplateTranslate);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 导出物模型模板翻译列表
     */
    @ApiOperation("导出物模型模板翻译列表")
    @PreAuthorize("@ss.hasPermi('iot:translate:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, ThingsModelTemplateTranslate thingsModelTemplateTranslate) {
        Page<ThingsModelTemplateTranslateVO> voPage = thingsModelTemplateTranslateService.pageThingsModelTemplateTranslateVO(thingsModelTemplateTranslate);
        ExcelUtil<ThingsModelTemplateTranslateVO> util = new ExcelUtil<ThingsModelTemplateTranslateVO>(ThingsModelTemplateTranslateVO.class);
        util.exportExcel(response, voPage.getRecords(), "物模型模板翻译数据");
    }

    /**
     * 获取物模型模板翻译详细信息
     */
    @PreAuthorize("@ss.hasPermi('iot:translate:query')")
    @GetMapping(value = "/{id}")
    @ApiOperation("获取物模型模板翻译详细信息")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(thingsModelTemplateTranslateService.queryByIdWithCache(id));
    }

    /**
     * 新增物模型模板翻译
     */
    @PreAuthorize("@ss.hasPermi('iot:translate:add')")
    @PostMapping
    @ApiOperation("新增物模型模板翻译")
    public AjaxResult add(@RequestBody ThingsModelTemplateTranslate thingsModelTemplateTranslate) {
        return toAjax(thingsModelTemplateTranslateService.insertWithCache(thingsModelTemplateTranslate));
    }

    /**
     * 修改物模型模板翻译
     */
    @PreAuthorize("@ss.hasPermi('iot:translate:edit')")
    @PutMapping
    @ApiOperation("修改物模型模板翻译")
    public AjaxResult edit(@RequestBody ThingsModelTemplateTranslate thingsModelTemplateTranslate) {
        return toAjax(thingsModelTemplateTranslateService.updateWithCache(thingsModelTemplateTranslate));
    }

    /**
     * 删除物模型模板翻译
     */
    @PreAuthorize("@ss.hasPermi('iot:translate:remove')")
    @DeleteMapping("/{ids}")
    @ApiOperation("删除物模型模板翻译")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(thingsModelTemplateTranslateService.deleteWithCacheByIds(ids, true));
    }
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
