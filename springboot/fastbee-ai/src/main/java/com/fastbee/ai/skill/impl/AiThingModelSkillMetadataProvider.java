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
 * 物模型生成技能元数据提供者。
 */
@Service
public class AiThingModelSkillMetadataProvider implements IAiSkillMetadataProvider {

    @Resource
    private FastBeeAiProperties properties;

    @Override
    public List<AiSkillDefinitionVO> listSkillDefinitions() {
        boolean enabled = properties.isEnabled()
                && properties.getRuntime() != null
                && properties.getRuntime().isChatEnabled();
        AiSkillDefinitionVO definition = new AiSkillDefinitionVO();
        definition.setSkillCategory("THING_MODEL");
        definition.setSkillCode("THING_MODEL_GENERATE");
        definition.setSkillName("物模型生成");
        definition.setChatMode(AiChatMode.THING_MODEL_GENERATE.name());
        definition.setDescription("解析客户上传的点位表、设备说明或字段清单，提取物模型并生成导入 Excel。");
        definition.setEnabled(enabled);
        definition.setBuiltin(Boolean.TRUE);
        definition.setSortNum(60);
        return Collections.singletonList(definition);
    }
}
