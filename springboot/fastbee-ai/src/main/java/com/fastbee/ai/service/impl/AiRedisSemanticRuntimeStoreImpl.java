package com.fastbee.ai.service.impl;

import static com.fastbee.ai.support.AiI18nMessageUtils.message;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fastbee.ai.config.properties.FastBeeAiProperties;
import com.fastbee.ai.domain.AiKnowledgeBase;
import com.fastbee.ai.domain.AiKnowledgeVersion;
import com.fastbee.ai.model.vo.AiSemanticFieldVO;
import com.fastbee.ai.model.vo.AiSemanticRuntimeBundleVO;
import com.fastbee.ai.service.IAiSemanticRuntimeStore;
import com.fastbee.ai.support.AiRedisKeyBuilder;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.utils.StringUtils;

/**
 * Redis 运行时语义存储实现。
 */
@Service
public class AiRedisSemanticRuntimeStoreImpl implements IAiSemanticRuntimeStore {

    private static final String STORE_TYPE = "REDIS";
    private static final Long GLOBAL_TENANT_ID = 0L;

    @Resource
    private RedisTemplate<Object, Object> redisTemplate;

    @Resource
    private FastBeeAiProperties fastBeeAiProperties;

    @Override
    public String getStoreType() {
        return STORE_TYPE;
    }

    @Override
    public void publishNl2SqlBundle(AiKnowledgeBase knowledgeBase, AiKnowledgeVersion version, List<AiSemanticFieldVO> fields) {
        if (knowledgeBase == null || version == null) {
            return;
        }
        List<AiSemanticFieldVO> semanticFields = sanitizeFields(fields);
        if (semanticFields.isEmpty()) {
            throw new ServiceException(message("ai.semantic.runtime.publish.fields.required"));
        }
        String prefix = resolveKeyPrefix();
        Long tenantId = resolveTenantId(knowledgeBase, version);
        String kbCode = requireKbCode(knowledgeBase);
        String versionNo = requireVersionNo(version);
        String activeKey = AiRedisKeyBuilder.buildNl2SqlActiveBundleKey(prefix, tenantId, kbCode);
        String versionKey = AiRedisKeyBuilder.buildNl2SqlVersionBundleKey(prefix, tenantId, kbCode, versionNo);
        String versionSetKey = AiRedisKeyBuilder.buildNl2SqlVersionSetKey(prefix, tenantId, kbCode);
        String activeKbSetKey = AiRedisKeyBuilder.buildNl2SqlActiveKbSetKey(prefix, tenantId);

        AiSemanticRuntimeBundleVO bundle = new AiSemanticRuntimeBundleVO();
        bundle.setTenantId(tenantId);
        bundle.setKnowledgeBaseId(knowledgeBase.getKnowledgeBaseId());
        bundle.setKbCode(kbCode);
        bundle.setKbType(knowledgeBase.getKbType());
        bundle.setVersionId(version.getVersionId());
        bundle.setVersionNo(versionNo);
        bundle.setVectorStoreType(getStoreType());
        bundle.setPublishTime(version.getPublishTime());
        bundle.setPublishedBy(version.getPublishedBy());
        bundle.setFieldCount(semanticFields.size());
        bundle.setFields(semanticFields);

        String payload = JSON.toJSONString(bundle);
        redisTemplate.opsForValue().set(versionKey, payload);
        redisTemplate.opsForValue().set(activeKey, payload);
        redisTemplate.opsForSet().add(versionSetKey, versionNo);
        redisTemplate.opsForSet().add(activeKbSetKey, kbCode);
    }

    @Override
    public void deleteNl2SqlVersion(AiKnowledgeBase knowledgeBase, AiKnowledgeVersion version) {
        if (knowledgeBase == null || version == null || StringUtils.isBlank(version.getVersionNo())) {
            return;
        }
        String prefix = resolveKeyPrefix();
        Long tenantId = resolveTenantId(knowledgeBase, version);
        String kbCode = requireKbCode(knowledgeBase);
        String versionKey = AiRedisKeyBuilder.buildNl2SqlVersionBundleKey(prefix, tenantId, kbCode, version.getVersionNo());
        String versionSetKey = AiRedisKeyBuilder.buildNl2SqlVersionSetKey(prefix, tenantId, kbCode);
        String activeKey = AiRedisKeyBuilder.buildNl2SqlActiveBundleKey(prefix, tenantId, kbCode);
        String activeKbSetKey = AiRedisKeyBuilder.buildNl2SqlActiveKbSetKey(prefix, tenantId);

        redisTemplate.delete(versionKey);
        redisTemplate.opsForSet().remove(versionSetKey, version.getVersionNo());

        AiSemanticRuntimeBundleVO activeBundle = readBundle(activeKey);
        if (activeBundle != null && Objects.equals(activeBundle.getVersionId(), version.getVersionId())) {
            redisTemplate.delete(activeKey);
            redisTemplate.opsForSet().remove(activeKbSetKey, kbCode);
        }
    }

    @Override
    public void restoreActiveNl2SqlBundle(AiKnowledgeBase knowledgeBase, AiKnowledgeVersion version) {
        if (knowledgeBase == null || version == null || StringUtils.isBlank(version.getVersionNo())) {
            return;
        }
        String prefix = resolveKeyPrefix();
        Long tenantId = resolveTenantId(knowledgeBase, version);
        String kbCode = requireKbCode(knowledgeBase);
        String versionKey = AiRedisKeyBuilder.buildNl2SqlVersionBundleKey(prefix, tenantId, kbCode, version.getVersionNo());
        String activeKey = AiRedisKeyBuilder.buildNl2SqlActiveBundleKey(prefix, tenantId, kbCode);
        String activeKbSetKey = AiRedisKeyBuilder.buildNl2SqlActiveKbSetKey(prefix, tenantId);
        Object versionPayload = redisTemplate.opsForValue().get(versionKey);
        if (versionPayload == null) {
            return;
        }
        redisTemplate.opsForValue().set(activeKey, versionPayload);
        redisTemplate.opsForSet().add(activeKbSetKey, kbCode);
    }

    @Override
    public void clearActiveNl2SqlBundle(AiKnowledgeBase knowledgeBase) {
        if (knowledgeBase == null || StringUtils.isBlank(knowledgeBase.getKbCode())) {
            return;
        }
        String prefix = resolveKeyPrefix();
        Long tenantId = GLOBAL_TENANT_ID;
        String kbCode = requireKbCode(knowledgeBase);
        String activeKey = AiRedisKeyBuilder.buildNl2SqlActiveBundleKey(prefix, tenantId, kbCode);
        String activeKbSetKey = AiRedisKeyBuilder.buildNl2SqlActiveKbSetKey(prefix, tenantId);
        redisTemplate.delete(activeKey);
        redisTemplate.opsForSet().remove(activeKbSetKey, kbCode);
    }

    @Override
    public List<AiSemanticRuntimeBundleVO> listActiveNl2SqlBundles(Long tenantId) {
        String activeKbSetKey = AiRedisKeyBuilder.buildNl2SqlActiveKbSetKey(resolveKeyPrefix(), tenantId);
        Set<Object> kbCodes = redisTemplate.opsForSet().members(activeKbSetKey);
        if (kbCodes == null || kbCodes.isEmpty()) {
            return Collections.emptyList();
        }
        List<AiSemanticRuntimeBundleVO> result = new ArrayList<>();
        for (Object item : kbCodes) {
            String kbCode = Objects.toString(item, "");
            if (StringUtils.isBlank(kbCode)) {
                continue;
            }
            String activeKey = AiRedisKeyBuilder.buildNl2SqlActiveBundleKey(resolveKeyPrefix(), tenantId, kbCode);
            AiSemanticRuntimeBundleVO bundle = readBundle(activeKey);
            if (bundle != null) {
                result.add(bundle);
            }
        }
        return result;
    }

    private AiSemanticRuntimeBundleVO readBundle(String key) {
        Object cachedValue = redisTemplate.opsForValue().get(key);
        if (cachedValue == null) {
            return null;
        }
        if (cachedValue instanceof AiSemanticRuntimeBundleVO bundle) {
            return bundle;
        }
        if (cachedValue instanceof JSONObject jsonObject) {
            return jsonObject.toJavaObject(AiSemanticRuntimeBundleVO.class);
        }
        if (cachedValue instanceof byte[] bytes) {
            return parseBundlePayload(new String(bytes, StandardCharsets.UTF_8));
        }
        if (cachedValue instanceof CharSequence charSequence) {
            return parseBundlePayload(charSequence.toString());
        }
        return JSON.parseObject(JSON.toJSONString(cachedValue), AiSemanticRuntimeBundleVO.class);
    }

    private AiSemanticRuntimeBundleVO parseBundlePayload(String payload) {
        if (StringUtils.isBlank(payload)) {
            return null;
        }
        String actualPayload = payload.trim();
        if (actualPayload.startsWith("\"") && actualPayload.endsWith("\"")) {
            actualPayload = JSON.parseObject(actualPayload, String.class);
        }
        return JSON.parseObject(actualPayload, AiSemanticRuntimeBundleVO.class);
    }

    private List<AiSemanticFieldVO> sanitizeFields(List<AiSemanticFieldVO> fields) {
        if (fields == null || fields.isEmpty()) {
            return Collections.emptyList();
        }
        return fields.stream()
                .filter(Objects::nonNull)
                .map(field -> {
                    AiSemanticFieldVO clonedField = JSON.parseObject(JSON.toJSONString(field), AiSemanticFieldVO.class);
                    clonedField.setMatchScore(null);
                    return clonedField;
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private Long resolveTenantId(AiKnowledgeBase knowledgeBase, AiKnowledgeVersion version) {
        return GLOBAL_TENANT_ID;
    }

    private String requireKbCode(AiKnowledgeBase knowledgeBase) {
        if (knowledgeBase == null || StringUtils.isBlank(knowledgeBase.getKbCode())) {
            throw new ServiceException(message("ai.semantic.runtime.knowledge.code.required"));
        }
        return knowledgeBase.getKbCode().trim();
    }

    private String requireVersionNo(AiKnowledgeVersion version) {
        if (version == null || StringUtils.isBlank(version.getVersionNo())) {
            throw new ServiceException(message("ai.semantic.runtime.version.no.required"));
        }
        return version.getVersionNo().trim();
    }

    private String resolveKeyPrefix() {
        return fastBeeAiProperties.getNl2sql().getSemanticRedisPrefix();
    }
}
