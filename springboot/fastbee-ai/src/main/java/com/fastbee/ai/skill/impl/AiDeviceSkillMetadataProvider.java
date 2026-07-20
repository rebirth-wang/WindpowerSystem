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
 * 设备控制技能元数据提供者。
 */
@Service
public class AiDeviceSkillMetadataProvider implements IAiSkillMetadataProvider {

    @Resource
    private FastBeeAiProperties properties;

    @Override
    public List<AiSkillDefinitionVO> listSkillDefinitions() {
        boolean enabled = properties.isEnabled()
                && properties.getRuntime() != null
                && properties.getRuntime().isChatEnabled()
                && properties.getRuntime().isDeviceControlEnabled();
        AiSkillDefinitionVO definition = new AiSkillDefinitionVO();
        definition.setSkillCategory("DEVICE");
        definition.setSkillCode("DEVICE_CONTROL");
        definition.setSkillName("设备控制");
        definition.setChatMode(AiChatMode.DEVICE_CONTROL.name());
        definition.setDescription("复用现有设备控制链路，执行指令生成、服务下发和场景联动。");
        definition.setEnabled(enabled);
        definition.setBuiltin(Boolean.TRUE);
        definition.setSortNum(40);
        return Collections.singletonList(definition);
    }
}
