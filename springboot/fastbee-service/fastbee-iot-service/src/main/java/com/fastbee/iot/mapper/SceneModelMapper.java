package com.fastbee.iot.mapper;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import com.fastbee.common.mybatis.mapper.BaseMapperX;
import com.fastbee.iot.domain.Product;
import com.fastbee.iot.domain.SceneModel;
import com.fastbee.iot.model.vo.SceneModelVO;

/**
 * 场景管理Mapper接口
 *
 * @author kerwincui
 * @date 2024-05-20
 */
public interface SceneModelMapper extends BaseMapperX<SceneModel>
{
    /**
     * 查询场景管理
     *
     * @param sceneModel 场景管理
     * @return 场景管理
     */
    public SceneModelVO selectSceneModelBySceneModelId(@Param("sceneModel") SceneModel sceneModel);

    /**
     * 查询场景管理列表
     *
     * @param sceneModelVO 场景管理
     * @return 场景管理集合
     */
    public Page<SceneModelVO> selectSceneModelVoPage(Page<SceneModelVO> page, @Param("sceneModelVO") SceneModelVO sceneModelVO);

    /**
     * 查询组态信息
     * @param guidList 组态id集合
     * @return java.util.List<com.fastbee.iot.domain.Product>
     */
    List<SceneModelVO> selectListScadaIdByGuidS(@Param("guidList") List<String> guidList);

    Page<SceneModelVO> delList(Page<SceneModelVO> page,@Param("sceneModel") SceneModel sceneModel);

    int phyDeleteBatchIds(List<Long> sceneModelIds);

    int restoreById(Long sceneModelId);

    List<SceneModel> selectByModelId(Long sceneModelId);
}
