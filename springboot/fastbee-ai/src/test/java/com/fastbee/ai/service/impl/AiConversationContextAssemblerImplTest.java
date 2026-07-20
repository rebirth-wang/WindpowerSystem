package com.fastbee.ai.service.impl;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fastbee.ai.domain.AiChatMessage;
import com.fastbee.ai.model.enums.AiChatMode;
import com.fastbee.ai.model.vo.AiConversationContextBundleVO;
import com.fastbee.ai.model.vo.AiConversationGlobalContextVO;
import com.fastbee.ai.model.vo.AiDeviceControlContextSnapshotVO;
import com.fastbee.ai.model.vo.AiNl2SqlContextSnapshotVO;
import com.fastbee.ai.model.vo.AiPlatformAssistantContextSnapshotVO;
import com.fastbee.ai.model.vo.AiProtocolParseContextSnapshotVO;
import com.fastbee.ai.service.IAiDeviceControlConversationContextService;
import com.fastbee.ai.service.IAiNl2SqlConversationContextService;
import com.fastbee.ai.service.IAiPlatformAssistantConversationContextService;
import com.fastbee.ai.service.IAiProtocolParseConversationContextService;

@ExtendWith(MockitoExtension.class)
class AiConversationContextAssemblerImplTest {

    @Mock
    private IAiDeviceControlConversationContextService aiDeviceControlConversationContextService;

    @Mock
    private IAiNl2SqlConversationContextService aiNl2SqlConversationContextService;

    @Mock
    private IAiPlatformAssistantConversationContextService aiPlatformAssistantConversationContextService;

    @Mock
    private IAiProtocolParseConversationContextService aiProtocolParseConversationContextService;

    @InjectMocks
    private AiConversationContextAssemblerImpl assembler;

    @Test
    void shouldBuildGlobalContextWithSafeFactsAndPreferDeviceSnapshot() {
        when(aiNl2SqlConversationContextService.resolveLatestContext(anyList())).thenReturn(buildNl2SqlSnapshot("问数设备", "NL-001", "问数产品", "humidity"));
        when(aiDeviceControlConversationContextService.resolveLatestContext(anyList())).thenReturn(buildDeviceSnapshot());
        when(aiPlatformAssistantConversationContextService.resolveLatestContext(anyList())).thenReturn(buildPlatformSnapshot());
        when(aiProtocolParseConversationContextService.resolveLatestContext(anyList())).thenReturn(buildProtocolSnapshot());

        AiConversationContextBundleVO bundle = assembler.buildExecutionContext(
                "继续处理这个问题",
                AiChatMode.GENERAL.name(),
                null,
                List.of(buildUserMessage("先看设备状态"), buildUserMessage("再看平台页面"))
        );

        AiConversationGlobalContextVO globalContext = bundle.getGlobalContext();
        Assertions.assertNotNull(globalContext);
        Assertions.assertEquals("设备A", globalContext.getFocusDeviceName());
        Assertions.assertEquals("SN-001", globalContext.getFocusSerialNumber());
        Assertions.assertEquals("产品A", globalContext.getFocusProductName());
        Assertions.assertEquals("温度", globalContext.getFocusThingModelName());
        Assertions.assertEquals("temp", globalContext.getFocusIdentifier());
        Assertions.assertEquals("系统管理 > 用户管理", globalContext.getFocusMenuPath());
        Assertions.assertEquals("用户管理", globalContext.getFocusPageTitle());
        Assertions.assertEquals("遥测上报", globalContext.getFocusProtocolModuleName());
        Assertions.assertEquals("温度", globalContext.getFocusProtocolFieldName());
        Assertions.assertEquals("temp", globalContext.getFocusProtocolFieldCode());
        Assertions.assertEquals("float", globalContext.getFocusProtocolDataType());
        Assertions.assertEquals(List.of("先看设备状态", "再看平台页面"), globalContext.getRecentUserTopics());
    }

    @Test
    void shouldFallbackToNl2SqlFactsWhenDeviceSnapshotMissing() {
        when(aiDeviceControlConversationContextService.resolveLatestContext(anyList())).thenReturn(null);
        when(aiNl2SqlConversationContextService.resolveLatestContext(anyList())).thenReturn(buildNl2SqlSnapshot("问数设备", "NL-001", "问数产品", "humidity"));
        when(aiPlatformAssistantConversationContextService.resolveLatestContext(anyList())).thenReturn(null);
        when(aiProtocolParseConversationContextService.resolveLatestContext(anyList())).thenReturn(null);

        AiConversationContextBundleVO bundle = assembler.buildExecutionContext(
                "继续统计这个设备",
                AiChatMode.GENERAL.name(),
                null,
                List.of(buildUserMessage("先统计这个设备"))
        );

        AiConversationGlobalContextVO globalContext = bundle.getGlobalContext();
        Assertions.assertNotNull(globalContext);
        Assertions.assertEquals("问数设备", globalContext.getFocusDeviceName());
        Assertions.assertEquals("NL-001", globalContext.getFocusSerialNumber());
        Assertions.assertEquals("问数产品", globalContext.getFocusProductName());
        Assertions.assertEquals("humidity", globalContext.getFocusIdentifier());
    }

    private AiChatMessage buildUserMessage(String content) {
        AiChatMessage message = new AiChatMessage();
        message.setRoleType("user");
        message.setMessageStatus("SUCCESS");
        message.setMessageContent(content);
        return message;
    }

    private AiDeviceControlContextSnapshotVO buildDeviceSnapshot() {
        AiDeviceControlContextSnapshotVO snapshot = new AiDeviceControlContextSnapshotVO();
        snapshot.setDeviceName("设备A");
        snapshot.setSerialNumber("SN-001");
        snapshot.setProductName("产品A");
        snapshot.setThingModelName("温度");
        snapshot.setIdentifier("temp");
        return snapshot;
    }

    private AiNl2SqlContextSnapshotVO buildNl2SqlSnapshot(String deviceName,
                                                           String serialNumber,
                                                           String productName,
                                                           String identifier) {
        AiNl2SqlContextSnapshotVO snapshot = new AiNl2SqlContextSnapshotVO();
        snapshot.setDeviceName(deviceName);
        snapshot.setSerialNumber(serialNumber);
        snapshot.setProductName(productName);
        snapshot.setIdentifier(identifier);
        return snapshot;
    }

    private AiPlatformAssistantContextSnapshotVO buildPlatformSnapshot() {
        AiPlatformAssistantContextSnapshotVO snapshot = new AiPlatformAssistantContextSnapshotVO();
        snapshot.setMenuPath("系统管理 > 用户管理");
        snapshot.setPageTitle("用户管理");
        return snapshot;
    }

    private AiProtocolParseContextSnapshotVO buildProtocolSnapshot() {
        AiProtocolParseContextSnapshotVO snapshot = new AiProtocolParseContextSnapshotVO();
        snapshot.setModuleName("遥测上报");
        snapshot.setFieldName("温度");
        snapshot.setFieldCode("temp");
        snapshot.setDataType("float");
        return snapshot;
    }
}
