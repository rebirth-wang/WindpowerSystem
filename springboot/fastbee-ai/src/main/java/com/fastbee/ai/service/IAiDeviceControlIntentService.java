package com.fastbee.ai.service;

import com.fastbee.ai.model.vo.AiChatIntentRouteVO;
import com.fastbee.ai.model.vo.AiDeviceControlIntentVO;

/**
 * AI 设备控制意图解析服务。
 */
public interface IAiDeviceControlIntentService {

    /**
     * 解析自然语言设备控制请求，输出设备候选、物模型候选与可执行请求体。
     *
     * @param question 问题内容
     * @param intentRouteResult AI 路由技能结果
     * @return 解析结果
     */
    AiDeviceControlIntentVO resolveIntent(String question, AiChatIntentRouteVO intentRouteResult);
}
