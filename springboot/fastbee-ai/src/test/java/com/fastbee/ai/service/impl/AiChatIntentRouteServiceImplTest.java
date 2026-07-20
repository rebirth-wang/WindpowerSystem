package com.fastbee.ai.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fastbee.ai.domain.AiChatMessage;

class AiChatIntentRouteServiceImplTest {

    private final AiChatIntentRouteServiceImpl service = new AiChatIntentRouteServiceImpl();

    @Test
    void shouldFilterHighRiskAssistantHistoryWhenBuildingRoutePrompt() throws Exception {
        String prompt = String.valueOf(invokePrivateMethod(
                "buildPrompt",
                new Class[]{String.class, String.class, List.class},
                "帮我继续刚才那个问题",
                "AUTO",
                List.of(
                        buildMessage("user", "先帮我看看告警情况", null),
                        buildMessage("assistant", "已生成 SQL：select * from alert_log", "NL2SQL"),
                        buildMessage("assistant", "已向设备下发开关指令", "DEVICE_CONTROL"),
                        buildMessage("assistant", "你可以在设备管理菜单继续查看", "PLATFORM_ASSISTANT"),
                        buildMessage("assistant", "刚才是普通说明", "GENERAL_CHAT"),
                        buildMessage("assistant", "这是协议解析结果", "PROTOCOL_PARSE")
                )
        ));

        Assertions.assertTrue(prompt.contains("先帮我看看告警情况"));
        Assertions.assertTrue(prompt.contains("你可以在设备管理菜单继续查看"));
        Assertions.assertTrue(prompt.contains("刚才是普通说明"));
        Assertions.assertFalse(prompt.contains("已生成 SQL"));
        Assertions.assertFalse(prompt.contains("已向设备下发开关指令"));
        Assertions.assertFalse(prompt.contains("这是协议解析结果"));
    }

    private AiChatMessage buildMessage(String roleType, String content, String abilityType) {
        AiChatMessage message = new AiChatMessage();
        message.setRoleType(roleType);
        message.setMessageContent(content);
        message.setAbilityType(abilityType);
        message.setMessageStatus("SUCCESS");
        return message;
    }

    private Object invokePrivateMethod(String methodName, Class<?>[] parameterTypes, Object... args) throws Exception {
        Method method = AiChatIntentRouteServiceImpl.class.getDeclaredMethod(methodName, parameterTypes);
        method.setAccessible(true);
        try {
            return method.invoke(service, args);
        } catch (InvocationTargetException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof Exception exception) {
                throw exception;
            }
            throw ex;
        }
    }
}
