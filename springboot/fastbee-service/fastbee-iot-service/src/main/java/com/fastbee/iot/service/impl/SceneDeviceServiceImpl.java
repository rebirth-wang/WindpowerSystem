package com.fastbee.iot.service.impl;

import java.util.List;

import jakarta.annotation.Resource;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import com.fastbee.iot.domain.SceneDevice;
import com.fastbee.iot.mapper.SceneDeviceMapper;
import com.fastbee.iot.model.MatchScenes;
import com.fastbee.iot.service.ISceneDeviceService;

/**
 * 场景设备Service业务层处理
 *
 * @author kerwincui
 * @date 2023-12-28
 */
@Service
public class SceneDeviceServiceImpl extends ServiceImpl<SceneDeviceMapper,SceneDevice> implements ISceneDeviceService
{
    @Resource
    private SceneDeviceMapper sceneDeviceMapper;

    /**
     * 查询场景设备列表
     *
     * @param sceneDevice 场景设备
     * @return 场景设备
     */
    @Override
    public List<SceneDevice> selectSceneDeviceList(SceneDevice sceneDevice)
    {
        return sceneDeviceMapper.selectSceneDeviceList(sceneDevice);
    }

    @Override
    public int deleteSceneDeviceBySceneIds(Long[] sceneIds) {
        return sceneDeviceMapper.deleteSceneDeviceBySceneIds(sceneIds);
    }

    @Override
    public int insertSceneDeviceList(List<SceneDevice> sceneDeviceList) {
        return sceneDeviceMapper.insertSceneDeviceList(sceneDeviceList);
    }

    @Override
    public List<MatchScenes> selectTriggerDeviceRelateScenes(SceneDevice sceneDevice) {
        return sceneDeviceMapper.selectTriggerDeviceRelateScenes(sceneDevice);
    }
}
