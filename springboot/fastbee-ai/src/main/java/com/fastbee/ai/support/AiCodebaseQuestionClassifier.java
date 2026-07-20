package com.fastbee.ai.support;

import java.util.List;
import java.util.Locale;

import com.fastbee.common.utils.StringUtils;

/**
 * 源码导航、二开和源码安全问句分类器。
 */
public final class AiCodebaseQuestionClassifier {

    private static final List<String> CODEBASE_ASSISTANT_QUESTION_TOKENS = List.of(
            "源码", "代码", "二开", "二次开发", "客户二开", "源码购买", "买源码", "源码交付", "开发", "改代码", "代码位置", "源码位置", "源码路径",
            "定制", "定制开发", "改造", "扩展", "自定义", "客户化",
            "类名", "方法名", "接口路径", "接口在哪里", "接口在哪", "在哪里改", "在哪改", "改哪里", "怎么改",
            "前端", "后端", "controller", "service", "mapper", "api", "vue", "sql",
            "按钮", "弹窗", "哪个页面", "哪些页面", "在哪里调用", "在哪调用", "调用位置",
            "权限校验", "在哪里校验", "日志保存", "在哪里保存", "回执结果", "在哪里更新",
            "哪个接口", "哪些接口", "页面在哪里", "菜单在哪里", "哪里处理", "在哪处理",
            "哪个表", "哪张表", "数据表", "表结构", "调用链", "入口在哪里",
            "sourcecode", "codebase", "sourcepath", "codepath", "secondarydevelopment", "customization",
            "customdevelopment", "modifycode", "changecode", "wherechange", "wheretochange", "howtochange",
            "whichclass", "whichmethod", "endpoint", "api", "frontend", "backend", "controller", "service",
            "mapper", "vue", "sql", "button", "dialog", "page", "callsite", "invocation", "permissioncheck",
            "logsaved", "receiptresult", "whichinterface", "whichapi", "database", "table", "tableschema",
            "callchain", "entrypoint"
    );
    private static final List<String> CODEBASE_LOCATION_ONLY_TOKENS = List.of(
            "在哪里改", "在哪改", "改哪里", "代码位置", "源码位置", "源码路径",
            "哪个类", "哪些类", "哪个方法", "哪些方法", "接口路径",
            "在哪里校验", "哪里校验", "在哪里保存", "哪里保存", "在哪里更新", "哪里更新",
            "哪个接口", "哪些接口", "哪个页面", "哪些页面", "页面在哪里", "菜单在哪里",
            "哪里处理", "在哪处理", "哪个表", "哪张表", "调用位置", "调用链", "入口在哪里",
            "wherechange", "wheretochange", "codeposition", "sourcepath", "codepath",
            "whichclass", "whichmethod", "endpoint", "whichinterface", "whichapi", "whichpage",
            "pagepath", "menupath", "wherehandle", "whichtable", "table", "callsite", "callchain", "entrypoint"
    );
    private static final List<String> CODEBASE_DEVICE_CONTROL_IOT_SOURCE_TOKENS = List.of(
            "fastbee-open-api", "fastbee-iot-data", "fastbee-service/fastbee-iot-service",
            "fastbee-mq", "vue3/src/api/iot", "vue3/src/views/iot",
            "deviceruntimecontroller", "devicemessagecontroller", "ifunctioninvoke",
            "functioninvoke", "mqttmessagepublish", "devicemessageserviceimpl",
            "functionlogserviceimpl", "functionlogmapper", "iot_function_log",
            "preauthorize", "haspermi", "service/invoke", "service/invokereply", "commandgenerate"
    );
    private static final List<String> CODEBASE_DEVICE_CONTROL_AI_SOURCE_TOKENS = List.of(
            "fastbee-ai", "aidevicecontrol", "aichatserviceimpl"
    );

    private AiCodebaseQuestionClassifier() {
    }

    public static String normalize(String question) {
        return StringUtils.defaultString(question).trim().replaceAll("\\s+", "").toLowerCase(Locale.ROOT);
    }

    public static boolean isLikelyCodebaseQuestion(String question) {
        return AiIntentRoutePolicy.containsAny(normalize(question), CODEBASE_ASSISTANT_QUESTION_TOKENS);
    }

    public static boolean isLocationOnlyQuestion(String question) {
        return AiIntentRoutePolicy.containsAny(normalize(question), CODEBASE_LOCATION_ONLY_TOKENS);
    }

    public static boolean isDeviceControlCodeQuestion(String question) {
        String normalizedQuestion = normalize(question);
        return AiIntentRoutePolicy.containsAny(normalizedQuestion, "设备控制", "设备下发", "设备命令",
                "命令下发", "指令下发", "服务下发", "下发命令", "控制设备",
                "devicecontrol", "devicecommand", "commanddispatch", "serviceinvoke", "sendcommand", "controldevice")
                && AiIntentRoutePolicy.containsAny(normalizedQuestion, "源码", "代码", "二开", "开发",
                "在哪里改", "在哪改", "改哪里", "接口", "类", "方法",
                "sourcecode", "code", "secondarydevelopment", "development", "wherechange", "endpoint", "class", "method");
    }

    public static boolean isGenericInterfaceCallSiteQuestion(String question) {
        String normalizedQuestion = normalize(question);
        return AiIntentRoutePolicy.containsAny(normalizedQuestion, "某个接口", "某个方法", "某个api", "某个API", "这个接口", "该接口")
                && AiIntentRoutePolicy.containsAny(normalizedQuestion, "在哪里调用", "在哪调用", "哪些页面调用", "前端页面调用",
                "在哪里前端页面调用", "调用位置");
    }

    public static boolean isCodebaseSafetyQuestion(String question) {
        String normalizedQuestion = normalize(question);
        if (StringUtils.isBlank(normalizedQuestion)) {
            return false;
        }
        return isRawCodebaseLeakQuestion(normalizedQuestion)
                || isSensitiveConfigLeakQuestion(normalizedQuestion)
                || isCompleteImplementationLeakQuestion(normalizedQuestion)
                || isStreamingSourceLeakQuestion(normalizedQuestion);
    }

    public static boolean isRawCodebaseLeakQuestion(String question) {
        String normalizedQuestion = normalize(question);
        if (StringUtils.isBlank(normalizedQuestion)) {
            return false;
        }
        if (AiIntentRoutePolicy.containsAny(normalizedQuestion, "完整源码", "真实源码", "源码原文", "源码片段", "完整代码", "真实代码",
                "完整方法体", "完整类", "完整方法", "源码全文", "整段源码", "贴出源码", "发我源码", "给我源码", "贴出原文", "代码原文")) {
            return true;
        }
        return AiIntentRoutePolicy.containsAny(normalizedQuestion, "源码", "代码", "mapper", "sql", "xml")
                && AiIntentRoutePolicy.containsAny(normalizedQuestion, "原文", "真实", "完整", "全文", "贴出", "发我", "给我", "展示", "再解释");
    }

    public static boolean isSensitiveConfigLeakQuestion(String question) {
        String normalizedQuestion = normalize(question);
        if (StringUtils.isBlank(normalizedQuestion)) {
            return false;
        }
        return AiIntentRoutePolicy.containsAny(normalizedQuestion, "application-dev.yml", "application.yml", "数据库密码", "redis地址",
                "redis密码", "apikey", "api-key", "token", "密钥", "secret", "连接串", "凭证", "配置原文")
                && AiIntentRoutePolicy.containsAny(normalizedQuestion, "发我", "给我", "贴出", "贴出来", "原文", "完整", "展示", "返回", "告诉我", "是什么", "多少", "值");
    }

    public static boolean isSqlXmlLeakQuestion(String question) {
        String normalizedQuestion = normalize(question);
        if (StringUtils.isBlank(normalizedQuestion)) {
            return false;
        }
        return AiIntentRoutePolicy.containsAny(normalizedQuestion, "mapper", "xml", "sql")
                && AiIntentRoutePolicy.containsAny(normalizedQuestion, "原文", "真实", "完整", "贴出", "贴出来", "发我", "给我", "展示", "代码块", "是什么", "源码");
    }

    public static boolean isCompleteImplementationLeakQuestion(String question) {
        String normalizedQuestion = normalize(question);
        if (StringUtils.isBlank(normalizedQuestion)) {
            return false;
        }
        return AiIntentRoutePolicy.containsAny(normalizedQuestion, "代码块", "完整实现", "完整代码", "整套实现", "完整功能", "一段完整", "完整二开")
                && AiIntentRoutePolicy.containsAny(normalizedQuestion, "java", "vue", "xml", "sql");
    }

    public static boolean isStreamingSourceLeakQuestion(String question) {
        String normalizedQuestion = normalize(question);
        if (StringUtils.isBlank(normalizedQuestion)) {
            return false;
        }
        return AiIntentRoutePolicy.containsAny(normalizedQuestion, "流式", "stream", "边输出", "实时输出", "一边输出")
                && AiIntentRoutePolicy.containsAny(normalizedQuestion, "真实源码", "源码片段", "真实代码", "原文", "代码块", "贴出", "解释");
    }

    public static boolean containsDeviceControlIotSource(String normalizedLine) {
        return AiIntentRoutePolicy.containsAny(normalizedLine, CODEBASE_DEVICE_CONTROL_IOT_SOURCE_TOKENS);
    }

    public static boolean containsDeviceControlAiSource(String normalizedLine) {
        return AiIntentRoutePolicy.containsAny(normalizedLine, CODEBASE_DEVICE_CONTROL_AI_SOURCE_TOKENS);
    }
}
