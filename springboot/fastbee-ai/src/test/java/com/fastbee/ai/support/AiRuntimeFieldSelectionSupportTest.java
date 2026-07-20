package com.fastbee.ai.support;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fastbee.ai.model.vo.AiSemanticFieldVO;
import com.fastbee.common.exception.ServiceException;

class AiRuntimeFieldSelectionSupportTest {

    @Test
    void shouldPreferExplicitIdentifierOverFirstCandidate() {
        AiSemanticFieldVO co2 = buildField("co2", "二氧化碳", "REDIS_VALUE");
        AiSemanticFieldVO lightColor = buildField("array_00_light_color", "灯光色值0", "REDIS_VALUE");

        AiSemanticFieldVO runtimeField = AiRuntimeFieldSelectionSupport.resolveRuntimeField(
                "查询智能开关产品10当前灯光色值是多少\n已确认物模型：灯光色值0\nidentifier=array_00_light_color",
                List.of(co2, lightColor),
                "未命中运行时物模型语义",
                "实时值查询"
        );

        Assertions.assertEquals("array_00_light_color", runtimeField.getSourceCode());
        Assertions.assertEquals("灯光色值0", runtimeField.getSemanticName());
    }

    @Test
    void shouldFallbackToFirstCandidateWhenQuestionHasNoExplicitIdentifier() {
        AiSemanticFieldVO co2 = buildField("co2", "二氧化碳", "REDIS_VALUE");
        AiSemanticFieldVO lightColor = buildField("array_00_light_color", "灯光色值0", "REDIS_VALUE");

        AiSemanticFieldVO runtimeField = AiRuntimeFieldSelectionSupport.resolveRuntimeField(
                "查询智能开关产品10当前灯光色值是多少",
                List.of(co2, lightColor),
                "未命中运行时物模型语义",
                "实时值查询"
        );

        Assertions.assertEquals("co2", runtimeField.getSourceCode());
    }

    @Test
    void shouldThrowWhenExplicitIdentifierIsNotInCurrentCandidates() {
        AiSemanticFieldVO co2 = buildField("co2", "二氧化碳", "REDIS_VALUE");

        ServiceException exception = Assertions.assertThrows(ServiceException.class,
                () -> AiRuntimeFieldSelectionSupport.resolveRuntimeField(
                        "查询智能开关产品10当前灯光色值是多少\n已确认物模型：灯光色值0\nidentifier=array_00_light_color",
                        List.of(co2),
                        "未命中运行时物模型语义",
                        "实时值查询"));

        Assertions.assertTrue(exception.getMessage().contains("array_00_light_color"));
        Assertions.assertTrue(exception.getMessage().contains("重新选择"));
    }

    private AiSemanticFieldVO buildField(String sourceCode, String semanticName, String dataSourceType) {
        AiSemanticFieldVO field = new AiSemanticFieldVO();
        field.setSourceCode(sourceCode);
        field.setSemanticName(semanticName);
        field.setDataSourceType(dataSourceType);
        return field;
    }
}
