package com.fastbee.iot.data.job.strategy;

import jakarta.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.fastbee.common.extend.enums.DeviceJobTypeEnum;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.data.service.IDataHandler;
import com.fastbee.iot.domain.DeviceJob;

/**
 * 场景运算
 * @author gsb
 * @date 2025/3/18 15:42
 */
@Slf4j
@Component
public class CalculateJobStrategy implements JobInvokeStrategy{

    @Resource
    private IDataHandler dataHandler;

    @Override
    public boolean supports(int jobType) {
        return DeviceJobTypeEnum.SCENE_MODEL.getType() == jobType;
    }

    @Override
    public void invoke(DeviceJob job) {
        System.out.println("------------------[定时执行场景运算型变量]---------------------");
        String s = dataHandler.calculateSceneModelTagValue(job.getDatasourceId());
        if (StringUtils.isNotEmpty(s)) {
            log.error("定时执行场景运算型变量失败:{}", s);
        }
    }
}
