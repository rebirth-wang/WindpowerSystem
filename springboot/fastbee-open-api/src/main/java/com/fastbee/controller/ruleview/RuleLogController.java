package com.fastbee.controller.ruleview;

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
import com.fastbee.iot.service.IRuleLogService;
import com.fastbee.rule.domain.RuleLog;
import com.fastbee.rule.domain.vo.RuleLogVO;

/**
 * 规则执行记录Controller
 *
 * @author fastbee
 * @date 2025-10-21
 */
@RestController
@RequestMapping("/iot/rulelog")
@Api(tags = "规则执行记录")
public class RuleLogController extends BaseController {
    /** 代码生成区域 可直接覆盖**/
    @Resource
    private IRuleLogService ruleLogService;

    /**
     * 查询规则执行记录列表
     */
    @PreAuthorize("@ss.hasPermi('iot:rulelog:list')")
    @GetMapping("/list")
    @ApiOperation("查询规则执行记录列表")
    public TableDataInfo list(RuleLog ruleLog) {
        Page<RuleLogVO> voPage = ruleLogService.pageRuleLogVO(ruleLog);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 导出规则执行记录列表
     */
    @ApiOperation("导出规则执行记录列表")
    @PreAuthorize("@ss.hasPermi('iot:rulelog:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, RuleLog ruleLog) {
        Page<RuleLogVO> voPage = ruleLogService.pageRuleLogVO(ruleLog);
        ExcelUtil<RuleLogVO> util = new ExcelUtil<RuleLogVO>(RuleLogVO.class);
        util.exportExcel(response, voPage.getRecords(), "规则执行记录数据");
    }

    /**
     * 获取规则执行记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('iot:rulelog:query')")
    @GetMapping(value = "/{ruleid}")
    @ApiOperation("获取规则执行记录详细信息")
    public AjaxResult getRuleLog(@PathVariable("ruleid") Long ruleId) {
        return success(ruleLogService.queryByRuleId(ruleId));
    }

    @PreAuthorize("@ss.hasPermi('iot:rulelog:query')")
    @GetMapping(value = "/{ruleid}/{nodeid}")
    @ApiOperation("获取规则执行记录详细信息")
    public AjaxResult getNodeLog(@PathVariable("ruleid") Long ruleId, @PathVariable("nodeid") String nodeId) {
        return success(ruleLogService.queryByNodeId(ruleId, nodeId));
    }

    /**
     * 新增规则执行记录
     */
    @PreAuthorize("@ss.hasPermi('iot:rulelog:add')")
    @PostMapping
    @ApiOperation("新增规则执行记录")
    public AjaxResult add(@RequestBody RuleLog ruleLog) {
        return toAjax(ruleLogService.insertWithCache(ruleLog));
    }

    /**
     * 修改规则执行记录
     */
    @PreAuthorize("@ss.hasPermi('iot:rulelog:edit')")
    @PutMapping
    @ApiOperation("修改规则执行记录")
    public AjaxResult edit(@RequestBody RuleLog ruleLog) {
        return toAjax(ruleLogService.updateWithCache(ruleLog));
    }

    /**
     * 删除规则执行记录
     */
    @PreAuthorize("@ss.hasPermi('iot:rulelog:remove')")
    @DeleteMapping("/{ids}")
    @ApiOperation("删除规则执行记录")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(ruleLogService.deleteWithCacheByIds(ids, true));
    }
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
