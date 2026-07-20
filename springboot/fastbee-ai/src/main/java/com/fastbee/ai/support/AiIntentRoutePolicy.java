package com.fastbee.ai.support;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import com.fastbee.ai.model.enums.AiChatMode;
import com.fastbee.common.utils.StringUtils;

/**
 * AI 会话 AUTO 路由的确定性意图护栏。
 */
public final class AiIntentRoutePolicy {

    public static final int PLATFORM_CONSULTATION_ACCEPT_SCORE = 70;
    public static final int DATA_QUERY_ACCEPT_SCORE = 70;
    public static final int DEVICE_CONTROL_ACCEPT_SCORE = 70;

    private static final List<String> DATA_ACTION_TOKENS = List.of(
            "查询", "查一下", "查下", "查看一下", "看一下", "看下", "帮我查", "帮我查询",
            "统计", "列出", "列一下", "获取", "返回", "展示",
            "query", "select", "count", "statistics", "statistic", "list", "show", "display", "fetch", "get"
    );
    private static final List<String> DATA_RESULT_TOKENS = List.of(
            "数量", "总数", "总量", "多少", "是多少", "几个", "几条", "趋势", "分布", "占比",
            "平均", "最大", "最小", "排行", "排名", "top", "记录数", "行数", "列表",
            "count", "total", "howmany", "trend", "distribution", "ratio", "average", "avg",
            "max", "min", "ranking", "rank", "recordcount", "rows", "list"
    );
    private static final List<String> DATA_RUNTIME_TOKENS = List.of(
            "当前", "实时", "现在", "此刻", "最新", "最近", "历史", "当天", "今天", "昨天", "昨日",
            "近1小时", "最近1小时", "最近一小时", "近24小时", "最近24小时",
            "current", "realtime", "now", "latest", "recent", "history", "today", "yesterday", "lasthour"
    );
    private static final List<String> DATA_ENTITY_TOKENS = List.of(
            "设备", "产品", "网关", "告警", "报表", "工单", "用户", "角色", "菜单", "租户",
            "物模型", "日志", "记录", "数据", "数据桥接", "数据中心", "知识库", "协议", "任务",
            "device", "product", "gateway", "alert", "alarm", "report", "ticket", "user", "role",
            "menu", "tenant", "thingmodel", "log", "record", "data", "databridge", "datacenter",
            "knowledgebase", "protocol", "task"
    );
    private static final List<String> DATA_METRIC_TOKENS = List.of(
            "温度", "湿度", "亮度", "电量", "电压", "电流", "功率", "状态", "水位", "压力",
            "浓度", "开关", "灯光", "色值", "指标", "属性", "事件", "功能",
            "temperature", "humidity", "brightness", "battery", "voltage", "current", "power",
            "status", "waterlevel", "pressure", "concentration", "switch", "light",
            "metric", "property", "event", "function"
    );
    private static final List<String> CONSULTATION_TOKENS = List.of(
            "怎么", "如何", "怎样", "为什么", "为啥", "为何", "是否", "能否", "能不能", "会不会",
            "可以吗", "吗", "必须", "一定要", "需要", "才能", "原因", "原理", "机制", "流程",
            "步骤", "作用", "区别", "差异", "含义", "意思", "解释", "说明", "介绍", "详细介绍", "教程",
            "how", "howto", "why", "whether", "can", "could", "will", "must", "needto",
            "reason", "principle", "mechanism", "process", "flow", "steps", "meaning", "explain"
    );
    private static final List<String> DEVICE_CONTROL_ACTION_TOKENS = List.of(
            "打开", "开启", "关闭", "关掉", "重启", "启动", "停止", "暂停", "恢复",
            "设置为", "设为", "调为", "调到", "调成", "调整为", "调整到", "改为", "改成",
            "切换为", "控制为", "下发", "执行场景", "运行场景",
            "turnon", "turnoff", "switchon", "switchoff", "restart", "startdevice", "stopdevice",
            "setto", "setdevice", "adjustto", "controlto", "sendcommand", "runscene", "executecommand"
    );
    private static final List<String> DEVICE_CONTROL_TARGET_TOKENS = List.of(
            "设备", "产品", "网关", "智能开关", "开关", "灯光", "空调", "阀", "继电器",
            "serialnumber", "device", "product", "gateway", "switch", "light", "relay"
    );
    private static final List<String> DEVICE_CONTROL_OBJECT_TOKENS = List.of(
            "开关", "灯光", "色值", "亮度", "模式", "属性", "功能", "服务", "指令", "命令",
            "identifier", "remotecommand", "params", "switch", "light", "brightness", "property", "function", "command"
    );
    private static final List<String> DEVICE_CONTROL_HELP_TOKENS = List.of(
            "如何", "怎么", "怎样", "实现", "原理", "流程", "机制", "步骤", "说明",
            "介绍", "教程", "方案", "设计", "架构", "链路", "过程", "为什么", "是什么",
            "how", "howto", "implement", "implementation", "principle", "process", "flow", "mechanism",
            "step", "steps", "explain", "design", "architecture", "why", "whatis"
    );
    private static final List<String> DEVICE_CONTROL_LOCATION_TOKENS = List.of(
            "在哪里", "在哪", "哪里", "接口在哪里", "接口在哪", "接口路径", "哪个接口", "哪些接口",
            "代码位置", "源码位置", "源码路径", "源码", "代码", "二开", "开发",
            "哪个类", "哪些类", "哪个方法", "哪些方法", "类名", "方法名", "api", "controller", "service",
            "页面", "前端页面", "哪个页面", "哪些页面", "按钮", "弹窗", "在哪里调用", "在哪调用",
            "where", "whereis", "whereare", "codeposition", "sourcepath", "sourcecode", "codepath",
            "whichclass", "whichmethod", "whichapi", "endpoint", "frontend", "backend", "page",
            "button", "dialog", "callsites", "callsite", "invocation", "entrypoint"
    );
    private static final List<String> PLATFORM_SUBJECT_TOKENS = List.of(
            "fastbee", "平台", "系统", "设备管理", "产品管理", "数据中心", "数据桥接",
            "设备", "产品", "emqx", "mqtt", "服务器", "外部服务器", "接入服务器",
            "物模型", "数据库", "报表", "图表", "历史数据", "数据存储", "监测", "导出",
            "固件", "升级", "指令", "命令", "源码", "代码", "二开",
            "platform", "system", "devicemanagement", "productmanagement", "datacenter",
            "databridge", "device", "product", "server", "externalserver", "thingmodel",
            "database", "report", "chart", "historydata", "historicaldata", "datastorage",
            "firmware", "upgrade", "command", "sourcecode", "codebase"
    );

    private AiIntentRoutePolicy() {
    }

    public static String normalizeQuestion(String question) {
        return question == null ? "" : question.trim().replaceAll("\\s+", "").toLowerCase(Locale.ROOT);
    }

    public static boolean isNonExecutionPlatformConsultation(String normalizedQuestion) {
        return evaluateNonExecutionPlatformConsultation(normalizedQuestion)
                .isAccepted(PLATFORM_CONSULTATION_ACCEPT_SCORE);
    }

    public static AiRouteDecision evaluateNonExecutionPlatformConsultation(String normalizedQuestion) {
        String question = normalizeQuestion(normalizedQuestion);
        AiRouteDecision.Builder builder = AiRouteDecision.builder(
                AiChatMode.PLATFORM_ASSISTANT.name(), "NON_EXECUTION_PLATFORM_CONSULTATION");
        if (StringUtils.isBlank(question)) {
            return builder.addBlocker("空问题").build();
        }
        if (isCodeSnippetExplanationQuestion(question)) {
            builder.intentType("CODE_SNIPPET_EXPLANATION")
                    .addScore(95, "代码片段解释意图");
        }
        if (isPlatformDataFormatExampleQuestion(question)) {
            builder.intentType("PLATFORM_DATA_FORMAT_EXAMPLE")
                    .addScore(90, "平台数据格式示例咨询");
        }
        if (isPlatformOperationGuideQuestion(question)) {
            builder.intentType("PLATFORM_OPERATION_GUIDE")
                    .addScore(85, "平台操作步骤咨询");
        }
        if (isPlatformConfigurationTroubleshootingQuestion(question)) {
            builder.intentType("PLATFORM_CONFIGURATION_TROUBLESHOOTING")
                    .addScore(85, "平台配置前提或原因排查");
        }
        if (isDeviceCommandMechanismQuestion(question)) {
            builder.intentType("DEVICE_COMMAND_MECHANISM")
                    .addScore(90, "设备指令机制说明");
        }
        if (isPlatformConceptConsultationQuestion(question)) {
            builder.intentType("PLATFORM_CONCEPT_EXPLANATION")
                    .addScore(75, "平台功能概念解释");
        }
        if (hasDirectDataExecutionIntent(question)
                && !isPlatformConfigurationTroubleshootingQuestion(question)
                && !isPlatformOperationGuideQuestion(question)
                && !isPlatformDataFormatExampleQuestion(question)
                && !isCodeSnippetExplanationQuestion(question)) {
            builder.addBlocker("存在明确查数执行意图");
        }
        if (hasDirectDeviceControlExecutionIntent(question)
                && !isDeviceCommandMechanismQuestion(question)
                && !hasDeviceControlHelpIntent(question)) {
            builder.addBlocker("存在明确设备控制执行意图");
        }
        return builder.build();
    }

    public static boolean isDataQueryExecutionQuestion(String normalizedQuestion) {
        return evaluateDataQueryExecution(normalizedQuestion).isAccepted(DATA_QUERY_ACCEPT_SCORE);
    }

    public static boolean isAssistantModelIdentityQuestion(String normalizedQuestion) {
        String question = normalizeQuestion(normalizedQuestion);
        if (StringUtils.isBlank(question)) {
            return false;
        }
        if (containsAny(question, "物模型", "产品模型", "模型数据", "模型字段", "模型标识符", "模型管理",
                "thingmodel", "productmodel", "modelmanagement")) {
            return false;
        }
        if (containsAny(question,
                "你是谁", "你是什么", "你是哪位", "你叫什么", "你能做什么", "你可以做什么",
                "介绍一下你自己", "介绍你自己", "whoareyou", "whatareyou", "introduceyourself")) {
            return true;
        }
        boolean hasModelSubject = containsAny(question, "大模型", "模型", "llm", "largemodel", "model");
        boolean hasAssistantSubject = containsAny(question,
                "你使用", "你用", "你是", "你是哪", "你当前", "当前对话", "当前ai", "当前助手",
                "当前模型", "当前大模型", "本轮模型", "本轮大模型", "助手使用", "ai使用", "assistant");
        boolean hasIdentityIntent = containsAny(question,
                "是什么", "什么", "哪个", "哪一个", "哪家", "哪款", "使用", "用的", "调用", "接入",
                "版本", "型号", "model", "version");
        return hasModelSubject && hasAssistantSubject && hasIdentityIntent;
    }

    public static boolean isFileContentAnalysisQuestion(String normalizedQuestion) {
        String question = normalizeQuestion(normalizedQuestion);
        if (StringUtils.isBlank(question)) {
            return false;
        }
        boolean hasFileSubject = containsAny(question,
                "这个文件", "该文件", "这份文件", "上传文件", "上传的文件", "附件", "文件中", "文件里",
                "文件内", "文档中", "文档里", "文档内", "json文件", "csv文件", "excel文件", "表格中",
                "file", "attachment", "uploadedfile", "document", "jsonfile", "csvfile", "excelfile", "spreadsheet");
        if (!hasFileSubject) {
            return false;
        }
        return containsAny(question,
                "有哪些问题", "有什么问题", "哪些问题", "数据问题", "问题", "异常", "错误", "不合理",
                "不正确", "是否正确", "检查", "分析", "诊断", "校验", "验证", "质量", "风险",
                "issue", "issues", "problem", "problems", "error", "errors", "invalid", "check",
                "analyze", "analyse", "review", "validate", "quality", "risk");
    }

    public static AiRouteDecision evaluateDataQueryExecution(String normalizedQuestion) {
        String question = normalizeQuestion(normalizedQuestion);
        AiRouteDecision.Builder builder = AiRouteDecision.builder(
                AiChatMode.NL2SQL.name(), "DATA_QUERY_EXECUTION");
        if (StringUtils.isBlank(question)) {
            return builder.addBlocker("空问题").build();
        }
        if (isFileContentAnalysisQuestion(question)) {
            builder.addBlocker("文件或附件内容分析");
        }
        AiRouteDecision platformDecision = evaluateNonExecutionPlatformConsultation(question);
        if (platformDecision.isAccepted(PLATFORM_CONSULTATION_ACCEPT_SCORE)) {
            builder.addBlocker("非执行型平台咨询");
        }
        boolean hasAction = containsAny(question, DATA_ACTION_TOKENS);
        boolean hasResult = containsAny(question, DATA_RESULT_TOKENS);
        boolean hasRuntime = containsAny(question, DATA_RUNTIME_TOKENS);
        boolean hasEntity = containsAny(question, DATA_ENTITY_TOKENS) || hasDeviceSerialSignal(question);
        boolean hasMetric = containsAny(question, DATA_METRIC_TOKENS);
        if (hasAction) {
            builder.addScore(30, "查询/统计动作");
        }
        if (hasResult) {
            builder.addScore(30, "结果口径");
        }
        if (hasRuntime) {
            builder.addScore(20, "实时/历史/时间窗口");
        }
        if (hasEntity) {
            builder.addScore(15, "业务对象");
        }
        if (hasMetric) {
            builder.addScore(15, "运行指标");
        }
        if (hasAction && (hasEntity || hasMetric) && (hasResult || hasRuntime)) {
            builder.addScore(15, "动作和对象口径完整");
        }
        if (!hasAction && hasResult && (hasRuntime || hasMetric) && hasEntity) {
            builder.addScore(20, "隐式当前值/历史值读取");
        }
        if (!hasAction && hasResult && hasRuntime && hasMetric) {
            builder.addScore(10, "隐式指标读取");
        }
        if (hasConsultationModifier(question) && !hasDirectDataExecutionIntent(question)) {
            builder.addBlocker("咨询/解释问法缺少明确查数动作");
            builder.capScore(45);
        }
        if (!hasAction && !hasResult && !hasRuntime) {
            builder.addBlocker("缺少查询统计结果意图");
        }
        if (!hasEntity && !hasMetric && !hasDeviceSerialSignal(question)) {
            builder.addBlocker("缺少可查询业务对象或指标");
        }
        return builder.build();
    }

    public static boolean isDeviceControlExecutionQuestion(String normalizedQuestion) {
        return evaluateDeviceControlExecution(normalizedQuestion).isAccepted(DEVICE_CONTROL_ACCEPT_SCORE);
    }

    public static boolean isDeviceControlHelpQuestion(String normalizedQuestion) {
        String question = normalizeQuestion(normalizedQuestion);
        return hasDeviceControlHelpIntent(question);
    }

    public static boolean isDeviceControlExplainQuestion(String normalizedQuestion) {
        String question = normalizeQuestion(normalizedQuestion);
        if (StringUtils.isBlank(question)
                || !containsAny(question, DEVICE_CONTROL_HELP_TOKENS)) {
            return false;
        }
        return containsDeviceControlSubject(question);
    }

    public static boolean isDeviceControlLocationQuestion(String normalizedQuestion) {
        String question = normalizeQuestion(normalizedQuestion);
        if (StringUtils.isBlank(question)
                || !containsAny(question, DEVICE_CONTROL_LOCATION_TOKENS)) {
            return false;
        }
        return containsDeviceControlSubject(question);
    }

    public static AiRouteDecision evaluateDeviceControlExecution(String normalizedQuestion) {
        String question = normalizeQuestion(normalizedQuestion);
        AiRouteDecision.Builder builder = AiRouteDecision.builder(
                AiChatMode.DEVICE_CONTROL.name(), "DEVICE_CONTROL_EXECUTION");
        if (StringUtils.isBlank(question)) {
            return builder.addBlocker("空问题").build();
        }
        boolean helpIntent = hasDeviceControlHelpIntent(question);
        boolean structuredInvoke = hasStructuredInvokePayload(question);
        if (structuredInvoke && !helpIntent) {
            builder.addScore(100, "结构化设备调用载荷");
        }
        boolean hasAction = containsAny(question, DEVICE_CONTROL_ACTION_TOKENS);
        boolean hasTarget = containsAny(question, DEVICE_CONTROL_TARGET_TOKENS) || hasDeviceSerialSignal(question);
        boolean hasObject = containsAny(question, DEVICE_CONTROL_OBJECT_TOKENS);
        boolean hasValue = hasControlValueSignal(question);
        if (hasAction) {
            builder.addScore(45, "控制动作");
        }
        if (hasTarget) {
            builder.addScore(20, "设备或产品目标");
        }
        if (hasObject) {
            builder.addScore(20, "控制对象");
        }
        if (hasValue) {
            builder.addScore(15, "控制值或参数");
        }
        if (!structuredInvoke && isDeviceCommandMechanismQuestion(question)) {
            builder.addBlocker("设备指令机制咨询");
            builder.capScore(45);
        }
        if (helpIntent) {
            builder.addBlocker("设备控制说明/位置问法");
            builder.capScore(45);
        }
        if (!structuredInvoke && hasConsultationModifier(question) && !hasDirectDeviceControlExecutionIntent(question)) {
            builder.addBlocker("咨询问法缺少明确控制执行意图");
            builder.capScore(45);
        }
        if (!structuredInvoke && !hasAction) {
            builder.addBlocker("缺少控制动作");
        }
        if (!structuredInvoke && !hasTarget) {
            builder.addBlocker("缺少设备或产品目标");
        }
        return builder.build();
    }

    public static boolean isCodeSnippetExplanationQuestion(String normalizedQuestion) {
        if (StringUtils.isBlank(normalizedQuestion)) {
            return false;
        }
        boolean hasExplanationIntent = containsAny(normalizedQuestion,
                "意思是不是", "是不是", "意思是", "含义", "解释", "说明", "这段", "这段代码", "是否",
                "meaning", "explain", "doesitmean", "isthat", "isthis");
        if (!hasExplanationIntent) {
            return false;
        }
        return containsAny(normalizedQuestion,
                "string", "long", "integer", "boolean", "jsonobject", "jsonarray", "jsonutil", "if(",
                "else", "new", "return", "public", "private", "class", "msgcontext", "gettopic",
                "getpayload", "systopic", "syspayload", "protocolcode", "payload", "topic", "parseobj",
                "foreach", "put(", "equals(", ";", "{", "}");
    }

    public static boolean isPlatformOperationGuideQuestion(String normalizedQuestion) {
        if (StringUtils.isBlank(normalizedQuestion) || hasDirectDataExecutionIntent(normalizedQuestion)) {
            return false;
        }
        boolean hasGuideIntent = containsAny(normalizedQuestion,
                "怎么", "如何", "怎样", "步骤", "流程", "操作", "配置", "接入", "怎么做", "如何做",
                "需要做", "要做", "查看", "查看数据", "看到数据", "接在", "接到", "接入到",
                "howto", "steps", "process", "flow", "operate", "operation", "configure", "connect", "setup", "viewdata");
        return hasGuideIntent && containsAny(normalizedQuestion, PLATFORM_SUBJECT_TOKENS);
    }

    public static boolean isPlatformConfigurationTroubleshootingQuestion(String normalizedQuestion) {
        if (StringUtils.isBlank(normalizedQuestion) || hasDirectDataExecutionIntent(normalizedQuestion)) {
            return false;
        }
        boolean hasThresholdExplainIntent = containsAny(normalizedQuestion,
                "至少", "至少要", "至少需要", "多少条才能", "多少条可以", "多少条能", "几条才能",
                "生成曲线", "生成数据曲线", "数据曲线", "曲线", "threshold", "minimum", "atleast", "chart", "curve");
        if (hasStrongDataIntent(normalizedQuestion) && !hasThresholdExplainIntent && !hasConsultationModifier(normalizedQuestion)) {
            return false;
        }
        boolean hasExplainOrTroubleshootIntent = containsAny(normalizedQuestion,
                "必须", "是否必须", "一定要", "需要", "才能", "为什么", "为啥", "为何", "原因",
                "怎么回事", "是否", "能否", "能不能", "能够", "还能", "可以", "是否可以", "吗",
                "开启", "启用", "同时", "进行", "只有", "只有一个", "只能", "不显示", "没有显示", "导出只有", "报表导出",
                "至少", "至少要", "多少条才能", "生成曲线", "生成数据曲线", "数据曲线", "曲线",
                "must", "required", "needto", "haveto", "why", "reason", "only", "exportonly", "troubleshoot",
                "whether", "can", "could", "enable", "enabled", "simultaneously", "threshold", "minimum", "atleast", "chart", "curve");
        return hasExplainOrTroubleshootIntent && containsAny(normalizedQuestion, PLATFORM_SUBJECT_TOKENS);
    }

    public static boolean isDeviceCommandMechanismQuestion(String normalizedQuestion) {
        if (StringUtils.isBlank(normalizedQuestion)) {
            return false;
        }
        boolean hasQuestionIntent = containsAny(normalizedQuestion,
                "如果", "是否", "能否", "能不能", "能够", "能不能够", "可以", "会不会", "会怎样",
                "为什么", "吗", "之后", "再上线", "上线时", "上线的时候", "离线时", "离线期间",
                "if", "whether", "can", "could", "will", "why", "after", "offline", "online", "reconnect");
        if (!hasQuestionIntent) {
            return false;
        }
        boolean hasFirmwareOrOfflineTopic = containsAny(normalizedQuestion,
                "固件", "固件升级", "升级", "升级任务", "升级指令", "ota", "ota升级",
                "离线", "上线", "断线", "重连", "offline", "online", "reconnect", "firmware", "upgrade");
        boolean hasCommandDelivery = containsAny(normalizedQuestion,
                "下发", "下发了", "下发指令", "下发命令", "平台下发", "指令", "命令", "收到", "接收",
                "消息", "队列", "sendcommand", "command", "receive", "message", "queue");
        return hasFirmwareOrOfflineTopic && hasCommandDelivery;
    }

    public static boolean hasStrongDataIntent(String normalizedQuestion) {
        return containsAny(normalizedQuestion,
                "统计", "数量", "总数", "总量", "多少", "列表", "排行", "排名", "趋势", "分布",
                "占比", "平均", "最大", "最小", "记录数", "行数",
                "statistics", "statistic", "count", "total", "howmany", "list", "rank", "ranking",
                "trend", "distribution", "ratio", "average", "avg", "max", "min", "recordcount", "rows");
    }

    public static boolean hasQueryDataIntent(String normalizedQuestion) {
        return containsAny(normalizedQuestion, "查询", "query")
                && containsAny(normalizedQuestion, "数量", "总数", "总量", "多少", "列表", "记录", "数据", "日志",
                "count", "total", "howmany", "list", "record", "data", "log");
    }

    public static boolean isDeviceRuntimeReadQuestion(String normalizedQuestion) {
        return isDeviceRuntimeReadQuestion(normalizedQuestion, false, false);
    }

    public static boolean isDeviceRuntimeReadQuestion(String normalizedQuestion,
                                                      boolean hasResolvedDeviceOrProduct,
                                                      boolean hasResolvedMetric) {
        String question = normalizeQuestion(normalizedQuestion);
        if (StringUtils.isBlank(question)
                || isDeviceControlLocationQuestion(question)
                || isDeviceControlExplainQuestion(question)
                || isDeviceCommandMechanismQuestion(question)
                || evaluateDeviceControlExecution(question).isAccepted(DEVICE_CONTROL_ACCEPT_SCORE)) {
            return false;
        }
        boolean hasRuntimeReadIntent = containsAny(question, DATA_RUNTIME_TOKENS)
                || containsAny(question, DATA_RESULT_TOKENS)
                || containsAny(question, "统计", "总计", "总和", "值", "statistics", "sum", "value");
        if (!hasRuntimeReadIntent) {
            return false;
        }
        boolean hasDeviceSignal = hasResolvedDeviceOrProduct
                || containsAny(question, "设备", "产品", "网关", "device", "product", "gateway")
                || hasDeviceSerialSignal(question);
        boolean hasMetricSignal = hasResolvedMetric
                || containsAny(question, DATA_METRIC_TOKENS)
                || containsAny(question, "指标", "物模型", "pm10", "pm2.5", "pm25", "co2", "二氧化碳", "metric", "thingmodel");
        boolean hasStrongReadPhrase = containsAny(question, "当前", "实时", "现在", "最新", "最近", "历史", "趋势",
                "current", "realtime", "now", "latest", "recent", "history", "trend")
                && containsAny(question, "多少", "是多少", "值", "howmany", "value");
        return hasDeviceSignal && (hasMetricSignal || hasStrongReadPhrase);
    }

    private static boolean isPlatformConceptConsultationQuestion(String normalizedQuestion) {
        if (!containsAny(normalizedQuestion, PLATFORM_SUBJECT_TOKENS)) {
            return false;
        }
        return containsAny(normalizedQuestion,
                "有什么作用", "有啥作用", "有什么用", "有啥用", "作用是什么", "用来做什么",
                "用来干什么", "干什么用", "能做什么", "可以做什么", "介绍", "详细介绍",
                "介绍一下", "解释一下", "详细解释", "说明一下", "详细说明", "是什么",
                "有什么区别", "有何区别", "区别是什么", "有什么不同",
                "有何不同", "差异是什么", "有什么联系", "有何联系", "区别", "差异", "不同", "联系",
                "whatis", "whatdoes", "whatfor", "purpose", "function", "difference", "different",
                "compare", "explain");
    }

    private static boolean hasDirectDataExecutionIntent(String normalizedQuestion) {
        if (isPlatformDataFormatExampleQuestion(normalizedQuestion)
                || isCodeSnippetExplanationQuestion(normalizedQuestion)
                || isFileContentAnalysisQuestion(normalizedQuestion)) {
            return false;
        }
        boolean hasAction = containsAny(normalizedQuestion, DATA_ACTION_TOKENS);
        boolean hasResult = containsAny(normalizedQuestion, DATA_RESULT_TOKENS);
        boolean hasRuntime = containsAny(normalizedQuestion, DATA_RUNTIME_TOKENS);
        boolean hasEntityOrMetric = containsAny(normalizedQuestion, DATA_ENTITY_TOKENS)
                || containsAny(normalizedQuestion, DATA_METRIC_TOKENS)
                || hasDeviceSerialSignal(normalizedQuestion);
        return hasAction && hasEntityOrMetric && (hasResult || hasRuntime || containsAny(normalizedQuestion, "数据", "记录", "日志", "data", "record", "log"));
    }

    private static boolean isPlatformDataFormatExampleQuestion(String normalizedQuestion) {
        if (StringUtils.isBlank(normalizedQuestion)) {
            return false;
        }
        boolean hasFormatExampleIntent = containsAny(normalizedQuestion,
                "标准格式", "数据格式", "入库格式", "标准结构", "标准数据", "样例", "示例", "例子",
                "举例", "一条标准", "一条数据", "列出一条", "给一条", "sample", "example", "format", "schema");
        if (!hasFormatExampleIntent) {
            return false;
        }
        boolean hasTransformOrMappingTopic = containsAny(normalizedQuestion,
                "原始数据", "上报数据", "上报的原始数据", "属性定义", "物模型属性", "物模型的属性定义",
                "结合物模型", "转换为", "转为", "标准标识符", "字段映射", "映射", "时序数据库",
                "物联网数据表", "入库", "存入", "payload", "thingmodel", "timeseries", "mapping");
        boolean hasPlatformSubject = containsAny(normalizedQuestion, PLATFORM_SUBJECT_TOKENS)
                || containsAny(normalizedQuestion, "原始数据", "上报数据", "物联网数据表", "时序数据库", "属性定义");
        return hasTransformOrMappingTopic && hasPlatformSubject;
    }

    private static boolean hasDirectDeviceControlExecutionIntent(String normalizedQuestion) {
        return hasStructuredInvokePayload(normalizedQuestion)
                || containsAny(normalizedQuestion, DEVICE_CONTROL_ACTION_TOKENS)
                && (containsAny(normalizedQuestion, DEVICE_CONTROL_TARGET_TOKENS) || hasDeviceSerialSignal(normalizedQuestion));
    }

    private static boolean hasDeviceControlHelpIntent(String normalizedQuestion) {
        if (isDeviceControlLocationQuestion(normalizedQuestion)) {
            return true;
        }
        return isDeviceControlExplainQuestion(normalizedQuestion);
    }

    private static boolean containsDeviceControlSubject(String normalizedQuestion) {
        return containsAny(normalizedQuestion, "设备控制", "设备下发", "设备命令", "命令下发", "指令下发",
                "服务下发", "下发命令", "控制命令", "控制指令", "下发指令",
                "下发给设备", "下发到设备", "发送给设备", "发送到设备",
                "打开设备", "关闭设备", "开启设备", "控制设备",
                "devicecontrol", "devicecommand", "commanddispatch", "serviceinvoke", "sendcommand",
                "controlcommand", "turnondevice", "turnoffdevice", "controldevice");
    }

    private static boolean hasStructuredInvokePayload(String normalizedQuestion) {
        boolean hasJsonPayload = normalizedQuestion.contains("invoke")
                && normalizedQuestion.contains("{")
                && normalizedQuestion.contains("}")
                && normalizedQuestion.contains("serialnumber")
                && normalizedQuestion.contains("identifier");
        boolean hasKeyValuePayload = normalizedQuestion.contains("invoke")
                && normalizedQuestion.contains("serialnumber=")
                && normalizedQuestion.contains("identifier=")
                && (normalizedQuestion.contains("remotecommand") || normalizedQuestion.contains("params"));
        return hasJsonPayload || hasKeyValuePayload
                || normalizedQuestion.contains("invokenoreply")
                || normalizedQuestion.contains("invokereply")
                || normalizedQuestion.contains("commandgenerate")
                || normalizedQuestion.contains("runscene");
    }

    private static boolean hasControlValueSignal(String normalizedQuestion) {
        return normalizedQuestion.matches(".*\\d+.*")
                || containsAny(normalizedQuestion, "开", "关", "开启", "关闭", "true", "false", "on", "off");
    }

    private static boolean hasConsultationModifier(String normalizedQuestion) {
        return containsAny(normalizedQuestion, CONSULTATION_TOKENS);
    }

    private static boolean hasDeviceSerialSignal(String normalizedQuestion) {
        return normalizedQuestion.matches(".*device[a-z0-9_\\-]+.*")
                || normalizedQuestion.matches(".*sn[a-z0-9_\\-]+.*")
                || normalizedQuestion.matches(".*serialnumber[a-z0-9_=:\\-\"']+.*");
    }

    public static boolean containsAny(String text, Collection<String> tokens) {
        if (StringUtils.isBlank(text) || tokens == null || tokens.isEmpty()) {
            return false;
        }
        for (String token : tokens) {
            if (StringUtils.isNotBlank(token) && text.contains(token)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsAny(String text, String... tokens) {
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
}
