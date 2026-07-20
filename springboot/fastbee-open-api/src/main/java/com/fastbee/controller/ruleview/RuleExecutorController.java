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
import com.fastbee.iot.service.IRuleExecutorService;
import com.fastbee.rule.domain.RuleExecutor;
import com.fastbee.rule.domain.vo.RuleExecutorVO;

/**
 * 规则执行器Controller
 *
 * @author zhuangpeng.li
 * @date 2025-05-08
 */
@RestController
@RequestMapping("/rule/executor")
@Api(tags = "规则执行器")
public class RuleExecutorController extends BaseController {
    /** 代码生成区域 可直接覆盖**/
    @Resource
    private IRuleExecutorService ruleExecutorService;

    /**
     * 查询规则执行器列表
     */
    @PreAuthorize("@ss.hasPermi('rule:executor:list')")
    @GetMapping("/list")
    @ApiOperation("查询规则执行器列表")
    public TableDataInfo list(RuleExecutor ruleExecutor) {
        Page<RuleExecutorVO> voPage = ruleExecutorService.pageRuleExecutorVO(ruleExecutor);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 导出规则执行器列表
     */
    @ApiOperation("导出规则执行器列表")
    @PreAuthorize("@ss.hasPermi('rule:executor:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, RuleExecutor ruleExecutor) {
        Page<RuleExecutorVO> voPage = ruleExecutorService.pageRuleExecutorVO(ruleExecutor);
        ExcelUtil<RuleExecutorVO> util = new ExcelUtil<RuleExecutorVO>(RuleExecutorVO.class);
        util.exportExcel(response, voPage.getRecords(), "规则执行器数据");
    }

    /**
     * 获取规则执行器详细信息
     */
    @PreAuthorize("@ss.hasPermi('rule:executor:query')")
    @GetMapping(value = "/{id}")
    @ApiOperation("获取规则执行器详细信息")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(ruleExecutorService.queryByIdWithCache(id));
    }

    /**
     * 新增规则执行器
     */
    @PreAuthorize("@ss.hasPermi('rule:executor:add')")
    @PostMapping
    @ApiOperation("新增规则执行器")
    public AjaxResult add(@RequestBody RuleExecutor ruleExecutor) {
        return toAjax(ruleExecutorService.insertWithCache(ruleExecutor));
    }

    /**
     * 修改规则执行器
     */
    @PreAuthorize("@ss.hasPermi('rule:executor:edit')")
    @PutMapping
    @ApiOperation("修改规则执行器")
    public AjaxResult edit(@RequestBody RuleExecutor ruleExecutor) {
        return toAjax(ruleExecutorService.updateWithCache(ruleExecutor));
    }

    /**
     * 删除规则执行器
     */
    @PreAuthorize("@ss.hasPermi('rule:executor:remove')")
    @DeleteMapping("/{ids}")
    @ApiOperation("删除规则执行器")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(ruleExecutorService.deleteWithCacheByIds(ids, true));
    }
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
