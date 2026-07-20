package com.fastbee.ai.model.enums;

import java.util.Locale;

import com.fastbee.common.utils.StringUtils;

/**
 * AI 路由技能识别出的业务子类型。
 */
public enum AiIntentBusinessType {

    GENERAL_CHAT,
    DEVICE_RUNTIME_QUERY,
    RDB_QUERY,
    HYBRID_QUERY,
    DEVICE_PROPERTY_CONTROL,
    DEVICE_SERVICE_INVOKE,
    DEVICE_COMMAND_GENERATE,
    DEVICE_SCENE_EXECUTE,
    PROTOCOL_PARSE,
    PROTOCOL_GENERATE,
    THING_MODEL_GENERATE,
    REQUIREMENT_EVALUATION,
    UNKNOWN;

    /**
     * 归一化业务子类型编码。
     *
     * @param rawValue 模型返回值
     * @return 标准编码
     */
    public static String normalizeCode(String rawValue) {
        if (StringUtils.isBlank(rawValue)) {
            return UNKNOWN.name();
        }
        String normalized = rawValue.trim().toUpperCase(Locale.ROOT)
                .replace('-', '_')
                .replace(' ', '_');
        return switch (normalized) {
            case "GENERAL", "GENERAL_CHAT", "CHAT", "通用对话", "普通对话" -> GENERAL_CHAT.name();
            case "DEVICE_RUNTIME_QUERY", "DEVICE_QUERY", "RUNTIME_QUERY", "设备问数", "设备运行时问数" -> DEVICE_RUNTIME_QUERY.name();
            case "RDB_QUERY", "RDB_SQL", "SQL_QUERY", "关系库问数", "业务问数" -> RDB_QUERY.name();
            case "HYBRID_QUERY", "HYBRID_PIPELINE", "多源问数", "混合问数" -> HYBRID_QUERY.name();
            case "DEVICE_PROPERTY_CONTROL", "PROPERTY_CONTROL", "属性控制", "属性写入" -> DEVICE_PROPERTY_CONTROL.name();
            case "DEVICE_SERVICE_INVOKE", "SERVICE_INVOKE", "服务调用", "设备服务调用" -> DEVICE_SERVICE_INVOKE.name();
            case "DEVICE_COMMAND_GENERATE", "COMMAND_GENERATE", "指令生成" -> DEVICE_COMMAND_GENERATE.name();
            case "DEVICE_SCENE_EXECUTE", "SCENE_EXECUTE", "RUN_SCENE", "场景执行", "场景触发" -> DEVICE_SCENE_EXECUTE.name();
            case "PROTOCOL_PARSE", "协议解析", "协议理解" -> PROTOCOL_PARSE.name();
            case "PROTOCOL_GENERATE", "协议生成", "协议骨架生成" -> PROTOCOL_GENERATE.name();
            case "THING_MODEL_GENERATE", "THING_MODEL", "物模型生成", "物模型导入", "生成物模型" -> THING_MODEL_GENERATE.name();
            case "REQUIREMENT_EVALUATION", "REQUIREMENT_ASSESSMENT", "需求评估", "需求匹配", "需求比对", "需求分析" ->
                    REQUIREMENT_EVALUATION.name();
            default -> UNKNOWN.name();
        };
    }
}
