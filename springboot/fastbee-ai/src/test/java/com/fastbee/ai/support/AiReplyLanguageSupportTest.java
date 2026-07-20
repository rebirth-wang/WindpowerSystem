package com.fastbee.ai.support;

import java.util.Locale;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AiReplyLanguageSupportTest {

    @Test
    void shouldKeepChineseForAttachmentQuestionWithEnglishJsonBody() {
        String question = """
                这个文件中的数据有哪些问题

                [上传文件上下文]
                文件名：writ_sixty_0m+iai_2026-05-22.json
                处理要求：请优先依据附件正文回答；若附件信息不足，请明确指出缺少的信息，不要编造。
                附件正文摘录：
                {"id_token":"long jwt string","access_token":"long jwt string","refresh_token":"long jwt string"}
                """;

        Locale locale = AiReplyLanguageSupport.resolveReplyLocale(question, null, Locale.SIMPLIFIED_CHINESE);

        Assertions.assertEquals(Locale.SIMPLIFIED_CHINESE, locale);
        String instruction = AiReplyLanguageSupport.buildModelInstruction(question, null, Locale.SIMPLIFIED_CHINESE);
        Assertions.assertTrue(instruction.contains("请使用简体中文"));
        Assertions.assertTrue(instruction.contains("自然语言字段也必须使用简体中文"));
        Assertions.assertTrue(instruction.contains("不要因为附件正文"));
    }

    @Test
    void shouldKeepChineseForDeviceRuntimeQuestionWithExecutionHints() {
        String question = """
                查询DEVICE88888888最近1小时灯光色值趋势
                serialNumber=DEVICE88888888
                identifier=array_01_light_color
                物模型 灯光色值1
                指标 灯光色值1
                """;

        Locale locale = AiReplyLanguageSupport.resolveReplyLocale(question, null, Locale.SIMPLIFIED_CHINESE);

        Assertions.assertEquals(Locale.SIMPLIFIED_CHINESE, locale);
        String instruction = AiReplyLanguageSupport.buildModelInstruction(question, null, Locale.SIMPLIFIED_CHINESE);
        Assertions.assertTrue(instruction.contains("请使用简体中文"));
        Assertions.assertTrue(instruction.contains("不要因为附件正文"));
    }

    @Test
    void shouldKeepEnglishForPlainEnglishQuestion() {
        Locale locale = AiReplyLanguageSupport.resolveReplyLocale(
                "Please analyze this uploaded JSON file",
                null,
                Locale.SIMPLIFIED_CHINESE);

        Assertions.assertEquals(Locale.US, locale);
    }

    @Test
    void shouldUseFallbackLocaleForPureCodeSnippet() {
        Locale locale = AiReplyLanguageSupport.resolveReplyLocale(
                "String payload = msgContext.getPayload();",
                null,
                Locale.SIMPLIFIED_CHINESE);

        Assertions.assertEquals(Locale.SIMPLIFIED_CHINESE, locale);
    }
}
