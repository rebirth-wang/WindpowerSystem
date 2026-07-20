package com.fastbee.ai.skill.impl;

import static com.fastbee.ai.support.AiI18nMessageUtils.message;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.fastbee.ai.model.vo.AiSkillDefinitionVO;
import com.fastbee.ai.skill.IAiSkillMetadataProvider;
import com.fastbee.ai.skill.IAiSkillRegistryService;
import com.fastbee.common.exception.ServiceException;

/**
 * AI 技能注册中心实现。
 */
@Service
public class AiSkillRegistryServiceImpl implements IAiSkillRegistryService {

    @Resource
    private List<IAiSkillMetadataProvider> aiSkillMetadataProviders;

    @Override
    public List<AiSkillDefinitionVO> listSkillDefinitions() {
        return buildSkillDefinitions();
    }

    @Override
    public AiSkillDefinitionVO getSkillDefinition(String skillCode) {
        if (StringUtils.isBlank(skillCode)) {
            return null;
        }
        return buildSkillDefinitions().stream()
                .filter(item -> skillCode.equals(item.getSkillCode()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean isSkillEnabled(String skillCode) {
        AiSkillDefinitionVO definition = getSkillDefinition(skillCode);
        return definition != null && Boolean.TRUE.equals(definition.getEnabled());
    }

    private List<AiSkillDefinitionVO> buildSkillDefinitions() {
        Map<String, AiSkillDefinitionVO> definitionMap = new LinkedHashMap<>();
        if (aiSkillMetadataProviders == null || aiSkillMetadataProviders.isEmpty()) {
            return new ArrayList<>();
        }
        for (IAiSkillMetadataProvider provider : aiSkillMetadataProviders) {
            List<AiSkillDefinitionVO> definitions = provider.listSkillDefinitions();
            if (definitions == null || definitions.isEmpty()) {
                continue;
            }
            for (AiSkillDefinitionVO definition : definitions) {
                AiSkillDefinitionVO normalizedDefinition = normalizeDefinition(definition);
                AiSkillDefinitionVO oldDefinition = definitionMap.putIfAbsent(normalizedDefinition.getSkillCode(), normalizedDefinition);
                if (oldDefinition != null) {
                    throw new ServiceException(message("ai.skill.registry.duplicate.code", normalizedDefinition.getSkillCode()));
                }
            }
        }
        List<AiSkillDefinitionVO> result = new ArrayList<>(definitionMap.values());
        result.sort(Comparator.comparing(AiSkillDefinitionVO::getSortNum)
                .thenComparing(AiSkillDefinitionVO::getSkillCode));
        return result;
    }

    private AiSkillDefinitionVO normalizeDefinition(AiSkillDefinitionVO source) {
        if (source == null || StringUtils.isBlank(source.getSkillCode())) {
            throw new ServiceException(message("ai.skill.registry.missing.skill.code"));
        }
        AiSkillDefinitionVO target = new AiSkillDefinitionVO();
        target.setSkillCategory(StringUtils.defaultIfBlank(source.getSkillCategory(), "CUSTOM"));
        target.setSkillCode(source.getSkillCode().trim());
        target.setSkillName(StringUtils.defaultIfBlank(source.getSkillName(), source.getSkillCode()));
        target.setChatMode(StringUtils.defaultString(source.getChatMode()));
        target.setDescription(StringUtils.defaultString(source.getDescription()));
        target.setEnabled(Boolean.TRUE.equals(source.getEnabled()));
        target.setBuiltin(source.getBuiltin() == null || source.getBuiltin());
        target.setSortNum(source.getSortNum() == null ? 9999 : source.getSortNum());
        return target;
    }
}
