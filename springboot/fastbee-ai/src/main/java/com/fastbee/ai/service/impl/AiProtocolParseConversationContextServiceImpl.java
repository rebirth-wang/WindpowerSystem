package com.fastbee.ai.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.stereotype.Service;

import com.fastbee.ai.domain.AiChatMessage;
import com.fastbee.ai.model.vo.AiProtocolKnowledgeContextVO;
import com.fastbee.ai.model.vo.AiProtocolKnowledgeItemVO;
import com.fastbee.ai.model.vo.AiProtocolParseContextSnapshotVO;
import com.fastbee.ai.service.IAiProtocolParseConversationContextService;
import com.fastbee.ai.support.AiCandidateMatchSupport;
import com.fastbee.common.utils.StringUtils;

/**
 * AI 协议解析会话私有上下文支撑服务实现。
 */
@Service
public class AiProtocolParseConversationContextServiceImpl implements IAiProtocolParseConversationContextService {

    private static final String CONTEXT_SNAPSHOT_KEY = "protocolParseContextSnapshot";

    private static final List<String> FOLLOW_UP_TOKENS = List.of(
            "继续", "刚才", "上次", "上述", "这个", "该", "它", "那条", "那个", "这里", "仍然", "然后"
    );

    @Override
    public AiProtocolParseContextSnapshotVO resolveLatestContext(List<AiChatMessage> historyMessages) {
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
            AiProtocolParseContextSnapshotVO snapshot = snapshotObject.to(AiProtocolParseContextSnapshotVO.class);
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
        AiProtocolParseContextSnapshotVO snapshot = resolveLatestContext(historyMessages);
        if (!isUsableSnapshot(snapshot) || !shouldReuseContext(question)) {
            return question;
        }
        StringBuilder builder = new StringBuilder(question.trim());
        boolean appended = false;
        appended = appendHint(builder, appended, question, "模块 ", snapshot.getModuleName());
        appended = appendHint(builder, appended, question, "字段名称 ", snapshot.getFieldName());
        appended = appendHint(builder, appended, question, "字段编码 ", snapshot.getFieldCode());
        return appended ? builder.toString() : question;
    }

    @Override
    public AiProtocolParseContextSnapshotVO buildContextSnapshot(String question,
                                                                 AiProtocolKnowledgeContextVO context) {
        if (context == null || context.getItems() == null || context.getItems().isEmpty()) {
            return null;
        }
        AiProtocolKnowledgeItemVO firstItem = context.getItems().get(0);
        if (firstItem == null) {
            return null;
        }
        AiProtocolParseContextSnapshotVO snapshot = new AiProtocolParseContextSnapshotVO();
        snapshot.setQuestion(trimToNull(question));
        snapshot.setKbCode(trimToNull(context.getKbCode()));
        snapshot.setKbName(trimToNull(context.getKbName()));
        snapshot.setVersionNo(trimToNull(context.getVersionNo()));
        snapshot.setRuntimeSource(trimToNull(context.getRuntimeSource()));
        snapshot.setModuleName(trimToNull(firstItem.getModuleName()));
        snapshot.setFieldName(trimToNull(firstItem.getFieldName()));
        snapshot.setFieldCode(trimToNull(firstItem.getFieldCode()));
        snapshot.setDataType(trimToNull(firstItem.getDataType()));
        snapshot.setSampleValue(trimToNull(firstItem.getSampleValue()));
        snapshot.setValueMappings(trimToNull(firstItem.getValueMappings()));
        snapshot.setRemark(trimToNull(firstItem.getRemark()));
        snapshot.setAliases(copyList(firstItem.getAliases()));
        return isUsableSnapshot(snapshot) ? snapshot : null;
    }

    @Override
    public String buildContextSummary(List<AiChatMessage> historyMessages) {
        AiProtocolParseContextSnapshotVO snapshot = resolveLatestContext(historyMessages);
        if (!isUsableSnapshot(snapshot)) {
            return null;
        }
        StringBuilder summary = new StringBuilder();
        if (StringUtils.isNotBlank(snapshot.getModuleName())) {
            summary.append("最近一次协议模块：").append(snapshot.getModuleName());
        }
        if (StringUtils.isNotBlank(snapshot.getFieldName())) {
            appendSummaryPart(summary, "最近一次字段名称：" + snapshot.getFieldName());
        }
        if (StringUtils.isNotBlank(snapshot.getFieldCode())) {
            appendSummaryPart(summary, "最近一次字段编码：" + snapshot.getFieldCode());
        }
        if (StringUtils.isNotBlank(snapshot.getDataType())) {
            appendSummaryPart(summary, "最近一次数据类型：" + snapshot.getDataType());
        }
        return summary.length() == 0 ? null : summary.toString();
    }

    @Override
    public void attachContextSnapshot(AiChatMessage userMessage, AiProtocolParseContextSnapshotVO snapshot) {
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

    private boolean appendHint(StringBuilder builder,
                               boolean appended,
                               String originalQuestion,
                               String prefix,
                               String value) {
        if (StringUtils.isBlank(value) || containsIgnoreCase(originalQuestion, value)) {
            return appended;
        }
        builder.append('\n').append(prefix).append(value);
        return true;
    }

    private boolean isUsableSnapshot(AiProtocolParseContextSnapshotVO snapshot) {
        if (snapshot == null) {
            return false;
        }
        return StringUtils.isNotBlank(snapshot.getModuleName())
                || StringUtils.isNotBlank(snapshot.getFieldName())
                || StringUtils.isNotBlank(snapshot.getFieldCode())
                || StringUtils.isNotBlank(snapshot.getDataType());
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

    private List<String> copyList(List<String> source) {
        return source == null ? new ArrayList<>() : new ArrayList<>(source);
    }

    private boolean containsIgnoreCase(String text, String fragment) {
        if (StringUtils.isBlank(text) || StringUtils.isBlank(fragment)) {
            return false;
        }
        return text.toLowerCase().contains(fragment.toLowerCase());
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
