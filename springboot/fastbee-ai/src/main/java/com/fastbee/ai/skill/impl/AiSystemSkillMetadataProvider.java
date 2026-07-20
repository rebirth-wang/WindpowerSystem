package com.fastbee.ai.skill.impl;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fastbee.ai.config.properties.FastBeeAiProperties;
import com.fastbee.ai.model.enums.AiChatMode;
import com.fastbee.ai.model.vo.AiSkillDefinitionVO;
import com.fastbee.ai.skill.IAiSkillMetadataProvider;

/**
 * 系统级技能元数据提供者。
 */
@Service
public class AiSystemSkillMetadataProvider implements IAiSkillMetadataProvider {

    @Resource
    private FastBeeAiProperties properties;

    @Override
    public List<AiSkillDefinitionVO> listSkillDefinitions() {
        List<AiSkillDefinitionVO> definitions = new ArrayList<>();
        boolean aiEnabled = properties.isEnabled() && properties.getRuntime() != null && properties.getRuntime().isChatEnabled();
        definitions.add(buildDefinition("SYSTEM", "AUTO_ROUTER", "自动识别", AiChatMode.AUTO.name(),
                "根据问题意图在平台助手、通用对话、智能问数、设备控制、协议解析和物模型生成之间自动路由。",
                aiEnabled && (properties.getRuntime().isNl2sqlEnabled()
                        || properties.getRuntime().isDeviceControlEnabled()
                        || properties.getRuntime().isProtocolEnabled()), 10));
        definitions.add(buildDefinition("SYSTEM", "PLATFORM_ASSISTANT", "平台助手", AiChatMode.PLATFORM_ASSISTANT.name(),
                "基于已发布平台知识库回答平台功能、菜单路径、配置步骤和操作说明类问题。", aiEnabled, 20));
        definitions.add(buildDefinition("SYSTEM", "GENERAL_CHAT", "通用对话", AiChatMode.GENERAL.name(),
                "与当前选中的模型进行普通多轮对话，不主动注入平台文档知识。", aiEnabled, 30));
        return definitions;
    }

    private AiSkillDefinitionVO buildDefinition(String category, String skillCode, String skillName, String chatMode,
                                                String description, boolean enabled, int sortNum) {
        AiSkillDefinitionVO definition = new AiSkillDefinitionVO();
        definition.setSkillCategory(category);
        definition.setSkillCode(skillCode);
        definition.setSkillName(skillName);
        definition.setChatMode(chatMode);
        definition.setDescription(description);
        definition.setEnabled(enabled);
        definition.setBuiltin(Boolean.TRUE);
        definition.setSortNum(sortNum);
        return definition;
    }
}
