package com.fastbee.ai.service.impl;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fastbee.ai.domain.AiChatMessage;
import com.fastbee.ai.model.enums.AiChatMode;
import com.fastbee.ai.model.vo.AiChatIntentRouteVO;
import com.fastbee.ai.model.vo.AiConversationContextBundleVO;
import com.fastbee.ai.model.vo.AiConversationGlobalContextVO;
import com.fastbee.ai.model.vo.AiDeviceControlContextSnapshotVO;
import com.fastbee.ai.model.vo.AiNl2SqlContextSnapshotVO;
import com.fastbee.ai.model.vo.AiPlatformAssistantContextSnapshotVO;
import com.fastbee.ai.model.vo.AiProtocolParseContextSnapshotVO;
import com.fastbee.ai.service.IAiConversationContextAssembler;
import com.fastbee.ai.service.IAiDeviceControlConversationContextService;
import com.fastbee.ai.service.IAiNl2SqlConversationContextService;
import com.fastbee.ai.service.IAiPlatformAssistantConversationContextService;
import com.fastbee.ai.service.IAiProtocolParseConversationContextService;
import com.fastbee.common.utils.StringUtils;

/**
 * AI 会话执行上下文装配器实现。
 */
@Service
public class AiConversationContextAssemblerImpl implements IAiConversationContextAssembler {

    @Resource
    private IAiDeviceControlConversationContextService aiDeviceControlConversationContextService;

    @Resource
    private IAiNl2SqlConversationContextService aiNl2SqlConversationContextService;

    @Resource
    private IAiPlatformAssistantConversationContextService aiPlatformAssistantConversationContextService;

    @Resource
    private IAiProtocolParseConversationContextService aiProtocolParseConversationContextService;

    @Override
    public AiConversationContextBundleVO buildExecutionContext(String question,
                                                               String effectiveMode,
                                                               AiChatIntentRouteVO intentRouteResult,
                                                               List<AiChatMessage> historyMessages) {
        AiConversationContextBundleVO bundle = new AiConversationContextBundleVO();
        bundle.setExecutionQuestion(question);
        bundle.setGlobalContext(buildGlobalContext(historyMessages));

        if (AiChatMode.DEVICE_CONTROL.name().equals(effectiveMode)) {
            bundle.setExecutionQuestion(aiDeviceControlConversationContextService.enrichExecutionQuestion(
                    question, intentRouteResult, historyMessages
            ));
            bundle.setSkillContextSummary(buildDeviceControlContextSummary(historyMessages));
            return bundle;
        }
        if (AiChatMode.NL2SQL.name().equals(effectiveMode)) {
            bundle.setExecutionQuestion(aiNl2SqlConversationContextService.enrichExecutionQuestion(
                    question, intentRouteResult, historyMessages
            ));
            bundle.setSkillContextSummary(aiNl2SqlConversationContextService.buildContextSummary(historyMessages));
            return bundle;
        }
        if (AiChatMode.PLATFORM_ASSISTANT.name().equals(effectiveMode)) {
            String enrichedQuestion = aiPlatformAssistantConversationContextService.enrichExecutionQuestion(
                    question, historyMessages
            );
            bundle.setExecutionQuestion(enrichedQuestion);
            if (StringUtils.isNotBlank(enrichedQuestion) && !enrichedQuestion.equals(question)) {
                bundle.setSkillContextSummary(aiPlatformAssistantConversationContextService.buildContextSummary(historyMessages));
            } else if (bundle.getGlobalContext() != null) {
                bundle.getGlobalContext().setFocusMenuPath(null);
                bundle.getGlobalContext().setFocusPageTitle(null);
            }
            return bundle;
        }
        if (AiChatMode.PROTOCOL_PARSE.name().equals(effectiveMode)) {
            bundle.setExecutionQuestion(aiProtocolParseConversationContextService.enrichExecutionQuestion(
                    question, historyMessages
            ));
            bundle.setSkillContextSummary(aiProtocolParseConversationContextService.buildContextSummary(historyMessages));
            return bundle;
        }
        return bundle;
    }

    private AiConversationGlobalContextVO buildGlobalContext(List<AiChatMessage> historyMessages) {
        AiConversationGlobalContextVO globalContext = new AiConversationGlobalContextVO();
        if (historyMessages == null || historyMessages.isEmpty()) {
            return globalContext;
        }
        List<String> recentUserTopics = new ArrayList<>();
        for (int index = historyMessages.size() - 1; index >= 0 && recentUserTopics.size() < 2; index--) {
            AiChatMessage item = historyMessages.get(index);
            if (item == null
                    || !"user".equalsIgnoreCase(item.getRoleType())
                    || StringUtils.isBlank(item.getMessageContent())
                    || "FAIL".equalsIgnoreCase(item.getMessageStatus())) {
                continue;
            }
            String content = item.getMessageContent().trim();
            if (recentUserTopics.contains(content)) {
                continue;
            }
            recentUserTopics.add(0, content);
        }
        globalContext.setRecentUserTopics(recentUserTopics);
        mergeDeviceAndNl2SqlFacts(globalContext, historyMessages);
        mergePlatformFacts(globalContext, historyMessages);
        mergeProtocolFacts(globalContext, historyMessages);
        return globalContext;
    }

    private void mergeDeviceAndNl2SqlFacts(AiConversationGlobalContextVO globalContext, List<AiChatMessage> historyMessages) {
        AiDeviceControlContextSnapshotVO deviceSnapshot = aiDeviceControlConversationContextService == null
                ? null
                : aiDeviceControlConversationContextService.resolveLatestContext(historyMessages);
        if (deviceSnapshot != null) {
            globalContext.setFocusDeviceName(trimToNull(deviceSnapshot.getDeviceName()));
            globalContext.setFocusSerialNumber(trimToNull(deviceSnapshot.getSerialNumber()));
            globalContext.setFocusProductName(trimToNull(deviceSnapshot.getProductName()));
            globalContext.setFocusThingModelName(trimToNull(deviceSnapshot.getThingModelName()));
            globalContext.setFocusIdentifier(trimToNull(deviceSnapshot.getIdentifier()));
        }

        AiNl2SqlContextSnapshotVO nl2SqlSnapshot = aiNl2SqlConversationContextService == null
                ? null
                : aiNl2SqlConversationContextService.resolveLatestContext(historyMessages);
        if (nl2SqlSnapshot == null) {
            return;
        }
        globalContext.setFocusDeviceName(defaultText(globalContext.getFocusDeviceName(), nl2SqlSnapshot.getDeviceName()));
        globalContext.setFocusSerialNumber(defaultText(globalContext.getFocusSerialNumber(), nl2SqlSnapshot.getSerialNumber()));
        globalContext.setFocusProductName(defaultText(globalContext.getFocusProductName(), nl2SqlSnapshot.getProductName()));
        globalContext.setFocusIdentifier(defaultText(globalContext.getFocusIdentifier(), nl2SqlSnapshot.getIdentifier()));
    }

    private void mergePlatformFacts(AiConversationGlobalContextVO globalContext, List<AiChatMessage> historyMessages) {
        AiPlatformAssistantContextSnapshotVO snapshot = aiPlatformAssistantConversationContextService == null
                ? null
                : aiPlatformAssistantConversationContextService.resolveLatestContext(historyMessages);
        if (snapshot == null) {
            return;
        }
        globalContext.setFocusMenuPath(trimToNull(snapshot.getMenuPath()));
        globalContext.setFocusPageTitle(trimToNull(snapshot.getPageTitle()));
    }

    private void mergeProtocolFacts(AiConversationGlobalContextVO globalContext, List<AiChatMessage> historyMessages) {
        AiProtocolParseContextSnapshotVO snapshot = aiProtocolParseConversationContextService == null
                ? null
                : aiProtocolParseConversationContextService.resolveLatestContext(historyMessages);
        if (snapshot == null) {
            return;
        }
        globalContext.setFocusProtocolModuleName(trimToNull(snapshot.getModuleName()));
        globalContext.setFocusProtocolFieldName(trimToNull(snapshot.getFieldName()));
        globalContext.setFocusProtocolFieldCode(trimToNull(snapshot.getFieldCode()));
        globalContext.setFocusProtocolDataType(trimToNull(snapshot.getDataType()));
    }

    private String buildDeviceControlContextSummary(List<AiChatMessage> historyMessages) {
        AiDeviceControlContextSnapshotVO snapshot = aiDeviceControlConversationContextService.resolveLatestContext(historyMessages);
        if (snapshot == null) {
            return null;
        }
        StringBuilder summary = new StringBuilder();
        if (StringUtils.isNotBlank(snapshot.getDeviceName()) || StringUtils.isNotBlank(snapshot.getSerialNumber())) {
            summary.append("最近一次控制对象：");
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
        if (StringUtils.isNotBlank(snapshot.getIdentifier())) {
            appendSummaryPart(summary, "最近一次控制标识符：" + snapshot.getIdentifier());
        }
        if (StringUtils.isNotBlank(snapshot.getActionValue())) {
            appendSummaryPart(summary, "最近一次动作值：" + snapshot.getActionValue());
        }
        return summary.length() == 0 ? null : summary.toString();
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

    private String defaultText(String preferred, String fallback) {
        return StringUtils.isNotBlank(preferred) ? preferred : trimToNull(fallback);
    }

    private String trimToNull(String value) {
        return StringUtils.isBlank(value) ? null : value.trim();
    }
}
