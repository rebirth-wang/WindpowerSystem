package com.fastbee.controller.ruleEngine;

import java.util.Arrays;
import java.util.List;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.fastbee.common.annotation.Log;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.core.page.TableDataInfo;
import com.fastbee.common.enums.BusinessType;
import com.fastbee.common.extend.core.controller.BaseController;
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.common.extend.utils.poi.ExcelUtil;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.iot.domain.Script;
import com.fastbee.iot.model.vo.ScriptVO;
import com.fastbee.iot.service.IScriptService;

/**
 * 规则引擎脚本Controller
 *
 * @author lizhuangpeng
 * @date 2023-07-01
 */
@RestController
@RequestMapping("/iot/script")
public class ScriptController extends BaseController {
    @Resource
    private IScriptService scriptService;

    /**
     * 查询规则引擎脚本列表
     */
    @PreAuthorize("@ss.hasPermi('iot:script:list')")
    @GetMapping("/list")
    public TableDataInfo list(Script ruleScript) {
        Page<ScriptVO> voPage = scriptService.selectRuleScriptList(ruleScript);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 导出规则引擎脚本列表
     */
    @PreAuthorize("@ss.hasPermi('iot:script:export')")
    @Log(title = "规则引擎脚本", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Script ruleScript) {
        Page<ScriptVO> voPage = scriptService.selectRuleScriptList(ruleScript);
        ExcelUtil<ScriptVO> util = new ExcelUtil<>(ScriptVO.class);
        util.exportExcel(response, voPage.getRecords(), "规则引擎脚本数据");
    }

    /**
     * 获取规则引擎脚本详细信息
     */
    @PreAuthorize("@ss.hasPermi('iot:script:query')")
    @GetMapping(value = "/{scriptId}")
    public AjaxResult getInfo(@PathVariable("scriptId") String scriptId) {
        Script script = new Script();
        script.setScriptId(scriptId);
        return success(scriptService.selectRuleScriptById(script));
    }

    /**
     * 新增规则引擎脚本
     */
    @PreAuthorize("@ss.hasPermi('iot:script:add')")
    @Log(title = "规则引擎脚本", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ScriptVO scriptVO) {
        return toAjax(scriptService.insertRuleScript(scriptVO));
    }

    /**
     * 修改规则引擎脚本
     */
    @PreAuthorize("@ss.hasPermi('iot:script:edit')")
    @Log(title = "规则引擎脚本", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ScriptVO scriptVO) {
        SecurityUtils.checkUserOperatePermission(scriptVO.getUserId(), scriptVO.getCreateBy());
        return toAjax(scriptService.updateRuleScript(scriptVO));
    }

    /**
     * 获取规则引擎脚本日志
     */
    @PreAuthorize("@ss.hasPermi('iot:script:query')")
    @GetMapping(value = "/log/{scriptId}")
    public AjaxResult getScriptLog(@PathVariable("scriptId") String scriptId) {
        return success(scriptService.selectRuleScriptLog("script", scriptId));
    }


    /**
     * 删除规则引擎脚本
     */
    @PreAuthorize("@ss.hasPermi('iot:script:remove')")
    @Log(title = "规则引擎脚本", businessType = BusinessType.DELETE)
    @DeleteMapping("/{scriptIds}")
    public AjaxResult remove(@PathVariable String[] scriptIds) {
        List<Script> scriptList = scriptService.listByIds(Arrays.asList(scriptIds));
        for (Script script : scriptList) {
            SecurityUtils.checkUserOperatePermission(script.getUserId(), script.getCreateBy());
        }
        return toAjax(scriptService.deleteRuleScriptByIds(scriptIds));
    }

    /**
     * 验证规则引擎脚本
     */
    @PostMapping("/validate")
    public AjaxResult validateScript(@RequestBody Script ruleScript) {
        return scriptService.validateScript(ruleScript);
    }

    @PreAuthorize("@ss.hasPermi('iot:script:allowpackage')")
    @GetMapping("/allowpackage")
    public AjaxResult getAllowPackage() {
        return AjaxResult.success(MessageUtils.message("operate.success"), scriptService.getAllowPackage());
    }
    @PreAuthorize("@ss.hasPermi('iot:script:allowpackage')")
    @PutMapping("/allowpackage")
    public AjaxResult addAllowPackage(@RequestBody String allowPackage) {
        return AjaxResult.success(MessageUtils.message("operate.success"), scriptService.addAllowPackage(allowPackage));
    }
    @PreAuthorize("@ss.hasPermi('iot:script:allowpackage')")
    @DeleteMapping("/allowpackage")
    public AjaxResult removeAllowPackage(@RequestBody String allowPackage) {
        return AjaxResult.success(MessageUtils.message("operate.success"), scriptService.removeAllowPackage(allowPackage));
    }
}
