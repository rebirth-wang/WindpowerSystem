package com.fastbee.ai.skill;

import java.util.List;

import com.fastbee.ai.model.vo.AiSkillDefinitionVO;

/**
 * AI 技能目录服务接口。
 */
public interface IAiSkillCatalogService {

    /**
     * 查询当前可用技能列表。
     *
     * @return 技能列表
     */
    List<AiSkillDefinitionVO> listSkillDefinitions();
}
