package com.fastbee.ai.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.stereotype.Service;

import com.fastbee.ai.domain.AiChatMessage;
import com.fastbee.ai.model.vo.AiChatIntentRouteVO;
import com.fastbee.ai.model.vo.AiNl2SqlAuditTrailVO;
import com.fastbee.ai.model.vo.AiNl2SqlContextSnapshotVO;
import com.fastbee.ai.model.vo.AiNl2SqlGenerateResultVO;
import com.fastbee.ai.model.vo.AiQueryPlanVO;
import com.fastbee.ai.service.IAiNl2SqlConversationContextService;
import com.fastbee.ai.support.AiCandidateMatchSupport;
import com.fastbee.common.utils.StringUtils;

/**
 * AI 智能问数会话私有上下文支撑服务实现。
 */
@Service
public class AiNl2SqlConversationContextServiceImpl implements IAiNl2SqlConversationContextService {

    private static final String CONTEXT_SNAPSHOT_KEY = "nl2sqlContextSnapshot";

    private static final Pattern SERIAL_NUMBER_PATTERN = Pattern.compile(
            "(?:serialNumber|deviceNumber|设备编号)\\s*[=:：]\\s*([^\\s,，；;]+)",
            Pattern.CASE_INSENSITIVE
    );

    private static final List<String> FOLLOW_UP_TOKENS = List.of(
            "继续", "刚才", "上次", "还是", "同样", "相同", "上述", "这个", "该", "它", "那台", "那条"
    );

    private static final List<String> TIME_RANGE_TOKENS = List.of(
            "今天", "今日", "昨天", "昨日", "本周", "本月", "近", "最近", "小时", "天", "周", "月", "当前", "实时", "最新"
    );

    @Override
    public AiNl2SqlContextSnapshotVO resolveLatestContext(List<AiChatMessage> historyMessages) {
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
            AiNl2SqlContextSnapshotVO snapshot = snapshotObject.to(AiNl2SqlContextSnapshotVO.class);
            if (isUsableSnapshot(snapshot)) {
                return snapshot;
            }
        }
        return null;
    }

    @Override
    public String enrichExecutionQuestion(String question,
                                          AiChatIntentRouteVO intentRouteResult,
                                          List<AiChatMessage> historyMessages) {
        if (StringUtils.isBlank(question) || containsSqlStatement(question)) {
            return question;
        }
        AiNl2SqlContextSnapshotVO snapshot = resolveLatestContext(historyMessages);
        if (!isUsableSnapshot(snapshot) || !shouldReuseContext(question, intentRouteResult)) {
            return question;
        }
        StringBuilder builder = new StringBuilder(question.trim());
        boolean appended = false;
        if (StringUtils.isNotBlank(snapshot.getSerialNumber())
                && !SERIAL_NUMBER_PATTERN.matcher(question).find()
                && (intentRouteResult == null || StringUtils.isBlank(intentRouteResult.getSerialNumberText()))) {
            builder.append('\n').append("serialNumber=").append(snapshot.getSerialNumber());
            appended = true;
        }
        if (StringUtils.isNotBlank(snapshot.getTimeRangeText()) && !containsTimeRange(question)) {
            builder.append('\n').append("时间范围 ").append(snapshot.getTimeRangeText());
            appended = true;
        }
        return appended ? builder.toString() : question;
    }

    @Override
    public AiNl2SqlContextSnapshotVO buildContextSnapshot(String question, AiNl2SqlGenerateResultVO generateResult) {
        if (generateResult == null) {
            return null;
        }
        AiNl2SqlContextSnapshotVO snapshot = new AiNl2SqlContextSnapshotVO();
        snapshot.setQuestion(trimToNull(question));
        snapshot.setQueryMode(trimToNull(generateResult.getQueryMode()));
        snapshot.setLastSummary(trimToNull(generateResult.getSummary()));
        snapshot.setTimeRangeText(resolveTimeRangeText(question));

        AiNl2SqlAuditTrailVO auditTrail = generateResult.getAuditTrail();
        if (auditTrail != null) {
            snapshot.setQueryMode(defaultText(snapshot.getQueryMode(), auditTrail.getQueryMode()));
            snapshot.setRuntimeSource(trimToNull(auditTrail.getRuntimeSource()));
            snapshot.setPrimarySemantic(trimToNull(auditTrail.getPrimarySemantic()));
            snapshot.setKnowledgeBases(copyList(auditTrail.getKnowledgeBases()));
            snapshot.setMatchedFieldKeys(copyList(auditTrail.getMatchedFieldKeys()));
            snapshot.setRuntimeFieldKeys(copyList(auditTrail.getRuntimeFieldKeys()));
            snapshot.setCandidateIdentifiers(copyList(auditTrail.getCandidateIdentifiers()));
            snapshot.setDeviceId(auditTrail.getDeviceId());
            snapshot.setDeviceName(trimToNull(auditTrail.getDeviceName()));
            snapshot.setSerialNumber(trimToNull(auditTrail.getSerialNumber()));
            snapshot.setProductId(auditTrail.getProductId());
            snapshot.setProductName(trimToNull(auditTrail.getProductName()));
            snapshot.setIdentifier(trimToNull(auditTrail.getIdentifier()));
        }

        AiQueryPlanVO queryPlan = generateResult.getQueryPlan();
        if (queryPlan != null) {
            snapshot.setQueryMode(defaultText(snapshot.getQueryMode(), queryPlan.getQueryMode()));
            snapshot.setRuntimeSource(defaultText(snapshot.getRuntimeSource(), queryPlan.getRuntimeSource()));
            snapshot.setPrimarySemantic(defaultText(snapshot.getPrimarySemantic(), queryPlan.getPrimarySemantic()));
            if (snapshot.getKnowledgeBases().isEmpty()) {
                snapshot.setKnowledgeBases(copyList(queryPlan.getKnowledgeBases()));
            }
            if (snapshot.getMatchedFieldKeys().isEmpty()) {
                snapshot.setMatchedFieldKeys(copyList(queryPlan.getMatchedFieldKeys()));
            }
            if (snapshot.getRuntimeFieldKeys().isEmpty()) {
                snapshot.setRuntimeFieldKeys(copyList(queryPlan.getRuntimeFieldKeys()));
            }
            if (snapshot.getCandidateIdentifiers().isEmpty()) {
                snapshot.setCandidateIdentifiers(copyList(queryPlan.getCandidateIdentifiers()));
            }
        }
        return isUsableSnapshot(snapshot) ? snapshot : null;
    }

    @Override
    public String buildContextSummary(List<AiChatMessage> historyMessages) {
        AiNl2SqlContextSnapshotVO snapshot = resolveLatestContext(historyMessages);
        if (!isUsableSnapshot(snapshot)) {
            return null;
        }
        StringBuilder summary = new StringBuilder();
        if (StringUtils.isNotBlank(snapshot.getDeviceName()) || StringUtils.isNotBlank(snapshot.getSerialNumber())) {
            summary.append("最近一次问数对象：");
            if (StringUtils.isNotBlank(snapshot.getDeviceName())) {
                summary.append(snapshot.getDeviceName());
            }
            if (StringUtils.isNotBlank(snapshot.getSerialNumber())) {
                if (summary.charAt(summary.length() - 1) != '：') {
                    summary.append(" / ");
                }
                summary.append(snapshot.getSerialNumber());
            }
        }
        if (StringUtils.isNotBlank(snapshot.getPrimarySemantic())) {
            appendSummaryPart(summary, "最近一次主语义：" + snapshot.getPrimarySemantic());
        }
        if (StringUtils.isNotBlank(snapshot.getIdentifier())) {
            appendSummaryPart(summary, "最近一次标识符：" + snapshot.getIdentifier());
        }
        if (StringUtils.isNotBlank(snapshot.getTimeRangeText())) {
            appendSummaryPart(summary, "最近一次时间范围：" + snapshot.getTimeRangeText());
        }
        return summary.length() == 0 ? null : summary.toString();
    }

    @Override
    public void attachContextSnapshot(AiChatMessage userMessage, AiNl2SqlContextSnapshotVO snapshot) {
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

    private boolean shouldReuseContext(String question, AiChatIntentRouteVO intentRouteResult) {
        if (StringUtils.isBlank(question)) {
            return false;
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
        return intentRouteResult != null
                && StringUtils.isBlank(intentRouteResult.getSerialNumberText())
                && StringUtils.isBlank(intentRouteResult.getDeviceNameText())
                && StringUtils.isBlank(intentRouteResult.getProductNameText())
                && StringUtils.isNotBlank(intentRouteResult.getThingModelText())
                && !containsTimeRange(question);
    }

    private boolean containsTimeRange(String question) {
        if (StringUtils.isBlank(question)) {
            return false;
        }
        for (String token : TIME_RANGE_TOKENS) {
            if (question.contains(token)) {
                return true;
            }
        }
        return false;
    }

    private String resolveTimeRangeText(String question) {
        if (StringUtils.isBlank(question)) {
            return null;
        }
        String normalizedQuestion = question.trim();
        if (containsAny(normalizedQuestion, "今天", "今日")) {
            return "今天";
        }
        if (containsAny(normalizedQuestion, "昨天", "昨日")) {
            return "昨天";
        }
        if (containsAny(normalizedQuestion, "本周")) {
            return "本周";
        }
        if (containsAny(normalizedQuestion, "本月")) {
            return "本月";
        }
        java.util.regex.Matcher matcher = Pattern.compile("(最近|近)\\s*([0-9一二三四五六七八九十两]+)\\s*(分钟|小时|天|周|月)").matcher(normalizedQuestion);
        if (matcher.find()) {
            return "最近" + matcher.group(2) + matcher.group(3);
        }
        if (containsAny(normalizedQuestion, "当前", "实时", "现在", "此刻", "最新")) {
            return "当前";
        }
        return null;
    }

    private boolean isUsableSnapshot(AiNl2SqlContextSnapshotVO snapshot) {
        if (snapshot == null) {
            return false;
        }
        return StringUtils.isNotBlank(snapshot.getSerialNumber())
                || StringUtils.isNotBlank(snapshot.getPrimarySemantic())
                || StringUtils.isNotBlank(snapshot.getIdentifier())
                || StringUtils.isNotBlank(snapshot.getTimeRangeText())
                || (snapshot.getCandidateIdentifiers() != null && !snapshot.getCandidateIdentifiers().isEmpty());
    }

    private boolean containsSqlStatement(String question) {
        return StringUtils.isNotBlank(question) && question.toLowerCase().contains("select");
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

    private boolean containsAny(String text, String... keywords) {
        if (StringUtils.isBlank(text) || keywords == null) {
            return false;
        }
        for (String keyword : keywords) {
            if (StringUtils.isNotBlank(keyword) && text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private String defaultText(String preferred, String fallback) {
        return StringUtils.isNotBlank(preferred) ? preferred : trimToNull(fallback);
    }

    private String trimToNull(String value) {
        return StringUtils.isBlank(value) ? null : value.trim();
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
}
