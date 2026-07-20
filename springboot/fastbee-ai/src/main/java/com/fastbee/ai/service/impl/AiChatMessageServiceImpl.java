package com.fastbee.ai.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import com.fastbee.ai.domain.AiChatMessage;
import com.fastbee.ai.mapper.AiChatMessageMapper;
import com.fastbee.ai.service.IAiChatMessageService;
import com.fastbee.common.utils.StringUtils;

/**
 * AI 会话消息服务实现。
 */
@Service
public class AiChatMessageServiceImpl extends ServiceImpl<AiChatMessageMapper, AiChatMessage> implements IAiChatMessageService {

    @Override
    public List<AiChatMessage> listAiChatMessage(AiChatMessage aiChatMessage) {
        return baseMapper.selectList(Wrappers.<AiChatMessage>lambdaQuery()
                .eq(aiChatMessage.getSessionId() != null, AiChatMessage::getSessionId, aiChatMessage.getSessionId())
                .eq(StringUtils.isNotBlank(aiChatMessage.getRoleType()), AiChatMessage::getRoleType, aiChatMessage.getRoleType())
                .eq(StringUtils.isNotBlank(aiChatMessage.getAbilityType()), AiChatMessage::getAbilityType, aiChatMessage.getAbilityType())
                .eq(StringUtils.isNotBlank(aiChatMessage.getProviderCode()), AiChatMessage::getProviderCode, aiChatMessage.getProviderCode())
                .eq(StringUtils.isNotBlank(aiChatMessage.getModelCode()), AiChatMessage::getModelCode, aiChatMessage.getModelCode())
                .orderByAsc(AiChatMessage::getMessageNo)
                .orderByAsc(AiChatMessage::getCreateTime));
    }

    @Override
    public int countBySessionId(Long sessionId) {
        Long count = baseMapper.selectCount(Wrappers.<AiChatMessage>lambdaQuery()
                .eq(AiChatMessage::getSessionId, sessionId));
        return count == null ? 0 : count.intValue();
    }

    @Override
    public int deleteBySessionId(Long sessionId) {
        return baseMapper.delete(Wrappers.<AiChatMessage>lambdaQuery()
                .eq(AiChatMessage::getSessionId, sessionId));
    }
}
