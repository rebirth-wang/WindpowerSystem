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
 * 问数技能元数据提供者。
 */
@Service
public class AiDataSkillMetadataProvider implements IAiSkillMetadataProvider {

    @Resource
    private FastBeeAiProperties properties;

    @Override
    public List<AiSkillDefinitionVO> listSkillDefinitions() {
        boolean enabled = properties.isEnabled()
                && properties.getRuntime() != null
                && properties.getRuntime().isChatEnabled()
                && properties.getRuntime().isNl2sqlEnabled()
                && properties.getNl2sql().isEnabled();
        AiSkillDefinitionVO definition = new AiSkillDefinitionVO();
        definition.setSkillCategory("DATA");
        definition.setSkillCode("NL2SQL");
        definition.setSkillName("智能问数");
        definition.setChatMode(AiChatMode.NL2SQL.name());
        definition.setDescription("通过受控问数技能访问现有数据中心能力，并支持在 AI 会话中预览执行只读 SELECT 查询。");
        definition.setEnabled(enabled);
        definition.setBuiltin(Boolean.TRUE);
        definition.setSortNum(30);
        return Collections.singletonList(definition);
    }
}
