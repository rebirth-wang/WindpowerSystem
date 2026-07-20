package com.fastbee.ai.support;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Function;

import com.fastbee.common.utils.StringUtils;

/**
 * AI 候选匹配公共工具。
 */
public final class AiCandidateMatchSupport {

    private static final String SLOT_BOUNDARY_DECORATOR_REGEX = "[\\s\"'`“”‘’{}｛｝]+";

    private AiCandidateMatchSupport() {
    }

    /**
     * 统一文本归一。
     */
    public static String normalizeText(String text) {
        if (StringUtils.isBlank(text)) {
            return "";
        }
        return text.trim()
                .replaceAll("[\\s\\-_.()（）【】\\[\\]{}｛｝,，。；;:：？?！!\"'`“”‘’★<>《》]+", "")
                .toLowerCase(Locale.ROOT);
    }

    /**
     * 剥离问句模板变量外层的装饰符，避免用户保留引号或占位符括号后影响检索。
     */
    public static String stripSlotDecorators(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        String value = text.trim()
                .replaceAll("^" + SLOT_BOUNDARY_DECORATOR_REGEX, "")
                .replaceAll(SLOT_BOUNDARY_DECORATOR_REGEX + "$", "")
                .trim();
        return StringUtils.isBlank(value) ? null : value;
    }

    /**
     * 按别名集合判断是否命中。
     */
    public static boolean matchesAliases(Iterable<String> aliases, String targetText, boolean exactMatch) {
        String normalizedTarget = normalizeText(targetText);
        if (aliases == null || StringUtils.isBlank(normalizedTarget)) {
            return false;
        }
        for (String alias : aliases) {
            String normalizedAlias = normalizeText(alias);
            if (StringUtils.isBlank(normalizedAlias)) {
                continue;
            }
            if (exactMatch) {
                if (normalizedAlias.equals(normalizedTarget)) {
                    return true;
                }
                continue;
            }
            if (normalizedAlias.contains(normalizedTarget) || normalizedTarget.contains(normalizedAlias)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 按唯一键去重并限制数量。
     */
    public static <T> List<T> deduplicateAndLimit(List<T> candidates, Function<T, String> keyExtractor, int limit) {
        List<T> results = new ArrayList<>();
        if (candidates == null || candidates.isEmpty() || keyExtractor == null) {
            return results;
        }
        int safeLimit = limit <= 0 ? Integer.MAX_VALUE : limit;
        Set<String> keys = new LinkedHashSet<>();
        for (T candidate : candidates) {
            if (candidate == null) {
                continue;
            }
            String key = keyExtractor.apply(candidate);
            if (StringUtils.isBlank(key)) {
                continue;
            }
            if (keys.add(key.trim())) {
                results.add(candidate);
            }
            if (results.size() >= safeLimit) {
                break;
            }
        }
        return results;
    }
}
