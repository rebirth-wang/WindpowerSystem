package com.fastbee.ai.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.ai.domain.AiKnowledgeDocument;
import com.fastbee.ai.model.vo.AiKnowledgeDocumentVO;

/**
 * AI 知识文档 Convert。
 */
@Mapper
public interface AiKnowledgeDocumentConvert {

    AiKnowledgeDocumentConvert INSTANCE = Mappers.getMapper(AiKnowledgeDocumentConvert.class);

    AiKnowledgeDocumentVO convertAiKnowledgeDocumentVO(AiKnowledgeDocument aiKnowledgeDocument);

    List<AiKnowledgeDocumentVO> convertAiKnowledgeDocumentVOList(List<AiKnowledgeDocument> aiKnowledgeDocumentList);

    Page<AiKnowledgeDocumentVO> convertAiKnowledgeDocumentVOPage(Page<AiKnowledgeDocument> aiKnowledgeDocumentPage);
}
