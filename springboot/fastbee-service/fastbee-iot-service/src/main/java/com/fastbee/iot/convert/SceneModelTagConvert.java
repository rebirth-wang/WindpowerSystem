package com.fastbee.iot.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.iot.domain.SceneModelTag;
import com.fastbee.iot.model.vo.SceneModelTagVO;

/**
 * 场景录入型变量Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-12-26
 */
@Mapper
public interface SceneModelTagConvert
{
    /** 代码生成区域 可直接覆盖**/
    SceneModelTagConvert INSTANCE = Mappers.getMapper(SceneModelTagConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param sceneModelTag
     * @return 场景录入型变量集合
     */
    SceneModelTagVO convertSceneModelTagVO(SceneModelTag sceneModelTag);

    /**
     * VO类转换为实体类集合
     *
     * @param sceneModelTagVO
     * @return 场景录入型变量集合
     */
    SceneModelTag convertSceneModelTag(SceneModelTagVO sceneModelTagVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param sceneModelTagList
     * @return 场景录入型变量集合
     */
    List<SceneModelTagVO> convertSceneModelTagVOList(List<SceneModelTag> sceneModelTagList);

    /**
     * VO类转换为实体类
     *
     * @param sceneModelTagVOList
     * @return 场景录入型变量集合
     */
    List<SceneModelTag> convertSceneModelTagList(List<SceneModelTagVO> sceneModelTagVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param sceneModelTagPage
     * @return 场景录入型变量分页
     */
    Page<SceneModelTagVO> convertSceneModelTagVOPage(Page<SceneModelTag> sceneModelTagPage);

    /**
     * VO类转换为实体类
     *
     * @param sceneModelTagVOPage
     * @return 场景录入型变量分页
     */
    Page<SceneModelTag> convertSceneModelTagPage(Page<SceneModelTagVO> sceneModelTagVOPage);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
