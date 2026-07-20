package com.fastbee.ai.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.ai.domain.AiModel;
import com.fastbee.ai.model.vo.AiModelVO;

/**
 * AI 模型 Convert。
 */
@Mapper
public interface AiModelConvert {

    AiModelConvert INSTANCE = Mappers.getMapper(AiModelConvert.class);

    AiModelVO convertAiModelVO(AiModel aiModel);

    List<AiModelVO> convertAiModelVOList(List<AiModel> aiModelList);

    Page<AiModelVO> convertAiModelVOPage(Page<AiModel> aiModelPage);
}
