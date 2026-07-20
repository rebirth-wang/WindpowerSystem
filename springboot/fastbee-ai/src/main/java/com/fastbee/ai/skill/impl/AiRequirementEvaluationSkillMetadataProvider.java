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
 * 需求评估技能元数据提供者。
 */
@Service
public class AiRequirementEvaluationSkillMetadataProvider implements IAiSkillMetadataProvider {

    @Resource
    private FastBeeAiProperties properties;

    @Override
    public List<AiSkillDefinitionVO> listSkillDefinitions() {
        boolean enabled = properties.isEnabled()
                && properties.getRuntime() != null
                && properties.getRuntime().isChatEnabled();
        AiSkillDefinitionVO definition = new AiSkillDefinitionVO();
        definition.setSkillCategory("REQUIREMENT");
        definition.setSkillCode("REQUIREMENT_EVALUATION");
        definition.setSkillName("需求评估");
        definition.setChatMode(AiChatMode.REQUIREMENT_EVALUATION.name());
        definition.setDescription("解析客户上传的需求文件，结合平台知识库输出能力匹配、差距建议、风险问题和参考结论。");
        definition.setEnabled(enabled);
        definition.setBuiltin(Boolean.TRUE);
        definition.setSortNum(70);
        return Collections.singletonList(definition);
    }
}
