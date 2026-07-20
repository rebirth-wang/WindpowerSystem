package com.fastbee.ai.service;

import java.util.List;

import com.fastbee.ai.model.dto.AiChatSendRequest;
import com.fastbee.ai.model.vo.AiChatStreamEventVO;
import com.fastbee.ai.model.vo.AiClarifyOptionVO;
import com.fastbee.ai.model.vo.AiClarifyPayloadVO;
import com.fastbee.ai.model.vo.AiResumeSelectionVO;

/**
 * AI 澄清与续跑支撑服务。
 */
public interface IAiClarifySupportService {

    /**
     * 构建统一澄清载体。
     */
    AiClarifyPayloadVO buildPayload(String clarifyType,
                                    String clarifyKey,
                                    String clarifyTitle,
                                    String displayContent,
                                    String toolName,
                                    String resumeQuestion,
                                    List<AiClarifyOptionVO> options);

    /**
     * 将澄清载体写入流式事件。
     */
    void applyPayload(AiClarifyPayloadVO payload, AiChatStreamEventVO event);

    /**
     * 解析续跑模式。
     */
    String resolveResumeEffectiveMode(AiChatSendRequest request);

    /**
     * 解析续跑选择结果。
     */
    AiResumeSelectionVO resolveResumeSelection(AiChatSendRequest request);

    /**
     * 构建续跑展示消息。
     */
    String buildResumeDisplayMessage(AiChatSendRequest request);

    /**
     * 构建续跑执行问题。
     */
    String buildResumeExecutionQuestion(AiChatSendRequest request);
}
