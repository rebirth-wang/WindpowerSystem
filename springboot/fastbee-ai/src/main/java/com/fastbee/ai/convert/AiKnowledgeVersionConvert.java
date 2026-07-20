package com.fastbee.ai.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.ai.domain.AiKnowledgeVersion;
import com.fastbee.ai.model.vo.AiKnowledgeVersionVO;

/**
 * AI 知识库版本 Convert。
 */
@Mapper
public interface AiKnowledgeVersionConvert {

    AiKnowledgeVersionConvert INSTANCE = Mappers.getMapper(AiKnowledgeVersionConvert.class);

    AiKnowledgeVersionVO convertAiKnowledgeVersionVO(AiKnowledgeVersion aiKnowledgeVersion);

    List<AiKnowledgeVersionVO> convertAiKnowledgeVersionVOList(List<AiKnowledgeVersion> aiKnowledgeVersionList);

    Page<AiKnowledgeVersionVO> convertAiKnowledgeVersionVOPage(Page<AiKnowledgeVersion> aiKnowledgeVersionPage);
}
