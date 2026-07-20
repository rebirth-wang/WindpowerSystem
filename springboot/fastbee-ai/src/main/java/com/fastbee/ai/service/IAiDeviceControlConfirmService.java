package com.fastbee.ai.service;

import com.fastbee.ai.model.dto.AiChatSendRequest;
import com.fastbee.ai.model.vo.AiChatIntentRouteVO;
import com.fastbee.ai.model.vo.AiClarifyPayloadVO;
import com.fastbee.ai.model.vo.AiDeviceControlIntentVO;

/**
 * AI 设备控制确认策略服务。
 */
public interface IAiDeviceControlConfirmService {

    /**
     * 构建设备控制高风险确认载体。
     */
    AiClarifyPayloadVO buildRiskConfirmPayload(String question,
                                              AiChatIntentRouteVO intentRouteResult,
                                              AiDeviceControlIntentVO controlIntent);

    /**
     * 当前问题是否已完成风险确认。
     */
    boolean isRiskConfirmedQuestion(String question);

    /**
     * 当前续跑选择是否确认继续执行。
     */
    boolean isApprovedSelection(AiChatSendRequest request);

    /**
     * 当前续跑选择是否取消执行。
     */
    boolean isRejectedSelection(AiChatSendRequest request);

    /**
     * 构建取消执行后的回复内容。
     */
    String buildRejectedAnswer(AiChatSendRequest request);
}
