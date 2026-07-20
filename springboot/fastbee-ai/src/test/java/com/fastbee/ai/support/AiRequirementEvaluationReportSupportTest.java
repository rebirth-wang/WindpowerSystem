package com.fastbee.ai.support;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fastbee.ai.model.vo.AiRequirementEvaluationResultVO;

/**
 * AI 需求评估结果文件生成支撑测试。
 */
class AiRequirementEvaluationReportSupportTest {

    private final AiRequirementEvaluationReportSupport support = new AiRequirementEvaluationReportSupport();

    @Test
    void shouldAppendEvaluationColumnsAndSummarySheetForWorkbook() throws Exception {
        byte[] sourceBytes;
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Row header = workbook.createSheet("需求清单").createRow(0);
            header.createCell(0).setCellValue("需求点");
            workbook.getSheetAt(0).createRow(1).createCell(0).setCellValue("支持Modbus接入");
            workbook.write(outputStream);
            sourceBytes = outputStream.toByteArray();
        }

        AiRequirementEvaluationReportSupport.GeneratedEvaluationArtifact artifact =
                support.buildEvaluationArtifact("客户需求.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                        sourceBytes, buildResult());

        Assertions.assertEquals("SOURCE_WORKBOOK_WITH_EVALUATION", artifact.artifactType());
        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(artifact.bytes()))) {
            Row header = workbook.getSheet("需求清单").getRow(0);
            Assertions.assertEquals("AI匹配结论", header.getCell(1).getStringCellValue());
            Assertions.assertEquals("平台已有能力", workbook.getSheet("需求清单").getRow(1).getCell(1).getStringCellValue());
            Assertions.assertNotNull(workbook.getSheet("AI评估总览"));
        }
    }

    @Test
    void shouldWriteEvaluationOnlyToEffectiveRequirementRows() throws Exception {
        byte[] sourceBytes;
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Row header = workbook.createSheet("需求清单").createRow(0);
            header.createCell(0).setCellValue("序号");
            header.createCell(1).setCellValue("系统名称");
            header.createCell(2).setCellValue("子系统名称");
            header.createCell(3).setCellValue("功能模块名称");
            header.createCell(4).setCellValue("具体功能实现要求说明");
            header.createCell(5).setCellValue("数量");
            header.createCell(6).setCellValue("单位");
            header.createCell(17).setCellValue("");
            workbook.getSheetAt(0).createRow(1).createCell(4).setCellValue("支持Modbus接入");
            workbook.getSheetAt(0).createRow(2);
            workbook.getSheetAt(0).createRow(3).createCell(4).setCellValue("支持自定义告警规则");
            workbook.write(outputStream);
            sourceBytes = outputStream.toByteArray();
        }

        AiRequirementEvaluationResultVO result = buildResult();
        LinkedHashMap<String, Object> secondItem = new LinkedHashMap<>();
        secondItem.put("需求点", "自定义告警规则");
        secondItem.put("匹配结论", "需要二开");
        secondItem.put("平台能力/依据", "平台已有告警配置能力，可扩展规则条件");
        secondItem.put("建议动作", "评估规则表达式和通知链路");
        secondItem.put("复杂度", "中");
        secondItem.put("涉及模块", "告警中心");
        result.setRequirementItems(List.of(result.getRequirementItems().get(0), secondItem));

        AiRequirementEvaluationReportSupport.GeneratedEvaluationArtifact artifact =
                support.buildEvaluationArtifact("物联网.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                        sourceBytes, result);

        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(artifact.bytes()))) {
            Row header = workbook.getSheet("需求清单").getRow(0);
            Assertions.assertEquals("AI匹配结论", header.getCell(7).getStringCellValue());
            Assertions.assertEquals("平台已有能力", workbook.getSheet("需求清单").getRow(1).getCell(7).getStringCellValue());
            Assertions.assertNull(workbook.getSheet("需求清单").getRow(2).getCell(7));
            Assertions.assertEquals("需要二开", workbook.getSheet("需求清单").getRow(3).getCell(7).getStringCellValue());
            Assertions.assertEquals("详见AI评估总览", workbook.getSheet("需求清单").getRow(1).getCell(11).getStringCellValue());
            Assertions.assertTrue(workbook.getSheet("需求清单").getColumnWidth(11) <= 60 * 256);
        }
    }

    @Test
    void shouldBuildStandaloneDocxReportForPdfSource() throws Exception {
        AiRequirementEvaluationReportSupport.GeneratedEvaluationArtifact artifact =
                support.buildEvaluationArtifact("客户需求.pdf", "application/pdf", null, buildResult());

        Assertions.assertEquals("EVALUATION_REPORT_DOCX", artifact.artifactType());
        Assertions.assertTrue(artifact.artifactName().endsWith(".docx"));
        try (XWPFDocument document = new XWPFDocument(new ByteArrayInputStream(artifact.bytes()))) {
            Assertions.assertFalse(document.getParagraphs().isEmpty());
        }
    }

    private AiRequirementEvaluationResultVO buildResult() {
        AiRequirementEvaluationResultVO result = new AiRequirementEvaluationResultVO();
        result.setSourceFileName("客户需求.xlsx");
        result.setMatchLevel("HIGH");
        result.setOverallConclusion("整体匹配度较高");
        result.setSummary("已完成初步评估");
        result.setDisclaimer("本评估结果仅供参考");
        LinkedHashMap<String, Object> item = new LinkedHashMap<>();
        item.put("需求点", "支持Modbus接入");
        item.put("匹配结论", "平台已有能力");
        item.put("平台能力/依据", "平台已有Modbus TCP接入能力");
        item.put("建议动作", "按平台接入配置");
        item.put("复杂度", "低");
        item.put("涉及模块", "设备接入");
        result.setRequirementItems(List.of(item));
        result.setRisks(List.of("需确认设备协议细节"));
        result.setPendingQuestions(List.of("确认设备型号"));
        result.setNextSteps(List.of("联系产品或项目方具体评估"));
        return result;
    }
}
