package com.fastbee.ai.support;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AiCandidateMatchSupportTest {

    @Test
    void shouldNormalizeText() {
        String normalized = AiCandidateMatchSupport.normalizeText("  当前-温度（设备） ");
        Assertions.assertEquals("当前温度设备", normalized);
    }

    @Test
    void shouldNormalizeTemplateDecorators() {
        String normalized = AiCandidateMatchSupport.normalizeText("查询设备“智能开关”当前{温度}是多少");

        Assertions.assertEquals("查询设备智能开关当前温度是多少", normalized);
    }

    @Test
    void shouldStripSlotDecorators() {
        String stripped = AiCandidateMatchSupport.stripSlotDecorators("{“智能开关”}");

        Assertions.assertEquals("智能开关", stripped);
    }

    @Test
    void shouldMatchAliasesByExactAndFuzzy() {
        List<String> aliases = List.of("温度", "当前温度", "temperature");

        Assertions.assertTrue(AiCandidateMatchSupport.matchesAliases(aliases, "温度", true));
        Assertions.assertTrue(AiCandidateMatchSupport.matchesAliases(aliases, "当前温", false));
        Assertions.assertFalse(AiCandidateMatchSupport.matchesAliases(aliases, "湿度", false));
    }

    @Test
    void shouldDeduplicateAndLimitByKey() {
        List<String> candidates = List.of("SN-001", "SN-002", "SN-001", "SN-003");

        List<String> results = AiCandidateMatchSupport.deduplicateAndLimit(candidates, item -> item, 2);

        Assertions.assertEquals(2, results.size());
        Assertions.assertEquals("SN-001", results.get(0));
        Assertions.assertEquals("SN-002", results.get(1));
    }
}
