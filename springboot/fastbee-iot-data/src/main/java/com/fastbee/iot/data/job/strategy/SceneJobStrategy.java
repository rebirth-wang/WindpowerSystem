package com.fastbee.iot.data.job.strategy;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Component;

import com.fastbee.common.extend.enums.DeviceJobTypeEnum;
import com.fastbee.iot.data.service.IRuleEngine;
import com.fastbee.iot.domain.DeviceJob;

/**
 * 场景运算
 *
 * @author gsb
 * @date 2025/3/18 15:42
 */
@Component
public class SceneJobStrategy implements JobInvokeStrategy {

    @Resource
    private IRuleEngine ruleEngine;

    @Override
    public boolean supports(int jobType) {
        return DeviceJobTypeEnum.SCENE.getType() == jobType;
    }

    @Override
    public void invoke(DeviceJob job) {
        System.out.println("------------------[定时执行场景联动]---------------------");
        ruleEngine.ruleMatchBySceneId(job.getSceneId());
    }
}
