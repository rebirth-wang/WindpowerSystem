package com.fastbee.ai.skill;

import java.util.List;

import com.fastbee.ai.model.vo.AiSkillDefinitionVO;

/**
 * AI 技能元数据提供者。
 */
public interface IAiSkillMetadataProvider {

    /**
     * 提供当前模块声明的技能列表。
     *
     * @return 技能元数据列表
     */
    List<AiSkillDefinitionVO> listSkillDefinitions();
}
