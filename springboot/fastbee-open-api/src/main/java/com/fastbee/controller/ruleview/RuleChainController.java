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
import com.fastbee.iot.service.IRuleChainService;
import com.fastbee.rule.domain.RuleChain;
import com.fastbee.rule.domain.vo.RuleChainVO;

/**
 * 规则链Controller
 *
 * @author zhuangpeng.li
 * @date 2025-05-08
 */
@RestController
@RequestMapping("/rule/chain")
@Api(tags = "规则链")
public class RuleChainController extends BaseController {
    /** 代码生成区域 可直接覆盖**/
    @Resource
    private IRuleChainService ruleChainService;

    /**
     * 查询规则链列表
     */
    @PreAuthorize("@ss.hasPermi('rule:chain:list')")
    @GetMapping("/list")
    @ApiOperation("查询规则链列表")
    public TableDataInfo list(RuleChain ruleChain) {
        Page<RuleChainVO> voPage = ruleChainService.pageRuleChainVO(ruleChain);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 导出规则链列表
     */
    @ApiOperation("导出规则链列表")
    @PreAuthorize("@ss.hasPermi('rule:chain:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, RuleChain ruleChain) {
        Page<RuleChainVO> voPage = ruleChainService.pageRuleChainVO(ruleChain);
        ExcelUtil<RuleChainVO> util = new ExcelUtil<RuleChainVO>(RuleChainVO.class);
        util.exportExcel(response, voPage.getRecords(), "规则链数据");
    }

    /**
     * 获取规则链详细信息
     */
    @PreAuthorize("@ss.hasPermi('rule:chain:query')")
    @GetMapping(value = "/{id}")
    @ApiOperation("获取规则链详细信息")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(ruleChainService.queryByIdWithCache(id));
    }

    /**
     * 新增规则链
     */
    @PreAuthorize("@ss.hasPermi('rule:chain:add')")
    @PostMapping
    @ApiOperation("新增规则链")
    public AjaxResult add(@RequestBody RuleChain ruleChain) {
        return toAjax(ruleChainService.insertWithCache(ruleChain));
    }

    /**
     * 修改规则链
     */
    @PreAuthorize("@ss.hasPermi('rule:chain:edit')")
    @PutMapping
    @ApiOperation("修改规则链")
    public AjaxResult edit(@RequestBody RuleChain ruleChain) {
        return toAjax(ruleChainService.updateWithCache(ruleChain));
    }

    /**
     * 删除规则链
     */
    @PreAuthorize("@ss.hasPermi('rule:chain:remove')")
    @DeleteMapping("/{ids}")
    @ApiOperation("删除规则链")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(ruleChainService.deleteWithCacheByIds(ids, true));
    }
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
