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
import com.fastbee.iot.service.IRuleScriptNodeService;
import com.fastbee.rule.domain.RuleScriptNode;
import com.fastbee.rule.domain.vo.RuleScriptNodeVO;

/**
 * 规则脚本节点Controller
 *
 * @author zhuangpeng.li
 * @date 2025-05-08
 */
@RestController
@RequestMapping("/rule/node")
@Api(tags = "规则脚本节点")
public class RuleScriptNodeController extends BaseController {
    /** 代码生成区域 可直接覆盖**/
    @Resource
    private IRuleScriptNodeService ruleScriptNodeService;

    /**
     * 查询规则脚本节点列表
     */
    @PreAuthorize("@ss.hasPermi('rule:node:list')")
    @GetMapping("/list")
    @ApiOperation("查询规则脚本节点列表")
    public TableDataInfo list(RuleScriptNode ruleScriptNode) {
        Page<RuleScriptNodeVO> voPage = ruleScriptNodeService.pageRuleScriptNodeVO(ruleScriptNode);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 导出规则脚本节点列表
     */
    @ApiOperation("导出规则脚本节点列表")
    @PreAuthorize("@ss.hasPermi('rule:node:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, RuleScriptNode ruleScriptNode) {
        Page<RuleScriptNodeVO> voPage = ruleScriptNodeService.pageRuleScriptNodeVO(ruleScriptNode);
        ExcelUtil<RuleScriptNodeVO> util = new ExcelUtil<RuleScriptNodeVO>(RuleScriptNodeVO.class);
        util.exportExcel(response, voPage.getRecords(), "规则脚本节点数据");
    }

    /**
     * 获取规则脚本节点详细信息
     */
    @PreAuthorize("@ss.hasPermi('rule:node:query')")
    @GetMapping(value = "/{id}")
    @ApiOperation("获取规则脚本节点详细信息")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(ruleScriptNodeService.queryByIdWithCache(id));
    }

    /**
     * 新增规则脚本节点
     */
    @PreAuthorize("@ss.hasPermi('rule:node:add')")
    @PostMapping
    @ApiOperation("新增规则脚本节点")
    public AjaxResult add(@RequestBody RuleScriptNode ruleScriptNode) {
        return toAjax(ruleScriptNodeService.insertWithCache(ruleScriptNode));
    }

    /**
     * 修改规则脚本节点
     */
    @PreAuthorize("@ss.hasPermi('rule:node:edit')")
    @PutMapping
    @ApiOperation("修改规则脚本节点")
    public AjaxResult edit(@RequestBody RuleScriptNode ruleScriptNode) {
        return toAjax(ruleScriptNodeService.updateWithCache(ruleScriptNode));
    }

    /**
     * 删除规则脚本节点
     */
    @PreAuthorize("@ss.hasPermi('rule:node:remove')")
    @DeleteMapping("/{ids}")
    @ApiOperation("删除规则脚本节点")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(ruleScriptNodeService.deleteWithCacheByIds(ids, true));
    }
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
