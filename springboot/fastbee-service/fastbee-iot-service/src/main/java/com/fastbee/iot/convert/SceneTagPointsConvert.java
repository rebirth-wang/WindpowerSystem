package com.fastbee.iot.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.iot.domain.SceneTagPoints;
import com.fastbee.iot.model.vo.SceneTagPointsVO;

/**
 * 运算型变量点Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-12-26
 */
@Mapper
public interface SceneTagPointsConvert
{
    /** 代码生成区域 可直接覆盖**/
    SceneTagPointsConvert INSTANCE = Mappers.getMapper(SceneTagPointsConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param sceneTagPoints
     * @return 运算型变量点集合
     */
    SceneTagPointsVO convertSceneTagPointsVO(SceneTagPoints sceneTagPoints);

    /**
     * VO类转换为实体类集合
     *
     * @param sceneTagPointsVO
     * @return 运算型变量点集合
     */
    SceneTagPoints convertSceneTagPoints(SceneTagPointsVO sceneTagPointsVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param sceneTagPointsList
     * @return 运算型变量点集合
     */
    List<SceneTagPointsVO> convertSceneTagPointsVOList(List<SceneTagPoints> sceneTagPointsList);

    /**
     * VO类转换为实体类
     *
     * @param sceneTagPointsVOList
     * @return 运算型变量点集合
     */
    List<SceneTagPoints> convertSceneTagPointsList(List<SceneTagPointsVO> sceneTagPointsVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param sceneTagPointsPage
     * @return 运算型变量点分页
     */
    Page<SceneTagPointsVO> convertSceneTagPointsVOPage(Page<SceneTagPoints> sceneTagPointsPage);

    /**
     * VO类转换为实体类
     *
     * @param sceneTagPointsVOPage
     * @return 运算型变量点分页
     */
    Page<SceneTagPoints> convertSceneTagPointsPage(Page<SceneTagPointsVO> sceneTagPointsVOPage);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
