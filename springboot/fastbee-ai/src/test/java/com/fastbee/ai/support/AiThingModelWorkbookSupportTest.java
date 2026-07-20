package com.fastbee.ai.support;

import java.io.ByteArrayInputStream;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * AI 物模型导入模板工作簿支撑测试。
 */
class AiThingModelWorkbookSupportTest {

    private final AiThingModelWorkbookSupport support = new AiThingModelWorkbookSupport();

    @Test
    void shouldExportFastBeeThingModelTemplateHeaders() throws Exception {
        JSONArray rows = new JSONArray();
        rows.add(JSON.parseObject("""
                {
                  "modelName":"温度",
                  "modelName_en_US":"temperature",
                  "identifier":"temperature",
                  "datatype":"double",
                  "unit":"℃",
                  "limitValue":"-40/125",
                  "typeStr":"属性"
                }
                """));

        byte[] workbookBytes = support.buildThingModelImportWorkbookBytes(rows);

        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(workbookBytes))) {
            Assertions.assertEquals("物模型导入模板", workbook.getSheetAt(0).getSheetName());
            Row header = workbook.getSheetAt(0).getRow(0);
            Assertions.assertEquals("物模型名称", header.getCell(0).getStringCellValue());
            Assertions.assertEquals("英文物模型名称", header.getCell(1).getStringCellValue());
            Assertions.assertEquals("标识符", header.getCell(2).getStringCellValue());
            Assertions.assertEquals("是否设备分享权限", header.getCell(13).getStringCellValue());
            Row data = workbook.getSheetAt(0).getRow(1);
            Assertions.assertEquals("温度", data.getCell(0).getStringCellValue());
            Assertions.assertEquals("temperature", data.getCell(2).getStringCellValue());
            Assertions.assertEquals("decimal", data.getCell(3).getStringCellValue());
        }
    }

    @Test
    void shouldBuildBoolEnumAndStringSpecs() {
        JSONObject boolRow = JSON.parseObject("""
                {"datatype":"boolean","limitValue":"0:关闭/1:开启"}
                """);
        JSONObject enumRow = JSON.parseObject("""
                {"datatype":"enum","limitValue":"0:低速档位/1:中速档位/2:高速档位"}
                """);
        JSONObject stringRow = JSON.parseObject("""
                {"datatype":"string","limitValue":"0/64"}
                """);

        JSONObject boolSpecs = JSON.parseObject(support.buildThingModelSpecs(boolRow));
        JSONObject enumSpecs = JSON.parseObject(support.buildThingModelSpecs(enumRow));
        JSONObject stringSpecs = JSON.parseObject(support.buildThingModelSpecs(stringRow));

        Assertions.assertEquals("bool", boolSpecs.getString("type"));
        Assertions.assertEquals("0:关闭", boolSpecs.getString("falseText"));
        Assertions.assertEquals("1:开启", boolSpecs.getString("trueText"));
        Assertions.assertEquals("enum", enumSpecs.getString("type"));
        Assertions.assertEquals(3, enumSpecs.getJSONArray("enumList").size());
        Assertions.assertEquals("低速档位", enumSpecs.getJSONArray("enumList").getJSONObject(0).getString("text"));
        Assertions.assertEquals("string", stringSpecs.getString("type"));
        Assertions.assertEquals("64", stringSpecs.getString("maxLength"));
    }

    @Test
    void shouldNormalizeRowsFromWorkbookFormat() {
        JSONArray rawRows = new JSONArray();
        rawRows.add(JSON.parseObject("""
                {
                  "物模型名称":"风机档位",
                  "英文物模型名称":"fan_level",
                  "标识符":"fan_level",
                  "数据类型":"enum",
                  "有效值范围":"0:低速档位/1:中速档位",
                  "模型类别":"功能",
                  "是否图表展示":"否"
                }
                """));

        JSONArray normalized = support.normalizeThingModelMappingsFromWorkbook(rawRows, new JSONArray());

        Assertions.assertEquals(1, normalized.size());
        JSONObject row = normalized.getJSONObject(0);
        Assertions.assertEquals("fan_level", row.getString("identifier"));
        Assertions.assertEquals("功能", row.getString("typeStr"));
        Assertions.assertEquals("FUNCTION", row.getString("modelType"));
        Assertions.assertTrue(row.getString("specs").contains("enumList"));
    }
}
