package com.fastbee.ai.model.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

import com.fastbee.ai.domain.AiChatMessage;

/**
 * AI 会话记录详情。
 */
@Data
public class AiChatRecordDetailVO {

    /**
     * 会话信息。
     */
    private AiChatSessionVO session;

    /**
     * 消息列表。
     */
    private List<AiChatMessage> messages = new ArrayList<>();
}
