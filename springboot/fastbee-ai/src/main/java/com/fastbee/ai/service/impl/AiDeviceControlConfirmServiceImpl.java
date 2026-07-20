package com.fastbee.ai.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fastbee.ai.model.dto.AiChatSendRequest;
import com.fastbee.ai.model.enums.AiIntentBusinessType;
import com.fastbee.ai.model.vo.AiChatIntentRouteVO;
import com.fastbee.ai.model.vo.AiClarifyOptionVO;
import com.fastbee.ai.model.vo.AiClarifyPayloadVO;
import com.fastbee.ai.model.vo.AiDeviceControlIntentVO;
import com.fastbee.ai.service.IAiClarifySupportService;
import com.fastbee.ai.service.IAiDeviceControlConfirmService;
import com.fastbee.ai.support.AiCandidateMatchSupport;
import com.fastbee.ai.support.AiClarifyConstants;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.enums.ThingsModelTypeEnum;
import com.fastbee.iot.model.DeviceMetaData;
import com.fastbee.iot.model.ThingsModelItem.Datatype;
import com.fastbee.iot.model.ThingsModels.ThingsModelValueItem;

/**
 * AI 设备控制确认策略服务实现。
 */
@Service
public class AiDeviceControlConfirmServiceImpl implements IAiDeviceControlConfirmService {

    private static final String SKILL_DEVICE_CONFIRM = "device_control_confirm";

    private static final List<String> STOP_ACTION_TOKENS = List.of(
            "关闭", "关掉", "停用", "停止", "暂停", "重启", "锁定", "解锁", "断电", "停机"
    );

    private static final List<String> SENSITIVE_THING_MODEL_TOKENS = List.of(
            "阈值", "上限", "下限", "告警", "报警", "目标", "参数", "配置", "模式", "设定"
    );

    @Resource
    private IAiClarifySupportService aiClarifySupportService;

    @Override
    public AiClarifyPayloadVO buildRiskConfirmPayload(String question,
                                                      AiChatIntentRouteVO intentRouteResult,
                                                      AiDeviceControlIntentVO controlIntent) {
        if (StringUtils.isBlank(question) || controlIntent == null || controlIntent.getInvokeRequest() == null) {
            return null;
        }
        if (isRiskConfirmedQuestion(question) || isCommandGenerate(intentRouteResult, question)) {
            return null;
        }
        List<String> riskReasons = collectRiskReasons(question, intentRouteResult, controlIntent);
        if (riskReasons.isEmpty()) {
            return null;
        }
        return aiClarifySupportService.buildPayload(
                AiClarifyConstants.CLARIFY_TYPE_CONFIRM,
                AiClarifyConstants.CLARIFY_KEY_DEVICE_CONTROL_CONFIRM,
                "当前控制命中了高风险策略，请确认后再继续执行。",
                buildConfirmContent(controlIntent, riskReasons),
                SKILL_DEVICE_CONFIRM,
                question,
                buildConfirmOptions()
        );
    }

    @Override
    public boolean isRiskConfirmedQuestion(String question) {
        if (StringUtils.isBlank(question)) {
            return false;
        }
        return question.contains("riskConfirmed=1") || question.contains("已确认高风险设备控制");
    }

    @Override
    public boolean isApprovedSelection(AiChatSendRequest request) {
        if (request == null || !AiClarifyConstants.CLARIFY_KEY_DEVICE_CONTROL_CONFIRM.equals(request.getClarifyKey())) {
            return false;
        }
        return AiClarifyConstants.CONFIRM_OPTION_CONTINUE.equalsIgnoreCase(trimToEmpty(request.getSelectedValue()));
    }

    @Override
    public boolean isRejectedSelection(AiChatSendRequest request) {
        if (request == null || !AiClarifyConstants.CLARIFY_KEY_DEVICE_CONTROL_CONFIRM.equals(request.getClarifyKey())) {
            return false;
        }
        return AiClarifyConstants.CONFIRM_OPTION_CANCEL.equalsIgnoreCase(trimToEmpty(request.getSelectedValue()));
    }

    @Override
    public String buildRejectedAnswer(AiChatSendRequest request) {
        String label = request == null ? null : request.getSelectedLabel();
        if (StringUtils.isBlank(label) || AiClarifyConstants.CONFIRM_OPTION_CANCEL.equalsIgnoreCase(label.trim())) {
            return "已取消本次高风险设备控制，系统不会继续向设备下发指令。";
        }
        return "已按你的选择“" + label.trim() + "”取消本次高风险设备控制，系统不会继续向设备下发指令。";
    }

    private boolean isCommandGenerate(AiChatIntentRouteVO intentRouteResult, String question) {
        if (intentRouteResult != null
                && AiIntentBusinessType.DEVICE_COMMAND_GENERATE.name().equals(intentRouteResult.getBusinessType())) {
            return true;
        }
        return StringUtils.isNotBlank(question)
                && (question.contains("指令生成") || question.contains("生成指令") || question.contains("commandGenerate"));
    }

    private List<String> collectRiskReasons(String question,
                                            AiChatIntentRouteVO intentRouteResult,
                                            AiDeviceControlIntentVO controlIntent) {
        LinkedHashSet<String> reasons = new LinkedHashSet<>();
        if (isServiceInvoke(intentRouteResult, controlIntent)) {
            reasons.add("当前控制对象属于服务调用，可能触发设备侧实际动作");
        }
        if (isStopLikeAction(question, intentRouteResult, controlIntent)) {
            reasons.add("当前动作包含关闭、停止、停用或重启等停机类指令");
        }
        if (isSensitiveThingModel(controlIntent.getThingModel())) {
            reasons.add("当前控制对象属于阈值、告警、配置或模式类敏感参数");
        }
        return new ArrayList<>(reasons);
    }

    private boolean isServiceInvoke(AiChatIntentRouteVO intentRouteResult, AiDeviceControlIntentVO controlIntent) {
        if (intentRouteResult != null
                && AiIntentBusinessType.DEVICE_SERVICE_INVOKE.name().equals(intentRouteResult.getBusinessType())) {
            return true;
        }
        ThingsModelValueItem thingModel = controlIntent == null ? null : controlIntent.getThingModel();
        if (thingModel == null || thingModel.getType() == null) {
            return false;
        }
        return ThingsModelTypeEnum.FUNCTION.getCode().equals(thingModel.getType());
    }

    private boolean isStopLikeAction(String question,
                                     AiChatIntentRouteVO intentRouteResult,
                                     AiDeviceControlIntentVO controlIntent) {
        String normalizedText = AiCandidateMatchSupport.normalizeText(String.join(" ",
                trimToEmpty(question),
                intentRouteResult == null ? "" : trimToEmpty(intentRouteResult.getActionText()),
                intentRouteResult == null ? "" : trimToEmpty(intentRouteResult.getActionValue()),
                controlIntent == null ? "" : trimToEmpty(controlIntent.getActionText()),
                controlIntent == null ? "" : trimToEmpty(controlIntent.getActionValue())
        ));
        if (containsAnyNormalized(normalizedText, STOP_ACTION_TOKENS)) {
            return true;
        }
        String normalizedValue = AiCandidateMatchSupport.normalizeText(controlIntent == null ? null : controlIntent.getActionValue());
        if ("0".equals(normalizedValue) || "false".equals(normalizedValue) || "off".equals(normalizedValue)) {
            return true;
        }
        return false;
    }

    private boolean isSensitiveThingModel(ThingsModelValueItem thingModel) {
        if (thingModel == null) {
            return false;
        }
        String normalizedThingModel = AiCandidateMatchSupport.normalizeText(String.join(" ",
                trimToEmpty(thingModel.getId()),
                trimToEmpty(thingModel.getName()),
                trimToEmpty(thingModel.getName_zh_CN()),
                trimToEmpty(thingModel.getName_en_US())
        ));
        if (containsAnyNormalized(normalizedThingModel, SENSITIVE_THING_MODEL_TOKENS)) {
            return true;
        }
        Datatype datatype = thingModel.getDatatype();
        if (datatype == null || StringUtils.isBlank(datatype.getType())) {
            return false;
        }
        String datatypeName = datatype.getType().trim().toLowerCase(Locale.ROOT);
        return "struct".equals(datatypeName) || "array".equals(datatypeName) || "object".equals(datatypeName);
    }

    private boolean containsAnyNormalized(String normalizedText, List<String> tokens) {
        if (StringUtils.isBlank(normalizedText) || tokens == null || tokens.isEmpty()) {
            return false;
        }
        for (String token : tokens) {
            String normalizedToken = AiCandidateMatchSupport.normalizeText(token);
            if (StringUtils.isNotBlank(normalizedToken) && normalizedText.contains(normalizedToken)) {
                return true;
            }
        }
        return false;
    }

    private String buildConfirmContent(AiDeviceControlIntentVO controlIntent, List<String> riskReasons) {
        StringBuilder builder = new StringBuilder("当前控制命中了高风险策略，请确认后再继续执行。");
        DeviceMetaData deviceMetaData = controlIntent == null ? null : controlIntent.getDeviceMetaData();
        if (deviceMetaData != null && deviceMetaData.getDevice() != null) {
            builder.append("\n- 设备：").append(defaultText(deviceMetaData.getDevice().getDeviceName(), deviceMetaData.getDevice().getSerialNumber()));
            if (StringUtils.isNotBlank(deviceMetaData.getDevice().getSerialNumber())) {
                builder.append("（").append(deviceMetaData.getDevice().getSerialNumber()).append("）");
            }
        }
        ThingsModelValueItem thingModel = controlIntent == null ? null : controlIntent.getThingModel();
        if (thingModel != null) {
            builder.append("\n- 控制对象：").append(defaultText(thingModel.getName(), thingModel.getId()));
            if (StringUtils.isNotBlank(thingModel.getId())) {
                builder.append("（").append(thingModel.getId()).append("）");
            }
        }
        if (controlIntent != null && StringUtils.isNotBlank(controlIntent.getActionValue())) {
            builder.append("\n- 拟写入值：").append(controlIntent.getActionValue());
        }
        builder.append("\n- 风险原因：");
        for (String riskReason : riskReasons) {
            builder.append("\n  - ").append(riskReason);
        }
        builder.append("\n请选择“继续执行”或“取消执行”。");
        return builder.toString();
    }

    private List<AiClarifyOptionVO> buildConfirmOptions() {
        List<AiClarifyOptionVO> options = new ArrayList<>();

        AiClarifyOptionVO continueOption = new AiClarifyOptionVO();
        continueOption.setLabel("继续执行");
        continueOption.setValue(AiClarifyConstants.CONFIRM_OPTION_CONTINUE);
        continueOption.setDescription("确认继续向设备下发本次高风险控制");
        continueOption.setType(AiClarifyConstants.CLARIFY_TYPE_CONFIRM);
        options.add(continueOption);

        AiClarifyOptionVO cancelOption = new AiClarifyOptionVO();
        cancelOption.setLabel("取消执行");
        cancelOption.setValue(AiClarifyConstants.CONFIRM_OPTION_CANCEL);
        cancelOption.setDescription("取消本次控制，不再向设备下发指令");
        cancelOption.setType(AiClarifyConstants.CLARIFY_TYPE_CONFIRM);
        options.add(cancelOption);

        return options;
    }

    private String defaultText(String primary, String fallback) {
        return StringUtils.isNotBlank(primary) ? primary.trim() : StringUtils.defaultIfBlank(fallback, "-");
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}
