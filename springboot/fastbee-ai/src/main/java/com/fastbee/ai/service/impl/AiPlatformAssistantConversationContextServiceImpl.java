package com.fastbee.ai.service.impl;

import java.util.List;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.stereotype.Service;

import com.fastbee.ai.domain.AiChatMessage;
import com.fastbee.ai.model.vo.AiPlatformAssistantContextSnapshotVO;
import com.fastbee.ai.model.vo.AiPlatformDocKnowledgeChunkVO;
import com.fastbee.ai.model.vo.AiPlatformDocKnowledgeContextVO;
import com.fastbee.ai.service.IAiPlatformAssistantConversationContextService;
import com.fastbee.ai.support.AiCandidateMatchSupport;
import com.fastbee.common.utils.StringUtils;

/**
 * AI 平台助手会话私有上下文支撑服务实现。
 */
@Service
public class AiPlatformAssistantConversationContextServiceImpl implements IAiPlatformAssistantConversationContextService {

    private static final String CONTEXT_SNAPSHOT_KEY = "platformAssistantContextSnapshot";

    private static final List<String> FOLLOW_UP_TOKENS = List.of(
            "继续", "刚才", "上次", "上述", "这个", "该", "它", "那里", "那个", "那页", "那一步", "然后"
    );

    private static final List<String> EXPLICIT_CONTEXT_TOKENS = List.of(
            "菜单", "页面", "路径", "功能", "模块", "角色", "配置", "步骤"
    );

    @Override
    public AiPlatformAssistantContextSnapshotVO resolveLatestContext(List<AiChatMessage> historyMessages) {
        if (historyMessages == null || historyMessages.isEmpty()) {
            return null;
        }
        for (int index = historyMessages.size() - 1; index >= 0; index--) {
            AiChatMessage item = historyMessages.get(index);
            if (item == null
                    || !"user".equalsIgnoreCase(item.getRoleType())
                    || "FAIL".equalsIgnoreCase(item.getMessageStatus())
                    || StringUtils.isBlank(item.getToolResult())) {
                continue;
            }
            JSONObject root = parseToolResult(item.getToolResult());
            if (root == null || !root.containsKey(CONTEXT_SNAPSHOT_KEY)) {
                continue;
            }
            JSONObject snapshotObject = root.getJSONObject(CONTEXT_SNAPSHOT_KEY);
            if (snapshotObject == null || snapshotObject.isEmpty()) {
                continue;
            }
            AiPlatformAssistantContextSnapshotVO snapshot = snapshotObject.to(AiPlatformAssistantContextSnapshotVO.class);
            if (isUsableSnapshot(snapshot)) {
                return snapshot;
            }
        }
        return null;
    }

    @Override
    public String enrichExecutionQuestion(String question, List<AiChatMessage> historyMessages) {
        if (StringUtils.isBlank(question)) {
            return question;
        }
        AiPlatformAssistantContextSnapshotVO snapshot = resolveLatestContext(historyMessages);
        if (!isUsableSnapshot(snapshot) || !shouldReuseContext(question)) {
            return question;
        }
        StringBuilder builder = new StringBuilder(question.trim());
        boolean appended = false;
        if (StringUtils.isNotBlank(snapshot.getMenuPath())) {
            builder.append('\n').append("菜单路径 ").append(snapshot.getMenuPath());
            appended = true;
        }
        if (StringUtils.isNotBlank(snapshot.getPageTitle())) {
            builder.append('\n').append("页面 ").append(snapshot.getPageTitle());
            appended = true;
        }
        if (StringUtils.isNotBlank(snapshot.getHeadingPath())) {
            builder.append('\n').append("标题路径 ").append(snapshot.getHeadingPath());
            appended = true;
        }
        return appended ? builder.toString() : question;
    }

    @Override
    public AiPlatformAssistantContextSnapshotVO buildContextSnapshot(String question,
                                                                     AiPlatformDocKnowledgeContextVO context) {
        if (context == null || context.getChunks() == null || context.getChunks().isEmpty()) {
            return null;
        }
        AiPlatformDocKnowledgeChunkVO firstChunk = context.getChunks().get(0);
        if (firstChunk == null) {
            return null;
        }
        AiPlatformAssistantContextSnapshotVO snapshot = new AiPlatformAssistantContextSnapshotVO();
        snapshot.setQuestion(trimToNull(question));
        snapshot.setKbCode(trimToNull(context.getKbCode()));
        snapshot.setKbName(trimToNull(context.getKbName()));
        snapshot.setVersionNo(trimToNull(context.getVersionNo()));
        snapshot.setMenuPath(trimToNull(firstChunk.getMenuPath()));
        snapshot.setPageTitle(trimToNull(firstChunk.getPageTitle()));
        snapshot.setHeadingPath(trimToNull(firstChunk.getHeadingPath()));
        snapshot.setSectionName(trimToNull(firstChunk.getSectionName()));
        snapshot.setKnowledgeType(trimToNull(firstChunk.getKnowledgeType()));
        snapshot.setTargetRole(trimToNull(firstChunk.getTargetRole()));
        snapshot.setSourceUrl(trimToNull(firstChunk.getSourceUrl()));
        snapshot.setContentPreview(trimToNull(firstChunk.getContentPreview()));
        return isUsableSnapshot(snapshot) ? snapshot : null;
    }

    @Override
    public String buildContextSummary(List<AiChatMessage> historyMessages) {
        AiPlatformAssistantContextSnapshotVO snapshot = resolveLatestContext(historyMessages);
        if (!isUsableSnapshot(snapshot)) {
            return null;
        }
        StringBuilder summary = new StringBuilder();
        if (StringUtils.isNotBlank(snapshot.getMenuPath())) {
            summary.append("最近一次菜单路径：").append(snapshot.getMenuPath());
        }
        if (StringUtils.isNotBlank(snapshot.getPageTitle())) {
            appendSummaryPart(summary, "最近一次页面：" + snapshot.getPageTitle());
        }
        if (StringUtils.isNotBlank(snapshot.getHeadingPath())) {
            appendSummaryPart(summary, "最近一次标题路径：" + snapshot.getHeadingPath());
        }
        if (StringUtils.isNotBlank(snapshot.getTargetRole())) {
            appendSummaryPart(summary, "最近一次适用角色：" + snapshot.getTargetRole());
        }
        return summary.length() == 0 ? null : summary.toString();
    }

    @Override
    public void attachContextSnapshot(AiChatMessage userMessage, AiPlatformAssistantContextSnapshotVO snapshot) {
        if (userMessage == null || !isUsableSnapshot(snapshot)) {
            return;
        }
        JSONObject root = parseToolResult(userMessage.getToolResult());
        if (root == null) {
            root = new JSONObject();
        }
        root.put(CONTEXT_SNAPSHOT_KEY, JSON.parseObject(JSON.toJSONString(snapshot)));
        userMessage.setToolResult(root.toJSONString());
    }

    private boolean shouldReuseContext(String question) {
        if (StringUtils.isBlank(question)) {
            return false;
        }
        for (String token : EXPLICIT_CONTEXT_TOKENS) {
            if (question.contains(token)) {
                return false;
            }
        }
        String normalizedQuestion = AiCandidateMatchSupport.normalizeText(question);
        if (StringUtils.isBlank(normalizedQuestion)) {
            return false;
        }
        for (String token : FOLLOW_UP_TOKENS) {
            String normalizedToken = AiCandidateMatchSupport.normalizeText(token);
            if (StringUtils.isNotBlank(normalizedToken) && normalizedQuestion.contains(normalizedToken)) {
                return true;
            }
        }
        return false;
    }

    private boolean isUsableSnapshot(AiPlatformAssistantContextSnapshotVO snapshot) {
        if (snapshot == null) {
            return false;
        }
        return StringUtils.isNotBlank(snapshot.getMenuPath())
                || StringUtils.isNotBlank(snapshot.getPageTitle())
                || StringUtils.isNotBlank(snapshot.getHeadingPath())
                || StringUtils.isNotBlank(snapshot.getSectionName());
    }

    private JSONObject parseToolResult(String toolResult) {
        if (StringUtils.isBlank(toolResult)) {
            return new JSONObject();
        }
        try {
            JSONObject object = JSON.parseObject(toolResult);
            return object == null ? new JSONObject() : object;
        } catch (Exception ex) {
            JSONObject object = new JSONObject();
            object.put("rawToolResult", toolResult);
            return object;
        }
    }

    private void appendSummaryPart(StringBuilder summary, String text) {
        if (StringUtils.isBlank(text)) {
            return;
        }
        if (summary.length() > 0) {
            summary.append("；");
        }
        summary.append(text.trim());
    }

    private String trimToNull(String value) {
        return StringUtils.isBlank(value) ? null : value.trim();
    }
}
