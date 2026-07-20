package com.fastbee.controller.ruleEngine;

import java.util.Arrays;
import java.util.List;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.fastbee.common.annotation.Log;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.core.page.TableDataInfo;
import com.fastbee.common.enums.BusinessType;
import com.fastbee.common.extend.core.controller.BaseController;
import com.fastbee.common.extend.core.domin.model.LoginUser;
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.common.extend.utils.poi.ExcelUtil;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.iot.domain.Alert;
import com.fastbee.iot.domain.Scene;
import com.fastbee.iot.model.vo.SceneVO;
import com.fastbee.iot.service.ISceneService;
import com.fastbee.iot.service.IScriptService;

/**
 * 场景联动Controller
 *
 * @author kerwincui
 * @date 2022-01-13
 */
@Api(tags = "场景联动")
@RestController
@RequestMapping("/iot/scene")
public class SceneController extends BaseController {
    @Resource
    private ISceneService sceneService;

    @Resource
    private IScriptService scriptService;

    /**
     * 查询场景联动列表
     */
    @ApiOperation("查询场景联动列表")
    @PreAuthorize("@ss.hasPermi('iot:scene:list')")
    @GetMapping("/list")
    public TableDataInfo list(Scene scene) {
        Page<SceneVO> voPage = sceneService.pageSceneVO(scene);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 导出场景联动列表
     */
    @ApiOperation("导出场景联动列表")
    @PreAuthorize("@ss.hasPermi('iot:scene:export')")
    @Log(title = "场景联动", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Scene scene) {
        Page<SceneVO> voPage = sceneService.pageSceneVO(scene);
        ExcelUtil<SceneVO> util = new ExcelUtil<>(SceneVO.class);
        util.exportExcel(response, voPage.getRecords(), "场景联动数据");
    }

    /**
     * 获取场景联动详细信息
     */
    @ApiOperation("获取场景联动详细信息")
    @PreAuthorize("@ss.hasPermi('iot:scene:query')")
    @GetMapping(value = "/{sceneId}")
    public AjaxResult getInfo(@PathVariable("sceneId") Long sceneId) {
        Scene scene = new Scene();
        scene.setSceneId(sceneId);
        return AjaxResult.success(sceneService.selectSceneBySceneId(scene));
    }

    /**
     * 新增场景联动
     */
    @ApiOperation("新增场景联动")
    @PreAuthorize("@ss.hasPermi('iot:scene:add')")
    @Log(title = "场景联动", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SceneVO sceneVO) {
        LoginUser loginUser = getLoginUser();
        if (loginUser == null || loginUser.getUser() == null) {
            return error(MessageUtils.message("user.not.login"));
        }
        // 查询归属机构
        if (null != loginUser.getDeptId()) {
            sceneVO.setUserId(loginUser.getUser().getDept().getDeptUserId());
            sceneVO.setUserName(loginUser.getUser().getDept().getDeptName());
            sceneVO.setTerminalUser(0);
        } else {
            sceneVO.setUserId(loginUser.getUser().getUserId());
            sceneVO.setUserName(loginUser.getUser().getUserName());
            sceneVO.setTerminalUser(1);
        }
        sceneVO.setCreateBy(loginUser.getUsername());
        return toAjax(sceneService.insertScene(sceneVO));
    }

    /**
     * 修改场景联动
     */
    @ApiOperation("修改场景联动")
    @PreAuthorize("@ss.hasPermi('iot:scene:edit')")
    @Log(title = "场景联动", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SceneVO sceneVO) {
        SecurityUtils.checkUserOperatePermission(sceneVO.getUserId(), sceneVO.getCreateBy());
        sceneVO.setUpdateBy(getUsername());
        return toAjax(sceneService.updateScene(sceneVO));
    }

    /**
     * 获取规则引擎脚本日志
     */
    @PreAuthorize("@ss.hasPermi('iot:scene:query')")
    @GetMapping(value = "/log/{chainName}")
    public AjaxResult getScriptLog(@PathVariable("chainName") String chainName) {
        return success(scriptService.selectRuleScriptLog("scene", chainName));
    }

    /**
     * 删除场景联动
     */
    @ApiOperation("删除场景联动")
    @PreAuthorize("@ss.hasPermi('iot:scene:remove')")
    @Log(title = "场景联动", businessType = BusinessType.DELETE)
    @DeleteMapping("/{sceneIds}")
    public AjaxResult remove(@PathVariable Long[] sceneIds) {
        List<Scene> sceneList = sceneService.listByIds(Arrays.asList(sceneIds));
        for (Scene scene : sceneList) {
            SecurityUtils.checkUserOperatePermission(scene.getUserId(), scene.getCreateBy());
        }
        return toAjax(sceneService.deleteSceneBySceneIds(sceneIds));
    }

    /**
     * 修改场景联动状态
     */
    @ApiOperation("修改场景联动状态")
    @PreAuthorize("@ss.hasPermi('iot:scene:edit')")
    @Log(title = "场景联动", businessType = BusinessType.UPDATE)
    @PutMapping("/updateStatus")
    public AjaxResult updateStatus(@RequestBody Scene scene) {
        SecurityUtils.checkUserOperatePermission(scene.getUserId(), scene.getCreateBy());
        return toAjax(sceneService.updateStatus(scene));
    }

    /**
     * 根据场景查询绑定的告警配置列表
     */
    @ApiOperation("根据场景查询绑定的告警配置列表")
    @PreAuthorize("@ss.hasPermi('iot:scene:query')")
    @GetMapping(value = "/alerts")
    public TableDataInfo getAlarmConfigsByScene(SceneVO  scene) {
        Page<Alert> alertPage = sceneService.selectAlarmConfigsByScene(scene);
        return getDataTable(alertPage.getRecords(), alertPage.getTotal());
    }

    /**
     * 为场景绑定告警配置
     */
    @ApiOperation("为场景绑定告警配置")
    @PreAuthorize("@ss.hasPermi('iot:scene:edit')")
    @Log(title = "场景绑定告警", businessType = BusinessType.UPDATE)
    @PostMapping("/bindAlerts")
    public AjaxResult bindAlertToScene(@RequestBody SceneVO sceneVO) {
        try {
            Long sceneId = sceneVO.getSceneId();
            List<Long> alertIds = sceneVO.getAlertIds(); // 假设SceneVO中有alertIds字段

            if (sceneId == null || alertIds == null || alertIds.isEmpty()) {
                return AjaxResult.error("场景ID和告警ID列表不能为空");
            }

            int result = sceneService.bindAlertToScene(sceneId, alertIds);
            return result > 0 ? AjaxResult.success() : AjaxResult.error("绑定失败");
        } catch (IllegalArgumentException e) {
            return AjaxResult.error(e.getMessage());
        } catch (Exception e) {
            return AjaxResult.error("绑定失败：" + e.getMessage());
        }
    }

    /**
     * 解除场景与告警配置的绑定关系
     */
    @ApiOperation("解除场景与告警配置的绑定关系")
    @PreAuthorize("@ss.hasPermi('iot:scene:edit')")
    @Log(title = "场景解绑告警", businessType = BusinessType.UPDATE)
    @DeleteMapping("/unbindAlerts")
    public AjaxResult unbindAlertFromScene(@RequestBody SceneVO sceneVO) {
        try {
            Long sceneId = sceneVO.getSceneId();
            List<Long> alertIds = sceneVO.getAlertIds();

            if (sceneId == null || alertIds == null || alertIds.isEmpty()) {
                return AjaxResult.error("场景ID和告警ID列表不能为空");
            }

            int result = sceneService.unbindAlertFromScene(sceneId, alertIds);
            return result > 0 ? AjaxResult.success() : AjaxResult.error("解绑失败");
        } catch (Exception e) {
            return AjaxResult.error("解绑失败：" + e.getMessage());
        }
    }

}
