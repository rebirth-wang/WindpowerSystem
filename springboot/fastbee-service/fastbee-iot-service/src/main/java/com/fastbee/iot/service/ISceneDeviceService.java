package com.fastbee.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.iot.domain.SceneDevice;
import com.fastbee.iot.model.MatchScenes;

/**
 * 场景设备Service接口
 *
 * @author kerwincui
 * @date 2023-12-28
 */
public interface ISceneDeviceService extends IService<SceneDevice>
{


    /**
     * 查询场景设备列表
     *
     * @param sceneDevice 场景设备
     * @return 场景设备集合
     */
    public List<SceneDevice> selectSceneDeviceList(SceneDevice sceneDevice);


    /**
     * 批量删除场景设备
     *
     * @param sceneIds 需要删除的数据场景ID集合
     * @return 结果
     */
    public int deleteSceneDeviceBySceneIds(Long[] sceneIds);

    /**
     * 批量新增场景脚本
     *
     * @param sceneDeviceList 场景联动触发器集合
     * @return 结果
     */
    public int insertSceneDeviceList(List<SceneDevice> sceneDeviceList);

    /**
     * 查询设备关联的场景列表
     *
     * @param sceneDevice 场景设备
     * @return 场景设备集合
     */
    public List<MatchScenes> selectTriggerDeviceRelateScenes(SceneDevice sceneDevice);

}
