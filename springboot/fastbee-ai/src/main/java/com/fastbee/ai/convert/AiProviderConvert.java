package com.fastbee.ai.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.ai.domain.AiProvider;
import com.fastbee.ai.model.vo.AiProviderVO;

/**
 * AI 厂商 Convert。
 */
@Mapper
public interface AiProviderConvert {

    AiProviderConvert INSTANCE = Mappers.getMapper(AiProviderConvert.class);

    AiProviderVO convertAiProviderVO(AiProvider aiProvider);

    List<AiProviderVO> convertAiProviderVOList(List<AiProvider> aiProviderList);

    Page<AiProviderVO> convertAiProviderVOPage(Page<AiProvider> aiProviderPage);
}
