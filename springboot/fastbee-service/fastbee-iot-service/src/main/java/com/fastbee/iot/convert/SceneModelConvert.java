package com.fastbee.iot.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.iot.domain.SceneModel;
import com.fastbee.iot.model.vo.SceneModelVO;

/**
 * 场景管理Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-11-19
 */
@Mapper
public interface SceneModelConvert
{

    SceneModelConvert INSTANCE = Mappers.getMapper(SceneModelConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param sceneModel
     * @return 场景管理集合
     */
    SceneModelVO convertSceneModelVO(SceneModel sceneModel);

    /**
     * VO类转换为实体类集合
     *
     * @param sceneModelVO
     * @return 场景管理集合
     */
    SceneModel convertSceneModel(SceneModelVO sceneModelVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param sceneModelList
     * @return 场景管理集合
     */
    List<SceneModelVO> convertSceneModelVOList(List<SceneModel> sceneModelList);

    /**
     * VO类转换为实体类
     *
     * @param sceneModelVOList
     * @return 场景管理集合
     */
    List<SceneModel> convertSceneModelList(List<SceneModelVO> sceneModelVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param sceneModelPage
     * @return 场景管理分页
     */
    Page<SceneModelVO> convertSceneModelVOPage(Page<SceneModel> sceneModelPage);

    /**
     * VO类转换为实体类
     *
     * @param sceneModelVOPage
     * @return 场景管理分页
     */
    Page<SceneModel> convertSceneModelPage(Page<SceneModelVO> sceneModelVOPage);
}
