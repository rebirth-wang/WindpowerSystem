package com.fastbee.ai.service.impl;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.stereotype.Service;

import com.fastbee.ai.domain.AiChatMessage;
import com.fastbee.ai.model.vo.AiChatIntentRouteVO;
import com.fastbee.ai.model.vo.AiDeviceControlContextSnapshotVO;
import com.fastbee.ai.model.vo.AiDeviceControlIntentVO;
import com.fastbee.ai.service.IAiDeviceControlConversationContextService;
import com.fastbee.ai.support.AiCandidateMatchSupport;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.domain.Product;
import com.fastbee.iot.model.DeviceMetaData;
import com.fastbee.iot.model.ThingsModels.ThingsModelValueItem;

/**
 * AI 设备控制会话上下文支撑服务实现。
 */
@Service
public class AiDeviceControlConversationContextServiceImpl implements IAiDeviceControlConversationContextService {

    private static final String CONTEXT_SNAPSHOT_KEY = "contextSnapshot";

    private static final Pattern SERIAL_NUMBER_PATTERN = Pattern.compile(
            "(?:serialNumber|deviceNumber|设备编号)\\s*[=:：]\\s*([^\\s,，；;]+)",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern DEVICE_NAME_PATTERN = Pattern.compile(
            "(?:deviceName|设备名称)\\s*[=:：]\\s*([^\\s,，；;]+)",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern PRODUCT_NAME_PATTERN = Pattern.compile(
            "(?:productName|产品名称|产品名)\\s*[=:：]\\s*([^\\s,，；;]+)",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern IDENTIFIER_PATTERN = Pattern.compile(
            "(?:identifier|标识符)\\s*[=:：]\\s*([^\\s,，；;]+)",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern ACTION_VALUE_PATTERN = Pattern.compile(
            "(?:actionValue|value|值)\\s*[=:：]\\s*([^\\s,，；;]+)",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern ACTION_VALUE_HINT_PATTERN = Pattern.compile(
            "(?:设为|设置为|设置成|改为|改成|调整为|调整到|调到|切换到)\\s*([^\\s,，；;]+)",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern THING_MODEL_TEXT_PATTERN = Pattern.compile(
            "(?:物模型|指标|功能|属性)\\s*[=:：]?\\s*([^\\s,，；;]+)",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern POSSESSIVE_THING_MODEL_PATTERN = Pattern.compile(
            "的\\s*([^\\s,，。；;？?]{1,32}?)(?=(?:设置为|设置成|设置|设为|设成|改为|改成|调整为|调整到|调到|切换到|切换为|打开|开启|关闭|关掉|停用|启动|停止|暂停|恢复|为|到|,|，|。|；|;|？|\\?|$))",
            Pattern.CASE_INSENSITIVE
    );

    private static final List<String> REPEAT_ACTION_TOKENS = List.of(
            "再执行一次", "再来一次", "重复上一条", "重复上一次", "继续刚才", "继续上一次",
            "按刚才", "照刚才", "还是刚才", "跟刚才一样", "和刚才一样", "按上次", "照上次",
            "同样操作", "再次执行", "继续执行"
    );

    @Override
    public AiDeviceControlContextSnapshotVO resolveLatestContext(List<AiChatMessage> historyMessages) {
        if (historyMessages == null || historyMessages.isEmpty()) {
            return null;
        }
        for (int i = historyMessages.size() - 1; i >= 0; i--) {
            AiChatMessage item = historyMessages.get(i);
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
            AiDeviceControlContextSnapshotVO snapshot = snapshotObject.to(AiDeviceControlContextSnapshotVO.class);
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
        if (StringUtils.isBlank(question) || shouldSkipContextReuse(question)) {
            return question;
        }
        AiDeviceControlContextSnapshotVO snapshot = resolveLatestContext(historyMessages);
        if (!isUsableSnapshot(snapshot)) {
            return question;
        }
        String baseQuestion = question.trim();
        StringBuilder builder = new StringBuilder(baseQuestion);
        boolean appended = false;
        if (shouldAppendSerialNumber(baseQuestion, intentRouteResult, snapshot)) {
            builder.append('\n').append("serialNumber=").append(snapshot.getSerialNumber());
            appended = true;
        }
        if (shouldAppendIdentifier(baseQuestion, intentRouteResult, snapshot)) {
            builder.append('\n').append("identifier=").append(snapshot.getIdentifier());
            appended = true;
        }
        if (shouldAppendActionValue(baseQuestion, intentRouteResult, snapshot)) {
            builder.append('\n').append("actionValue=").append(snapshot.getActionValue());
            appended = true;
        }
        return appended ? builder.toString() : question;
    }

    @Override
    public AiDeviceControlContextSnapshotVO buildContextSnapshot(AiChatIntentRouteVO intentRouteResult,
                                                                 AiDeviceControlIntentVO controlIntent,
                                                                 String riskConfirmed) {
        if (controlIntent == null) {
            return null;
        }
        DeviceMetaData deviceMetaData = controlIntent.getDeviceMetaData();
        Device device = deviceMetaData == null ? null : deviceMetaData.getDevice();
        ThingsModelValueItem thingModel = controlIntent.getThingModel();
        if (device == null || StringUtils.isBlank(device.getSerialNumber()) || thingModel == null || StringUtils.isBlank(thingModel.getId())) {
            return null;
        }

        AiDeviceControlContextSnapshotVO snapshot = new AiDeviceControlContextSnapshotVO();
        Product product = deviceMetaData == null ? null : deviceMetaData.getProduct();
        snapshot.setSerialNumber(trimToNull(device.getSerialNumber()));
        snapshot.setDeviceName(trimToNull(device.getDeviceName()));
        snapshot.setProductId(product == null ? null : product.getProductId());
        snapshot.setProductName(product == null ? null : trimToNull(product.getProductName()));
        snapshot.setIdentifier(trimToNull(thingModel.getId()));
        snapshot.setThingModelName(resolveThingModelName(thingModel));
        snapshot.setThingModelType(thingModel.getType());
        snapshot.setActionText(trimToNull(defaultText(controlIntent.getActionText(),
                intentRouteResult == null ? null : intentRouteResult.getActionText())));
        snapshot.setActionValue(trimToNull(defaultText(controlIntent.getActionValue(),
                intentRouteResult == null ? null : intentRouteResult.getActionValue())));
        snapshot.setBusinessType(intentRouteResult == null ? null : trimToNull(intentRouteResult.getBusinessType()));
        snapshot.setRiskConfirmed("1".equals(trimToNull(riskConfirmed)) ? "1" : "0");
        return isUsableSnapshot(snapshot) ? snapshot : null;
    }

    @Override
    public void attachContextSnapshot(AiChatMessage userMessage, AiDeviceControlContextSnapshotVO snapshot) {
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

    private boolean shouldSkipContextReuse(String question) {
        if (StringUtils.isBlank(question)) {
            return true;
        }
        if (question.contains("{") && question.contains("}")) {
            return true;
        }
        return question.contains("执行场景") || question.contains("运行场景") || question.contains("runScene");
    }

    private boolean shouldAppendSerialNumber(String question,
                                             AiChatIntentRouteVO intentRouteResult,
                                             AiDeviceControlContextSnapshotVO snapshot) {
        if (snapshot == null || StringUtils.isBlank(snapshot.getSerialNumber())) {
            return false;
        }
        if (hasPatternValue(question, SERIAL_NUMBER_PATTERN)
                || hasPatternValue(question, DEVICE_NAME_PATTERN)
                || hasPatternValue(question, PRODUCT_NAME_PATTERN)) {
            return false;
        }
        return intentRouteResult == null
                || (StringUtils.isBlank(intentRouteResult.getSerialNumberText())
                && StringUtils.isBlank(intentRouteResult.getDeviceNameText())
                && StringUtils.isBlank(intentRouteResult.getProductNameText()));
    }

    private boolean shouldAppendIdentifier(String question,
                                           AiChatIntentRouteVO intentRouteResult,
                                           AiDeviceControlContextSnapshotVO snapshot) {
        if (snapshot == null || StringUtils.isBlank(snapshot.getIdentifier())) {
            return false;
        }
        if (hasPatternValue(question, IDENTIFIER_PATTERN)
                || hasPatternValue(question, THING_MODEL_TEXT_PATTERN)
                || hasPatternValue(question, POSSESSIVE_THING_MODEL_PATTERN)) {
            return false;
        }
        if (intentRouteResult != null && StringUtils.isNotBlank(intentRouteResult.getThingModelText())) {
            return false;
        }
        return isContextReferenceQuestion(question);
    }

    private boolean shouldAppendActionValue(String question,
                                            AiChatIntentRouteVO intentRouteResult,
                                            AiDeviceControlContextSnapshotVO snapshot) {
        if (snapshot == null || StringUtils.isBlank(snapshot.getActionValue())) {
            return false;
        }
        if (hasPatternValue(question, ACTION_VALUE_PATTERN) || hasPatternValue(question, ACTION_VALUE_HINT_PATTERN)) {
            return false;
        }
        if (intentRouteResult != null
                && (StringUtils.isNotBlank(intentRouteResult.getActionValue())
                || StringUtils.isNotBlank(intentRouteResult.getActionText()))) {
            return false;
        }
        return isRepeatActionQuestion(question);
    }

    private boolean isRepeatActionQuestion(String question) {
        String normalizedQuestion = AiCandidateMatchSupport.normalizeText(question);
        if (StringUtils.isBlank(normalizedQuestion)) {
            return false;
        }
        for (String token : REPEAT_ACTION_TOKENS) {
            String normalizedToken = AiCandidateMatchSupport.normalizeText(token);
            if (StringUtils.isNotBlank(normalizedToken) && normalizedQuestion.contains(normalizedToken)) {
                return true;
            }
        }
        return false;
    }

    private boolean isContextReferenceQuestion(String question) {
        String normalizedQuestion = AiCandidateMatchSupport.normalizeText(question);
        if (StringUtils.isBlank(normalizedQuestion)) {
            return false;
        }
        return normalizedQuestion.contains("它")
                || normalizedQuestion.contains("该设备")
                || normalizedQuestion.contains("这个设备")
                || normalizedQuestion.contains("当前设备")
                || isRepeatActionQuestion(question);
    }

    private boolean isUsableSnapshot(AiDeviceControlContextSnapshotVO snapshot) {
        if (snapshot == null) {
            return false;
        }
        return StringUtils.isNotBlank(snapshot.getSerialNumber())
                || StringUtils.isNotBlank(snapshot.getIdentifier())
                || StringUtils.isNotBlank(snapshot.getActionValue());
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

    private boolean hasPatternValue(String source, Pattern pattern) {
        if (StringUtils.isBlank(source) || pattern == null) {
            return false;
        }
        Matcher matcher = pattern.matcher(source);
        return matcher.find() && StringUtils.isNotBlank(matcher.group(1));
    }

    private String resolveThingModelName(ThingsModelValueItem thingModel) {
        if (thingModel == null) {
            return null;
        }
        return trimToNull(defaultText(thingModel.getName(),
                defaultText(thingModel.getName_zh_CN(),
                        defaultText(thingModel.getName_en_US(), thingModel.getId()))));
    }

    private String defaultText(String primary, String fallback) {
        return StringUtils.isNotBlank(primary) ? primary.trim() : trimToNull(fallback);
    }

    private String trimToNull(String value) {
        return StringUtils.isBlank(value) ? null : value.trim();
    }
}
