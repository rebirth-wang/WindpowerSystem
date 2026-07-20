package com.fastbee.ai.service.impl.runtime;

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
import com.fastbee.ai.service.IAiKnowledgeRuntimeHandler;
import com.fastbee.ai.service.IAiSemanticNormalizationService;
import com.fastbee.ai.service.IAiSemanticRuntimeStore;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.utils.StringUtils;

/**
 * 问数语义库运行态处理器。
 */
@Service
public class AiNl2SqlKnowledgeRuntimeHandler extends AbstractKnowledgeRuntimeHandler implements IAiKnowledgeRuntimeHandler {

    private static final String KB_TYPE_NL2SQL = "NL2SQL_SEMANTIC";
    private static final String RUNTIME_SCENE_NL2SQL = "NL2SQL_RUNTIME";
    private static final Long GLOBAL_TENANT_ID = 0L;

    @Autowired
    private IAiSemanticNormalizationService aiSemanticNormalizationService;

    @Autowired
    private FastBeeAiProperties fastBeeAiProperties;

    @Autowired(required = false)
    private List<IAiSemanticRuntimeStore> aiSemanticRuntimeStores = Collections.emptyList();

    @Override
    public boolean supports(String kbType) {
        return KB_TYPE_NL2SQL.equalsIgnoreCase(kbType);
    }

    @Override
    public boolean supportsRebuild() {
        return true;
    }

    @Override
    public AiKnowledgeRuntimeStatusVO buildStatus(AiKnowledgeBase knowledgeBase, AiKnowledgeVersion activeVersion) {
        AiKnowledgeRuntimeStatusVO status = initStatus(knowledgeBase);
        status.setRuntimeScene(RUNTIME_SCENE_NL2SQL);
        status.setRuntimeTargetName("Redis active bundle");
        status.setStoreCheckRequired(Boolean.TRUE);
        status.setRebuildSupported(Boolean.TRUE);
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

        AiSemanticRuntimeBundleVO runtimeBundle =
                resolveActiveBundle(runtimeStore, knowledgeBase.getKbCode(), preloadIssues);
        if (runtimeBundle != null) {
            int runtimeFieldCount = runtimeBundle.getFieldCount() == null
                    ? sizeOf(runtimeBundle.getFields())
                    : runtimeBundle.getFieldCount();
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

    @Override
    public AiKnowledgeRuntimeSyncResultVO rebuild(AiKnowledgeBase knowledgeBase, AiKnowledgeVersion activeVersion) {
        if (!KNOWLEDGE_STATUS_ENABLED.equals(knowledgeBase.getStatus())) {
            throw new ServiceException(message("ai.knowledge.runtime.knowledge.disabled.rebuild"));
        }
        if (activeVersion == null) {
            throw new ServiceException(message("ai.knowledge.runtime.version.required"));
        }
        IAiSemanticRuntimeStore runtimeStore =
                resolveSemanticRuntimeStore(resolveRuntimeStoreType(knowledgeBase, activeVersion));
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

        List<AiSemanticFieldVO> semanticFields =
                aiSemanticNormalizationService.listSemanticFieldsByVersion(knowledgeBase, activeVersion);
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

    private int sizeOf(List<?> list) {
        return list == null ? 0 : list.size();
    }
}
