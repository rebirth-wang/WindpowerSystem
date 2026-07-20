package com.fastbee.ai.skill.impl;

import java.util.List;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fastbee.ai.model.vo.AiSkillDefinitionVO;
import com.fastbee.ai.skill.IAiSkillCatalogService;
import com.fastbee.ai.skill.IAiSkillRegistryService;

/**
 * AI 技能目录服务实现。
 */
@Service
public class AiSkillCatalogServiceImpl implements IAiSkillCatalogService {

    @Resource
    private IAiSkillRegistryService aiSkillRegistryService;

    @Override
    public List<AiSkillDefinitionVO> listSkillDefinitions() {
        return aiSkillRegistryService.listSkillDefinitions();
    }
}
