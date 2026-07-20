package com.fastbee.iot.mapper;

import java.util.List;

import com.fastbee.common.mybatis.mapper.BaseMapperX;
import com.fastbee.iot.domain.SceneModelTag;
import com.fastbee.iot.model.vo.SceneModelTagVO;

/**
 * 场景录入型变量Mapper接口
 *
 * @author kerwincui
 * @date 2024-05-21
 */
public interface SceneModelTagMapper extends BaseMapperX<SceneModelTag>
{
    /**
     * 查询场景录入型变量
     *
     * @param id 场景录入型变量主键
     * @return 场景录入型变量
     */
    public SceneModelTagVO selectSceneModelTagById(Long id);

    /**
     * 校验变量名称唯一
     * @param sceneModelTagVO 场景变量
     * @return com.fastbee.iot.domain.SceneModelTag
     */
    SceneModelTag checkName(SceneModelTagVO sceneModelTagVO);

    /**
     * 查询场景录入型变量
     *
     * @param id 场景录入型变量主键
     * @return 场景录入型变量
     */
    SceneModelTagVO selectSceneModelTagAndTenantById(Long id);

    void phyDeleteBySceneModelIds(List<Long> sceneModelIds);

    int restoreBySceneModelId(Long sceneModelId);

    List<SceneModelTag> selectEnableTagBySceneModelId(Long sceneModelId);
}
