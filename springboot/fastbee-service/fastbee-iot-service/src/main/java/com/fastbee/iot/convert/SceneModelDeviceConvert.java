package com.fastbee.iot.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.iot.domain.SceneModelDevice;
import com.fastbee.iot.model.vo.SceneModelDeviceVO;

/**
 * 场景管理关联设备Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-12-26
 */
@Mapper
public interface SceneModelDeviceConvert
{
    /** 代码生成区域 可直接覆盖**/
    SceneModelDeviceConvert INSTANCE = Mappers.getMapper(SceneModelDeviceConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param sceneModelDevice
     * @return 场景管理关联设备集合
     */
    SceneModelDeviceVO convertSceneModelDeviceVO(SceneModelDevice sceneModelDevice);

    /**
     * VO类转换为实体类集合
     *
     * @param sceneModelDeviceVO
     * @return 场景管理关联设备集合
     */
    SceneModelDevice convertSceneModelDevice(SceneModelDeviceVO sceneModelDeviceVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param sceneModelDeviceList
     * @return 场景管理关联设备集合
     */
    List<SceneModelDeviceVO> convertSceneModelDeviceVOList(List<SceneModelDevice> sceneModelDeviceList);

    /**
     * VO类转换为实体类
     *
     * @param sceneModelDeviceVOList
     * @return 场景管理关联设备集合
     */
    List<SceneModelDevice> convertSceneModelDeviceList(List<SceneModelDeviceVO> sceneModelDeviceVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param sceneModelDevicePage
     * @return 场景管理关联设备分页
     */
    Page<SceneModelDeviceVO> convertSceneModelDeviceVOPage(Page<SceneModelDevice> sceneModelDevicePage);

    /**
     * VO类转换为实体类
     *
     * @param sceneModelDeviceVOPage
     * @return 场景管理关联设备分页
     */
    Page<SceneModelDevice> convertSceneModelDevicePage(Page<SceneModelDeviceVO> sceneModelDeviceVOPage);

}
