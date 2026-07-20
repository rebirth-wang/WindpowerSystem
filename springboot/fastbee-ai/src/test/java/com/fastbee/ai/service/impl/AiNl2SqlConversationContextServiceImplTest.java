package com.fastbee.ai.service.impl;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fastbee.ai.domain.AiChatMessage;
import com.fastbee.ai.model.vo.AiNl2SqlAuditTrailVO;
import com.fastbee.ai.model.vo.AiNl2SqlContextSnapshotVO;
import com.fastbee.ai.model.vo.AiNl2SqlGenerateResultVO;
import com.fastbee.ai.model.vo.AiQueryPlanVO;

class AiNl2SqlConversationContextServiceImplTest {

    private final AiNl2SqlConversationContextServiceImpl service = new AiNl2SqlConversationContextServiceImpl();

    @Test
    void shouldResolveLatestSnapshotFromUserToolResult() {
        AiChatMessage userMessage = new AiChatMessage();
        userMessage.setRoleType("user");
        userMessage.setMessageStatus("SUCCESS");

        AiNl2SqlContextSnapshotVO snapshot = new AiNl2SqlContextSnapshotVO();
        snapshot.setSerialNumber("DEVICE001");
        snapshot.setPrimarySemantic("空气温度");
        snapshot.setTimeRangeText("最近24小时");

        service.attachContextSnapshot(userMessage, snapshot);

        AiNl2SqlContextSnapshotVO resolved = service.resolveLatestContext(List.of(userMessage));

        Assertions.assertNotNull(resolved);
        Assertions.assertEquals("DEVICE001", resolved.getSerialNumber());
        Assertions.assertEquals("空气温度", resolved.getPrimarySemantic());
        Assertions.assertEquals("最近24小时", resolved.getTimeRangeText());
    }

    @Test
    void shouldEnrichFollowUpQuestionWithSnapshotSerialAndTimeRange() {
        AiChatMessage userMessage = new AiChatMessage();
        userMessage.setRoleType("user");
        userMessage.setMessageStatus("SUCCESS");

        AiNl2SqlContextSnapshotVO snapshot = new AiNl2SqlContextSnapshotVO();
        snapshot.setSerialNumber("DEVICE001");
        snapshot.setPrimarySemantic("空气温度");
        snapshot.setTimeRangeText("最近24小时");
        service.attachContextSnapshot(userMessage, snapshot);

        String executionQuestion = service.enrichExecutionQuestion(
                "继续查趋势",
                null,
                List.of(userMessage)
        );

        Assertions.assertTrue(executionQuestion.contains("serialNumber=DEVICE001"));
        Assertions.assertTrue(executionQuestion.contains("时间范围 最近24小时"));
    }

    @Test
    void shouldBuildNl2SqlContextSnapshotFromGenerateResult() {
        AiQueryPlanVO queryPlan = new AiQueryPlanVO();
        queryPlan.setQueryMode("TSDB_QUERY");
        queryPlan.setRuntimeSource("THING_MODEL_RUNTIME");
        queryPlan.setPrimarySemantic("空气温度");
        queryPlan.setKnowledgeBases(List.of("KB_NL2SQL"));
        queryPlan.setMatchedFieldKeys(List.of("iot_device.serial_number"));
        queryPlan.setRuntimeFieldKeys(List.of("temperature"));
        queryPlan.setCandidateIdentifiers(List.of("temperature"));

        AiNl2SqlAuditTrailVO auditTrail = new AiNl2SqlAuditTrailVO();
        auditTrail.setQueryMode("TSDB_QUERY");
        auditTrail.setRuntimeSource("THING_MODEL_RUNTIME");
        auditTrail.setPrimarySemantic("空气温度");
        auditTrail.setSerialNumber("DEVICE001");
        auditTrail.setDeviceName("智能开关产品10");
        auditTrail.setProductId(100L);
        auditTrail.setProductName("智能开关产品");
        auditTrail.setIdentifier("temperature");

        AiNl2SqlGenerateResultVO generateResult = new AiNl2SqlGenerateResultVO();
        generateResult.setQueryMode("TSDB_QUERY");
        generateResult.setSummary("已查询设备最近24小时温度趋势。");
        generateResult.setQueryPlan(queryPlan);
        generateResult.setAuditTrail(auditTrail);

        AiNl2SqlContextSnapshotVO snapshot = service.buildContextSnapshot(
                "查询这台设备最近24小时温度趋势",
                generateResult
        );

        Assertions.assertNotNull(snapshot);
        Assertions.assertEquals("DEVICE001", snapshot.getSerialNumber());
        Assertions.assertEquals("智能开关产品10", snapshot.getDeviceName());
        Assertions.assertEquals("空气温度", snapshot.getPrimarySemantic());
        Assertions.assertEquals("temperature", snapshot.getIdentifier());
        Assertions.assertEquals("最近24小时", snapshot.getTimeRangeText());
        Assertions.assertEquals("已查询设备最近24小时温度趋势。", snapshot.getLastSummary());
        Assertions.assertTrue(snapshot.getCandidateIdentifiers().contains("temperature"));
    }
}
