package com.fastbee.ai.service;

import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import com.fastbee.ai.model.dto.AiChatSendRequest;
import com.fastbee.ai.model.vo.AiChatSendResponseVO;
import com.fastbee.ai.model.vo.AiChatStreamEventVO;

/**
 * AI 基础对话服务。
 */
public interface AiChatService {

    /**
     * 发送普通对话消息。
     *
     * @param request 对话请求
     * @return 对话结果
     */
    AiChatSendResponseVO send(AiChatSendRequest request);

    /**
     * 发送 AI 流式对话消息。
     *
     * @param request 对话请求
     * @return 流式事件
     */
    Flux<AiChatStreamEventVO> sendStream(AiChatSendRequest request);

    /**
     * 发送带附件上下文的 AI 流式对话消息。
     *
     * @param request 会话请求
     * @param file    上传附件
     * @return 流式事件
     */
    Flux<AiChatStreamEventVO> sendStreamWithAttachment(AiChatSendRequest request, MultipartFile file);

    /**
     * 继续执行上一轮澄清后的流式对话。
     *
     * @param request 恢复执行请求
     * @return 流式事件
     */
    Flux<AiChatStreamEventVO> resumeStream(AiChatSendRequest request);

    /**
     * 在 AI 会话中流式上传协议说明文件并触发协议适配自动编排。
     *
     * @param request 会话请求
     * @param file    协议说明文件
     * @return 流式事件
     */
    Flux<AiChatStreamEventVO> uploadProtocolDocumentStream(AiChatSendRequest request, MultipartFile file);

    /**
     * 在 AI 会话中流式上传设备资料并生成物模型导入模板。
     *
     * @param request 会话请求
     * @param file    设备资料文件
     * @return 流式事件
     */
    Flux<AiChatStreamEventVO> uploadThingModelDocumentStream(AiChatSendRequest request, MultipartFile file);

    /**
     * 在 AI 会话中流式上传需求文件并生成初步评估结果。
     *
     * @param request 会话请求
     * @param file    需求文件
     * @return 流式事件
     */
    Flux<AiChatStreamEventVO> uploadRequirementEvaluationDocumentStream(AiChatSendRequest request, MultipartFile file);
}
