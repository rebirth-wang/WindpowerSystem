package com.fastbee.iot.mapper;

import com.fastbee.common.mybatis.mapper.BaseMapperX;
import com.fastbee.iot.domain.SceneTagPoints;

/**
 * 运算型变量点Mapper接口
 *
 * @author kerwincui
 * @date 2024-05-21
 */
public interface SceneTagPointsMapper extends BaseMapperX<SceneTagPoints>
{

    /**
     * 批量删除场景变量运算
     * @param sceneModelIds
     * @return void
     */
    void deleteBySceneModelIds(Long[] sceneModelIds);

    void phyDeleteBySceneModelIds(Long[] sceneModelIds);

    int restoreBySceneModelId(Long sceneModelId);
}
