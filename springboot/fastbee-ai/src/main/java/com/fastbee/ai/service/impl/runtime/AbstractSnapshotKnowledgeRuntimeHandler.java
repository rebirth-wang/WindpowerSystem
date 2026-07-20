package com.fastbee.ai.service.impl.runtime;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import com.fastbee.ai.domain.AiKnowledgeBase;
import com.fastbee.ai.domain.AiKnowledgeVersion;
import com.fastbee.ai.model.vo.AiKnowledgeRuntimeStatusVO;

/**
 * 基于已发布快照的知识库运行态处理抽象基类。
 */
public abstract class AbstractSnapshotKnowledgeRuntimeHandler extends AbstractKnowledgeRuntimeHandler {

    @Override
    public AiKnowledgeRuntimeStatusVO buildStatus(AiKnowledgeBase knowledgeBase, AiKnowledgeVersion activeVersion) {
        AiKnowledgeRuntimeStatusVO status = initStatus(knowledgeBase);
        status.setRuntimeScene(runtimeScene());
        status.setRuntimeTargetName(runtimeTargetName());
        status.setStoreCheckRequired(Boolean.FALSE);
        status.setRebuildSupported(Boolean.FALSE);
        status.setRuntimeStoreImplemented(isRuntimeImplemented());
        status.setReaderStoreMatched(Boolean.TRUE);
        status.setExpectedStoreType(expectedStoreType());
        status.setReaderStoreType(readerStoreType());

        List<String> issues = new ArrayList<>();
        status.setActiveVersionNo(activeVersion == null ? null : activeVersion.getVersionNo());
        status.setSourceFileCount(activeVersion == null ? 0 : defaultCount(activeVersion.getSourceFileCount()));
        status.setBuildSummary(activeVersion == null ? null : activeVersion.getBuildSummary());
        status.setExpectedItemCount(activeVersion == null ? 0 : defaultCount(activeVersion.getMergedItemCount()));
        status.setExpectedFieldCount(status.getExpectedItemCount());

        if (!Boolean.TRUE.equals(status.getRuntimeStoreImplemented())) {
            issues.add(missingImplementationIssue());
        }

        appendCustomIssues(issues, knowledgeBase, activeVersion, status);
        if (activeVersion == null) {
            status.setRuntimeLoaded(Boolean.FALSE);
            status.setRuntimeItemCount(0);
            status.setRuntimeFieldCount(0);
            finalizeStatus(status, issues, "知识库尚未发布版本");
            return status;
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
        } else if (status.getExpectedItemCount() != null
                && status.getExpectedItemCount() > 0
                && !Objects.equals(status.getExpectedItemCount(), status.getRuntimeItemCount())) {
            issues.add("运行态条目数与已发布版本不一致：运行态="
                    + status.getRuntimeItemCount() + "，已发布="
                    + status.getExpectedItemCount());
        }

        if (!KNOWLEDGE_STATUS_ENABLED.equals(knowledgeBase.getStatus())) {
            finalizeStatus(status, issues, issues.isEmpty()
                    ? "知识库已停用，当前快照仅供回溯排查"
                    : "知识库已停用，但当前消费状态存在待关注项");
            return status;
        }
        finalizeStatus(status, issues, buildEnabledSummary(issues, knowledgeBase, activeVersion, status));
        return status;
    }

    /**
     * 运行态场景编码。
     *
     * @return 运行态场景
     */
    protected abstract String runtimeScene();

    /**
     * 运行态对象名称。
     *
     * @return 对象名称
     */
    protected abstract String runtimeTargetName();

    /**
     * 发布侧形态文案。
     *
     * @return 形态文案
     */
    protected abstract String expectedStoreType();

    /**
     * 消费侧形态文案。
     *
     * @return 消费侧文案
     */
    protected abstract String readerStoreType();

    /**
     * 当前消费实现是否已经接入。
     *
     * @return 是否接入
     */
    protected abstract boolean isRuntimeImplemented();

    /**
     * 消费实现缺失时的问题描述。
     *
     * @return 问题描述
     */
    protected abstract String missingImplementationIssue();

    /**
     * 在标准问题明细之外补充自定义问题。
     *
     * @param issues        问题列表
     * @param knowledgeBase 知识库
     * @param activeVersion 当前版本
     * @param status        运行态对象
     */
    protected void appendCustomIssues(List<String> issues,
                                      AiKnowledgeBase knowledgeBase,
                                      AiKnowledgeVersion activeVersion,
                                      AiKnowledgeRuntimeStatusVO status) {
        // 默认无扩展问题。
    }

    /**
     * 生成启用状态下的摘要。
     *
     * @param issues        问题列表
     * @param knowledgeBase 知识库
     * @param activeVersion 当前版本
     * @param status        运行态对象
     * @return 摘要
     */
    protected String buildEnabledSummary(List<String> issues,
                                         AiKnowledgeBase knowledgeBase,
                                         AiKnowledgeVersion activeVersion,
                                         AiKnowledgeRuntimeStatusVO status) {
        return issues == null || issues.isEmpty()
                ? "当前消费快照与已发布版本一致"
                : "当前消费状态与已发布版本不一致";
    }

    private SnapshotRuntimeMetrics resolveSnapshotRuntimeMetrics(String snapshotPath) {
        if (com.fastbee.common.utils.StringUtils.isBlank(snapshotPath)) {
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

    /**
     * 快照运行态统计。
     */
    protected static class SnapshotRuntimeMetrics {
        private final boolean exists;
        private final int itemCount;

        protected SnapshotRuntimeMetrics(boolean exists, int itemCount) {
            this.exists = exists;
            this.itemCount = itemCount;
        }

        protected boolean exists() {
            return exists;
        }

        protected int itemCount() {
            return itemCount;
        }
    }
}
