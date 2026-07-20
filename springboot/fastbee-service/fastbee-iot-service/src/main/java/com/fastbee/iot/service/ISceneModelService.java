package com.fastbee.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.iot.domain.SceneModel;
import com.fastbee.iot.model.vo.SceneModelVO;

/**
 * 场景管理Service接口
 *
 * @author kerwincui
 * @date 2024-05-20
 */
public interface ISceneModelService extends IService<SceneModel>
{
    /**
     * 查询场景管理
     *
     * @param sceneModel 场景管理
     * @return 场景管理
     */
    public SceneModelVO selectSceneModelBySceneModelId(SceneModel sceneModel);

    /**
     * 查询场景管理列表
     *
     * @param sceneModel 场景管理
     * @return 场景管理集合
     */
    public List<SceneModelVO> selectSceneModelList(SceneModelVO sceneModel);

    /**
     * 查询场景管理列表
     *
     * @param sceneModel 场景管理
     * @return 场景管理分页集合
     */
    Page<SceneModelVO> pageSceneModelVO(SceneModelVO sceneModel);

    /**
     * 新增场景管理
     *
     * @param sceneModel 场景管理
     * @return 结果
     */
    public int insertSceneModel(SceneModel sceneModel);

    /**
     * 修改场景管理
     *
     * @param sceneModel 场景管理
     * @return 结果
     */
    public AjaxResult updateSceneModel(SceneModelVO sceneModel);

    /**
     * 批量删除场景管理
     *
     * @param sceneModelIds 需要删除的场景管理主键集合
     * @return 结果
     */
    public int deleteSceneModelBySceneModelIds(Long[] sceneModelIds);

    /**
     * 删除场景管理信息
     *
     * @param sceneModelId 场景管理主键
     * @return 结果
     */
    public int deleteSceneModelBySceneModelId(Long sceneModelId);

    Page<SceneModelVO> delList(SceneModel sceneModel);

    AjaxResult physicalDeleteSceneModel(Long[] sceneModelIds);

    AjaxResult restoreSceneModel(Long sceneModelId);

}
