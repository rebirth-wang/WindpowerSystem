package com.fastbee.ai.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fastbee.ai.model.enums.AiIntentBusinessType;
import com.fastbee.ai.model.enums.AiIntentThingModelTypeHint;
import com.fastbee.ai.model.vo.AiChatIntentRouteVO;
import com.fastbee.ai.model.vo.AiDeviceControlIntentVO;
import com.fastbee.ai.service.IAiDeviceControlIntentService;
import com.fastbee.ai.service.IAiDeviceMetadataService;
import com.fastbee.ai.service.IAiDeviceResolveService;
import com.fastbee.ai.support.AiCandidateMatchSupport;
import com.fastbee.common.extend.core.domin.model.device.DeviceAndProtocol;
import com.fastbee.common.extend.core.domin.mq.InvokeReqDto;
import com.fastbee.common.extend.core.domin.mq.MQSendMessageBo;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.cache.ITSLCache;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.domain.ModbusParams;
import com.fastbee.iot.domain.Product;
import com.fastbee.iot.domain.SubGateway;
import com.fastbee.iot.enums.ThingsModelTypeEnum;
import com.fastbee.iot.model.DeviceMetaData;
import com.fastbee.iot.model.DeviceShortOutput;
import com.fastbee.iot.model.ThingsModelItem.Datatype;
import com.fastbee.iot.model.ThingsModelItem.EnumItem;
import com.fastbee.iot.model.ThingsModels.ThingsModelValueItem;

/**
 * AI 设备控制意图解析服务实现。
 */
@Service
public class AiDeviceControlIntentServiceImpl implements IAiDeviceControlIntentService {

    private static final Pattern IDENTIFIER_PATTERN = Pattern.compile(
            "(?:identifier|标识符)\\s*[=:：]\\s*([^\\s,，；;]+)",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern VALUE_PATTERN = Pattern.compile(
            "(?:actionValue|value|值)\\s*[=:：]\\s*([^\\s,，；;]+)",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern ACTION_VALUE_HINT_PATTERN = Pattern.compile(
            "(?:设为|设置为|设置成|改为|改成|调整为|调整到|调到|切换到|到)\\s*([^\\s,，；;]+)",
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

    private static final List<String> CONTROL_NOISE_TOKENS = Arrays.asList(
            "请", "帮我", "帮忙", "把", "将", "给我", "设备", "产品", "物模型", "指标",
            "打开", "开启", "关闭", "关掉", "停用", "启动", "停止", "暂停", "恢复",
            "设置", "设为", "调到", "调整", "改成", "切换", "执行", "运行", "触发",
            "调用", "发送", "下发", "生成", "等待回执", "回执", "并等待回执", "的"
    );

    private static final List<String> ACTION_TOKENS = Arrays.asList(
            "打开", "开启", "关闭", "关掉", "停用", "启动", "停止", "暂停", "恢复",
            "设置", "设为", "调到", "调整", "改成", "切换", "重启", "锁定", "解锁"
    );

    @Resource
    private IAiDeviceResolveService aiDeviceResolveService;

    @Resource
    private IAiDeviceMetadataService aiDeviceMetadataService;

    @Resource
    private ITSLCache itslCache;

    @Override
    public AiDeviceControlIntentVO resolveIntent(String question, AiChatIntentRouteVO intentRouteResult) {
        AiDeviceControlIntentVO result = new AiDeviceControlIntentVO();
        result.setQuestion(question);
        if (StringUtils.isBlank(question)) {
            return result;
        }

        List<DeviceShortOutput> deviceCandidates = deduplicateDeviceCandidates(aiDeviceResolveService.listDeviceCandidates(question));
        result.setDeviceCandidates(deviceCandidates);

        DeviceMetaData deviceMetaData = resolveDeviceMetaData(question, deviceCandidates);
        result.setDeviceMetaData(deviceMetaData);
        result.setThingModelText(resolveThingModelText(question, intentRouteResult));
        result.setActionText(resolveActionText(question, intentRouteResult));

        if (deviceMetaData == null || deviceMetaData.getProduct() == null) {
            return result;
        }

        List<ThingsModelValueItem> thingModelCandidates = resolveThingModelCandidates(question, intentRouteResult, deviceMetaData);
        result.setThingModelCandidates(thingModelCandidates);
        if (thingModelCandidates.size() == 1) {
            result.setThingModel(thingModelCandidates.get(0));
        }

        if (result.getThingModel() == null) {
            return result;
        }

        String actionValue = resolveActionValue(question, intentRouteResult, result.getThingModel());
        result.setActionValue(actionValue);
        if (StringUtils.isBlank(actionValue)) {
            return result;
        }

        result.setInvokeRequest(buildInvokeRequest(deviceMetaData, result.getThingModel(), actionValue));
        result.setCommandRequest(buildCommandRequest(deviceMetaData, result.getThingModel(), actionValue));
        return result;
    }

    private DeviceMetaData resolveDeviceMetaData(String question, List<DeviceShortOutput> deviceCandidates) {
        DeviceMetaData metaData = aiDeviceResolveService.resolveOptionalDeviceMetaData(question);
        if (metaData != null) {
            return metaData;
        }
        if (deviceCandidates == null || deviceCandidates.size() != 1) {
            return null;
        }
        DeviceShortOutput candidate = deviceCandidates.get(0);
        if (candidate == null || StringUtils.isBlank(candidate.getSerialNumber())) {
            return null;
        }
        return aiDeviceMetadataService.getOptionalDeviceMetaData(candidate.getSerialNumber());
    }

    private List<DeviceShortOutput> deduplicateDeviceCandidates(List<DeviceShortOutput> candidates) {
        return AiCandidateMatchSupport.deduplicateAndLimit(candidates, DeviceShortOutput::getSerialNumber, 0);
    }

    private List<ThingsModelValueItem> resolveThingModelCandidates(String question,
                                                                   AiChatIntentRouteVO intentRouteResult,
                                                                   DeviceMetaData deviceMetaData) {
        Product product = deviceMetaData.getProduct();
        if (product == null || product.getProductId() == null) {
            return new ArrayList<>();
        }

        String explicitIdentifier = extractPatternValue(question, IDENTIFIER_PATTERN);
        if (StringUtils.isNotBlank(explicitIdentifier)) {
            ThingsModelValueItem item = itslCache.getSingleThingModels(product.getProductId(), explicitIdentifier.trim());
            if (isControllableThingModel(item)) {
                List<ThingsModelValueItem> results = new ArrayList<>();
                results.add(item);
                return results;
            }
            return new ArrayList<>();
        }

        List<ThingsModelValueItem> allModels = itslCache.getThingsModelList(product.getProductId());
        if (allModels == null || allModels.isEmpty()) {
            return new ArrayList<>();
        }

        List<ThingsModelValueItem> controllableModels = new ArrayList<>();
        String typeHint = resolveThingModelTypeHint(intentRouteResult);
        String businessType = resolveBusinessType(intentRouteResult);
        for (ThingsModelValueItem item : allModels) {
            if (isControllableThingModel(item)) {
                controllableModels.add(item);
            }
        }
        if (controllableModels.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> targetTexts = resolveTargetTexts(question, intentRouteResult, deviceMetaData);
        if (targetTexts.isEmpty()) {
            return new ArrayList<>();
        }

        List<ThingsModelValueItem> exactMatches = matchThingModels(controllableModels, targetTexts, true);
        if (!exactMatches.isEmpty()) {
            return preferCandidatesByRouteHint(exactMatches, typeHint, businessType);
        }
        List<ThingsModelValueItem> fuzzyMatches = matchThingModels(controllableModels, targetTexts, false);
        if (!fuzzyMatches.isEmpty()) {
            return preferCandidatesByRouteHint(fuzzyMatches, typeHint, businessType);
        }
        return preferCandidatesByRouteHint(resolveActionOnlyThingModelCandidates(controllableModels, question, intentRouteResult),
                typeHint, businessType);
    }

    private boolean isControllableThingModel(ThingsModelValueItem item) {
        if (item == null || StringUtils.isBlank(item.getId()) || item.getType() == null) {
            return false;
        }
        if (Objects.equals(ThingsModelTypeEnum.EVENT.getCode(), item.getType())) {
            return false;
        }
        if (Objects.equals(ThingsModelTypeEnum.PROPERTY.getCode(), item.getType()) && Integer.valueOf(1).equals(item.getIsReadonly())) {
            return false;
        }
        return Objects.equals(ThingsModelTypeEnum.PROPERTY.getCode(), item.getType())
                || Objects.equals(ThingsModelTypeEnum.FUNCTION.getCode(), item.getType());
    }

    private List<ThingsModelValueItem> preferCandidatesByRouteHint(List<ThingsModelValueItem> candidates,
                                                                   String typeHint,
                                                                   String businessType) {
        if (candidates == null || candidates.size() <= 1) {
            return candidates == null ? new ArrayList<>() : candidates;
        }
        List<ThingsModelValueItem> preferred = new ArrayList<>();
        for (ThingsModelValueItem item : candidates) {
            if (matchesRouteTypeHint(item, typeHint, businessType)) {
                preferred.add(item);
            }
        }
        return preferred.isEmpty() ? candidates : preferred;
    }

    private boolean matchesRouteTypeHint(ThingsModelValueItem item, String typeHint, String businessType) {
        if (item == null || item.getType() == null) {
            return false;
        }
        if (AiIntentThingModelTypeHint.PROPERTY.name().equals(typeHint)
                || AiIntentBusinessType.DEVICE_PROPERTY_CONTROL.name().equals(businessType)) {
            return Objects.equals(ThingsModelTypeEnum.PROPERTY.getCode(), item.getType());
        }
        if (AiIntentThingModelTypeHint.SERVICE.name().equals(typeHint)
                || AiIntentBusinessType.DEVICE_SERVICE_INVOKE.name().equals(businessType)) {
            return Objects.equals(ThingsModelTypeEnum.FUNCTION.getCode(), item.getType());
        }
        return true;
    }

    private List<String> resolveTargetTexts(String question,
                                            AiChatIntentRouteVO intentRouteResult,
                                            DeviceMetaData deviceMetaData) {
        LinkedHashSet<String> targetTexts = new LinkedHashSet<>();
        addTargetText(targetTexts, resolveThingModelText(question, intentRouteResult));
        addTargetText(targetTexts, extractPossessiveThingModelText(question));
        addTargetText(targetTexts, resolveActionText(question, intentRouteResult));
        addTargetText(targetTexts, normalizeResidualTarget(question, deviceMetaData, intentRouteResult));
        return new ArrayList<>(targetTexts);
    }

    private void addTargetText(Set<String> targetTexts, String targetText) {
        if (targetTexts == null || StringUtils.isBlank(targetText)) {
            return;
        }
        String normalized = normalizeText(targetText);
        if (StringUtils.isNotBlank(normalized)) {
            targetTexts.add(normalized);
        }
    }

    private List<ThingsModelValueItem> matchThingModels(List<ThingsModelValueItem> candidates, List<String> targetTexts, boolean exact) {
        LinkedHashMap<String, ThingsModelValueItem> matched = new LinkedHashMap<>();
        for (String targetText : targetTexts) {
            if (StringUtils.isBlank(targetText)) {
                continue;
            }
            for (ThingsModelValueItem item : candidates) {
                if (item == null || StringUtils.isBlank(item.getId())) {
                    continue;
                }
                if (matchesThingModel(item, targetText, exact)) {
                    matched.putIfAbsent(item.getId(), item);
                }
            }
            if (!matched.isEmpty()) {
                return new ArrayList<>(matched.values());
            }
        }
        return new ArrayList<>();
    }

    private boolean matchesThingModel(ThingsModelValueItem item, String targetText, boolean exact) {
        if (item == null || StringUtils.isBlank(targetText)) {
            return false;
        }
        return AiCandidateMatchSupport.matchesAliases(buildAliases(item), targetText, exact);
    }

    private List<ThingsModelValueItem> resolveActionOnlyThingModelCandidates(List<ThingsModelValueItem> controllableModels,
                                                                            String question,
                                                                            AiChatIntentRouteVO intentRouteResult) {
        List<ThingsModelValueItem> results = new ArrayList<>();
        String actionText = resolveActionText(question, intentRouteResult);
        String actionValue = intentRouteResult == null ? null : intentRouteResult.getActionValue();
        if (!isBooleanControlValue(actionText) && !isBooleanControlValue(actionValue)) {
            return results;
        }
        if (controllableModels == null || controllableModels.isEmpty()) {
            return results;
        }
        for (ThingsModelValueItem item : controllableModels) {
            if (isBooleanThingModel(item)) {
                results.add(item);
            }
        }
        return results;
    }

    private boolean isBooleanControlValue(String value) {
        if (StringUtils.isBlank(value)) {
            return false;
        }
        String normalized = normalizeText(value);
        return "1".equals(normalized)
                || "0".equals(normalized)
                || "true".equals(normalized)
                || "false".equals(normalized)
                || "on".equals(normalized)
                || "off".equals(normalized)
                || containsAny(normalized, "打开", "开启", "启动", "关闭", "关掉", "停用", "停止");
    }

    private boolean isBooleanThingModel(ThingsModelValueItem item) {
        if (item == null || item.getDatatype() == null || StringUtils.isBlank(item.getDatatype().getType())) {
            return false;
        }
        String datatypeName = item.getDatatype().getType().trim().toLowerCase(Locale.ROOT);
        return "bool".equals(datatypeName) || "boolean".equals(datatypeName);
    }

    private List<String> buildAliases(ThingsModelValueItem item) {
        LinkedHashSet<String> aliases = new LinkedHashSet<>();
        addAlias(aliases, item.getId());
        addAlias(aliases, item.getName());
        addAlias(aliases, item.getName_zh_CN());
        addAlias(aliases, item.getName_en_US());
        return new ArrayList<>(aliases);
    }

    private void addAlias(Set<String> aliases, String alias) {
        if (aliases == null || StringUtils.isBlank(alias)) {
            return;
        }
        aliases.add(alias.trim());
        String normalized = alias.trim()
                .replace("设备", "")
                .replace("空气", "")
                .replace("环境", "")
                .replace("当前", "")
                .replace("实时", "")
                .replace("只读", "")
                .replace("-", "")
                .trim();
        if (StringUtils.isNotBlank(normalized)) {
            aliases.add(normalized);
        }
    }

    private String resolveThingModelText(String question, AiChatIntentRouteVO intentRouteResult) {
        if (intentRouteResult != null && StringUtils.isNotBlank(intentRouteResult.getThingModelText())) {
            String thingModelText = intentRouteResult.getThingModelText().trim();
            if (isRouteThingModelTextCredible(question, thingModelText)) {
                return thingModelText;
            }
        }
        return extractPatternValue(question, THING_MODEL_TEXT_PATTERN);
    }

    private boolean isRouteThingModelTextCredible(String question, String thingModelText) {
        String normalizedQuestion = normalizeText(question);
        String normalizedThingModelText = normalizeText(thingModelText);
        return StringUtils.isNotBlank(normalizedQuestion)
                && StringUtils.isNotBlank(normalizedThingModelText)
                && normalizedQuestion.contains(normalizedThingModelText);
    }

    private String extractPossessiveThingModelText(String question) {
        return extractPatternValue(question, POSSESSIVE_THING_MODEL_PATTERN);
    }

    private String resolveActionText(String question, AiChatIntentRouteVO intentRouteResult) {
        if (intentRouteResult != null && StringUtils.isNotBlank(intentRouteResult.getActionText())) {
            return intentRouteResult.getActionText().trim();
        }
        if (StringUtils.isBlank(question)) {
            return null;
        }
        for (String token : ACTION_TOKENS) {
            if (question.contains(token)) {
                return token;
            }
        }
        return null;
    }

    private String resolveActionValue(String question, AiChatIntentRouteVO intentRouteResult, ThingsModelValueItem thingModel) {
        String explicitValue = extractPatternValue(question, VALUE_PATTERN);
        if (StringUtils.isNotBlank(explicitValue)) {
            Object normalized = normalizeValueByThingModel(thingModel, explicitValue.trim());
            return normalized == null ? null : String.valueOf(normalized);
        }
        if (intentRouteResult != null && StringUtils.isNotBlank(intentRouteResult.getActionValue())) {
            Object normalized = normalizeValueByThingModel(thingModel, intentRouteResult.getActionValue().trim());
            if (normalized != null) {
                return String.valueOf(normalized);
            }
        }
        if (intentRouteResult != null && StringUtils.isNotBlank(intentRouteResult.getActionText())) {
            Object normalized = normalizeValueByThingModel(thingModel, intentRouteResult.getActionText().trim());
            if (normalized != null) {
                return String.valueOf(normalized);
            }
        }
        String hintedValue = extractPatternValue(question, ACTION_VALUE_HINT_PATTERN);
        if (StringUtils.isNotBlank(hintedValue)) {
            Object normalized = normalizeValueByThingModel(thingModel, hintedValue.trim());
            if (normalized != null) {
                return String.valueOf(normalized);
            }
        }
        String actionText = resolveActionText(question, intentRouteResult);
        if (StringUtils.isBlank(actionText)) {
            return null;
        }
        Object normalized = normalizeValueByThingModel(thingModel, actionText);
        return normalized == null ? null : String.valueOf(normalized);
    }

    private Object normalizeValueByThingModel(ThingsModelValueItem thingModel, String rawValue) {
        if (thingModel == null || StringUtils.isBlank(rawValue)) {
            return null;
        }
        Datatype datatype = thingModel.getDatatype();
        if (datatype == null || StringUtils.isBlank(datatype.getType())) {
            return sanitizeValue(rawValue);
        }
        String datatypeName = datatype.getType().trim().toLowerCase(Locale.ROOT);
        if ("bool".equals(datatypeName) || "boolean".equals(datatypeName)) {
            return resolveBooleanValue(datatype, rawValue);
        }
        if ("enum".equals(datatypeName)) {
            return resolveEnumValue(datatype.getEnumList(), rawValue);
        }
        if ("integer".equals(datatypeName) || "int".equals(datatypeName)) {
            return parseIntegerValue(rawValue);
        }
        if ("decimal".equals(datatypeName) || "float".equals(datatypeName) || "double".equals(datatypeName)) {
            return parseDecimalValue(rawValue);
        }
        return sanitizeValue(rawValue);
    }

    private Object resolveBooleanValue(Datatype datatype, String rawValue) {
        String normalized = normalizeText(rawValue);
        if (StringUtils.isBlank(normalized)) {
            return null;
        }
        if ("1".equals(normalized) || "true".equals(normalized) || "on".equals(normalized)
                || containsAny(normalized, "打开", "开启", "启动")) {
            return 1;
        }
        if ("0".equals(normalized) || "false".equals(normalized) || "off".equals(normalized)
                || containsAny(normalized, "关闭", "关掉", "停用", "停止")) {
            return 0;
        }
        if (datatype != null) {
            if (StringUtils.isNotBlank(datatype.getTrueText()) && normalized.contains(normalizeText(datatype.getTrueText()))) {
                return 1;
            }
            if (StringUtils.isNotBlank(datatype.getFalseText()) && normalized.contains(normalizeText(datatype.getFalseText()))) {
                return 0;
            }
        }
        return null;
    }

    private Object resolveEnumValue(List<EnumItem> enumItems, String rawValue) {
        if (enumItems == null || enumItems.isEmpty() || StringUtils.isBlank(rawValue)) {
            return null;
        }
        String normalized = normalizeText(rawValue);
        for (EnumItem enumItem : enumItems) {
            if (enumItem == null) {
                continue;
            }
            if (normalized.equals(normalizeText(enumItem.getValue()))
                    || normalized.equals(normalizeText(enumItem.getText()))
                    || normalized.contains(normalizeText(enumItem.getText()))
                    || normalized.contains(normalizeText(enumItem.getValue()))) {
                return enumItem.getValue();
            }
        }
        if (enumItems.size() == 1) {
            EnumItem enumItem = enumItems.get(0);
            return enumItem == null ? null : enumItem.getValue();
        }
        return null;
    }

    private Integer parseIntegerValue(String rawValue) {
        String numericText = extractNumericValue(rawValue, false);
        if (StringUtils.isBlank(numericText)) {
            return null;
        }
        return Integer.parseInt(numericText);
    }

    private BigDecimal parseDecimalValue(String rawValue) {
        String numericText = extractNumericValue(rawValue, true);
        if (StringUtils.isBlank(numericText)) {
            return null;
        }
        return new BigDecimal(numericText);
    }

    private String extractNumericValue(String rawValue, boolean allowDecimal) {
        if (StringUtils.isBlank(rawValue)) {
            return null;
        }
        Matcher matcher = Pattern.compile(allowDecimal ? "-?\\d+(?:\\.\\d+)?" : "-?\\d+").matcher(rawValue);
        return matcher.find() ? matcher.group() : null;
    }

    private String sanitizeValue(String rawValue) {
        if (StringUtils.isBlank(rawValue)) {
            return null;
        }
        String value = rawValue.trim()
                .replace("“", "")
                .replace("”", "")
                .replace("\"", "");
        return StringUtils.isBlank(value) ? null : value;
    }

    private InvokeReqDto buildInvokeRequest(DeviceMetaData deviceMetaData, ThingsModelValueItem thingModel, String actionValue) {
        InvokeReqDto request = new InvokeReqDto();
        request.setSerialNumber(deviceMetaData.getDevice().getSerialNumber());
        request.setIdentifier(thingModel.getId());
        request.setProductId(deviceMetaData.getProduct().getProductId());
        request.setModelName(defaultText(thingModel.getName(), thingModel.getId()));
        request.setType(thingModel.getType());
        LinkedHashMap<String, Object> remoteCommand = new LinkedHashMap<>();
        remoteCommand.put(thingModel.getId(), buildTypedValue(thingModel, actionValue));
        request.setRemoteCommand(remoteCommand);
        return request;
    }

    private MQSendMessageBo buildCommandRequest(DeviceMetaData deviceMetaData, ThingsModelValueItem thingModel, String actionValue) {
        MQSendMessageBo request = new MQSendMessageBo();
        request.setSerialNumber(deviceMetaData.getDevice().getSerialNumber());
        request.setIdentifier(thingModel.getId());
        request.setValue(actionValue);
        com.alibaba.fastjson2.JSONObject params = new com.alibaba.fastjson2.JSONObject();
        params.put(thingModel.getId(), buildTypedValue(thingModel, actionValue));
        request.setParams(params);
        request.setDp(buildDeviceAndProtocol(deviceMetaData));
        return request;
    }

    private Object buildTypedValue(ThingsModelValueItem thingModel, String actionValue) {
        Object value = normalizeValueByThingModel(thingModel, actionValue);
        return value == null ? actionValue : value;
    }

    private DeviceAndProtocol buildDeviceAndProtocol(DeviceMetaData deviceMetaData) {
        DeviceAndProtocol dp = new DeviceAndProtocol();
        if (deviceMetaData == null) {
            return dp;
        }
        Device device = deviceMetaData.getDevice();
        Product product = deviceMetaData.getProduct();
        SubGateway subGateway = deviceMetaData.getSubGateway();
        ModbusParams modbusParams = deviceMetaData.getModbusParams();
        if (device != null) {
            dp.setDeviceId(device.getDeviceId());
            dp.setDeviceName(device.getDeviceName());
            dp.setSerialNumber(device.getSerialNumber());
            dp.setTenantId(device.getTenantId());
            dp.setTenantName(device.getTenantName());
            dp.setCreateBy(device.getCreateBy());
            dp.setDeviceIp(device.getDeviceIp());
            dp.setDevicePort(device.getDevicePort());
            dp.setStatus(device.getStatus());
        }
        if (product != null) {
            dp.setProtocolCode(product.getProtocolCode());
            dp.setProductId(product.getProductId());
            dp.setTransport(product.getTransport());
            dp.setDeviceType(product.getDeviceType());
        }
        if (subGateway != null) {
            dp.setAddress(subGateway.getAddress());
            dp.setGwProductId(subGateway.getParentProductId());
            dp.setGwSerialNumber(subGateway.getParentClientId());
        }
        if (modbusParams != null) {
            dp.setModbusAddress(modbusParams.getAddress());
        }
        return dp;
    }

    private String normalizeResidualTarget(String question, DeviceMetaData deviceMetaData, AiChatIntentRouteVO intentRouteResult) {
        if (StringUtils.isBlank(question)) {
            return null;
        }
        String normalized = normalizeText(question);
        if (StringUtils.isBlank(normalized)) {
            return null;
        }
        if (deviceMetaData != null) {
            Device device = deviceMetaData.getDevice();
            Product product = deviceMetaData.getProduct();
            if (device != null) {
                normalized = removeToken(normalized, device.getDeviceName());
                normalized = removeToken(normalized, device.getSerialNumber());
            }
            if (product != null) {
                normalized = removeToken(normalized, product.getProductName());
            }
        }
        if (intentRouteResult != null) {
            normalized = removeToken(normalized, intentRouteResult.getDeviceNameText());
            normalized = removeToken(normalized, intentRouteResult.getSerialNumberText());
            normalized = removeToken(normalized, intentRouteResult.getProductNameText());
        }
        normalized = removeToken(normalized, extractPatternValue(question, IDENTIFIER_PATTERN));
        normalized = removeToken(normalized, extractPatternValue(question, VALUE_PATTERN));
        normalized = removeToken(normalized, extractPatternValue(question, ACTION_VALUE_HINT_PATTERN));
        for (String token : CONTROL_NOISE_TOKENS) {
            normalized = removeToken(normalized, token);
        }
        return StringUtils.isBlank(normalized) ? null : normalized;
    }

    private String removeToken(String text, String rawToken) {
        if (StringUtils.isBlank(text) || StringUtils.isBlank(rawToken)) {
            return text;
        }
        return text.replace(normalizeText(rawToken), "");
    }

    private String extractPatternValue(String text, Pattern pattern) {
        if (StringUtils.isBlank(text) || pattern == null) {
            return null;
        }
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group(1).trim() : null;
    }

    private String resolveThingModelTypeHint(AiChatIntentRouteVO intentRouteResult) {
        return intentRouteResult == null ? AiIntentThingModelTypeHint.UNKNOWN.name() : intentRouteResult.getThingModelTypeHint();
    }

    private String resolveBusinessType(AiChatIntentRouteVO intentRouteResult) {
        return intentRouteResult == null ? AiIntentBusinessType.UNKNOWN.name() : intentRouteResult.getBusinessType();
    }

    private boolean containsAny(String text, String... tokens) {
        if (StringUtils.isBlank(text) || tokens == null || tokens.length == 0) {
            return false;
        }
        for (String token : tokens) {
            if (StringUtils.isNotBlank(token) && text.contains(token)) {
                return true;
            }
        }
        return false;
    }

    private String normalizeText(String text) {
        return AiCandidateMatchSupport.normalizeText(text);
    }

    private String defaultText(String primary, String fallback) {
        if (StringUtils.isNotBlank(primary)) {
            return primary.trim();
        }
        return StringUtils.isNotBlank(fallback) ? fallback.trim() : null;
    }
}
