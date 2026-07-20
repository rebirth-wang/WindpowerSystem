package com.fastbee.ai.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.ai.domain.AiProtocolAdaptationTask;
import com.fastbee.ai.model.vo.AiProtocolAdaptationTaskVO;

/**
 * AI 协议适配任务 Convert。
 */
@Mapper
public interface AiProtocolAdaptationTaskConvert {

    AiProtocolAdaptationTaskConvert INSTANCE = Mappers.getMapper(AiProtocolAdaptationTaskConvert.class);

    AiProtocolAdaptationTaskVO convertAiProtocolAdaptationTaskVO(AiProtocolAdaptationTask task);

    List<AiProtocolAdaptationTaskVO> convertAiProtocolAdaptationTaskVOList(List<AiProtocolAdaptationTask> taskList);

    Page<AiProtocolAdaptationTaskVO> convertAiProtocolAdaptationTaskVOPage(Page<AiProtocolAdaptationTask> taskPage);
}
