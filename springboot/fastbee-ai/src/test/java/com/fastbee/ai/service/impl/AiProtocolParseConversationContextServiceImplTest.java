package com.fastbee.ai.service.impl;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fastbee.ai.domain.AiChatMessage;
import com.fastbee.ai.model.vo.AiProtocolKnowledgeContextVO;
import com.fastbee.ai.model.vo.AiProtocolKnowledgeItemVO;
import com.fastbee.ai.model.vo.AiProtocolParseContextSnapshotVO;

class AiProtocolParseConversationContextServiceImplTest {

    private final AiProtocolParseConversationContextServiceImpl service = new AiProtocolParseConversationContextServiceImpl();

    @Test
    void shouldResolveLatestSnapshotFromUserToolResult() {
        AiChatMessage userMessage = new AiChatMessage();
        userMessage.setRoleType("user");
        userMessage.setMessageStatus("SUCCESS");

        AiProtocolParseContextSnapshotVO snapshot = new AiProtocolParseContextSnapshotVO();
        snapshot.setModuleName("遥测上报");
        snapshot.setFieldName("温度");
        snapshot.setFieldCode("temp");
        service.attachContextSnapshot(userMessage, snapshot);

        AiProtocolParseContextSnapshotVO resolved = service.resolveLatestContext(List.of(userMessage));

        Assertions.assertNotNull(resolved);
        Assertions.assertEquals("遥测上报", resolved.getModuleName());
        Assertions.assertEquals("温度", resolved.getFieldName());
        Assertions.assertEquals("temp", resolved.getFieldCode());
    }

    @Test
    void shouldEnrichFollowUpQuestionWithProtocolHints() {
        AiChatMessage userMessage = new AiChatMessage();
        userMessage.setRoleType("user");
        userMessage.setMessageStatus("SUCCESS");

        AiProtocolParseContextSnapshotVO snapshot = new AiProtocolParseContextSnapshotVO();
        snapshot.setModuleName("遥测上报");
        snapshot.setFieldName("温度");
        snapshot.setFieldCode("temp");
        service.attachContextSnapshot(userMessage, snapshot);

        String enrichedQuestion = service.enrichExecutionQuestion("继续说这个字段怎么解析", List.of(userMessage));

        Assertions.assertTrue(enrichedQuestion.contains("模块 遥测上报"));
        Assertions.assertTrue(enrichedQuestion.contains("字段名称 温度"));
        Assertions.assertTrue(enrichedQuestion.contains("字段编码 temp"));
    }

    @Test
    void shouldBuildContextSnapshotFromProtocolKnowledgeContext() {
        AiProtocolKnowledgeItemVO item = new AiProtocolKnowledgeItemVO();
        item.setModuleName("遥测上报");
        item.setFieldName("温度");
        item.setFieldCode("temp");
        item.setDataType("float");
        item.setSampleValue("23.5");
        item.setValueMappings("无");
        item.setRemark("设备实时温度");
        item.setAliases(List.of("temperature"));

        AiProtocolKnowledgeContextVO context = new AiProtocolKnowledgeContextVO();
        context.setKbCode("PROTOCOL_SPEC");
        context.setKbName("协议知识库");
        context.setVersionNo("v1");
        context.setRuntimeSource("FILE_SNAPSHOT");
        context.setItems(List.of(item));

        AiProtocolParseContextSnapshotVO snapshot = service.buildContextSnapshot("这个字段怎么解析", context);

        Assertions.assertNotNull(snapshot);
        Assertions.assertEquals("PROTOCOL_SPEC", snapshot.getKbCode());
        Assertions.assertEquals("协议知识库", snapshot.getKbName());
        Assertions.assertEquals("v1", snapshot.getVersionNo());
        Assertions.assertEquals("FILE_SNAPSHOT", snapshot.getRuntimeSource());
        Assertions.assertEquals("遥测上报", snapshot.getModuleName());
        Assertions.assertEquals("温度", snapshot.getFieldName());
        Assertions.assertEquals("temp", snapshot.getFieldCode());
    }
}
