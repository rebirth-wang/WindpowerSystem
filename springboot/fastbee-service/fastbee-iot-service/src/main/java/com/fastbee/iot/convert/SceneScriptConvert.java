package com.fastbee.iot.convert;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.iot.domain.SceneScript;
import com.fastbee.iot.model.vo.SceneScriptVO;

/**
 * 场景脚本Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-11-13
 */
@Mapper
public interface SceneScriptConvert
{

    SceneScriptConvert INSTANCE = Mappers.getMapper(SceneScriptConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param sceneScript
     * @return 场景脚本集合
     */
    SceneScriptVO convertSceneScriptVO(SceneScript sceneScript);

    /**
     * VO类转换为实体类集合
     *
     * @param sceneScriptVO
     * @return 场景脚本集合
     */
    SceneScript convertSceneScript(SceneScriptVO sceneScriptVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param sceneScriptList
     * @return 场景脚本集合
     */
    List<SceneScriptVO> convertSceneScriptVOList(List<SceneScript> sceneScriptList);

    /**
     * VO类转换为实体类
     *
     * @param sceneScriptVOList
     * @return 场景脚本集合
     */
    List<SceneScript> convertSceneScriptList(List<SceneScriptVO> sceneScriptVOList);
}
