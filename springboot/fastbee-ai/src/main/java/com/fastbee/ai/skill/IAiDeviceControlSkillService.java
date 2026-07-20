package com.fastbee.ai.skill;

import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.extend.core.domin.mq.InvokeReqDto;
import com.fastbee.common.extend.core.domin.mq.MQSendMessageBo;

/**
 * AI 设备控制技能服务接口。
 */
public interface IAiDeviceControlSkillService {

    /**
     * 下发设备服务，不等待设备回执。
     *
     * @param reqDto 下发参数
     * @return 下发结果
     */
    AjaxResult invokeNoReply(InvokeReqDto reqDto);

    /**
     * 下发设备服务，并等待设备回执。
     *
     * @param reqDto 下发参数
     * @return 下发结果
     */
    AjaxResult invokeReply(InvokeReqDto reqDto);

    /**
     * 生成设备协议指令。
     *
     * @param messageBo 指令参数
     * @return 指令内容
     */
    String commandGenerate(MQSendMessageBo messageBo);

    /**
     * 执行场景联动。
     *
     * @param sceneId 场景编号
     */
    void runScene(Long sceneId);
}
