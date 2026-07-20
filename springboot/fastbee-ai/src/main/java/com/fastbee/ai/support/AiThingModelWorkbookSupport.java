package com.fastbee.ai.support;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import com.fastbee.common.utils.StringUtils;

/**
 * FastBee 物模型导入模板工作簿支撑。
 */
@Component
public class AiThingModelWorkbookSupport {

    public static final String DEFAULT_THING_MODEL_IMPORT_SHEET_NAME = "物模型导入模板";

    private static final List<String> THING_MODEL_TEMPLATE_KEYS = List.of(
            "modelName", "modelName_en_US", "identifier", "datatype", "formula", "modelOrder",
            "unit", "limitValue", "typeStr", "isChartStr", "isMonitorStr", "isHistoryStr",
            "isReadonlyStr", "isSharePermStr"
    );

    private static final List<String> THING_MODEL_TEMPLATE_HEADERS = List.of(
            "物模型名称", "英文物模型名称", "标识符", "数据类型", "计算公式", "排序值",
            "单位", "有效值范围", "模型类别", "是否图表展示", "是否实时监测", "是否历史存储",
            "是否只读数据", "是否设备分享权限"
    );

    /**
     * 生成单 Sheet 物模型导入模板工作簿。
     *
     * @param mappings 物模型映射行
     * @return xlsx 字节
     */
    public byte[] buildThingModelImportWorkbookBytes(JSONArray mappings) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            appendThingModelSheet(workbook, DEFAULT_THING_MODEL_IMPORT_SHEET_NAME, mappings);
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    /**
     * 向工作簿追加 FastBee 物模型模板 Sheet。
     *
     * @param workbook 工作簿
     * @param sheetName Sheet 名
     * @param mappings 物模型映射行
     */
    public void appendThingModelSheet(Workbook workbook, String sheetName, JSONArray mappings) {
        appendJsonArraySheetWithLabels(workbook,
                StringUtils.isBlank(sheetName) ? DEFAULT_THING_MODEL_IMPORT_SHEET_NAME : sheetName,
                THING_MODEL_TEMPLATE_KEYS,
                THING_MODEL_TEMPLATE_HEADERS,
                normalizeThingModelMappingsForWorkbook(mappings));
    }

    /**
     * 规范化 AI 或协议 DSL 中的物模型映射，保证导出的模板字段完整。
     *
     * @param mappings 原始物模型映射
     * @return 规范化行
     */
    public JSONArray normalizeThingModelMappingsForWorkbook(JSONArray mappings) {
        JSONArray normalizedRows = new JSONArray();
        JSONArray actualMappings = mappings == null ? new JSONArray() : mappings;
        for (int index = 0; index < actualMappings.size(); index++) {
            Object item = actualMappings.get(index);
            if (!(item instanceof JSONObject source)) {
                continue;
            }
            JSONObject row = new JSONObject();
            row.putAll(source);

            String identifier = getFirstNonBlankString(row, "identifier");
            String datatype = normalizeThingModelDataType(getFirstNonBlankString(row, "datatype", "dataType", "valueType"));
            if (StringUtils.isNotBlank(datatype)) {
                row.put("datatype", datatype);
            }
            putIfBlank(row, "modelName", getFirstNonBlankString(row, "displayName", "modelName_zh_CN", "fieldName", "identifier"));
            putIfBlank(row, "modelName_en_US", identifier);
            putIfBlank(row, "modelType", normalizeThingModelModelType(row));
            putIfBlank(row, "typeStr", defaultIfBlank(normalizeThingModelTypeStr(row), "属性"));
            putIfBlank(row, "unit", inferThingModelUnitFromSpecs(getFirstNonBlankString(row, "specs")));
            putIfBlank(row, "limitValue", inferThingModelLimitValueFromSpecs(getFirstNonBlankString(row, "specs"), datatype));
            putIfBlank(row, "isChartStr", normalizeThingModelBoolText(getFirstNonBlankString(row, "isChart"),
                    defaultThingModelFlag(row, "isChartStr")));
            putIfBlank(row, "isMonitorStr", normalizeThingModelBoolText(getFirstNonBlankString(row, "isMonitor"),
                    defaultThingModelFlag(row, "isMonitorStr")));
            putIfBlank(row, "isHistoryStr", normalizeThingModelBoolText(getFirstNonBlankString(row, "isHistory"),
                    defaultThingModelFlag(row, "isHistoryStr")));
            putIfBlank(row, "isReadonlyStr", normalizeThingModelBoolText(getFirstNonBlankString(row, "isReadonly"),
                    defaultIfBlank(inferReadonlyText(getFirstNonBlankString(row, "accessMode")),
                            defaultThingModelFlag(row, "isReadonlyStr"))));
            putIfBlank(row, "isSharePermStr", normalizeThingModelBoolText(getFirstNonBlankString(row, "isSharePerm"),
                    defaultThingModelFlag(row, "isSharePermStr")));
            putIfBlank(row, "specs", buildThingModelSpecs(row));
            normalizedRows.add(row);
        }
        return normalizedRows;
    }

    /**
     * 规范化从工作簿回填的物模型模板行，供协议适配导入复用。
     *
     * @param rawRows 原始工作簿行
     * @param fields 协议字段定义行
     * @return DSL 物模型映射行
     */
    public JSONArray normalizeThingModelMappingsFromWorkbook(JSONArray rawRows, JSONArray fields) {
        JSONArray normalizedRows = new JSONArray();
        JSONArray actualRows = rawRows == null ? new JSONArray() : rawRows;
        for (int index = 0; index < actualRows.size(); index++) {
            Object item = actualRows.get(index);
            if (!(item instanceof JSONObject source)) {
                continue;
            }
            String identifier = getFirstNonBlankString(source, "identifier", "标识符");
            String modelName = getFirstNonBlankString(source, "modelName", "物模型名称", "displayName", "modelName_zh_CN");
            if (StringUtils.isBlank(identifier) && StringUtils.isBlank(modelName)) {
                continue;
            }

            JSONObject matchedField = findMatchedProtocolField(fields, identifier, modelName);
            JSONObject row = new JSONObject();
            row.put("identifier", identifier);
            row.put("modelName", modelName);
            row.put("displayName", modelName);
            row.put("modelName_en_US", getFirstNonBlankString(source, "modelName_en_US", "英文物模型名称"));
            row.put("datatype", normalizeThingModelDataType(getFirstNonBlankString(source, "datatype", "dataType", "数据类型", "valueType")));
            row.put("formula", getFirstNonBlankString(source, "formula", "计算公式"));
            row.put("modelOrder", getFirstNonBlankString(source, "modelOrder", "排序值"));
            row.put("unit", getFirstNonBlankString(source, "unit", "单位"));
            row.put("limitValue", getFirstNonBlankString(source, "limitValue", "有效值范围"));
            row.put("typeStr", getFirstNonBlankString(source, "typeStr", "模型类别"));
            row.put("modelType", normalizeThingModelModelType(row));
            row.put("isChartStr", normalizeThingModelBoolText(getFirstNonBlankString(source, "isChartStr", "是否图表展示"), ""));
            row.put("isMonitorStr", normalizeThingModelBoolText(getFirstNonBlankString(source, "isMonitorStr", "是否实时监测"), ""));
            row.put("isHistoryStr", normalizeThingModelBoolText(getFirstNonBlankString(source, "isHistoryStr", "是否历史存储"), ""));
            row.put("isReadonlyStr", normalizeThingModelBoolText(getFirstNonBlankString(source, "isReadonlyStr", "是否只读数据"), ""));
            row.put("isSharePermStr", normalizeThingModelBoolText(getFirstNonBlankString(source, "isSharePermStr", "是否设备分享权限"), ""));

            String sourceField = getFirstNonBlankString(source, "sourceField", "来源字段", "协议字段", "报文字段", "jsonPath", "JSON路径");
            if (StringUtils.isBlank(sourceField) && matchedField != null) {
                sourceField = getFirstNonBlankString(matchedField, "jsonPath", "sourceField", "fieldCode", "fieldName");
            }
            row.put("sourceField", defaultIfBlank(sourceField, identifier));
            if (matchedField != null) {
                row.put("messageType", getFirstNonBlankString(matchedField, "messageType"));
            }
            putIfBlank(row, "modelName_en_US", identifier);
            putIfBlank(row, "typeStr", defaultIfBlank(normalizeThingModelTypeStr(row), "属性"));
            putIfBlank(row, "specs", buildThingModelSpecs(row));
            normalizedRows.add(row);
        }
        return normalizedRows;
    }

    /**
     * 根据模板字段构建 FastBee 物模型 specs JSON。
     *
     * @param row 模板行
     * @return specs JSON 字符串
     */
    public String buildThingModelSpecs(JSONObject row) {
        String datatype = normalizeThingModelDataType(getFirstNonBlankString(row, "datatype", "dataType", "valueType"));
        if (StringUtils.isBlank(datatype)) {
            return "";
        }
        JSONObject specs = new JSONObject();
        specs.put("type", datatype);
        String unit = getFirstNonBlankString(row, "unit");
        String limitValue = getFirstNonBlankString(row, "limitValue");
        switch (datatype) {
            case "integer":
            case "decimal":
                if (StringUtils.isNotBlank(unit)) {
                    specs.put("unit", unit);
                }
                String[] range = splitLimitValue(limitValue);
                if (range.length >= 2) {
                    specs.put("min", range[0]);
                    specs.put("max", range[1]);
                }
                putJsonIfNotBlank(specs, "step", getFirstNonBlankString(row, "step"));
                break;
            case "bool":
                String[] boolTexts = splitLimitValue(limitValue);
                if (boolTexts.length >= 2) {
                    specs.put("falseText", boolTexts[0]);
                    specs.put("trueText", boolTexts[1]);
                }
                putJsonIfNotBlank(specs, "falseText", getFirstNonBlankString(row, "falseText"));
                putJsonIfNotBlank(specs, "trueText", getFirstNonBlankString(row, "trueText"));
                break;
            case "string":
                String maxLength = getFirstNonBlankString(row, "maxLength");
                if (StringUtils.isBlank(maxLength)) {
                    String[] stringRange = splitLimitValue(limitValue);
                    if (stringRange.length >= 2) {
                        maxLength = stringRange[1];
                    }
                }
                putJsonIfNotBlank(specs, "maxLength", maxLength);
                break;
            case "array":
                putJsonIfNotBlank(specs, "arrayType", getFirstNonBlankString(row, "arrayType"));
                break;
            case "enum":
                JSONArray enumList = parseThingModelEnumList(limitValue);
                if (!enumList.isEmpty()) {
                    specs.put("showWay", "select");
                    specs.put("enumList", enumList);
                }
                break;
            default:
                break;
        }
        return JSON.toJSONString(specs);
    }

    public String normalizeThingModelDataType(String value) {
        String normalized = trimToEmpty(value).toLowerCase(Locale.ROOT);
        switch (normalized) {
            case "int":
            case "integer":
            case "long":
            case "short":
                return "integer";
            case "decimal":
            case "double":
            case "float":
            case "number":
            case "numeric":
                return "decimal";
            case "boolean":
            case "bool":
                return "bool";
            case "string":
            case "text":
            case "varchar":
                return "string";
            case "array":
                return "array";
            case "enum":
                return "enum";
            default:
                return StringUtils.isBlank(value) ? "" : value.trim();
        }
    }

    private JSONObject findMatchedProtocolField(JSONArray fields, String identifier, String modelName) {
        JSONArray actualFields = fields == null ? new JSONArray() : fields;
        for (int index = 0; index < actualFields.size(); index++) {
            Object item = actualFields.get(index);
            if (!(item instanceof JSONObject field)) {
                continue;
            }
            if (matchAnyIgnoreCase(identifier,
                    getFirstNonBlankString(field, "fieldName"),
                    getFirstNonBlankString(field, "fieldCode"),
                    getFirstNonBlankString(field, "identifier"),
                    getFirstNonBlankString(field, "sourceField"),
                    getFirstNonBlankString(field, "jsonPath"))
                    || matchAnyIgnoreCase(modelName,
                    getFirstNonBlankString(field, "displayName"),
                    getFirstNonBlankString(field, "fieldName"))) {
                return field;
            }
        }
        return null;
    }

    private boolean matchAnyIgnoreCase(String expected, String... candidates) {
        if (StringUtils.isBlank(expected) || candidates == null) {
            return false;
        }
        String normalizedExpected = expected.trim().toLowerCase(Locale.ROOT);
        for (String candidate : candidates) {
            if (StringUtils.isNotBlank(candidate) && normalizedExpected.equals(candidate.trim().toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }

    private String defaultThingModelFlag(JSONObject row, String key) {
        String typeStr = normalizeThingModelTypeStr(row);
        boolean property = StringUtils.isBlank(typeStr) || "属性".equals(typeStr);
        switch (key) {
            case "isChartStr":
            case "isMonitorStr":
                return property ? "是" : "否";
            case "isHistoryStr":
                return "是";
            case "isReadonlyStr":
                return property ? "是" : "否";
            case "isSharePermStr":
                return "否";
            default:
                return "";
        }
    }

    private String normalizeThingModelModelType(JSONObject row) {
        String modelType = getFirstNonBlankString(row, "modelType");
        if (StringUtils.isNotBlank(modelType)) {
            return modelType;
        }
        String typeText = normalizeText(getFirstNonBlankString(row, "typeStr", "type"));
        if (typeText.contains("属性") || typeText.contains("property") || "1".equals(typeText)) {
            return "PROPERTY";
        }
        if (typeText.contains("功能") || typeText.contains("服务") || typeText.contains("function")
                || typeText.contains("service") || "2".equals(typeText)) {
            return "FUNCTION";
        }
        if (typeText.contains("事件") || typeText.contains("event") || "3".equals(typeText)) {
            return "EVENT";
        }
        return "";
    }

    private String normalizeThingModelTypeStr(JSONObject row) {
        String typeText = normalizeText(getFirstNonBlankString(row, "typeStr", "modelType", "type"));
        if (typeText.contains("属性") || typeText.contains("property") || "1".equals(typeText)) {
            return "属性";
        }
        if (typeText.contains("功能") || typeText.contains("服务") || typeText.contains("function")
                || typeText.contains("service") || "2".equals(typeText)) {
            return "功能";
        }
        if (typeText.contains("事件") || typeText.contains("event") || "3".equals(typeText)) {
            return "事件";
        }
        return "";
    }

    private String normalizeThingModelBoolText(String value, String defaultValue) {
        String normalized = trimToEmpty(value).toLowerCase(Locale.ROOT);
        if (StringUtils.isBlank(normalized)) {
            return trimToEmpty(defaultValue);
        }
        if ("1".equals(normalized) || "true".equals(normalized) || "y".equals(normalized)
                || "yes".equals(normalized) || "是".equals(normalized)) {
            return "是";
        }
        if ("0".equals(normalized) || "false".equals(normalized) || "n".equals(normalized)
                || "no".equals(normalized) || "否".equals(normalized)) {
            return "否";
        }
        return value.trim();
    }

    private String inferReadonlyText(String accessMode) {
        String normalized = trimToEmpty(accessMode).toUpperCase(Locale.ROOT);
        if ("READ".equals(normalized) || "READONLY".equals(normalized) || "RO".equals(normalized)) {
            return "是";
        }
        if ("WRITE".equals(normalized) || "READ_WRITE".equals(normalized) || "RW".equals(normalized)) {
            return "否";
        }
        return "";
    }

    private void putIfBlank(JSONObject row, String key, String value) {
        if (row == null || StringUtils.isNotBlank(row.getString(key)) || StringUtils.isBlank(value)) {
            return;
        }
        row.put(key, value);
    }

    private String getFirstNonBlankString(JSONObject object, String... keys) {
        if (object == null || keys == null) {
            return "";
        }
        for (String key : keys) {
            Object value = object.get(key);
            if (value == null) {
                continue;
            }
            String text = value instanceof JSONObject || value instanceof JSONArray
                    ? JSON.toJSONString(value)
                    : String.valueOf(value);
            if (StringUtils.isNotBlank(text)) {
                return text.trim();
            }
        }
        return "";
    }

    private void putJsonIfNotBlank(JSONObject object, String key, String value) {
        if (StringUtils.isNotBlank(value)) {
            object.put(key, value.trim());
        }
    }

    private String[] splitLimitValue(String limitValue) {
        if (StringUtils.isBlank(limitValue)) {
            return new String[0];
        }
        return limitValue.trim().split("/", -1);
    }

    private JSONArray parseThingModelEnumList(String limitValue) {
        JSONArray enumList = new JSONArray();
        if (StringUtils.isBlank(limitValue)) {
            return enumList;
        }
        String[] items = limitValue.trim().split("/");
        for (String item : items) {
            String[] pair = item.split(":", 2);
            if (pair.length < 2 || StringUtils.isBlank(pair[0]) || StringUtils.isBlank(pair[1])) {
                continue;
            }
            JSONObject enumItem = new JSONObject();
            enumItem.put("value", pair[0].trim());
            enumItem.put("text", pair[1].trim());
            enumList.add(enumItem);
        }
        return enumList;
    }

    private String inferThingModelUnitFromSpecs(String specsText) {
        JSONObject specs = parseThingModelSpecsObject(specsText);
        return specs == null ? "" : trimToEmpty(specs.getString("unit"));
    }

    private String inferThingModelLimitValueFromSpecs(String specsText, String datatype) {
        JSONObject specs = parseThingModelSpecsObject(specsText);
        if (specs == null) {
            return "";
        }
        String actualDatatype = normalizeThingModelDataType(defaultIfBlank(datatype, specs.getString("type")));
        switch (actualDatatype) {
            case "integer":
            case "decimal":
                String min = trimToEmpty(specs.getString("min"));
                String max = trimToEmpty(specs.getString("max"));
                return StringUtils.isNotBlank(min) && StringUtils.isNotBlank(max) ? min + "/" + max : "";
            case "bool":
                String falseText = trimToEmpty(specs.getString("falseText"));
                String trueText = trimToEmpty(specs.getString("trueText"));
                return StringUtils.isNotBlank(falseText) && StringUtils.isNotBlank(trueText) ? falseText + "/" + trueText : "";
            case "string":
                String maxLength = trimToEmpty(specs.getString("maxLength"));
                return StringUtils.isNotBlank(maxLength) ? "0/" + maxLength : "";
            case "enum":
                JSONArray enumList = specs.getJSONArray("enumList");
                if (enumList == null || enumList.isEmpty()) {
                    return "";
                }
                StringBuilder builder = new StringBuilder();
                for (int index = 0; index < enumList.size(); index++) {
                    JSONObject enumItem = enumList.getJSONObject(index);
                    if (enumItem == null) {
                        continue;
                    }
                    String value = trimToEmpty(enumItem.getString("value"));
                    String text = trimToEmpty(enumItem.getString("text"));
                    if (StringUtils.isBlank(value) || StringUtils.isBlank(text)) {
                        continue;
                    }
                    if (builder.length() > 0) {
                        builder.append("/");
                    }
                    builder.append(value).append(":").append(text);
                }
                return builder.toString();
            default:
                return "";
        }
    }

    private JSONObject parseThingModelSpecsObject(String specsText) {
        if (StringUtils.isBlank(specsText)) {
            return null;
        }
        try {
            return JSON.parseObject(specsText);
        } catch (Exception ignored) {
            return null;
        }
    }

    private void appendJsonArraySheetWithLabels(Workbook workbook, String sheetName, List<String> keys,
                                                List<String> headers, JSONArray rows) {
        Sheet sheet = workbook.createSheet(sheetName);
        sheet.createFreezePane(0, 1);
        CellStyle headerStyle = createWorkbookHeaderStyle(workbook);
        CellStyle contentStyle = createWorkbookContentStyle(workbook);
        Row headerRow = sheet.createRow(0);
        List<String> actualKeys = keys == null ? Collections.emptyList() : keys;
        List<String> actualHeaders = headers == null ? actualKeys : headers;
        for (int index = 0; index < actualHeaders.size(); index++) {
            writeCell(headerRow, index, actualHeaders.get(index), headerStyle);
            sheet.setColumnWidth(index, Math.min(72, Math.max(18, actualHeaders.get(index).length() + 14)) * 256);
        }
        JSONArray actualRows = rows == null ? new JSONArray() : rows;
        for (int rowIndex = 0; rowIndex < actualRows.size(); rowIndex++) {
            Object item = actualRows.get(rowIndex);
            JSONObject object = item instanceof JSONObject ? (JSONObject) item : new JSONObject();
            Row row = sheet.createRow(rowIndex + 1);
            for (int columnIndex = 0; columnIndex < actualHeaders.size(); columnIndex++) {
                String key = columnIndex < actualKeys.size() ? actualKeys.get(columnIndex) : actualHeaders.get(columnIndex);
                writeCell(row, columnIndex, formatWorkbookCellValue(object.get(key)), contentStyle);
            }
        }
    }

    private String formatWorkbookCellValue(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof JSONObject || value instanceof JSONArray) {
            return JSON.toJSONString(value);
        }
        return String.valueOf(value);
    }

    private CellStyle createWorkbookHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
        style.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
        style.setWrapText(true);
        return style;
    }

    private CellStyle createWorkbookContentStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);
        style.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.TOP);
        return style;
    }

    private void writeCell(Row row, int columnIndex, String value, CellStyle style) {
        Cell cell = row.createCell(columnIndex);
        cell.setCellValue(trimToEmpty(value));
        if (style != null) {
            cell.setCellStyle(style);
        }
    }

    private String normalizeText(String text) {
        return text == null ? "" : text.trim().toLowerCase(Locale.ROOT);
    }

    private String defaultIfBlank(String value, String defaultValue) {
        return StringUtils.isBlank(value) ? defaultValue : value.trim();
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}
