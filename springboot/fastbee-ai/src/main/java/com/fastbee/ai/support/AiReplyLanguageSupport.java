package com.fastbee.ai.support;

import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;

import com.fastbee.ai.domain.AiChatMessage;
import com.fastbee.common.utils.StringUtils;

/**
 * AI 回复语言决策辅助。
 */
public final class AiReplyLanguageSupport {

    private static final Locale ZH_CN = Locale.SIMPLIFIED_CHINESE;
    private static final Locale EN_US = Locale.US;
    private static final Pattern LATIN_WORD_PATTERN = Pattern.compile("[A-Za-z][A-Za-z']+");
    private static final Pattern CODE_KEYWORD_PATTERN = Pattern.compile(
            "\\b(String|Long|Integer|Boolean|JSONObject|JSONArray|JSONUtil|public|private|protected|class|return|new|if|else|for|while|void|null|true|false)\\b",
            Pattern.CASE_INSENSITIVE);
    private static final int MAX_HISTORY_SCAN = 12;

    private AiReplyLanguageSupport() {
    }

    /**
     * 捕获当前线程的系统语言。
     *
     * @return 当前 Locale
     */
    public static Locale currentLocale() {
        Locale locale = LocaleContextHolder.getLocale();
        return locale == null ? ZH_CN : locale;
    }

    /**
     * 基于用户输入、会话历史和系统语言决策本轮回复语言。
     *
     * @param userMessage 用户本轮消息
     * @param historyMessages 会话历史
     * @param fallbackLocale 系统兜底语言
     * @return 本轮回复 Locale
     */
    public static Locale resolveReplyLocale(String userMessage,
                                            List<AiChatMessage> historyMessages,
                                            Locale fallbackLocale) {
        String languageSource = extractLanguageDecisionSource(userMessage);
        Locale explicitLocale = resolveExplicitLocale(languageSource);
        if (explicitLocale != null) {
            return explicitLocale;
        }
        Locale persistentLocale = resolvePersistentLocale(historyMessages);
        if (persistentLocale != null) {
            return persistentLocale;
        }
        if (isMostlyCodeLike(languageSource)) {
            return normalizeLocale(fallbackLocale);
        }
        Locale dominantLocale = resolveDominantLocale(languageSource);
        if (dominantLocale != null) {
            return dominantLocale;
        }
        return normalizeLocale(fallbackLocale);
    }

    /**
     * 生成可追加到 Prompt 的输出语言约束。
     *
     * @param userMessage 用户本轮消息
     * @param historyMessages 会话历史
     * @param fallbackLocale 系统兜底语言
     * @return Prompt 语言约束
     */
    public static String buildModelInstruction(String userMessage,
                                               List<AiChatMessage> historyMessages,
                                               Locale fallbackLocale) {
        Locale replyLocale = resolveReplyLocale(userMessage, historyMessages, fallbackLocale);
        if (isEnglish(replyLocale)) {
            return "输出语言规则：本轮最终回复请使用英文；表格标题、字段解释、风险说明、建议动作、总结和待确认问题也使用英文。"
                    + "如果返回 JSON 或结构化对象，summary、description、reason、result、suggestion 等自然语言字段也必须使用英文。"
                    + "Do not switch the answer language only because uploaded content, identifiers, JSON keys, SQL, code, or device numbers are in another language. "
                    + "文件名、设备编号、字段名、源码路径、类名、方法名、SQL、identifier、JSON key 和用户原文中的专有名词保持原文。";
        }
        return "输出语言规则：本轮最终回复请使用简体中文；表格标题、字段解释、风险说明、建议动作、总结和待确认问题也使用简体中文。"
                + "如果返回 JSON 或结构化对象，summary、description、reason、result、suggestion 等自然语言字段也必须使用简体中文。"
                + "不要因为附件正文、identifier、JSON key、SQL、代码片段或设备编号是英文，就把自然语言回复切换为英文。"
                + "文件名、设备编号、字段名、源码路径、类名、方法名、SQL、identifier、JSON key 和用户原文中的专有名词保持原文。";
    }

    /**
     * 给 Prompt 追加输出语言约束。
     *
     * @param prompt 原 Prompt
     * @param userMessage 用户本轮消息
     * @param historyMessages 会话历史
     * @param fallbackLocale 系统兜底语言
     * @return 追加语言约束后的 Prompt
     */
    public static String appendModelInstruction(String prompt,
                                                String userMessage,
                                                List<AiChatMessage> historyMessages,
                                                Locale fallbackLocale) {
        StringBuilder builder = new StringBuilder(StringUtils.defaultString(prompt));
        if (builder.length() > 0 && builder.charAt(builder.length() - 1) != '\n') {
            builder.append('\n');
        }
        return builder.append(buildModelInstruction(userMessage, historyMessages, fallbackLocale)).append('\n').toString();
    }

    /**
     * 在指定语言上下文中执行逻辑，避免流式链路切换线程后丢失 Locale。
     *
     * @param locale 目标 Locale
     * @param supplier 执行逻辑
     * @return 执行结果
     */
    public static <T> T callWithLocale(Locale locale, Supplier<T> supplier) {
        LocaleContext previousContext = LocaleContextHolder.getLocaleContext();
        try {
            LocaleContextHolder.setLocale(normalizeLocale(locale));
            return supplier.get();
        } finally {
            LocaleContextHolder.setLocaleContext(previousContext);
        }
    }

    /**
     * 在指定语言上下文中执行无返回逻辑。
     *
     * @param locale 目标 Locale
     * @param runnable 执行逻辑
     */
    public static void runWithLocale(Locale locale, Runnable runnable) {
        LocaleContext previousContext = LocaleContextHolder.getLocaleContext();
        try {
            LocaleContextHolder.setLocale(normalizeLocale(locale));
            runnable.run();
        } finally {
            LocaleContextHolder.setLocaleContext(previousContext);
        }
    }

    private static Locale resolveExplicitLocale(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        String normalized = normalizeForLanguageDirective(text);
        if (containsAny(normalized,
                "用英文", "用英语", "英文回答", "英语回答", "回答英文", "回复英文", "切到英文",
                "answerinenglish", "respondinenglish", "replyinenglish", "useenglish", "inenglish",
                "englishplease", "speakinenglish", "writeinenglish")) {
            return EN_US;
        }
        if (containsAny(normalized,
                "用中文", "中文回答", "汉语回答", "简体中文", "回答中文", "回复中文", "切到中文",
                "answerinchinese", "respondinchinese", "replyinchinese", "usechinese", "inchinese",
                "chineseplease", "simplifiedchinese", "writeinchinese")) {
            return ZH_CN;
        }
        return null;
    }

    private static String extractLanguageDecisionSource(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }
        String source = text;
        int attachmentContextIndex = firstIndexOf(source,
                "[上传文件上下文]",
                "附件正文摘录：",
                "附件正文摘录:",
                "\n文件名：",
                "\n文件名:");
        if (attachmentContextIndex > 0) {
            source = source.substring(0, attachmentContextIndex);
        }
        StringBuilder builder = new StringBuilder(source.length());
        String[] lines = source.split("\\R");
        for (String line : lines) {
            String trimmed = line == null ? "" : line.trim();
            if (trimmed.matches("(?i)^(serialNumber|deviceNumber|deviceName|productName|identifier)\\s*=.*$")) {
                continue;
            }
            if (trimmed.startsWith("物模型 ") || trimmed.startsWith("指标 ")) {
                continue;
            }
            builder.append(line).append('\n');
        }
        String cleaned = builder.toString().trim();
        return StringUtils.isBlank(cleaned) ? text : cleaned;
    }

    private static int firstIndexOf(String text, String... markers) {
        if (StringUtils.isBlank(text) || markers == null) {
            return -1;
        }
        int firstIndex = -1;
        for (String marker : markers) {
            if (StringUtils.isBlank(marker)) {
                continue;
            }
            int index = text.indexOf(marker);
            if (index >= 0 && (firstIndex < 0 || index < firstIndex)) {
                firstIndex = index;
            }
        }
        return firstIndex;
    }

    private static Locale resolvePersistentLocale(List<AiChatMessage> historyMessages) {
        if (historyMessages == null || historyMessages.isEmpty()) {
            return null;
        }
        int scanned = 0;
        for (int index = historyMessages.size() - 1; index >= 0 && scanned < MAX_HISTORY_SCAN; index--) {
            AiChatMessage item = historyMessages.get(index);
            if (item == null || !"user".equalsIgnoreCase(item.getRoleType())
                    || StringUtils.isBlank(item.getMessageContent())) {
                continue;
            }
            scanned++;
            String normalized = normalizeForLanguageDirective(item.getMessageContent());
            if (!containsAny(normalized, "以后", "后续", "接下来", "之后", "后面", "fromnowon", "always", "nexttime")) {
                continue;
            }
            Locale locale = resolveExplicitLocale(item.getMessageContent());
            if (locale != null) {
                return locale;
            }
        }
        return null;
    }

    private static boolean isMostlyCodeLike(String text) {
        if (StringUtils.isBlank(text)) {
            return false;
        }
        int cjkCount = 0;
        int codeSymbolCount = 0;
        for (int index = 0; index < text.length(); index++) {
            char ch = text.charAt(index);
            Character.UnicodeScript script = Character.UnicodeScript.of(ch);
            if (script == Character.UnicodeScript.HAN
                    || script == Character.UnicodeScript.HIRAGANA
                    || script == Character.UnicodeScript.KATAKANA
                    || script == Character.UnicodeScript.HANGUL) {
                cjkCount++;
            }
            if (";{}=<>".indexOf(ch) >= 0) {
                codeSymbolCount++;
            }
        }
        int latinWordCount = 0;
        Matcher latinMatcher = LATIN_WORD_PATTERN.matcher(text);
        while (latinMatcher.find()) {
            latinWordCount++;
        }
        int keywordCount = 0;
        Matcher keywordMatcher = CODE_KEYWORD_PATTERN.matcher(text);
        while (keywordMatcher.find()) {
            keywordCount++;
        }
        boolean hasCallLikeExpression = Pattern.compile("\\b[A-Za-z_]\\w*\\s*\\.\\s*[A-Za-z_]\\w*\\s*\\(").matcher(text).find();
        boolean hasLineOrBlockComment = text.contains("//") || text.contains("/*") || text.contains("```");
        int codeSignalCount = codeSymbolCount + keywordCount + (hasCallLikeExpression ? 2 : 0) + (hasLineOrBlockComment ? 1 : 0);
        return cjkCount == 0 && latinWordCount >= 2 && codeSignalCount >= 3;
    }

    private static Locale resolveDominantLocale(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        int cjkCount = 0;
        for (int index = 0; index < text.length(); index++) {
            Character.UnicodeScript script = Character.UnicodeScript.of(text.charAt(index));
            if (script == Character.UnicodeScript.HAN
                    || script == Character.UnicodeScript.HIRAGANA
                    || script == Character.UnicodeScript.KATAKANA
                    || script == Character.UnicodeScript.HANGUL) {
                cjkCount++;
            }
        }
        int latinWordCount = 0;
        Matcher matcher = LATIN_WORD_PATTERN.matcher(text);
        while (matcher.find()) {
            latinWordCount++;
        }
        if (cjkCount >= 2 && cjkCount >= latinWordCount * 2) {
            return ZH_CN;
        }
        if (latinWordCount >= 3 && latinWordCount > cjkCount / 2) {
            return EN_US;
        }
        if (cjkCount >= 4) {
            return ZH_CN;
        }
        return null;
    }

    private static Locale normalizeLocale(Locale locale) {
        return isEnglish(locale) ? EN_US : ZH_CN;
    }

    private static boolean isEnglish(Locale locale) {
        return locale != null && "en".equalsIgnoreCase(locale.getLanguage());
    }

    private static String normalizeForLanguageDirective(String text) {
        return StringUtils.defaultString(text)
                .toLowerCase(Locale.ROOT)
                .replaceAll("[\\s\\-_/（）()【】\\[\\]、，,；;:：。.!！?？\"'`]+", "");
    }

    private static boolean containsAny(String content, String... keywords) {
        if (StringUtils.isBlank(content) || keywords == null) {
            return false;
        }
        for (String keyword : keywords) {
            if (StringUtils.isNotBlank(keyword) && content.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}
