package com.fastbee.ai.service.impl;

import static com.fastbee.ai.support.AiI18nMessageUtils.message;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.alibaba.fastjson2.JSON;
import org.springframework.stereotype.Service;

import com.fastbee.ai.model.dto.AiChatSendRequest;
import com.fastbee.ai.model.enums.AiChatMode;
import com.fastbee.ai.model.vo.AiChatStreamEventVO;
import com.fastbee.ai.model.vo.AiClarifyOptionVO;
import com.fastbee.ai.model.vo.AiClarifyPayloadVO;
import com.fastbee.ai.model.vo.AiResumeSelectionVO;
import com.fastbee.ai.service.IAiClarifySupportService;
import com.fastbee.ai.support.AiClarifyConstants;
import com.fastbee.ai.support.AiSecuritySupport;
import com.fastbee.common.utils.StringUtils;

/**
 * AI 澄清与续跑支撑服务实现。
 */
@Service
public class AiClarifySupportServiceImpl implements IAiClarifySupportService {

    @Override
    public AiClarifyPayloadVO buildPayload(String clarifyType,
                                           String clarifyKey,
                                           String clarifyTitle,
                                           String displayContent,
                                           String toolName,
                                           String resumeQuestion,
                                           List<AiClarifyOptionVO> options) {
        AiClarifyPayloadVO payload = new AiClarifyPayloadVO();
        payload.setClarifyType(clarifyType);
        payload.setClarifyKey(clarifyKey);
        payload.setClarifyTitle(clarifyTitle);
        payload.setDisplayContent(displayContent);
        payload.setToolName(toolName);
        payload.setResumeToken(AiSecuritySupport.newSessionCode());
        payload.setResumeQuestion(trimToNull(resumeQuestion));
        if (options != null && !options.isEmpty()) {
            payload.setOptions(new ArrayList<>(options));
        }
        payload.setToolResult(buildToolResult(payload));
        return payload;
    }

    @Override
    public void applyPayload(AiClarifyPayloadVO payload, AiChatStreamEventVO event) {
        if (payload == null || event == null) {
            return;
        }
        event.setClarifyType(payload.getClarifyType());
        event.setClarifyKey(payload.getClarifyKey());
        event.setClarifyTitle(payload.getClarifyTitle());
        event.setClarifyOptions(convertOptions(payload.getOptions()));
        event.setResumeToken(payload.getResumeToken());
        event.setResumeQuestion(payload.getResumeQuestion());
    }

    @Override
    public String resolveResumeEffectiveMode(AiChatSendRequest request) {
        if (request == null) {
            return AiChatMode.GENERAL.name();
        }
        String clarifyKey = trimToNull(request.getClarifyKey());
        if (AiClarifyConstants.CLARIFY_KEY_AUTO_MODE_CONFIRM.equals(clarifyKey)) {
            return normalizeChatMode(request.getSelectedValue(), trimToNull(request.getChatMode()));
        }
        if (AiClarifyConstants.CLARIFY_KEY_NL2SQL_DEVICE.equals(clarifyKey)
                || AiClarifyConstants.CLARIFY_KEY_NL2SQL_THING_MODEL.equals(clarifyKey)) {
            return AiChatMode.NL2SQL.name();
        }
        if (AiClarifyConstants.CLARIFY_KEY_DEVICE_CONTROL_DEVICE.equals(clarifyKey)
                || AiClarifyConstants.CLARIFY_KEY_DEVICE_CONTROL_THING_MODEL.equals(clarifyKey)
                || AiClarifyConstants.CLARIFY_KEY_DEVICE_CONTROL_CONFIRM.equals(clarifyKey)) {
            return AiChatMode.DEVICE_CONTROL.name();
        }
        return trimToNull(request.getChatMode()) == null ? AiChatMode.GENERAL.name() : request.getChatMode().trim();
    }

    @Override
    public AiResumeSelectionVO resolveResumeSelection(AiChatSendRequest request) {
        if (request == null) {
            return null;
        }
        AiResumeSelectionVO selection = new AiResumeSelectionVO();
        selection.setClarifyKey(trimToNull(request.getClarifyKey()));
        selection.setEffectiveChatMode(resolveResumeEffectiveMode(request));
        selection.setSelectedValue(trimToNull(request.getSelectedValue()));
        selection.setSelectedLabel(resolveSelectedLabel(request));
        String baseQuestion = StringUtils.isNotBlank(request.getResumeQuestion())
                ? request.getResumeQuestion().trim()
                : trimToNull(request.getMessage());
        selection.setResumeQuestion(baseQuestion);
        return selection;
    }

    @Override
    public String buildResumeDisplayMessage(AiChatSendRequest request) {
        if (request != null && StringUtils.isNotBlank(request.getMessage())) {
            return request.getMessage().trim();
        }
        AiResumeSelectionVO selection = resolveResumeSelection(request);
        if (selection == null) {
            return message("ai.chat.clarify.resume.confirmed.option");
        }
        String selectedLabel = defaultText(selection.getSelectedLabel(), selection.getSelectedValue());
        if (AiClarifyConstants.CLARIFY_KEY_NL2SQL_DEVICE.equals(selection.getClarifyKey())
                || AiClarifyConstants.CLARIFY_KEY_DEVICE_CONTROL_DEVICE.equals(selection.getClarifyKey())) {
            return message("ai.chat.clarify.resume.selected.device", defaultText(selectedLabel));
        }
        if (AiClarifyConstants.CLARIFY_KEY_NL2SQL_THING_MODEL.equals(selection.getClarifyKey())) {
            return message("ai.chat.clarify.resume.selected.thing.model", defaultText(selectedLabel));
        }
        if (AiClarifyConstants.CLARIFY_KEY_DEVICE_CONTROL_THING_MODEL.equals(selection.getClarifyKey())) {
            return message("ai.chat.clarify.resume.selected.control.target", defaultText(selectedLabel));
        }
        if (AiClarifyConstants.CLARIFY_KEY_DEVICE_CONTROL_CONFIRM.equals(selection.getClarifyKey())) {
            if (AiClarifyConstants.CONFIRM_OPTION_CANCEL.equalsIgnoreCase(selection.getSelectedValue())) {
                return message("ai.chat.clarify.resume.risk.cancelled");
            }
            return message("ai.chat.clarify.resume.risk.confirmed");
        }
        if (AiClarifyConstants.CLARIFY_KEY_AUTO_MODE_CONFIRM.equals(selection.getClarifyKey())) {
            return message("ai.chat.clarify.resume.mode.confirmed", resolveModeLabel(selection.getEffectiveChatMode()));
        }
        return message("ai.chat.clarify.resume.confirmed.option.with.label", defaultText(selectedLabel));
    }

    @Override
    public String buildResumeExecutionQuestion(AiChatSendRequest request) {
        AiResumeSelectionVO selection = resolveResumeSelection(request);
        if (selection == null || StringUtils.isBlank(selection.getResumeQuestion())) {
            return null;
        }
        StringBuilder builder = new StringBuilder(selection.getResumeQuestion());
        String selectedLabel = defaultText(selection.getSelectedLabel(), selection.getSelectedValue());
        String selectedValue = selection.getSelectedValue();
        if (AiClarifyConstants.CLARIFY_KEY_NL2SQL_DEVICE.equals(selection.getClarifyKey())
                || AiClarifyConstants.CLARIFY_KEY_DEVICE_CONTROL_DEVICE.equals(selection.getClarifyKey())) {
            builder.append('\n').append(message("ai.chat.clarify.execution.confirmed.device", defaultText(selectedLabel)));
            if (StringUtils.isNotBlank(selectedValue)) {
                builder.append("\nserialNumber=").append(selectedValue);
            }
        } else if (AiClarifyConstants.CLARIFY_KEY_NL2SQL_THING_MODEL.equals(selection.getClarifyKey())) {
            builder.append('\n').append(message("ai.chat.clarify.execution.confirmed.thing.model", defaultText(selectedLabel)));
            if (StringUtils.isNotBlank(selectedValue)) {
                builder.append("\nidentifier=").append(selectedValue);
            }
        } else if (AiClarifyConstants.CLARIFY_KEY_DEVICE_CONTROL_THING_MODEL.equals(selection.getClarifyKey())) {
            builder.append('\n').append(message("ai.chat.clarify.execution.confirmed.control.target", defaultText(selectedLabel)));
            if (StringUtils.isNotBlank(selectedValue)) {
                builder.append("\nidentifier=").append(selectedValue);
            }
        } else if (AiClarifyConstants.CLARIFY_KEY_DEVICE_CONTROL_CONFIRM.equals(selection.getClarifyKey())) {
            if (AiClarifyConstants.CONFIRM_OPTION_CONTINUE.equalsIgnoreCase(selectedValue)) {
                builder.append('\n').append(message("ai.chat.clarify.execution.risk.confirmed"));
                builder.append("\nriskConfirmed=1");
            }
        } else if (AiClarifyConstants.CLARIFY_KEY_AUTO_MODE_CONFIRM.equals(selection.getClarifyKey())) {
            builder.append('\n').append(message("ai.chat.clarify.execution.mode.confirmed", resolveModeLabel(selection.getEffectiveChatMode())));
        }
        return builder.toString();
    }

    private String normalizeChatMode(String mode, String fallbackMode) {
        if (StringUtils.isNotBlank(mode)) {
            String normalized = mode.trim().toUpperCase();
            for (AiChatMode item : AiChatMode.values()) {
                if (item.name().equals(normalized)) {
                    return normalized;
                }
            }
        }
        if (StringUtils.isNotBlank(fallbackMode)) {
            String normalized = fallbackMode.trim().toUpperCase();
            for (AiChatMode item : AiChatMode.values()) {
                if (item.name().equals(normalized)) {
                    return normalized;
                }
            }
        }
        return AiChatMode.GENERAL.name();
    }

    private String resolveModeLabel(String mode) {
        if (AiChatMode.PLATFORM_ASSISTANT.name().equalsIgnoreCase(mode)) {
            return message("ai.chat.mode.platform.assistant");
        }
        if (AiChatMode.GENERAL.name().equalsIgnoreCase(mode)) {
            return message("ai.chat.mode.general");
        }
        if (AiChatMode.NL2SQL.name().equalsIgnoreCase(mode)) {
            return message("ai.chat.mode.nl2sql");
        }
        if (AiChatMode.DEVICE_CONTROL.name().equalsIgnoreCase(mode)) {
            return message("ai.chat.mode.device.control");
        }
        if (AiChatMode.PROTOCOL_PARSE.name().equalsIgnoreCase(mode)) {
            return message("ai.chat.mode.protocol.parse");
        }
        if (AiChatMode.AUTO.name().equalsIgnoreCase(mode)) {
            return message("ai.chat.mode.auto");
        }
        return defaultText(mode, message("ai.chat.mode.general"));
    }

    private String buildToolResult(AiClarifyPayloadVO payload) {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("clarifyType", payload.getClarifyType());
        result.put("clarifyKey", payload.getClarifyKey());
        result.put("clarifyTitle", payload.getClarifyTitle());
        result.put("clarifyOptions", convertOptions(payload.getOptions()));
        result.put("resumeToken", payload.getResumeToken());
        result.put("resumeQuestion", payload.getResumeQuestion());
        return JSON.toJSONString(result);
    }

    private List<LinkedHashMap<String, Object>> convertOptions(List<AiClarifyOptionVO> options) {
        List<LinkedHashMap<String, Object>> results = new ArrayList<>();
        if (options == null || options.isEmpty()) {
            return results;
        }
        for (AiClarifyOptionVO option : options) {
            if (option == null || StringUtils.isBlank(option.getValue())) {
                continue;
            }
            LinkedHashMap<String, Object> item = new LinkedHashMap<>();
            item.put("label", option.getLabel());
            item.put("value", option.getValue());
            item.put("description", option.getDescription());
            item.put("type", option.getType());
            if (option.getScore() != null) {
                item.put("score", option.getScore());
            }
            results.add(item);
        }
        return results;
    }

    private String resolveSelectedLabel(AiChatSendRequest request) {
        if (request == null) {
            return null;
        }
        if (StringUtils.isNotBlank(request.getSelectedLabel())) {
            return request.getSelectedLabel().trim();
        }
        return trimToNull(request.getSelectedValue());
    }

    private String trimToNull(String value) {
        return StringUtils.isBlank(value) ? null : value.trim();
    }

    private String defaultText(String value) {
        return StringUtils.defaultIfBlank(value, message("ai.chat.clarify.default.option"));
    }

    private String defaultText(String primary, String fallback) {
        return StringUtils.defaultIfBlank(primary, StringUtils.defaultIfBlank(fallback, message("ai.chat.clarify.default.option")));
    }
}
