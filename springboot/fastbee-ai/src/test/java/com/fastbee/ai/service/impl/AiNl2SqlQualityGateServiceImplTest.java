package com.fastbee.ai.service.impl;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.fastbee.ai.domain.AiKnowledgeBase;
import com.fastbee.ai.domain.AiKnowledgeVersion;
import com.fastbee.ai.model.vo.AiKnowledgeVersionQualityCheckVO;
import com.fastbee.ai.model.vo.AiKnowledgeVersionQualityIssueVO;

/**
 * NL2SQL 企业真实问句质量门禁回归测试。
 */
class AiNl2SqlQualityGateServiceImplTest {

    private final AiNl2SqlQualityGateServiceImpl service = new AiNl2SqlQualityGateServiceImpl();

    @TempDir
    Path tempDir;

    @Test
    void shouldPassExpandedEnterpriseQuestionRegressionSet() throws Exception {
        JSONObject snapshot = buildBaseSnapshot();
        snapshot.put("questionExamples", arrayOf(
                question("统计未处理告警数量", "告警记录", "iot_alert_log", "iot_alert_log",
                        "iot_alert COUNT(*)", "COUNT(*) + 状态字典映射", "P0"),
                question("统计在线设备占比", "设备", "iot_device", "iot_device",
                        "只返回在线数量或未计算设备总量分母", "COUNT_IF(status=在线)/COUNT(*)", "P0"),
                question("统计每个产品下在线设备数量", "产品 + 设备", "iot_product + iot_device", "iot_product;iot_device",
                        "只查产品主数据或未按设备状态映射过滤", "GROUP BY 产品ID + 产品名称 + 状态字典映射", "P0"),
                question("统计近7天告警趋势", "告警记录", "iot_alert_log", "iot_alert_log",
                        "iot_alert COUNT(*) 或不带时间窗口", "COUNT(*) + GROUP BY 日期", "P0"),
                question("查询待处理告警数量", "告警记录", "iot_alert_log", "iot_alert_log",
                        "iot_work_order COUNT(*) 或 iot_alert COUNT(*)", "COUNT(*) + 状态字典映射", "P0")
        ));

        AiKnowledgeVersionQualityCheckVO result = check(snapshot);

        Assertions.assertTrue(Boolean.TRUE.equals(result.getPassed()));
        Assertions.assertEquals(5, result.getCheckedQuestionCount());
        Assertions.assertEquals(5, result.getP0QuestionCount());
        Assertions.assertEquals(0, result.getBlockingIssueCount());
    }

    @Test
    void shouldBlockRatioQuestionWhenRatioMetricRuleMissing() throws Exception {
        JSONObject snapshot = buildBaseSnapshot();
        snapshot.put("metricRules", arrayOf(
                metric("DEVICE_COUNT", "设备总量", "设备", "iot_device", "COUNT(*)", "", "", "RDB_SQL"),
                metric("ALERT_LOG_COUNT", "告警记录数量", "告警记录", "iot_alert_log", "COUNT(*)", "最近7天按 create_time", "", "RDB_SQL")
        ));
        snapshot.put("questionExamples", arrayOf(
                question("统计在线设备占比", "设备", "iot_device", "iot_device",
                        "只返回在线数量或未计算设备总量分母", "COUNT_IF(status=在线)/COUNT(*)", "P0")
        ));

        AiKnowledgeVersionQualityCheckVO result = check(snapshot);

        Assertions.assertFalse(Boolean.TRUE.equals(result.getPassed()));
        Assertions.assertTrue(result.getIssues().stream()
                .map(AiKnowledgeVersionQualityIssueVO::getMessage)
                .anyMatch(message -> message != null && message.contains("比例计算方式")));
    }

    @Test
    void shouldBlockUnhandledAlertQuestionWhenAlertRuleTableIsMisused() throws Exception {
        JSONObject snapshot = buildBaseSnapshot();
        snapshot.put("questionExamples", arrayOf(
                question("统计未处理告警数量", "告警记录", "iot_alert", "iot_alert",
                        "iot_alert_log COUNT(*)", "COUNT(*) + 状态字典映射", "P0")
        ));

        AiKnowledgeVersionQualityCheckVO result = check(snapshot);

        Assertions.assertFalse(Boolean.TRUE.equals(result.getPassed()));
        Assertions.assertTrue(result.getIssues().stream()
                .map(AiKnowledgeVersionQualityIssueVO::getMessage)
                .anyMatch(message -> message != null && message.contains("告警记录表 iot_alert_log")));
    }

    @Test
    void shouldBlockPendingAlertQuestionWhenWorkOrderTableIsMisused() throws Exception {
        JSONObject snapshot = buildBaseSnapshot();
        snapshot.put("questionExamples", arrayOf(
                question("查询待处理告警数量", "工单", "iot_work_order", "iot_work_order",
                        "iot_alert_log COUNT(*)", "COUNT(*) + 状态字典映射", "P0")
        ));

        AiKnowledgeVersionQualityCheckVO result = check(snapshot);

        Assertions.assertFalse(Boolean.TRUE.equals(result.getPassed()));
        Assertions.assertTrue(result.getIssues().stream()
                .map(AiKnowledgeVersionQualityIssueVO::getMessage)
                .anyMatch(message -> message != null && message.contains("告警记录表 iot_alert_log")));
    }

    private AiKnowledgeVersionQualityCheckVO check(JSONObject snapshot) throws Exception {
        Path snapshotPath = tempDir.resolve("snapshot.json");
        Files.writeString(snapshotPath, JSON.toJSONString(snapshot), StandardCharsets.UTF_8);
        AiKnowledgeBase knowledgeBase = new AiKnowledgeBase();
        knowledgeBase.setKnowledgeBaseId(1L);
        knowledgeBase.setKbType("NL2SQL_SEMANTIC");
        AiKnowledgeVersion version = new AiKnowledgeVersion();
        version.setVersionId(1L);
        version.setVersionNo("VER_TEST");
        version.setSnapshotPath(snapshotPath.toString());
        return service.checkBeforePublish(knowledgeBase, version);
    }

    private JSONObject buildBaseSnapshot() {
        JSONObject snapshot = new JSONObject();
        snapshot.put("items", arrayOf(
                semanticItem("iot_device", "device_id", "MANUAL", ""),
                semanticItem("iot_device", "product_id", "MANUAL", ""),
                semanticItem("iot_device", "status", "DICT", "在线=1;离线=0"),
                semanticItem("iot_product", "product_id", "MANUAL", ""),
                semanticItem("iot_alert", "alert_id", "MANUAL", ""),
                semanticItem("iot_alert", "status", "DICT", "启用=1;停用=0"),
                semanticItem("iot_alert_log", "alert_log_id", "MANUAL", ""),
                semanticItem("iot_alert_log", "status", "DICT", "不需要处理=1;未处理=2;待处理=2;已处理=3"),
                semanticItem("iot_alert_log", "create_time", "MANUAL", "")
        ));
        snapshot.put("tables", arrayOf("iot_device", "iot_product", "iot_alert", "iot_alert_log"));
        snapshot.put("tableSemantics", arrayOf(
                tableSemantic("iot_device"),
                tableSemantic("iot_product"),
                tableSemantic("iot_alert"),
                tableSemantic("iot_alert_log")
        ));
        snapshot.put("businessObjects", arrayOf(
                businessObject("DEVICE", "设备", "设备;设备数量;在线设备", "iot_device"),
                businessObject("PRODUCT_DEVICE_RELATION", "产品 + 设备", "每个产品下设备数量;按产品统计在线设备", "iot_device"),
                businessObject("ALERT_RULE", "告警规则", "告警规则;报警规则", "iot_alert"),
                businessObject("ALERT_LOG", "告警记录", "告警记录;未处理告警;待处理告警;告警趋势", "iot_alert_log")
        ));
        snapshot.put("metricRules", arrayOf(
                metric("DEVICE_COUNT", "设备总量", "设备", "iot_device", "COUNT(*)", "", "", "RDB_SQL"),
                metric("DEVICE_ONLINE_RATIO", "在线设备占比", "设备", "iot_device",
                        "COUNT_IF(status=在线)/COUNT(*)", "按当前设备主数据统计", "", "RDB_SQL"),
                metric("PRODUCT_ONLINE_DEVICE_COUNT", "每个产品在线设备数量", "产品 + 设备", "iot_device",
                        "GROUP BY product_id + product_name + COUNT(*)", "按当前设备主数据统计", "", "RDB_SQL"),
                metric("ALERT_LOG_TREND", "告警记录趋势", "告警记录", "iot_alert_log",
                        "COUNT(*) + GROUP BY day", "最近7天按 create_time", "", "RDB_SQL"),
                metric("ALERT_LOG_PENDING_COUNT", "待处理告警数量", "告警记录", "iot_alert_log",
                        "COUNT(*) + status=未处理", "按当前告警记录处理状态统计", "", "RDB_SQL")
        ));
        return snapshot;
    }

    private JSONObject semanticItem(String tableName, String columnName, String sourceType, String valueMappings) {
        JSONObject item = new JSONObject();
        item.put("tableName", tableName);
        item.put("columnName", columnName);
        item.put("sourceType", sourceType);
        item.put("valueMappings", valueMappings);
        return item;
    }

    private JSONObject tableSemantic(String tableName) {
        JSONObject item = new JSONObject();
        item.put("tableName", tableName);
        return item;
    }

    private JSONObject businessObject(String code, String name, String aliases, String primaryTable) {
        JSONObject item = new JSONObject();
        item.put("businessObjectCode", code);
        item.put("businessObjectName", name);
        item.put("aliases", aliases);
        item.put("primaryTable", primaryTable);
        return item;
    }

    private JSONObject metric(String code, String name, String businessObjectName, String primaryTable,
                              String aggregationType, String timeRule, String distinctColumn, String dataSource) {
        JSONObject item = new JSONObject();
        item.put("metricRuleCode", code);
        item.put("metricRuleName", name);
        item.put("businessObjectName", businessObjectName);
        item.put("primaryTable", primaryTable);
        item.put("aggregationType", aggregationType);
        item.put("timeRule", timeRule);
        item.put("distinctColumn", distinctColumn);
        item.put("defaultDataSource", dataSource);
        item.put("stateRule", "状态口径已由字段语义维护");
        item.put("applicableQuestion", name);
        return item;
    }

    private JSONObject question(String question, String expectedBusinessObject, String expectedSource,
                                String allowedTables, String forbiddenTables, String expectedAction, String riskLevel) {
        JSONObject item = new JSONObject();
        item.put("question", question);
        item.put("expectedBusinessObject", expectedBusinessObject);
        item.put("expectedSource", expectedSource);
        item.put("allowedTables", allowedTables);
        item.put("forbiddenTables", forbiddenTables);
        item.put("expectedAction", expectedAction);
        item.put("riskLevel", riskLevel);
        item.put("rowNum", 2);
        return item;
    }

    private JSONArray arrayOf(Object... values) {
        JSONArray array = new JSONArray();
        if (values != null) {
            for (Object value : values) {
                array.add(value);
            }
        }
        return array;
    }
}
