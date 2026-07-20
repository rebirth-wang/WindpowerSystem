package com.fastbee.ai.support;

import java.util.Collection;
import java.util.List;

import com.fastbee.common.utils.StringUtils;

/**
 * AUTO 分层路由的本地特征抽取器。
 */
public final class AiRouteFeatureExtractor {

    private static final List<String> DEFAULT_PLATFORM_FEATURE_TOKENS = List.of(
            "数据桥接", "数据中心", "数据可视化", "知识库", "会话观测", "规则引擎",
            "场景联动", "协议适配", "协议解析", "产品物模型", "物模型", "云云对接",
            "运行状态", "数据采集", "数据调试", "实时监测", "监测统计", "告警统计",
            "设备控制", "设备下发", "设备命令", "命令下发", "指令下发", "服务下发", "下发命令",
            "下发给设备", "下发到设备", "发送给设备", "发送到设备", "打开设备", "关闭设备", "开启设备", "控制设备",
            "源码导航", "源码路径", "二开助手", "类名", "方法名", "接口路径",
            "datacenter", "dashboard", "knowledgebase", "chatobservability", "ruleengine", "sceneautomation",
            "protocoladaptation", "protocolparse", "productthingmodel", "thingmodel", "cloudintegration",
            "runtimestatus", "datacollection", "datadebug", "realtimemonitoring", "alertstatistics",
            "devicecontrol", "devicecommand", "commanddispatch", "serviceinvoke", "sourcenavigation",
            "sourcepath", "secondarydevelopment", "classname", "methodname", "endpoint"
    );
    private static final List<String> PLATFORM_DOMAIN_TOKENS = List.of(
            "fastbee", "平台", "系统", "设备管理", "产品管理", "数据中心", "数据桥接",
            "设备", "产品", "物模型", "报表", "图表", "历史数据", "实时监测", "数据存储",
            "数据库", "emqx", "mqtt", "服务器", "外部服务器", "接入服务器",
            "固件", "升级", "指令", "命令", "菜单", "页面", "模块", "配置入口",
            "platform", "system", "devicemanagement", "productmanagement", "datacenter",
            "databridge", "device", "product", "thingmodel", "report", "chart", "historydata",
            "database", "server", "externalserver", "firmware", "upgrade", "command", "menu", "page", "module"
    );
    private static final List<String> PROTOCOL_TOKENS = List.of(
            "协议文件", "解析协议", "协议解析", "协议适配", "上传协议", "代码包", "生成代码",
            "编解码", "报文解析", "寄存器", "dsl", "modbus",
            "protocolfile", "parseprotocol", "protocolparse", "protocoladapter", "protocoladaptation",
            "generatecode", "codepackage", "encoder", "decoder", "codec", "register", "frame"
    );
    private static final List<String> THING_MODEL_TOKENS = List.of(
            "物模型生成", "生成物模型", "物模型导入", "物模型模板", "导入模板", "产品物模型",
            "设备属性", "属性清单", "点位表", "点表", "测点", "遥测",
            "thingmodel", "generatethingmodel", "thingmodelimport", "importtemplate", "productmodel",
            "deviceproperty", "propertylist", "pointlist", "pointtable", "telemetry"
    );
    private static final List<String> THING_MODEL_ACTION_TOKENS = List.of(
            "生成", "导入", "模板", "提取", "解析文件", "上传文件", "excel",
            "generate", "import", "template", "extract", "parsefile", "uploadfile"
    );
    private static final List<String> REQUIREMENT_TOKENS = List.of(
            "需求评估", "评估需求", "需求比对", "需求匹配", "需求分析", "需求文档", "需求文件",
            "需求说明", "功能清单", "招标需求", "招标文件", "客户需求", "可行性", "能否实现",
            "是否支持", "差距", "缺口", "改造评估", "二开评估", "工作量评估",
            "requirement", "requirements", "requirementevaluation", "evaluaterequirement",
            "requirementdocument", "feasibility", "capabilitymatch", "gap", "risk", "workload"
    );
    private static final List<String> REQUIREMENT_ACTION_TOKENS = List.of(
            "评估", "比对", "匹配", "可行性", "能否实现", "是否支持", "差距", "缺口",
            "风险", "工作量", "二开", "改造",
            "evaluate", "compare", "match", "feasibility", "support", "gap", "risk", "workload"
    );
    private static final List<String> CODEBASE_TOKENS = List.of(
            "源码", "代码", "二开", "二次开发", "客户二开", "源码购买", "买源码", "源码交付",
            "改代码", "代码位置", "源码位置", "源码路径", "定制", "定制开发",
            "改造", "扩展", "自定义", "类名", "方法名", "接口路径", "接口在哪里", "接口在哪", "在哪里改", "在哪改",
            "改哪里", "怎么改", "前端", "后端", "controller", "service", "mapper", "api",
            "按钮", "弹窗", "哪个页面", "哪些页面", "页面在哪里", "菜单在哪里", "在哪里调用", "在哪调用",
            "哪里调用", "调用位置", "权限校验", "哪个接口", "哪些接口", "调用链", "入口在哪里",
            "sourcecode", "codebase", "sourcepath", "codepath", "secondarydevelopment",
            "customization", "modifycode", "changecode", "wherechange", "howtochange",
            "whichclass", "whichmethod", "endpoint", "frontend", "backend", "callsite", "callchain"
    );
    private static final List<String> GENERAL_KNOWLEDGE_TOKENS = List.of(
            "怎么做", "如何做", "做法", "制作", "菜谱", "食谱", "帮我写", "写一篇",
            "总结一下", "翻译", "润色", "你知道", "知道", "百科", "是什么", "是谁",
            "介绍一下", "介绍下", "给我介绍", "手机", "电脑", "汽车",
            "recipe", "cook", "cooking", "write", "tellmeabout", "whatis", "whois",
            "summarize", "translate", "polish"
    );
    private static final List<String> CONSULTATION_TOKENS = List.of(
            "怎么", "如何", "怎样", "在哪", "哪里", "配置", "创建", "新增", "使用", "操作",
            "步骤", "流程", "说明", "介绍", "作用", "区别", "为什么", "是否", "能否", "能不能",
            "可以", "会不会", "必须", "需要", "吗", "是不是", "意思", "含义", "原理", "机制",
            "how", "where", "configure", "create", "add", "operation", "steps", "explain", "why"
    );
    private static final List<String> PLATFORM_FEATURE_EXPLAIN_TOKENS = List.of(
            "有什么作用", "有啥作用", "有什么用", "有啥用", "作用是什么", "用来做什么",
            "用来干什么", "干什么用", "能做什么", "可以做什么", "介绍一下", "解释一下",
            "说明一下", "是什么", "有什么区别", "有何区别", "区别是什么", "有什么不同",
            "有何不同", "差异是什么", "有什么联系", "有何联系", "区别", "差异", "不同", "联系",
            "如何", "怎么", "怎样", "如何实现", "怎么实现", "怎样实现", "实现的", "实现原理", "实现机制",
            "实现流程", "原理", "流程", "步骤", "怎么做", "如何做", "需要做", "机制",
            "whatis", "whatdoes", "whatfor", "purpose", "function", "difference", "different", "compare",
            "howto", "howdoes", "implementation", "principle", "process", "flow", "mechanism", "explain"
    );

    public static List<String> defaultPlatformFeatureTokens() {
        return DEFAULT_PLATFORM_FEATURE_TOKENS;
    }

    public AiRouteFeatures extract(AiAutoRouteContext context, Collection<String> platformFeatureTokens) {
        String question = context == null ? "" : context.getNormalizedQuestion();
        boolean fileReview = context != null && context.hasAttachment()
                || AiIntentRoutePolicy.isFileContentAnalysisQuestion(question);
        boolean assistantModelIdentity = AiIntentRoutePolicy.isAssistantModelIdentityQuestion(question);
        boolean requirementEvaluation = isRequirementEvaluation(question);
        boolean thingModelGenerate = isThingModelGenerate(question);
        boolean protocolParse = isProtocolParse(question);
        boolean codebaseGuide = isCodebaseGuide(question);
        boolean platformDomain = hasPlatformDomain(question, platformFeatureTokens) || codebaseGuide;
        boolean platformConsultation = AiIntentRoutePolicy.isNonExecutionPlatformConsultation(question)
                || isPlatformFeatureHelpQuestion(question, platformFeatureTokens)
                || isThingModelFieldMappingHelpQuestion(question)
                || AiIntentRoutePolicy.isDeviceControlHelpQuestion(question)
                || platformDomain && AiIntentRoutePolicy.containsAny(question, CONSULTATION_TOKENS);
        AiRouteDecision dataDecision = AiIntentRoutePolicy.evaluateDataQueryExecution(question);
        AiRouteDecision deviceDecision = AiIntentRoutePolicy.evaluateDeviceControlExecution(question);
        boolean generalKnowledge = isGeneralKnowledge(question, platformDomain, requirementEvaluation,
                thingModelGenerate, protocolParse, codebaseGuide);
        return new AiRouteFeatures(fileReview, assistantModelIdentity, requirementEvaluation,
                thingModelGenerate, protocolParse, codebaseGuide, platformDomain, platformConsultation,
                dataDecision, deviceDecision, generalKnowledge);
    }

    private boolean isRequirementEvaluation(String question) {
        if (StringUtils.isBlank(question)) {
            return false;
        }
        if (AiIntentRoutePolicy.containsAny(question, "需求评估", "评估需求", "requirementevaluation", "evaluaterequirement")) {
            return true;
        }
        return AiIntentRoutePolicy.containsAny(question, REQUIREMENT_TOKENS)
                && AiIntentRoutePolicy.containsAny(question, REQUIREMENT_ACTION_TOKENS);
    }

    private boolean isThingModelGenerate(String question) {
        return StringUtils.isNotBlank(question)
                && AiIntentRoutePolicy.containsAny(question, THING_MODEL_TOKENS)
                && AiIntentRoutePolicy.containsAny(question, THING_MODEL_ACTION_TOKENS);
    }

    private boolean isProtocolParse(String question) {
        return StringUtils.isNotBlank(question) && AiIntentRoutePolicy.containsAny(question, PROTOCOL_TOKENS);
    }

    private boolean isCodebaseGuide(String question) {
        if (StringUtils.isBlank(question)) {
            return false;
        }
        if (AiIntentRoutePolicy.isCodeSnippetExplanationQuestion(question)) {
            return false;
        }
        if (AiIntentRoutePolicy.containsAny(question, CODEBASE_TOKENS)) {
            return true;
        }
        boolean hasUiSurface = AiIntentRoutePolicy.containsAny(question, "前端", "页面", "按钮", "弹窗", "表单", "列表", "字段",
                "frontend", "page", "button", "dialog", "form", "list", "field");
        boolean hasChangeIntent = AiIntentRoutePolicy.containsAny(question, "改", "修改", "新增", "扩展", "调整", "开发",
                "change", "modify", "add", "extend", "adjust", "develop");
        return hasUiSurface && hasChangeIntent;
    }

    public boolean isCodebaseGuideQuestion(String question) {
        return isCodebaseGuide(AiIntentRoutePolicy.normalizeQuestion(question));
    }

    public boolean isSecondaryDevelopmentQuestion(String question) {
        String normalizedQuestion = AiIntentRoutePolicy.normalizeQuestion(question);
        if (StringUtils.isBlank(normalizedQuestion)) {
            return false;
        }
        return AiIntentRoutePolicy.containsAny(normalizedQuestion, "二开", "二次开发", "定制", "定制开发",
                "改造", "扩展", "自定义", "客户化", "客户二开", "源码购买", "买源码", "买了源码",
                "购买源码", "源码交付", "secondarydevelopment", "customization", "customdevelopment",
                "extend", "extension", "sourcecode");
    }

    public boolean isThingModelFieldMappingHelpQuestion(String question) {
        String normalizedQuestion = AiIntentRoutePolicy.normalizeQuestion(question);
        if (StringUtils.isBlank(normalizedQuestion)
                || AiIntentRoutePolicy.hasStrongDataIntent(normalizedQuestion)
                || AiIntentRoutePolicy.hasQueryDataIntent(normalizedQuestion)) {
            return false;
        }
        boolean hasMappingIntent = AiIntentRoutePolicy.containsAny(normalizedQuestion, "映射", "对应", "转换",
                "转成", "关联", "绑定", "mapping", "map", "convert", "bind");
        if (!hasMappingIntent) {
            return false;
        }
        boolean hasSourceField = AiIntentRoutePolicy.containsAny(normalizedQuestion,
                "原始数据字段", "原始字段", "上报字段", "数据字段", "字段名", "字段编码", "/property/post",
                "rawfield", "sourcefield", "reportfield", "datafield", "fieldname", "fieldcode")
                || normalizedQuestion.contains("mqtt");
        boolean hasThingModelTarget = AiIntentRoutePolicy.containsAny(normalizedQuestion, "物模型", "thingmodel")
                && (AiIntentRoutePolicy.containsAny(normalizedQuestion, "标识符", "标准标识符", "标准字段")
                || normalizedQuestion.contains("identifier"));
        return hasSourceField && hasThingModelTarget;
    }

    public boolean isPlatformFeatureHelpQuestion(String question, Collection<String> platformFeatureTokens) {
        String normalizedQuestion = AiIntentRoutePolicy.normalizeQuestion(question);
        if (StringUtils.isBlank(normalizedQuestion)
                || AiIntentRoutePolicy.hasStrongDataIntent(normalizedQuestion)
                || AiIntentRoutePolicy.hasQueryDataIntent(normalizedQuestion)) {
            return false;
        }
        if (!AiIntentRoutePolicy.containsAny(normalizedQuestion, PLATFORM_FEATURE_EXPLAIN_TOKENS)) {
            return false;
        }
        if (hasPlatformDomain(normalizedQuestion, platformFeatureTokens)) {
            return true;
        }
        return AiIntentRoutePolicy.containsAny(normalizedQuestion, "平台上", "平台里的", "平台中的", "页面上",
                "页面里的", "菜单里", "模块里", "platform", "page", "menu", "module");
    }

    public boolean isPlatformConsultationQuestion(String question, Collection<String> platformFeatureTokens) {
        AiRouteFeatures features = extract(AiAutoRouteContext.builder(question).build(), platformFeatureTokens);
        return features.isPlatformConsultation() || features.isCodebaseGuide();
    }

    public boolean isGeneralQuestion(String question, Collection<String> platformFeatureTokens) {
        AiRouteFeatures features = extract(AiAutoRouteContext.builder(question).build(), platformFeatureTokens);
        return features.isFileReview() || features.isGeneralKnowledge();
    }

    private boolean hasPlatformDomain(String question, Collection<String> platformFeatureTokens) {
        if (AiIntentRoutePolicy.containsAny(question, PLATFORM_DOMAIN_TOKENS)) {
            return true;
        }
        return platformFeatureTokens != null && AiIntentRoutePolicy.containsAny(question, platformFeatureTokens);
    }

    private boolean isGeneralKnowledge(String question,
                                       boolean platformDomain,
                                       boolean requirementEvaluation,
                                       boolean thingModelGenerate,
                                       boolean protocolParse,
                                       boolean codebaseGuide) {
        if (StringUtils.isBlank(question) || platformDomain || requirementEvaluation
                || thingModelGenerate || protocolParse || codebaseGuide) {
            return false;
        }
        return AiIntentRoutePolicy.containsAny(question, GENERAL_KNOWLEDGE_TOKENS);
    }

    public static final class AiRouteFeatures {

        private final boolean fileReview;
        private final boolean assistantModelIdentity;
        private final boolean requirementEvaluation;
        private final boolean thingModelGenerate;
        private final boolean protocolParse;
        private final boolean codebaseGuide;
        private final boolean platformDomain;
        private final boolean platformConsultation;
        private final AiRouteDecision dataDecision;
        private final AiRouteDecision deviceDecision;
        private final boolean generalKnowledge;

        private AiRouteFeatures(boolean fileReview,
                                boolean assistantModelIdentity,
                                boolean requirementEvaluation,
                                boolean thingModelGenerate,
                                boolean protocolParse,
                                boolean codebaseGuide,
                                boolean platformDomain,
                                boolean platformConsultation,
                                AiRouteDecision dataDecision,
                                AiRouteDecision deviceDecision,
                                boolean generalKnowledge) {
            this.fileReview = fileReview;
            this.assistantModelIdentity = assistantModelIdentity;
            this.requirementEvaluation = requirementEvaluation;
            this.thingModelGenerate = thingModelGenerate;
            this.protocolParse = protocolParse;
            this.codebaseGuide = codebaseGuide;
            this.platformDomain = platformDomain;
            this.platformConsultation = platformConsultation;
            this.dataDecision = dataDecision;
            this.deviceDecision = deviceDecision;
            this.generalKnowledge = generalKnowledge;
        }

        public boolean isFileReview() {
            return fileReview;
        }

        public boolean isAssistantModelIdentity() {
            return assistantModelIdentity;
        }

        public boolean isRequirementEvaluation() {
            return requirementEvaluation;
        }

        public boolean isThingModelGenerate() {
            return thingModelGenerate;
        }

        public boolean isProtocolParse() {
            return protocolParse;
        }

        public boolean isCodebaseGuide() {
            return codebaseGuide;
        }

        public boolean isPlatformDomain() {
            return platformDomain;
        }

        public boolean isPlatformConsultation() {
            return platformConsultation;
        }

        public AiRouteDecision getDataDecision() {
            return dataDecision;
        }

        public AiRouteDecision getDeviceDecision() {
            return deviceDecision;
        }

        public boolean isGeneralKnowledge() {
            return generalKnowledge;
        }
    }
}
