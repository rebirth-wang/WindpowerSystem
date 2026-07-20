package com.fastbee.ai.skill.impl;

import java.util.Collections;
import java.util.List;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fastbee.ai.config.properties.FastBeeAiProperties;
import com.fastbee.ai.model.enums.AiChatMode;
import com.fastbee.ai.model.vo.AiSkillDefinitionVO;
import com.fastbee.ai.skill.IAiSkillMetadataProvider;

/**
 * 协议解析技能元数据提供者。
 */
@Service
public class AiProtocolSkillMetadataProvider implements IAiSkillMetadataProvider {

    @Resource
    private FastBeeAiProperties properties;

    @Override
    public List<AiSkillDefinitionVO> listSkillDefinitions() {
        boolean enabled = properties.isEnabled()
                && properties.getRuntime() != null
                && properties.getRuntime().isChatEnabled()
                && properties.getRuntime().isProtocolEnabled();
        AiSkillDefinitionVO definition = new AiSkillDefinitionVO();
        definition.setSkillCategory("PROTOCOL");
        definition.setSkillCode("PROTOCOL_PARSE");
        definition.setSkillName("协议解析");
        definition.setChatMode(AiChatMode.PROTOCOL_PARSE.name());
        definition.setDescription("用于协议文档理解、协议骨架生成和协议知识检索的统一入口能力。");
        definition.setEnabled(enabled);
        definition.setBuiltin(Boolean.TRUE);
        definition.setSortNum(50);
        return Collections.singletonList(definition);
    }
}
