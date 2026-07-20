package com.fastbee.ai.support;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 源码导航回答后置安全拦截测试。
 */
class AiCodebaseAnswerSanitizerTest {

    @Test
    void shouldKeepCodebaseNavigationButRemoveRealSourceBlock() {
        String answer = """
                | 源码路径 | 类/组件 | 方法/接口 | 职责 |
                | --- | --- | --- | --- |
                | springboot/fastbee-ai/src/main/java/com/fastbee/ai/controller/AiChatController.java | AiChatController | send | AI 会话入口 |

                ```java
                @PostMapping("/send")
                public AjaxResult send(@RequestBody AiChatSendRequest request) {
                    return AjaxResult.success(aiChatService.send(request));
                }
                ```
                """;

        AiCodebaseAnswerSanitizer.SanitizeResult result = AiCodebaseAnswerSanitizer.sanitize(answer, true);

        Assertions.assertTrue(result.isChanged());
        Assertions.assertTrue(result.getContent().contains("AiChatController"));
        Assertions.assertTrue(result.getContent().contains("springboot/fastbee-ai/src/main/java/com/fastbee/ai/controller/AiChatController.java"));
        Assertions.assertFalse(result.getContent().contains("public AjaxResult send"));
        Assertions.assertFalse(result.getContent().contains("@PostMapping"));
        Assertions.assertTrue(result.getContent().contains("安全提示"));
    }

    @Test
    void shouldRedactSensitiveConfigurationValues() {
        String answer = """
                可以关注 application-dev.yml 中的配置项名称，但不要输出配置值：
                spring.datasource.password=fastbee123456
                apiKey: sk-abc123456789
                jdbc:mysql://127.0.0.1:3306/fastbee?user=root&password=123456
                """;

        AiCodebaseAnswerSanitizer.SanitizeResult result = AiCodebaseAnswerSanitizer.sanitize(answer, true);

        Assertions.assertTrue(result.isChanged());
        Assertions.assertFalse(result.getContent().contains("fastbee123456"));
        Assertions.assertFalse(result.getContent().contains("sk-abc123456789"));
        Assertions.assertFalse(result.getContent().contains("jdbc:mysql://127.0.0.1"));
        Assertions.assertTrue(result.getContent().contains("[已拦截敏感值]")
                || result.getContent().contains("[已拦截连接串]"));
    }

    @Test
    void shouldRemoveSqlAndMapperXmlLinesInStrictMode() {
        String answer = """
                数据落库可以先看 FunctionLogMapper 和 iot_function_log。
                SELECT * FROM iot_function_log WHERE message_id = #{messageId}
                <select id="selectLogByMessageId" resultType="FunctionLog">
                """;

        AiCodebaseAnswerSanitizer.SanitizeResult result = AiCodebaseAnswerSanitizer.sanitize(answer, true);

        Assertions.assertTrue(result.isChanged());
        Assertions.assertTrue(result.getContent().contains("FunctionLogMapper"));
        Assertions.assertTrue(result.getContent().contains("iot_function_log"));
        Assertions.assertFalse(result.getContent().contains("SELECT *"));
        Assertions.assertFalse(result.getContent().contains("<select"));
    }

    @Test
    void shouldOnlyRedactSecretsInNonStrictMode() {
        String answer = """
                ```java
                public void demo() {
                    return;
                }
                ```
                token=abc123456789
                """;

        AiCodebaseAnswerSanitizer.SanitizeResult result = AiCodebaseAnswerSanitizer.sanitize(answer, false);

        Assertions.assertTrue(result.isChanged());
        Assertions.assertTrue(result.getContent().contains("public void demo"));
        Assertions.assertFalse(result.getContent().contains("abc123456789"));
    }
}
