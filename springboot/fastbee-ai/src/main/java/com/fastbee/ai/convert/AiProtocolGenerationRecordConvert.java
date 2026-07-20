package com.fastbee.ai.convert;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.ai.domain.AiProtocolGenerationRecord;
import com.fastbee.ai.model.vo.AiProtocolGenerationRecordVO;

/**
 * AI 协议代码生成记录 Convert。
 */
@Mapper
public interface AiProtocolGenerationRecordConvert {

    AiProtocolGenerationRecordConvert INSTANCE = Mappers.getMapper(AiProtocolGenerationRecordConvert.class);

    AiProtocolGenerationRecordVO convertAiProtocolGenerationRecordVO(AiProtocolGenerationRecord record);

    List<AiProtocolGenerationRecordVO> convertAiProtocolGenerationRecordVOList(List<AiProtocolGenerationRecord> recordList);
}
