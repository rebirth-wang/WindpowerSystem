package com.fastbee.ai.convert;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.ai.domain.AiProtocolAdaptationArtifact;
import com.fastbee.ai.model.vo.AiProtocolAdaptationArtifactVO;

/**
 * AI 协议适配产物 Convert。
 */
@Mapper
public interface AiProtocolAdaptationArtifactConvert {

    AiProtocolAdaptationArtifactConvert INSTANCE = Mappers.getMapper(AiProtocolAdaptationArtifactConvert.class);

    AiProtocolAdaptationArtifactVO convertAiProtocolAdaptationArtifactVO(AiProtocolAdaptationArtifact artifact);

    List<AiProtocolAdaptationArtifactVO> convertAiProtocolAdaptationArtifactVOList(List<AiProtocolAdaptationArtifact> artifactList);
}
