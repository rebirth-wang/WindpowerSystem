package com.fastbee.ai.support;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fastbee.ai.model.vo.AiNl2SqlStructuredResultVO;
import com.fastbee.common.exception.ServiceException;

/**
 * AI 智能问数结构化解析器测试。
 */
class AiNl2SqlStructuredParserTest {

    private final AiNl2SqlStructuredParser parser = new AiNl2SqlStructuredParser();

    @Test
    void shouldParseStrictJsonResponse() {
        String response = """
                {"sql":"SELECT COUNT(*) AS online_device_count FROM iot_device WHERE status = 'online'","summary":"统计状态为在线的设备数量","confidence":0.93,"tables":["iot_device"]}
                """;

        AiNl2SqlStructuredResultVO result = parser.parse("统计在线设备数量", response);

        Assertions.assertEquals("SUCCESS", result.getParseStatus());
        Assertions.assertTrue(Boolean.TRUE.equals(result.getStructuredOutput()));
        Assertions.assertEquals("统计状态为在线的设备数量", result.getSummary());
        Assertions.assertEquals(0.93D, result.getConfidence());
        Assertions.assertEquals(List.of("iot_device"), result.getTables());
        Assertions.assertTrue(result.getSql().startsWith("SELECT COUNT(*)"));
    }

    @Test
    void shouldParseJsonCodeBlockResponse() {
        String response = """
                ```json
                {
                  "sql":"SELECT alert_level, COUNT(*) AS total FROM iot_alert_log GROUP BY alert_level",
                  "summary":"统计告警级别分布",
                  "confidence":0.88,
                  "tables":["iot_alert_log"]
                }
                ```
                """;

        AiNl2SqlStructuredResultVO result = parser.parse("统计告警级别分布", response);

        Assertions.assertEquals("SUCCESS", result.getParseStatus());
        Assertions.assertTrue(Boolean.TRUE.equals(result.getStructuredOutput()));
        Assertions.assertEquals(List.of("iot_alert_log"), result.getTables());
        Assertions.assertTrue(result.getSql().contains("GROUP BY alert_level"));
    }

    @Test
    void shouldFallbackWhenJsonMixedIntoSql() {
        String response = """
                SELECT COUNT(*) AS online_device_count FROM iot_device WHERE status = 'online'", "summary": "统计状态为在线的设备数量" })
                """;

        AiNl2SqlStructuredResultVO result = parser.parse("统计在线设备数量", response);

        Assertions.assertEquals("DEGRADED", result.getParseStatus());
        Assertions.assertFalse(Boolean.TRUE.equals(result.getStructuredOutput()));
        Assertions.assertEquals("NL2SQL_STRUCTURED_OUTPUT_FALLBACK", result.getParseErrorCode());
        Assertions.assertEquals(List.of("iot_device"), result.getTables());
        Assertions.assertEquals("SELECT COUNT(*) AS online_device_count FROM iot_device WHERE status = 'online'", result.getSql());
    }

    @Test
    void shouldClampConfidenceAndExtractTablesFromSql() {
        String response = """
                {"sql":"SELECT d.device_name, COUNT(*) AS total FROM iot_device d JOIN iot_function_log f ON d.serial_number = f.serial_number GROUP BY d.device_name","summary":"统计设备调用次数","confidence":3.5}
                """;

        AiNl2SqlStructuredResultVO result = parser.parse("统计设备调用次数", response);

        Assertions.assertEquals(1.0D, result.getConfidence());
        Assertions.assertEquals(List.of("iot_device", "iot_function_log"), result.getTables());
    }

    @Test
    void shouldThrowWhenNoSqlCanBeParsed() {
        String response = "我无法判断，请补充更多上下文，不提供 SQL。";

        ServiceException exception = Assertions.assertThrows(ServiceException.class,
                () -> parser.parse("统计在线设备数量", response));

        Assertions.assertTrue(exception.getMessage().contains("结构化输出解析失败"));
    }
}
