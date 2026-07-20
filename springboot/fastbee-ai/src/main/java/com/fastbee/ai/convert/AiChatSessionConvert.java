package com.fastbee.ai.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.ai.domain.AiChatSession;
import com.fastbee.ai.model.vo.AiChatSessionVO;

/**
 * AI 会话 Convert。
 */
@Mapper
public interface AiChatSessionConvert {

    AiChatSessionConvert INSTANCE = Mappers.getMapper(AiChatSessionConvert.class);

    AiChatSessionVO convertAiChatSessionVO(AiChatSession aiChatSession);

    List<AiChatSessionVO> convertAiChatSessionVOList(List<AiChatSession> aiChatSessionList);

    Page<AiChatSessionVO> convertAiChatSessionVOPage(Page<AiChatSession> aiChatSessionPage);
}
