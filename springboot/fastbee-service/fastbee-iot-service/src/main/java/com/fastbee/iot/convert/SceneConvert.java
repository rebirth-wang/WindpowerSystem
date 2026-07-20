package com.fastbee.iot.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.iot.domain.Scene;
import com.fastbee.iot.model.vo.SceneVO;

/**
 * 场景联动Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-11-13
 */
@Mapper
public interface SceneConvert
{

    SceneConvert INSTANCE = Mappers.getMapper(SceneConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param scene
     * @return 场景联动集合
     */
    SceneVO convertSceneVO(Scene scene);

    /**
     * VO类转换为实体类集合
     *
     * @param sceneVO
     * @return 场景联动集合
     */
    Scene convertScene(SceneVO sceneVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param sceneList
     * @return 场景联动集合
     */
    List<SceneVO> convertSceneVOList(List<Scene> sceneList);

    /**
     * VO类转换为实体类
     *
     * @param sceneVOList
     * @return 场景联动集合
     */
    List<Scene> convertSceneList(List<SceneVO> sceneVOList);

    /**
     * 分页转换
     * @param scenePage
     * @return
     */
    Page<SceneVO> convertSceneVOPage(Page<Scene> scenePage);
}
