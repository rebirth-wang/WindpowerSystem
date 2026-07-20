package com.fastbee.controller.sceneModel;

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
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.common.extend.utils.poi.ExcelUtil;
import com.fastbee.iot.domain.SceneModel;
import com.fastbee.iot.model.vo.SceneModelVO;
import com.fastbee.iot.service.ISceneModelService;

/**
 * 场景管理Controller
 *
 * @author kerwincui
 * @date 2024-05-20
 */
@RestController
@RequestMapping("/scene/model")
@Api(tags = "场景管理")
public class SceneModelController extends BaseController
{
    @Resource
    private ISceneModelService sceneModelService;

    /**
     * 查询场景管理列表
     */
    @PreAuthorize("@ss.hasPermi('scene:model:list')")
    @GetMapping("/list")
    @ApiOperation("查询场景管理列表")
    public TableDataInfo list(SceneModelVO sceneModel)
    {
        Page<SceneModelVO> voPage = sceneModelService.pageSceneModelVO(sceneModel);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 导出场景管理列表
     */
    @PreAuthorize("@ss.hasPermi('scene:model:export')")
    @Log(title = "场景管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ApiOperation("导出场景管理列表")
    public void export(HttpServletResponse response, SceneModelVO sceneModel)
    {
        Page<SceneModelVO> voPage = sceneModelService.pageSceneModelVO(sceneModel);
        ExcelUtil<SceneModelVO> util = new ExcelUtil<SceneModelVO>(SceneModelVO.class);
        util.exportExcel(response, voPage.getRecords(), "场景管理数据");
    }

    /**
     * 获取场景管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('scene:model:query')")
    @GetMapping(value = "/{sceneModelId}")
    @ApiOperation("获取场景管理详细信息")
    public AjaxResult getInfo(@PathVariable("sceneModelId") Long sceneModelId)
    {
        SceneModel sceneModel = new SceneModel();
        sceneModel.setSceneModelId(sceneModelId);
        return success(sceneModelService.selectSceneModelBySceneModelId(sceneModel));
    }

    /**
     * 新增场景管理
     */
    @PreAuthorize("@ss.hasPermi('scene:model:add')")
    @Log(title = "场景管理", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation("新增场景管理")
    public AjaxResult add(@RequestBody SceneModel sceneModel)
    {
        return toAjax(sceneModelService.insertSceneModel(sceneModel));
    }

    /**
     * 修改场景管理
     */
    @PreAuthorize("@ss.hasPermi('scene:model:edit')")
    @Log(title = "场景管理", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation("修改场景管理")
    public AjaxResult edit(@RequestBody SceneModelVO sceneModel)
    {
        SecurityUtils.checkUserOperatePermission(sceneModel.getTenantId(), sceneModel.getCreateBy());
        return sceneModelService.updateSceneModel(sceneModel);
    }

    /**
     * 删除场景管理
     */
    @PreAuthorize("@ss.hasPermi('scene:model:remove')")
    @Log(title = "场景管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{sceneModelIds}")
    @ApiOperation("删除场景管理")
    public AjaxResult remove(@PathVariable Long[] sceneModelIds)
    {
        List<SceneModel> sceneModelList = sceneModelService.listByIds(Arrays.asList(sceneModelIds));
        for (SceneModel sceneModel : sceneModelList) {
            SecurityUtils.checkUserOperatePermission(sceneModel.getTenantId(), sceneModel.getCreateBy());
        }
        return toAjax(sceneModelService.deleteSceneModelBySceneModelIds(sceneModelIds));
    }

    /**
     * 查询逻辑删除场景管理列表
     */
    @PreAuthorize("@ss.hasPermi('iot:recovery:sceneModel')")
    @GetMapping("/delList")
    @ApiOperation("查询场景管理列表")
    public TableDataInfo delList(SceneModel sceneModel)
    {
        Page<SceneModelVO> voPage = sceneModelService.delList(sceneModel);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 物理删除场景管理
     */
    @PreAuthorize("@ss.hasPermi('iot:recovery:delete')")
    @Log(title = "场景管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/physicDel/{sceneModelIds}")
    @ApiOperation("物理删除场景管理")
    public AjaxResult physicalDeleteSceneModel(@PathVariable Long[] sceneModelIds)
    {
        return sceneModelService.physicalDeleteSceneModel(sceneModelIds);
    }

    /**
     * 还原逻辑删除场景管理
     */
    @PreAuthorize("@ss.hasPermi('iot:recovery:restore')")
    @Log(title = "场景管理", businessType = BusinessType.UPDATE)
    @PutMapping("/restore")
    @ApiOperation("还原逻辑删除场景管理")
    public AjaxResult restoreSceneModel(Long sceneModelId)
    {
        return sceneModelService.restoreSceneModel(sceneModelId);
    }
}
