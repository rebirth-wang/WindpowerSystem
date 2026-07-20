package com.fastbee.ai.skill;

import java.util.List;

import com.fastbee.ai.model.vo.AiSkillDefinitionVO;

/**
 * AI 技能注册中心服务。
 */
public interface IAiSkillRegistryService {

    /**
     * 查询当前已注册的技能列表。
     *
     * @return 技能列表
     */
    List<AiSkillDefinitionVO> listSkillDefinitions();

    /**
     * 按技能编码查询技能定义。
     *
     * @param skillCode 技能编码
     * @return 技能定义，不存在时返回空
     */
    AiSkillDefinitionVO getSkillDefinition(String skillCode);

    /**
     * 判断技能是否已注册且启用。
     *
     * @param skillCode 技能编码
     * @return 是否启用
     */
    boolean isSkillEnabled(String skillCode);
}
