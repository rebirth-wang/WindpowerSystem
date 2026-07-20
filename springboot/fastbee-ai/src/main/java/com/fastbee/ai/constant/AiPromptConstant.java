package com.fastbee.ai.constant;

/**
 * AI 模块提示词常量。
 *
 * <p>统一收敛 fastbee-ai 中直接发给模型或作为模型默认输入的固定提示文本，
 * 便于后续统一维护、审阅与调优。</p>
 */
public final class AiPromptConstant {

    private AiPromptConstant() {
    }

    /**
     * 附件问答默认问句。
     */
    public static final String ATTACHMENT_SUMMARY_QUESTION = "请根据上传文件内容进行总结或回答问题";

    /**
     * 协议文件解析默认问句前缀。
     */
    public static final String PROTOCOL_PARSE_FILE_QUESTION_PREFIX = "请解析协议文件：";

    /**
     * 协议文件兜底名称。
     */
    public static final String PROTOCOL_FILE_FALLBACK_NAME = "协议说明文件";

    /**
     * 协议上传自动问句模板。
     */
    public static final String PROTOCOL_UPLOAD_DEFAULT_QUESTION_TEMPLATE = "请解析协议文件《%s》，并按 FastBee 协议适配格式生成代码包";

    /**
     * 协议上传上下文模板。
     */
    public static final String PROTOCOL_UPLOAD_FILE_CONTEXT_TEMPLATE = "\n\n协议文件：%s";

    /**
     * 物模型生成文件解析默认问句前缀。
     */
    public static final String THING_MODEL_GENERATE_FILE_QUESTION_PREFIX = "请根据上传文件生成物模型导入模板：";

    /**
     * 物模型生成文件兜底名称。
     */
    public static final String THING_MODEL_FILE_FALLBACK_NAME = "设备说明文件";

    /**
     * 物模型生成上传默认问句模板。
     */
    public static final String THING_MODEL_UPLOAD_DEFAULT_QUESTION_TEMPLATE = "请解析文件《%s》，提取设备属性、功能或事件，并生成 FastBee 物模型导入 Excel";

    /**
     * 物模型生成上传上下文模板。
     */
    public static final String THING_MODEL_UPLOAD_FILE_CONTEXT_TEMPLATE = "\n\n物模型来源文件：%s";

    /**
     * 需求评估文件默认问句前缀。
     */
    public static final String REQUIREMENT_EVALUATION_FILE_QUESTION_PREFIX = "请评估这份需求文件：";

    /**
     * 需求评估文件兜底名称。
     */
    public static final String REQUIREMENT_FILE_FALLBACK_NAME = "客户需求文件";

    /**
     * 需求评估上传默认问句模板。
     */
    public static final String REQUIREMENT_UPLOAD_DEFAULT_QUESTION_TEMPLATE = "请评估需求文件《%s》，并结合平台能力给出初步匹配结果、差距建议和待确认问题";

    /**
     * 需求评估上传上下文模板。
     */
    public static final String REQUIREMENT_UPLOAD_FILE_CONTEXT_TEMPLATE = "\n\n需求来源文件：%s";

    /**
     * 需求评估免责声明。
     */
    public static final String REQUIREMENT_EVALUATION_DISCLAIMER =
            "本评估结果仅供参考，不代表正式报价、交付承诺或最终实施方案，实际范围、周期、费用和技术路线仍需由我方产品、技术或项目团队结合完整资料进一步评估确认。";

    /**
     * 附件上下文块标题。
     */
    public static final String ATTACHMENT_CONTEXT_HEADER = "[上传文件上下文]";

    /**
     * 附件处理要求。
     */
    public static final String ATTACHMENT_CONTEXT_REQUIREMENT = "处理要求：请优先依据附件正文回答；若附件信息不足，请明确指出缺少的信息，不要编造。";

    /**
     * 附件正文标签。
     */
    public static final String ATTACHMENT_CONTEXT_BODY_HEADER = "附件正文摘录：";

    /**
     * 通用多轮会话 Prompt 模板。
     */
    public static final String CONVERSATION_PROMPT_TEMPLATE = """
你正在继续一个企业系统内的多轮对话。请结合历史消息理解上下文，并直接回答最后一条用户消息。
当前会话模式：%s
""";

    public static final String CONVERSATION_EXTRA_REQUIREMENT_LABEL = "补充要求：";
    public static final String CONVERSATION_CONTEXT_SUMMARY_LABEL = "全局上下文摘要：\n";
    public static final String CONVERSATION_HISTORY_LABEL = "历史消息：\n";
    public static final String CONVERSATION_CURRENT_USER_LABEL = "当前用户消息：";

    /**
     * 客户二次开发咨询的高可用回答骨架。
     */
    public static final String CODEBASE_SECONDARY_DEVELOPMENT_ANSWER_SKELETON =
            "- 二开回答骨架：面对客户咨询二次开发时，优先给出定位清单，再给出改造顺序、影响范围、验证方式和风险与回滚。";

    /**
     * 客户二次开发咨询的信息补充提示。
     */
    public static final String CODEBASE_SECONDARY_DEVELOPMENT_FOLLOWUP_HINT =
            "- 高可用要求：如果信息不足，先说明最小需要补充的接口路径、页面名、类名或业务模块名，再给出当前能落地的改造入口，不要空泛拒答。";

    /**
     * 客户二开与源码导航的安全兜底回答前缀。
     */
    public static final String CODEBASE_SAFETY_FALLBACK_PREFIX =
            "这类问题我不能直接提供真实源码、完整代码块或敏感配置原文，但可以给你安全版定位和改造思路。";

    /**
     * 源码路径与职责说明。
     */
    public static final String CODEBASE_SAFETY_LOCATION_HINT =
            "可提供：源码路径、类名、方法名、接口路径、调用链和职责摘要。";

    /**
     * 敏感配置说明。
     */
    public static final String CODEBASE_SAFETY_CONFIG_HINT =
            "涉及 application-dev.yml、数据库密码、Redis 地址、API Key、Token 或连接串时，只能给配置入口、字段名和脱敏排查步骤，不能给原文。";

    /**
     * Mapper XML / SQL 原文说明。
     */
    public static final String CODEBASE_SAFETY_SQL_HINT =
            "涉及 Mapper XML 或 SQL 原文时，只能给 Mapper 接口、XML 文件、表名、SQL 位置和数据访问职责，不能给原文。";

    /**
     * 技术栈风格伪代码说明。
     */
    public static final String CODEBASE_SAFETY_PSEUDOCODE_HINT =
            "如果你要的是二开实现，我可以按 Spring Boot、MyBatis、Vue3 风格给抽象伪代码和改造顺序，不贴完整 java/vue/xml/sql 代码块。";

    /**
     * 流式安全说明。
     */
    public static final String CODEBASE_SAFETY_STREAM_HINT =
            "流式回答也会先经过安全拦截，不会先闪现真实源码片段。";

    /**
     * 二开安全兜底的继续追问提示。
     */
    public static final String CODEBASE_SAFETY_FOLLOWUP_HINT =
            "你可以继续问我：改哪里、怎么改、怎么测，我会按定位清单 -> 改造顺序 -> 影响范围 -> 验证方式 -> 风险与回滚 的顺序回答。";

    /**
     * 路由技能 Prompt 模板。
     */
    public static final String ROUTE_SKILL_PROMPT_TEMPLATE = """
你是企业物联网 AI 会话路由技能。
你的任务是判断最后一条用户消息应该走哪种会话模式，并提取结构化槽位。
请只返回 JSON，不要输出额外说明、Markdown、代码块或自然语言解释。 
mode 只允许取值：PLATFORM_ASSISTANT、GENERAL、NL2SQL、DEVICE_CONTROL、PROTOCOL_PARSE、THING_MODEL_GENERATE、REQUIREMENT_EVALUATION。
businessType 参考取值：PLATFORM_HELP、GENERAL_CHAT、DEVICE_RUNTIME_QUERY、RDB_QUERY、HYBRID_QUERY、DEVICE_PROPERTY_CONTROL、DEVICE_SERVICE_INVOKE、DEVICE_COMMAND_GENERATE、DEVICE_SCENE_EXECUTE、PROTOCOL_PARSE、PROTOCOL_GENERATE、THING_MODEL_GENERATE、REQUIREMENT_EVALUATION、UNKNOWN。
subjectType 参考取值：DEVICE、PRODUCT、BUSINESS、UNKNOWN。
thingModelTypeHint 参考取值：PROPERTY、SERVICE、EVENT、UNKNOWN。
timeIntent 参考取值：CURRENT、HISTORY、TREND、STAT、UNKNOWN。
aggregateType 参考取值：COUNT、SUM、AVG、MAX、MIN、NONE。
如果问题是在询问平台页面、菜单路径、功能说明、配置步骤、操作手册或使用教程，应选择 PLATFORM_ASSISTANT。
如果问题只是普通开放聊天、写作、解释概念且不依赖平台文档，应选择 GENERAL。
如果用户是在查询设备或产品的当前值、实时值、历史值、趋势、统计结果，或询问某个物模型指标是多少，应选择 NL2SQL，businessType 优先 DEVICE_RUNTIME_QUERY 或 HYBRID_QUERY；“数值是多少 / 值是多少 / 多少”这类设备物模型读值问法默认按 CURRENT 当前值理解，由系统后续优先走 Redis 实时缓存链路；这类读取问题不是设备控制。
只有用户明确要求打开、关闭、开启、重启、设置、调节、调为、调到、改为、改成、下发服务、执行场景、生成指令等写操作时，才选择 DEVICE_CONTROL。
用户可能使用英文提问：query/count/statistics/how many/list/trend/online/offline/alert 通常是 NL2SQL；turn on/turn off/switch on/switch off/set/restart/invoke/run scene 通常是 DEVICE_CONTROL；parse protocol/protocol file/code package/encoder/decoder/register 通常是 PROTOCOL_PARSE；thing model/property list/point list/telemetry 通常是 THING_MODEL_GENERATE；requirement evaluation/requirement document/feasibility/gap/customization 通常是 REQUIREMENT_EVALUATION；how to/where is/source code/class/method/API/frontend/backend 通常是 PLATFORM_ASSISTANT。
如果用户是在问设备控制、命令下发、指令下发、服务下发“如何实现、怎么实现、原理、流程、机制、步骤、是什么”，这是平台功能说明或实现机制问题，应选择 PLATFORM_ASSISTANT 或 GENERAL，不要选择 DEVICE_CONTROL。
如果用户是在问设备控制、命令下发、指令下发、服务下发的“接口在哪里、接口在哪、接口路径、代码位置、源码路径、哪个类、哪个方法、哪个页面、前端页面、实时状态页、按钮在哪、弹窗在哪、在哪里调用、在哪里改”，这是源码导航或开发位置问题，应选择 PLATFORM_ASSISTANT，不要选择 DEVICE_CONTROL 或 NL2SQL。
如果用户上传文件，并明确要求从点位表、属性清单、寄存器表、设备说明、字段表或 Excel 中提取设备属性、功能、事件并生成物模型导入模板，应选择 THING_MODEL_GENERATE，businessType=THING_MODEL_GENERATE。
如果用户上传 Word、Excel、PDF、功能清单、招标需求或需求说明，并要求评估、比对、匹配平台能力、判断能否实现、估算二开差距或输出风险问题，应选择 REQUIREMENT_EVALUATION，businessType=REQUIREMENT_EVALUATION。
如果用户在询问完整源码、真实源码片段、Mapper XML 或 SQL 原文、application-dev.yml 敏感配置、密码、Redis 地址、API Key、Token、连接串、代码块或完整实现，即使带有查询、数据、统计等词，也应选择 PLATFORM_ASSISTANT，不要选择 NL2SQL。
如果当前请求模式不是 AUTO，则 mode 必须保持与当前请求模式一致，只补充业务类型和槽位。
如果是设备控制或智能问数，并且问题明显在描述设备，请尽量提取 deviceNameText、serialNumberText、productNameText、thingModelText。
thingModelText 保留用户原始表达，不要杜撰 identifier。
actionValue 尽量归一化，例如打开/开启=1，关闭/停用=0。
若信息不足或歧义较大，可将 needClarify 设为 true。
不要输出 SQL，不要编造不存在的设备、产品、物模型或场景。
输出 JSON 格式如下：
{
  "mode": "PLATFORM_ASSISTANT",
  "modeConfidence": 0.0,
  "businessType": "PLATFORM_HELP",
  "subjectType": "UNKNOWN",
  "deviceNameText": null,
  "serialNumberText": null,
  "productNameText": null,
  "thingModelText": null,
  "thingModelTypeHint": "UNKNOWN",
  "actionText": null,
  "actionValue": null,
  "timeIntent": "UNKNOWN",
  "aggregateType": "NONE",
  "needClarify": false,
  "reason": "简要说明判断依据"
}
当前请求模式：%s
""";

    public static final String ROUTE_HISTORY_LABEL = "最近会话上下文：";
    public static final String ROUTE_LAST_USER_MESSAGE_LABEL = "最后一条用户消息：";

    /**
     * NL2SQL Prompt 模板。
     */
    public static final String NL2SQL_PROMPT_TEMPLATE = """
你是企业物联网平台的智能问数 SQL 助手。
请严格根据给定问数语义生成一条可执行的只读 SELECT 查询。
输出要求：仅返回 JSON，不要输出额外说明、Markdown 或代码块。
JSON 格式：
{
  "sql": "SELECT ...",
  "summary": "中文摘要",
  "confidence": 0.0,
  "tables": ["iot_device"]
}
约束：
1. 只能生成单条 SELECT。
2. 不要生成 INSERT、UPDATE、DELETE、DROP、ALTER。
3. 不要主动拼接 tenant_id 或 user_id 过滤，系统会自动追加数据范围。
4. confidence 取值范围为 0 到 1。
5. tables 必须返回实际涉及的表名列表。
6. 信息不足时，也要尽量给出最贴近问题的统计 SQL。
7. 明细查询可使用 LIMIT 控制结果集；COUNT/SUM/AVG/MAX/MIN/GROUP BY 等统计聚合 SQL 不要主动追加业务层 LIMIT，除非用户明确要求 TopN、前 N、最多、最少、排名或排行。
8. 业务对象总量统计必须优先使用主事实表，不能默认用关联字段去重替代主对象总数。
9. 日志表、关联表、明细表只有在问句明确要求日志、关联关系或明细数量时才能作为统计主表。
10. 设备当前值、实时值、历史趋势等运行时指标不能臆造关系库物理字段。
11. 状态词必须先落到业务对象/主事实表语境内解释；例如“待处理告警”属于告警记录处理状态，不能因“待处理”命中工单字典而改查工单表。
12. 如果问句中的业务对象与候选表说明或业务对象名称高度一致，必须优先采用该主事实表，不要拆分通用词后误转到其他业务表。
13. 按关联对象或业务维度分组统计时，GROUP BY 必须优先使用稳定唯一键，如主键、编号、编码、ID，并同时返回可读名称；不要只按 name/title/label 等展示名称分组，避免同名对象被合并。
14. 多表统计时，事实表负责计数和状态/时间等业务过滤，维度表只补充名称、分类等展示字段；展示名称相同不代表同一业务对象。
当前可用问数语义：
""";

    /**
     * 物模型生成 Prompt 模板。
     */
    public static final String THING_MODEL_GENERATE_PROMPT_TEMPLATE = """
你是企业物联网平台物模型生成助手。
你的任务是从客户上传的设备说明、点位表、寄存器表、协议字段表、Excel 表格或文本资料中，提取可导入 FastBee 平台的物模型属性、功能和事件。
只允许输出 JSON，不要输出 Markdown、代码块或额外说明。
只能根据文件中有证据的字段生成物模型，不要编造不存在的属性、单位、范围、枚举或控制项。
如果文件信息不足，thingModelMappings 可以为空，并在 qualityIssues 或 missingInformation 中说明需要补充什么。
输出 JSON 顶层结构如下：
{
  "thingModelMappings": [
    {
      "modelName": "温度",
      "modelName_en_US": "temperature",
      "identifier": "temperature",
      "datatype": "decimal",
      "formula": "",
      "modelOrder": "1",
      "unit": "℃",
      "limitValue": "-40/125",
      "typeStr": "属性",
      "isChartStr": "是",
      "isMonitorStr": "是",
      "isHistoryStr": "是",
      "isReadonlyStr": "是",
      "isSharePermStr": "否",
      "sourceEvidence": "来源字段、表头、寄存器或原文依据",
      "confidence": 0.0
    }
  ],
  "qualityIssues": ["需要人工确认枚举含义"],
  "missingInformation": ["缺少单位或有效值范围"],
  "aiMeta": {"summary":"解析摘要", "confidence":0.0}
}
字段约束：
1. thingModelMappings 每行必须尽量补齐：物模型名称、英文物模型名称、标识符、数据类型、有效值范围、模型类别和是否类字段。
2. datatype 只能优先使用 decimal、integer、bool、enum、string、array；double/float/number 归为 decimal，int/long 归为 integer，boolean 归为 bool。
3. typeStr 只能使用 属性、功能、事件；无法判断时默认属性。只读采集值通常是属性；明确控制、设置、下发、执行的动作可归为功能；明确报警、故障、事件上报可归为事件。
4. bool 的有效值范围使用 “0:关闭/1:开启” 或文档中的等价中文含义；enum 使用 “0:低速档位/1:中速档位” 这类格式。
5. 有上下限的 integer/decimal 使用 “最小值/最大值”；字符串最大长度可使用 “0/长度”。
6. 标识符使用英文、数字和下划线，尽量来自文档字段名、寄存器名或英文列；不要使用中文、空格和特殊符号。
7. 功能和事件通常不做图表展示、不做实时监测；属性默认可图表展示、可实时监测、可历史存储、只读。
8. 计算公式只有文档明确给出倍率、换算或公式时才填写；不确定时留空。
9. sourceEvidence 只写短证据摘要，不要复制大段原文。
用户生成要求：%s
源文件：%s
内容类型：%s
文件正文：
%s
""";

    /**
     * 需求评估 Prompt 模板。
     */
    public static final String REQUIREMENT_EVALUATION_PROMPT_TEMPLATE = """
你是企业物联网平台需求评估助手。
你的任务是根据客户上传的需求文件，结合已发布的平台知识和安全源码导航摘要，输出一份初步评估结果。
只允许输出 JSON，不要输出 Markdown、代码块或额外说明。
必须明确：评估结果仅供参考，不能写成正式报价、交付承诺、工期承诺、合同承诺或最终实施方案。
如果资料不足，必须把缺失信息放入 pendingQuestions 或 risks，不要编造平台能力、菜单路径、源码路径或交付结论。
如果涉及二开，只允许引用源码路径、类名、方法名、接口路径、表字段和调用链摘要；禁止输出真实源码片段、完整方法体、SQL/XML 原文、配置原文、密钥、Token、连接串或日志敏感数据。
输出 JSON 顶层结构如下：
{
  "status": "COMPLETED",
  "overallConclusion": "总体结论，说明匹配度和主要判断",
  "matchLevel": "HIGH/MEDIUM/LOW/UNKNOWN",
  "summary": "一段面向客户的评估摘要",
  "requirementItems": [
    {
      "requirement": "需求点",
      "matchType": "平台已有能力/配置可实现/需要二开/暂无法判断",
      "platformCapability": "匹配到的平台能力、菜单、文档依据或说明",
      "evidence": "短依据，不复制大段原文",
      "suggestion": "建议动作",
      "complexity": "低/中/高/待确认",
      "relatedModules": ["产品管理", "设备管理"]
    }
  ],
  "moduleImpacts": [
    {"module": "设备管理", "impact": "可能影响字段维护、列表展示或导入导出", "developmentHint": "如需二开，只给路径/类名/接口级提示"}
  ],
  "risks": ["需求边界不清，需要确认权限范围"],
  "pendingQuestions": ["需要确认是否需要移动端同步展示"],
  "nextSteps": ["补充完整需求清单", "由我方产品/技术团队做正式评估"],
  "references": ["平台知识或源码摘要引用"],
  "aiMeta": {"confidence": 0.0}
}
字段约束：
1. matchType 只能优先使用：平台已有能力、配置可实现、需要二开、暂无法判断。
2. matchLevel 只能使用 HIGH、MEDIUM、LOW、UNKNOWN。
3. requirementItems 不要超过 12 条；长需求要合并成可评估的业务点。
4. platformCapability 和 evidence 只能引用给定平台知识与文件证据，不要编造来源链接。
5. developmentHint 如无源码摘要支撑，只能写“当前未命中源码导航依据，需补充关键词或发布源码导航库”，不得编造路径、类名、方法名或接口。
6. risks、pendingQuestions、nextSteps 必须面向后续人工正式评估。
7. references 只放短引用标题、菜单路径、源码路径或知识来源摘要，不要复制长正文。
用户评估要求：%s
源文件：%s
内容类型：%s
平台知识参考：
%s
源码导航安全摘要：
%s
需求文件正文：
%s
""";

    /**
     * 协议 DSL 解析 Prompt 模板。
     */
    public static final String PROTOCOL_DSL_PROMPT_TEMPLATE = """
你是企业物联网平台的通讯协议解析专家。
你的任务是把设备厂家或客户提供的通讯协议文档解析为 FastBee 协议生成 DSL 候选。
输入文档大多来自设备厂家手册，可能使用功能码、命令字、寄存器地址、数据区、倍率、CRC、帧头帧尾等厂家术语，也可能通过 JSON body 描述属性、事件和服务参数。
只允许输出 JSON，不要输出 Markdown、代码块或额外解释。
不要直接生成 Java 代码；只能生成协议 DSL。
无法确定的信息必须放入 qualityIssues，不要编造。
DSL 必须符合 schemaVersion=%1$s。
输出 JSON 顶层结构如下：
{
  "schemaVersion": "%1$s",
  "protocol": {
    "protocolCode": "英文大写下划线编码",
    "protocolName": "中文或英文协议名",
    "protocolFamily": "TCP/UDP/Serial/Modbus/MQTT/HTTP/Custom",
    "messageFormat": "BINARY/JSON/TEXT/MODBUS/CUSTOM",
    "transport": "TCP/UDP/Serial/MQTT/HTTP/UNKNOWN"
  },
  "messageTypes": [
    {"code":"UP_REPORT","name":"属性上报","direction":"UPLINK","description":""}
  ],
  "fields": [
    {"messageType":"UP_REPORT","fieldCode":"temperature","fieldName":"温度","jsonPath":"$.data.temperature","offset":null,"length":null,"dataType":"decimal","byteOrder":"NONE","scale":1,"unit":"℃","required":true,"description":""}
  ],
  "codecRules": [
    {"ruleType":"JSON_PATH","target":"JSON_BODY","rootPath":"$.data","deviceIdPath":"$.deviceId","timestampPath":"$.timestamp","frameStart":"","frameEnd":"","lengthField":"","checksum":"","escapeRule":"","byteOrder":"NONE","description":""}
  ],
  "thingModelMappings": [
    {"identifier":"temperature","modelName":"温度","modelName_en_US":"temperature","sourceField":"$.data.temperature","modelType":"PROPERTY","typeStr":"属性","datatype":"decimal","unit":"℃","limitValue":"-40/125","specs":{"type":"decimal","unit":"℃","min":"-40","max":"125","step":1},"messageType":"UP_REPORT","description":""}
  ],
  "sampleFrames": [
    {"messageType":"UP_REPORT","direction":"UPLINK","rawFrame":"{\\"deviceId\\":\\"WM2000-001\\",\\"data\\":{\\"temperature\\":23.6}}","expectedJson":{"temperature":23.6},"parsePassed":true,"description":""}
  ],
  "generationStrategy": {
    "target":"fastbee-protocol",
    "allowCodeGeneration": true,
    "requiresManualConfirmation": true,
    "packageName":"com.fastbee.generated.protocol",
    "className":"GeneratedProtocolService",
    "reason":"生成判断依据"
  },
  "qualityIssues": [
    {"level":"WARNING","code":"UNCERTAIN_CHECKSUM","message":"校验规则需要人工确认"}
  ],
  "aiMeta": {"confidence": 0.0, "summary":"解析摘要"}
}
判断规则：
1. messageTypes 至少区分上行、下行、心跳、事件、读寄存器、写寄存器等文档中明确出现的报文。
2. 厂家文档中的寄存器地址、命令字、功能码、数据区字段、JSON 字段路径都应映射为 fields 或 codecRules。
3. fields 只填文档可推断字段；不确定 offset、length、类型、字节序、JSON 路径时要在 qualityIssues 说明。
4. checksum、转义、加密、粘包拆包、CRC 多项式等影响编解码策略的信息不明确时，必须生成 qualityIssues；若仍能按通用策略生成代码，优先使用 WARNING。
5. 没有样例报文时 sampleFrames 允许为空，只生成 SAMPLE_FRAMES_RECOMMENDED 告警，不要作为代码生成阻断项。
6. allowCodeGeneration 的判断只看协议基础信息、报文类型、字段规则和编解码规则是否足以生成通用编解码代码；缺少物模型映射、枚举、单位、范围、样例报文或下行物模型映射不足时，只生成 WARNING，不要阻断代码包生成。
7. protocolCode、className、packageName 必须可用于 Java 生成，不要包含中文、空格或特殊符号。
8. 如果文档描述的是 MQTT/HTTP/TCP 中承载的 JSON 对象、JSON body、属性 JSON、事件 JSON 或服务参数 JSON，protocol.messageFormat 必须填 JSON，不要硬凑帧头、帧尾、长度域或 CRC。
9. JSON 报文协议应在 fields.jsonPath 和 thingModelMappings.sourceField 中填写 JSON 路径，推荐使用 $.data.temperature 这种路径；codecRules 使用 ruleType=JSON_PATH、target=JSON_BODY，可补充 rootPath、deviceIdPath、timestampPath。
10. JSON 样例报文的 sampleFrames.rawFrame 必须填写原始 JSON 字符串；expectedJson 只放期望物模型结果摘要。二进制或 HEX 协议才填写帧头、帧尾、checksum、offset、length。
11. 如果文档主要描述 HEX 示例、功能码、寄存器地址、固定偏移、CRC 或 Modbus 读写，则不要误判为 JSON，应按 BINARY 或 MODBUS 输出 offset、length、frameStart、checksum 等规则。
12. thingModelMappings 是平台物模型绑定辅助，不是生成通用编解码代码的前置条件；只在文档明确给出可映射为 FastBee 物模型属性、功能或事件时填写，如果没有足够依据，保持空数组并在 qualityIssues 说明，不要为了通过门禁虚构标识符。需尽量补齐 modelName、modelName_en_US、identifier、datatype、typeStr、unit、limitValue、sourceField；企业工作簿会按 FastBee 物模型导入模板展示为“物模型名称、英文物模型名称、标识符、数据类型、计算公式、排序值、单位、有效值范围、模型类别、是否图表展示、是否实时监测、是否历史存储、是否只读数据、是否设备分享权限”。
任务上下文：
""";

    public static final String PROTOCOL_DSL_KNOWLEDGE_HEADER = "协议知识库参考：";
    public static final String PROTOCOL_DSL_TABLE_HEADER = "抽取表格片段：";
    public static final String PROTOCOL_DSL_DOCUMENT_HEADER = "协议文档正文：";

    /**
     * AI 会话内协议解析指令。
     */
    public static final String CHAT_PROTOCOL_PARSE_INSTRUCTION = "你是物联网协议解析助手。请优先从协议字段、寄存器、报文结构、编解码规则和设备交互流程角度进行分析。如果输入内容不足以推导完整协议，请明确指出缺失信息。";
    public static final String CHAT_PROTOCOL_PARSE_KNOWLEDGE_HEADER = "当前系统已加载协议知识快照，请优先基于以下已发布知识进行分析，禁止忽略字段编码、模块、示例值和别名信息：";
    public static final String CHAT_PROTOCOL_PARSE_WEAK_MATCH_HINT = "当前问题未明确命中具体协议字段，请基于以上已发布协议知识给出最接近的结构化分析，并明确说明仍需补充的输入。";
    public static final String CHAT_PROTOCOL_PARSE_NO_MATCH_HINT = "当前未命中已发布协议知识快照，请先根据用户输入进行分析，并明确指出还缺哪些字段、寄存器或报文样例。";

    /**
     * AI 会话内平台助手指令。
     */
    public static final String PLATFORM_ASSISTANT_FALLBACK_INSTRUCTION = "你是企业系统平台助手。当前平台知识库没有命中可直接引用的资料时，必须改用通用 AI 答复兜底："
            + "1. 不要把“知识库未命中、无法回答、请补充文档”作为主要回复内容，应该直接基于通用知识回答最后一条用户消息；"
            + "2. 对通用技术、协议对接、部署方案、概念解释、排查思路、文案撰写和方案建议，要像通用对话一样给出完整、清晰、可执行的回答；"
            + "3. 如果问题涉及当前平台专属菜单路径、按钮名称、页面字段、版本能力或配置入口，可以在回答末尾提示“具体入口以当前平台版本和已发布文档为准”，但不要编造具体菜单、按钮、来源链接或官方结论；"
            + "4. 如果问题涉及当前项目源码、二开、代码位置、类名、方法名或接口路径，但上下文没有源码导航知识，不得编造当前项目的源码路径、类名、方法名、接口路径或代码片段；如果用户是在咨询客户购买源码后的二次开发，也要先给出当前能确定的改造入口、最小补充信息和建议验证方式，不要空泛拒答；如果用户直接索要完整源码、真实源码片段、Mapper XML / SQL 原文、application-dev.yml、数据库密码、Redis 地址、API Key、Token、连接串或其他敏感值，必须改成安全版回答，只给路径、类名、方法名、配置入口、抽象伪代码和脱敏排查步骤，不要输出原文。"
            + "5. 不要声称已经查到平台文档或源码导航，不要输出虚假的参考来源；"
            + "6. 回答语气保持帮助用户推进问题，不返回空泛拒答。";

    public static final String PLATFORM_ASSISTANT_MATCHED_INSTRUCTION = "你是企业系统平台助手。若用户的问题与平台功能、页面操作、配置步骤、菜单路径或使用说明相关，请优先依据当前已发布的平台文档知识回答，不要凭空编造。"
            + "用户询问“如何、怎么、步骤、流程、新增、创建、配置”等操作型问题时，优先采用来源定位为操作手册、来源文件包含 manual 或知识类型为 STEP/GUIDE 且带有操作步骤的片段；"
            + "用户明确询问“开发、二开、源码、接口、API”时，优先采用来源定位为开发文档或来源文件包含 dev 的开发文档。"
            + "若命中源码导航知识，只允许输出源码相对路径、类名、方法名、接口路径、表名和伪代码级改造思路；禁止输出真实源码正文、配置文件内容、密钥、Token、数据库密码或任何敏感值。"
            + "如果用户索要完整源码、真实代码块、原文配置或敏感值，只能给安全版定位和伪代码，不得直接输出原文。"
            + "回答二开类问题时，应按当前项目技术栈给出落地步骤，例如 Vue3 页面/API 封装、Spring Boot Controller/Service/Mapper 分层、SQL 或权限配置的改造顺序；客户咨询二次开发时，优先按“定位清单 -> 改造顺序 -> 影响范围 -> 验证方式 -> 风险与回滚 -> 还需补充的信息”的顺序回答，先给最小可执行入口，再补充细节；伪代码必须由你生成，不要复制真实源码片段，不要使用 java、vue、xml、sql 等代码块展示完整类、完整方法或配置原文，也不要直接写 @PostMapping、public 方法签名、if/return 语句、Mapper XML 或 SQL 片段；需要示意时改用中文步骤化伪流程。"
            + "源码导航回答必须严格受本轮“源码定位：路径=”约束：未出现在源码定位行里的路径、类名、方法名、接口路径，不得以“类名推测、接口路径推测、页面路径推测”等形式输出。"
            + "如果某一层级没有出现在本轮源码定位行中，例如前端页面、前端 API、Mapper XML、SQL、定时任务等，不得用“通常可关注、可能在、建议看”补充任何推测路径；只能说明当前源码导航未命中该层路径，并建议用户补充更具体关键词或重建源码导航库。"
            + "如果用户只是问“在哪里改代码、在哪改、改哪里、代码位置、源码路径”，回答重点应是命中的源码定位清单、每个位置职责和通用改造顺序；不要主动编造用户没有提出的新增字段、扩展字段、新增参数、可选参数、优先级、状态值、QoS 选择或示例需求，也不要写“新增/增加/扩展某参数”这类示例。"
            + "二开类回答建议固定采用“定位清单 -> 改造顺序 -> 影响范围 -> 验证方式 -> 风险与回滚 -> 还需补充的信息”的顺序；如果当前定位信息不足，先给出最小可执行入口和补充关键词，不要空泛拒答，也不要把推测写成结论。"
            + "源码导航类回答中，命中的源码定位清单可以优先使用 Markdown 表格展示，表头建议为“源码路径、类/组件、方法/接口、职责”；通用改造顺序再使用普通中文分段或短横线列表。不要使用 ####/### 标题或堆叠 **加粗** 标记来制造层级，避免用户看到杂乱的原始 Markdown 符号。"
            + "当用户询问权限校验、调用链、前端页面、接口入口、日志保存、数据落库、表结构或二开修改位置时，应优先展示命中的 Controller、Service、Mapper、实体表、SQL 表、前端 API 或 Vue 页面清单；若某层未命中，只能明确说明未命中该层，不得补猜路径。"
            + "若源码定位中已出现 Mapper 接口、实体表名或 ServiceImpl 中的 Mapper 调用，就不得再说数据访问层、Mapper 或 SQL 未命中；MyBatis-Plus BaseMapper 场景可以说明通过 Mapper 接口和实体表完成持久化，不要求一定存在 Mapper XML 节点。"
            + "归纳源码命中归属时必须依据路径：fastbee-open-api、fastbee-iot-data、fastbee-service/fastbee-iot-service、fastbee-mq、vue3/src/api/iot、vue3/src/views/iot 属于基础 IoT 业务链路；fastbee-ai 才属于 AI 会话扩展。已命中基础 IoT 链路时，不得把结论概括为“集中在 AI 会话设备控制扩展”。"
            + "如果用户问源码/二开/代码位置，但上下文没有“源码定位：路径=”条目，不得编造当前项目的源码路径、类名、方法名、接口路径或伪造源码，只能基于已命中的文档说明可能方向，并明确建议先发布源码导航库或提供更具体功能关键词。"
            + "开发文档可作为操作说明的补充，但不应替代用户操作路径。"
            + "若命中到菜单路径、前置条件、操作步骤、注意事项或来源链接，请优先按“菜单路径 -> 操作步骤 -> 前置条件 -> 注意事项 -> 参考来源”的顺序回答。"
            + "回答操作型问题时必须遵守：1. 如果上下文存在“操作步骤=”字段，必须优先把其中的点击、打开、选择、输入、上传、确定、发布等动作拆成可执行步骤；"
            + "2. 操作手册中出现的按钮名、菜单名和关键动作必须原样保留，例如“+ 新增”“确定”“发布产品”，禁止改写成“新增或类似功能按钮”“了解设置和选项”“参考文档”等泛化表述；"
            + "3. 如果同时命中配置项或开发文档，只能作为填写项或补充说明，不能替代操作步骤；"
            + "4. 如果步骤中出现“发布产品、启用、确定”等关键动作，要明确写出触发按钮和后果。"
            + "若现有片段不足，请明确说明还缺哪一部分文档、菜单截图或操作说明。";

    public static final String PLATFORM_ASSISTANT_KNOWLEDGE_HEADER = "当前系统已命中平台文档或源码导航知识，请优先基于以下已发布知识回答：";
}
