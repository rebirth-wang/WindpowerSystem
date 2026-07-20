package com.fastbee.iot.convert;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.iot.domain.SceneDevice;
import com.fastbee.iot.model.vo.SceneDeviceVO;

/**
 * 场景设备Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-11-13
 */
@Mapper
public interface SceneDeviceConvert
{

    SceneDeviceConvert INSTANCE = Mappers.getMapper(SceneDeviceConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param sceneDevice
     * @return 场景设备集合
     */
    SceneDeviceVO convertSceneDeviceVO(SceneDevice sceneDevice);

    /**
     * VO类转换为实体类集合
     *
     * @param sceneDeviceVO
     * @return 场景设备集合
     */
    SceneDevice convertSceneDevice(SceneDeviceVO sceneDeviceVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param sceneDeviceList
     * @return 场景设备集合
     */
    List<SceneDeviceVO> convertSceneDeviceVOList(List<SceneDevice> sceneDeviceList);

    /**
     * VO类转换为实体类
     *
     * @param sceneDeviceVOList
     * @return 场景设备集合
     */
    List<SceneDevice> convertSceneDeviceList(List<SceneDeviceVO> sceneDeviceVOList);
}
