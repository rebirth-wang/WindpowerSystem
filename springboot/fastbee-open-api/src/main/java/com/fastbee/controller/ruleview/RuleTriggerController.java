package com.fastbee.controller.ruleview;

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
import com.fastbee.iot.service.IRuleTriggerService;
import com.fastbee.rule.domain.RuleTrigger;
import com.fastbee.rule.domain.vo.RuleTriggerVO;

/**
 * 规则触发条件Controller
 *
 * @author fastbee
 * @date 2025-10-21
 */
@RestController
@RequestMapping("/iot/ruletrigger")
@Api(tags = "规则触发条件")
public class RuleTriggerController extends BaseController {
    /** 代码生成区域 可直接覆盖**/
    @Resource
    private IRuleTriggerService ruleTriggerService;

    /**
     * 查询规则触发条件列表
     */
    @PreAuthorize("@ss.hasPermi('iot:ruletrigger:list')")
    @GetMapping("/list")
    @ApiOperation("查询规则触发条件列表")
    public TableDataInfo list(RuleTrigger ruleTrigger) {
        Page<RuleTriggerVO> voPage = ruleTriggerService.pageRuleTriggerVO(ruleTrigger);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 导出规则触发条件列表
     */
    @ApiOperation("导出规则触发条件列表")
    @PreAuthorize("@ss.hasPermi('iot:ruletrigger:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, RuleTrigger ruleTrigger) {
        Page<RuleTriggerVO> voPage = ruleTriggerService.pageRuleTriggerVO(ruleTrigger);
        ExcelUtil<RuleTriggerVO> util = new ExcelUtil<RuleTriggerVO>(RuleTriggerVO.class);
        util.exportExcel(response, voPage.getRecords(), "规则触发条件数据");
    }

    /**
     * 获取规则触发条件详细信息
     */
    @PreAuthorize("@ss.hasPermi('iot:ruletrigger:query')")
    @GetMapping(value = "/{id}")
    @ApiOperation("获取规则触发条件详细信息")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(ruleTriggerService.queryByIdWithCache(id));
    }

    /**
     * 新增规则触发条件
     */
    @PreAuthorize("@ss.hasPermi('iot:ruletrigger:add')")
    @PostMapping
    @ApiOperation("新增规则触发条件")
    public AjaxResult add(@RequestBody RuleTrigger ruleTrigger) {
        return toAjax(ruleTriggerService.insertWithCache(ruleTrigger));
    }

    /**
     * 修改规则触发条件
     */
    @PreAuthorize("@ss.hasPermi('iot:ruletrigger:edit')")
    @PutMapping
    @ApiOperation("修改规则触发条件")
    public AjaxResult edit(@RequestBody RuleTrigger ruleTrigger) {
        return toAjax(ruleTriggerService.updateWithCache(ruleTrigger));
    }

    /**
     * 删除规则触发条件
     */
    @PreAuthorize("@ss.hasPermi('iot:ruletrigger:remove')")
    @DeleteMapping("/{ids}")
    @ApiOperation("删除规则触发条件")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(ruleTriggerService.deleteWithCacheByIds(ids, true));
    }
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
