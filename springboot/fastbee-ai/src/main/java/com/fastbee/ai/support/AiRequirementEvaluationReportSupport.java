package com.fastbee.ai.support;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.stereotype.Component;

import com.fastbee.ai.constant.AiPromptConstant;
import com.fastbee.ai.model.vo.AiRequirementEvaluationResultVO;
import com.fastbee.common.utils.StringUtils;

/**
 * AI 需求评估结果文件生成支撑。
 */
@Component
public class AiRequirementEvaluationReportSupport {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String[] EVALUATION_COLUMNS = {
            "AI匹配结论", "AI平台能力/依据", "AI建议动作", "AI复杂度", "AI风险/待确认", "AI涉及模块"
    };
    private static final int MIN_EXCEL_COLUMN_WIDTH = 10 * 256;
    private static final int MAX_EXCEL_COLUMN_WIDTH = 60 * 256;
    private static final double REQUIREMENT_MATCH_THRESHOLD = 0.35D;

    /**
     * 根据源文件类型生成评估结果文件。
     *
     * @param sourceFileName 源文件名
     * @param contentType 内容类型
     * @param sourceBytes 源文件字节
     * @param result 评估结果
     * @return 生成产物
     * @throws IOException 文件生成异常
     */
    public GeneratedEvaluationArtifact buildEvaluationArtifact(String sourceFileName,
                                                               String contentType,
                                                               byte[] sourceBytes,
                                                               AiRequirementEvaluationResultVO result) throws IOException {
        String suffix = resolveSuffix(sourceFileName);
        String baseName = resolveBaseName(sourceFileName, AiPromptConstant.REQUIREMENT_FILE_FALLBACK_NAME);
        if (isWorkbookSuffix(suffix) && sourceBytes != null && sourceBytes.length > 0) {
            try {
                byte[] bytes = buildWorkbookWithEvaluation(sourceBytes, result);
                String extension = ".xls".equals(suffix) ? ".xls" : ".xlsx";
                return new GeneratedEvaluationArtifact(bytes,
                        baseName + "_AI需求评估结果" + extension,
                        extension,
                        "SOURCE_WORKBOOK_WITH_EVALUATION");
            } catch (Exception ignore) {
                // 原始 Excel 异常时降级生成独立评估报告，避免影响本轮评估结果交付。
            }
        }
        if (".docx".equals(suffix) && sourceBytes != null && sourceBytes.length > 0) {
            try {
                byte[] bytes = buildDocxWithEvaluation(sourceBytes, sourceFileName, result);
                return new GeneratedEvaluationArtifact(bytes,
                        baseName + "_AI需求评估结果.docx",
                        ".docx",
                        "SOURCE_DOCX_WITH_EVALUATION");
            } catch (Exception ignore) {
                // 原始 Word 异常时降级生成独立评估报告。
            }
        }
        return new GeneratedEvaluationArtifact(buildStandaloneDocxReport(sourceFileName, contentType, result),
                baseName + "_AI需求评估报告.docx",
                ".docx",
                "EVALUATION_REPORT_DOCX");
    }

    private byte[] buildWorkbookWithEvaluation(byte[] sourceBytes,
                                               AiRequirementEvaluationResultVO result) throws IOException {
        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(sourceBytes));
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            appendEvaluationColumns(workbook, result);
            appendWorkbookReportSheet(workbook, result);
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private void appendEvaluationColumns(Workbook workbook, AiRequirementEvaluationResultVO result) {
        List<LinkedHashMap<String, Object>> items = safeRows(result == null ? null : result.getRequirementItems());
        if (items.isEmpty()) {
            return;
        }
        Sheet sheet = findPrimarySheet(workbook);
        if (sheet == null) {
            sheet = workbook.createSheet("客户需求清单");
        }
        Row headerRow = findHeaderRow(sheet);
        if (headerRow == null) {
            headerRow = sheet.createRow(Math.max(sheet.getFirstRowNum(), 0));
        }
        int startColumn = resolveEvaluationStartColumn(headerRow);
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle contentStyle = createContentStyle(workbook);
        for (int index = 0; index < EVALUATION_COLUMNS.length; index++) {
            writeCell(headerRow, startColumn + index, EVALUATION_COLUMNS[index], headerStyle);
        }
        clearEvaluationColumns(sheet, headerRow.getRowNum() + 1, startColumn, startColumn + EVALUATION_COLUMNS.length - 1);
        int requirementTextColumn = resolveRequirementTextColumn(sheet, headerRow, startColumn);
        List<Row> requirementRows = resolveRequirementRows(sheet, headerRow, startColumn, requirementTextColumn);
        Map<Integer, LinkedHashMap<String, Object>> matchedItems = matchItemsToSourceRows(items, requirementRows, startColumn);
        if (requirementRows.isEmpty()) {
            int firstDataRowIndex = headerRow.getRowNum() + 1;
            for (int index = 0; index < items.size(); index++) {
                Row row = sheet.getRow(firstDataRowIndex + index);
                if (row == null) {
                    row = sheet.createRow(firstDataRowIndex + index);
                }
                writeEvaluationCells(row, startColumn, items.get(index), result, contentStyle);
            }
        } else {
            for (Row row : requirementRows) {
                LinkedHashMap<String, Object> item = matchedItems.get(row.getRowNum());
                if (item != null) {
                    writeEvaluationCells(row, startColumn, item, result, contentStyle);
                }
            }
            appendUnmatchedItems(sheet, items, matchedItems, requirementTextColumn, startColumn, result, contentStyle);
        }
        autoSizeColumns(sheet, startColumn, startColumn + EVALUATION_COLUMNS.length - 1);
    }

    private void writeEvaluationCells(Row row,
                                      int startColumn,
                                      LinkedHashMap<String, Object> item,
                                      AiRequirementEvaluationResultVO result,
                                      CellStyle contentStyle) {
        if (row == null || item == null) {
            return;
        }
        writeCell(row, startColumn, cellText(item.get("匹配结论")), contentStyle);
        writeCell(row, startColumn + 1, cellText(item.get("平台能力/依据")), contentStyle);
        writeCell(row, startColumn + 2, cellText(item.get("建议动作")), contentStyle);
        writeCell(row, startColumn + 3, cellText(item.get("复杂度")), contentStyle);
        writeCell(row, startColumn + 4, buildItemRiskAndPendingText(item, result), contentStyle);
        writeCell(row, startColumn + 5, cellText(item.get("涉及模块")), contentStyle);
    }

    private void appendUnmatchedItems(Sheet sheet,
                                      List<LinkedHashMap<String, Object>> items,
                                      Map<Integer, LinkedHashMap<String, Object>> matchedItems,
                                      int requirementTextColumn,
                                      int startColumn,
                                      AiRequirementEvaluationResultVO result,
                                      CellStyle contentStyle) {
        Set<LinkedHashMap<String, Object>> matchedValues = Collections.newSetFromMap(new IdentityHashMap<>());
        matchedValues.addAll(matchedItems.values());
        for (LinkedHashMap<String, Object> item : items) {
            if (matchedValues.contains(item)) {
                continue;
            }
            int rowIndex = sheet.getLastRowNum() + 1;
            Row row = sheet.createRow(rowIndex);
            writeCell(row, Math.max(requirementTextColumn, 0), "AI识别需求：" + cellText(item.get("需求点")), contentStyle);
            writeEvaluationCells(row, startColumn, item, result, contentStyle);
        }
    }

    private int resolveEvaluationStartColumn(Row headerRow) {
        if (headerRow == null || headerRow.getLastCellNum() <= 0) {
            return 0;
        }
        int firstAiColumn = -1;
        int lastBusinessColumn = -1;
        for (int columnIndex = 0; columnIndex < headerRow.getLastCellNum(); columnIndex++) {
            String text = readCellText(headerRow, columnIndex);
            if (StringUtils.isBlank(text)) {
                continue;
            }
            if (text.startsWith("AI")) {
                if (firstAiColumn < 0) {
                    firstAiColumn = columnIndex;
                }
                continue;
            }
            if (firstAiColumn < 0) {
                lastBusinessColumn = columnIndex;
            }
        }
        if (firstAiColumn >= 0) {
            return firstAiColumn;
        }
        return Math.max(lastBusinessColumn + 1, 0);
    }

    private void clearEvaluationColumns(Sheet sheet, int firstDataRowIndex, int startColumn, int endColumn) {
        if (sheet == null) {
            return;
        }
        for (int rowIndex = firstDataRowIndex; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                continue;
            }
            for (int columnIndex = startColumn; columnIndex <= endColumn; columnIndex++) {
                Cell cell = row.getCell(columnIndex);
                if (cell != null) {
                    cell.setCellValue("");
                }
            }
        }
    }

    private List<Row> resolveRequirementRows(Sheet sheet, Row headerRow, int startColumn, int requirementTextColumn) {
        List<Row> rows = new ArrayList<>();
        if (sheet == null || headerRow == null) {
            return rows;
        }
        for (int rowIndex = headerRow.getRowNum() + 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                continue;
            }
            String requirementText = readCellText(row, requirementTextColumn);
            if (StringUtils.isNotBlank(requirementText)) {
                rows.add(row);
            }
        }
        if (!rows.isEmpty()) {
            return rows;
        }
        for (int rowIndex = headerRow.getRowNum() + 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row != null && StringUtils.isNotBlank(readRowText(row, startColumn))) {
                rows.add(row);
            }
        }
        return rows;
    }

    private int resolveRequirementTextColumn(Sheet sheet, Row headerRow, int startColumn) {
        String[] headerKeywords = {
                "具体功能实现要求说明", "功能实现要求", "需求点", "需求描述", "需求内容", "功能需求", "功能说明", "说明"
        };
        for (String keyword : headerKeywords) {
            for (int columnIndex = 0; columnIndex < startColumn; columnIndex++) {
                String headerText = readCellText(headerRow, columnIndex);
                if (StringUtils.isNotBlank(headerText) && headerText.contains(keyword)) {
                    return columnIndex;
                }
            }
        }
        int bestColumn = 0;
        int bestScore = -1;
        for (int columnIndex = 0; columnIndex < startColumn; columnIndex++) {
            int score = 0;
            for (int rowIndex = headerRow.getRowNum() + 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                String text = readCellText(sheet.getRow(rowIndex), columnIndex);
                if (StringUtils.isNotBlank(text)) {
                    score += Math.min(text.length(), 200);
                }
            }
            if (score > bestScore) {
                bestScore = score;
                bestColumn = columnIndex;
            }
        }
        return bestColumn;
    }

    private Map<Integer, LinkedHashMap<String, Object>> matchItemsToSourceRows(List<LinkedHashMap<String, Object>> items,
                                                                               List<Row> requirementRows,
                                                                               int startColumn) {
        Map<Integer, LinkedHashMap<String, Object>> matchedItems = new LinkedHashMap<>();
        if (items == null || items.isEmpty() || requirementRows == null || requirementRows.isEmpty()) {
            return matchedItems;
        }
        Set<Integer> usedRowNums = new HashSet<>();
        List<LinkedHashMap<String, Object>> unmatchedItems = new ArrayList<>();
        for (LinkedHashMap<String, Object> item : items) {
            Row bestRow = null;
            double bestScore = 0D;
            String itemText = resolveItemRequirementText(item);
            for (Row row : requirementRows) {
                if (row == null || usedRowNums.contains(row.getRowNum())) {
                    continue;
                }
                double score = calculateRequirementSimilarity(itemText, buildRowMatchText(row, startColumn));
                if (score > bestScore) {
                    bestScore = score;
                    bestRow = row;
                }
            }
            if (bestRow != null && bestScore >= REQUIREMENT_MATCH_THRESHOLD) {
                matchedItems.put(bestRow.getRowNum(), item);
                usedRowNums.add(bestRow.getRowNum());
            } else {
                unmatchedItems.add(item);
            }
        }
        int fallbackIndex = 0;
        for (LinkedHashMap<String, Object> item : unmatchedItems) {
            while (fallbackIndex < requirementRows.size()
                    && usedRowNums.contains(requirementRows.get(fallbackIndex).getRowNum())) {
                fallbackIndex++;
            }
            if (fallbackIndex >= requirementRows.size()) {
                break;
            }
            Row row = requirementRows.get(fallbackIndex++);
            matchedItems.put(row.getRowNum(), item);
            usedRowNums.add(row.getRowNum());
        }
        return matchedItems;
    }

    private String buildRowMatchText(Row row, int startColumn) {
        return readRowText(row, startColumn);
    }

    private String readRowText(Row row, int endColumnExclusive) {
        if (row == null || endColumnExclusive <= 0) {
            return "";
        }
        List<String> values = new ArrayList<>();
        for (int columnIndex = 0; columnIndex < endColumnExclusive; columnIndex++) {
            String text = readCellText(row, columnIndex);
            if (StringUtils.isNotBlank(text)) {
                values.add(text);
            }
        }
        return String.join(" ", values);
    }

    private String readCellText(Row row, int columnIndex) {
        if (row == null || columnIndex < 0) {
            return "";
        }
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            return "";
        }
        return new DataFormatter(Locale.CHINA).formatCellValue(cell).trim();
    }

    private String resolveItemRequirementText(LinkedHashMap<String, Object> item) {
        if (item == null || item.isEmpty()) {
            return "";
        }
        return firstNonBlankItemValue(item, "需求点", "需求", "需求描述", "名称", "标题");
    }

    private double calculateRequirementSimilarity(String left, String right) {
        String normalizedLeft = normalizeMatchText(left);
        String normalizedRight = normalizeMatchText(right);
        if (StringUtils.isBlank(normalizedLeft) || StringUtils.isBlank(normalizedRight)) {
            return 0D;
        }
        if (normalizedLeft.contains(normalizedRight) || normalizedRight.contains(normalizedLeft)) {
            return 1D;
        }
        Set<String> leftTokens = buildMatchTokens(normalizedLeft);
        Set<String> rightTokens = buildMatchTokens(normalizedRight);
        if (leftTokens.isEmpty() || rightTokens.isEmpty()) {
            return 0D;
        }
        int intersection = 0;
        for (String token : leftTokens) {
            if (rightTokens.contains(token)) {
                intersection++;
            }
        }
        double diceScore = 2D * intersection / (leftTokens.size() + rightTokens.size());
        Set<Integer> leftChars = buildCodePointSet(normalizedLeft);
        Set<Integer> rightChars = buildCodePointSet(normalizedRight);
        int commonChars = 0;
        for (Integer codePoint : leftChars) {
            if (rightChars.contains(codePoint)) {
                commonChars++;
            }
        }
        double charScore = leftChars.isEmpty() || rightChars.isEmpty()
                ? 0D : (double) commonChars / Math.min(leftChars.size(), rightChars.size());
        return Math.max(diceScore, charScore * 0.8D);
    }

    private Set<Integer> buildCodePointSet(String text) {
        Set<Integer> values = new HashSet<>();
        if (StringUtils.isNotBlank(text)) {
            text.codePoints().forEach(values::add);
        }
        return values;
    }

    private Set<String> buildMatchTokens(String text) {
        Set<String> tokens = new HashSet<>();
        if (StringUtils.isBlank(text)) {
            return tokens;
        }
        if (text.length() <= 2) {
            tokens.add(text);
            return tokens;
        }
        for (int index = 0; index < text.length() - 1; index++) {
            tokens.add(text.substring(index, index + 2));
        }
        return tokens;
    }

    private String normalizeMatchText(String text) {
        if (StringUtils.isBlank(text)) {
            return "";
        }
        return text.toLowerCase(Locale.ROOT)
                .replaceAll("[\\s　\\p{Punct}，。；：、（）【】《》“”‘’！？·]+", "")
                .trim();
    }

    private void appendWorkbookReportSheet(Workbook workbook, AiRequirementEvaluationResultVO result) {
        Sheet sheet = workbook.createSheet(resolveUniqueSheetName(workbook, "AI评估总览"));
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle labelStyle = createLabelStyle(workbook);
        CellStyle contentStyle = createContentStyle(workbook);
        int rowIndex = 0;
        Row titleRow = sheet.createRow(rowIndex++);
        writeCell(titleRow, 0, "AI需求评估结果", headerStyle);
        writeCell(titleRow, 1, LocalDateTime.now().format(DATE_TIME_FORMATTER), contentStyle);
        rowIndex = appendKeyValueRows(sheet, rowIndex, labelStyle, contentStyle, List.<String[]>of(
                new String[]{"源文件", safeText(result == null ? "" : result.getSourceFileName())},
                new String[]{"匹配等级", safeText(result == null ? "" : result.getMatchLevel())},
                new String[]{"总体结论", safeText(result == null ? "" : result.getOverallConclusion())},
                new String[]{"结果摘要", safeText(result == null ? "" : result.getSummary())}
        ));
        rowIndex++;
        rowIndex = appendMapRows(sheet, rowIndex, "需求点匹配", result == null ? null : result.getRequirementItems(), headerStyle, contentStyle);
        rowIndex++;
        rowIndex = appendMapRows(sheet, rowIndex, "模块影响", result == null ? null : result.getModuleImpacts(), headerStyle, contentStyle);
        rowIndex++;
        rowIndex = appendListRows(sheet, rowIndex, "主要风险", result == null ? null : result.getRisks(), headerStyle, contentStyle);
        rowIndex++;
        rowIndex = appendListRows(sheet, rowIndex, "待确认问题", result == null ? null : result.getPendingQuestions(), headerStyle, contentStyle);
        rowIndex++;
        rowIndex = appendListRows(sheet, rowIndex, "建议下一步", result == null ? null : result.getNextSteps(), headerStyle, contentStyle);
        rowIndex++;
        appendKeyValueRows(sheet, rowIndex, labelStyle, contentStyle, List.<String[]>of(
                new String[]{"免责声明", safeText(result == null ? AiPromptConstant.REQUIREMENT_EVALUATION_DISCLAIMER : result.getDisclaimer())}
        ));
        autoSizeColumns(sheet, 0, 8);
    }

    private byte[] buildDocxWithEvaluation(byte[] sourceBytes,
                                           String sourceFileName,
                                           AiRequirementEvaluationResultVO result) throws IOException {
        try (XWPFDocument document = new XWPFDocument(new ByteArrayInputStream(sourceBytes));
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            XWPFParagraph breakParagraph = document.createParagraph();
            breakParagraph.createRun().addBreak(BreakType.PAGE);
            appendDocxEvaluationReport(document, sourceFileName, result);
            document.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private byte[] buildStandaloneDocxReport(String sourceFileName,
                                             String contentType,
                                             AiRequirementEvaluationResultVO result) throws IOException {
        try (XWPFDocument document = new XWPFDocument();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            appendDocxEvaluationReport(document, sourceFileName, result);
            if (StringUtils.isNotBlank(contentType)) {
                appendDocxParagraph(document, "源文件类型：" + contentType, false);
            }
            document.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private void appendDocxEvaluationReport(XWPFDocument document,
                                            String sourceFileName,
                                            AiRequirementEvaluationResultVO result) {
        appendDocxTitle(document, "AI需求评估结果");
        appendDocxParagraph(document, "源文件：" + safeText(defaultIfBlank(sourceFileName,
                result == null ? "" : result.getSourceFileName())), false);
        appendDocxParagraph(document, "生成时间：" + LocalDateTime.now().format(DATE_TIME_FORMATTER), false);
        appendDocxParagraph(document, "匹配等级：" + safeText(result == null ? "" : result.getMatchLevel()), true);
        appendDocxSection(document, "总体结论");
        appendDocxParagraph(document, safeText(result == null ? "" : defaultIfBlank(result.getOverallConclusion(), result.getSummary())), false);
        appendDocxTable(document, "需求点匹配", result == null ? null : result.getRequirementItems());
        appendDocxTable(document, "模块影响", result == null ? null : result.getModuleImpacts());
        appendDocxList(document, "主要风险", result == null ? null : result.getRisks());
        appendDocxList(document, "待确认问题", result == null ? null : result.getPendingQuestions());
        appendDocxList(document, "建议下一步", result == null ? null : result.getNextSteps());
        appendDocxSection(document, "免责声明");
        appendDocxParagraph(document, safeText(result == null ? AiPromptConstant.REQUIREMENT_EVALUATION_DISCLAIMER : result.getDisclaimer()), true);
    }

    private void appendDocxTitle(XWPFDocument document, String text) {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setBold(true);
        run.setFontSize(18);
    }

    private void appendDocxSection(XWPFDocument document, String text) {
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setBold(true);
        run.setFontSize(13);
    }

    private void appendDocxParagraph(XWPFDocument document, String text, boolean bold) {
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText(StringUtils.isBlank(text) ? "-" : text);
        run.setBold(bold);
        run.setFontSize(11);
    }

    private void appendDocxList(XWPFDocument document, String title, List<String> values) {
        appendDocxSection(document, title);
        List<String> actualValues = safeList(values);
        if (actualValues.isEmpty()) {
            appendDocxParagraph(document, "-", false);
            return;
        }
        for (String value : actualValues) {
            appendDocxParagraph(document, "· " + value, false);
        }
    }

    private void appendDocxTable(XWPFDocument document, String title, List<LinkedHashMap<String, Object>> rows) {
        appendDocxSection(document, title);
        List<LinkedHashMap<String, Object>> actualRows = safeRows(rows);
        if (actualRows.isEmpty()) {
            appendDocxParagraph(document, "-", false);
            return;
        }
        List<String> headers = new ArrayList<>(actualRows.get(0).keySet());
        XWPFTable table = document.createTable(actualRows.size() + 1, headers.size());
        writeDocxTableRow(table.getRow(0), headers, true);
        for (int index = 0; index < actualRows.size(); index++) {
            LinkedHashMap<String, Object> row = actualRows.get(index);
            List<String> values = headers.stream().map(header -> cellText(row.get(header))).toList();
            writeDocxTableRow(table.getRow(index + 1), values, false);
        }
    }

    private void writeDocxTableRow(XWPFTableRow row, List<String> values, boolean bold) {
        if (row == null || values == null) {
            return;
        }
        for (int index = 0; index < values.size(); index++) {
            XWPFTableCell cell = row.getCell(index);
            if (cell == null) {
                cell = row.addNewTableCell();
            }
            if (!cell.getParagraphs().isEmpty()) {
                cell.removeParagraph(0);
            }
            XWPFParagraph paragraph = cell.addParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText(safeText(values.get(index)));
            run.setBold(bold);
            run.setFontSize(10);
        }
    }

    private int appendKeyValueRows(Sheet sheet,
                                   int rowIndex,
                                   CellStyle labelStyle,
                                   CellStyle contentStyle,
                                   List<String[]> rows) {
        for (String[] rowValues : rows) {
            Row row = sheet.createRow(rowIndex++);
            writeCell(row, 0, rowValues[0], labelStyle);
            writeCell(row, 1, rowValues.length > 1 ? rowValues[1] : "", contentStyle);
        }
        return rowIndex;
    }

    private int appendMapRows(Sheet sheet,
                              int rowIndex,
                              String title,
                              List<LinkedHashMap<String, Object>> rows,
                              CellStyle headerStyle,
                              CellStyle contentStyle) {
        List<LinkedHashMap<String, Object>> actualRows = safeRows(rows);
        Row titleRow = sheet.createRow(rowIndex++);
        writeCell(titleRow, 0, title, headerStyle);
        if (actualRows.isEmpty()) {
            Row emptyRow = sheet.createRow(rowIndex++);
            writeCell(emptyRow, 0, "-", contentStyle);
            return rowIndex;
        }
        List<String> headers = new ArrayList<>(actualRows.get(0).keySet());
        Row headerRow = sheet.createRow(rowIndex++);
        for (int index = 0; index < headers.size(); index++) {
            writeCell(headerRow, index, headers.get(index), headerStyle);
        }
        for (LinkedHashMap<String, Object> rowData : actualRows) {
            Row row = sheet.createRow(rowIndex++);
            for (int index = 0; index < headers.size(); index++) {
                writeCell(row, index, cellText(rowData.get(headers.get(index))), contentStyle);
            }
        }
        return rowIndex;
    }

    private int appendListRows(Sheet sheet,
                               int rowIndex,
                               String title,
                               List<String> values,
                               CellStyle headerStyle,
                               CellStyle contentStyle) {
        Row titleRow = sheet.createRow(rowIndex++);
        writeCell(titleRow, 0, title, headerStyle);
        List<String> actualValues = safeList(values);
        if (actualValues.isEmpty()) {
            Row row = sheet.createRow(rowIndex++);
            writeCell(row, 0, "-", contentStyle);
            return rowIndex;
        }
        for (String value : actualValues) {
            Row row = sheet.createRow(rowIndex++);
            writeCell(row, 0, value, contentStyle);
        }
        return rowIndex;
    }

    private Sheet findPrimarySheet(Workbook workbook) {
        if (workbook == null || workbook.getNumberOfSheets() == 0) {
            return null;
        }
        Sheet first = workbook.getSheetAt(0);
        for (int index = 0; index < workbook.getNumberOfSheets(); index++) {
            Sheet sheet = workbook.getSheetAt(index);
            if (sheet != null && sheet.getPhysicalNumberOfRows() > 0) {
                return sheet;
            }
        }
        return first;
    }

    private Row findHeaderRow(Sheet sheet) {
        if (sheet == null) {
            return null;
        }
        for (int rowIndex = sheet.getFirstRowNum(); rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row != null && row.getLastCellNum() > 0) {
                return row;
            }
        }
        return null;
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setWrapText(true);
        return style;
    }

    private CellStyle createLabelStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setWrapText(true);
        return style;
    }

    private CellStyle createContentStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);
        return style;
    }

    private void writeCell(Row row, int columnIndex, String value, CellStyle style) {
        if (row == null) {
            return;
        }
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            cell = row.createCell(columnIndex);
        }
        cell.setCellValue(safeText(value));
        if (style != null) {
            cell.setCellStyle(style);
        }
    }

    private void autoSizeColumns(Sheet sheet, int startColumn, int endColumn) {
        if (sheet == null) {
            return;
        }
        for (int index = startColumn; index <= endColumn; index++) {
            try {
                sheet.autoSizeColumn(index);
                int currentWidth = sheet.getColumnWidth(index);
                if (currentWidth < MIN_EXCEL_COLUMN_WIDTH) {
                    sheet.setColumnWidth(index, MIN_EXCEL_COLUMN_WIDTH);
                } else if (currentWidth > MAX_EXCEL_COLUMN_WIDTH) {
                    sheet.setColumnWidth(index, MAX_EXCEL_COLUMN_WIDTH);
                }
            } catch (Exception ignore) {
                // 自动列宽失败不影响文件生成。
            }
        }
    }

    private String buildItemRiskAndPendingText(LinkedHashMap<String, Object> item, AiRequirementEvaluationResultVO result) {
        String itemRisk = firstNonBlankItemValue(item,
                "风险/待确认", "风险", "待确认", "待确认问题", "risk", "pendingQuestion", "pendingQuestions");
        if (StringUtils.isNotBlank(itemRisk)) {
            return itemRisk;
        }
        List<String> globalValues = new ArrayList<>();
        globalValues.addAll(safeList(result == null ? null : result.getRisks()));
        globalValues.addAll(safeList(result == null ? null : result.getPendingQuestions()));
        return globalValues.isEmpty() ? "" : "详见AI评估总览";
    }

    private String firstNonBlankItemValue(LinkedHashMap<String, Object> item, String... keys) {
        if (item == null || keys == null) {
            return "";
        }
        for (String key : keys) {
            Object value = item.get(key);
            if (value == null) {
                continue;
            }
            String text = value instanceof List<?> list
                    ? String.join("；", list.stream().map(String::valueOf).filter(StringUtils::isNotBlank).toList())
                    : String.valueOf(value);
            if (StringUtils.isNotBlank(text) && !"-".equals(text.trim())) {
                return text.trim();
            }
        }
        return "";
    }

    private String resolveUniqueSheetName(Workbook workbook, String preferredName) {
        String baseName = preferredName.length() > 25 ? preferredName.substring(0, 25) : preferredName;
        String candidate = baseName;
        int index = 1;
        while (workbook.getSheet(candidate) != null) {
            String suffix = "_" + index++;
            candidate = baseName;
            if (candidate.length() + suffix.length() > 31) {
                candidate = candidate.substring(0, 31 - suffix.length());
            }
            candidate = candidate + suffix;
        }
        return candidate;
    }

    private List<LinkedHashMap<String, Object>> safeRows(List<LinkedHashMap<String, Object>> rows) {
        return rows == null ? new ArrayList<>() : rows.stream()
                .filter(row -> row != null && !row.isEmpty())
                .toList();
    }

    private List<String> safeList(List<String> values) {
        return values == null ? new ArrayList<>() : values.stream()
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .toList();
    }

    private String cellText(Object value) {
        return safeText(value == null ? "" : String.valueOf(value));
    }

    private String safeText(String value) {
        return StringUtils.isBlank(value) ? "-" : value.trim();
    }

    private String defaultIfBlank(String value, String defaultValue) {
        return StringUtils.isBlank(value) ? defaultValue : value.trim();
    }

    private boolean isWorkbookSuffix(String suffix) {
        return ".xlsx".equals(suffix) || ".xls".equals(suffix);
    }

    private String resolveSuffix(String fileName) {
        int index = fileName == null ? -1 : fileName.lastIndexOf('.');
        return index >= 0 ? fileName.substring(index).toLowerCase(Locale.ROOT) : "";
    }

    private String resolveBaseName(String fileName, String fallbackName) {
        String normalized = defaultIfBlank(fileName, fallbackName).replace('\\', '/');
        int slashIndex = normalized.lastIndexOf('/');
        if (slashIndex >= 0) {
            normalized = normalized.substring(slashIndex + 1);
        }
        normalized = normalized.replaceFirst("\\.[^.]+$", "")
                .replaceAll("[\\\\/:*?\"<>|]+", "_")
                .trim();
        return StringUtils.isBlank(normalized) ? fallbackName : normalized;
    }

    public record GeneratedEvaluationArtifact(byte[] bytes,
                                              String artifactName,
                                              String fileExtension,
                                              String artifactType) {
    }
}
