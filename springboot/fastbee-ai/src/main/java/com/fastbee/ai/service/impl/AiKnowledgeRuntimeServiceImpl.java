package com.fastbee.ai.service.impl;

import static com.fastbee.ai.support.AiI18nMessageUtils.message;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import jakarta.annotation.Resource;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fastbee.ai.config.properties.FastBeeAiProperties;
import com.fastbee.ai.domain.AiKnowledgeBase;
import com.fastbee.ai.domain.AiKnowledgeVersion;
import com.fastbee.ai.model.vo.AiKnowledgeRuntimeStatusVO;
import com.fastbee.ai.model.vo.AiKnowledgeRuntimeSyncResultVO;
import com.fastbee.ai.model.vo.AiSemanticFieldVO;
import com.fastbee.ai.model.vo.AiSemanticRuntimeBundleVO;
import com.fastbee.ai.service.IAiKnowledgeBaseService;
import com.fastbee.ai.service.IAiKnowledgeRuntimeService;
import com.fastbee.ai.service.IAiKnowledgeVersionService;
import com.fastbee.ai.service.IAiPlatformDocKnowledgeService;
import com.fastbee.ai.service.IAiProtocolKnowledgeService;
import com.fastbee.ai.service.IAiSemanticNormalizationService;
import com.fastbee.ai.service.IAiSemanticRuntimeStore;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.utils.StringUtils;

/**
 * AI 知识库运行时治理服务实现。
 */
@Service
public class AiKnowledgeRuntimeServiceImpl implements IAiKnowledgeRuntimeService {

    private static final String KB_TYPE_NL2SQL = "NL2SQL_SEMANTIC";
    private static final String KB_TYPE_PROTOCOL = "PROTOCOL_SPEC";
    private static final String KB_TYPE_PLATFORM = "PLATFORM_DOC";
    private static final String KB_TYPE_CODEBASE_GUIDE = "CODEBASE_GUIDE";
    private static final String RUNTIME_SCENE_NL2SQL = "NL2SQL_RUNTIME";
    private static final String RUNTIME_SCENE_PROTOCOL = "PROTOCOL_SNAPSHOT";
    private static final String RUNTIME_SCENE_PLATFORM = "PLATFORM_DOC_RUNTIME";
    private static final String RUNTIME_SCENE_CODEBASE = "CODEBASE_GUIDE_RUNTIME";
    private static final String KNOWLEDGE_STATUS_ENABLED = "0";
    private static final Long GLOBAL_TENANT_ID = 0L;
    private static final String DEFAULT_STORE_TYPE = "REDIS";

    @Resource
    private IAiKnowledgeBaseService aiKnowledgeBaseService;

    @Resource
    private IAiKnowledgeVersionService aiKnowledgeVersionService;

    @Resource
    private IAiSemanticNormalizationService aiSemanticNormalizationService;

    @Resource
    private FastBeeAiProperties fastBeeAiProperties;

    @Autowired(required = false)
    private IAiProtocolKnowledgeService aiProtocolKnowledgeService;

    @Autowired(required = false)
    private IAiPlatformDocKnowledgeService aiPlatformDocKnowledgeService;

    @Autowired(required = false)
    private List<IAiSemanticRuntimeStore> aiSemanticRuntimeStores = Collections.emptyList();

    @Override
    public AiKnowledgeRuntimeStatusVO getRuntimeStatus(Long knowledgeBaseId) {
        AiKnowledgeBase knowledgeBase = requireKnowledgeBase(knowledgeBaseId);
        AiKnowledgeRuntimeStatusVO status = new AiKnowledgeRuntimeStatusVO();
        status.setKnowledgeBaseId(knowledgeBase.getKnowledgeBaseId());
        status.setKbCode(knowledgeBase.getKbCode());
        status.setKbName(knowledgeBase.getKbName());
        status.setKbType(knowledgeBase.getKbType());
        status.setKnowledgeStatus(knowledgeBase.getStatus());
        status.setPublishStatus(knowledgeBase.getPublishStatus());
        status.setActiveVersionId(knowledgeBase.getActiveVersionId());

        if (isNl2SqlKnowledgeBase(knowledgeBase)) {
            return buildNl2SqlRuntimeStatus(knowledgeBase, status);
        }
        if (isProtocolKnowledgeBase(knowledgeBase)) {
            return buildProtocolRuntimeStatus(knowledgeBase, status);
        }
        if (isPlatformKnowledgeBase(knowledgeBase)) {
            return buildPlatformRuntimeStatus(knowledgeBase, status);
        }
        if (isCodebaseKnowledgeBase(knowledgeBase)) {
            return buildCodebaseRuntimeStatus(knowledgeBase, status);
        }
        status.setRuntimeLoaded(Boolean.FALSE);
        status.setRuntimeStoreImplemented(Boolean.FALSE);
        status.setReaderStoreMatched(Boolean.FALSE);
        status.setStoreCheckRequired(Boolean.FALSE);
        status.setConsistent(Boolean.FALSE);
        status.setSummary("当前知识库类型暂未定义统一运行态详情");
        status.getIssues().add("当前知识库类型尚未接入运行态详情模型");
        return status;
    }

    private AiKnowledgeRuntimeStatusVO buildNl2SqlRuntimeStatus(AiKnowledgeBase knowledgeBase,
                                                                AiKnowledgeRuntimeStatusVO status) {
        status.setRuntimeScene(RUNTIME_SCENE_NL2SQL);
        status.setRuntimeTargetName("Redis active bundle");
        status.setStoreCheckRequired(Boolean.TRUE);
        status.setRebuildSupported(Boolean.TRUE);

        AiKnowledgeVersion activeVersion = resolveActiveVersion(knowledgeBase);
        status.setActiveVersionNo(activeVersion == null ? null : activeVersion.getVersionNo());
        status.setSourceFileCount(activeVersion == null ? 0 : defaultCount(activeVersion.getSourceFileCount()));
        status.setBuildSummary(activeVersion == null ? null : activeVersion.getBuildSummary());
        String expectedStoreType = resolveRuntimeStoreType(knowledgeBase, activeVersion);
        String readerStoreType = resolveReaderStoreType();
        List<String> preloadIssues = new ArrayList<>();
        status.setExpectedStoreType(expectedStoreType);
        status.setReaderStoreType(readerStoreType);
        status.setReaderStoreMatched(StringUtils.equalsIgnoreCase(expectedStoreType, readerStoreType));

        IAiSemanticRuntimeStore runtimeStore = resolveSemanticRuntimeStore(readerStoreType);
        status.setRuntimeStoreImplemented(runtimeStore != null);

        if (activeVersion != null) {
            try {
                int expectedFieldCount = resolveNl2SqlExpectedFieldCount(activeVersion);
                status.setExpectedFieldCount(expectedFieldCount);
                status.setExpectedItemCount(expectedFieldCount);
            } catch (Exception ex) {
                int fallbackFieldCount = defaultCount(activeVersion.getMergedItemCount());
                status.setExpectedFieldCount(fallbackFieldCount);
                status.setExpectedItemCount(fallbackFieldCount);
                preloadIssues.add("读取已发布版本快照失败，已回退到版本汇总计数：" + trimMessage(ex.getMessage()));
            }
        } else {
            status.setExpectedFieldCount(0);
            status.setExpectedItemCount(0);
        }

        AiSemanticRuntimeBundleVO runtimeBundle = resolveActiveBundle(runtimeStore, knowledgeBase.getKbCode(), preloadIssues);
        if (runtimeBundle != null) {
            int runtimeFieldCount = runtimeBundle.getFieldCount() == null
                    ? sizeOf(runtimeBundle.getFields()) : runtimeBundle.getFieldCount();
            status.setRuntimeLoaded(Boolean.TRUE);
            status.setRuntimeVersionId(runtimeBundle.getVersionId());
            status.setRuntimeVersionNo(runtimeBundle.getVersionNo());
            status.setRuntimeStoreType(runtimeBundle.getVectorStoreType());
            status.setRuntimeFieldCount(runtimeFieldCount);
            status.setRuntimeItemCount(runtimeFieldCount);
            status.setRuntimePublishTime(runtimeBundle.getPublishTime());
            status.setRuntimePublishedBy(runtimeBundle.getPublishedBy());
        } else {
            status.setRuntimeLoaded(Boolean.FALSE);
            status.setRuntimeFieldCount(0);
            status.setRuntimeItemCount(0);
        }

        evaluateConsistency(status, knowledgeBase, activeVersion, preloadIssues);
        return status;
    }

    private AiKnowledgeRuntimeStatusVO buildProtocolRuntimeStatus(AiKnowledgeBase knowledgeBase,
                                                                  AiKnowledgeRuntimeStatusVO status) {
        status.setRuntimeScene(RUNTIME_SCENE_PROTOCOL);
        status.setRuntimeTargetName("协议消费快照");
        status.setStoreCheckRequired(Boolean.FALSE);
        status.setRebuildSupported(Boolean.FALSE);
        status.setRuntimeStoreImplemented(aiProtocolKnowledgeService != null);
        status.setReaderStoreMatched(Boolean.TRUE);
        status.setExpectedStoreType("结构化快照");
        status.setReaderStoreType("协议知识消费");

        AiKnowledgeVersion activeVersion = resolveActiveVersion(knowledgeBase);
        populateSnapshotRuntimeStatus(status, knowledgeBase, activeVersion, "协议知识消费实现未接入");
        return status;
    }

    private AiKnowledgeRuntimeStatusVO buildPlatformRuntimeStatus(AiKnowledgeBase knowledgeBase,
                                                                  AiKnowledgeRuntimeStatusVO status) {
        status.setRuntimeScene(RUNTIME_SCENE_PLATFORM);
        status.setRuntimeTargetName("平台检索快照");
        status.setStoreCheckRequired(Boolean.FALSE);
        status.setRebuildSupported(Boolean.FALSE);
        status.setRuntimeStoreImplemented(aiPlatformDocKnowledgeService != null);
        status.setReaderStoreMatched(Boolean.TRUE);
        status.setExpectedStoreType("文档切块快照");
        status.setReaderStoreType("平台文档检索");

        AiKnowledgeVersion activeVersion = resolveActiveVersion(knowledgeBase);
        populateSnapshotRuntimeStatus(status, knowledgeBase, activeVersion, "平台文档消费实现未接入");
        return status;
    }

    private AiKnowledgeRuntimeStatusVO buildCodebaseRuntimeStatus(AiKnowledgeBase knowledgeBase,
                                                                  AiKnowledgeRuntimeStatusVO status) {
        status.setRuntimeScene(RUNTIME_SCENE_CODEBASE);
        status.setRuntimeTargetName("源码导航摘要");
        status.setStoreCheckRequired(Boolean.FALSE);
        status.setRebuildSupported(Boolean.FALSE);
        status.setRuntimeStoreImplemented(Boolean.TRUE);
        status.setReaderStoreMatched(Boolean.TRUE);
        status.setExpectedStoreType("源码安全摘要快照");
        status.setReaderStoreType("源码导航检索");

        AiKnowledgeVersion activeVersion = resolveActiveVersion(knowledgeBase);
        populateSnapshotRuntimeStatus(status, knowledgeBase, activeVersion, "源码导航消费实现未接入");
        return status;
    }

    private void populateSnapshotRuntimeStatus(AiKnowledgeRuntimeStatusVO status,
                                               AiKnowledgeBase knowledgeBase,
                                               AiKnowledgeVersion activeVersion,
                                               String implementationMissingIssue) {
        List<String> issues = new ArrayList<>();
        status.setActiveVersionNo(activeVersion == null ? null : activeVersion.getVersionNo());
        status.setSourceFileCount(activeVersion == null ? 0 : defaultCount(activeVersion.getSourceFileCount()));
        status.setBuildSummary(activeVersion == null ? null : activeVersion.getBuildSummary());
        status.setExpectedItemCount(activeVersion == null ? 0 : defaultCount(activeVersion.getMergedItemCount()));
        status.setExpectedFieldCount(status.getExpectedItemCount());

        if (!Boolean.TRUE.equals(status.getRuntimeStoreImplemented())) {
            issues.add(implementationMissingIssue);
        }

        if (activeVersion == null) {
            status.setRuntimeLoaded(Boolean.FALSE);
            status.setRuntimeItemCount(0);
            status.setRuntimeFieldCount(0);
            finalizeStatus(status, issues, "知识库尚未发布版本");
            return;
        }

        status.setRuntimeVersionId(activeVersion.getVersionId());
        status.setRuntimeVersionNo(activeVersion.getVersionNo());
        status.setRuntimeStoreType(activeVersion.getVectorStoreType());
        status.setRuntimePublishTime(activeVersion.getPublishTime());
        status.setRuntimePublishedBy(activeVersion.getPublishedBy());

        SnapshotRuntimeMetrics snapshotMetrics = resolveSnapshotRuntimeMetrics(activeVersion.getSnapshotPath());
        status.setRuntimeLoaded(snapshotMetrics.exists());
        status.setRuntimeItemCount(snapshotMetrics.itemCount());
        status.setRuntimeFieldCount(snapshotMetrics.itemCount());

        if (!snapshotMetrics.exists()) {
            issues.add("已发布版本快照不存在或不可读取");
        } else {
            if (status.getExpectedItemCount() != null
                    && status.getExpectedItemCount() > 0
                    && !Objects.equals(status.getExpectedItemCount(), status.getRuntimeItemCount())) {
                issues.add("运行态条目数与已发布版本不一致：运行态="
                        + status.getRuntimeItemCount() + "，已发布="
                        + status.getExpectedItemCount());
            }
        }

        if (!KNOWLEDGE_STATUS_ENABLED.equals(knowledgeBase.getStatus())) {
            finalizeStatus(status, issues, issues.isEmpty()
                    ? "知识库已停用，当前快照仅供回溯排查"
                    : "知识库已停用，但当前消费状态存在待关注项");
            return;
        }
        finalizeStatus(status, issues, issues.isEmpty()
                ? "当前消费快照与已发布版本一致"
                : "当前消费状态与已发布版本不一致");
    }

    @Override
    public AiKnowledgeRuntimeSyncResultVO rebuildNl2SqlRuntime(Long knowledgeBaseId) {
        AiKnowledgeBase knowledgeBase = requireKnowledgeBase(knowledgeBaseId);
        if (!isNl2SqlKnowledgeBase(knowledgeBase)) {
            throw new ServiceException(message("ai.knowledge.runtime.nl2sql.type.required"));
        }
        if (!KNOWLEDGE_STATUS_ENABLED.equals(knowledgeBase.getStatus())) {
            throw new ServiceException(message("ai.knowledge.runtime.knowledge.disabled.rebuild"));
        }
        AiKnowledgeVersion activeVersion = resolveActiveVersion(knowledgeBase);
        if (activeVersion == null) {
            throw new ServiceException(message("ai.knowledge.runtime.version.required"));
        }
        IAiSemanticRuntimeStore runtimeStore = resolveSemanticRuntimeStore(resolveRuntimeStoreType(knowledgeBase, activeVersion));
        if (runtimeStore == null) {
            throw new ServiceException(message("ai.knowledge.runtime.store.unsupported.rebuild"));
        }

        AiKnowledgeRuntimeSyncResultVO result = new AiKnowledgeRuntimeSyncResultVO();
        result.setKnowledgeBaseId(knowledgeBase.getKnowledgeBaseId());
        result.setKbCode(knowledgeBase.getKbCode());
        result.setKbName(knowledgeBase.getKbName());
        result.setVersionId(activeVersion.getVersionId());
        result.setVersionNo(activeVersion.getVersionNo());
        result.setStoreType(resolveRuntimeStoreType(knowledgeBase, activeVersion));
        result.setStartedTime(new Date());

        List<AiSemanticFieldVO> semanticFields = aiSemanticNormalizationService.listSemanticFieldsByVersion(knowledgeBase, activeVersion);
        runtimeStore.publishNl2SqlBundle(knowledgeBase, activeVersion, semanticFields);

        result.setSuccess(Boolean.TRUE);
        result.setFieldCount(semanticFields.size());
        result.setMessage(message("ai.knowledge.runtime.rebuild.success"));
        result.setFinishedTime(new Date());
        return result;
    }

    private void evaluateConsistency(AiKnowledgeRuntimeStatusVO status,
                                     AiKnowledgeBase knowledgeBase,
                                     AiKnowledgeVersion activeVersion,
                                     List<String> preloadIssues) {
        List<String> issues = preloadIssues == null ? new ArrayList<>() : new ArrayList<>(preloadIssues);
        if (!Boolean.TRUE.equals(status.getRuntimeStoreImplemented())) {
            issues.add("读取侧运行时存储策略未实现：" + defaultIfBlank(status.getReaderStoreType(), "-"));
        }
        if (!Boolean.TRUE.equals(status.getReaderStoreMatched())) {
            issues.add("读取侧运行时存储类型与知识库发布侧不一致：读取侧="
                    + defaultIfBlank(status.getReaderStoreType(), "-") + "，发布侧="
                    + defaultIfBlank(status.getExpectedStoreType(), "-"));
        }
        if (!KNOWLEDGE_STATUS_ENABLED.equals(knowledgeBase.getStatus())) {
            if (Boolean.TRUE.equals(status.getRuntimeLoaded())) {
                issues.add("知识库已停用，但 Redis 运行时 active bundle 仍然存在");
            }
            finalizeStatus(status, issues, issues.isEmpty()
                    ? "知识库已停用，运行时 active bundle 已正确下线"
                    : "知识库已停用，但运行时状态与预期不一致");
            return;
        }
        if (activeVersion == null) {
            if (Boolean.TRUE.equals(status.getRuntimeLoaded())) {
                issues.add("知识库尚未发布版本，但 Redis 运行时 active bundle 仍然存在");
            } else {
                issues.add("知识库尚未发布版本，当前不存在可校验的运行时语义包");
            }
            finalizeStatus(status, issues, "知识库尚未发布版本");
            return;
        }
        if (!Boolean.TRUE.equals(status.getRuntimeLoaded())) {
            issues.add("Redis 运行时 active bundle 不存在");
        } else {
            if (!Objects.equals(status.getRuntimeVersionId(), activeVersion.getVersionId())) {
                issues.add("运行时 active bundle 版本与知识库已发布版本不一致：运行时="
                        + defaultIfBlank(status.getRuntimeVersionNo(), "-") + "，已发布="
                        + defaultIfBlank(activeVersion.getVersionNo(), "-"));
            }
            if (status.getExpectedFieldCount() != null
                    && status.getRuntimeFieldCount() != null
                    && !Objects.equals(status.getExpectedFieldCount(), status.getRuntimeFieldCount())) {
                issues.add("运行时字段数量与当前已发布版本不一致：运行时="
                        + status.getRuntimeFieldCount() + "，已发布="
                        + status.getExpectedFieldCount());
            }
        }
        finalizeStatus(status, issues, issues.isEmpty()
                ? "Redis 运行时语义包与当前已发布版本一致"
                : "Redis 运行时语义包与当前已发布版本不一致");
    }

    private void finalizeStatus(AiKnowledgeRuntimeStatusVO status, List<String> issues, String summary) {
        status.setIssues(issues);
        status.setConsistent(issues.isEmpty());
        status.setSummary(summary);
    }

    private AiKnowledgeBase requireKnowledgeBase(Long knowledgeBaseId) {
        AiKnowledgeBase query = new AiKnowledgeBase();
        query.setKnowledgeBaseId(knowledgeBaseId);
        AiKnowledgeBase knowledgeBase = aiKnowledgeBaseService.selectAiKnowledgeBase(query);
        if (knowledgeBase == null) {
            throw new ServiceException(message("ai.knowledge.base.not.exists.or.deleted"));
        }
        return knowledgeBase;
    }

    private AiKnowledgeVersion resolveActiveVersion(AiKnowledgeBase knowledgeBase) {
        if (knowledgeBase == null || knowledgeBase.getActiveVersionId() == null) {
            return null;
        }
        return aiKnowledgeVersionService.selectAiKnowledgeVersion(knowledgeBase.getActiveVersionId());
    }

    private AiSemanticRuntimeBundleVO resolveActiveBundle(IAiSemanticRuntimeStore runtimeStore,
                                                          String kbCode,
                                                          List<String> preloadIssues) {
        if (runtimeStore == null || StringUtils.isBlank(kbCode)) {
            return null;
        }
        try {
            return runtimeStore.listActiveNl2SqlBundles(GLOBAL_TENANT_ID).stream()
                    .filter(Objects::nonNull)
                    .filter(item -> StringUtils.equalsIgnoreCase(kbCode, item.getKbCode()))
                    .findFirst()
                    .orElse(null);
        } catch (Exception ex) {
            if (preloadIssues != null) {
                preloadIssues.add("读取 Redis 运行时 active bundle 失败：" + trimMessage(ex.getMessage()));
            }
            return null;
        }
    }

    private IAiSemanticRuntimeStore resolveSemanticRuntimeStore(String storeType) {
        if (StringUtils.isBlank(storeType) || aiSemanticRuntimeStores == null || aiSemanticRuntimeStores.isEmpty()) {
            return null;
        }
        for (IAiSemanticRuntimeStore runtimeStore : aiSemanticRuntimeStores) {
            if (runtimeStore != null && runtimeStore.supports(storeType)) {
                return runtimeStore;
            }
        }
        return null;
    }

    private String resolveRuntimeStoreType(AiKnowledgeBase knowledgeBase, AiKnowledgeVersion version) {
        if (version != null && StringUtils.isNotBlank(version.getVectorStoreType())) {
            return version.getVectorStoreType().trim().toUpperCase(Locale.ROOT);
        }
        if (knowledgeBase != null && StringUtils.isNotBlank(knowledgeBase.getVectorStoreType())) {
            return knowledgeBase.getVectorStoreType().trim().toUpperCase(Locale.ROOT);
        }
        return DEFAULT_STORE_TYPE;
    }

    private String resolveReaderStoreType() {
        if (fastBeeAiProperties.getNl2sql() == null
                || StringUtils.isBlank(fastBeeAiProperties.getNl2sql().getSemanticStoreType())) {
            return DEFAULT_STORE_TYPE;
        }
        return fastBeeAiProperties.getNl2sql().getSemanticStoreType().trim().toUpperCase(Locale.ROOT);
    }

    private boolean isNl2SqlKnowledgeBase(AiKnowledgeBase knowledgeBase) {
        return knowledgeBase != null && KB_TYPE_NL2SQL.equalsIgnoreCase(knowledgeBase.getKbType());
    }

    private boolean isProtocolKnowledgeBase(AiKnowledgeBase knowledgeBase) {
        return knowledgeBase != null && KB_TYPE_PROTOCOL.equalsIgnoreCase(knowledgeBase.getKbType());
    }

    private boolean isPlatformKnowledgeBase(AiKnowledgeBase knowledgeBase) {
        return knowledgeBase != null && KB_TYPE_PLATFORM.equalsIgnoreCase(knowledgeBase.getKbType());
    }

    private boolean isCodebaseKnowledgeBase(AiKnowledgeBase knowledgeBase) {
        return knowledgeBase != null && KB_TYPE_CODEBASE_GUIDE.equalsIgnoreCase(knowledgeBase.getKbType());
    }

    private SnapshotRuntimeMetrics resolveSnapshotRuntimeMetrics(String snapshotPath) {
        if (StringUtils.isBlank(snapshotPath)) {
            return new SnapshotRuntimeMetrics(false, 0);
        }
        try {
            Path path = Paths.get(snapshotPath.trim());
            if (!Files.exists(path)) {
                return new SnapshotRuntimeMetrics(false, 0);
            }
            JSONObject snapshot = JSON.parseObject(Files.readString(path, StandardCharsets.UTF_8));
            JSONArray items = snapshot.getJSONArray("items");
            int rowCount = snapshot.getIntValue("rowCount");
            int itemCount = rowCount > 0 ? rowCount : (items == null ? 0 : items.size());
            return new SnapshotRuntimeMetrics(true, itemCount);
        } catch (Exception ex) {
            return new SnapshotRuntimeMetrics(false, 0);
        }
    }

    private int resolveNl2SqlExpectedFieldCount(AiKnowledgeVersion activeVersion) throws Exception {
        if (activeVersion == null) {
            return 0;
        }
        if (StringUtils.isBlank(activeVersion.getSnapshotPath())) {
            return defaultCount(activeVersion.getMergedItemCount());
        }
        Path path = Paths.get(activeVersion.getSnapshotPath().trim());
        if (!Files.exists(path)) {
            return defaultCount(activeVersion.getMergedItemCount());
        }
        JSONObject snapshot = JSON.parseObject(Files.readString(path, StandardCharsets.UTF_8));
        int fieldCount = snapshot.getIntValue("fieldCount");
        if (fieldCount > 0) {
            return fieldCount;
        }
        int rowCount = snapshot.getIntValue("rowCount");
        if (rowCount > 0) {
            return rowCount;
        }
        JSONArray items = snapshot.getJSONArray("items");
        if (items != null && !items.isEmpty()) {
            return items.size();
        }
        return defaultCount(activeVersion.getMergedItemCount());
    }

    private int defaultCount(Integer count) {
        return count == null ? 0 : count;
    }

    private int sizeOf(List<?> list) {
        return list == null ? 0 : list.size();
    }

    private String defaultIfBlank(String value, String defaultValue) {
        return StringUtils.isBlank(value) ? defaultValue : value;
    }

    private String trimMessage(String message) {
        if (StringUtils.isBlank(message)) {
            return "未知异常";
        }
        String actualMessage = message.trim();
        return actualMessage.length() > 200 ? actualMessage.substring(0, 200) : actualMessage;
    }

    private record SnapshotRuntimeMetrics(boolean exists, int itemCount) {
    }
}
