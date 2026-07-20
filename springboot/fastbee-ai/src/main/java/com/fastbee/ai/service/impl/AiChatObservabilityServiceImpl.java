package com.fastbee.ai.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import com.fastbee.ai.domain.AiChatMessage;
import com.fastbee.ai.domain.AiChatSession;
import com.fastbee.ai.mapper.AiChatMessageMapper;
import com.fastbee.ai.mapper.AiChatSessionMapper;
import com.fastbee.ai.model.enums.AiChatMode;
import com.fastbee.ai.model.vo.AiChatIntentRouteVO;
import com.fastbee.ai.model.vo.AiChatObservabilityStatsVO;
import com.fastbee.ai.service.IAiChatObservabilityService;
import com.fastbee.ai.support.AiChatObservabilityConstants;
import com.fastbee.common.utils.StringUtils;

/**
 * AI 会话观测服务实现。
 */
@Service
public class AiChatObservabilityServiceImpl implements IAiChatObservabilityService {

    private static final String ROLE_USER = "user";
    private static final String ROLE_ASSISTANT = "assistant";
    private static final String MESSAGE_STATUS_SUCCESS = "SUCCESS";
    private static final String TOOL_NAME_AUTO_ROUTER_ANALYZE = "auto_router_analyze";
    private static final int DEFAULT_WINDOW_DAYS = 7;
    private static final int MAX_WINDOW_DAYS = 30;
    private static final int SESSION_BATCH_SIZE = 200;

    @Resource
    private AiChatSessionMapper aiChatSessionMapper;

    @Resource
    private AiChatMessageMapper aiChatMessageMapper;

    @Override
    public AiChatObservabilityStatsVO getCurrentUserStats(Long userId, Integer days) {
        int windowDays = normalizeWindowDays(days);
        AiChatObservabilityStatsVO stats = initStats(windowDays);
        if (userId == null) {
            return stats;
        }
        List<Long> sessionIds = aiChatSessionMapper.selectList(Wrappers.<AiChatSession>lambdaQuery()
                        .select(AiChatSession::getSessionId)
                        .eq(AiChatSession::getUserId, userId))
                .stream()
                .map(AiChatSession::getSessionId)
                .filter(id -> id != null)
                .collect(Collectors.toList());
        if (sessionIds.isEmpty()) {
            return stats;
        }
        return buildStats(loadMessages(sessionIds, resolveWindowBeginTime(windowDays)), windowDays);
    }

    AiChatObservabilityStatsVO buildStats(List<AiChatMessage> messages, int windowDays) {
        AiChatObservabilityStatsVO stats = initStats(windowDays);
        if (messages == null || messages.isEmpty()) {
            return stats;
        }
        Map<Long, List<AiChatMessage>> sessionMessageMap = messages.stream()
                .filter(message -> message.getSessionId() != null)
                .sorted(Comparator.comparing(AiChatMessage::getSessionId)
                        .thenComparing(message -> message.getMessageNo() == null ? Integer.MAX_VALUE : message.getMessageNo())
                        .thenComparing(AiChatMessage::getCreateTime, Comparator.nullsLast(Date::compareTo))
                        .thenComparing(message -> message.getMessageId() == null ? Long.MAX_VALUE : message.getMessageId()))
                .collect(Collectors.groupingBy(AiChatMessage::getSessionId, LinkedHashMap::new, Collectors.toList()));

        Set<Long> autoRequestIds = new LinkedHashSet<>();
        Set<Long> correctedAutoRequestIds = new LinkedHashSet<>();
        for (List<AiChatMessage> sessionMessages : sessionMessageMap.values()) {
            for (int index = 0; index < sessionMessages.size(); index++) {
                AiChatMessage userMessage = sessionMessages.get(index);
                if (!ROLE_USER.equalsIgnoreCase(StringUtils.defaultString(userMessage.getRoleType()))) {
                    continue;
                }
                AiChatIntentRouteVO routeAudit = parseRouteAudit(userMessage);
                if (routeAudit == null) {
                    continue;
                }
                stats.setRequestCount(stats.getRequestCount() + 1);
                String requestedMode = normalizeChatMode(StringUtils.defaultIfBlank(routeAudit.getRequestedMode(), userMessage.getAbilityType()));
                if (AiChatMode.AUTO.name().equals(requestedMode)) {
                    stats.setAutoRequestCount(stats.getAutoRequestCount() + 1);
                    if (userMessage.getMessageId() != null) {
                        autoRequestIds.add(userMessage.getMessageId());
                    }
                } else {
                    stats.setManualModeRequestCount(stats.getManualModeRequestCount() + 1);
                    String manualModeSource = normalizeManualModeSource(routeAudit.getManualModeSource());
                    if (AiChatObservabilityConstants.MANUAL_MODE_SOURCE_PINNED.equals(manualModeSource)) {
                        stats.setPinnedRequestCount(stats.getPinnedRequestCount() + 1);
                    } else if (AiChatObservabilityConstants.MANUAL_MODE_SOURCE_OVERRIDE.equals(manualModeSource)) {
                        stats.setOverrideRequestCount(stats.getOverrideRequestCount() + 1);
                    } else {
                        stats.setDirectManualRequestCount(stats.getDirectManualRequestCount() + 1);
                    }
                }

                if (AiChatObservabilityConstants.INTERACTION_SOURCE_MODE_CORRECTION_RETRY
                        .equalsIgnoreCase(StringUtils.defaultString(routeAudit.getInteractionSource()))) {
                    stats.setCorrectionRetryCount(stats.getCorrectionRetryCount() + 1);
                    if (routeAudit.getRetrySourceMessageId() != null) {
                        correctedAutoRequestIds.add(routeAudit.getRetrySourceMessageId());
                    }
                    if (isCorrectionSuccess(findNextAssistantMessage(sessionMessages, index), requestedMode)) {
                        stats.setCorrectionSuccessCount(stats.getCorrectionSuccessCount() + 1);
                    }
                }
            }
        }
        long autoCorrectedCount = autoRequestIds.stream()
                .filter(correctedAutoRequestIds::contains)
                .count();
        stats.setAutoCorrectedCount(autoCorrectedCount);
        stats.setAutoHitCount(Math.max(stats.getAutoRequestCount() - autoCorrectedCount, 0));
        stats.setAutoHitRate(safeRate(stats.getAutoHitCount(), stats.getAutoRequestCount()));
        stats.setManualModeRate(safeRate(stats.getManualModeRequestCount(), stats.getRequestCount()));
        stats.setCorrectionSuccessRate(safeRate(stats.getCorrectionSuccessCount(), stats.getCorrectionRetryCount()));
        return stats;
    }

    private List<AiChatMessage> loadMessages(List<Long> sessionIds, Date beginTime) {
        List<AiChatMessage> result = new ArrayList<>();
        for (int start = 0; start < sessionIds.size(); start += SESSION_BATCH_SIZE) {
            int end = Math.min(start + SESSION_BATCH_SIZE, sessionIds.size());
            List<Long> batchIds = sessionIds.subList(start, end);
            result.addAll(aiChatMessageMapper.selectList(Wrappers.<AiChatMessage>lambdaQuery()
                    .select(AiChatMessage::getMessageId,
                            AiChatMessage::getSessionId,
                            AiChatMessage::getMessageNo,
                            AiChatMessage::getRoleType,
                            AiChatMessage::getMessageContent,
                            AiChatMessage::getToolName,
                            AiChatMessage::getToolResult,
                            AiChatMessage::getAbilityType,
                            AiChatMessage::getMessageStatus,
                            AiChatMessage::getCreateTime)
                    .in(AiChatMessage::getSessionId, batchIds)
                    .ge(AiChatMessage::getCreateTime, beginTime)));
        }
        return result;
    }

    private AiChatObservabilityStatsVO initStats(int windowDays) {
        AiChatObservabilityStatsVO stats = new AiChatObservabilityStatsVO();
        stats.setScopeLabel("当前账号");
        stats.setWindowDays(windowDays);
        stats.setWindowLabel("最近" + windowDays + "天");
        return stats;
    }

    private Date resolveWindowBeginTime(int windowDays) {
        LocalDate beginDate = LocalDate.now().minusDays(Math.max(windowDays - 1, 0));
        LocalDateTime beginDateTime = LocalDateTime.of(beginDate, LocalTime.MIN);
        return Date.from(beginDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    private int normalizeWindowDays(Integer days) {
        if (days == null || days <= 0) {
            return DEFAULT_WINDOW_DAYS;
        }
        return Math.min(days, MAX_WINDOW_DAYS);
    }

    private AiChatIntentRouteVO parseRouteAudit(AiChatMessage message) {
        if (message == null || StringUtils.isBlank(message.getToolResult())) {
            return null;
        }
        try {
            JSONObject payload = JSON.parseObject(message.getToolResult());
            if (!isRouteAuditPayload(payload, message.getToolName())) {
                return null;
            }
            return payload.toJavaObject(AiChatIntentRouteVO.class);
        } catch (Exception ignored) {
            return null;
        }
    }

    private boolean isRouteAuditPayload(JSONObject payload, String toolName) {
        if (payload == null || payload.isEmpty()) {
            return false;
        }
        if (TOOL_NAME_AUTO_ROUTER_ANALYZE.equalsIgnoreCase(StringUtils.defaultString(toolName))) {
            return true;
        }
        return payload.containsKey("requestedMode")
                || payload.containsKey("mode")
                || payload.containsKey("ruleMode")
                || payload.containsKey("finalMode");
    }

    private AiChatMessage findNextAssistantMessage(List<AiChatMessage> sessionMessages, int currentIndex) {
        for (int index = currentIndex + 1; index < sessionMessages.size(); index++) {
            AiChatMessage nextMessage = sessionMessages.get(index);
            if (ROLE_ASSISTANT.equalsIgnoreCase(StringUtils.defaultString(nextMessage.getRoleType()))) {
                return nextMessage;
            }
            if (ROLE_USER.equalsIgnoreCase(StringUtils.defaultString(nextMessage.getRoleType()))) {
                return null;
            }
        }
        return null;
    }

    private boolean isCorrectionSuccess(AiChatMessage assistantMessage, String requestedMode) {
        if (assistantMessage == null
                || !MESSAGE_STATUS_SUCCESS.equalsIgnoreCase(StringUtils.defaultString(assistantMessage.getMessageStatus()))
                || StringUtils.isBlank(requestedMode)) {
            return false;
        }
        return requestedMode.equals(normalizeChatMode(assistantMessage.getAbilityType()));
    }

    private String normalizeChatMode(String mode) {
        if (StringUtils.isBlank(mode)) {
            return null;
        }
        String normalized = mode.trim().toUpperCase();
        if ("GENERAL_CHAT".equals(normalized)) {
            return AiChatMode.GENERAL.name();
        }
        return normalized;
    }

    private String normalizeManualModeSource(String source) {
        if (StringUtils.isBlank(source)) {
            return AiChatObservabilityConstants.MANUAL_MODE_SOURCE_DIRECT;
        }
        String normalized = source.trim().toUpperCase();
        if (AiChatObservabilityConstants.MANUAL_MODE_SOURCE_PINNED.equals(normalized)) {
            return AiChatObservabilityConstants.MANUAL_MODE_SOURCE_PINNED;
        }
        if (AiChatObservabilityConstants.MANUAL_MODE_SOURCE_OVERRIDE.equals(normalized)) {
            return AiChatObservabilityConstants.MANUAL_MODE_SOURCE_OVERRIDE;
        }
        return AiChatObservabilityConstants.MANUAL_MODE_SOURCE_DIRECT;
    }

    private double safeRate(long numerator, long denominator) {
        if (denominator <= 0) {
            return 0D;
        }
        return (double) numerator / (double) denominator;
    }
}
