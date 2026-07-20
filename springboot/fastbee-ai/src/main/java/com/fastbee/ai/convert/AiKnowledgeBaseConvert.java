package com.fastbee.ai.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.ai.domain.AiKnowledgeBase;
import com.fastbee.ai.model.vo.AiKnowledgeBaseVO;

/**
 * AI 知识库 Convert。
 */
@Mapper
public interface AiKnowledgeBaseConvert {

    AiKnowledgeBaseConvert INSTANCE = Mappers.getMapper(AiKnowledgeBaseConvert.class);

    AiKnowledgeBaseVO convertAiKnowledgeBaseVO(AiKnowledgeBase aiKnowledgeBase);

    List<AiKnowledgeBaseVO> convertAiKnowledgeBaseVOList(List<AiKnowledgeBase> aiKnowledgeBaseList);

    Page<AiKnowledgeBaseVO> convertAiKnowledgeBaseVOPage(Page<AiKnowledgeBase> aiKnowledgeBasePage);
}
