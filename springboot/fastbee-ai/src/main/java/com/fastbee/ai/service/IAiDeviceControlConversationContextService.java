package com.fastbee.ai.service;

import java.util.List;

import com.fastbee.ai.domain.AiChatMessage;
import com.fastbee.ai.model.vo.AiChatIntentRouteVO;
import com.fastbee.ai.model.vo.AiDeviceControlContextSnapshotVO;
import com.fastbee.ai.model.vo.AiDeviceControlIntentVO;

/**
 * AI 设备控制会话上下文支撑服务。
 */
public interface IAiDeviceControlConversationContextService {

    /**
     * 从历史消息中提取最近一次可复用的设备控制上下文快照。
     *
     * @param historyMessages 历史消息
     * @return 上下文快照
     */
    AiDeviceControlContextSnapshotVO resolveLatestContext(List<AiChatMessage> historyMessages);

    /**
     * 基于会话历史补全当前执行问题中的缺失槽位。
     *
     * @param question         当前执行问题
     * @param intentRouteResult 路由结果
     * @param historyMessages  历史消息
     * @return 补全后的执行问题
     */
    String enrichExecutionQuestion(String question,
                                   AiChatIntentRouteVO intentRouteResult,
                                   List<AiChatMessage> historyMessages);

    /**
     * 根据本次成功控制结果构建设备控制上下文快照。
     *
     * @param intentRouteResult 路由结果
     * @param controlIntent     控制意图
     * @param riskConfirmed     风险确认标记
     * @return 上下文快照
     */
    AiDeviceControlContextSnapshotVO buildContextSnapshot(AiChatIntentRouteVO intentRouteResult,
                                                          AiDeviceControlIntentVO controlIntent,
                                                          String riskConfirmed);

    /**
     * 将设备控制上下文快照附加到当前用户消息的工具结果中。
     *
     * @param userMessage 用户消息
     * @param snapshot    上下文快照
     */
    void attachContextSnapshot(AiChatMessage userMessage, AiDeviceControlContextSnapshotVO snapshot);
}
