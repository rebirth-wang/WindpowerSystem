package com.fastbee.ai.service.impl;

import static com.fastbee.ai.support.AiI18nMessageUtils.message;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.annotation.Resource;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fastbee.ai.config.properties.FastBeeAiProperties;
import com.fastbee.ai.domain.AiKnowledgeBase;
import com.fastbee.ai.domain.AiKnowledgeDocument;
import com.fastbee.ai.domain.AiKnowledgeVersion;
import com.fastbee.ai.model.vo.AiCodebaseGuideContextVO;
import com.fastbee.ai.model.vo.AiCodebaseGuideItemVO;
import com.fastbee.ai.model.vo.AiKnowledgeTemplateUploadResultVO;
import com.fastbee.ai.service.IAiCodebaseGuideKnowledgeService;
import com.fastbee.ai.service.IAiKnowledgeBaseService;
import com.fastbee.ai.service.IAiKnowledgeDocumentService;
import com.fastbee.ai.service.IAiKnowledgeVersionService;
import com.fastbee.ai.support.AiSecuritySupport;
import com.fastbee.common.config.RuoYiConfig;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.utils.StringUtils;

/**
 * 源码导航知识服务实现。
 *
 * <p>扫描结果只保留安全摘要：源码相对路径、类名、方法名、接口路径、表名与二开提示；
 * 不写入方法体、配置值、密钥或真实源码片段。</p>
 */
@Service
public class AiCodebaseGuideKnowledgeServiceImpl implements IAiCodebaseGuideKnowledgeService {

    private static final String KB_TYPE_CODEBASE_GUIDE = "CODEBASE_GUIDE";
    private static final String KNOWLEDGE_STATUS_ENABLED = "0";
    private static final String RUNTIME_SOURCE_VERSION_SNAPSHOT_ITEM = "VERSION_SNAPSHOT_ITEM";
    private static final String RUNTIME_SOURCE_EMPTY = "EMPTY";
    private static final String SCAN_DOCUMENT_FILE_NAME = "codebase-guide-snapshot.json";
    private static final String SOURCE_ORIGIN_OFFICIAL = "OFFICIAL";
    private static final String SOURCE_ORIGIN_CUSTOM = "CUSTOM";
    private static final String DOCUMENT_STATUS_SUCCESS = "SUCCESS";
    private static final String DOCUMENT_ENABLED = "0";
    private static final int IMPORTED_DOCUMENT_SORT_NUM = 20;
    private static final int MAX_PROMPT_ITEMS = 8;
    private static final int MIN_MATCH_SCORE = 4;
    private static final long MAX_SCAN_FILE_BYTES = 1024 * 1024L;
    private static final long MAX_UPLOAD_SNAPSHOT_BYTES = 30 * 1024 * 1024L;
    private static final Pattern PACKAGE_PATTERN = Pattern.compile("^\\s*package\\s+([a-zA-Z0-9_.]+)\\s*;");
    private static final Pattern CLASS_PATTERN = Pattern.compile("\\b(class|interface|enum)\\s+([A-Za-z0-9_]+)");
    private static final Pattern METHOD_PATTERN = Pattern.compile("^\\s*(public|protected|private)\\s+(?:static\\s+)?(?:final\\s+)?[A-Za-z0-9_<>, ?\\[\\].]+\\s+([A-Za-z0-9_]+)\\s*\\(([^)]*)\\)\\s*(?:throws\\s+[^{;]+)?[\\{;].*");
    private static final Pattern METHOD_NAME_PATTERN = Pattern.compile("^\\s*(public|protected|private)\\s+(?:static\\s+)?(?:final\\s+)?[A-Za-z0-9_<>, ?\\[\\].]+\\s+([A-Za-z0-9_]+)\\s*\\(");
    private static final Pattern MAPPING_PATTERN = Pattern.compile("@(RequestMapping|GetMapping|PostMapping|PutMapping|DeleteMapping|PatchMapping)\\s*(?:\\(([^)]*)\\))?");
    private static final Pattern STRING_LITERAL_PATTERN = Pattern.compile("\"([^\"]+)\"|'([^']+)'");
    private static final Pattern REQUEST_METHOD_PATTERN = Pattern.compile("RequestMethod\\.([A-Z]+)");
    private static final Pattern ANNOTATION_NAME_PATTERN = Pattern.compile("@([A-Za-z0-9_$.]+)");
    private static final Pattern PERMISSION_LITERAL_PATTERN = Pattern.compile("hasPermi\\s*\\(\\s*['\\\"]([^'\\\"]+)['\\\"]\\s*\\)");
    private static final Pattern TABLE_NAME_ANNOTATION_PATTERN = Pattern.compile("@TableName\\s*\\(\\s*['\\\"]([^'\\\"]+)['\\\"]");
    private static final Pattern JAVA_FIELD_PATTERN = Pattern.compile("^\\s*(?:private|protected|public)\\s+(?:static\\s+)?(?:final\\s+)?([A-Za-z0-9_.$<>?, ]+)\\s+([A-Za-z0-9_]+)\\s*(?:=|;).*");
    private static final Pattern JAVA_CALL_PATTERN = Pattern.compile("\\b([A-Za-z_][A-Za-z0-9_]*)\\.([A-Za-z_][A-Za-z0-9_]*)\\s*\\(");
    private static final Pattern EXPORT_FUNCTION_PATTERN = Pattern.compile("\\bexport\\s+function\\s+([A-Za-z0-9_]+)\\s*\\(");
    private static final Pattern FRONTEND_IMPORT_PATTERN = Pattern.compile("\\bimport\\s*\\{([^}]+)\\}\\s*from\\s*['\"][^'\"]+['\"]");
    private static final Pattern VUE_ARROW_FUNCTION_PATTERN = Pattern.compile("\\b(?:const|let|var)\\s+([A-Za-z0-9_]+)\\s*=\\s*(?:async\\s*)?(?:\\([^)]*\\)|[A-Za-z0-9_]+)\\s*=>");
    private static final Pattern VUE_FUNCTION_DECL_PATTERN = Pattern.compile("\\bfunction\\s+([A-Za-z0-9_]+)\\s*\\(");
    private static final Pattern URL_PATTERN = Pattern.compile("\\burl\\s*:\\s*['\"]([^'\"]+)['\"]");
    private static final Pattern FRONTEND_LITERAL_API_PATH_PATTERN = Pattern.compile("['\"](/[^'\"]{2,160})['\"]");
    private static final Pattern XML_NAMESPACE_PATTERN = Pattern.compile("<mapper\\s+[^>]*namespace\\s*=\\s*['\"]([^'\"]+)['\"]");
    private static final Pattern XML_STATEMENT_PATTERN = Pattern.compile("<(select|insert|update|delete)\\s+[^>]*id\\s*=\\s*['\"]([^'\"]+)['\"]");
    private static final Pattern XML_TABLE_REFERENCE_PATTERN = Pattern.compile("(?i)\\b(?:from|join|update|into)\\s+`?([a-zA-Z0-9_]+)`?");
    private static final Pattern SQL_TABLE_PATTERN = Pattern.compile("(?i)\\bcreate\\s+table\\s+(?:if\\s+not\\s+exists\\s+)?`?([a-zA-Z0-9_]+)`?");
    private static final Pattern ASCII_WORD_PATTERN = Pattern.compile("[A-Za-z0-9_./-]+");
    private static final Set<String> CODEBASE_QUERY_HINTS = Set.of(
            "源码", "代码", "二开", "二次开发", "开发", "定制", "改造", "扩展", "自定义", "类", "方法", "接口", "api", "controller", "service",
            "mapper", "vue", "前端", "后端", "页面代码", "改哪里", "在哪里改", "在哪改",
            "在哪里", "在哪", "哪里", "哪里处理", "接口在哪里", "接口路径", "怎么改",
            "新增字段", "新增接口", "实现逻辑", "表结构", "改代码", "代码位置", "源码位置",
            "设备控制", "设备下发", "命令下发", "指令下发", "服务下发", "知识库", "平台助手",
            "智能问数", "问数", "协议适配", "协议解析", "会话", "聊天", "前端页面", "前端API",
            "按钮", "弹窗", "下发按钮", "服务下发弹窗", "实时状态页", "调用页面", "调用位置",
            "在哪里调用", "在哪调用", "哪些前端页面", "权限", "权限校验", "校验", "鉴权",
            "回执", "回调", "回执结果", "日志", "控制日志", "日志保存", "保存", "落库", "更新",
            "数据表", "表名", "哪个表", "哪张表", "调用链", "入口", "菜单", "路由", "页面在哪里",
            "产品", "产品管理", "设备管理", "物模型", "规则引擎", "场景联动", "告警", "通知",
            "数据中心", "用户", "角色", "菜单权限", "租户", "字典", "文件管理", "模板",
            "模板下载", "文件模板", "文件模板下载", "下载权限", "导出企业版模板", "知识库模板"
    );
    private static final List<KeywordExpansion> CODEBASE_KEYWORD_EXPANSIONS = List.of(
            new KeywordExpansion("设备控制", List.of(
                    "devicecontrol", "device", "control", "command", "invoke", "function", "runtime", "service",
                    "mqtt", "runstatus", "runningstatus", "realtimestatus", "devicevariable", "deviceedit")),
            new KeywordExpansion("设备下发", List.of(
                    "device", "command", "invoke", "function", "runtime", "service", "mqtt", "runstatus",
                    "runningstatus", "realtimestatus", "devicevariable")),
            new KeywordExpansion("指令下发", List.of(
                    "command", "commandgenerate", "invoke", "function", "runtime", "mqtt", "message", "runstatus",
                    "runningstatus", "realtimestatus", "devicevariable")),
            new KeywordExpansion("命令下发", List.of(
                    "command", "commandgenerate", "invoke", "function", "runtime", "mqtt", "message", "runstatus",
                    "runningstatus", "realtimestatus", "devicevariable")),
            new KeywordExpansion("服务下发", List.of(
                    "invoke", "invokereply", "invokenoreply", "function", "runtime", "service", "runstatus",
                    "runningstatus", "realtimestatus", "devicevariable", "functionlog", "ifunctionlogservice",
                    "functionlogserviceimpl", "functionlogmapper", "iot_function_log")),
            new KeywordExpansion("权限校验", List.of(
                    "preauthorize", "haspermi", "permission", "security", "ss.haspermi",
                    "syspermissionservice", "sysrolemapper", "sysmenumapper", "securityutils")),
            new KeywordExpansion("权限", List.of(
                    "preauthorize", "haspermi", "permission", "security", "ss.haspermi",
                    "syspermissionservice", "sysrolemapper", "sysmenumapper", "securityutils")),
            new KeywordExpansion("校验", List.of(
                    "preauthorize", "haspermi", "permission", "security", "validate", "valid")),
            new KeywordExpansion("鉴权", List.of(
                    "preauthorize", "haspermi", "permission", "security", "ss.haspermi")),
            new KeywordExpansion("用户角色权限", List.of(
                    "syspermissionservice", "getrolepermission", "getmenupermission",
                    "sysrolecontroller", "sysusercontroller", "sysmenucontroller",
                    "sysrolemapper", "sysmenumapper", "sysuserrole", "sysrolemenu",
                    "selectrolepermissionbyuserid", "selectmenupermsbyuserid",
                    "sys_role", "sys_user", "sys_menu", "sys_user_role", "sys_role_menu")),
            new KeywordExpansion("用户权限", List.of(
                    "syspermissionservice", "sysusercontroller", "sysrolemapper", "sysmenumapper",
                    "getmenupermission", "getrolepermission", "sys_user", "sys_role", "sys_menu")),
            new KeywordExpansion("角色权限", List.of(
                    "syspermissionservice", "sysrolecontroller", "sysrolemapper", "sysmenumapper",
                    "getrolepermission", "selectrolepermissionbyuserid", "sys_role", "sys_role_menu")),
            new KeywordExpansion("回执", List.of(
                    "invokereply", "selectlogbymessageid", "updatebymessageid", "functionlog",
                    "ifunctionlogservice", "functionlogserviceimpl", "functionlogmapper", "resultcode",
                    "resultmsg", "replytime", "iot_function_log")),
            new KeywordExpansion("回调", List.of(
                    "invokereply", "selectlogbymessageid", "updatebymessageid", "functionlog",
                    "resultcode", "resultmsg", "replytime")),
            new KeywordExpansion("回执结果", List.of(
                    "invokereply", "selectlogbymessageid", "updatebymessageid", "functionlog",
                    "ifunctionlogservice", "functionlogserviceimpl", "functionlogmapper", "iot_function_log")),
            new KeywordExpansion("同步结果", List.of(
                    "invokereply", "selectlogbymessageid", "updatebymessageid", "functionlog",
                    "ifunctionlogservice", "functionlogserviceimpl", "functionlogmapper", "iot_function_log")),
            new KeywordExpansion("日志", List.of(
                    "functionlog", "ifunctionlogservice", "functionlogserviceimpl", "functionlogmapper",
                    "insertfunctionlog", "updatebymessageid", "selectlogbymessageid", "iot_function_log",
                    "mqttmessagepublish", "publishwithlog", "handlelog")),
            new KeywordExpansion("日志保存", List.of(
                    "functionlog", "ifunctionlogservice", "functionlogserviceimpl", "functionlogmapper",
                    "insertfunctionlog", "iot_function_log", "mqttmessagepublish", "publishwithlog", "handlelog")),
            new KeywordExpansion("保存", List.of(
                    "insert", "insertfunctionlog", "functionlog", "functionlogserviceimpl", "functionlogmapper",
                    "iot_function_log")),
            new KeywordExpansion("落库", List.of(
                    "insert", "insertfunctionlog", "update", "updatebymessageid", "functionlog",
                    "functionlogserviceimpl", "functionlogmapper", "iot_function_log")),
            new KeywordExpansion("更新", List.of(
                    "update", "updatebymessageid", "updatefunctionlog", "updatefunclogbatch", "functionlog",
                    "functionlogserviceimpl", "functionlogmapper", "iot_function_log")),
            new KeywordExpansion("数据表", List.of(
                    "mapper", "xml", "sql", "table", "tablename", "domain", "entity", "basemapperx")),
            new KeywordExpansion("表名", List.of(
                    "mapper", "xml", "sql", "table", "tablename", "domain", "entity", "basemapperx")),
            new KeywordExpansion("哪个表", List.of(
                    "mapper", "xml", "sql", "table", "tablename", "domain", "entity", "basemapperx")),
            new KeywordExpansion("哪张表", List.of(
                    "mapper", "xml", "sql", "table", "tablename", "domain", "entity", "basemapperx")),
            new KeywordExpansion("调用链", List.of(
                    "controller", "service", "serviceimpl", "mapper", "api", "vue", "invoke", "调用")),
            new KeywordExpansion("入口", List.of(
                    "controller", "api", "vue", "views", "router", "service")),
            new KeywordExpansion("菜单", List.of(
                    "menu", "router", "route", "sysmenu", "permission", "vue", "views")),
            new KeywordExpansion("路由", List.of(
                    "router", "route", "menu", "vue", "views", "permission")),
            new KeywordExpansion("产品", List.of(
                    "product", "iot_product", "productcontroller", "productservice", "thingsmodel")),
            new KeywordExpansion("产品管理", List.of(
                    "product", "iot_product", "productcontroller", "productservice", "thingsmodel")),
            new KeywordExpansion("设备管理", List.of(
                    "device", "iot_device", "devicecontroller", "deviceservice", "devicemapper")),
            new KeywordExpansion("物模型", List.of(
                    "thingsmodel", "thingsmodels", "thingmodel", "model", "iot_things_model", "product")),
            new KeywordExpansion("规则引擎", List.of(
                    "rule", "engine", "ruleengine", "scene", "script", "sceneModel")),
            new KeywordExpansion("场景联动", List.of(
                    "scene", "ruleengine", "sceneModel", "script", "linkage")),
            new KeywordExpansion("告警", List.of(
                    "alarm", "alert", "notify", "notice", "message", "event")),
            new KeywordExpansion("通知", List.of(
                    "notify", "notice", "message", "channel", "alarm")),
            new KeywordExpansion("数据中心", List.of(
                    "datacenter", "dataCenter", "dashboard", "statistics", "chart", "query")),
            new KeywordExpansion("用户", List.of(
                    "sysuser", "user", "sys_user", "profile", "auth")),
            new KeywordExpansion("角色", List.of(
                    "sysrole", "role", "sys_role", "permission", "auth")),
            new KeywordExpansion("菜单权限", List.of(
                    "sysmenu", "menu", "sys_menu", "permission", "perms", "router")),
            new KeywordExpansion("租户", List.of(
                    "tenant", "tenantid", "租户", "sys_tenant")),
            new KeywordExpansion("字典", List.of(
                    "dict", "sysdict", "sys_dict", "dictionary")),
            new KeywordExpansion("文件管理", List.of(
                    "file", "document", "upload", "oss", "storage", "minio")),
            new KeywordExpansion("模板", List.of(
                    "template", "export", "import", "excel", "workbook")),
            new KeywordExpansion("文件模板", List.of(
                    "downloadtemplate", "aiknowledgetemplatecontroller", "aiknowledgetemplateservice",
                    "aiknowledgedocumentcontroller", "aisecuritysupport", "requireadminaccount",
                    "ai/knowledge/template/download", "ai/knowledge/document/download",
                    "knowledgedocumentmanager", "knowledge/index.vue")),
            new KeywordExpansion("模板下载", List.of(
                    "downloadtemplate", "aiknowledgetemplatecontroller", "aiknowledgetemplateservice",
                    "aiknowledgedocumentcontroller", "aisecuritysupport", "requireadminaccount",
                    "ai/knowledge/template/download", "ai/knowledge/document/download", "knowledge/index.vue")),
            new KeywordExpansion("文件模板下载", List.of(
                    "downloadtemplate", "aiknowledgetemplatecontroller", "aiknowledgetemplateservice",
                    "aiknowledgedocumentcontroller", "aisecuritysupport", "requireadminaccount",
                    "ai/knowledge/template/download", "ai/knowledge/document/download",
                    "knowledgedocumentmanager", "knowledge/index.vue")),
            new KeywordExpansion("导出企业版模板", List.of(
                    "templatemodeenterpriseexport", "aiknowledgetemplatecontroller",
                    "aiknowledgetemplateservice", "aiplatformdoctemplatesourceservice",
                    "aisecuritysupport", "requireadminaccount", "ai/knowledge/template/download")),
            new KeywordExpansion("下载权限", List.of(
                    "download", "downloadtemplate", "aiknowledgetemplatecontroller",
                    "aiknowledgedocumentcontroller", "aisecuritysupport", "requireadminaccount",
                    "preauthorize", "haspermi")),
            new KeywordExpansion("知识库", List.of(
                    "knowledge", "knowledgebase", "knowledgedocument", "knowledgeversion", "runtime")),
            new KeywordExpansion("平台助手", List.of(
                    "platformassistant", "platform", "assistant", "chat")),
            new KeywordExpansion("协议适配", List.of(
                    "protocol", "adaptation", "parse", "dsl", "codec")),
            new KeywordExpansion("协议解析", List.of(
                    "protocol", "parse", "parser", "codec", "message")),
            new KeywordExpansion("智能问数", List.of(
                    "nl2sql", "query", "semantic", "data", "sql")),
            new KeywordExpansion("问数", List.of(
                    "nl2sql", "query", "semantic", "data", "sql")),
            new KeywordExpansion("会话", List.of(
                    "chat", "session", "message", "conversation")),
            new KeywordExpansion("聊天", List.of(
                    "chat", "session", "message", "conversation")),
            new KeywordExpansion("前端页面", List.of(
                    "vue", "views", "frontend", "page", "runningstatus", "realtimestatus", "devicevariable")),
            new KeywordExpansion("按钮", List.of(
                    "button", "btn", "promotion", "sendservice", "editfunc", "serviceinvoke", "vue", "views")),
            new KeywordExpansion("弹窗", List.of(
                    "dialog", "modal", "sendservice", "editfunc", "serviceinvoke", "vue", "views")),
            new KeywordExpansion("实时状态页", List.of(
                    "runningstatus", "realtimestatus", "running-status", "realTime-status", "serviceinvoke")),
            new KeywordExpansion("serviceinvoke", List.of(
                    "serviceinvoke", "runstatus", "runningstatus", "realtimestatus", "devicevariable", "vue", "views")),
            new KeywordExpansion("页面", List.of(
                    "vue", "views", "frontend", "page"))
    );
    private static final Set<String> NOISE_TOKENS = Set.of(
            "请问", "帮我", "一下", "这个", "那个", "如何", "怎么", "怎样", "哪些", "什么",
            "需要", "可以", "应该", "实现", "平台", "系统"
    );

    @Resource
    private IAiKnowledgeBaseService aiKnowledgeBaseService;

    @Resource
    private IAiKnowledgeDocumentService aiKnowledgeDocumentService;

    @Resource
    private IAiKnowledgeVersionService aiKnowledgeVersionService;

    @Resource
    private FastBeeAiProperties fastBeeAiProperties;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AiKnowledgeTemplateUploadResultVO rebuildCodebaseGuide(Long knowledgeBaseId) {
        AiSecuritySupport.requireAdminAccount("重建源码导航知识");
        AiKnowledgeBase knowledgeBase = requireCodebaseKnowledgeBase(knowledgeBaseId);
        if (fastBeeAiProperties.getCodebase() != null && !fastBeeAiProperties.getCodebase().isEnabled()) {
            throw new ServiceException(message("ai.codebase.guide.scan.disabled"));
        }

        Path rootPath = resolveCodebaseRootPath();
        CodebaseScanResult scanResult = scanCodebase(rootPath);
        JSONObject snapshot = buildCodebaseSnapshot(knowledgeBase, scanResult);
        Path snapshotPath = writeSnapshot(knowledgeBase, snapshot);
        byte[] snapshotBytes = JSON.toJSONString(snapshot, JSONWriter.Feature.PrettyFormat).getBytes(StandardCharsets.UTF_8);
        String checksum = sha256(snapshotBytes);
        AiKnowledgeDocument document = upsertScanDocument(knowledgeBase, snapshotPath, snapshotBytes.length, checksum, scanResult);

        AiKnowledgeTemplateUploadResultVO result = new AiKnowledgeTemplateUploadResultVO();
        result.setKnowledgeBaseId(knowledgeBase.getKnowledgeBaseId());
        result.setDocumentId(document.getDocumentId());
        result.setKbType(KB_TYPE_CODEBASE_GUIDE);
        result.setTemplateCode("CODEBASE_GUIDE_SCAN");
        result.setTemplateVersion("v1");
        result.setParserType("CODEBASE_METADATA_SCANNER");
        result.setSourceOrigin(document.getSourceOrigin());
        result.setAppVersion(document.getAppVersion());
        result.setSortNum(document.getSortNum());
        result.setRowCount(scanResult.items().size());
        result.setParseStatus(DOCUMENT_STATUS_SUCCESS);
        result.setParsedSummary(document.getParsedSummary());
        result.setSnapshotPath(snapshotPath.toString());
        result.setMessage(message("ai.codebase.guide.rebuild.next.step"));
        result.setValidationErrorCount(0);
        result.setValidationWarningCount(scanResult.warningCount());
        result.setValidationIssues(Collections.emptyList());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AiKnowledgeTemplateUploadResultVO uploadCodebaseGuideSnapshot(Long knowledgeBaseId, String sourceOrigin,
                                                                         String appVersion, Integer sortNum,
                                                                         MultipartFile file) {
        AiSecuritySupport.requireAdminAccount("上传源码导航安全摘要");
        AiKnowledgeBase knowledgeBase = requireCodebaseKnowledgeBase(knowledgeBaseId);
        if (file == null || file.isEmpty()) {
            throw new ServiceException(message("ai.codebase.guide.snapshot.file.required"));
        }
        String originalFilename = StringUtils.isBlank(file.getOriginalFilename())
                ? SCAN_DOCUMENT_FILE_NAME : file.getOriginalFilename();
        if (!originalFilename.toLowerCase(Locale.ROOT).endsWith(".json")) {
            throw new ServiceException(message("ai.codebase.guide.snapshot.json.only"));
        }
        if (file.getSize() > MAX_UPLOAD_SNAPSHOT_BYTES) {
            throw new ServiceException(message("ai.codebase.guide.snapshot.json.max.size"));
        }

        try {
            byte[] fileBytes = file.getBytes();
            JSONObject uploadedSnapshot = JSON.parseObject(new String(fileBytes, StandardCharsets.UTF_8));
            JSONObject snapshot = normalizeUploadedSnapshot(knowledgeBase, uploadedSnapshot);
            Path snapshotPath = writeImportedSnapshot(knowledgeBase, originalFilename, snapshot);
            byte[] snapshotBytes = JSON.toJSONString(snapshot, JSONWriter.Feature.PrettyFormat).getBytes(StandardCharsets.UTF_8);
            String checksum = sha256(snapshotBytes);
            AiKnowledgeDocument document = createImportedSnapshotDocument(knowledgeBase, snapshotPath, originalFilename,
                    snapshotBytes.length, checksum, snapshot.getIntValue("rowCount"), sourceOrigin, appVersion, sortNum);

            AiKnowledgeTemplateUploadResultVO result = new AiKnowledgeTemplateUploadResultVO();
            result.setKnowledgeBaseId(knowledgeBase.getKnowledgeBaseId());
            result.setDocumentId(document.getDocumentId());
            result.setKbType(KB_TYPE_CODEBASE_GUIDE);
            result.setTemplateCode("CODEBASE_GUIDE_IMPORT");
            result.setTemplateVersion("v1");
            result.setParserType("CODEBASE_METADATA_JSON");
            result.setSourceOrigin(document.getSourceOrigin());
            result.setAppVersion(document.getAppVersion());
            result.setSortNum(document.getSortNum());
            result.setRowCount(snapshot.getIntValue("rowCount"));
            result.setParseStatus(DOCUMENT_STATUS_SUCCESS);
            result.setParsedSummary(document.getParsedSummary());
            result.setSnapshotPath(snapshotPath.toString());
            result.setMessage(message("ai.codebase.guide.snapshot.import.next.step"));
            result.setValidationErrorCount(0);
            result.setValidationWarningCount(snapshot.getIntValue("validationWarningCount"));
            result.setValidationIssues(Collections.emptyList());
            return result;
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException(message("ai.codebase.guide.snapshot.json.parse.failed", ex.getMessage()));
        }
    }

    @Override
    public AiCodebaseGuideContextVO buildCodebaseContext(String question) {
        AiCodebaseGuideContextVO context = new AiCodebaseGuideContextVO();
        context.setQuestion(question);
        if (!isCodebaseQuestion(question)) {
            context.setRuntimeSource(RUNTIME_SOURCE_EMPTY);
            return context;
        }

        AiKnowledgeBase knowledgeBase = loadActiveCodebaseKnowledgeBase();
        if (knowledgeBase == null || knowledgeBase.getActiveVersionId() == null) {
            context.setRuntimeSource(RUNTIME_SOURCE_EMPTY);
            return context;
        }
        AiKnowledgeVersion activeVersion = aiKnowledgeVersionService.selectAiKnowledgeVersion(knowledgeBase.getActiveVersionId());
        if (activeVersion == null || StringUtils.isBlank(activeVersion.getSnapshotPath())) {
            context.setRuntimeSource(RUNTIME_SOURCE_EMPTY);
            return context;
        }

        List<AiCodebaseGuideItemVO> items = loadSnapshotItems(activeVersion.getSnapshotPath());
        context.setRuntimeSource(RUNTIME_SOURCE_VERSION_SNAPSHOT_ITEM);
        context.setKnowledgeBaseId(knowledgeBase.getKnowledgeBaseId());
        context.setKbCode(knowledgeBase.getKbCode());
        context.setKbName(knowledgeBase.getKbName());
        context.setVersionId(activeVersion.getVersionId());
        context.setVersionNo(activeVersion.getVersionNo());
        context.setTotalItems(items.size());
        if (items.isEmpty()) {
            return context;
        }

        String normalizedQuestion = normalizeText(question);
        List<String> keywords = extractKeywords(question);
        List<AiCodebaseGuideItemVO> matchedItems = items.stream()
                .peek(item -> item.setMatchScore(calculateScore(normalizedQuestion, keywords, item)))
                .filter(item -> item.getMatchScore() != null && item.getMatchScore() >= MIN_MATCH_SCORE)
                .sorted(Comparator.comparing(AiCodebaseGuideItemVO::getMatchScore, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(AiCodebaseGuideItemVO::getSourcePath, Comparator.nullsLast(String::compareToIgnoreCase))
                        .thenComparing(AiCodebaseGuideItemVO::getSymbolName, Comparator.nullsLast(String::compareToIgnoreCase)))
                .limit(MAX_PROMPT_ITEMS)
                .collect(Collectors.toCollection(ArrayList::new));
        if (matchedItems.isEmpty()) {
            return context;
        }

        context.setMatched(Boolean.TRUE);
        context.setMatchedItems(matchedItems.size());
        context.setItems(matchedItems);
        context.setPromptLines(buildPromptLines(context, matchedItems));
        return context;
    }

    private AiKnowledgeBase requireCodebaseKnowledgeBase(Long knowledgeBaseId) {
        if (knowledgeBaseId == null) {
            throw new ServiceException(message("ai.knowledge.base.id.required"));
        }
        AiKnowledgeBase knowledgeBase = aiKnowledgeBaseService.getById(knowledgeBaseId);
        if (knowledgeBase == null) {
            throw new ServiceException(message("ai.knowledge.base.not.exists.or.deleted"));
        }
        if (!KB_TYPE_CODEBASE_GUIDE.equalsIgnoreCase(knowledgeBase.getKbType())) {
            throw new ServiceException(message("ai.codebase.guide.knowledge.type.invalid"));
        }
        return knowledgeBase;
    }

    private AiKnowledgeBase loadActiveCodebaseKnowledgeBase() {
        AiKnowledgeBase query = new AiKnowledgeBase();
        query.setKbType(KB_TYPE_CODEBASE_GUIDE);
        query.setStatus(KNOWLEDGE_STATUS_ENABLED);
        List<AiKnowledgeBase> knowledgeBases = aiKnowledgeBaseService.listAiKnowledgeBase(query);
        if (knowledgeBases == null || knowledgeBases.isEmpty()) {
            return null;
        }
        return knowledgeBases.stream()
                .filter(Objects::nonNull)
                .filter(item -> KB_TYPE_CODEBASE_GUIDE.equalsIgnoreCase(item.getKbType()))
                .filter(item -> KNOWLEDGE_STATUS_ENABLED.equals(item.getStatus()))
                .findFirst()
                .orElse(null);
    }

    private Path resolveCodebaseRootPath() {
        String configuredRoot = fastBeeAiProperties.getCodebase() == null ? null : fastBeeAiProperties.getCodebase().getRootPath();
        Path rootPath;
        if (StringUtils.isNotBlank(configuredRoot)) {
            rootPath = Paths.get(configuredRoot.trim());
        } else {
            rootPath = Paths.get(System.getProperty("user.dir", "."));
            if ("springboot".equalsIgnoreCase(rootPath.getFileName() == null ? "" : rootPath.getFileName().toString())) {
                rootPath = rootPath.getParent() == null ? rootPath : rootPath.getParent();
            }
        }
        rootPath = rootPath.toAbsolutePath().normalize();
        if (!Files.isDirectory(rootPath)) {
            throw new ServiceException(message("ai.codebase.guide.root.not.readable", rootPath));
        }
        return rootPath;
    }

    private CodebaseScanResult scanCodebase(Path rootPath) {
        List<String> includeRoots = resolveIncludeRoots();
        int maxFiles = Math.max(100, fastBeeAiProperties.getCodebase() == null ? 5000 : fastBeeAiProperties.getCodebase().getMaxFiles());
        int maxItems = Math.max(100, fastBeeAiProperties.getCodebase() == null ? 20000 : fastBeeAiProperties.getCodebase().getMaxItems());
        List<Path> files = new ArrayList<>();
        int skippedFileCount = 0;
        for (String includeRoot : includeRoots) {
            Path scanRoot = rootPath.resolve(includeRoot).normalize();
            if (!scanRoot.startsWith(rootPath) || !Files.isDirectory(scanRoot)) {
                continue;
            }
            try (Stream<Path> stream = Files.walk(scanRoot)) {
                List<Path> matched = stream
                        .filter(Files::isRegularFile)
                        .filter(path -> !isSensitivePath(rootPath, path))
                        .filter(this::isAllowedSourceFile)
                        .limit(maxFiles)
                        .collect(Collectors.toCollection(ArrayList::new));
                files.addAll(matched);
            } catch (IOException ex) {
                skippedFileCount++;
            }
            if (files.size() >= maxFiles) {
                break;
            }
        }
        files = files.stream()
                .distinct()
                .sorted(Comparator.comparing(path -> normalizePath(rootPath.relativize(path))))
                .limit(maxFiles)
                .collect(Collectors.toCollection(ArrayList::new));

        List<AiCodebaseGuideItemVO> items = new ArrayList<>();
        for (Path file : files) {
            if (items.size() >= maxItems) {
                break;
            }
            try {
                if (Files.size(file) > MAX_SCAN_FILE_BYTES) {
                    skippedFileCount++;
                    continue;
                }
                scanSourceFile(rootPath, file, items, maxItems);
            } catch (Exception ex) {
                skippedFileCount++;
            }
        }
        return new CodebaseScanResult(rootPath, includeRoots, files.size(), skippedFileCount, items);
    }

    private List<String> resolveIncludeRoots() {
        List<String> includeRoots = fastBeeAiProperties.getCodebase() == null
                ? null
                : fastBeeAiProperties.getCodebase().getIncludeRoots();
        if (includeRoots == null || includeRoots.isEmpty()) {
            return List.of("springboot", "vue3");
        }
        return includeRoots.stream()
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private void scanSourceFile(Path rootPath, Path file, List<AiCodebaseGuideItemVO> items, int maxItems) throws IOException {
        String relativePath = normalizePath(rootPath.relativize(file));
        String lowerPath = relativePath.toLowerCase(Locale.ROOT);
        if (lowerPath.endsWith(".java")) {
            scanJavaFile(file, relativePath, items, maxItems);
            return;
        }
        if (lowerPath.endsWith(".vue")) {
            scanVuePageFile(file, relativePath, items, maxItems);
            return;
        }
        if ((lowerPath.endsWith(".js") || lowerPath.endsWith(".ts")) && lowerPath.contains("/src/api/")) {
            scanFrontendApiFile(file, relativePath, items, maxItems);
            return;
        }
        if (lowerPath.endsWith(".xml") && lowerPath.contains("mapper")) {
            scanMapperXmlFile(file, relativePath, items, maxItems);
            return;
        }
        if (lowerPath.endsWith(".sql")) {
            scanSqlFile(file, relativePath, items, maxItems);
        }
    }

    private void scanJavaFile(Path file, String relativePath, List<AiCodebaseGuideItemVO> items, int maxItems) throws IOException {
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        String packageName = "";
        String className = stripExtension(Paths.get(relativePath).getFileName().toString());
        int classLine = -1;
        Map<String, String> fieldTypes = collectJavaFieldTypes(lines);
        for (int index = 0; index < lines.size(); index++) {
            String line = lines.get(index);
            if (StringUtils.isBlank(packageName)) {
                Matcher packageMatcher = PACKAGE_PATTERN.matcher(line);
                if (packageMatcher.find()) {
                    packageName = packageMatcher.group(1);
                }
            }
            Matcher classMatcher = CLASS_PATTERN.matcher(line);
            if (classLine < 0 && classMatcher.find()) {
                className = classMatcher.group(2);
                classLine = index;
            }
        }
        String layer = resolveLayer(relativePath, className);
        String classAnnotations = classLine < 0 ? "" : collectAnnotationWindow(lines, classLine);
        String baseEndpoint = extractFirstEndpointPath(classAnnotations);
        String classSignature = buildJavaClassSignature(className, classAnnotations);
        addItem(items, maxItems, buildItem(relativePath, "JAVA_CLASS", layer, packageName, className, null,
                className, classSignature, null, baseEndpoint, classLine < 0 ? 1 : classLine + 1));

        for (int index = 0; index < lines.size() && items.size() < maxItems; index++) {
            String line = lines.get(index);
            String methodName = extractJavaMethodName(line);
            if (StringUtils.isBlank(methodName)) {
                continue;
            }
            if (isIgnoredMethod(methodName)) {
                continue;
            }
            String annotationWindow = collectAnnotationWindow(lines, index);
            List<EndpointMapping> mappings = extractEndpointMappings(annotationWindow);
            String methodDeclaration = collectJavaMethodDeclaration(lines, index);
            String methodSignature = buildJavaMethodSignature(methodDeclaration, annotationWindow, lines, index, fieldTypes);
            if (mappings.isEmpty()) {
                addItem(items, maxItems, buildItem(relativePath, "JAVA_METHOD", layer, packageName, className, methodName,
                        className + "#" + methodName, methodSignature, null, null, index + 1));
                continue;
            }
            for (EndpointMapping mapping : mappings) {
                String endpointPath = combineEndpointPaths(baseEndpoint, mapping.path());
                String symbolType = StringUtils.isNotBlank(endpointPath) ? "BACKEND_API" : "JAVA_METHOD";
                addItem(items, maxItems, buildItem(relativePath, symbolType, layer, packageName, className, methodName,
                        className + "#" + methodName, methodSignature, mapping.httpMethod(), endpointPath, index + 1));
            }
        }
    }

    private void scanVuePageFile(Path file, String relativePath, List<AiCodebaseGuideItemVO> items, int maxItems) throws IOException {
        String symbolName = stripExtension(Paths.get(relativePath).getFileName().toString());
        String content = Files.readString(file, StandardCharsets.UTF_8);
        String signature = buildVuePageSignature(content);
        addItem(items, maxItems, buildItem(relativePath, "VUE_PAGE", "前端页面", "", symbolName, null,
                symbolName, signature, null, null, 1));
    }

    private String buildVuePageSignature(String content) {
        if (StringUtils.isBlank(content)) {
            return "";
        }
        LinkedHashSet<String> imports = new LinkedHashSet<>();
        Matcher importMatcher = FRONTEND_IMPORT_PATTERN.matcher(content);
        while (importMatcher.find()) {
            for (String rawName : importMatcher.group(1).split(",")) {
                String importName = rawName == null ? "" : rawName.replaceAll("\\s+as\\s+.*$", "").trim();
                if (StringUtils.isNotBlank(importName) && importName.length() <= 48) {
                    imports.add(importName);
                }
            }
        }

        LinkedHashSet<String> functions = new LinkedHashSet<>();
        collectVueFunctionNames(content, VUE_ARROW_FUNCTION_PATTERN, functions);
        collectVueFunctionNames(content, VUE_FUNCTION_DECL_PATTERN, functions);

        LinkedHashSet<String> apiCalls = new LinkedHashSet<>();
        addKeywordIfContains(content, apiCalls, "serviceInvoke", "serviceInvoke");
        addKeywordIfContains(content, apiCalls, "serviceInvokeReply", "serviceInvokeReply");
        addKeywordIfContains(content, apiCalls, "runningStatus", "runningStatus");
        addKeywordIfContains(content, apiCalls, "runStatus", "runStatus");
        addKeywordIfContains(content, apiCalls, "propGet", "propGet");

        LinkedHashSet<String> endpointPaths = collectFrontendApiPaths(content);

        LinkedHashSet<String> keywords = new LinkedHashSet<>();
        addKeywordIfContains(content, keywords, "serviceInvoke", "服务下发调用");
        addKeywordIfContains(content, keywords, "serviceInvokeReply", "带回执服务下发调用");
        addKeywordIfContains(content, keywords, "runningStatus", "运行状态查询");
        addKeywordIfContains(content, keywords, "runStatus", "实时数据查询");
        addKeywordIfContains(content, keywords, "指令下发", "指令下发");
        addKeywordIfContains(content, keywords, "服务下发", "服务下发");
        addKeywordIfContains(content, keywords, "弹窗", "弹窗");
        addKeywordIfContains(content, keywords, "Promotion", "下发按钮图标");

        List<String> parts = new ArrayList<>();
        if (!apiCalls.isEmpty()) {
            parts.add("调用API=" + apiCalls.stream().limit(10).collect(Collectors.joining("、")));
        }
        if (!endpointPaths.isEmpty()) {
            parts.add("接口路径=" + joinPrioritizedValues(endpointPaths, 12,
                    "/ai/knowledge/template/download", "/ai/knowledge/document/download",
                    "/iot/runtime/service/invoke", "/iot/runtime/service/invokeReply"));
        }
        if (!imports.isEmpty()) {
            parts.add("导入=" + joinPrioritizedValues(imports, 16,
                    "serviceInvoke", "serviceInvokeReply", "runningStatus", "runStatus", "propGet"));
        }
        if (!functions.isEmpty()) {
            parts.add("页面函数=" + joinPrioritizedValues(functions, 16,
                    "sendService", "editFunc", "getRuntimeStatus", "getValueName", "submitForm", "handleSend"));
        }
        if (!keywords.isEmpty()) {
            parts.add("关键词=" + keywords.stream().limit(8).collect(Collectors.joining("、")));
        }
        return shortenText(String.join("；", parts), 420);
    }

    private LinkedHashSet<String> collectFrontendApiPaths(String content) {
        LinkedHashSet<String> paths = new LinkedHashSet<>();
        if (StringUtils.isBlank(content)) {
            return paths;
        }
        Matcher matcher = FRONTEND_LITERAL_API_PATH_PATTERN.matcher(content);
        while (matcher.find() && paths.size() < 20) {
            String path = matcher.group(1);
            if (isLikelyFrontendApiPath(path)) {
                paths.add(path.trim());
            }
        }
        return paths;
    }

    private boolean isLikelyFrontendApiPath(String path) {
        if (StringUtils.isBlank(path) || !path.startsWith("/") || path.startsWith("//")) {
            return false;
        }
        String normalizedPath = path.trim().toLowerCase(Locale.ROOT);
        if (normalizedPath.matches(".+\\.(png|jpg|jpeg|gif|svg|webp|ico|css|scss|less|js|ts|vue|html|md)$")) {
            return false;
        }
        return normalizedPath.chars().filter(ch -> ch == '/').count() >= 2;
    }

    private String joinPrioritizedValues(Set<String> values, int limit, String... priorityValues) {
        if (values == null || values.isEmpty()) {
            return "";
        }
        LinkedHashSet<String> orderedValues = new LinkedHashSet<>();
        if (priorityValues != null) {
            for (String priorityValue : priorityValues) {
                if (containsValueIgnoreCase(values, priorityValue)) {
                    orderedValues.add(priorityValue);
                }
            }
        }
        orderedValues.addAll(values);
        return orderedValues.stream()
                .filter(StringUtils::isNotBlank)
                .limit(limit)
                .collect(Collectors.joining("、"));
    }

    private boolean containsValueIgnoreCase(Set<String> values, String target) {
        if (values == null || StringUtils.isBlank(target)) {
            return false;
        }
        return values.stream().anyMatch(value -> target.equalsIgnoreCase(value));
    }

    private void collectVueFunctionNames(String content, Pattern pattern, Set<String> functions) {
        Matcher matcher = pattern.matcher(content);
        while (matcher.find() && functions.size() < 20) {
            String functionName = matcher.group(1);
            if (StringUtils.isNotBlank(functionName) && !isIgnoredMethod(functionName)) {
                functions.add(functionName);
            }
        }
    }

    private void addKeywordIfContains(String content, Set<String> keywords, String source, String keyword) {
        if (StringUtils.isNotBlank(content) && StringUtils.isNotBlank(source) && content.contains(source)) {
            addIfNotBlank(keywords, keyword);
        }
    }

    private void scanFrontendApiFile(Path file, String relativePath, List<AiCodebaseGuideItemVO> items, int maxItems) throws IOException {
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        String className = stripExtension(Paths.get(relativePath).getFileName().toString());
        for (int index = 0; index < lines.size() && items.size() < maxItems; index++) {
            Matcher matcher = EXPORT_FUNCTION_PATTERN.matcher(lines.get(index));
            if (!matcher.find()) {
                continue;
            }
            String functionName = matcher.group(1);
            String endpointPath = findNearbyUrl(lines, index);
            addItem(items, maxItems, buildItem(relativePath, "FRONTEND_API", "前端接口", "", className, functionName,
                    className + "." + functionName, functionName + "(...)", null, endpointPath, index + 1));
        }
        if (!hasFileItem(items, relativePath)) {
            addItem(items, maxItems, buildItem(relativePath, "FRONTEND_API_FILE", "前端接口", "", className, null,
                    className, null, null, null, 1));
        }
    }

    private void scanMapperXmlFile(Path file, String relativePath, List<AiCodebaseGuideItemVO> items, int maxItems) throws IOException {
        String content = Files.readString(file, StandardCharsets.UTF_8);
        Matcher namespaceMatcher = XML_NAMESPACE_PATTERN.matcher(content);
        String namespace = namespaceMatcher.find() ? namespaceMatcher.group(1) : stripExtension(Paths.get(relativePath).getFileName().toString());
        String tableSummary = buildXmlTableSummary(content);
        addItem(items, maxItems, buildItem(relativePath, "MYBATIS_MAPPER_XML", "数据访问", "", namespace, null,
                namespace, tableSummary, null, null, 1));
        Matcher statementMatcher = XML_STATEMENT_PATTERN.matcher(content);
        while (statementMatcher.find() && items.size() < maxItems) {
            String statementType = statementMatcher.group(1).toUpperCase(Locale.ROOT);
            String statementId = statementMatcher.group(2);
            addItem(items, maxItems, buildItem(relativePath, "MYBATIS_" + statementType, "数据访问", "", namespace, statementId,
                    namespace + "#" + statementId, StringUtils.isBlank(tableSummary)
                            ? statementType + " " + statementId
                            : statementType + " " + statementId + "；" + tableSummary,
                    null, null, null));
        }
    }

    private String buildXmlTableSummary(String content) {
        if (StringUtils.isBlank(content)) {
            return "";
        }
        LinkedHashSet<String> tables = new LinkedHashSet<>();
        Matcher matcher = XML_TABLE_REFERENCE_PATTERN.matcher(content);
        while (matcher.find() && tables.size() < 12) {
            addIfNotBlank(tables, matcher.group(1));
        }
        if (tables.isEmpty()) {
            return "";
        }
        return "表=" + String.join("、", tables);
    }

    private void scanSqlFile(Path file, String relativePath, List<AiCodebaseGuideItemVO> items, int maxItems) throws IOException {
        String content = Files.readString(file, StandardCharsets.UTF_8);
        Matcher matcher = SQL_TABLE_PATTERN.matcher(content);
        while (matcher.find() && items.size() < maxItems) {
            String tableName = matcher.group(1);
            addItem(items, maxItems, buildItem(relativePath, "SQL_TABLE", "数据库脚本", "", tableName, null,
                    tableName, null, null, null, null));
        }
    }

    private AiCodebaseGuideItemVO buildItem(String sourcePath,
                                            String symbolType,
                                            String layer,
                                            String packageName,
                                            String className,
                                            String methodName,
                                            String symbolName,
                                            String signature,
                                            String httpMethod,
                                            String endpointPath,
                                            Integer lineStart) {
        AiCodebaseGuideItemVO item = new AiCodebaseGuideItemVO();
        item.setSourcePath(sourcePath);
        item.setModuleName(resolveModuleName(sourcePath));
        item.setSymbolType(symbolType);
        item.setLayer(layer);
        item.setPackageName(packageName);
        item.setClassName(className);
        item.setMethodName(methodName);
        item.setSymbolName(symbolName);
        item.setSignature(signature);
        item.setHttpMethod(httpMethod);
        item.setEndpointPath(endpointPath);
        item.setLineStart(lineStart);
        item.setTags(buildTags(item));
        item.setAliases(buildAliases(item));
        item.setSummary(buildSummary(item));
        item.setDevHint(buildDevHint(item));
        item.setContent(buildSearchContent(item));
        return item;
    }

    private void addItem(List<AiCodebaseGuideItemVO> items, int maxItems, AiCodebaseGuideItemVO item) {
        if (items.size() >= maxItems || item == null || StringUtils.isBlank(item.getSourcePath())) {
            return;
        }
        items.add(item);
    }

    private JSONObject buildCodebaseSnapshot(AiKnowledgeBase knowledgeBase, CodebaseScanResult scanResult) {
        JSONObject snapshot = new JSONObject();
        snapshot.put("knowledgeBaseId", knowledgeBase.getKnowledgeBaseId());
        snapshot.put("kbCode", knowledgeBase.getKbCode());
        snapshot.put("kbName", knowledgeBase.getKbName());
        snapshot.put("kbType", KB_TYPE_CODEBASE_GUIDE);
        snapshot.put("generatedAt", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        snapshot.put("buildMode", "CODEBASE_METADATA_SCAN");
        snapshot.put("scanRoots", scanResult.includeRoots());
        snapshot.put("scannedFileCount", scanResult.scannedFileCount());
        snapshot.put("skippedFileCount", scanResult.skippedFileCount());
        snapshot.put("rowCount", scanResult.items().size());
        snapshot.put("sourcePolicy", "仅保存源码相对路径、类名、方法名、接口路径、表名和AI摘要，不保存源码正文、配置值或密钥。");
        JSONArray items = new JSONArray();
        for (AiCodebaseGuideItemVO item : scanResult.items()) {
            items.add(JSON.parseObject(JSON.toJSONString(item)));
        }
        snapshot.put("items", items);
        return snapshot;
    }

    private Path writeSnapshot(AiKnowledgeBase knowledgeBase, JSONObject snapshot) {
        try {
            Path directory = Paths.get(RuoYiConfig.getProfile(), "ai", "knowledge", "codebase",
                    sanitizeFileName(StringUtils.defaultIfBlank(knowledgeBase.getKbCode(), "codebase-guide")));
            Files.createDirectories(directory);
            Path snapshotPath = directory.resolve(SCAN_DOCUMENT_FILE_NAME);
            Files.writeString(snapshotPath, JSON.toJSONString(snapshot, JSONWriter.Feature.PrettyFormat), StandardCharsets.UTF_8);
            return snapshotPath;
        } catch (IOException ex) {
            throw new ServiceException(message("ai.codebase.guide.snapshot.write.failed", ex.getMessage()));
        }
    }

    private AiKnowledgeDocument upsertScanDocument(AiKnowledgeBase knowledgeBase,
                                                   Path snapshotPath,
                                                   long fileSize,
                                                   String checksum,
                                                   CodebaseScanResult scanResult) {
        AiKnowledgeDocument old = aiKnowledgeDocumentService.list(Wrappers.<AiKnowledgeDocument>lambdaQuery()
                        .eq(AiKnowledgeDocument::getKnowledgeBaseId, knowledgeBase.getKnowledgeBaseId())
                        .eq(AiKnowledgeDocument::getFileName, SCAN_DOCUMENT_FILE_NAME))
                .stream()
                .findFirst()
                .orElse(null);
        AiKnowledgeDocument document = new AiKnowledgeDocument();
        if (old != null) {
            document.setDocumentId(old.getDocumentId());
        } else {
            document.setKnowledgeBaseId(knowledgeBase.getKnowledgeBaseId());
            document.setFileName(SCAN_DOCUMENT_FILE_NAME);
            document.setSortNum(10);
            document.setSourceOrigin(SOURCE_ORIGIN_OFFICIAL);
            document.setCreateBy(AiSecuritySupport.resolveUsername());
            document.setCreateTime(AiSecuritySupport.now());
        }
        document.setKnowledgeBaseId(knowledgeBase.getKnowledgeBaseId());
        document.setFileName(SCAN_DOCUMENT_FILE_NAME);
        document.setFilePath(snapshotPath.toString());
        document.setFileSize(fileSize);
        document.setChecksum(checksum);
        document.setParseStatus(DOCUMENT_STATUS_SUCCESS);
        document.setChunkCount(scanResult.items().size());
        document.setParsedSnapshotPath(snapshotPath.toString());
        document.setParsedSummary("源码导航摘要已生成：扫描文件 " + scanResult.scannedFileCount()
                + " 个，安全条目 " + scanResult.items().size()
                + " 条，跳过文件 " + scanResult.skippedFileCount() + " 个。");
        document.setAppVersion("");
        document.setStatus(DOCUMENT_ENABLED);
        document.setRemark("系统扫描生成的源码导航安全摘要，不包含真实源码正文、配置值或密钥。");
        document.setUpdateBy(AiSecuritySupport.resolveUsername());
        document.setUpdateTime(AiSecuritySupport.now());
        if (old == null) {
            aiKnowledgeDocumentService.save(document);
        } else {
            aiKnowledgeDocumentService.updateById(document);
        }
        return old == null ? document : aiKnowledgeDocumentService.getById(old.getDocumentId());
    }

    private JSONObject normalizeUploadedSnapshot(AiKnowledgeBase knowledgeBase, JSONObject uploadedSnapshot) {
        if (uploadedSnapshot == null || uploadedSnapshot.isEmpty()) {
            throw new ServiceException(message("ai.codebase.guide.snapshot.content.required"));
        }
        String uploadedKbType = uploadedSnapshot.getString("kbType");
        if (StringUtils.isNotBlank(uploadedKbType) && !KB_TYPE_CODEBASE_GUIDE.equalsIgnoreCase(uploadedKbType)) {
            throw new ServiceException(message("ai.codebase.guide.snapshot.kb.type.invalid", uploadedKbType));
        }
        JSONArray sourceItems = uploadedSnapshot.getJSONArray("items");
        if (sourceItems == null || sourceItems.isEmpty()) {
            throw new ServiceException(message("ai.codebase.guide.snapshot.items.required"));
        }

        JSONArray items = new JSONArray();
        int warningCount = 0;
        for (int index = 0; index < sourceItems.size(); index++) {
            Object rawItem = sourceItems.get(index);
            if (!(rawItem instanceof JSONObject itemObject)) {
                warningCount++;
                continue;
            }
            AiCodebaseGuideItemVO item = normalizeUploadedItem(itemObject, index + 1);
            if (item == null) {
                warningCount++;
                continue;
            }
            items.add(JSON.parseObject(JSON.toJSONString(item)));
        }
        if (items.isEmpty()) {
            throw new ServiceException(message("ai.codebase.guide.snapshot.safe.items.required"));
        }

        JSONObject snapshot = new JSONObject();
        snapshot.put("knowledgeBaseId", knowledgeBase.getKnowledgeBaseId());
        snapshot.put("kbCode", knowledgeBase.getKbCode());
        snapshot.put("kbName", knowledgeBase.getKbName());
        snapshot.put("kbType", KB_TYPE_CODEBASE_GUIDE);
        snapshot.put("versionNo", uploadedSnapshot.getString("versionNo"));
        snapshot.put("generatedAt", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        snapshot.put("importedAt", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        snapshot.put("sourceGeneratedAt", uploadedSnapshot.getString("generatedAt"));
        snapshot.put("buildMode", "CODEBASE_METADATA_JSON_IMPORT");
        snapshot.put("sourceBuildMode", uploadedSnapshot.getString("buildMode"));
        snapshot.put("scanRoots", uploadedSnapshot.getJSONArray("scanRoots"));
        snapshot.put("scannedFileCount", uploadedSnapshot.getIntValue("scannedFileCount"));
        snapshot.put("skippedFileCount", uploadedSnapshot.getIntValue("skippedFileCount"));
        snapshot.put("rowCount", items.size());
        snapshot.put("validationErrorCount", 0);
        snapshot.put("validationWarningCount", warningCount);
        snapshot.put("sourcePolicy", "仅保存源码相对路径、类名、方法名、接口路径、表名和AI摘要，不保存源码正文、配置值或密钥。");
        snapshot.put("items", items);
        return snapshot;
    }

    private AiCodebaseGuideItemVO normalizeUploadedItem(JSONObject itemObject, int rowNumber) {
        rejectUnsafeUploadedKeys(itemObject, rowNumber);
        AiCodebaseGuideItemVO item = new AiCodebaseGuideItemVO();
        item.setSourcePath(normalizeUploadedSourcePath(itemObject.getString("sourcePath"), rowNumber));
        item.setModuleName(limitText(defaultIfBlank(itemObject.getString("moduleName"), resolveModuleName(item.getSourcePath())), 80));
        item.setSymbolType(limitCode(defaultIfBlank(itemObject.getString("symbolType"), "CODEBASE_ITEM"), 48));
        item.setLayer(limitText(itemObject.getString("layer"), 80));
        item.setPackageName(limitText(itemObject.getString("packageName"), 180));
        item.setClassName(limitText(itemObject.getString("className"), 120));
        item.setMethodName(limitText(itemObject.getString("methodName"), 120));
        item.setSymbolName(limitText(defaultIfBlank(itemObject.getString("symbolName"),
                defaultIfBlank(item.getMethodName(), item.getClassName())), 180));
        item.setSignature(limitText(itemObject.getString("signature"), 260));
        item.setHttpMethod(limitCode(itemObject.getString("httpMethod"), 16));
        item.setEndpointPath(normalizeEndpointPath(itemObject.getString("endpointPath")));
        item.setLineStart(normalizeLineStart(itemObject.getInteger("lineStart")));
        item.setSummary(buildSummary(item));
        item.setDevHint(buildDevHint(item));
        item.setTags(normalizeStringList(itemObject.get("tags"), 24, 80));
        if (item.getTags().isEmpty()) {
            item.setTags(buildTags(item));
        }
        item.setAliases(normalizeStringList(itemObject.get("aliases"), 24, 80));
        if (item.getAliases().isEmpty()) {
            item.setAliases(buildAliases(item));
        }
        item.setContent(limitText(buildSearchContent(item), 1200));
        item.setMatchScore(0);
        return item;
    }

    private void rejectUnsafeUploadedKeys(JSONObject itemObject, int rowNumber) {
        for (String key : itemObject.keySet()) {
            String normalizedKey = normalizeText(key);
            if (containsAnyNormalized(normalizedKey, "sourcecode", "methodbody", "filecontent", "rawcontent",
                    "configvalue", "password", "passwd", "secret", "apikey", "api_key", "token", "credential",
                    "connectionstring", "jdbcurl", "privatekey")) {
                throw new ServiceException(message("ai.codebase.guide.snapshot.sensitive.field.rejected", rowNumber, key));
            }
        }
    }

    private String normalizeUploadedSourcePath(String sourcePath, int rowNumber) {
        String path = limitText(sourcePath, 260).replace('\\', '/');
        if (StringUtils.isBlank(path)) {
            throw new ServiceException(message("ai.codebase.guide.snapshot.source.path.required", rowNumber));
        }
        if (path.startsWith("/") || path.matches("^[A-Za-z]:/.*") || path.contains("../") || path.contains("/..")
                || "..".equals(path)) {
            throw new ServiceException(message("ai.codebase.guide.snapshot.source.path.unsafe", rowNumber));
        }
        return path;
    }

    private String normalizeEndpointPath(String endpointPath) {
        String path = limitText(endpointPath, 180);
        if (StringUtils.isBlank(path)) {
            return "";
        }
        return path.startsWith("/") ? path : "/" + path;
    }

    private Integer normalizeLineStart(Integer lineStart) {
        if (lineStart == null || lineStart <= 0 || lineStart > 1000000) {
            return null;
        }
        return lineStart;
    }

    private List<String> normalizeStringList(Object rawValue, int maxItems, int maxLength) {
        List<String> values = new ArrayList<>();
        if (rawValue instanceof JSONArray array) {
            for (int index = 0; index < array.size() && values.size() < maxItems; index++) {
                addIfNotBlank(values, limitText(String.valueOf(array.get(index)), maxLength));
            }
        } else if (rawValue instanceof List<?> list) {
            for (Object item : list) {
                if (values.size() >= maxItems) {
                    break;
                }
                addIfNotBlank(values, limitText(String.valueOf(item), maxLength));
            }
        } else if (rawValue != null) {
            for (String item : String.valueOf(rawValue).split("[,，;；\\r\\n]+")) {
                if (values.size() >= maxItems) {
                    break;
                }
                addIfNotBlank(values, limitText(item, maxLength));
            }
        }
        return values.stream().distinct().collect(Collectors.toCollection(ArrayList::new));
    }

    private Path writeImportedSnapshot(AiKnowledgeBase knowledgeBase, String originalFilename, JSONObject snapshot) {
        try {
            String datePart = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String fileName = UUID.randomUUID() + "_" + sanitizeFileName(defaultIfBlank(originalFilename, SCAN_DOCUMENT_FILE_NAME));
            Path directory = Paths.get(RuoYiConfig.getProfile(), "ai", "knowledge", "codebase",
                    sanitizeFileName(StringUtils.defaultIfBlank(knowledgeBase.getKbCode(), "codebase-guide")),
                    "import", datePart);
            Files.createDirectories(directory);
            Path snapshotPath = directory.resolve(fileName).normalize();
            Files.writeString(snapshotPath, JSON.toJSONString(snapshot, JSONWriter.Feature.PrettyFormat), StandardCharsets.UTF_8);
            return snapshotPath;
        } catch (IOException ex) {
            throw new ServiceException(message("ai.codebase.guide.snapshot.import.write.failed", ex.getMessage()));
        }
    }

    private AiKnowledgeDocument createImportedSnapshotDocument(AiKnowledgeBase knowledgeBase,
                                                               Path snapshotPath,
                                                               String originalFilename,
                                                               long fileSize,
                                                               String checksum,
                                                               int rowCount,
                                                               String sourceOrigin,
                                                               String appVersion,
                                                               Integer sortNum) {
        AiKnowledgeDocument document = new AiKnowledgeDocument();
        document.setKnowledgeBaseId(knowledgeBase.getKnowledgeBaseId());
        document.setFileName(sanitizeFileName(defaultIfBlank(originalFilename, SCAN_DOCUMENT_FILE_NAME)));
        document.setFilePath(snapshotPath.toString());
        document.setFileSize(fileSize);
        document.setChecksum(checksum);
        document.setParseStatus(DOCUMENT_STATUS_SUCCESS);
        document.setChunkCount(rowCount);
        document.setParsedSnapshotPath(snapshotPath.toString());
        document.setParsedSummary("源码导航安全摘要已导入：安全条目 " + rowCount + " 条。");
        document.setSourceOrigin(normalizeSourceOrigin(sourceOrigin));
        document.setAppVersion(limitText(appVersion, 80));
        document.setSortNum(sortNum == null ? IMPORTED_DOCUMENT_SORT_NUM : Math.max(0, sortNum));
        document.setStatus(DOCUMENT_ENABLED);
        document.setRemark("上传导入的源码导航安全摘要，不包含真实源码正文、配置值或密钥。");
        document.setCreateBy(AiSecuritySupport.resolveUsername());
        document.setCreateTime(AiSecuritySupport.now());
        document.setUpdateBy(AiSecuritySupport.resolveUsername());
        document.setUpdateTime(AiSecuritySupport.now());
        aiKnowledgeDocumentService.save(document);
        return document;
    }

    private String normalizeSourceOrigin(String sourceOrigin) {
        String actual = StringUtils.isBlank(sourceOrigin) ? SOURCE_ORIGIN_CUSTOM : sourceOrigin.trim().toUpperCase(Locale.ROOT);
        if (SOURCE_ORIGIN_OFFICIAL.equals(actual)) {
            return SOURCE_ORIGIN_OFFICIAL;
        }
        return SOURCE_ORIGIN_CUSTOM;
    }

    private List<AiCodebaseGuideItemVO> loadSnapshotItems(String snapshotPath) {
        try {
            Path path = Paths.get(snapshotPath.trim());
            if (!Files.exists(path)) {
                return Collections.emptyList();
            }
            JSONObject snapshot = JSON.parseObject(Files.readString(path, StandardCharsets.UTF_8));
            JSONArray array = snapshot.getJSONArray("items");
            if (array == null || array.isEmpty()) {
                return Collections.emptyList();
            }
            List<AiCodebaseGuideItemVO> items = new ArrayList<>();
            for (int index = 0; index < array.size(); index++) {
                JSONObject object = array.getJSONObject(index);
                if (object != null) {
                    items.add(object.to(AiCodebaseGuideItemVO.class));
                }
            }
            return items;
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    private List<String> buildPromptLines(AiCodebaseGuideContextVO context, List<AiCodebaseGuideItemVO> items) {
        List<String> lines = new ArrayList<>();
        lines.add("当前已加载源码导航知识快照：知识库=" + defaultIfBlank(context.getKbName(), "-")
                + "，版本=" + defaultIfBlank(context.getVersionNo(), "-")
                + "，安全摘要条目数=" + defaultIfBlank(String.valueOf(context.getTotalItems()), "0")
                + "。注意：源码导航只提供路径、类名、方法名、接口路径和伪代码级思路，禁止输出真实源码正文、配置值或密钥。");
        for (AiCodebaseGuideItemVO item : items) {
            StringBuilder line = new StringBuilder("- 源码定位：路径=").append(defaultIfBlank(item.getSourcePath(), "-"));
            if (StringUtils.isNotBlank(item.getLayer())) {
                line.append("；分层=").append(item.getLayer());
            }
            if (StringUtils.isNotBlank(item.getSymbolType())) {
                line.append("；类型=").append(item.getSymbolType());
            }
            if (StringUtils.isNotBlank(item.getClassName())) {
                line.append("；类/组件=").append(item.getClassName());
            }
            if (StringUtils.isNotBlank(item.getMethodName())) {
                line.append("；方法=").append(item.getMethodName());
            }
            if (StringUtils.isNotBlank(item.getSignature())) {
                line.append("；签名摘要=").append(shortenText(item.getSignature(), 220));
            }
            if (StringUtils.isNotBlank(item.getHttpMethod()) || StringUtils.isNotBlank(item.getEndpointPath())) {
                line.append("；接口=").append(defaultIfBlank(item.getHttpMethod(), ""))
                        .append(" ").append(defaultIfBlank(item.getEndpointPath(), "-"));
            }
            if (StringUtils.isNotBlank(item.getSummary())) {
                line.append("；职责=").append(shortenText(item.getSummary(), 160));
            }
            if (StringUtils.isNotBlank(item.getDevHint())) {
                line.append("；二开提示=").append(shortenText(item.getDevHint(), 180));
            }
            if (item.getTags() != null && !item.getTags().isEmpty()) {
                line.append("；标签=").append(String.join("、", item.getTags()));
            }
            lines.add(line.toString());
        }
        return lines;
    }

    private int calculateScore(String normalizedQuestion, List<String> keywords, AiCodebaseGuideItemVO item) {
        String searchText = normalizeText(buildSearchContent(item));
        int score = 0;
        for (String keyword : keywords) {
            String normalizedKeyword = normalizeText(keyword);
            if (StringUtils.isBlank(normalizedKeyword) || NOISE_TOKENS.contains(normalizedKeyword)) {
                continue;
            }
            if (searchText.contains(normalizedKeyword)) {
                score += normalizedKeyword.length() > 3 ? 4 : 2;
                if (normalizedKeyword.startsWith("/") && normalizedKeyword.length() > 6) {
                    score += 60;
                }
            }
        }
        score += exactHitScore(normalizedQuestion, item.getSourcePath(), 10);
        score += exactHitScore(normalizedQuestion, item.getClassName(), 10);
        score += exactHitScore(normalizedQuestion, item.getMethodName(), 8);
        score += exactHitScore(normalizedQuestion, item.getEndpointPath(), 8);
        score += typeBoost(normalizedQuestion, item);
        score += businessDomainBoost(normalizedQuestion, searchText);
        return score;
    }

    private int typeBoost(String normalizedQuestion, AiCodebaseGuideItemVO item) {
        String symbolType = normalizeText(item.getSymbolType());
        int boost = 0;
        if (normalizedQuestion.contains("接口") || normalizedQuestion.contains("api")) {
            if (symbolType.contains("api") || symbolType.contains("controller")) {
                boost += 6;
            }
        }
        if (normalizedQuestion.contains("前端") || normalizedQuestion.contains("页面") || normalizedQuestion.contains("vue")) {
            if (symbolType.contains("vue") || symbolType.contains("frontend")) {
                boost += 6;
            }
        }
        if (normalizedQuestion.contains("数据库") || normalizedQuestion.contains("表") || normalizedQuestion.contains("sql")) {
            if (symbolType.contains("sql") || symbolType.contains("mapper")) {
                boost += 6;
            }
        }
        return boost;
    }

    private int businessDomainBoost(String normalizedQuestion, String searchText) {
        int boost = 0;
        boost += genericSourceNavigationBoost(normalizedQuestion, searchText);
        if (containsAnyNormalized(normalizedQuestion, "serviceinvoke", "serviceinvokereply")
                && containsAnyNormalized(searchText, "serviceinvoke", "serviceinvokereply")) {
            if (containsAnyNormalized(searchText, "vue3/src/views/iot")) {
                boost += 22;
            }
            if (containsAnyNormalized(searchText, "vue3/src/api/iot/runstatus.js")) {
                boost += 8;
            }
        }
        if (isDeviceControlSourceQuestion(normalizedQuestion)) {
            if (containsAnyNormalized(searchText, "fastbee-open-api", "fastbee-iot-data",
                    "fastbee-service/fastbee-iot-service", "fastbee-mq", "vue3/src/api/iot",
                    "vue3/src/views/iot")) {
                boost += 12;
            }
            if (containsAnyNormalized(searchText, "deviceruntimecontroller", "devicemessagecontroller",
                    "functioninvoke", "ifunctioninvoke", "functioninvokequeue", "messageproducer",
                    "mqttmessagepublish", "service/invoke", "service/invokereply", "commandgenerate",
                    "runstatus.js", "mqtttest.js", "running-status", "realtime-status", "device-variable",
                    "device-variable-card", "device-edit")) {
                boost += 10;
            }
            if (containsAnyNormalized(normalizedQuestion, "权限", "权限校验", "校验", "鉴权")) {
                if (containsAnyNormalized(searchText, "preauthorize", "haspermi", "permission", "security")) {
                    boost += 22;
                }
                if (containsAnyNormalized(searchText, "deviceruntimecontroller", "service/invoke", "service/invokereply")) {
                    boost += 10;
                }
                if (isDeviceControlPermissionQuestion(normalizedQuestion)) {
                    if (containsAnyNormalized(searchText, "iot:service:invoke", "iot:service:invokereply")) {
                        boost += 30;
                    }
                    if (containsAnyNormalized(searchText, "deviceruntimecontroller")
                            && containsAnyNormalized(searchText, "invoke", "invokereply", "service/invoke", "service/invokereply")) {
                        boost += 28;
                    }
                    if (containsAnyNormalized(searchText, "devicemessagecontroller", "commandgenerate")) {
                        boost += 18;
                    }
                    if (containsAnyNormalized(searchText, "springboot/fastbee-open-api/src/main/java/com/fastbee/controller/device/devicecontroller.java")) {
                        boost -= 28;
                    }
                    if (containsAnyNormalized(searchText, "devicecontroller")
                            && !containsAnyNormalized(searchText, "deviceruntimecontroller", "devicemessagecontroller")) {
                        boost -= 18;
                    }
                }
            }
            if (containsAnyNormalized(normalizedQuestion, "回执", "回调", "回执结果", "返回结果", "更新")) {
                if (containsAnyNormalized(searchText, "invokereply", "selectlogbymessageid", "updatebymessageid",
                        "resultcode", "resultmsg", "replytime")) {
                    boost += 22;
                }
                if (containsAnyNormalized(searchText, "functioninvokeimpl", "ifunctioninvoke",
                        "ifunctionlogservice", "functionlogserviceimpl", "functionlogmapper", "iot_function_log")) {
                    boost += 16;
                }
                if (containsAnyNormalized(searchText, "functionlogmapper", "functionlog.java",
                        "iot_function_log", "tablename")) {
                    boost += 18;
                }
            }
            if (containsAnyNormalized(normalizedQuestion, "日志", "控制日志", "日志保存", "保存", "落库")) {
                if (containsAnyNormalized(searchText, "functionlog", "ifunctionlogservice", "functionlogserviceimpl",
                        "functionlogmapper", "iot_function_log", "insertfunctionlog")) {
                    boost += 22;
                }
                if (containsAnyNormalized(searchText, "mqttmessagepublish", "publishwithlog", "handlelog")) {
                    boost += 16;
                }
                if (containsAnyNormalized(searchText, "functionlogmapper", "functionlog.java",
                        "iot_function_log", "tablename")) {
                    boost += 24;
                }
            }
            if (isFrontendDeviceControlQuestion(normalizedQuestion)) {
                if (containsAnyNormalized(searchText, "vue3/src/views/iot/device/running-status.vue",
                        "vue3/src/views/iot/device/realtime-status.vue",
                        "vue3/src/views/iot/device/device-variable.vue",
                        "vue3/src/views/iot/device/device-variable-card.vue",
                        "vue3/src/views/iot/device/device-edit.vue",
                        "vue3/src/api/iot/runstatus.js")) {
                    boost += 18;
                }
                if (!containsAnyNormalized(normalizedQuestion, "日志", "记录", "回执")
                        && containsAnyNormalized(searchText, "functionlog", "function-log", "funcLog")) {
                    boost -= 10;
                }
            }
            if (!isAiDeviceControlCodeQuestion(normalizedQuestion)
                    && containsAnyNormalized(searchText, "fastbee-ai", "aidevicecontrol", "aichatserviceimpl")) {
                boost -= 14;
            }
        }
        return boost;
    }

    private int genericSourceNavigationBoost(String normalizedQuestion, String searchText) {
        int boost = 0;
        if (isPermissionSourceQuestion(normalizedQuestion)) {
            if (containsAnyNormalized(searchText, "preauthorize", "haspermi", "permission", "sacheckpermission",
                    "security", "datascope")) {
                boost += 18;
            }
            if (containsAnyNormalized(searchText, "controller", "backend_api", "postmapping", "getmapping",
                    "putmapping", "deletemapping")) {
                boost += 8;
            }
            if (containsAnyNormalized(searchText, "sysmenu", "sys_menu", "sysrole", "sys_role", "perms")) {
                boost += 8;
            }
        }
        if (isSystemPermissionSourceQuestion(normalizedQuestion)) {
            if (containsAnyNormalized(searchText, "syspermissionservice", "sysrolecontroller", "sysusercontroller",
                    "sysmenucontroller", "sysrolemapper", "sysmenumapper", "sys_user", "sys_role",
                    "sys_menu", "sys_user_role", "sys_role_menu")) {
                boost += 28;
            }
            if (containsAnyNormalized(searchText, "getrolepermission", "getmenupermission",
                    "selectrolepermissionbyuserid", "selectmenupermsbyuserid", "permissionservice")) {
                boost += 18;
            }
            if (containsAnyNormalized(searchText, "fastbee-ai", "aichat", "aimodel", "aiprovider")
                    && !containsAnyNormalized(searchText, "sysrole", "sysuser", "sysmenu", "syspermission")) {
                boost -= 16;
            }
        }
        if (isKnowledgeTemplatePermissionQuestion(normalizedQuestion)) {
            if (containsAnyNormalized(searchText, "aiknowledgetemplatecontroller", "aiknowledgedocumentcontroller",
                    "ai/knowledge/template/download", "ai/knowledge/document/download", "downloadtemplate")) {
                boost += 50;
            }
            if (containsAnyNormalized(searchText, "requireadminaccount")
                    && containsAnyNormalized(searchText, "aiknowledgetemplatecontroller",
                    "aiknowledgedocumentcontroller", "ai/knowledge/template/download",
                    "ai/knowledge/document/download", "downloadtemplate", "导出企业版模板", "下载知识文档")) {
                boost += 35;
            }
            if (containsAnyNormalized(searchText, "aiknowledgetemplateservice", "aisecuritysupport")) {
                boost += 24;
            }
            if (containsAnyNormalized(searchText, "knowledge/index.vue", "knowledgedocumentmanager",
                    "downloadtemplate", "templatemodeenterpriseexport")) {
                boost += 26;
            }
            if (containsAnyNormalized(searchText, "aiknowledgeversioncontroller", "aiknowledgebasecontroller",
                    "knowledgeruntime")
                    && !containsAnyNormalized(searchText, "aiknowledgetemplatecontroller",
                    "aiknowledgedocumentcontroller", "downloadtemplate")) {
                boost -= 18;
            }
            if (containsAnyNormalized(searchText, "notifytemplate", "notify/template")
                    && !containsAnyNormalized(normalizedQuestion, "通知")) {
                boost -= 24;
            }
        }
        if (isPersistenceSourceQuestion(normalizedQuestion)) {
            if (containsAnyNormalized(searchText, "mapper", "mybatis", "basemapperx", "tablename",
                    "sql_table", "domain", "entity")) {
                boost += 16;
            }
            if (containsAnyNormalized(searchText, "insert", "update", "delete", "select", "save", "remove",
                    "page", "list")) {
                boost += 8;
            }
            if (containsAnyNormalized(searchText, "serviceimpl", "service")) {
                boost += 6;
            }
        }
        if (isFrontendSourceQuestion(normalizedQuestion)) {
            if (containsAnyNormalized(searchText, "vue3/src/views", "vue_page", "frontend", "页面函数", "调用api")) {
                boost += 18;
            }
            if (containsAnyNormalized(searchText, "vue3/src/api", "frontend_api")) {
                boost += 10;
            }
        }
        if (isBackendApiSourceQuestion(normalizedQuestion)) {
            if (containsAnyNormalized(searchText, "backend_api", "controller", "postmapping", "getmapping",
                    "putmapping", "deletemapping", "requestmapping")) {
                boost += 18;
            }
        }
        if (isCallSiteQuestion(normalizedQuestion)) {
            if (containsAnyNormalized(searchText, "调用api", "导入", "页面函数", "frontend_api", "vue_page")) {
                boost += 14;
            }
            if (containsAnyNormalized(searchText, "调用=", ".invoke", ".select", ".insert", ".update")) {
                boost += 8;
            }
        }
        return boost;
    }

    private boolean isDeviceControlSourceQuestion(String normalizedQuestion) {
        return containsAnyNormalized(normalizedQuestion, "设备控制", "设备下发", "设备命令",
                "命令下发", "指令下发", "服务下发", "下发命令", "控制设备")
                && containsAnyNormalized(normalizedQuestion, "源码", "代码", "二开", "开发",
                "改代码", "代码位置", "源码位置", "源码路径", "在哪里改", "在哪改", "改哪里", "怎么改",
                "在哪里", "在哪", "哪里", "接口在哪里", "接口路径", "哪个接口", "哪个类", "哪个方法",
                "前端", "页面", "流程", "回执", "回调", "权限", "校验", "鉴权", "日志", "保存",
                "落库", "更新", "哪里处理", "在哪处理");
    }

    private boolean isPermissionSourceQuestion(String normalizedQuestion) {
        return containsAnyNormalized(normalizedQuestion, "权限", "权限校验", "鉴权", "授权", "校验",
                "数据权限", "按钮权限", "菜单权限", "角色权限", "访问控制");
    }

    private boolean isSystemPermissionSourceQuestion(String normalizedQuestion) {
        return containsAnyNormalized(normalizedQuestion, "用户角色权限", "用户权限", "角色权限", "菜单权限",
                "系统权限", "用户菜单权限", "角色菜单权限")
                || (containsAnyNormalized(normalizedQuestion, "权限", "权限校验", "鉴权")
                && containsAnyNormalized(normalizedQuestion, "用户", "角色", "菜单", "系统"));
    }

    private boolean isKnowledgeTemplatePermissionQuestion(String normalizedQuestion) {
        return containsAnyNormalized(normalizedQuestion, "模板下载", "文件模板", "文件模板下载", "下载模板",
                "模板权限", "下载权限", "导出企业版模板", "知识库模板", "知识文档下载",
                "文件管理下载", "下载按钮")
                && containsAnyNormalized(normalizedQuestion, "权限", "校验", "鉴权", "admin", "管理员");
    }

    private boolean isPersistenceSourceQuestion(String normalizedQuestion) {
        return containsAnyNormalized(normalizedQuestion, "保存", "落库", "入库", "更新", "删除", "查询",
                "数据表", "表名", "表结构", "哪个表", "哪张表", "mapper", "mybatis", "sql",
                "数据库", "持久化", "记录", "日志", "回执");
    }

    private boolean isFrontendSourceQuestion(String normalizedQuestion) {
        return containsAnyNormalized(normalizedQuestion, "前端", "页面", "vue", "按钮", "弹窗",
                "组件", "路由", "菜单", "表单", "列表", "详情页", "在哪里调用", "在哪调用");
    }

    private boolean isBackendApiSourceQuestion(String normalizedQuestion) {
        return containsAnyNormalized(normalizedQuestion, "接口", "api", "controller", "入口", "请求",
                "接口路径", "后端入口", "哪个接口", "哪些接口");
    }

    private boolean isCallSiteQuestion(String normalizedQuestion) {
        return containsAnyNormalized(normalizedQuestion, "在哪里调用", "在哪调用", "哪些地方调用",
                "哪些页面调用", "调用位置", "调用链", "谁调用", "被谁调用");
    }

    private boolean isFrontendDeviceControlQuestion(String normalizedQuestion) {
        return containsAnyNormalized(normalizedQuestion, "设备控制", "设备下发", "设备命令",
                "命令下发", "指令下发", "服务下发", "下发命令", "控制设备")
                && containsAnyNormalized(normalizedQuestion, "前端", "页面", "vue", "按钮", "弹窗", "交互");
    }

    private boolean isDeviceControlPermissionQuestion(String normalizedQuestion) {
        return containsAnyNormalized(normalizedQuestion, "设备控制", "设备下发", "设备命令",
                "命令下发", "指令下发", "服务下发", "下发命令", "控制设备")
                && containsAnyNormalized(normalizedQuestion, "权限", "权限校验", "校验", "鉴权");
    }

    private boolean isAiDeviceControlCodeQuestion(String normalizedQuestion) {
        return containsAnyNormalized(normalizedQuestion, "ai", "AI", "会话", "聊天", "平台助手",
                "智能对话", "自动识别", "路由", "fastbee-ai");
    }

    private int exactHitScore(String normalizedQuestion, String value, int score) {
        String normalizedValue = normalizeText(value);
        if (StringUtils.isBlank(normalizedQuestion) || StringUtils.isBlank(normalizedValue)) {
            return 0;
        }
        return normalizedQuestion.contains(normalizedValue) ? score : 0;
    }

    private boolean isCodebaseQuestion(String question) {
        if (StringUtils.isBlank(question)) {
            return false;
        }
        String normalizedQuestion = normalizeText(question);
        for (String hint : CODEBASE_QUERY_HINTS) {
            if (normalizedQuestion.contains(normalizeText(hint))) {
                return true;
            }
        }
        return false;
    }

    private List<String> extractKeywords(String question) {
        if (StringUtils.isBlank(question)) {
            return Collections.emptyList();
        }
        LinkedHashSet<String> keywords = new LinkedHashSet<>();
        String normalizedQuestion = question.toLowerCase(Locale.ROOT);
        Matcher matcher = ASCII_WORD_PATTERN.matcher(normalizedQuestion);
        while (matcher.find()) {
            String keyword = matcher.group();
            if (keyword.length() > 1) {
                keywords.add(keyword);
            }
        }
        for (String hint : CODEBASE_QUERY_HINTS) {
            if (normalizedQuestion.contains(hint.toLowerCase(Locale.ROOT))) {
                keywords.add(hint);
            }
        }
        addExpandedKeywords(keywords, question);
        String[] chineseParts = question.split("[\\s,，。；;：:?？!！{}（）()\\[\\]【】]+");
        for (String part : chineseParts) {
            String keyword = part == null ? "" : part.trim();
            if (keyword.length() >= 2 && keyword.length() <= 16 && !NOISE_TOKENS.contains(keyword)) {
                keywords.add(keyword);
            }
        }
        return new ArrayList<>(keywords);
    }

    private boolean isAllowedSourceFile(Path path) {
        String fileName = path.getFileName() == null ? "" : path.getFileName().toString().toLowerCase(Locale.ROOT);
        if (fileName.endsWith(".d.ts") || fileName.endsWith(".min.js")) {
            return false;
        }
        return fileName.endsWith(".java")
                || fileName.endsWith(".vue")
                || fileName.endsWith(".js")
                || fileName.endsWith(".ts")
                || fileName.endsWith(".xml")
                || fileName.endsWith(".sql");
    }

    private boolean isSensitivePath(Path rootPath, Path path) {
        String relativePath = normalizePath(rootPath.relativize(path)).toLowerCase(Locale.ROOT);
        String fileName = path.getFileName() == null ? "" : path.getFileName().toString().toLowerCase(Locale.ROOT);
        if (relativePath.contains("/.git/")
                || relativePath.contains("/.idea/")
                || relativePath.contains("/node_modules/")
                || relativePath.contains("/target/")
                || relativePath.contains("/dist/")
                || relativePath.contains("/.vite/")
                || relativePath.contains("/logs/")
                || relativePath.contains("/upload/")
                || relativePath.contains("/profile/")
                || relativePath.contains("/temp/")
                || relativePath.contains("/tmp/")) {
            return true;
        }
        return fileName.startsWith(".env")
                || fileName.startsWith("application")
                || fileName.startsWith("bootstrap")
                || fileName.endsWith(".key")
                || fileName.endsWith(".pem")
                || fileName.endsWith(".p12")
                || fileName.endsWith(".jks")
                || fileName.endsWith(".crt")
                || fileName.endsWith(".log")
                || fileName.endsWith(".zip")
                || fileName.endsWith(".jar")
                || fileName.endsWith(".class");
    }

    private String resolveLayer(String relativePath, String className) {
        String lowerPath = relativePath.toLowerCase(Locale.ROOT);
        String lowerClass = className == null ? "" : className.toLowerCase(Locale.ROOT);
        if (lowerClass.endsWith("controller") || lowerPath.contains("/controller/")) {
            return "后端控制器";
        }
        if (lowerClass.endsWith("service") || lowerClass.endsWith("serviceimpl") || lowerPath.contains("/service/")) {
            return "后端服务";
        }
        if (lowerClass.endsWith("mapper") || lowerPath.contains("/mapper/")) {
            return "数据访问";
        }
        if (lowerPath.contains("/domain/") || lowerPath.contains("/entity/")) {
            return "领域模型";
        }
        if (lowerPath.contains("/config/")) {
            return "配置类";
        }
        return "后端代码";
    }

    private List<String> buildTags(AiCodebaseGuideItemVO item) {
        LinkedHashSet<String> tags = new LinkedHashSet<>();
        addIfNotBlank(tags, item.getModuleName());
        addIfNotBlank(tags, item.getLayer());
        addIfNotBlank(tags, item.getSymbolType());
        addIfNotBlank(tags, item.getClassName());
        addIfNotBlank(tags, item.getMethodName());
        addIfNotBlank(tags, item.getEndpointPath());
        return new ArrayList<>(tags);
    }

    private List<String> buildAliases(AiCodebaseGuideItemVO item) {
        LinkedHashSet<String> aliases = new LinkedHashSet<>();
        addIfNotBlank(aliases, item.getSymbolName());
        addIfNotBlank(aliases, item.getPackageName());
        addDomainAliases(aliases, buildBaseSearchContent(item));
        return new ArrayList<>(aliases);
    }

    private String buildSummary(AiCodebaseGuideItemVO item) {
        if ("BACKEND_API".equals(item.getSymbolType())) {
            return "后端接口入口，可用于定位接口参数、权限、业务编排和返回结果相关二开点。";
        }
        if ("JAVA_METHOD".equals(item.getSymbolType())) {
            return "后端方法入口，可用于定位具体业务逻辑或扩展点。";
        }
        if ("JAVA_CLASS".equals(item.getSymbolType())) {
            return "后端类入口，可用于按模块和分层定位二开范围。";
        }
        if ("VUE_PAGE".equals(item.getSymbolType())) {
            return "前端页面入口，可用于定位页面布局、交互和组件展示相关二开点。";
        }
        if ("FRONTEND_API".equals(item.getSymbolType())) {
            return "前端接口封装入口，可用于定位页面调用的后端接口。";
        }
        if (item.getSymbolType() != null && item.getSymbolType().startsWith("MYBATIS")) {
            return "MyBatis 数据访问入口，可用于定位数据库读写语句所在位置。";
        }
        if ("SQL_TABLE".equals(item.getSymbolType())) {
            return "数据库表结构入口，可用于定位初始化脚本中的表定义。";
        }
        return "源码导航入口，可用于定位二开范围。";
    }

    private String buildDevHint(AiCodebaseGuideItemVO item) {
        if ("VUE_PAGE".equals(item.getSymbolType())) {
            return "需要调整页面展示、按钮、表单或交互，或回答客户二开咨询时，优先从该前端页面文件定位。";
        }
        if ("FRONTEND_API".equals(item.getSymbolType())) {
            return "需要确认前端调用了哪个后端接口，或回答客户二开咨询时，优先查看该接口封装方法。";
        }
        if ("BACKEND_API".equals(item.getSymbolType())) {
            return "需要新增参数、调整权限、改返回结构、衔接服务层，或回答客户二开咨询时，优先从该 Controller 方法定位。";
        }
        if (item.getSymbolType() != null && item.getSymbolType().startsWith("MYBATIS")) {
            return "需要调整查询条件、字段映射或数据写入，或回答客户二开咨询时，优先定位对应 Mapper XML 节点。";
        }
        if ("SQL_TABLE".equals(item.getSymbolType())) {
            return "需要了解表结构、补充初始化脚本，或回答客户二开咨询时，可从该 SQL 文件定位。";
        }
        return "需要二开或回答客户二开咨询时，可先从该路径、类名和方法名定位，再按项目技术栈生成伪代码级改造思路。";
    }

    private String buildSearchContent(AiCodebaseGuideItemVO item) {
        return String.join(" ",
                defaultIfBlank(item.getSourcePath(), ""),
                defaultIfBlank(item.getModuleName(), ""),
                defaultIfBlank(item.getSymbolType(), ""),
                defaultIfBlank(item.getLayer(), ""),
                defaultIfBlank(item.getPackageName(), ""),
                defaultIfBlank(item.getClassName(), ""),
                defaultIfBlank(item.getMethodName(), ""),
                defaultIfBlank(item.getSymbolName(), ""),
                defaultIfBlank(item.getSignature(), ""),
                defaultIfBlank(item.getHttpMethod(), ""),
                defaultIfBlank(item.getEndpointPath(), ""),
                defaultIfBlank(item.getSummary(), ""),
                defaultIfBlank(item.getDevHint(), ""),
                joinValues(item.getTags()),
                joinValues(item.getAliases()),
                buildDomainAliasContent(item));
    }

    private List<EndpointMapping> extractEndpointMappings(String annotations) {
        if (StringUtils.isBlank(annotations)) {
            return Collections.emptyList();
        }
        List<EndpointMapping> mappings = new ArrayList<>();
        Matcher matcher = MAPPING_PATTERN.matcher(annotations);
        while (matcher.find()) {
            String annotationName = matcher.group(1);
            String body = matcher.group(2);
            String httpMethod = resolveHttpMethod(annotationName, body);
            String path = extractFirstStringLiteral(body);
            mappings.add(new EndpointMapping(httpMethod, path));
        }
        return mappings;
    }

    private String resolveHttpMethod(String annotationName, String body) {
        if ("GetMapping".equals(annotationName)) {
            return "GET";
        }
        if ("PostMapping".equals(annotationName)) {
            return "POST";
        }
        if ("PutMapping".equals(annotationName)) {
            return "PUT";
        }
        if ("DeleteMapping".equals(annotationName)) {
            return "DELETE";
        }
        if ("PatchMapping".equals(annotationName)) {
            return "PATCH";
        }
        Matcher matcher = REQUEST_METHOD_PATTERN.matcher(defaultIfBlank(body, ""));
        return matcher.find() ? matcher.group(1) : "";
    }

    private String extractFirstEndpointPath(String annotations) {
        List<EndpointMapping> mappings = extractEndpointMappings(annotations);
        if (mappings.isEmpty()) {
            return "";
        }
        return defaultIfBlank(mappings.get(0).path(), "");
    }

    private String extractFirstStringLiteral(String body) {
        Matcher matcher = STRING_LITERAL_PATTERN.matcher(defaultIfBlank(body, ""));
        if (!matcher.find()) {
            return "";
        }
        return StringUtils.defaultIfBlank(matcher.group(1), matcher.group(2));
    }

    private String combineEndpointPaths(String basePath, String childPath) {
        String base = defaultIfBlank(basePath, "");
        String child = defaultIfBlank(childPath, "");
        if (StringUtils.isBlank(base)) {
            return child;
        }
        if (StringUtils.isBlank(child)) {
            return base;
        }
        return ("/" + base + "/" + child).replaceAll("/+", "/");
    }

    private String collectAnnotationWindow(List<String> lines, int lineIndex) {
        StringBuilder builder = new StringBuilder();
        for (int index = lineIndex - 1; index >= 0 && lineIndex - index <= 8; index--) {
            String line = lines.get(index).trim();
            if (StringUtils.isBlank(line)) {
                continue;
            }
            if (line.startsWith("//")) {
                continue;
            }
            if (!line.startsWith("@")) {
                break;
            }
            builder.insert(0, line).insert(0, '\n');
        }
        return builder.toString();
    }

    private String findNearbyUrl(List<String> lines, int startIndex) {
        int endIndex = Math.min(lines.size(), startIndex + 12);
        for (int index = startIndex; index < endIndex; index++) {
            Matcher matcher = URL_PATTERN.matcher(lines.get(index));
            if (matcher.find()) {
                return matcher.group(1);
            }
            matcher = FRONTEND_LITERAL_API_PATH_PATTERN.matcher(lines.get(index));
            if (matcher.find() && isLikelyFrontendApiPath(matcher.group(1))) {
                return matcher.group(1);
            }
        }
        return "";
    }

    private boolean hasFileItem(List<AiCodebaseGuideItemVO> items, String relativePath) {
        return items.stream().anyMatch(item -> relativePath.equals(item.getSourcePath()));
    }

    private boolean isIgnoredMethod(String methodName) {
        return Set.of("if", "for", "while", "switch", "catch").contains(methodName);
    }

    private String extractJavaMethodName(String line) {
        Matcher matcher = METHOD_PATTERN.matcher(defaultIfBlank(line, ""));
        if (matcher.matches()) {
            return matcher.group(2);
        }
        matcher = METHOD_NAME_PATTERN.matcher(defaultIfBlank(line, ""));
        return matcher.find() ? matcher.group(2) : "";
    }

    private String collectJavaMethodDeclaration(List<String> lines, int startIndex) {
        if (lines == null || startIndex < 0 || startIndex >= lines.size()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        int endIndex = Math.min(lines.size(), startIndex + 12);
        for (int index = startIndex; index < endIndex; index++) {
            String line = stripJavaLineComment(lines.get(index)).trim();
            if (StringUtils.isBlank(line)) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(line);
            if (line.contains("{") || line.endsWith(";")) {
                break;
            }
        }
        return builder.toString();
    }

    private String sanitizeSignature(String line) {
        String signature = defaultIfBlank(line, "").trim();
        if (signature.endsWith("{")) {
            signature = signature.substring(0, signature.length() - 1).trim();
        }
        if (signature.endsWith(";")) {
            signature = signature.substring(0, signature.length() - 1).trim();
        }
        return shortenText(signature, 180);
    }

    private Map<String, String> collectJavaFieldTypes(List<String> lines) {
        if (lines == null || lines.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, String> fieldTypes = new LinkedHashMap<>();
        for (String line : lines) {
            Matcher matcher = JAVA_FIELD_PATTERN.matcher(defaultIfBlank(line, ""));
            if (!matcher.matches()) {
                continue;
            }
            String typeName = simpleJavaTypeName(matcher.group(1));
            String fieldName = matcher.group(2);
            if (StringUtils.isNotBlank(typeName) && StringUtils.isNotBlank(fieldName)) {
                fieldTypes.put(fieldName, typeName);
            }
        }
        return fieldTypes;
    }

    private String buildJavaClassSignature(String className, String annotationWindow) {
        List<String> parts = new ArrayList<>();
        addIfNotBlank(parts, className);
        addIfNotBlank(parts, buildJavaAnnotationSummary(annotationWindow));
        return shortenText(String.join("；", parts), 260);
    }

    private String buildJavaMethodSignature(String methodLine,
                                            String annotationWindow,
                                            List<String> lines,
                                            int lineIndex,
                                            Map<String, String> fieldTypes) {
        List<String> parts = new ArrayList<>();
        addIfNotBlank(parts, buildJavaAnnotationSummary(annotationWindow));
        addIfNotBlank(parts, collectJavaCallSummary(lines, lineIndex, fieldTypes));
        addIfNotBlank(parts, sanitizeSignature(methodLine));
        return shortenText(String.join("；", parts), 360);
    }

    private String buildJavaAnnotationSummary(String annotationWindow) {
        if (StringUtils.isBlank(annotationWindow)) {
            return "";
        }
        LinkedHashSet<String> annotations = new LinkedHashSet<>();
        Matcher annotationMatcher = ANNOTATION_NAME_PATTERN.matcher(annotationWindow);
        while (annotationMatcher.find()) {
            String annotationName = simpleJavaTypeName(annotationMatcher.group(1));
            addIfNotBlank(annotations, annotationName);
        }

        LinkedHashSet<String> permissions = new LinkedHashSet<>();
        Matcher permissionMatcher = PERMISSION_LITERAL_PATTERN.matcher(annotationWindow);
        while (permissionMatcher.find()) {
            addIfNotBlank(permissions, permissionMatcher.group(1));
        }

        LinkedHashSet<String> tableNames = new LinkedHashSet<>();
        Matcher tableMatcher = TABLE_NAME_ANNOTATION_PATTERN.matcher(annotationWindow);
        while (tableMatcher.find()) {
            addIfNotBlank(tableNames, tableMatcher.group(1));
        }

        List<String> parts = new ArrayList<>();
        if (!annotations.isEmpty()) {
            parts.add("注解=" + annotations.stream().limit(8).collect(Collectors.joining("、")));
        }
        if (!permissions.isEmpty()) {
            parts.add("权限=" + permissions.stream().limit(4).collect(Collectors.joining("、")));
        }
        if (!tableNames.isEmpty()) {
            parts.add("表=" + tableNames.stream().limit(4).collect(Collectors.joining("、")));
        }
        return String.join("；", parts);
    }

    private String collectJavaCallSummary(List<String> lines, int lineIndex, Map<String, String> fieldTypes) {
        if (lines == null || lineIndex < 0 || lineIndex >= lines.size()) {
            return "";
        }
        LinkedHashSet<String> calls = new LinkedHashSet<>();
        int braceBalance = 0;
        boolean bodyStarted = false;
        int endIndex = Math.min(lines.size(), lineIndex + 120);
        for (int index = lineIndex; index < endIndex; index++) {
            String line = stripJavaLineComment(lines.get(index));
            if (!bodyStarted && line.contains("{")) {
                bodyStarted = true;
            }
            if (bodyStarted) {
                collectImportantJavaCalls(line, fieldTypes, calls);
                collectImportantBareCalls(line, calls);
            }
            braceBalance += countChar(line, '{') - countChar(line, '}');
            if (bodyStarted && index > lineIndex && braceBalance <= 0) {
                break;
            }
            if (!bodyStarted && index > lineIndex) {
                break;
            }
        }
        if (calls.isEmpty()) {
            return "";
        }
        return "调用=" + joinPrioritizedValues(calls, 14,
                "AiSecuritySupport.requireAdminAccount",
                "IFunctionLogService.updateByMessageId",
                "IFunctionLogService.selectLogByMessageId",
                "IFunctionLogService.insertFunctionLog",
                "FunctionLogMapper.update",
                "FunctionLogMapper.insert",
                "MessageProducer.sendFunctionInvoke",
                "MqttMessagePublishImpl.publishFunction",
                "publishWithLog",
                "handleLog",
                "updateFunctionLog",
                "invokeNoReply",
                "queryData");
    }

    private void collectImportantJavaCalls(String line, Map<String, String> fieldTypes, Set<String> calls) {
        Matcher matcher = JAVA_CALL_PATTERN.matcher(defaultIfBlank(line, ""));
        while (matcher.find()) {
            String target = matcher.group(1);
            String method = matcher.group(2);
            if (!isImportantJavaCall(target, method)) {
                continue;
            }
            String fieldType = fieldTypes == null ? "" : fieldTypes.get(target);
            String typedCall = StringUtils.isNotBlank(fieldType) ? fieldType + "." + method : target + "." + method;
            addIfNotBlank(calls, typedCall);
            if (StringUtils.isNotBlank(fieldType)) {
                addIfNotBlank(calls, target + "." + method);
            }
        }
    }

    private void collectImportantBareCalls(String line, Set<String> calls) {
        addBareCallIfPresent(line, calls, "publishWithLog");
        addBareCallIfPresent(line, calls, "handleLog");
        addBareCallIfPresent(line, calls, "invokeNoReply");
        addBareCallIfPresent(line, calls, "queryData");
        addBareCallIfPresent(line, calls, "updateFunctionLog");
        addBareCallIfPresent(line, calls, "insertFunctionLog");
        addBareCallIfPresent(line, calls, "updateByMessageId");
        addBareCallIfPresent(line, calls, "selectLogByMessageId");
        addBareCallIfPresent(line, calls, "sendFunctionInvoke");
        addBareCallIfPresent(line, calls, "requireAdminAccount");
    }

    private void addBareCallIfPresent(String line, Set<String> calls, String methodName) {
        if (StringUtils.isNotBlank(line) && line.contains(methodName + "(")) {
            addIfNotBlank(calls, methodName);
        }
    }

    private boolean isImportantJavaCall(String target, String method) {
        String normalizedTarget = normalizeText(target);
        String normalizedMethod = normalizeText(method);
        if (StringUtils.isBlank(normalizedTarget) || StringUtils.isBlank(normalizedMethod)) {
            return false;
        }
        if (normalizedMethod.startsWith("get") || normalizedMethod.startsWith("set") || normalizedMethod.startsWith("is")) {
            return false;
        }
        if (Set.of("put", "add", "remove", "format", "sleep", "equals", "tostring", "hashcode", "print", "println")
                .contains(normalizedMethod)) {
            return false;
        }
        String combined = normalizedTarget + "." + normalizedMethod;
        return containsAnyNormalized(combined,
                "service", "mapper", "producer", "publisher", "publish", "invoke", "functionlog",
                "mqtt", "runtime", "permission", "security", "haspermi", "selectlogbymessageid",
                "updatebymessageid", "insertfunctionlog", "sendfunctioninvoke", "messageproducer",
                "datahandler", "ruleengine", "scriptservice", "mqttclient");
    }

    private String stripJavaLineComment(String line) {
        String actualLine = defaultIfBlank(line, "");
        int index = actualLine.indexOf("//");
        return index >= 0 ? actualLine.substring(0, index) : actualLine;
    }

    private int countChar(String text, char target) {
        if (StringUtils.isBlank(text)) {
            return 0;
        }
        int count = 0;
        for (int index = 0; index < text.length(); index++) {
            if (text.charAt(index) == target) {
                count++;
            }
        }
        return count;
    }

    private String simpleJavaTypeName(String typeName) {
        String actualType = defaultIfBlank(typeName, "").trim();
        int genericIndex = actualType.indexOf('<');
        if (genericIndex > 0) {
            actualType = actualType.substring(0, genericIndex);
        }
        int dotIndex = actualType.lastIndexOf('.');
        if (dotIndex >= 0 && dotIndex + 1 < actualType.length()) {
            actualType = actualType.substring(dotIndex + 1);
        }
        return actualType.replaceAll("[^A-Za-z0-9_]", "");
    }

    private String resolveModuleName(String relativePath) {
        String path = defaultIfBlank(relativePath, "");
        int index = path.indexOf('/');
        return index > 0 ? path.substring(0, index) : path;
    }

    private String stripExtension(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return "";
        }
        int index = fileName.lastIndexOf('.');
        return index > 0 ? fileName.substring(0, index) : fileName;
    }

    private String sanitizeFileName(String value) {
        return defaultIfBlank(value, "codebase-guide").replaceAll("[^A-Za-z0-9_.-]", "_");
    }

    private String normalizePath(Path path) {
        return path == null ? "" : path.toString().replace('\\', '/');
    }

    private void addExpandedKeywords(Set<String> keywords, String question) {
        String normalizedQuestion = normalizeText(question);
        for (KeywordExpansion expansion : CODEBASE_KEYWORD_EXPANSIONS) {
            if (normalizedQuestion.contains(normalizeText(expansion.trigger()))) {
                expansion.keywords().forEach(keyword -> addIfNotBlank(keywords, keyword));
            }
        }
    }

    private void addDomainAliases(Set<String> aliases, String content) {
        String searchText = normalizeText(content);
        if (StringUtils.isBlank(searchText)) {
            return;
        }
        if (containsAnyNormalized(searchText, "devicecontrol", "device/control", "devicecommand",
                "functioninvoke", "ifunctioninvoke", "functioninvokequeue", "messageproducer",
                "mqttmessagepublish", "invokereply", "invokenoreply", "commandgenerate", "runtime/service",
                "service/invoke", "service/invokereply", "deviceruntimecontroller", "devicemessagecontroller",
                "functionlog", "runstatus.js", "mqtttest.js", "running-status", "realtime-status",
                "device-variable", "device-variable-card", "device-edit")) {
            addIfNotBlank(aliases, "设备控制");
            addIfNotBlank(aliases, "设备下发");
            addIfNotBlank(aliases, "命令下发");
            addIfNotBlank(aliases, "指令下发");
            addIfNotBlank(aliases, "服务下发");
            addIfNotBlank(aliases, "设备控制前端页面");
        }
        if (containsAnyNormalized(searchText, "preauthorize", "haspermi", "permission", "security")) {
            addIfNotBlank(aliases, "权限校验");
            addIfNotBlank(aliases, "接口权限");
            addIfNotBlank(aliases, "设备控制权限");
        }
        if (containsAnyNormalized(searchText, "functionlog", "iot_function_log", "insertfunctionlog",
                "updatebymessageid", "selectlogbymessageid", "functionlogmapper")) {
            addIfNotBlank(aliases, "设备控制日志");
            addIfNotBlank(aliases, "设备下发日志");
            addIfNotBlank(aliases, "日志保存");
            addIfNotBlank(aliases, "回执结果");
            addIfNotBlank(aliases, "回执更新");
        }
        if (containsAnyNormalized(searchText, "productcontroller", "productservice", "productmapper",
                "iot_product", "thingsmodel", "thingmodel")) {
            addIfNotBlank(aliases, "产品管理");
            addIfNotBlank(aliases, "产品");
            addIfNotBlank(aliases, "物模型");
        }
        if (containsAnyNormalized(searchText, "devicecontroller", "deviceservice", "devicemapper",
                "iot_device")) {
            addIfNotBlank(aliases, "设备管理");
            addIfNotBlank(aliases, "设备");
        }
        if (containsAnyNormalized(searchText, "thingsmodel", "thingsmodels", "thingmodel", "物模型")) {
            addIfNotBlank(aliases, "物模型");
        }
        if (containsAnyNormalized(searchText, "ruleengine", "rule", "scene", "scenemodel", "script")) {
            addIfNotBlank(aliases, "规则引擎");
            addIfNotBlank(aliases, "场景联动");
        }
        if (containsAnyNormalized(searchText, "alarm", "alert", "notify", "notice", "event")) {
            addIfNotBlank(aliases, "告警");
            addIfNotBlank(aliases, "通知");
        }
        if (containsAnyNormalized(searchText, "datacenter", "dashboard", "statistics", "chart")) {
            addIfNotBlank(aliases, "数据中心");
            addIfNotBlank(aliases, "统计看板");
        }
        if (containsAnyNormalized(searchText, "sysuser", "sys_user", "sysrole", "sys_role",
                "sysmenu", "sys_menu", "perms")) {
            addIfNotBlank(aliases, "用户权限");
            addIfNotBlank(aliases, "角色权限");
            addIfNotBlank(aliases, "菜单权限");
        }
        if (containsAnyNormalized(searchText, "syspermissionservice", "getrolepermission", "getmenupermission",
                "selectrolepermissionbyuserid", "selectmenupermsbyuserid", "sys_user_role", "sys_role_menu")) {
            addIfNotBlank(aliases, "系统权限");
            addIfNotBlank(aliases, "用户角色权限");
            addIfNotBlank(aliases, "权限校验");
        }
        if (containsAnyNormalized(searchText, "tenant", "tenantid", "sys_tenant")) {
            addIfNotBlank(aliases, "租户");
        }
        if (containsAnyNormalized(searchText, "dict", "sysdict", "sys_dict", "dictionary")) {
            addIfNotBlank(aliases, "字典");
        }
        if (containsAnyNormalized(searchText, "file", "document", "upload", "oss", "storage", "minio")) {
            addIfNotBlank(aliases, "文件管理");
            addIfNotBlank(aliases, "文件上传");
        }
        if (containsAnyNormalized(searchText, "template", "workbook", "excel", "export", "import")) {
            addIfNotBlank(aliases, "模板");
            addIfNotBlank(aliases, "导入导出");
        }
        if (containsAnyNormalized(searchText, "aiknowledgetemplatecontroller", "aiknowledgetemplateservice",
                "aiknowledgedocumentcontroller", "aisecuritysupport", "requireadminaccount",
                "ai/knowledge/template/download", "ai/knowledge/document/download",
                "knowledgedocumentmanager", "templatemodeenterpriseexport")) {
            addIfNotBlank(aliases, "知识库模板下载");
            addIfNotBlank(aliases, "文件管理下载");
            addIfNotBlank(aliases, "文件模板下载权限");
            addIfNotBlank(aliases, "下载权限校验");
            addIfNotBlank(aliases, "导出企业版模板");
            addIfNotBlank(aliases, "admin权限校验");
        }
        if (containsAnyNormalized(searchText, "knowledgebase", "knowledgedocument", "knowledgeversion",
                "knowledgeruntime", "knowledge")) {
            addIfNotBlank(aliases, "知识库");
        }
        if (containsAnyNormalized(searchText, "platformassistant", "platformdoc", "platform", "assistant")) {
            addIfNotBlank(aliases, "平台助手");
        }
        if (containsAnyNormalized(searchText, "protocoladaptation", "protocolparse", "protocol", "codec")) {
            addIfNotBlank(aliases, "协议适配");
            addIfNotBlank(aliases, "协议解析");
        }
        if (containsAnyNormalized(searchText, "nl2sql", "semanticquery", "semantic", "query")) {
            addIfNotBlank(aliases, "智能问数");
            addIfNotBlank(aliases, "问数");
        }
        if (containsAnyNormalized(searchText, "chat", "session", "message", "conversation")) {
            addIfNotBlank(aliases, "会话");
            addIfNotBlank(aliases, "聊天");
        }
        if (containsAnyNormalized(searchText, "vue", "views", "frontend")) {
            addIfNotBlank(aliases, "前端页面");
            addIfNotBlank(aliases, "页面代码");
        }
    }

    private boolean containsAnyNormalized(String content, String... keywords) {
        if (StringUtils.isBlank(content) || keywords == null) {
            return false;
        }
        for (String keyword : keywords) {
            if (StringUtils.isNotBlank(keyword) && content.contains(normalizeText(keyword))) {
                return true;
            }
        }
        return false;
    }

    private String buildBaseSearchContent(AiCodebaseGuideItemVO item) {
        return String.join(" ",
                defaultIfBlank(item.getSourcePath(), ""),
                defaultIfBlank(item.getModuleName(), ""),
                defaultIfBlank(item.getSymbolType(), ""),
                defaultIfBlank(item.getLayer(), ""),
                defaultIfBlank(item.getPackageName(), ""),
                defaultIfBlank(item.getClassName(), ""),
                defaultIfBlank(item.getMethodName(), ""),
                defaultIfBlank(item.getSymbolName(), ""),
                defaultIfBlank(item.getSignature(), ""),
                defaultIfBlank(item.getHttpMethod(), ""),
                defaultIfBlank(item.getEndpointPath(), ""),
                defaultIfBlank(item.getSummary(), ""),
                defaultIfBlank(item.getDevHint(), ""),
                joinValues(item.getTags()));
    }

    private String buildDomainAliasContent(AiCodebaseGuideItemVO item) {
        LinkedHashSet<String> aliases = new LinkedHashSet<>();
        addDomainAliases(aliases, buildBaseSearchContent(item));
        return joinValues(new ArrayList<>(aliases));
    }

    private String normalizeText(String text) {
        return defaultIfBlank(text, "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", "")
                .trim();
    }

    private String shortenText(String text, int maxLength) {
        String actualText = defaultIfBlank(text, "").trim();
        if (actualText.length() <= maxLength) {
            return actualText;
        }
        return actualText.substring(0, maxLength) + "...";
    }

    private void addIfNotBlank(Set<String> values, String value) {
        if (StringUtils.isNotBlank(value)) {
            values.add(value);
        }
    }

    private void addIfNotBlank(List<String> values, String value) {
        if (StringUtils.isNotBlank(value)) {
            values.add(value);
        }
    }

    private String defaultIfBlank(String value, String defaultValue) {
        return StringUtils.isBlank(value) ? defaultValue : value;
    }

    private String limitText(String value, int maxLength) {
        String actualValue = defaultIfBlank(value, "")
                .replace("\u0000", "")
                .replaceAll("[\\r\\n\\t]+", " ")
                .trim();
        if (actualValue.length() <= maxLength) {
            return actualValue;
        }
        return actualValue.substring(0, maxLength);
    }

    private String limitCode(String value, int maxLength) {
        String actualValue = limitText(value, maxLength).replaceAll("[^A-Za-z0-9_./:-]", "");
        if (actualValue.length() <= maxLength) {
            return actualValue;
        }
        return actualValue.substring(0, maxLength);
    }

    private String joinValues(List<String> values) {
        if (values == null || values.isEmpty()) {
            return "";
        }
        return values.stream()
                .filter(value -> StringUtils.isNotBlank(value))
                .collect(Collectors.joining(" "));
    }

    private String sha256(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(bytes);
            StringBuilder builder = new StringBuilder();
            for (byte item : hash) {
                builder.append(String.format("%02x", item));
            }
            return builder.toString();
        } catch (Exception ex) {
            return "";
        }
    }

    private record EndpointMapping(String httpMethod, String path) {
    }

    private record KeywordExpansion(String trigger, List<String> keywords) {
    }

    private record CodebaseScanResult(Path rootPath,
                                      List<String> includeRoots,
                                      int scannedFileCount,
                                      int skippedFileCount,
                                      List<AiCodebaseGuideItemVO> items) {

        private int warningCount() {
            return skippedFileCount;
        }
    }
}
