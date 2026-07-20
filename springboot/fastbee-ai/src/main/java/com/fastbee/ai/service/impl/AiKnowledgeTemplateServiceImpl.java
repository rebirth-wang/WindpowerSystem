package com.fastbee.ai.service.impl;

import static com.fastbee.ai.support.AiI18nMessageUtils.message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import java.util.Set;
import java.util.UUID;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import org.anyline.metadata.Table;
import org.anyline.proxy.ServiceProxy;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fastbee.ai.domain.AiKnowledgeBase;
import com.fastbee.ai.domain.AiKnowledgeDocument;
import com.fastbee.ai.model.template.AiNl2SqlSemanticTemplateRow;
import com.fastbee.ai.model.template.AiPlatformDocTemplateRow;
import com.fastbee.ai.model.template.AiProtocolKnowledgeTemplateRow;
import com.fastbee.ai.model.vo.AiKnowledgeDocumentParseResultVO;
import com.fastbee.ai.model.vo.AiKnowledgeTemplateUploadResultVO;
import com.fastbee.ai.model.vo.AiTemplateValidationIssueVO;
import com.fastbee.ai.service.IAiKnowledgeBaseService;
import com.fastbee.ai.service.IAiKnowledgeDocumentService;
import com.fastbee.ai.service.IAiKnowledgeTemplateService;
import com.fastbee.ai.service.IAiPlatformDocTemplateSourceService;
import com.fastbee.ai.support.AiSecuritySupport;
import com.fastbee.common.annotation.Excel.Type;
import com.fastbee.common.config.RuoYiConfig;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.ai.annotation.AiSemanticField;
import com.fastbee.common.extend.ai.annotation.AiSemanticIgnore;
import com.fastbee.common.extend.utils.poi.ExcelUtil;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.generator.domain.GenTableColumn;
import com.fastbee.generator.service.IGenTableService;

@Service
public class AiKnowledgeTemplateServiceImpl implements IAiKnowledgeTemplateService {

    private static final String KB_TYPE_PLATFORM_DOC = "PLATFORM_DOC";
    private static final String KB_TYPE_NL2SQL = "NL2SQL_SEMANTIC";
    private static final String KB_TYPE_PROTOCOL = "PROTOCOL_SPEC";
    private static final String KB_TYPE_CODEBASE_GUIDE = "CODEBASE_GUIDE";
    private static final String TEMPLATE_VERSION_V1 = "v1";
    private static final String TEMPLATE_VERSION_V2 = "v2";
    private static final String TEMPLATE_MODE_EMPTY = "EMPTY";
    private static final String TEMPLATE_MODE_ENTERPRISE_EXPORT = "ENTERPRISE_EXPORT";
    private static final String TEMPLATE_MODE_ENTERPRISE_DOC_EXPORT = "ENTERPRISE_DOC_EXPORT";
    private static final String SOURCE_STRATEGY_AUTO = "AUTO";
    private static final String SOURCE_STRATEGY_LOCAL = "LOCAL";
    private static final String SOURCE_STRATEGY_WEB = "WEB";
    private static final String VERSION_STATUS_DRAFT = "DRAFT";
    private static final String DOCUMENT_STATUS_SUCCESS = "SUCCESS";
    private static final String DOCUMENT_STATUS_FAILED = "FAILED";
    private static final String DOCUMENT_STATUS_PARSING = "PARSING";
    private static final String DOCUMENT_SOURCE_OFFICIAL = "OFFICIAL";
    private static final String DOCUMENT_SOURCE_CUSTOM = "CUSTOM";
    private static final int DOCUMENT_SORT_STEP = 10;
    private static final int OFFICIAL_DOCUMENT_SORT_BASE = 100;
    private static final int CUSTOM_DOCUMENT_SORT_BASE = 200;
    private static final int DEFAULT_DOCUMENT_SORT_NUM = 100;
    private static final String SEMANTIC_RELATION_KEY = "RELATION_KEY";
    private static final String SEMANTIC_ENUM = "ENUM";
    private static final String SEMANTIC_DIMENSION = "DIMENSION";
    private static final String SOURCE_MANUAL = "MANUAL";
    private static final String SOURCE_DICT = "DICT";
    private static final String SOURCE_ENUM = "ENUM";
    private static final String SOURCE_AUTO_COMMENT = "AUTO_COMMENT";
    private static final String VALIDATION_LEVEL_ERROR = "ERROR";
    private static final String VALIDATION_LEVEL_WARNING = "WARNING";
    private static final String[] TEMPLATE_IGNORE_PREFIXES = {"sj_", "act_", "flw_", "gen_"};
    private static final String HEADER_TABLE_NAME = "表名";
    private static final String HEADER_TABLE_COMMENT = "表说明";
    private static final String HEADER_COLUMN_NAME = "字段名";
    private static final String HEADER_COLUMN_COMMENT = "字段说明";
    private static final String HEADER_SEMANTIC_NAME = "语义名称";
    private static final String HEADER_SEMANTIC_TYPE = "语义类型";
    private static final String HEADER_SOURCE_TYPE = "来源类型";
    private static final String HEADER_SOURCE_CODE = "来源编码";
    private static final String HEADER_RELATION_HINTS = "关联提示";
    private static final String HEADER_VALUE_MAPPINGS = "值映射";
    private static final String HEADER_ALIASES = "同义词";
    private static final String HEADER_QUERY_HINTS = "查询提示";
    private static final String HEADER_REMARK = "备注";
    private static final Map<String, String> PREFERRED_RELATION_TABLES = Map.of(
            "device_id", "iot_device",
            "serial_number", "iot_device",
            "product_id", "iot_product",
            "model_id", "iot_things_model",
            "scene_model_id", "scene_model",
            "category_id", "iot_category",
            "user_id", "sys_user",
            "role_id", "sys_role",
            "dept_id", "sys_dept");
    private static final Set<String> RELATION_KEY_COLUMNS = Set.of(
            "tenant_id", "dept_id", "user_id", "device_id", "product_id", "scene_id", "model_id",
            "task_id", "alert_id", "order_id", "category_id", "group_id", "script_id", "version_id",
            "parent_id", "serial_number", "client_id");

    @Resource
    private IAiKnowledgeBaseService aiKnowledgeBaseService;

    @Resource
    private IAiKnowledgeDocumentService aiKnowledgeDocumentService;

    @Resource
    private IGenTableService genTableService;

    @Resource
    private IAiPlatformDocTemplateSourceService aiPlatformDocTemplateSourceService;

    @Value("${spring.datasource.dynamic.primary:master}")
    private String primaryDataSource;

    @Override
    public void downloadTemplate(HttpServletResponse response, Long knowledgeBaseId, String kbType,
                                 String templateMode, String sourceStrategy) {
        String actualKbType = resolveKnowledgeType(knowledgeBaseId, kbType);
        String actualTemplateMode = normalizeTemplateMode(templateMode);
        if (KB_TYPE_NL2SQL.equals(actualKbType)) {
            exportNl2SqlTemplate(response, actualTemplateMode);
            return;
        }
        if (KB_TYPE_PROTOCOL.equals(actualKbType)) {
            exportProtocolTemplate(response, actualTemplateMode);
            return;
        }
        if (KB_TYPE_CODEBASE_GUIDE.equals(actualKbType)) {
            throw new ServiceException(message("ai.knowledge.template.codebase.excel.unsupported"));
        }
        exportPlatformDocTemplate(response, actualTemplateMode, sourceStrategy);
    }

    private void exportNl2SqlTemplate(HttpServletResponse response, String templateMode) {
        List<AiNl2SqlSemanticTemplateRow> rows = Collections.emptyList();
        if (TEMPLATE_MODE_ENTERPRISE_EXPORT.equals(templateMode)) {
            rows = buildNl2SqlSemanticTemplateRows();
        }
        ExcelUtil<AiNl2SqlSemanticTemplateRow> excelUtil = new ExcelUtil<>(AiNl2SqlSemanticTemplateRow.class);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        excelUtil.init(rows, "字段语义模板", StringUtils.EMPTY, Type.EXPORT);
        appendNl2SqlInstructionSheet(excelUtil.getWorkbook(), templateMode, rows.size());
        appendNl2SqlEnterpriseSheets(excelUtil.getWorkbook(), templateMode, rows);
        excelUtil.exportExcel(response);
    }

    private void exportProtocolTemplate(HttpServletResponse response, String templateMode) {
        ExcelUtil<AiProtocolKnowledgeTemplateRow> excelUtil = new ExcelUtil<>(AiProtocolKnowledgeTemplateRow.class);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        excelUtil.init(Collections.emptyList(), "协议知识模板", StringUtils.EMPTY, Type.EXPORT);
        appendProtocolInstructionSheet(excelUtil.getWorkbook(), templateMode);
        excelUtil.exportExcel(response);
    }

    private void exportPlatformDocTemplate(HttpServletResponse response, String templateMode, String sourceStrategy) {
        String actualSourceStrategy = normalizeSourceStrategy(sourceStrategy);
        List<AiPlatformDocTemplateRow> rows = Collections.emptyList();
        if (TEMPLATE_MODE_ENTERPRISE_EXPORT.equals(templateMode)) {
            rows = aiPlatformDocTemplateSourceService.loadTemplateRows(actualSourceStrategy);
        }
        ExcelUtil<AiPlatformDocTemplateRow> excelUtil = new ExcelUtil<>(AiPlatformDocTemplateRow.class);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        excelUtil.init(rows, "平台知识模板", StringUtils.EMPTY, Type.EXPORT);
        appendPlatformDocInstructionSheet(excelUtil.getWorkbook(), templateMode, actualSourceStrategy, rows.size());
        excelUtil.exportExcel(response);
    }

    /**
     * 追加“填写说明”sheet，避免用户仅看列头仍然不清楚填写口径。
     * 第一张 sheet 仍然保持字段语义模板，不影响现有上传解析逻辑。
     */
    private void appendNl2SqlInstructionSheet(Workbook workbook, String templateMode, int rowCount) {
        workbook.setSheetName(0, "字段语义模板");
        Sheet sheet = workbook.createSheet("填写说明");
        sheet.setColumnWidth(0, 28 * 256);
        sheet.setColumnWidth(1, 88 * 256);
        sheet.createFreezePane(0, 1);

        CellStyle titleStyle = createInstructionTitleStyle(workbook);
        CellStyle labelStyle = createInstructionLabelStyle(workbook);
        CellStyle contentStyle = createInstructionContentStyle(workbook);

        int rowNum = 0;
        Row titleRow = sheet.createRow(rowNum++);
        titleRow.setHeightInPoints(24);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellStyle(titleStyle);
        titleCell.setCellValue("问数语义模板填写说明");
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));

        rowNum = appendInstructionRow(sheet, rowNum, labelStyle, contentStyle,
                "当前模式",
                TEMPLATE_MODE_ENTERPRISE_EXPORT.equals(templateMode)
                        ? "当前导出模式为企业版模板导出。系统会结合当前库表结构、字段注释和已有口径自动预填语义字段。"
                        : "当前导出模式为空白模板。系统仅保留字段结构和填写说明，适合企业现场从零补录或二次整理。");
        rowNum = appendInstructionRow(sheet, rowNum, labelStyle, contentStyle,
                "当前主链路",
                TEMPLATE_MODE_ENTERPRISE_EXPORT.equals(templateMode)
                        ? "系统会结合当前数据库表结构自动预填第一张 sheet，用户只需确认或补充语义信息，然后上传 Excel 即可解析成知识库版本。"
                        : "当前导出为空白模板，第一张 sheet 仅保留企业版字段结构与填写说明，适合从零补录或整理后再上传。");
        rowNum = appendInstructionRow(sheet, rowNum, labelStyle, contentStyle,
                "企业版辅助 Sheet",
                "业务对象、表级语义、关联路径、指标口径、问句样例、质量报告用于企业现场复核和后续发布门禁；当前上传解析仍以第一张“字段语义模板”为准。");
        rowNum = appendInstructionRow(sheet, rowNum, labelStyle, contentStyle,
                "填写要点",
                "语义类型、来源类型、来源编码统一填英文代码，不填中文，系统已提供列表头说明和下拉候选值。");
        rowNum = appendInstructionRow(sheet, rowNum, labelStyle, contentStyle,
                "语义类型",
                "RELATION_KEY=关系键；ENUM=状态/类型/启停等枚举字段；DIMENSION=普通业务维度字段。");
        rowNum = appendInstructionRow(sheet, rowNum, labelStyle, contentStyle,
                "来源类型",
                "MANUAL=人工补充；DICT=系统字典；ENUM=Java 枚举；AUTO_COMMENT=字段注释自动识别。");
        rowNum = appendInstructionRow(sheet, rowNum, labelStyle, contentStyle,
                "来源编码",
                "DICT 填字典类型；ENUM 填枚举类全限定名；MANUAL 或 AUTO_COMMENT 一般留空。");
        rowNum = appendInstructionRow(sheet, rowNum, labelStyle, contentStyle,
                "关联提示",
                TEMPLATE_MODE_ENTERPRISE_EXPORT.equals(templateMode)
                        ? "系统会自动预填部分关联表达式，如 iot_device.product_id=iot_product.product_id。如需补充，请用分号分隔多条提示。"
                        : "可按 iot_device.product_id=iot_product.product_id 这类格式人工补充；如存在多条关系提示，使用分号分隔。");
        rowNum = appendInstructionRow(sheet, rowNum, labelStyle, contentStyle,
                "值映射",
                "推荐格式：在线=1;离线=0;启用=1;停用=0。如字段已接入字典，优先在“来源类型/来源编码”内填写字典信息。");
        rowNum = appendInstructionRow(sheet, rowNum, labelStyle, contentStyle,
                "用户不用维护的内容",
                "不需要在 Excel 里维护动态物模型 identifier（例如 temperature/power），这些由系统在运行时自动解析。");
        rowNum = appendInstructionRow(sheet, rowNum, labelStyle, contentStyle,
                "执行路由",
                "Excel 不再维护 dataSourceType。问数运行时走 Redis、TSDB 还是关系库，由系统根据语义和运行环境自动决定。");
        appendInstructionRow(sheet, rowNum, labelStyle, contentStyle,
                "当前结果",
                TEMPLATE_MODE_ENTERPRISE_EXPORT.equals(templateMode)
                        ? "本次已预填问数语义条目数：" + rowCount + "。"
                        : "当前为空白问数语义模板，可用于人工补录或企业现场初始化整理。");
    }

    /**
     * 追加企业版治理 Sheet。
     *
     * <p>上传解析仍只读取第一张“字段语义模板”，这些 Sheet 先用于企业现场复核、
     * 业务口径治理和后续发布前质量门禁。</p>
     */
    private void appendNl2SqlEnterpriseSheets(Workbook workbook, String templateMode,
                                              List<AiNl2SqlSemanticTemplateRow> rows) {
        List<AiNl2SqlSemanticTemplateRow> actualRows = rows == null ? Collections.emptyList() : rows;
        appendSimpleSheet(workbook, "业务对象",
                List.of("业务对象编码", "业务对象名称", "同义词", "主事实表", "默认统计口径", "默认数据源", "澄清规则", "备注"),
                buildBusinessObjectSheetRows(actualRows));
        appendSimpleSheet(workbook, "表级语义",
                List.of("表名", "表说明", "表角色", "主键字段", "业务对象", "适用场景", "禁止场景", "默认过滤条件", "备注"),
                buildTableSemanticSheetRows(actualRows));
        appendSimpleSheet(workbook, "关联路径",
                List.of("关联编码", "起始表", "起始字段", "目标表", "目标字段", "适用问法", "禁止问法", "备注"),
                buildRelationPathSheetRows(actualRows));
        appendSimpleSheet(workbook, "指标口径",
                List.of("指标口径编码", "指标口径名称", "业务对象", "主事实表", "聚合方式", "去重字段",
                        "状态/字典口径", "时间口径", "默认数据源", "适用问法", "备注"),
                buildMetricRuleSheetRows(actualRows));
        appendSimpleSheet(workbook, "问句样例",
                List.of("问题", "期望业务对象", "期望主表/数据源", "允许表", "禁止表", "期望聚合/动作", "是否允许澄清", "风险等级", "备注"),
                buildQuestionExampleSheetRows(actualRows));
        appendSimpleSheet(workbook, "质量报告",
                List.of("检查项", "级别", "结果", "建议处理"),
                buildNl2SqlQualityReportRows(templateMode, actualRows));
    }

    private void appendSimpleSheet(Workbook workbook, String sheetName, List<String> headers, List<List<String>> rows) {
        Sheet sheet = workbook.createSheet(sheetName);
        sheet.createFreezePane(0, 1);
        CellStyle headerStyle = createInstructionLabelStyle(workbook);
        CellStyle contentStyle = createInstructionContentStyle(workbook);
        Row headerRow = sheet.createRow(0);
        for (int index = 0; index < headers.size(); index++) {
            Cell cell = headerRow.createCell(index);
            cell.setCellStyle(headerStyle);
            cell.setCellValue(headers.get(index));
            sheet.setColumnWidth(index, Math.min(60, Math.max(16, headers.get(index).length() + 12)) * 256);
        }
        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            Row row = sheet.createRow(rowIndex + 1);
            List<String> values = rows.get(rowIndex);
            for (int columnIndex = 0; columnIndex < headers.size(); columnIndex++) {
                Cell cell = row.createCell(columnIndex);
                cell.setCellStyle(contentStyle);
                cell.setCellValue(columnIndex < values.size() ? trimToEmpty(values.get(columnIndex)) : "");
            }
        }
    }

    private List<List<String>> buildBusinessObjectSheetRows(List<AiNl2SqlSemanticTemplateRow> rows) {
        LinkedHashMap<String, String> tableComments = collectTableComments(rows);
        List<List<String>> result = new ArrayList<>();
        Set<String> businessObjectCodes = new LinkedHashSet<>();
        for (Map.Entry<String, String> entry : tableComments.entrySet()) {
            String tableName = entry.getKey();
            String tableComment = entry.getValue();
            if (!isMainFactTable(tableName)) {
                continue;
            }
            String objectName = resolveBusinessObjectName(tableName, tableComment);
            String objectCode = resolveBusinessObjectCode(tableName);
            result.add(List.of(
                    objectCode,
                    objectName,
                    resolveBusinessObjectAliases(tableName, objectName),
                    tableName,
                    resolveDefaultMetricRule(tableName, objectName),
                    resolveDefaultDataSource(tableName),
                    resolveBusinessClarifyRule(tableName, objectName),
                    "系统根据表名、表说明和内置业务对象规则预填，发布前建议结合企业二开口径复核"
            ));
            businessObjectCodes.add(objectCode);
        }
        appendDerivedBusinessObjects(tableComments, result, businessObjectCodes);
        return result;
    }

    private List<List<String>> buildTableSemanticSheetRows(List<AiNl2SqlSemanticTemplateRow> rows) {
        LinkedHashMap<String, String> tableComments = collectTableComments(rows);
        LinkedHashMap<String, String> primaryKeys = collectPrimaryKeyCandidates(rows);
        List<List<String>> result = new ArrayList<>();
        for (Map.Entry<String, String> entry : tableComments.entrySet()) {
            String tableName = entry.getKey();
            String tableComment = entry.getValue();
            String tableRole = resolveTableRole(tableName);
            String objectName = resolveBusinessObjectName(tableName, tableComment);
            result.add(List.of(
                    tableName,
                    tableComment,
                    tableRole,
                    primaryKeys.getOrDefault(tableName, ""),
                    objectName,
                    resolveTableApplicableScene(tableName, tableRole, objectName),
                    resolveTableForbiddenScene(tableName, tableRole, objectName),
                    resolveDefaultFilterHint(tableName),
                    "表级语义用于约束模型选表，避免把关联表、日志表或明细表误当主事实表"
            ));
        }
        return result;
    }

    private List<List<String>> buildRelationPathSheetRows(List<AiNl2SqlSemanticTemplateRow> rows) {
        LinkedHashSet<String> uniqueRelations = new LinkedHashSet<>();
        List<List<String>> result = new ArrayList<>();
        for (AiNl2SqlSemanticTemplateRow row : rows) {
            if (row == null || StringUtils.isBlank(row.getRelationHints())) {
                continue;
            }
            for (String hint : row.getRelationHints().split("[;；\\r\\n]+")) {
                String actualHint = trimToEmpty(hint);
                if (StringUtils.isBlank(actualHint) || !actualHint.contains("=")) {
                    continue;
                }
                String[] pair = actualHint.split("=", 2);
                String left = trimToEmpty(pair[0]);
                String right = trimToEmpty(pair[1]);
                if (!left.contains(".") || !right.contains(".")) {
                    continue;
                }
                String relationKey = left + "=" + right;
                if (!uniqueRelations.add(relationKey)) {
                    continue;
                }
                String[] leftParts = left.split("\\.", 2);
                String[] rightParts = right.split("\\.", 2);
                result.add(List.of(
                        "REL_" + uniqueRelations.size(),
                        leftParts[0],
                        leftParts[1],
                        rightParts[0],
                        rightParts[1],
                        resolveRelationApplicableScene(leftParts[0], leftParts[1], rightParts[0], rightParts[1]),
                        resolveRelationForbiddenScene(leftParts[0], leftParts[1], rightParts[0], rightParts[1]),
                        "系统从字段关联提示自动提取，复杂多跳关联发布前建议人工复核"
                ));
            }
        }
        return result;
    }

    private List<List<String>> buildMetricRuleSheetRows(List<AiNl2SqlSemanticTemplateRow> rows) {
        LinkedHashMap<String, String> tableComments = collectTableComments(rows);
        List<List<String>> result = new ArrayList<>();
        Set<String> metricRuleCodes = new LinkedHashSet<>();
        for (Map.Entry<String, String> entry : tableComments.entrySet()) {
            String tableName = entry.getKey();
            String tableComment = entry.getValue();
            if (!isMainFactTable(tableName)) {
                continue;
            }
            String objectCode = resolveBusinessObjectCode(tableName);
            String objectName = resolveBusinessObjectName(tableName, tableComment);
            String metricRuleCode = objectCode + "_COUNT";
            result.add(List.of(
                    metricRuleCode,
                    objectName + "总量",
                    objectName,
                    tableName,
                    "COUNT(*)",
                    "",
                    resolveMetricStateRule(tableName),
                    resolveMetricTimeRule(tableName),
                    resolveDefaultDataSource(tableName),
                    "统计" + objectName + "数量；" + objectName + "总数",
                    "系统预填基础总量口径，复杂去重、状态或跨表指标建议企业按实际业务补充"
            ));
            metricRuleCodes.add(metricRuleCode);
        }
        appendDerivedMetricRules(tableComments, result, metricRuleCodes);
        return result;
    }

    private void appendDerivedBusinessObjects(LinkedHashMap<String, String> tableComments,
                                              List<List<String>> rows,
                                              Set<String> businessObjectCodes) {
        if (tableComments.containsKey("iot_product") && tableComments.containsKey("iot_device")) {
            addBusinessObjectRow(rows, businessObjectCodes,
                    "PRODUCT_DEVICE_RELATION",
                    "产品 + 设备",
                    "有设备的产品;接入设备的产品;产品下设备;每个产品下设备数量",
                    "iot_device",
                    "统计有设备的产品数量时允许使用 COUNT(DISTINCT product_id) FROM iot_device；统计每个产品下设备数量时按 product_id 等稳定唯一键分组，并同时返回产品名称",
                    "RDB_SQL",
                    "需区分产品总数、接入设备的产品数和每个产品下设备数，无法判断时先澄清",
                    "系统补充的跨业务对象口径，用于承接产品与设备组合问法");
        }
        if (tableComments.containsKey("iot_device") && tableComments.containsKey("iot_things_model")) {
            addBusinessObjectRow(rows, businessObjectCodes,
                    "DEVICE_RUNTIME_METRIC",
                    "设备运行时指标",
                    "设备实时指标;设备当前值;设备实时值;当前温度;当前电量;当前湿度",
                    "iot_things_model",
                    "当前/实时指标先定位设备与物模型，再优先走 Redis 或 Hybrid，不直接生成关系库物理字段",
                    "REDIS_VALUE / HYBRID_PIPELINE",
                    "需先确认设备、物模型和时间语义，无法唯一定位时先澄清",
                    "系统补充的运行时业务对象，用于承接当前值、实时值类问句");
            addBusinessObjectRow(rows, businessObjectCodes,
                    "DEVICE_HISTORY_METRIC",
                    "设备历史指标",
                    "设备历史值;设备趋势;设备总计;当天电量;历史温度",
                    "iot_things_model",
                    "历史/趋势/总计指标先定位设备与物模型，再优先走 TSDB 或 Hybrid，不直接生成关系库物理字段",
                    "TSDB_QUERY / HYBRID_PIPELINE",
                    "需先确认设备、物模型、时间窗口和是否启用 TSDB，无法判断时先澄清",
                    "系统补充的运行时业务对象，用于承接历史值、趋势和总计类问句");
        }
    }

    private void addBusinessObjectRow(List<List<String>> rows,
                                      Set<String> businessObjectCodes,
                                      String businessObjectCode,
                                      String businessObjectName,
                                      String aliases,
                                      String primaryTable,
                                      String defaultMetricRule,
                                      String defaultDataSource,
                                      String clarifyRule,
                                      String remark) {
        if (businessObjectCodes == null || rows == null || StringUtils.isBlank(businessObjectCode)
                || !businessObjectCodes.add(businessObjectCode)) {
            return;
        }
        rows.add(List.of(
                businessObjectCode,
                businessObjectName,
                aliases,
                primaryTable,
                defaultMetricRule,
                defaultDataSource,
                clarifyRule,
                remark
        ));
    }

    private void appendDerivedMetricRules(LinkedHashMap<String, String> tableComments,
                                          List<List<String>> rows,
                                          Set<String> metricRuleCodes) {
        if (tableComments.containsKey("iot_product") && tableComments.containsKey("iot_device")) {
            addMetricRuleRow(rows, metricRuleCodes,
                    "PRODUCT_WITH_DEVICE_COUNT",
                    "有设备的产品数量",
                    "产品 + 设备",
                    "iot_device",
                    "COUNT(DISTINCT product_id)",
                    "product_id",
                    "产品状态类条件仍需结合字段语义中的字典、枚举和值映射治理",
                    "默认按当前设备关系统计，必要时叠加时间或状态过滤",
                    "RDB_SQL",
                    "统计有设备的产品数量；接入设备的产品数",
                    "只有问句明确要求“有设备的产品”时才允许使用该去重口径");
            addMetricRuleRow(rows, metricRuleCodes,
                    "PRODUCT_ONLINE_DEVICE_COUNT",
                    "每个产品在线设备数量",
                    "产品 + 设备",
                    "iot_device",
                    "GROUP BY product_id + product_name + COUNT(*)",
                    "",
                    "在线、离线等设备状态需结合字段语义中的值映射、字典或枚举口径",
                    "默认按当前设备主数据统计，可结合产品维度分组",
                    "RDB_SQL",
                    "统计每个产品下在线设备数量；按产品统计在线设备",
                    "只有问句明确要求产品维度分组时才使用该口径");
        }
        if (tableComments.containsKey("iot_device") && tableComments.containsKey("iot_things_model")) {
            addMetricRuleRow(rows, metricRuleCodes,
                    "DEVICE_RUNTIME_VALUE",
                    "设备运行时实时值",
                    "设备运行时指标",
                    "iot_things_model",
                    "实时值读取",
                    "",
                    "状态类运行时指标需结合物模型值映射、字典或枚举语义解释",
                    "当前/实时/现在/此刻类问法优先走 Redis 或 Hybrid",
                    "REDIS_VALUE / HYBRID_PIPELINE",
                    "查询某设备当前温度；查询某设备实时电量",
                    "系统补充的运行时指标口径，不要求用户维护动态 identifier");
            addMetricRuleRow(rows, metricRuleCodes,
                    "DEVICE_HISTORY_AGGREGATION",
                    "设备运行时历史聚合",
                    "设备历史指标",
                    "iot_things_model",
                    "时间窗口聚合",
                    "",
                    "状态类历史指标需结合物模型值映射、字典或枚举语义解释",
                    "历史/趋势/总计/当天类问法优先走 TSDB 或 Hybrid",
                    "TSDB_QUERY / HYBRID_PIPELINE",
                    "查询某设备当天电量总计；查询某设备最近24小时温度趋势",
                    "系统补充的历史指标口径，优先承接时序或混合执行计划");
        }
        if (tableComments.containsKey("iot_device")) {
            addMetricRuleRow(rows, metricRuleCodes,
                    "DEVICE_ONLINE_RATIO",
                    "在线设备占比",
                    "设备",
                    "iot_device",
                    "COUNT_IF(status=在线)/COUNT(*)",
                    "",
                    "在线、离线等设备状态需结合字段语义中的值映射、字典或枚举口径",
                    "按当前设备主数据统计在线数量与设备总量的比例",
                    "RDB_SQL",
                    "统计在线设备占比；在线设备比例是多少",
                    "占比类问法必须同时明确分子状态口径和分母总量口径");
        }
        if (tableComments.containsKey("iot_alert_log")) {
            addMetricRuleRow(rows, metricRuleCodes,
                    "ALERT_LOG_TREND",
                    "告警记录趋势",
                    "告警记录",
                    "iot_alert_log",
                    "COUNT(*) + GROUP BY 日期",
                    "",
                    "未处理、待处理、已处理等告警处理状态需结合告警记录表状态字段语义解释，其中待处理告警在告警语境下兼容未处理口径",
                    "近7天、近30天、按天、按小时等趋势类问法按告警记录时间字段聚合",
                    "RDB_SQL",
                    "统计近7天告警趋势；按天统计告警记录数量；告警趋势",
                    "趋势类问法必须使用告警记录事实表，并声明时间窗口与分组口径");
            addMetricRuleRow(rows, metricRuleCodes,
                    "ALERT_LOG_PENDING_COUNT",
                    "待处理告警数量",
                    "告警记录",
                    "iot_alert_log",
                    "COUNT(*) + status=未处理",
                    "",
                    "待处理告警、未处理告警、未处置告警在告警记录语境下统一解释为 iot_alert_log.status=未处理",
                    "默认按当前告警记录处理状态统计",
                    "RDB_SQL",
                    "查询待处理告警数量；统计未处理告警数量；待处置告警有多少",
                    "必须先命中告警记录事实表，再解释处理状态；不能因待处理命中工单状态而改查工单表");
        }
    }

    private void addMetricRuleRow(List<List<String>> rows,
                                  Set<String> metricRuleCodes,
                                  String metricRuleCode,
                                  String metricRuleName,
                                  String businessObjectName,
                                  String primaryTable,
                                  String aggregationType,
                                  String distinctColumn,
                                  String stateRule,
                                  String timeRule,
                                  String defaultDataSource,
                                  String applicableQuestion,
                                  String remark) {
        if (metricRuleCodes == null || rows == null || StringUtils.isBlank(metricRuleCode)
                || !metricRuleCodes.add(metricRuleCode)) {
            return;
        }
        rows.add(List.of(
                metricRuleCode,
                metricRuleName,
                businessObjectName,
                primaryTable,
                aggregationType,
                distinctColumn,
                stateRule,
                timeRule,
                defaultDataSource,
                applicableQuestion,
                remark
        ));
    }

    private List<List<String>> buildQuestionExampleSheetRows(List<AiNl2SqlSemanticTemplateRow> semanticRows) {
        List<List<String>> rows = new ArrayList<>();
        addQuestionExampleRow(rows, "统计产品数量", "产品", "iot_product", "iot_product",
                "仅使用 iot_device 去重 product_id", "COUNT(*)", "否", "P0", "产品总数必须优先使用产品主表");
        addQuestionExampleRow(rows, "统计已发布产品数量", "产品", "iot_product", "iot_product",
                "仅使用 iot_device 去重 product_id", "COUNT(*) + 状态字典映射", "否", "P0",
                "产品状态类统计必须落在产品主表，并结合产品状态字典或值映射");
        addQuestionExampleRow(rows, "统计设备数量", "设备", "iot_device", "iot_device",
                "仅使用 iot_device_log / iot_device_user 去重 device_id", "COUNT(*)", "否", "P0",
                "设备总数必须优先使用设备主表");
        addQuestionExampleRow(rows, "统计在线设备数量", "设备", "iot_device", "iot_device",
                "未按设备状态字典转换的 SQL", "COUNT(*) + 状态字典映射", "否", "P0",
                "在线、离线等自然语言状态必须按字典或值映射转换");
        addQuestionExampleRow(rows, "统计有设备的产品数量", "产品 + 设备", "iot_device", "iot_device",
                "iot_product COUNT(*)", "COUNT(DISTINCT product_id)", "否", "P0",
                "只有明确问“有设备的产品”才允许设备表 product_id 去重");
        addQuestionExampleRow(rows, "统计每个产品下设备数量", "产品 + 设备", "iot_product + iot_device", "iot_product;iot_device",
                "", "GROUP BY 产品ID + 产品名称", "否", "P1", "该问法允许使用设备表按稳定产品ID分组并关联产品表返回名称，避免同名产品被合并");
        addQuestionExampleRow(rows, "查询某设备当前温度是多少", "设备运行时指标", "REDIS_VALUE / HYBRID_PIPELINE",
                "设备元数据;产品物模型;Redis", "SELECT temperature FROM iot_device", "实时值读取", "是", "P0",
                "当前/实时指标不应误走关系库物理字段");
        addQuestionExampleRow(rows, "查询某设备实时电量是多少", "设备运行时指标", "REDIS_VALUE / HYBRID_PIPELINE",
                "设备元数据;产品物模型;Redis", "SELECT power FROM iot_device", "实时值读取", "是", "P0",
                "实时类运行时指标应先定位设备和物模型，再走 Redis 或 Hybrid");
        addQuestionExampleRow(rows, "查询某设备当天电量总计", "设备历史指标", "TSDB_QUERY / HYBRID_PIPELINE",
                "设备元数据;产品物模型;TSDB/关系库", "仅查 iot_device", "时间窗口聚合", "是", "P0",
                "历史/总计类运行时指标需结合 TSDB 启用情况路由");
        addQuestionExampleRow(rows, "查询某设备最近24小时温度趋势", "设备历史指标", "TSDB_QUERY / HYBRID_PIPELINE",
                "设备元数据;产品物模型;TSDB/关系库", "仅查 iot_device", "时间窗口聚合", "是", "P0",
                "趋势类问句需声明时间窗口口径，并优先走 TSDB 或 Hybrid");
        addQuestionExampleRow(rows, "统计未处理告警数量", "告警记录", "iot_alert_log", "iot_alert_log",
                "iot_alert COUNT(*)", "COUNT(*) + 处理状态映射", "是", "P0",
                "未处理告警数量必须落在告警记录表，并结合处理状态字典或值映射");
        addQuestionExampleRow(rows, "查询待处理告警数量", "告警记录", "iot_alert_log", "iot_alert_log",
                "iot_work_order COUNT(*) 或 iot_alert COUNT(*)", "COUNT(*) + iot_alert_log.status=未处理", "是", "P0",
                "待处理告警应先命中告警记录表，再在告警处理状态语境下兼容未处理口径，不能误查工单表");
        addQuestionExampleRow(rows, "统计告警规则数量", "告警规则", "iot_alert", "iot_alert",
                "iot_alert_log COUNT(*)", "COUNT(*)", "否", "P1",
                "告警规则数量和告警记录数量必须区分，不能互相替代");
        addQuestionExampleRow(rows, "统计每个产品下在线设备数量", "产品 + 设备", "iot_product + iot_device", "iot_product;iot_device",
                "只查产品主数据或未按设备状态映射过滤", "GROUP BY 产品ID + 产品名称 + 状态字典映射", "是", "P0",
                "企业常见分组问法，必须同时识别产品稳定ID、展示名称、设备事实表和在线状态口径，避免同名产品被合并");
        addQuestionExampleRow(rows, "统计在线设备占比", "设备", "iot_device", "iot_device",
                "只返回在线数量或未计算设备总量分母", "COUNT_IF(status=在线)/COUNT(*)", "否", "P0",
                "占比类问法必须声明分子状态口径和分母总量口径，避免只做单边统计");
        addQuestionExampleRow(rows, "统计近7天告警趋势", "告警记录", "iot_alert_log", "iot_alert_log",
                "iot_alert COUNT(*) 或不带时间窗口", "时间窗口聚合 + GROUP BY 日期", "是", "P0",
                "趋势类问法必须落在记录事实表，并按时间字段聚合");
        addQuestionExampleRow(rows, "统计待处理工单数量", "工单", "iot_work_order", "iot_work_order",
                "未按工单状态字典转换的 SQL", "COUNT(*) + 状态字典映射", "否", "P1",
                "工单状态口径通常由企业二开字典决定，建议上线前补齐");
        addQuestionExampleRow(rows, "统计启用的 AI 模型数量", "AI 模型", "ai_model", "ai_model",
                "未按模型状态字典转换的 SQL", "COUNT(*) + 状态字典映射", "否", "P1",
                "AI 治理类配置表同样需要状态字典口径，避免启停统计错误");
        addQuestionExampleRow(rows, "统计知识库数量", "知识库", "ai_knowledge_base", "ai_knowledge_base",
                "ai_knowledge_document COUNT(*)", "COUNT(*)", "否", "P1",
                "知识库主对象数量不能被文档数量、版本数量替代");
        return rows;
    }

    private void addQuestionExampleRow(List<List<String>> rows,
                                       String question,
                                       String expectedBusinessObject,
                                       String expectedSource,
                                       String allowedTables,
                                       String forbiddenTables,
                                       String expectedAction,
                                       String allowClarify,
                                       String riskLevel,
                                       String remark) {
        rows.add(List.of(question,
                expectedBusinessObject,
                expectedSource,
                allowedTables,
                forbiddenTables,
                expectedAction,
                allowClarify,
                riskLevel,
                remark));
    }

    private List<List<String>> buildNl2SqlQualityReportRows(String templateMode, List<AiNl2SqlSemanticTemplateRow> rows) {
        int fieldCount = rows.size();
        int tableCount = collectTableComments(rows).size();
        int relationCount = buildRelationPathSheetRows(rows).size();
        int metricRuleCount = buildMetricRuleSheetRows(rows).size();
        List<List<String>> questionExamples = buildQuestionExampleSheetRows(rows);
        int goldenQuestionCount = questionExamples.size();
        int goldenQuestionP0Count = 0;
        for (List<String> row : questionExamples) {
            if (row != null && row.size() > 7 && "P0".equalsIgnoreCase(trimToEmpty(row.get(7)))) {
                goldenQuestionP0Count++;
            }
        }
        List<List<String>> result = new ArrayList<>();
        result.add(List.of("模板模式", "INFO", TEMPLATE_MODE_ENTERPRISE_EXPORT.equals(templateMode) ? "企业版自动预填" : "空白模板", "企业版模板建议先下载预填版本，再人工复核发布"));
        result.add(List.of("字段语义条目", "INFO", String.valueOf(fieldCount), "第一张字段语义模板仍是当前上传解析入口"));
        result.add(List.of("表级语义条目", "INFO", String.valueOf(tableCount), "表级语义用于后续主事实表和表角色守卫"));
        result.add(List.of("关联路径条目", "INFO", String.valueOf(relationCount), "关联路径来自字段关联提示，复杂路径发布前建议复核"));
        result.add(List.of("指标口径条目", "INFO", String.valueOf(metricRuleCount), "指标口径用于补充默认聚合、去重、状态和时间治理提示"));
        result.add(List.of("默认黄金问句", "INFO",
                "共 " + goldenQuestionCount + " 条，P0 共 " + goldenQuestionP0Count + " 条",
                "默认已覆盖主事实表、状态字典、去重统计、分组统计、占比统计、Redis 当前值、TSDB/Hybrid 历史聚合、告警规则/告警记录区分和 AI 治理配置表"));
        result.add(List.of("业务语义守卫", "INFO", "已接入生成后守卫首版", "生成 SQL 后会先经过业务语义守卫，再进入执行链路"));
        result.add(List.of("黄金问句门禁", "INFO", "已接入发布前静态门禁首版", "P0 问句样例会在版本发布前校验主表、运行时数据源和字典映射准备情况"));
        result.add(List.of("回归建议", "INFO", "当前模板已具备直接回归基础", "建议优先保留默认“问句样例”，仅按企业命名补充设备、产品、字典和指标口径后，即可上传、构建草稿版本并执行发布门禁回归"));
        return result;
    }

    private LinkedHashMap<String, String> collectTableComments(List<AiNl2SqlSemanticTemplateRow> rows) {
        LinkedHashMap<String, String> tableComments = new LinkedHashMap<>();
        for (AiNl2SqlSemanticTemplateRow row : rows) {
            if (row == null || StringUtils.isBlank(row.getTableName())) {
                continue;
            }
            tableComments.putIfAbsent(normalizeTableName(row.getTableName()), trimToEmpty(row.getTableComment()));
        }
        return tableComments;
    }

    private LinkedHashMap<String, String> collectPrimaryKeyCandidates(List<AiNl2SqlSemanticTemplateRow> rows) {
        LinkedHashMap<String, String> primaryKeys = new LinkedHashMap<>();
        for (AiNl2SqlSemanticTemplateRow row : rows) {
            if (row == null || StringUtils.isBlank(row.getTableName()) || StringUtils.isBlank(row.getColumnName())) {
                continue;
            }
            String tableName = normalizeTableName(row.getTableName());
            String columnName = normalizeColumnName(row.getColumnName());
            String expectedPk = resolveExpectedPrimaryKey(tableName);
            if (StringUtils.equals(columnName, expectedPk)) {
                primaryKeys.putIfAbsent(tableName, columnName);
                continue;
            }
            if (!primaryKeys.containsKey(tableName) && columnName.endsWith("_id")
                    && StringUtils.equalsIgnoreCase(row.getSemanticType(), SEMANTIC_RELATION_KEY)) {
                primaryKeys.put(tableName, columnName);
            }
        }
        return primaryKeys;
    }

    private boolean isMainFactTable(String tableName) {
        String actualTable = normalizeTableName(tableName);
        return Set.of("iot_product", "iot_device", "iot_things_model", "iot_alert", "iot_alert_log", "iot_work_order",
                "sys_user", "sys_role", "sys_dept", "iot_scene", "iot_firmware", "ai_provider",
                "ai_model", "ai_chat_session", "ai_knowledge_base").contains(actualTable)
                || "主表".equals(resolveTableRole(actualTable));
    }

    private String resolveBusinessObjectCode(String tableName) {
        return switch (normalizeTableName(tableName)) {
            case "iot_product" -> "PRODUCT";
            case "iot_device" -> "DEVICE";
            case "iot_things_model" -> "THING_MODEL";
            case "iot_alert" -> "ALERT_RULE";
            case "iot_alert_log" -> "ALERT_LOG";
            case "iot_work_order" -> "WORK_ORDER";
            case "sys_user" -> "SYS_USER";
            case "sys_role" -> "SYS_ROLE";
            case "sys_dept" -> "SYS_DEPT";
            case "iot_scene" -> "SCENE";
            case "iot_firmware" -> "FIRMWARE";
            case "ai_provider" -> "AI_PROVIDER";
            case "ai_model" -> "AI_MODEL";
            case "ai_chat_session" -> "AI_CHAT_SESSION";
            case "ai_knowledge_base" -> "AI_KNOWLEDGE_BASE";
            default -> normalizeTableName(tableName).toUpperCase(Locale.ROOT);
        };
    }

    private String resolveBusinessObjectName(String tableName, String tableComment) {
        String actualTable = normalizeTableName(tableName);
        return switch (actualTable) {
            case "iot_product" -> "产品";
            case "iot_device" -> "设备";
            case "iot_things_model" -> "物模型";
            case "iot_alert" -> "告警规则";
            case "iot_alert_log" -> "告警记录";
            case "iot_work_order" -> "工单";
            case "sys_user" -> "系统用户";
            case "sys_role" -> "角色";
            case "sys_dept" -> "机构";
            case "iot_scene" -> "场景";
            case "iot_firmware" -> "固件";
            case "ai_provider" -> "AI 厂商";
            case "ai_model" -> "AI 模型";
            case "ai_chat_session" -> "AI 会话";
            case "ai_knowledge_base" -> "知识库";
            default -> StringUtils.isNotBlank(tableComment) ? stripCommentDecorators(tableComment) : actualTable;
        };
    }

    private String resolveBusinessObjectAliases(String tableName, String objectName) {
        String actualTable = normalizeTableName(tableName);
        LinkedHashSet<String> aliases = new LinkedHashSet<>();
        addSemanticText(aliases, objectName);
        addSemanticText(aliases, stripBusinessObjectSuffix(objectName));
        switch (actualTable) {
            case "iot_product" -> addSemanticText(aliases, "产品;产品信息;产品列表;产品总数");
            case "iot_device" -> addSemanticText(aliases, "设备;设备信息;设备列表;设备总数;终端");
            case "iot_things_model" -> addSemanticText(aliases, "物模型;指标;属性;事件;功能");
            case "iot_alert" -> addSemanticText(aliases, "告警规则;报警规则;预警规则");
            case "iot_alert_log" -> addSemanticText(aliases, "告警记录;报警记录;告警日志;未处理告警;待处理告警;未处置告警;待处置告警");
            case "iot_work_order" -> addSemanticText(aliases, "工单;运维工单;维修工单");
            case "sys_user" -> addSemanticText(aliases, "用户;系统用户;账号;账户");
            default -> addSemanticText(aliases, actualTable);
        }
        return String.join(";", aliases);
    }

    private String resolveDefaultMetricRule(String tableName, String objectName) {
        String actualTable = normalizeTableName(tableName);
        if ("iot_product".equals(actualTable)) {
            return "统计产品数量时默认使用 COUNT(*) FROM iot_product";
        }
        if ("iot_device".equals(actualTable)) {
            return "统计设备数量时默认使用 COUNT(*) FROM iot_device";
        }
        if ("sys_user".equals(actualTable)) {
            return "统计系统用户数量时默认使用 COUNT(*) FROM sys_user，业务关联表 user_id 去重不能替代用户总数";
        }
        if ("iot_alert".equals(actualTable)) {
            return "统计告警规则数量时使用告警规则主表；统计告警记录需改用告警记录表";
        }
        if ("iot_alert_log".equals(actualTable)) {
            return "统计告警记录、未处理告警、待处理告警和告警趋势时使用告警记录表，先命中告警记录事实表，再按处理状态字段解释状态，不能用告警规则表或工单表替代";
        }
        if ("iot_work_order".equals(actualTable)) {
            return "统计工单数量时使用工单主表，待处理、已完成等状态需结合工单状态字典";
        }
        if ("ai_model".equals(actualTable)) {
            return "统计 AI 模型数量时使用模型主表，启用、停用等状态需结合 AI 模型状态字典";
        }
        if ("ai_knowledge_base".equals(actualTable)) {
            return "统计知识库数量时使用知识库主表，不能用知识文档或版本数量替代";
        }
        return "统计" + objectName + "数量时优先使用主事实表 COUNT(*)，除非问句明确要求关联对象去重或明细数量";
    }

    private String resolveDefaultDataSource(String tableName) {
        String actualTable = normalizeTableName(tableName);
        if ("iot_things_model".equals(actualTable)) {
            return "RDB_SQL + RUNTIME_PROVIDER";
        }
        if (actualTable.contains("_log") || actualTable.endsWith("_record")) {
            return "RDB_SQL / TSDB_QUERY";
        }
        return "RDB_SQL";
    }

    private String resolveMetricStateRule(String tableName) {
        String actualTable = normalizeTableName(tableName);
        if ("iot_device".equals(actualTable)) {
            return "在线、离线等状态需结合字段语义中的值映射、字典或枚举口径";
        }
        if ("iot_alert".equals(actualTable) || "iot_alert_log".equals(actualTable)) {
            return "未处理、待处理、已处理等状态需先明确所属业务对象，再在对应事实表状态字段内解释，不能默认按规则表或工单表总数替代";
        }
        if ("iot_work_order".equals(actualTable)) {
            return "待处理、处理中、已完成等工单状态需结合字段语义中的字典、枚举或值映射";
        }
        if ("ai_model".equals(actualTable) || "ai_knowledge_base".equals(actualTable)) {
            return "启用、停用、发布、未发布等治理状态需结合字段语义中的字典、枚举或值映射";
        }
        return "状态、类型、启停类指标需结合字段语义中的字典、枚举和值映射治理";
    }

    private String resolveMetricTimeRule(String tableName) {
        String actualTable = normalizeTableName(tableName);
        if ("iot_things_model".equals(actualTable)) {
            return "当前/实时优先走 Redis，历史/趋势/总计优先走 TSDB 或 Hybrid，不直接臆造关系库字段";
        }
        if (actualTable.contains("_log") || actualTable.endsWith("_record")) {
            return "涉及时间窗口时优先按日志/记录时间字段聚合，避免拿主表当前值替代";
        }
        return "数量类默认按当前主数据口径统计；涉及今天、昨天、最近等时间条件时需结合业务时间字段明确过滤";
    }

    private String resolveBusinessClarifyRule(String tableName, String objectName) {
        String actualTable = normalizeTableName(tableName);
        if ("iot_alert".equals(actualTable)) {
            return "问句只说告警数量时需判断是告警规则、告警记录还是未处理/待处理告警，无法判断时应澄清";
        }
        if ("iot_alert_log".equals(actualTable)) {
            return "问句涉及告警趋势、未处理告警、待处理告警或告警记录时使用告警记录表；只问告警规则时改用告警规则表";
        }
        if ("sys_user".equals(actualTable)) {
            return "问句只说用户数量时需判断是系统用户、设备用户、租户用户还是客户账号";
        }
        if ("iot_things_model".equals(actualTable)) {
            return "问句涉及当前值、历史值、总计、趋势时应先定位设备和产品物模型，再路由 Redis/TSDB/关系库";
        }
        return "当问句中的“" + objectName + "”与其他业务对象存在歧义时，应先澄清，不直接强猜";
    }

    private String resolveTableRole(String tableName) {
        String actualTable = normalizeTableName(tableName);
        if (actualTable.contains("_log") || actualTable.endsWith("_log")) {
            return "日志表";
        }
        if (actualTable.endsWith("_record") || actualTable.endsWith("_records")) {
            return "快照表";
        }
        if (actualTable.endsWith("_detail") || actualTable.endsWith("_data") || actualTable.endsWith("_param_value")) {
            return "明细表";
        }
        if (actualTable.endsWith("_user") || actualTable.endsWith("_authorize") || actualTable.endsWith("_share")
                || actualTable.endsWith("_relation") || actualTable.contains("_sub_")) {
            return "关联表";
        }
        if (actualTable.contains("_runtime") || actualTable.contains("_cache")) {
            return "运行时表";
        }
        return "主表";
    }

    private String resolveExpectedPrimaryKey(String tableName) {
        String actualTable = normalizeTableName(tableName);
        if (actualTable.startsWith("iot_")) {
            String suffix = actualTable.substring("iot_".length());
            return suffix + "_id";
        }
        if (actualTable.startsWith("sys_")) {
            String suffix = actualTable.substring("sys_".length());
            return suffix + "_id";
        }
        if (actualTable.startsWith("ai_")) {
            String suffix = actualTable.substring("ai_".length());
            return suffix + "_id";
        }
        return "id";
    }

    private String resolveTableApplicableScene(String tableName, String tableRole, String objectName) {
        String actualTable = normalizeTableName(tableName);
        if ("主表".equals(tableRole)) {
            return objectName + "列表、" + objectName + "详情、" + objectName + "数量统计、按" + objectName + "维度筛选";
        }
        if ("日志表".equals(tableRole)) {
            return "历史记录、操作记录、上报记录、趋势分析、按时间统计";
        }
        if ("关联表".equals(tableRole)) {
            return "对象关系查询、按关联对象筛选、明确要求“拥有/关联/绑定/授权/分享”的问法";
        }
        if ("明细表".equals(tableRole)) {
            return "明细项、子项、配置项、扩展参数查询";
        }
        if ("快照表".equals(tableRole)) {
            return "快照、记录、归档、历史版本查询";
        }
        return "运行时或特殊场景查询";
    }

    private String resolveTableForbiddenScene(String tableName, String tableRole, String objectName) {
        if ("主表".equals(tableRole)) {
            return "";
        }
        if ("日志表".equals(tableRole)) {
            return "不能默认用日志表去重统计" + objectName + "总量";
        }
        if ("关联表".equals(tableRole)) {
            return "不能默认用关联字段去重替代主对象总量，除非问句明确要求“有某关联的对象数量”";
        }
        if ("明细表".equals(tableRole)) {
            return "不能默认用明细表条数替代主对象数量";
        }
        if ("快照表".equals(tableRole)) {
            return "不能默认用快照记录数替代当前主数据数量";
        }
        return "不能在未识别运行时事实源时直接生成关系库字段 SQL";
    }

    private String resolveDefaultFilterHint(String tableName) {
        String actualTable = normalizeTableName(tableName);
        if (actualTable.startsWith("iot_") || actualTable.startsWith("ai_")) {
            return "默认遵循租户数据范围、逻辑删除和启停状态治理";
        }
        if (actualTable.startsWith("sys_")) {
            return "默认遵循系统权限、租户/机构数据范围和逻辑删除治理";
        }
        return "默认遵循当前业务表的数据权限、状态和逻辑删除治理";
    }

    private String resolveRelationApplicableScene(String leftTable, String leftColumn, String rightTable, String rightColumn) {
        if ("product_id".equals(leftColumn) || "product_id".equals(rightColumn)) {
            return "按产品筛选设备、统计每个产品下设备数量、查询产品关联物模型";
        }
        if ("device_id".equals(leftColumn) || "device_id".equals(rightColumn)
                || "serial_number".equals(leftColumn) || "serial_number".equals(rightColumn)) {
            return "按设备筛选日志、记录、告警、工单或运行时指标";
        }
        if ("user_id".equals(leftColumn) || "user_id".equals(rightColumn)) {
            return "按用户筛选创建人、负责人、授权用户或操作记录";
        }
        return "明确需要跨表关联、分组或过滤时使用";
    }

    private String resolveRelationForbiddenScene(String leftTable, String leftColumn, String rightTable, String rightColumn) {
        if ("product_id".equals(leftColumn) || "product_id".equals(rightColumn)) {
            return "不能把设备表 product_id 去重默认当作产品总数";
        }
        if ("device_id".equals(leftColumn) || "device_id".equals(rightColumn)
                || "serial_number".equals(leftColumn) || "serial_number".equals(rightColumn)) {
            return "不能把日志或关联表中的设备字段去重默认当作设备总数";
        }
        if ("user_id".equals(leftColumn) || "user_id".equals(rightColumn)) {
            return "不能把业务表 user_id 去重默认当作系统用户总数";
        }
        return "不能在问句未要求关联口径时替代主事实表";
    }

    private void appendProtocolInstructionSheet(Workbook workbook, String templateMode) {
        workbook.setSheetName(0, "协议知识模板");
        Sheet sheet = workbook.createSheet("填写说明");
        sheet.setColumnWidth(0, 28 * 256);
        sheet.setColumnWidth(1, 88 * 256);
        sheet.createFreezePane(0, 1);

        CellStyle titleStyle = createInstructionTitleStyle(workbook);
        CellStyle labelStyle = createInstructionLabelStyle(workbook);
        CellStyle contentStyle = createInstructionContentStyle(workbook);

        int rowNum = 0;
        Row titleRow = sheet.createRow(rowNum++);
        titleRow.setHeightInPoints(24);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellStyle(titleStyle);
        titleCell.setCellValue("协议知识模板填写说明");
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));

        rowNum = appendInstructionRow(sheet, rowNum, labelStyle, contentStyle,
                "当前模式",
                TEMPLATE_MODE_ENTERPRISE_EXPORT.equals(templateMode)
                        ? "当前请求为企业版模板导出，但协议知识库尚未实现企业版自动预填，本次已自动降级为空白模板。"
                        : "当前导出模式为空白模板，适合整理协议字段、示例报文和值映射。");
        rowNum = appendInstructionRow(sheet, rowNum, labelStyle, contentStyle,
                "字段说明",
                "module_name=模块名称；field_code=字段编码；field_name=字段中文名称；data_type=字段类型；sample_value=示例值；value_mappings=值映射；aliases=同义词；remark=备注。");
        rowNum = appendInstructionRow(sheet, rowNum, labelStyle, contentStyle,
                "填写建议",
                "一行代表一个协议字段或协议知识单元。若存在枚举或状态值，请优先在 value_mappings 中按“开=1;关=0”格式填写。");
        appendInstructionRow(sheet, rowNum, labelStyle, contentStyle,
                "使用方式",
                "填写完成后可直接上传到 PROTOCOL_SPEC 知识库，后续再进入版本管理构建草稿并发布。");
    }

    /**
     * 企业版平台知识模板约定：
     * 第一张 sheet 为数据表，可直接上传；第二张 sheet 为填写说明。
     */
    private void appendPlatformDocInstructionSheet(Workbook workbook, String templateMode,
                                                   String sourceStrategy, int rowCount) {
        workbook.setSheetName(0, "平台知识模板");
        Sheet sheet = workbook.createSheet("填写说明");
        sheet.setColumnWidth(0, 28 * 256);
        sheet.setColumnWidth(1, 92 * 256);
        sheet.createFreezePane(0, 1);

        CellStyle titleStyle = createInstructionTitleStyle(workbook);
        CellStyle labelStyle = createInstructionLabelStyle(workbook);
        CellStyle contentStyle = createInstructionContentStyle(workbook);

        int rowNum = 0;
        Row titleRow = sheet.createRow(rowNum++);
        titleRow.setHeightInPoints(24);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellStyle(titleStyle);
        titleCell.setCellValue("平台知识企业版模板填写说明");
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));

        rowNum = appendInstructionRow(sheet, rowNum, labelStyle, contentStyle,
                "当前模式",
                TEMPLATE_MODE_ENTERPRISE_EXPORT.equals(templateMode)
                        ? "当前导出模式为企业版导出。系统会优先读取 FastBee-doc 本地源码目录，若目录不存在或结构不完整，则自动回退到官网公开文档。"
                        : "当前导出模式为空白模板。模板列头已升级为企业版结构，适合直接用于平台助手知识建设。");
        rowNum = appendInstructionRow(sheet, rowNum, labelStyle, contentStyle,
                "导出范围",
                "导出范围固定为除 src/internal 外的全部公开文档；若走官网 fallback，则等价为仅排除 internal 路径。");
        rowNum = appendInstructionRow(sheet, rowNum, labelStyle, contentStyle,
                "自动预填",
                "系统会尽量自动预填一级栏目、栏目路径、页面标题、标题路径、知识类型、菜单路径、前置条件、操作步骤、结果说明、注意事项、标签、同义问法、来源文件、来源链接等字段。");
        rowNum = appendInstructionRow(sheet, rowNum, labelStyle, contentStyle,
                "上传使用",
                "第一张 sheet 可直接上传到 PLATFORM_DOC 知识库；上传后仍需进入“版本管理”构建草稿并发布，平台助手才会消费已发布版本。");
        rowNum = appendInstructionRow(sheet, rowNum, labelStyle, contentStyle,
                "来源策略",
                "AUTO=本地源码优先，缺失时回退官网；LOCAL=只读取 FastBee-doc；WEB=只读取官网文档。当前策略为：" + actualOrDefault(sourceStrategy, SOURCE_STRATEGY_AUTO));
        rowNum = appendInstructionRow(sheet, rowNum, labelStyle, contentStyle,
                "知识类型",
                "OVERVIEW=概述；STEP=操作步骤；CONFIG=配置说明；NOTICE=注意事项；FAQ=常见问题；PERMISSION=权限口径；GUIDE=其他引导。");
        rowNum = appendInstructionRow(sheet, rowNum, labelStyle, contentStyle,
                "维护建议",
                "系统已尽量自动补全高价值字段，实际使用中优先做校对而不是重填。若个别菜单路径、角色口径或同义问法不够精准，可按企业现场实际情况微调。");
        appendInstructionRow(sheet, rowNum, labelStyle, contentStyle,
                "当前结果",
                TEMPLATE_MODE_ENTERPRISE_EXPORT.equals(templateMode)
                        ? "本次已预填企业版平台知识条目数：" + rowCount + "。"
                        : "当前为空白企业版模板，可用于人工补录或后续批量导入。");
    }

    private int appendInstructionRow(Sheet sheet, int rowNum, CellStyle labelStyle, CellStyle contentStyle,
                                     String label, String content) {
        Row row = sheet.createRow(rowNum++);
        row.setHeightInPoints(28);
        Cell labelCell = row.createCell(0);
        labelCell.setCellStyle(labelStyle);
        labelCell.setCellValue(label);
        Cell contentCell = row.createCell(1);
        contentCell.setCellStyle(contentStyle);
        contentCell.setCellValue(content);
        return rowNum;
    }

    private CellStyle createInstructionTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.BLUE_GREY.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        return style;
    }

    private CellStyle createInstructionLabelStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setWrapText(true);
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private CellStyle createInstructionContentStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);
        return style;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, noRollbackFor = ServiceException.class)
    public AiKnowledgeTemplateUploadResultVO uploadAndParseTemplate(Long knowledgeBaseId, String sourceOrigin,
                                                                    String appVersion, Integer sortNum,
                                                                    MultipartFile file) throws Exception {
        if (knowledgeBaseId == null) {
            throw new ServiceException(message("ai.knowledge.base.id.required"));
        }
        if (file == null || file.isEmpty()) {
            throw new ServiceException(message("ai.knowledge.template.file.required"));
        }
        validateExcelFile(file);

        AiKnowledgeBase knowledgeBase = requireKnowledgeBase(knowledgeBaseId);
        String actualKbType = resolveKnowledgeType(knowledgeBase.getKbType());
        if (KB_TYPE_CODEBASE_GUIDE.equals(actualKbType)) {
            throw new ServiceException(message("ai.knowledge.template.codebase.excel.upload.unsupported"));
        }
        String actualSourceOrigin = normalizeDocumentSourceOrigin(sourceOrigin);
        String actualAppVersion = normalizeDocumentAppVersion(appVersion);
        Integer actualSortNum = resolveDocumentSortNum(knowledgeBaseId, actualSourceOrigin, sortNum);
        byte[] fileBytes = file.getBytes();
        String originalFilename = StringUtils.isBlank(file.getOriginalFilename()) ? "knowledge-template.xlsx" : file.getOriginalFilename();
        String templateCode = resolveTemplateCode(actualKbType);
        String templateVersion = resolveTemplateVersion(actualKbType);
        String parserType = resolveParserType(actualKbType);
        Path uploadedFilePath = saveTemplateFile(knowledgeBase, actualKbType, originalFilename, fileBytes);

        AiKnowledgeDocument document = buildDocumentDraft(knowledgeBase, originalFilename, uploadedFilePath, fileBytes,
                templateCode, templateVersion, actualSourceOrigin, actualAppVersion, actualSortNum);
        aiKnowledgeDocumentService.save(document);
        String snapshotCode = buildDocumentSnapshotCode(document.getDocumentId());
        AiKnowledgeDocumentParseResultVO parseResult = parseAndPersistDocument(knowledgeBase, document, fileBytes,
                templateCode, templateVersion, parserType, snapshotCode);

        updateKnowledgeBaseMetadata(knowledgeBase);

        AiKnowledgeTemplateUploadResultVO result = new AiKnowledgeTemplateUploadResultVO();
        result.setKnowledgeBaseId(knowledgeBaseId);
        result.setDocumentId(document.getDocumentId());
        result.setKbType(actualKbType);
        result.setTemplateCode(templateCode);
        result.setTemplateVersion(templateVersion);
        result.setParserType(parserType);
        result.setSourceOrigin(actualSourceOrigin);
        result.setAppVersion(actualAppVersion);
        result.setSortNum(document.getSortNum());
        result.setRowCount(parseResult.getRowCount());
        result.setParseStatus(parseResult.getParseStatus());
        result.setParsedSummary(parseResult.getParsedSummary());
        result.setSnapshotPath(parseResult.getSnapshotPath());
        result.setValidationErrorCount(parseResult.getValidationErrorCount());
        result.setValidationWarningCount(parseResult.getValidationWarningCount());
        result.setValidationIssues(parseResult.getValidationIssues());
        result.setMessage(message("ai.knowledge.template.parse.next.step"));
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, noRollbackFor = ServiceException.class)
    public AiKnowledgeDocumentParseResultVO reparseDocument(Long documentId) {
        AiKnowledgeDocument document = requireKnowledgeDocument(documentId);
        AiKnowledgeBase knowledgeBase = requireKnowledgeBase(document.getKnowledgeBaseId());
        if (StringUtils.isBlank(document.getFilePath())) {
            throw new ServiceException(message("ai.knowledge.document.file.path.required.reparse"));
        }
        Path filePath = Paths.get(document.getFilePath());
        if (!Files.exists(filePath)) {
            throw new ServiceException(message("ai.knowledge.document.file.not.exists", document.getFilePath()));
        }
        try {
            byte[] fileBytes = Files.readAllBytes(filePath);
            String kbType = resolveKnowledgeType(knowledgeBase.getKbType());
            if (KB_TYPE_CODEBASE_GUIDE.equals(kbType)) {
                throw new ServiceException(message("ai.knowledge.template.codebase.reparse.unsupported"));
            }
            String templateCode = resolveTemplateCode(kbType);
            String templateVersion = resolveTemplateVersion(kbType);
            String parserType = resolveParserType(kbType);
            String snapshotCode = buildDocumentSnapshotCode(documentId);
            return parseAndPersistDocument(knowledgeBase, document, fileBytes,
                    templateCode, templateVersion, parserType, snapshotCode);
        } catch (IOException ex) {
            updateDocumentParseFailure(documentId, message("ai.knowledge.document.read.failed", ex.getMessage()), null);
            throw new ServiceException(message("ai.knowledge.document.read.failed", ex.getMessage()));
        }
    }

    @Override
    public JSONObject loadDocumentSnapshot(Long documentId, Integer previewSize) {
        AiKnowledgeDocument document = requireKnowledgeDocument(documentId);
        if (StringUtils.isBlank(document.getParsedSnapshotPath())) {
            throw new ServiceException(message("ai.knowledge.document.snapshot.required"));
        }
        Path snapshotPath = Paths.get(document.getParsedSnapshotPath());
        if (!Files.exists(snapshotPath)) {
            throw new ServiceException(message("ai.knowledge.document.snapshot.not.exists", document.getParsedSnapshotPath()));
        }
        try {
            JSONObject snapshot = JSON.parseObject(Files.readString(snapshotPath, StandardCharsets.UTF_8));
            JSONObject preview = snapshot == null ? new JSONObject() : snapshot;
            int actualPreviewSize = previewSize == null || previewSize <= 0 ? 30 : Math.min(previewSize, 100);
            JSONArray items = preview.getJSONArray("items");
            int totalItemCount = items == null ? 0 : items.size();
            if (items != null && items.size() > actualPreviewSize) {
                JSONArray previewItems = new JSONArray();
                for (int index = 0; index < actualPreviewSize; index++) {
                    previewItems.add(items.get(index));
                }
                preview.put("items", previewItems);
                preview.put("hasMoreItems", true);
            } else {
                preview.put("hasMoreItems", false);
            }
            preview.put("previewItemCount", items == null ? 0 : preview.getJSONArray("items").size());
            preview.put("totalItemCount", totalItemCount);
            preview.put("documentId", document.getDocumentId());
            preview.put("fileName", document.getFileName());
            preview.put("parseStatus", document.getParseStatus());
            preview.put("parsedSummary", document.getParsedSummary());
            preview.put("parsedSnapshotPath", document.getParsedSnapshotPath());
            preview.put("documentUpdateTime", document.getUpdateTime());
            return preview;
        } catch (Exception ex) {
            throw new ServiceException(message("ai.knowledge.document.snapshot.read.failed", ex.getMessage()));
        }
    }

    private AiKnowledgeDocumentParseResultVO parseAndPersistDocument(AiKnowledgeBase knowledgeBase,
                                                                     AiKnowledgeDocument document,
                                                                     byte[] fileBytes,
                                                                     String templateCode,
                                                                     String templateVersion,
                                                                     String parserType,
                                                                     String snapshotCode) {
        Long documentId = document.getDocumentId();
        String kbType = resolveKnowledgeType(knowledgeBase.getKbType());
        updateDocumentParsing(documentId);
        try {
            JSONObject snapshot = parseSnapshot(kbType, fileBytes, knowledgeBase, snapshotCode, templateCode, templateVersion);
            Path snapshotPath = saveSnapshotFile(knowledgeBase, snapshotCode, snapshot);
            int rowCount = snapshot.getIntValue("rowCount");
            int validationErrorCount = snapshot.getIntValue("validationErrorCount");
            int validationWarningCount = snapshot.getIntValue("validationWarningCount");
            List<AiTemplateValidationIssueVO> validationIssues = snapshot.containsKey("validationIssues")
                    ? snapshot.getList("validationIssues", AiTemplateValidationIssueVO.class)
                    : new ArrayList<>();
            String parsedSummary = validationErrorCount > 0
                    ? buildValidationErrorMessage(validationIssues)
                    : buildParsedSummary(kbType, snapshot);

            AiKnowledgeDocumentParseResultVO result = new AiKnowledgeDocumentParseResultVO();
            result.setKnowledgeBaseId(knowledgeBase.getKnowledgeBaseId());
            result.setDocumentId(documentId);
            result.setKbType(kbType);
            result.setTemplateCode(templateCode);
            result.setTemplateVersion(templateVersion);
            result.setParserType(parserType);
            result.setSnapshotCode(snapshotCode);
            result.setRowCount(rowCount);
            result.setParseStatus(validationErrorCount > 0 ? DOCUMENT_STATUS_FAILED : DOCUMENT_STATUS_SUCCESS);
            result.setParsedSummary(parsedSummary);
            result.setSnapshotPath(snapshotPath.toString());
            result.setValidationErrorCount(validationErrorCount);
            result.setValidationWarningCount(validationWarningCount);
            result.setValidationIssues(validationIssues);
            result.setMessage(validationErrorCount > 0
                    ? message("ai.knowledge.document.parse.failed")
                    : message("ai.knowledge.document.parse.success"));

            if (validationErrorCount > 0) {
                updateDocumentParseFailure(documentId, parsedSummary, snapshotPath.toString());
                throw new ServiceException(parsedSummary);
            }

            updateDocumentParseSuccess(documentId, rowCount, snapshotPath.toString(), parsedSummary);
            return result;
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            updateDocumentParseFailure(documentId, message("ai.knowledge.document.parse.failed.reason", ex.getMessage()), null);
            throw new ServiceException(message("ai.knowledge.document.parse.failed.reason", ex.getMessage()));
        }
    }

    private JSONObject parseSnapshot(String kbType, byte[] fileBytes, AiKnowledgeBase knowledgeBase, String versionNo,
                                     String templateCode, String templateVersion) throws Exception {
        if (KB_TYPE_NL2SQL.equals(kbType)) {
            return buildNl2SqlSnapshot(fileBytes, knowledgeBase, versionNo, templateCode, templateVersion);
        }
        if (KB_TYPE_PROTOCOL.equals(kbType)) {
            return buildProtocolSnapshot(fileBytes, knowledgeBase, versionNo, templateCode, templateVersion);
        }
        if (KB_TYPE_CODEBASE_GUIDE.equals(kbType)) {
            throw new ServiceException(message("ai.knowledge.template.codebase.generic.parse.unsupported"));
        }
        return buildPlatformDocSnapshot(fileBytes, knowledgeBase, versionNo, templateCode, templateVersion);
    }

    private JSONObject buildNl2SqlSnapshot(byte[] fileBytes, AiKnowledgeBase knowledgeBase, String versionNo,
                                           String templateCode, String templateVersion) throws Exception {
        List<AiNl2SqlSemanticTemplateRow> rows = importNl2SqlTemplateRows(fileBytes);
        if (rows == null || rows.isEmpty()) {
            throw new ServiceException(message("ai.knowledge.template.nl2sql.rows.required"));
        }
        JSONObject enterpriseSheets = buildNl2SqlEnterpriseSnapshot(fileBytes);
        Map<String, List<GenTableColumn>> tableColumnsMap = loadSemanticTableColumnsMap();
        List<AiTemplateValidationIssueVO> validationIssues = validateNl2SqlRows(rows, tableColumnsMap);
        int validationErrorCount = countValidationIssues(validationIssues, VALIDATION_LEVEL_ERROR);
        int validationWarningCount = countValidationIssues(validationIssues, VALIDATION_LEVEL_WARNING);
        JSONArray items = new JSONArray();
        Set<String> tableNames = new LinkedHashSet<>();
        Set<String> columnKeys = new LinkedHashSet<>();
        for (AiNl2SqlSemanticTemplateRow row : rows) {
            JSONObject item = JSONObject.parseObject(JSON.toJSONString(row));
            item.put("tableColumnKey", buildTableColumnKey(row.getTableName(), row.getColumnName()));
            items.add(item);
            if (StringUtils.isNotBlank(row.getTableName())) {
                tableNames.add(row.getTableName().trim());
            }
            if (StringUtils.isNotBlank(row.getTableName()) || StringUtils.isNotBlank(row.getColumnName())) {
                columnKeys.add(buildTableColumnKey(row.getTableName(), row.getColumnName()));
            }
        }
        JSONObject snapshot = buildSnapshotHeader(knowledgeBase, versionNo, templateCode, templateVersion, KB_TYPE_NL2SQL);
        snapshot.put("rowCount", rows.size());
        snapshot.put("tableCount", tableNames.size());
        snapshot.put("fieldCount", columnKeys.size());
        snapshot.put("tables", tableNames);
        snapshot.put("items", items);
        snapshot.put("businessObjects", enterpriseSheets.getJSONArray("businessObjects"));
        snapshot.put("tableSemantics", enterpriseSheets.getJSONArray("tableSemantics"));
        snapshot.put("relationPaths", enterpriseSheets.getJSONArray("relationPaths"));
        snapshot.put("metricRules", enterpriseSheets.getJSONArray("metricRules"));
        snapshot.put("questionExamples", enterpriseSheets.getJSONArray("questionExamples"));
        snapshot.put("qualityReports", enterpriseSheets.getJSONArray("qualityReports"));
        snapshot.put("enterpriseSheetCount", countNl2SqlEnterpriseSheets(enterpriseSheets));
        snapshot.put("metricRuleCount", enterpriseSheets.getJSONArray("metricRules") == null
                ? 0 : enterpriseSheets.getJSONArray("metricRules").size());
        snapshot.put("validationErrorCount", validationErrorCount);
        snapshot.put("validationWarningCount", validationWarningCount);
        snapshot.put("validationIssues", validationIssues);
        return snapshot;
    }

    private JSONObject buildNl2SqlEnterpriseSnapshot(byte[] fileBytes) throws IOException {
        JSONObject result = new JSONObject();
        try (Workbook workbook = org.apache.poi.ss.usermodel.WorkbookFactory.create(new ByteArrayInputStream(fileBytes))) {
            result.put("businessObjects", readSheetAsJsonArray(workbook, "业务对象"));
            result.put("tableSemantics", readSheetAsJsonArray(workbook, "表级语义"));
            result.put("relationPaths", readSheetAsJsonArray(workbook, "关联路径"));
            result.put("metricRules", readSheetAsJsonArray(workbook, "指标口径"));
            result.put("questionExamples", readSheetAsJsonArray(workbook, "问句样例"));
            result.put("qualityReports", readSheetAsJsonArray(workbook, "质量报告"));
            return result;
        } catch (Exception ex) {
            throw new IOException("解析问数语义企业版辅助 Sheet 失败：" + ex.getMessage(), ex);
        }
    }

    private int countNl2SqlEnterpriseSheets(JSONObject enterpriseSheets) {
        if (enterpriseSheets == null || enterpriseSheets.isEmpty()) {
            return 0;
        }
        int count = 0;
        for (String key : List.of("businessObjects", "tableSemantics", "relationPaths", "metricRules", "questionExamples", "qualityReports")) {
            JSONArray array = enterpriseSheets.getJSONArray(key);
            if (array != null && !array.isEmpty()) {
                count++;
            }
        }
        return count;
    }

    private JSONArray readSheetAsJsonArray(Workbook workbook, String sheetName) {
        JSONArray result = new JSONArray();
        if (workbook == null || StringUtils.isBlank(sheetName)) {
            return result;
        }
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            return result;
        }
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            return result;
        }
        Map<Integer, String> headerMap = new LinkedHashMap<>();
        for (int column = 0; column < headerRow.getLastCellNum(); column++) {
            Cell cell = headerRow.getCell(column);
            String header = trimToEmpty(String.valueOf(getCellText(cell)));
            String key = resolveNl2SqlEnterpriseHeaderKey(sheetName, header);
            if (StringUtils.isNotBlank(key)) {
                headerMap.put(column, key);
            }
        }
        if (headerMap.isEmpty()) {
            return result;
        }
        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                continue;
            }
            JSONObject item = new JSONObject();
            boolean hasValue = false;
            for (Map.Entry<Integer, String> entry : headerMap.entrySet()) {
                Cell cell = row.getCell(entry.getKey());
                String value = trimToEmpty(String.valueOf(getCellText(cell)));
                item.put(entry.getValue(), value);
                if (StringUtils.isNotBlank(value)) {
                    hasValue = true;
                }
            }
            if (hasValue) {
                item.put("sheetName", sheetName);
                item.put("rowNum", rowIndex + 1);
                result.add(item);
            }
        }
        return result;
    }

    private String resolveNl2SqlEnterpriseHeaderKey(String sheetName, String header) {
        String actualHeader = trimToEmpty(header);
        if (StringUtils.isBlank(actualHeader)) {
            return "";
        }
        if ("业务对象".equals(sheetName)) {
            return switch (actualHeader) {
                case "业务对象编码" -> "businessObjectCode";
                case "业务对象名称" -> "businessObjectName";
                case "同义词" -> "aliases";
                case "主事实表" -> "primaryTable";
                case "默认统计口径" -> "defaultMetricRule";
                case "默认数据源" -> "defaultDataSource";
                case "澄清规则" -> "clarifyRule";
                case "备注" -> "remark";
                default -> "";
            };
        }
        if ("表级语义".equals(sheetName)) {
            return switch (actualHeader) {
                case "表名" -> "tableName";
                case "表说明" -> "tableComment";
                case "表角色" -> "tableRole";
                case "主键字段" -> "primaryKeyColumn";
                case "业务对象" -> "businessObjectName";
                case "适用场景" -> "applicableScene";
                case "禁止场景" -> "forbiddenScene";
                case "默认过滤条件" -> "defaultFilter";
                case "备注" -> "remark";
                default -> "";
            };
        }
        if ("关联路径".equals(sheetName)) {
            return switch (actualHeader) {
                case "关联编码" -> "relationCode";
                case "起始表" -> "sourceTable";
                case "起始字段" -> "sourceColumn";
                case "目标表" -> "targetTable";
                case "目标字段" -> "targetColumn";
                case "适用问法" -> "applicableQuestion";
                case "禁止问法" -> "forbiddenQuestion";
                case "备注" -> "remark";
                default -> "";
            };
        }
        if ("指标口径".equals(sheetName)) {
            return switch (actualHeader) {
                case "指标口径编码" -> "metricRuleCode";
                case "指标口径名称" -> "metricRuleName";
                case "业务对象" -> "businessObjectName";
                case "主事实表" -> "primaryTable";
                case "聚合方式" -> "aggregationType";
                case "去重字段" -> "distinctColumn";
                case "状态/字典口径" -> "stateRule";
                case "时间口径" -> "timeRule";
                case "默认数据源" -> "defaultDataSource";
                case "适用问法" -> "applicableQuestion";
                case "备注" -> "remark";
                default -> "";
            };
        }
        if ("问句样例".equals(sheetName)) {
            return switch (actualHeader) {
                case "问题" -> "question";
                case "期望业务对象" -> "expectedBusinessObject";
                case "期望主表/数据源" -> "expectedSource";
                case "允许表" -> "allowedTables";
                case "禁止表" -> "forbiddenTables";
                case "期望聚合/动作" -> "expectedAction";
                case "是否允许澄清" -> "allowClarify";
                case "风险等级" -> "riskLevel";
                case "备注" -> "remark";
                default -> "";
            };
        }
        if ("质量报告".equals(sheetName)) {
            return switch (actualHeader) {
                case "检查项" -> "checkItem";
                case "级别" -> "level";
                case "结果" -> "result";
                case "建议处理" -> "suggestion";
                default -> "";
            };
        }
        return "";
    }

    private Map<String, List<GenTableColumn>> loadSemanticTableColumnsMap() {
        Map<String, List<GenTableColumn>> tableColumnsMap = new LinkedHashMap<>();
        for (Table<?> table : loadSemanticTables()) {
            List<GenTableColumn> columns = genTableService.selectDbTableColumnsByName(table.getName(), primaryDataSource);
            if (columns == null || columns.isEmpty()) {
                continue;
            }
            columns.sort(Comparator.comparing(GenTableColumn::getSort, Comparator.nullsLast(Integer::compareTo))
                    .thenComparing(GenTableColumn::getColumnName, Comparator.nullsLast(String::compareToIgnoreCase)));
            tableColumnsMap.put(normalizeTableName(table.getName()), columns);
        }
        return tableColumnsMap;
    }

    private List<AiTemplateValidationIssueVO> validateNl2SqlRows(List<AiNl2SqlSemanticTemplateRow> rows,
                                                                 Map<String, List<GenTableColumn>> tableColumnsMap) {
        List<AiTemplateValidationIssueVO> issues = new ArrayList<>();
        Set<String> uniqueKeys = new LinkedHashSet<>();
        for (int index = 0; index < rows.size(); index++) {
            AiNl2SqlSemanticTemplateRow row = rows.get(index);
            int rowNum = index + 2;
            normalizeNl2SqlRow(row);
            String tableName = normalizeTableName(row.getTableName());
            String columnName = normalizeColumnName(row.getColumnName());
            String semanticName = trimToEmpty(row.getSemanticName());
            String semanticType = normalizeSemanticType(row.getSemanticType());
            String sourceType = normalizeSourceType(row.getSourceType());
            String sourceCode = trimToEmpty(row.getSourceCode());
            String valueMappings = trimToEmpty(row.getValueMappings());
            String relationHints = trimToEmpty(row.getRelationHints());

            row.setTableName(tableName);
            row.setColumnName(columnName);
            row.setSemanticName(semanticName);
            row.setSemanticType(semanticType);
            row.setSourceType(sourceType);
            row.setSourceCode(sourceCode);
            row.setValueMappings(valueMappings);
            row.setRelationHints(relationHints);
            row.setAliases(normalizeDelimitedValue(row.getAliases()));
            row.setQueryHints(trimToEmpty(row.getQueryHints()));
            row.setRemark(trimToEmpty(row.getRemark()));

            if (StringUtils.isBlank(tableName)) {
                addValidationIssue(issues, rowNum, VALIDATION_LEVEL_ERROR, "tableName",
                        "TABLE_NAME_REQUIRED", row.getTableName(), "表名不能为空");
            } else if (!containsTable(tableColumnsMap, tableName)) {
                addValidationIssue(issues, rowNum, VALIDATION_LEVEL_ERROR, "tableName",
                        "TABLE_NOT_FOUND", row.getTableName(), "当前数据库中不存在该表");
            }

            if (StringUtils.isBlank(columnName)) {
                addValidationIssue(issues, rowNum, VALIDATION_LEVEL_ERROR, "columnName",
                        "COLUMN_NAME_REQUIRED", row.getColumnName(), "字段名不能为空");
            } else if (containsTable(tableColumnsMap, tableName) && !hasColumn(tableColumnsMap.get(tableName), columnName)) {
                addValidationIssue(issues, rowNum, VALIDATION_LEVEL_ERROR, "columnName",
                        "COLUMN_NOT_FOUND", row.getColumnName(), "当前表中不存在该字段");
            }

            if (StringUtils.isBlank(semanticName)) {
                addValidationIssue(issues, rowNum, VALIDATION_LEVEL_ERROR, "semanticName",
                        "SEMANTIC_NAME_REQUIRED", row.getSemanticName(), "语义名称不能为空");
            }

            if (StringUtils.isBlank(semanticType)) {
                addValidationIssue(issues, rowNum, VALIDATION_LEVEL_ERROR, "semanticType",
                        "SEMANTIC_TYPE_REQUIRED", row.getSemanticType(), "语义类型不能为空");
            } else if (!Set.of(SEMANTIC_RELATION_KEY, SEMANTIC_ENUM, SEMANTIC_DIMENSION).contains(semanticType)) {
                addValidationIssue(issues, rowNum, VALIDATION_LEVEL_ERROR, "semanticType",
                        "SEMANTIC_TYPE_INVALID", row.getSemanticType(), "语义类型只允许填写 RELATION_KEY / ENUM / DIMENSION");
            }

            if (StringUtils.isBlank(sourceType)) {
                addValidationIssue(issues, rowNum, VALIDATION_LEVEL_ERROR, "sourceType",
                        "SOURCE_TYPE_REQUIRED", row.getSourceType(), "来源类型不能为空");
            } else if (!Set.of(SOURCE_MANUAL, SOURCE_DICT, SOURCE_ENUM, SOURCE_AUTO_COMMENT).contains(sourceType)) {
                addValidationIssue(issues, rowNum, VALIDATION_LEVEL_ERROR, "sourceType",
                        "SOURCE_TYPE_INVALID", row.getSourceType(), "来源类型只允许填写 MANUAL / DICT / ENUM / AUTO_COMMENT");
            }

            if ((SOURCE_DICT.equals(sourceType) || SOURCE_ENUM.equals(sourceType)) && StringUtils.isBlank(sourceCode)) {
                addValidationIssue(issues, rowNum, VALIDATION_LEVEL_ERROR, "sourceCode",
                        "SOURCE_CODE_REQUIRED", row.getSourceCode(), "当来源类型为 DICT 或 ENUM 时，来源编码不能为空");
            }
            if ((SOURCE_MANUAL.equals(sourceType) || SOURCE_AUTO_COMMENT.equals(sourceType)) && StringUtils.isNotBlank(sourceCode)) {
                addValidationIssue(issues, rowNum, VALIDATION_LEVEL_WARNING, "sourceCode",
                        "SOURCE_CODE_REDUNDANT", row.getSourceCode(), "当来源类型为 MANUAL 或 AUTO_COMMENT 时，来源编码通常应留空");
            }

            if (StringUtils.isNotBlank(valueMappings) && !isValidValueMappings(valueMappings)) {
                addValidationIssue(issues, rowNum, VALIDATION_LEVEL_WARNING, "valueMappings",
                        "VALUE_MAPPING_INVALID", row.getValueMappings(), "值映射建议使用 标签=值;标签=值 的格式");
            }
            if (SOURCE_DICT.equals(sourceType) && StringUtils.isNotBlank(valueMappings)) {
                addValidationIssue(issues, rowNum, VALIDATION_LEVEL_WARNING, "valueMappings",
                        "DICT_AND_VALUE_MAPPING_CONFLICT", row.getValueMappings(), "当前字段已指定系统字典，值映射建议只在字典缺失时补充");
            }

            if (StringUtils.isNotBlank(relationHints)) {
                validateRelationHints(issues, rowNum, relationHints, tableColumnsMap);
            }

            if (StringUtils.isNotBlank(tableName) && StringUtils.isNotBlank(columnName)) {
                String uniqueKey = buildTableColumnKey(tableName, columnName);
                if (!uniqueKeys.add(uniqueKey)) {
                    addValidationIssue(issues, rowNum, VALIDATION_LEVEL_ERROR, "columnName",
                            "DUPLICATE_FIELD_MAPPING", uniqueKey, "同一表字段只允许维护一条语义记录");
                }
            }
        }
        return issues;
    }

    private void normalizeNl2SqlRow(AiNl2SqlSemanticTemplateRow row) {
        row.setTableName(trimToEmpty(row.getTableName()));
        row.setTableComment(trimToEmpty(row.getTableComment()));
        row.setColumnName(trimToEmpty(row.getColumnName()));
        row.setColumnComment(trimToEmpty(row.getColumnComment()));
        row.setSemanticName(trimToEmpty(row.getSemanticName()));
        row.setSemanticType(trimToEmpty(row.getSemanticType()));
        row.setSourceType(trimToEmpty(row.getSourceType()));
        row.setSourceCode(trimToEmpty(row.getSourceCode()));
        row.setRelationHints(trimToEmpty(row.getRelationHints()));
        row.setValueMappings(trimToEmpty(row.getValueMappings()));
        row.setAliases(trimToEmpty(row.getAliases()));
        row.setQueryHints(trimToEmpty(row.getQueryHints()));
        row.setRemark(trimToEmpty(row.getRemark()));
    }

    private String normalizeSemanticType(String semanticType) {
        String actualType = trimToEmpty(semanticType);
        if (StringUtils.isBlank(actualType)) {
            return "";
        }
        String upperType = actualType.toUpperCase(Locale.ROOT);
        if (SEMANTIC_RELATION_KEY.equals(upperType) || "关系键".equals(actualType)) {
            return SEMANTIC_RELATION_KEY;
        }
        if (SEMANTIC_ENUM.equals(upperType) || "枚举".equals(actualType)) {
            return SEMANTIC_ENUM;
        }
        if (SEMANTIC_DIMENSION.equals(upperType) || "维度".equals(actualType)) {
            return SEMANTIC_DIMENSION;
        }
        return upperType;
    }

    private String normalizeSourceType(String sourceType) {
        String actualType = trimToEmpty(sourceType);
        if (StringUtils.isBlank(actualType)) {
            return "";
        }
        String upperType = actualType.toUpperCase(Locale.ROOT);
        if (SOURCE_MANUAL.equals(upperType) || "人工".equals(actualType) || "人工补充".equals(actualType)) {
            return SOURCE_MANUAL;
        }
        if (SOURCE_DICT.equals(upperType) || "字典".equals(actualType) || "系统字典".equals(actualType)) {
            return SOURCE_DICT;
        }
        if (SOURCE_ENUM.equals(upperType) || "枚举".equals(actualType) || "枚举类".equals(actualType)) {
            return SOURCE_ENUM;
        }
        if (SOURCE_AUTO_COMMENT.equals(upperType) || "注释".equals(actualType) || "注释解析".equals(actualType)) {
            return SOURCE_AUTO_COMMENT;
        }
        return upperType;
    }

    private String normalizeDelimitedValue(String value) {
        return trimToEmpty(value).replace('；', ';');
    }

    private void validateRelationHints(List<AiTemplateValidationIssueVO> issues, int rowNum, String relationHints,
                                       Map<String, List<GenTableColumn>> tableColumnsMap) {
        for (String hint : relationHints.split("[;；\\r\\n]+")) {
            String actualHint = trimToEmpty(hint);
            if (StringUtils.isBlank(actualHint) || !actualHint.contains("=")) {
                continue;
            }
            String[] pair = actualHint.split("=", 2);
            if (pair.length != 2 || !isValidRelationEndpoint(pair[0], tableColumnsMap)
                    || !isValidRelationEndpoint(pair[1], tableColumnsMap)) {
                addValidationIssue(issues, rowNum, VALIDATION_LEVEL_WARNING, "relationHints",
                        "RELATION_HINT_INVALID", actualHint, "关联提示建议使用 table.column=table.column，且表字段必须存在");
            }
        }
    }

    private boolean isValidRelationEndpoint(String endpoint, Map<String, List<GenTableColumn>> tableColumnsMap) {
        String actualEndpoint = trimToEmpty(endpoint);
        if (!actualEndpoint.contains(".")) {
            return false;
        }
        String[] pair = actualEndpoint.split("\\.", 2);
        if (pair.length != 2) {
            return false;
        }
        String tableName = normalizeTableName(pair[0]);
        String columnName = normalizeColumnName(pair[1]);
        return containsTable(tableColumnsMap, tableName) && hasColumn(tableColumnsMap.get(tableName), columnName);
    }

    private boolean isValidValueMappings(String valueMappings) {
        for (String item : valueMappings.split("[;；]+")) {
            String actualItem = trimToEmpty(item);
            if (StringUtils.isBlank(actualItem)) {
                continue;
            }
            if (!actualItem.contains("=")) {
                return false;
            }
            String[] pair = actualItem.split("=", 2);
            if (pair.length != 2 || StringUtils.isBlank(trimToEmpty(pair[0])) || StringUtils.isBlank(trimToEmpty(pair[1]))) {
                return false;
            }
        }
        return true;
    }

    private int countValidationIssues(List<AiTemplateValidationIssueVO> issues, String level) {
        return (int) issues.stream().filter(issue -> StringUtils.equalsIgnoreCase(level, issue.getLevel())).count();
    }

    private String buildValidationErrorMessage(List<AiTemplateValidationIssueVO> issues) {
        List<String> messages = issues.stream()
                .filter(issue -> StringUtils.equalsIgnoreCase(VALIDATION_LEVEL_ERROR, issue.getLevel()))
                .limit(5)
                .map(issue -> "第" + issue.getRowNum() + "行[" + issue.getFieldName() + "]" + issue.getMessage())
                .toList();
        return "模板校验失败，共 " + countValidationIssues(issues, VALIDATION_LEVEL_ERROR)
                + " 处错误：" + String.join("；", messages);
    }

    private void addValidationIssue(List<AiTemplateValidationIssueVO> issues, int rowNum, String level,
                                    String fieldName, String issueCode, String currentValue, String message) {
        AiTemplateValidationIssueVO issue = new AiTemplateValidationIssueVO();
        issue.setRowNum(rowNum);
        issue.setLevel(level);
        issue.setFieldName(fieldName);
        issue.setIssueCode(issueCode);
        issue.setCurrentValue(trimToEmpty(currentValue));
        issue.setMessage(message);
        issues.add(issue);
    }

    private JSONObject buildProtocolSnapshot(byte[] fileBytes, AiKnowledgeBase knowledgeBase, String versionNo,
                                             String templateCode, String templateVersion) throws Exception {
        ExcelUtil<AiProtocolKnowledgeTemplateRow> excelUtil = new ExcelUtil<>(AiProtocolKnowledgeTemplateRow.class);
        List<AiProtocolKnowledgeTemplateRow> rows = excelUtil.importExcel(new ByteArrayInputStream(fileBytes));
        if (rows == null || rows.isEmpty()) {
            throw new ServiceException(message("ai.knowledge.template.protocol.rows.required"));
        }
        JSONArray items = new JSONArray();
        Set<String> moduleNames = new LinkedHashSet<>();
        for (AiProtocolKnowledgeTemplateRow row : rows) {
            items.add(JSONObject.parseObject(JSON.toJSONString(row)));
            if (StringUtils.isNotBlank(row.getModuleName())) {
                moduleNames.add(row.getModuleName().trim());
            }
        }
        JSONObject snapshot = buildSnapshotHeader(knowledgeBase, versionNo, templateCode, templateVersion, KB_TYPE_PROTOCOL);
        snapshot.put("rowCount", rows.size());
        snapshot.put("moduleCount", moduleNames.size());
        snapshot.put("modules", moduleNames);
        snapshot.put("items", items);
        snapshot.put("validationErrorCount", 0);
        snapshot.put("validationWarningCount", 0);
        snapshot.put("validationIssues", new ArrayList<>());
        return snapshot;
    }

    private List<AiNl2SqlSemanticTemplateRow> importNl2SqlTemplateRows(byte[] fileBytes) throws Exception {
        ExcelUtil<AiNl2SqlSemanticTemplateRow> excelUtil = new ExcelUtil<>(AiNl2SqlSemanticTemplateRow.class);
        return excelUtil.importExcel(new ByteArrayInputStream(normalizeNl2SqlTemplateHeaders(fileBytes)));
    }

    private byte[] normalizeNl2SqlTemplateHeaders(byte[] fileBytes) throws IOException {
        try (Workbook workbook = org.apache.poi.ss.usermodel.WorkbookFactory.create(new ByteArrayInputStream(fileBytes));
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            if (workbook.getNumberOfSheets() <= 0) {
                return fileBytes;
            }
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                return fileBytes;
            }
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                return fileBytes;
            }
            boolean changed = false;
            for (int column = 0; column < headerRow.getLastCellNum(); column++) {
                Cell cell = headerRow.getCell(column);
                if (cell == null) {
                    continue;
                }
                String actualHeader = trimToEmpty(String.valueOf(getCellText(cell)));
                String canonicalHeader = resolveNl2SqlTemplateHeader(actualHeader);
                if (StringUtils.isNotBlank(canonicalHeader) && !StringUtils.equals(actualHeader, canonicalHeader)) {
                    cell.setCellValue(canonicalHeader);
                    changed = true;
                }
            }
            if (!changed) {
                return fileBytes;
            }
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (Exception ex) {
            throw new IOException("标准化问数语义模板表头失败：" + ex.getMessage(), ex);
        }
    }

    private Object getCellText(Cell cell) {
        if (cell == null) {
            return "";
        }
        if (cell.getCellType() == org.apache.poi.ss.usermodel.CellType.STRING) {
            return cell.getStringCellValue();
        }
        return cell.toString();
    }

    private String resolveNl2SqlTemplateHeader(String header) {
        String actualHeader = trimToEmpty(header).replace('\r', ' ').replace('\n', ' ').replace("　", " ");
        if (StringUtils.isBlank(actualHeader)) {
            return actualHeader;
        }
        if (actualHeader.startsWith(HEADER_TABLE_NAME)) {
            return HEADER_TABLE_NAME;
        }
        if (actualHeader.startsWith(HEADER_TABLE_COMMENT)) {
            return HEADER_TABLE_COMMENT;
        }
        if (actualHeader.startsWith(HEADER_COLUMN_NAME)) {
            return HEADER_COLUMN_NAME;
        }
        if (actualHeader.startsWith(HEADER_COLUMN_COMMENT)) {
            return HEADER_COLUMN_COMMENT;
        }
        if (actualHeader.startsWith(HEADER_SEMANTIC_NAME)) {
            return HEADER_SEMANTIC_NAME;
        }
        if (actualHeader.startsWith(HEADER_SEMANTIC_TYPE)) {
            return HEADER_SEMANTIC_TYPE;
        }
        if (actualHeader.startsWith(HEADER_SOURCE_TYPE)) {
            return HEADER_SOURCE_TYPE;
        }
        if (actualHeader.startsWith(HEADER_SOURCE_CODE)) {
            return HEADER_SOURCE_CODE;
        }
        if (actualHeader.startsWith(HEADER_RELATION_HINTS)) {
            return HEADER_RELATION_HINTS;
        }
        if (actualHeader.startsWith(HEADER_VALUE_MAPPINGS)) {
            return HEADER_VALUE_MAPPINGS;
        }
        if (actualHeader.startsWith(HEADER_ALIASES)) {
            return HEADER_ALIASES;
        }
        if (actualHeader.startsWith(HEADER_QUERY_HINTS)) {
            return HEADER_QUERY_HINTS;
        }
        if (actualHeader.startsWith(HEADER_REMARK)) {
            return HEADER_REMARK;
        }
        return actualHeader;
    }

    private JSONObject buildPlatformDocSnapshot(byte[] fileBytes, AiKnowledgeBase knowledgeBase, String versionNo,
                                                String templateCode, String templateVersion) throws Exception {
        ExcelUtil<AiPlatformDocTemplateRow> excelUtil = new ExcelUtil<>(AiPlatformDocTemplateRow.class);
        List<AiPlatformDocTemplateRow> rows = excelUtil.importExcel(new ByteArrayInputStream(fileBytes));
        if (rows == null || rows.isEmpty()) {
            throw new ServiceException(message("ai.knowledge.template.platform.rows.required"));
        }
        JSONArray items = new JSONArray();
        Set<String> pageTitles = new LinkedHashSet<>();
        Set<String> sectionPaths = new LinkedHashSet<>();
        for (AiPlatformDocTemplateRow row : rows) {
            normalizePlatformDocRow(row);
            JSONObject item = new JSONObject();
            item.put("sectionLevel1", row.getSectionLevel1());
            item.put("sectionPath", row.getSectionPath());
            item.put("pageTitle", row.getPageTitle());
            item.put("headingPath", row.getHeadingPath());
            item.put("knowledgeType", row.getKnowledgeType());
            item.put("menuPath", row.getMenuPath());
            item.put("targetRole", row.getTargetRole());
            item.put("preconditions", row.getPreconditions());
            item.put("actionSteps", row.getActionSteps());
            item.put("resultDesc", row.getResultDesc());
            item.put("cautions", row.getCautions());
            item.put("bodySupplement", row.getContent());
            item.put("tags", row.getTags());
            item.put("aliases", row.getAliases());
            item.put("relatedDocs", row.getRelatedDocs());
            item.put("sourceFile", row.getSourceFile());
            item.put("sourceUrl", row.getSourceUrl());
            item.put("sourceType", row.getSourceType());
            item.put("language", row.getLanguage());
            item.put("docVersion", row.getDocVersion());
            item.put("remarkRaw", row.getRemark());
            // 兼容当前运行时结构，避免平台助手和知识检索在模板升级时断链。
            item.put("sectionName", resolvePlatformCompatSectionName(row));
            item.put("title", resolvePlatformCompatTitle(row));
            item.put("content", buildPlatformCompatContent(row));
            item.put("remark", buildPlatformCompatRemark(row));
            items.add(item);
            if (StringUtils.isNotBlank(row.getPageTitle())) {
                pageTitles.add(row.getPageTitle());
            }
            if (StringUtils.isNotBlank(row.getSectionPath())) {
                sectionPaths.add(row.getSectionPath());
            }
        }
        JSONObject snapshot = buildSnapshotHeader(knowledgeBase, versionNo, templateCode, templateVersion, KB_TYPE_PLATFORM_DOC);
        snapshot.put("rowCount", rows.size());
        snapshot.put("pageCount", pageTitles.size());
        snapshot.put("sectionCount", sectionPaths.size());
        snapshot.put("items", items);
        snapshot.put("validationErrorCount", 0);
        snapshot.put("validationWarningCount", 0);
        snapshot.put("validationIssues", new ArrayList<>());
        return snapshot;
    }

    private void normalizePlatformDocRow(AiPlatformDocTemplateRow row) {
        row.setSectionLevel1(trimToEmpty(row.getSectionLevel1()));
        row.setSectionPath(trimToEmpty(row.getSectionPath()));
        row.setPageTitle(trimToEmpty(row.getPageTitle()));
        row.setHeadingPath(trimToEmpty(row.getHeadingPath()));
        row.setKnowledgeType(trimToEmpty(row.getKnowledgeType()).toUpperCase(Locale.ROOT));
        row.setMenuPath(trimToEmpty(row.getMenuPath()));
        row.setTargetRole(normalizeDelimitedValue(row.getTargetRole()));
        row.setPreconditions(normalizeDelimitedValue(row.getPreconditions()));
        row.setActionSteps(normalizeDelimitedValue(row.getActionSteps()));
        row.setResultDesc(normalizeDelimitedValue(row.getResultDesc()));
        row.setCautions(normalizeDelimitedValue(row.getCautions()));
        row.setContent(trimToEmpty(row.getContent()));
        row.setTags(normalizeDelimitedValue(row.getTags()));
        row.setAliases(normalizeDelimitedValue(row.getAliases()));
        row.setRelatedDocs(normalizeDelimitedValue(row.getRelatedDocs()));
        row.setSourceFile(trimToEmpty(row.getSourceFile()));
        row.setSourceUrl(trimToEmpty(row.getSourceUrl()));
        row.setSourceType(trimToEmpty(row.getSourceType()).toUpperCase(Locale.ROOT));
        row.setLanguage(StringUtils.isBlank(row.getLanguage()) ? "zh-CN" : trimToEmpty(row.getLanguage()));
        row.setDocVersion(trimToEmpty(row.getDocVersion()));
        row.setRemark(trimToEmpty(row.getRemark()));
    }

    private String resolvePlatformCompatSectionName(AiPlatformDocTemplateRow row) {
        if (StringUtils.isNotBlank(row.getSectionPath())) {
            return row.getSectionPath();
        }
        if (StringUtils.isNotBlank(row.getSectionLevel1())) {
            return row.getSectionLevel1();
        }
        return row.getPageTitle();
    }

    private String resolvePlatformCompatTitle(AiPlatformDocTemplateRow row) {
        if (StringUtils.isNotBlank(row.getHeadingPath())) {
            return row.getHeadingPath();
        }
        return row.getPageTitle();
    }

    private String buildPlatformCompatContent(AiPlatformDocTemplateRow row) {
        List<String> segments = new ArrayList<>();
        appendPlatformContentSegment(segments, "前置条件", row.getPreconditions());
        appendPlatformContentSegment(segments, "操作步骤", row.getActionSteps());
        appendPlatformContentSegment(segments, "结果说明", row.getResultDesc());
        appendPlatformContentSegment(segments, "注意事项", row.getCautions());
        appendPlatformContentSegment(segments, "正文补充", row.getContent());
        return String.join("\n", segments);
    }

    private void appendPlatformContentSegment(List<String> segments, String label, String value) {
        String actualValue = trimToEmpty(value);
        if (StringUtils.isBlank(actualValue)) {
            return;
        }
        segments.add(label + "：" + actualValue.replace(';', '；'));
    }

    private String buildPlatformCompatRemark(AiPlatformDocTemplateRow row) {
        List<String> segments = new ArrayList<>();
        appendRemarkSegment(segments, "知识类型", row.getKnowledgeType());
        appendRemarkSegment(segments, "菜单路径", row.getMenuPath());
        appendRemarkSegment(segments, "适用角色", row.getTargetRole());
        appendRemarkSegment(segments, "关联文档", row.getRelatedDocs());
        appendRemarkSegment(segments, "来源文件", row.getSourceFile());
        appendRemarkSegment(segments, "来源链接", row.getSourceUrl());
        appendRemarkSegment(segments, "来源类型", row.getSourceType());
        appendRemarkSegment(segments, "备注", row.getRemark());
        return String.join("；", segments);
    }

    private void appendRemarkSegment(List<String> segments, String label, String value) {
        String actualValue = trimToEmpty(value);
        if (StringUtils.isBlank(actualValue)) {
            return;
        }
        segments.add(label + "=" + actualValue);
    }

    private JSONObject buildSnapshotHeader(AiKnowledgeBase knowledgeBase, String versionNo, String templateCode,
                                           String templateVersion, String kbType) {
        JSONObject snapshot = new JSONObject();
        snapshot.put("knowledgeBaseId", knowledgeBase.getKnowledgeBaseId());
        snapshot.put("kbCode", knowledgeBase.getKbCode());
        snapshot.put("kbName", knowledgeBase.getKbName());
        snapshot.put("kbType", kbType);
        snapshot.put("templateCode", templateCode);
        snapshot.put("templateVersion", templateVersion);
        snapshot.put("versionNo", versionNo);
        snapshot.put("generatedAt", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        return snapshot;
    }

    private AiKnowledgeDocument buildDocumentDraft(AiKnowledgeBase knowledgeBase, String originalFilename, Path uploadedFilePath,
                                                   byte[] fileBytes, String templateCode, String templateVersion,
                                                   String sourceOrigin, String appVersion, Integer sortNum) {
        AiKnowledgeDocument document = new AiKnowledgeDocument();
        document.setKnowledgeBaseId(knowledgeBase.getKnowledgeBaseId());
        document.setFileName(originalFilename);
        document.setFilePath(uploadedFilePath.toString());
        document.setFileSize((long) fileBytes.length);
        document.setChecksum(DigestUtils.md5DigestAsHex(fileBytes));
        document.setParseStatus(DOCUMENT_STATUS_PARSING);
        document.setChunkCount(0);
        document.setSortNum(sortNum);
        document.setSourceOrigin(sourceOrigin);
        document.setAppVersion(appVersion);
        document.setStatus("0");
        document.setCreateBy(AiSecuritySupport.resolveUsername());
        document.setCreateTime(AiSecuritySupport.now());
        document.setUpdateBy(AiSecuritySupport.resolveUsername());
        document.setUpdateTime(AiSecuritySupport.now());
        return document;
    }

    private void updateKnowledgeBaseMetadata(AiKnowledgeBase knowledgeBase) {
        AiKnowledgeBase update = new AiKnowledgeBase();
        update.setKnowledgeBaseId(knowledgeBase.getKnowledgeBaseId());
        update.setVectorStoreType(resolveVectorStoreType(knowledgeBase));
        String publishStatus = knowledgeBase.getPublishStatus();
        if (StringUtils.isBlank(publishStatus)
                || VERSION_STATUS_DRAFT.equalsIgnoreCase(publishStatus)) {
            update.setPublishStatus(VERSION_STATUS_DRAFT);
        } else {
            update.setPublishStatus(publishStatus);
        }
        update.setUpdateBy(AiSecuritySupport.resolveUsername());
        update.setUpdateTime(AiSecuritySupport.now());
        aiKnowledgeBaseService.updateById(update);
    }

    private String resolveKnowledgeType(Long knowledgeBaseId, String kbType) {
        if (knowledgeBaseId != null) {
            return resolveKnowledgeType(requireKnowledgeBase(knowledgeBaseId).getKbType());
        }
        return resolveKnowledgeType(kbType);
    }

    private String resolveKnowledgeType(String kbType) {
        String actualType = StringUtils.isBlank(kbType) ? KB_TYPE_PLATFORM_DOC : kbType.trim().toUpperCase(Locale.ROOT);
        if ("NL2SQL".equals(actualType)) {
            return KB_TYPE_NL2SQL;
        }
        if ("PROTOCOL".equals(actualType)) {
            return KB_TYPE_PROTOCOL;
        }
        if ("GENERAL".equals(actualType) || "PLATFORM".equals(actualType) || "PLATFORM_KNOWLEDGE".equals(actualType)) {
            return KB_TYPE_PLATFORM_DOC;
        }
        return actualType;
    }

    private List<AiNl2SqlSemanticTemplateRow> buildNl2SqlSemanticTemplateRows() {
        List<AiNl2SqlSemanticTemplateRow> rows = new ArrayList<>();
        List<Table<?>> tables = loadSemanticTables();
        Map<String, Map<String, SemanticAnnotationMeta>> annotationMetas = loadSemanticAnnotationMetas();
        Map<String, List<GenTableColumn>> tableColumnsMap = new LinkedHashMap<>();
        for (Table<?> table : tables) {
            List<GenTableColumn> columns = genTableService.selectDbTableColumnsByName(table.getName(), primaryDataSource);
            if (columns == null || columns.isEmpty()) {
                continue;
            }
            columns.sort(Comparator.comparing(GenTableColumn::getSort, Comparator.nullsLast(Integer::compareTo))
                    .thenComparing(GenTableColumn::getColumnName, Comparator.nullsLast(String::compareToIgnoreCase)));
            tableColumnsMap.put(normalizeTableName(table.getName()), columns);
        }
        for (Table<?> table : tables) {
            List<GenTableColumn> columns = tableColumnsMap.get(normalizeTableName(table.getName()));
            if (columns == null || columns.isEmpty()) {
                continue;
            }
            for (GenTableColumn column : columns) {
                AiNl2SqlSemanticTemplateRow row = buildNl2SqlTemplateRow(table, column, tableColumnsMap, annotationMetas);
                if (row != null) {
                    rows.add(row);
                }
            }
        }
        return rows;
    }

    private List<Table<?>> loadSemanticTables() {
        try {
            Map<String, Table<?>> tablesMap = ServiceProxy.metadata().tables();
            if (tablesMap == null || tablesMap.isEmpty()) {
                throw new ServiceException(message("ai.knowledge.template.datasource.metadata.empty"));
            }
            return tablesMap.values().stream()
                    .filter(table -> table != null && StringUtils.isNotBlank(table.getName()))
                    .filter(table -> shouldExportSemanticTable(table.getName()))
                    .sorted(Comparator.comparing(Table::getName, String.CASE_INSENSITIVE_ORDER))
                    .toList();
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException(message("ai.knowledge.template.datasource.metadata.load.failed", ex.getMessage()));
        }
    }

    private boolean shouldExportSemanticTable(String tableName) {
        String actualTableName = tableName == null ? "" : tableName.trim().toLowerCase(Locale.ROOT);
        if (actualTableName.isEmpty()) {
            return false;
        }
        for (String prefix : TEMPLATE_IGNORE_PREFIXES) {
            if (actualTableName.startsWith(prefix)) {
                return false;
            }
        }
        return true;
    }

    private AiNl2SqlSemanticTemplateRow buildNl2SqlTemplateRow(Table<?> table, GenTableColumn column,
                                                               Map<String, List<GenTableColumn>> tableColumnsMap,
                                                               Map<String, Map<String, SemanticAnnotationMeta>> annotationMetas) {
        AiNl2SqlSemanticTemplateRow row = new AiNl2SqlSemanticTemplateRow();
        String valueMappings = resolveValueMappings(column);
        row.setTableName(trimToEmpty(table.getName()));
        row.setTableComment(trimToEmpty(table.getComment()));
        row.setColumnName(trimToEmpty(column.getColumnName()));
        row.setColumnComment(trimToEmpty(column.getColumnComment()));
        row.setSemanticName(resolveSemanticName(column.getColumnName(), column.getColumnComment()));
        row.setSemanticType(resolveSemanticType(column));
        row.setSourceType(resolveSourceType(column, valueMappings));
        row.setSourceCode(resolveSourceCode(column));
        row.setRelationHints(resolveRelationHints(table.getName(), column, tableColumnsMap));
        row.setValueMappings(valueMappings);
        row.setAliases(resolveAliases(table.getName(), table.getComment(), column));
        row.setQueryHints(resolveQueryHints(table.getName(), table.getComment(), column));
        row.setRemark(resolveRemark(table.getName(), table.getComment(), column));
        SemanticAnnotationMeta annotationMeta =
                resolveSemanticAnnotationMeta(table.getName(), column.getColumnName(), annotationMetas);
        if (annotationMeta != null && annotationMeta.ignore()) {
            return null;
        }
        applySemanticAnnotation(row, annotationMeta);
        return row;
    }

    private void applySemanticAnnotation(AiNl2SqlSemanticTemplateRow row, SemanticAnnotationMeta annotationMeta) {
        if (annotationMeta == null || annotationMeta.ignore()) {
            return;
        }
        AiSemanticField semanticField = annotationMeta.semanticField();
        if (semanticField == null) {
            return;
        }
        if (StringUtils.isNotBlank(semanticField.semanticName())) {
            row.setSemanticName(semanticField.semanticName());
        }
        row.setSemanticType(semanticField.semanticType().name());
        row.setSourceType(semanticField.sourceType().name());
        row.setSourceCode(trimToEmpty(semanticField.sourceCode()));
        row.setRelationHints(mergeSemanticText(row.getRelationHints(), semanticField.relationHint()));
        String valueMappings = joinAnnotationValues(semanticField.valueMappings());
        if (StringUtils.isNotBlank(valueMappings)) {
            row.setValueMappings(valueMappings);
        }
        row.setAliases(mergeSemanticText(row.getAliases(), joinAnnotationValues(semanticField.synonyms())));
        row.setQueryHints(mergeSemanticText(row.getQueryHints(), semanticField.queryHint()));
        row.setRemark(mergeSemanticText(row.getRemark(), semanticField.remark()));
    }

    private SemanticAnnotationMeta resolveSemanticAnnotationMeta(String tableName, String columnName,
                                                                Map<String, Map<String, SemanticAnnotationMeta>> annotationMetas) {
        if (annotationMetas == null || annotationMetas.isEmpty()) {
            return null;
        }
        Map<String, SemanticAnnotationMeta> tableMetas = annotationMetas.get(normalizeTableName(tableName));
        if (tableMetas == null || tableMetas.isEmpty()) {
            return null;
        }
        return tableMetas.get(normalizeColumnName(columnName));
    }

    private Map<String, Map<String, SemanticAnnotationMeta>> loadSemanticAnnotationMetas() {
        Map<String, Map<String, SemanticAnnotationMeta>> annotationMetas = new LinkedHashMap<>();
        try {
            ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
            scanner.addIncludeFilter(new AnnotationTypeFilter(TableName.class));
            for (BeanDefinition beanDefinition : scanner.findCandidateComponents("com.fastbee")) {
                registerSemanticAnnotationMeta(annotationMetas, beanDefinition.getBeanClassName());
            }
        } catch (Exception ignored) {
            // 注解扫描只是语义模板导出的增强能力，失败时继续走数据库结构默认导出。
        }
        return annotationMetas;
    }

    private void registerSemanticAnnotationMeta(Map<String, Map<String, SemanticAnnotationMeta>> annotationMetas,
                                                String className) {
        if (StringUtils.isBlank(className)) {
            return;
        }
        try {
            Class<?> entityClass = Class.forName(className);
            TableName tableName = entityClass.getAnnotation(TableName.class);
            if (tableName == null || StringUtils.isBlank(tableName.value())) {
                return;
            }
            Map<String, SemanticAnnotationMeta> fieldMetas =
                    annotationMetas.computeIfAbsent(normalizeTableName(tableName.value()), key -> new LinkedHashMap<>());
            for (Field field : entityClass.getDeclaredFields()) {
                AiSemanticField semanticField = field.getAnnotation(AiSemanticField.class);
                AiSemanticIgnore semanticIgnore = field.getAnnotation(AiSemanticIgnore.class);
                if (semanticField == null && semanticIgnore == null) {
                    continue;
                }
                fieldMetas.put(normalizeColumnName(resolveEntityColumnName(field)),
                        new SemanticAnnotationMeta(semanticField, semanticIgnore != null));
            }
        } catch (Exception ignored) {
            // 单个实体类加载失败不影响整体模板导出。
        }
    }

    private String resolveEntityColumnName(Field field) {
        TableField tableField = field.getAnnotation(TableField.class);
        if (tableField != null && StringUtils.isNotBlank(tableField.value())) {
            return tableField.value();
        }
        return camelToUnderline(field.getName());
    }

    private String camelToUnderline(String fieldName) {
        if (StringUtils.isBlank(fieldName)) {
            return "";
        }
        return fieldName.replaceAll("([a-z0-9])([A-Z])", "$1_$2").toLowerCase(Locale.ROOT);
    }

    private String mergeSemanticText(String first, String second) {
        LinkedHashSet<String> items = new LinkedHashSet<>();
        addSemanticText(items, first);
        addSemanticText(items, second);
        return String.join(";", items);
    }

    private void addSemanticText(Set<String> items, String text) {
        if (StringUtils.isBlank(text)) {
            return;
        }
        for (String item : text.split("[;；]+")) {
            if (StringUtils.isNotBlank(item)) {
                items.add(item.trim());
            }
        }
    }

    private String joinAnnotationValues(String[] values) {
        if (values == null || values.length == 0) {
            return "";
        }
        LinkedHashSet<String> items = new LinkedHashSet<>();
        for (String value : values) {
            addSemanticText(items, value);
        }
        return String.join(";", items);
    }

    private String resolveSemanticName(String columnName, String columnComment) {
        String cleanedComment = stripCommentDecorators(columnComment);
        if (StringUtils.isNotBlank(cleanedComment)) {
            return cleanedComment;
        }
        return trimToEmpty(columnName);
    }

    private String resolveSemanticType(GenTableColumn column) {
        String columnName = trimToEmpty(column.getColumnName()).toLowerCase(Locale.ROOT);
        if (RELATION_KEY_COLUMNS.contains(columnName) || columnName.endsWith("_id")) {
            return SEMANTIC_RELATION_KEY;
        }
        if (isEnumLikeColumn(columnName)) {
            return SEMANTIC_ENUM;
        }
        return SEMANTIC_DIMENSION;
    }

    private String resolveSourceType(GenTableColumn column, String valueMappings) {
        if (StringUtils.isNotBlank(column.getDictType())) {
            return SOURCE_DICT;
        }
        if (StringUtils.isNotBlank(valueMappings)) {
            return SOURCE_AUTO_COMMENT;
        }
        return SOURCE_MANUAL;
    }

    private String resolveSourceCode(GenTableColumn column) {
        return trimToEmpty(column.getDictType());
    }

    private String resolveRelationHints(String tableName, GenTableColumn column,
                                        Map<String, List<GenTableColumn>> tableColumnsMap) {
        String currentTable = normalizeTableName(tableName);
        String actualColumnName = normalizeColumnName(column.getColumnName());
        LinkedHashSet<String> hints = new LinkedHashSet<>();
        if ("tenant_id".equals(actualColumnName)) {
            hints.add("系统租户隔离字段，查询时自动追加租户过滤");
            return String.join(";", hints);
        }
        if ("dept_id".equals(actualColumnName)) {
            addJoinHint(hints, currentTable, actualColumnName, "sys_dept", tableColumnsMap);
            hints.add("机构数据范围字段，查询时可按机构过滤");
            return String.join(";", hints);
        }
        if ("serial_number".equals(actualColumnName)) {
            addJoinHint(hints, currentTable, actualColumnName, "iot_device", tableColumnsMap);
            if (containsTable(tableColumnsMap, "iot_device")) {
                hints.add("可通过 iot_device.product_id=iot_product.product_id 继续关联产品");
            }
            return String.join(";", hints);
        }
        addPreferredRelationHint(hints, currentTable, actualColumnName, tableColumnsMap);
        addUniquePrimaryKeyRelationHint(hints, currentTable, actualColumnName, tableColumnsMap);
        addSuffixRelationHint(hints, currentTable, actualColumnName, tableColumnsMap);
        if ("product_id".equals(actualColumnName) && containsTable(tableColumnsMap, "iot_product")) {
            hints.add("可通过 iot_product.product_id=iot_things_model.product_id 继续关联物模型");
        }
        return String.join(";", hints);
    }

    private void addPreferredRelationHint(Set<String> hints, String currentTable, String columnName,
                                          Map<String, List<GenTableColumn>> tableColumnsMap) {
        String targetTable = PREFERRED_RELATION_TABLES.get(columnName);
        addJoinHint(hints, currentTable, columnName, targetTable, tableColumnsMap);
    }

    private void addUniquePrimaryKeyRelationHint(Set<String> hints, String currentTable, String columnName,
                                                 Map<String, List<GenTableColumn>> tableColumnsMap) {
        List<String> candidates = tableColumnsMap.entrySet().stream()
                .filter(entry -> !entry.getKey().equals(currentTable))
                .filter(entry -> hasPrimaryKeyColumn(entry.getValue(), columnName))
                .map(Map.Entry::getKey)
                .toList();
        if (candidates.size() == 1) {
            addJoinHint(hints, currentTable, columnName, candidates.get(0), tableColumnsMap);
        }
    }

    private void addSuffixRelationHint(Set<String> hints, String currentTable, String columnName,
                                       Map<String, List<GenTableColumn>> tableColumnsMap) {
        if (!columnName.endsWith("_id")) {
            return;
        }
        String suffix = columnName.substring(0, columnName.length() - 3);
        List<String> candidates = tableColumnsMap.keySet().stream()
                .filter(table -> !table.equals(currentTable))
                .filter(table -> table.equals(suffix) || table.endsWith("_" + suffix))
                .filter(table -> hasColumn(tableColumnsMap.get(table), columnName))
                .toList();
        if (candidates.size() == 1) {
            addJoinHint(hints, currentTable, columnName, candidates.get(0), tableColumnsMap);
        }
    }

    private void addJoinHint(Set<String> hints, String currentTable, String columnName, String targetTable,
                             Map<String, List<GenTableColumn>> tableColumnsMap) {
        if (StringUtils.isBlank(targetTable) || StringUtils.equals(currentTable, targetTable)) {
            return;
        }
        if (!containsTable(tableColumnsMap, targetTable) || !hasColumn(tableColumnsMap.get(targetTable), columnName)) {
            return;
        }
        hints.add(currentTable + "." + columnName + "=" + targetTable + "." + columnName);
    }

    private boolean containsTable(Map<String, List<GenTableColumn>> tableColumnsMap, String tableName) {
        return tableColumnsMap.containsKey(normalizeTableName(tableName));
    }

    private boolean hasPrimaryKeyColumn(List<GenTableColumn> columns, String columnName) {
        if (columns == null || columns.isEmpty()) {
            return false;
        }
        return columns.stream().anyMatch(column ->
                StringUtils.equalsIgnoreCase(trimToEmpty(column.getColumnName()), columnName) && column.isPk());
    }

    private boolean hasColumn(List<GenTableColumn> columns, String columnName) {
        if (columns == null || columns.isEmpty()) {
            return false;
        }
        return columns.stream().anyMatch(column ->
                StringUtils.equalsIgnoreCase(trimToEmpty(column.getColumnName()), columnName));
    }

    private String normalizeTableName(String tableName) {
        return trimToEmpty(tableName).toLowerCase(Locale.ROOT);
    }

    private String normalizeColumnName(String columnName) {
        return trimToEmpty(columnName).toLowerCase(Locale.ROOT);
    }

    private String resolveValueMappings(GenTableColumn column) {
        String comment = trimToEmpty(column.getColumnComment());
        if (StringUtils.isBlank(comment)) {
            return "";
        }
        GenTableColumn helper = new GenTableColumn();
        helper.setColumnComment(comment);
        String converterExp = trimToEmpty(helper.readConverterExp());
        if (StringUtils.isBlank(converterExp) || comment.equals(converterExp) || !converterExp.contains("=")) {
            return "";
        }
        List<String> mappingItems = new ArrayList<>();
        for (String item : converterExp.split(",")) {
            if (StringUtils.isBlank(item) || !item.contains("=")) {
                continue;
            }
            String[] pair = item.split("=", 2);
            String left = trimToEmpty(pair[0]);
            String right = trimToEmpty(pair[1]);
            if (StringUtils.isBlank(left) || StringUtils.isBlank(right)) {
                continue;
            }
            if (looksLikeCode(left) && !looksLikeCode(right)) {
                mappingItems.add(right + "=" + left);
                continue;
            }
            mappingItems.add(left + "=" + right);
        }
        return String.join(";", mappingItems);
    }

    private String resolveAliases(String tableName, String tableComment, GenTableColumn column) {
        String actualTableName = normalizeTableName(tableName);
        String actualColumnName = normalizeColumnName(column.getColumnName());
        LinkedHashSet<String> aliases = new LinkedHashSet<>();
        String semanticName = stripCommentDecorators(column.getColumnComment());
        addTableBusinessAliases(aliases, actualTableName, tableComment);
        addSemanticText(aliases, semanticName);
        addColumnAliasRules(aliases, actualTableName, actualColumnName);
        if (actualColumnName.endsWith("_name") && StringUtils.isNotBlank(semanticName)
                && !semanticName.endsWith("名称") && !semanticName.endsWith("名")) {
            addSemanticText(aliases, semanticName + "名称");
            addSemanticText(aliases, semanticName + "名");
        }
        if ("status".equals(actualColumnName) || actualColumnName.endsWith("_status")) {
            addSemanticText(aliases, "状态;状态值");
        }
        if ("type".equals(actualColumnName) || actualColumnName.endsWith("_type")) {
            addSemanticText(aliases, "类型;分类");
        }
        if ("create_time".equals(actualColumnName)) {
            addSemanticText(aliases, "创建时间;新增时间;添加时间");
        }
        if ("update_time".equals(actualColumnName)) {
            addSemanticText(aliases, "更新时间;修改时间;最近更新时间");
        }
        return String.join(";", aliases);
    }

    private void addTableBusinessAliases(Set<String> aliases, String tableName, String tableComment) {
        String objectName = resolveBusinessObjectName(tableName, tableComment);
        addSemanticText(aliases, objectName);
        addSemanticText(aliases, stripBusinessObjectSuffix(objectName));
        addSemanticText(aliases, stripCommentDecorators(tableComment));
        addSemanticText(aliases, stripBusinessObjectSuffix(stripCommentDecorators(tableComment)));
    }

    private String stripBusinessObjectSuffix(String text) {
        String actualText = trimToEmpty(text);
        if (StringUtils.isBlank(actualText)) {
            return "";
        }
        for (String suffix : List.of("信息表", "数据表", "记录表", "日志表", "明细表", "关联表", "配置表", "管理表", "主表", "表")) {
            if (actualText.endsWith(suffix) && actualText.length() > suffix.length() + 1) {
                return actualText.substring(0, actualText.length() - suffix.length()).trim();
            }
        }
        return actualText;
    }

    private void addColumnAliasRules(Set<String> aliases, String tableName, String columnName) {
        if ("iot_product".equals(tableName) && ("product_id".equals(columnName) || "product_name".equals(columnName))) {
            addSemanticText(aliases, "产品;产品信息;产品名称;产品名");
        }
        if ("iot_device".equals(tableName) && ("device_id".equals(columnName) || "device_name".equals(columnName))) {
            addSemanticText(aliases, "设备;设备信息;设备名称;设备名");
        }
        if ("iot_device".equals(tableName) && "product_id".equals(columnName)) {
            addSemanticText(aliases, "所属产品;设备所属产品;产品ID");
        }
        if ("serial_number".equals(columnName)) {
            addSemanticText(aliases, "设备编号;设备编码;设备序列号;序列号;SN");
        }
        if ("tenant_id".equals(columnName) || "tenant_name".equals(columnName)) {
            addSemanticText(aliases, "租户;租户名称;租户编号");
        }
        if ("dept_id".equals(columnName) || "dept_name".equals(columnName)) {
            addSemanticText(aliases, "机构;部门;组织");
        }
        if ("user_id".equals(columnName) || "user_name".equals(columnName)) {
            addSemanticText(aliases, "用户;账号;人员");
        }
    }

    private String resolveQueryHints(String tableName, String tableComment, GenTableColumn column) {
        LinkedHashSet<String> hints = new LinkedHashSet<>();
        String actualTableName = normalizeTableName(tableName);
        String actualColumnName = normalizeColumnName(column.getColumnName());
        addSemanticText(hints, resolveBasicQueryHints(actualColumnName));
        addSemanticText(hints, resolveBusinessQueryHints(actualTableName, actualColumnName));
        if (StringUtils.isNotBlank(column.getDictType())) {
            addSemanticText(hints, "自然语言状态/类型需先按系统字典 " + column.getDictType().trim() + " 转换为真实值");
        }
        if (isEnumLikeColumn(actualColumnName)) {
            addSemanticText(hints, "枚举、状态、启停类问法必须先确认值映射，不能直接使用中文入 SQL");
        }
        if ("del_flag".equals(actualColumnName)) {
            addSemanticText(hints, "默认排除逻辑删除数据，除非用户明确查询已删除数据");
        }
        if ("create_time".equals(actualColumnName) || "update_time".equals(actualColumnName)) {
            addSemanticText(hints, "时间范围问法需转换为明确起止时间");
        }
        return String.join(";", hints);
    }

    private String resolveBasicQueryHints(String columnName) {
        String actualColumnName = trimToEmpty(columnName).toLowerCase(Locale.ROOT);
        if ("tenant_id".equals(actualColumnName) || "dept_id".equals(actualColumnName) || "user_id".equals(actualColumnName)) {
            return "auto-data-scope";
        }
        if ("serial_number".equals(actualColumnName)) {
            return "device-number";
        }
        if ("status".equals(actualColumnName)) {
            return "check-value-mapping";
        }
        return "";
    }

    private String resolveBusinessQueryHints(String tableName, String columnName) {
        if ("iot_product".equals(tableName) && ("product_id".equals(columnName) || "product_name".equals(columnName))) {
            return "产品主事实表字段；统计产品数量时优先使用 iot_product，不使用 iot_device.product_id 去重替代";
        }
        if ("iot_device".equals(tableName) && "product_id".equals(columnName)) {
            return "设备所属产品字段；仅在按产品筛选、分组或统计有设备的产品时使用，不代表产品总数";
        }
        if ("iot_device".equals(tableName) && ("device_id".equals(columnName) || "device_name".equals(columnName))) {
            return "设备主事实表字段；统计设备数量时优先使用 iot_device，不使用日志表或关联表去重替代";
        }
        if ("iot_things_model".equals(tableName)) {
            return "物模型定义字段；设备当前值、历史值和统计值应结合设备元数据、产品物模型、Redis 或 TSDB 运行时链路";
        }
        if ((tableName.contains("_log") || tableName.endsWith("_record")) && columnName.endsWith("_id")) {
            return "日志/记录表关联字段；统计主对象总量时不要从日志或记录表去重";
        }
        if ((tableName.endsWith("_user") || tableName.endsWith("_authorize") || tableName.endsWith("_share"))
                && columnName.endsWith("_id")) {
            return "关联表字段；仅在问句明确要求绑定、授权、分享、拥有关系时使用";
        }
        return "";
    }

    private String resolveRemark(String tableName, String tableComment, GenTableColumn column) {
        String actualTableName = normalizeTableName(tableName);
        String actualColumnName = normalizeColumnName(column.getColumnName());
        LinkedHashSet<String> remarks = new LinkedHashSet<>();
        addSemanticText(remarks, "自动预填：来源=数据库表结构、代码生成器元数据、字段注释和内置业务规则");
        addSemanticText(remarks, "发布前建议结合企业二开字段、业务口径和黄金问句回归复核");
        String tableRole = resolveTableRole(actualTableName);
        if (!"主表".equals(tableRole)) {
            addSemanticText(remarks, "当前字段所属表角色=" + tableRole + "，不能默认替代业务对象主事实表");
        }
        if ("product_id".equals(actualColumnName) && !"iot_product".equals(actualTableName)) {
            addSemanticText(remarks, "该字段通常表示所属产品或产品关联，不等于产品主数据总量");
        }
        if ("serial_number".equals(actualColumnName)) {
            addSemanticText(remarks, "设备编号通常适合精确匹配，避免仅按设备名称模糊匹配造成歧义");
        }
        return String.join(";", remarks);
    }

    private boolean isEnumLikeColumn(String columnName) {
        return "status".equals(columnName)
                || "type".equals(columnName)
                || columnName.startsWith("is_")
                || columnName.startsWith("enable")
                || columnName.endsWith("_status")
                || columnName.endsWith("_type")
                || columnName.endsWith("_flag");
    }

    private String stripCommentDecorators(String comment) {
        String actualComment = trimToEmpty(comment);
        if (StringUtils.isBlank(actualComment)) {
            return "";
        }
        actualComment = actualComment.replaceAll("[（(].*$", "");
        actualComment = actualComment.replaceAll("[：:].*$", "");
        return actualComment.trim();
    }

    private boolean looksLikeCode(String value) {
        if (StringUtils.isBlank(value)) {
            return false;
        }
        String actualValue = value.trim();
        return actualValue.matches("-?\\d+(\\.\\d+)?")
                || actualValue.matches("[A-Z0-9_\\-]+")
                || "true".equalsIgnoreCase(actualValue)
                || "false".equalsIgnoreCase(actualValue);
    }

    private record SemanticAnnotationMeta(AiSemanticField semanticField, boolean ignore) {
    }

    private String resolveTemplateCode(String kbType) {
        if (KB_TYPE_NL2SQL.equals(kbType)) {
            return "NL2SQL_SEMANTIC_EXCEL";
        }
        if (KB_TYPE_PROTOCOL.equals(kbType)) {
            return "PROTOCOL_SPEC_EXCEL";
        }
        return "PLATFORM_DOC_EXCEL";
    }

    private String resolveTemplateVersion(String kbType) {
        if (KB_TYPE_PLATFORM_DOC.equals(kbType)) {
            return TEMPLATE_VERSION_V2;
        }
        return TEMPLATE_VERSION_V1;
    }

    private String resolveParserType(String kbType) {
        if (KB_TYPE_NL2SQL.equals(kbType)) {
            return "EXCEL_NL2SQL_SEMANTIC";
        }
        if (KB_TYPE_PROTOCOL.equals(kbType)) {
            return "EXCEL_PROTOCOL_SPEC";
        }
        return "EXCEL_PLATFORM_DOC";
    }

    private String normalizeTemplateMode(String templateMode) {
        if (StringUtils.isBlank(templateMode)) {
            return TEMPLATE_MODE_EMPTY;
        }
        String actualMode = templateMode.trim().toUpperCase(Locale.ROOT);
        if (TEMPLATE_MODE_ENTERPRISE_EXPORT.equals(actualMode)
                || TEMPLATE_MODE_ENTERPRISE_DOC_EXPORT.equals(actualMode)) {
            return TEMPLATE_MODE_ENTERPRISE_EXPORT;
        }
        return TEMPLATE_MODE_EMPTY;
    }

    private String normalizeSourceStrategy(String sourceStrategy) {
        if (StringUtils.isBlank(sourceStrategy)) {
            return SOURCE_STRATEGY_AUTO;
        }
        String actualStrategy = sourceStrategy.trim().toUpperCase(Locale.ROOT);
        if (SOURCE_STRATEGY_LOCAL.equals(actualStrategy) || SOURCE_STRATEGY_WEB.equals(actualStrategy)) {
            return actualStrategy;
        }
        return SOURCE_STRATEGY_AUTO;
    }

    private String actualOrDefault(String value, String defaultValue) {
        return StringUtils.isBlank(value) ? defaultValue : value;
    }

    private String resolveVectorStoreType(AiKnowledgeBase knowledgeBase) {
        return StringUtils.isBlank(knowledgeBase.getVectorStoreType()) ? "REDIS" : knowledgeBase.getVectorStoreType();
    }

    private String normalizeDocumentSourceOrigin(String sourceOrigin) {
        if (StringUtils.isBlank(sourceOrigin)) {
            return DOCUMENT_SOURCE_CUSTOM;
        }
        String actualSourceOrigin = sourceOrigin.trim().toUpperCase(Locale.ROOT);
        if (DOCUMENT_SOURCE_OFFICIAL.equals(actualSourceOrigin)) {
            return DOCUMENT_SOURCE_OFFICIAL;
        }
        return DOCUMENT_SOURCE_CUSTOM;
    }

    private String normalizeDocumentAppVersion(String appVersion) {
        return StringUtils.isBlank(appVersion) ? "" : appVersion.trim();
    }

    private Integer resolveDocumentSortNum(Long knowledgeBaseId, String sourceOrigin, Integer sortNum) {
        if (sortNum != null) {
            if (sortNum < 0) {
                throw new ServiceException(message("ai.knowledge.template.sort.order.negative"));
            }
            return sortNum;
        }
        return resolveRecommendedDocumentSortNum(knowledgeBaseId, sourceOrigin);
    }

    private int resolveRecommendedDocumentSortNum(Long knowledgeBaseId, String sourceOrigin) {
        if (knowledgeBaseId == null) {
            return DEFAULT_DOCUMENT_SORT_NUM;
        }
        AiKnowledgeDocument query = new AiKnowledgeDocument();
        query.setKnowledgeBaseId(knowledgeBaseId);
        List<AiKnowledgeDocument> documents = aiKnowledgeDocumentService.listAiKnowledgeDocument(query);
        if (DOCUMENT_SOURCE_OFFICIAL.equals(sourceOrigin)) {
            int maxOfficialSortNum = documents.stream()
                    .filter(item -> item != null
                            && item.getSortNum() != null
                            && StringUtils.isNotBlank(item.getSourceOrigin())
                            && DOCUMENT_SOURCE_OFFICIAL.equalsIgnoreCase(item.getSourceOrigin().trim()))
                    .map(AiKnowledgeDocument::getSortNum)
                    .max(Integer::compareTo)
                    .orElse(OFFICIAL_DOCUMENT_SORT_BASE - DOCUMENT_SORT_STEP);
            return Math.max(maxOfficialSortNum + DOCUMENT_SORT_STEP, OFFICIAL_DOCUMENT_SORT_BASE);
        }
        int maxSortNum = documents.stream()
                .filter(item -> item != null && item.getSortNum() != null)
                .map(AiKnowledgeDocument::getSortNum)
                .max(Integer::compareTo)
                .orElse(CUSTOM_DOCUMENT_SORT_BASE - DOCUMENT_SORT_STEP);
        return Math.max(maxSortNum + DOCUMENT_SORT_STEP, CUSTOM_DOCUMENT_SORT_BASE);
    }

    private AiKnowledgeBase requireKnowledgeBase(Long knowledgeBaseId) {
        AiKnowledgeBase query = new AiKnowledgeBase();
        query.setKnowledgeBaseId(knowledgeBaseId);
        AiKnowledgeBase knowledgeBase = aiKnowledgeBaseService.selectAiKnowledgeBase(query);
        if (knowledgeBase == null) {
            throw new ServiceException("knowledge base not found or no permission");
        }
        return knowledgeBase;
    }

    private void validateExcelFile(MultipartFile file) {
        String fileName = StringUtils.isBlank(file.getOriginalFilename()) ? "" : file.getOriginalFilename().toLowerCase(Locale.ROOT);
        if (fileName.endsWith(".sql")) {
            throw new ServiceException(message("ai.knowledge.template.sql.upload.deprecated"));
        }
        if (!(fileName.endsWith(".xls") || fileName.endsWith(".xlsx"))) {
            throw new ServiceException(message("ai.knowledge.template.excel.only"));
        }
    }

    private Path saveTemplateFile(AiKnowledgeBase knowledgeBase, String kbType, String originalFilename, byte[] fileBytes) throws IOException {
        String suffix = getFileSuffix(originalFilename);
        String safeCode = StringUtils.isBlank(knowledgeBase.getKbCode()) ? "knowledge" : knowledgeBase.getKbCode().trim().toLowerCase(Locale.ROOT);
        Path targetDir = Paths.get(RuoYiConfig.getProfile(), "ai", "knowledge", "template", safeCode, kbType.toLowerCase(Locale.ROOT));
        Files.createDirectories(targetDir);
        String targetName = System.currentTimeMillis() + "_" + UUID.randomUUID().toString().replace("-", "") + suffix;
        Path targetFile = targetDir.resolve(targetName);
        Files.write(targetFile, fileBytes);
        return targetFile;
    }

    private Path saveSnapshotFile(AiKnowledgeBase knowledgeBase, String versionNo, JSONObject snapshot) throws IOException {
        String safeCode = StringUtils.isBlank(knowledgeBase.getKbCode()) ? "knowledge" : knowledgeBase.getKbCode().trim().toLowerCase(Locale.ROOT);
        Path targetDir = Paths.get(RuoYiConfig.getProfile(), "ai", "knowledge", "snapshot", safeCode);
        Files.createDirectories(targetDir);
        Path targetFile = targetDir.resolve(versionNo + ".json");
        Files.writeString(targetFile, JSON.toJSONString(snapshot), StandardCharsets.UTF_8);
        return targetFile;
    }

    private String buildDocumentSnapshotCode(Long documentId) {
        return "DOC_" + documentId + "_" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
    }

    private String buildParsedSummary(String kbType, JSONObject snapshot) {
        int rowCount = snapshot.getIntValue("rowCount");
        if (KB_TYPE_NL2SQL.equals(kbType)) {
            String summary = "已解析 " + rowCount + " 条问数语义记录，覆盖 "
                    + snapshot.getIntValue("tableCount") + " 张表、" + snapshot.getIntValue("fieldCount") + " 个字段";
            int warningCount = snapshot.getIntValue("validationWarningCount");
            if (warningCount > 0) {
                summary += "，包含 " + warningCount + " 条校验告警";
            }
            return summary;
        }
        if (KB_TYPE_PROTOCOL.equals(kbType)) {
            return "已解析 " + rowCount + " 条协议记录，覆盖 " + snapshot.getIntValue("moduleCount") + " 个模块";
        }
        return "已解析 " + rowCount + " 条平台知识记录";
    }

    private String buildTableColumnKey(String tableName, String columnName) {
        String actualTableName = StringUtils.isBlank(tableName) ? "*" : tableName.trim();
        String actualColumnName = StringUtils.isBlank(columnName) ? "*" : columnName.trim();
        return actualTableName + "." + actualColumnName;
    }

    private String getFileSuffix(String originalFilename) {
        if (StringUtils.isBlank(originalFilename) || !originalFilename.contains(".")) {
            return ".xlsx";
        }
        return originalFilename.substring(originalFilename.lastIndexOf('.'));
    }

    private String trimReason(String reason) {
        if (reason == null) {
            return "";
        }
        String actualReason = reason.trim();
        return actualReason.length() > 500 ? actualReason.substring(0, 500) : actualReason;
    }

    private void updateDocumentParsing(Long documentId) {
        AiKnowledgeDocument updateDocument = new AiKnowledgeDocument();
        updateDocument.setDocumentId(documentId);
        updateDocument.setParseStatus(DOCUMENT_STATUS_PARSING);
        updateDocument.setChunkCount(0);
        updateDocument.setParsedSummary("正在解析文档...");
        updateDocument.setParsedSnapshotPath("");
        updateDocument.setUpdateBy(AiSecuritySupport.resolveUsername());
        updateDocument.setUpdateTime(AiSecuritySupport.now());
        aiKnowledgeDocumentService.updateById(updateDocument);
    }

    private void updateDocumentParseSuccess(Long documentId, Integer rowCount, String snapshotPath, String parsedSummary) {
        AiKnowledgeDocument updateDocument = new AiKnowledgeDocument();
        updateDocument.setDocumentId(documentId);
        updateDocument.setParseStatus(DOCUMENT_STATUS_SUCCESS);
        updateDocument.setChunkCount(rowCount == null ? 0 : rowCount);
        updateDocument.setParsedSnapshotPath(snapshotPath);
        updateDocument.setParsedSummary(parsedSummary);
        updateDocument.setUpdateBy(AiSecuritySupport.resolveUsername());
        updateDocument.setUpdateTime(AiSecuritySupport.now());
        aiKnowledgeDocumentService.updateById(updateDocument);
    }

    private void updateDocumentParseFailure(Long documentId, String failedReason, String snapshotPath) {
        AiKnowledgeDocument updateDocument = new AiKnowledgeDocument();
        updateDocument.setDocumentId(documentId);
        updateDocument.setParseStatus(DOCUMENT_STATUS_FAILED);
        updateDocument.setChunkCount(0);
        updateDocument.setParsedSnapshotPath(StringUtils.isBlank(snapshotPath) ? "" : snapshotPath);
        updateDocument.setParsedSummary(trimReason(failedReason));
        updateDocument.setUpdateBy(AiSecuritySupport.resolveUsername());
        updateDocument.setUpdateTime(AiSecuritySupport.now());
        aiKnowledgeDocumentService.updateById(updateDocument);
    }

    private AiKnowledgeDocument requireKnowledgeDocument(Long documentId) {
        if (documentId == null) {
            throw new ServiceException(message("ai.knowledge.document.id.required"));
        }
        AiKnowledgeDocument document = aiKnowledgeDocumentService.selectAiKnowledgeDocument(documentId);
        if (document == null) {
            throw new ServiceException(message("ai.knowledge.document.not.exists.or.deleted"));
        }
        return document;
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}
