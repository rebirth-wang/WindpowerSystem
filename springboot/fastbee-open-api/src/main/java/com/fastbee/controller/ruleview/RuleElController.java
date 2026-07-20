package com.fastbee.controller.ruleview;

import java.util.Arrays;
import java.util.List;

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
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.common.extend.utils.poi.ExcelUtil;
import com.fastbee.iot.service.IRuleElService;
import com.fastbee.rule.domain.RuleEl;
import com.fastbee.rule.domain.vo.RuleElVO;

/**
 * 规则elController
 *
 * @author zhuangpeng.li
 * @date 2025-05-08
 */
@RestController
@RequestMapping("/rule/el")
@Api(tags = "规则el")
public class RuleElController extends BaseController {
    /** 代码生成区域 可直接覆盖**/
    @Resource
    private IRuleElService ruleElService;

    /**
     * 查询规则el列表
     */
    @PreAuthorize("@ss.hasPermi('rule:el:list')")
    @GetMapping("/list")
    @ApiOperation("查询规则el列表")
    public TableDataInfo list(RuleEl ruleEl) {
        Page<RuleElVO> voPage = ruleElService.pageRuleElVO(ruleEl);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 导出规则el列表
     */
    @ApiOperation("导出规则el列表")
    @PreAuthorize("@ss.hasPermi('rule:el:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, RuleEl ruleEl) {
        Page<RuleElVO> voPage = ruleElService.pageRuleElVO(ruleEl);
        ExcelUtil<RuleElVO> util = new ExcelUtil<RuleElVO>(RuleElVO.class);
        util.exportExcel(response, voPage.getRecords(), "规则el数据");
    }

    /**
     * 获取规则el详细信息
     */
    @PreAuthorize("@ss.hasPermi('rule:el:query')")
    @GetMapping(value = "/{id}")
    @ApiOperation("获取规则el详细信息")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        RuleEl ruleEl = new RuleEl();
        ruleEl.setId(id);
        return success(ruleElService.queryByIdWithCache(ruleEl));
    }

    /**
     * 新增规则el
     */
    @PreAuthorize("@ss.hasPermi('rule:el:add')")
    @PostMapping
    @ApiOperation("新增规则el")
    public AjaxResult add(@RequestBody RuleEl ruleEl) {
        return toAjax(ruleElService.insertWithCache(ruleEl));
    }

    /**
     * 修改规则el
     */
    @PreAuthorize("@ss.hasPermi('rule:el:edit')")
    @PutMapping
    @ApiOperation("修改规则el")
    public AjaxResult edit(@RequestBody RuleEl ruleEl) {
        SecurityUtils.checkUserOperatePermission(ruleEl.getTenantId(), ruleEl.getCreateBy());
        return toAjax(ruleElService.updateWithCache(ruleEl));
    }

    /**
     * 删除规则el
     */
    @PreAuthorize("@ss.hasPermi('rule:el:remove')")
    @DeleteMapping("/{ids}")
    @ApiOperation("删除规则el")
    public AjaxResult remove(@PathVariable Long[] ids) {
        List<RuleEl> ruleElList = ruleElService.listByIds(Arrays.asList(ids));
        for (RuleEl ruleEl : ruleElList) {
            SecurityUtils.checkUserOperatePermission(ruleEl.getTenantId(), ruleEl.getCreateBy());
        }
        return toAjax(ruleElService.deleteWithCacheByIds(ids, true));
    }

    @PostMapping("/exec")
    @PreAuthorize("@ss.hasPermi('rule:cmp:edit')")
    public AjaxResult exec(@RequestBody RuleEl ruleEl){
        SecurityUtils.checkUserOperatePermission(ruleEl.getTenantId(), ruleEl.getCreateBy());
        return toAjax(ruleElService.exec(ruleEl));
    }

    @PostMapping("/publish")
    @PreAuthorize("@ss.hasPermi('rule:cmp:edit')")
    @ApiOperation("规则发布")
    public AjaxResult publish(@RequestBody RuleEl ruleEl){
        SecurityUtils.checkUserOperatePermission(ruleEl.getTenantId(), ruleEl.getCreateBy());
        return toAjax(ruleElService.publish(ruleEl));
    }

}
