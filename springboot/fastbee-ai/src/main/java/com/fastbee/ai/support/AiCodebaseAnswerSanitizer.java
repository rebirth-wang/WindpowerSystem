package com.fastbee.ai.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 源码导航回答后置安全拦截器。
 *
 * <p>该拦截器只保留路径、类名、方法名、接口名等导航信息，拦截模型可能外溢的真实源码片段、
 * SQL/XML/配置原文和敏感值。它是 Prompt 约束之外的确定性兜底。</p>
 */
public final class AiCodebaseAnswerSanitizer {

    private static final String SOURCE_BLOCK_PLACEHOLDER = "（已拦截疑似真实源码/配置代码块，请改用中文步骤化伪流程描述。）";
    private static final String SOURCE_LINE_PLACEHOLDER = "（已拦截疑似真实源码/SQL/配置原文行，仅保留源码导航信息。）";
    private static final String SAFETY_NOTICE = "安全提示：已自动拦截疑似真实源码片段或敏感配置，仅保留源码导航信息和伪代码级思路。";

    private static final Pattern FENCED_CODE_BLOCK_PATTERN = Pattern.compile("(?s)```[^\\r\\n]*\\R.*?```");
    private static final Pattern SENSITIVE_ASSIGNMENT_PATTERN = Pattern.compile(
            "(?i)^\\s*([\\w.\\-]*(?:password|passwd|pwd|secret|token|api[-_]?key|access[-_]?key|secret[-_]?key|private[-_]?key|jdbc|datasource|redis|mqtt|oss|endpoint|authorization|auth)[\\w.\\-]*)\\s*[:=：]\\s*(.+)$");
    private static final Pattern INLINE_SENSITIVE_PATTERN = Pattern.compile(
            "(?i)(password|passwd|pwd|secret|token|api[-_ ]?key|access[-_ ]?key|secret[-_ ]?key|private[-_ ]?key|authorization|auth)\\s*[:=：]\\s*([^\\s,，;；)）]+)");
    private static final Pattern CONNECTION_STRING_PATTERN = Pattern.compile(
            "(?i)(jdbc:(?:mysql|postgresql|oracle|sqlserver):|mongodb://|redis://|amqp://|mqtt://)[^\\s,，)）]+");
    private static final Pattern SQL_LINE_PATTERN = Pattern.compile(
            "(?i)^\\s*(select|insert|update|delete|create|alter|drop|truncate|merge|replace)\\s+.+");
    private static final Pattern MAPPER_XML_LINE_PATTERN = Pattern.compile(
            "(?i)^\\s*</?(select|insert|update|delete|mapper|resultMap|sql|where|if|foreach|trim|set)\\b.*");
    private static final Pattern JAVA_DECLARATION_LINE_PATTERN = Pattern.compile(
            "^\\s*(public|private|protected)\\s+(?:static\\s+)?(?:final\\s+)?[\\w<>\\[\\], ?]+\\s+\\w+\\s*\\([^)]*\\)\\s*\\{?.*");
    private static final Pattern JAVA_CLASS_LINE_PATTERN = Pattern.compile(
            "^\\s*(public|private|protected)?\\s*(?:abstract\\s+|final\\s+)?(?:class|interface|enum|record)\\s+\\w+.*[\\{;]\\s*$");
    private static final Pattern JAVA_CONTROL_LINE_PATTERN = Pattern.compile(
            "^\\s*(if|for|while|switch|catch|try|return|throw|else)\\b.*[;{}].*");
    private static final Pattern JAVA_ANNOTATION_LINE_PATTERN = Pattern.compile(
            "^\\s*@(?:PostMapping|GetMapping|PutMapping|DeleteMapping|PatchMapping|RequestMapping|Autowired|Resource|Value|Bean|TableName|TableField|Select|Update|Insert|Delete)\\b.*");
    private static final Pattern CONFIG_LINE_PATTERN = Pattern.compile(
            "(?i)^\\s*[\\w.\\-]+\\s*[:=]\\s*.+");

    private AiCodebaseAnswerSanitizer() {
    }

    public static SanitizeResult sanitize(String answer, boolean strictCodebaseMode) {
        if (isBlank(answer)) {
            return new SanitizeResult(answer, false, List.of());
        }
        List<String> hitTypes = new ArrayList<>();
        String sanitized = redactConnectionStrings(answer, hitTypes);
        if (strictCodebaseMode) {
            sanitized = redactFencedCodeBlocks(sanitized, hitTypes);
        }
        sanitized = sanitizeLines(sanitized, strictCodebaseMode, hitTypes);
        boolean changed = !Objects.equals(answer, sanitized);
        if (changed && !sanitized.contains(SAFETY_NOTICE)) {
            sanitized = sanitized.stripTrailing() + "\n\n" + SAFETY_NOTICE;
        }
        return new SanitizeResult(sanitized, changed, hitTypes);
    }

    private static String redactConnectionStrings(String text, List<String> hitTypes) {
        Matcher matcher = CONNECTION_STRING_PATTERN.matcher(text);
        if (!matcher.find()) {
            return text;
        }
        hitTypes.add("CONNECTION_STRING");
        return matcher.replaceAll("[已拦截连接串]");
    }

    private static String redactFencedCodeBlocks(String text, List<String> hitTypes) {
        Matcher matcher = FENCED_CODE_BLOCK_PATTERN.matcher(text);
        if (!matcher.find()) {
            return text;
        }
        hitTypes.add("FENCED_CODE_BLOCK");
        return matcher.replaceAll(Matcher.quoteReplacement(SOURCE_BLOCK_PLACEHOLDER));
    }

    private static String sanitizeLines(String text, boolean strictCodebaseMode, List<String> hitTypes) {
        String[] lines = text.split("\\R", -1);
        List<String> sanitizedLines = new ArrayList<>(lines.length);
        boolean previousPlaceholder = false;
        for (String line : lines) {
            LineSanitizeResult lineResult = sanitizeLine(line, strictCodebaseMode);
            if (lineResult.changed()) {
                hitTypes.add(lineResult.hitType());
            }
            String sanitizedLine = lineResult.content();
            boolean currentPlaceholder = SOURCE_LINE_PLACEHOLDER.equals(sanitizedLine)
                    || SOURCE_BLOCK_PLACEHOLDER.equals(sanitizedLine);
            if (currentPlaceholder && previousPlaceholder) {
                continue;
            }
            sanitizedLines.add(sanitizedLine);
            previousPlaceholder = currentPlaceholder;
        }
        return String.join("\n", sanitizedLines);
    }

    private static LineSanitizeResult sanitizeLine(String line, boolean strictCodebaseMode) {
        String sensitiveRedacted = redactSensitiveAssignments(line);
        if (!Objects.equals(line, sensitiveRedacted)) {
            return new LineSanitizeResult(sensitiveRedacted, true, "SENSITIVE_ASSIGNMENT");
        }
        String inlineRedacted = redactInlineSensitiveValues(line);
        if (!Objects.equals(line, inlineRedacted)) {
            return new LineSanitizeResult(inlineRedacted, true, "INLINE_SENSITIVE_VALUE");
        }
        if (!strictCodebaseMode || isMarkdownTableLine(inlineRedacted)) {
            return new LineSanitizeResult(inlineRedacted, false, null);
        }
        if (isSourceLikeLine(inlineRedacted)) {
            return new LineSanitizeResult(SOURCE_LINE_PLACEHOLDER, true, "SOURCE_LIKE_LINE");
        }
        return new LineSanitizeResult(inlineRedacted, false, null);
    }

    private static String redactSensitiveAssignments(String line) {
        Matcher matcher = SENSITIVE_ASSIGNMENT_PATTERN.matcher(line);
        if (!matcher.matches()) {
            return line;
        }
        String key = matcher.group(1);
        String prefix = line.substring(0, line.indexOf(key));
        String separator = line.contains("：") ? "：" : (line.contains(":") ? ":" : "=");
        return prefix + key + separator + "[已拦截敏感值]";
    }

    private static String redactInlineSensitiveValues(String line) {
        Matcher matcher = INLINE_SENSITIVE_PATTERN.matcher(line);
        if (!matcher.find()) {
            return line;
        }
        return matcher.replaceAll("$1=[已拦截敏感值]");
    }

    private static boolean isMarkdownTableLine(String line) {
        String trimmed = defaultString(line).trim();
        return trimmed.startsWith("|") && trimmed.endsWith("|");
    }

    private static boolean isSourceLikeLine(String line) {
        String trimmed = defaultString(line).trim();
        if (isBlank(trimmed)) {
            return false;
        }
        return SQL_LINE_PATTERN.matcher(trimmed).matches()
                || MAPPER_XML_LINE_PATTERN.matcher(trimmed).matches()
                || JAVA_DECLARATION_LINE_PATTERN.matcher(trimmed).matches()
                || JAVA_CLASS_LINE_PATTERN.matcher(trimmed).matches()
                || JAVA_CONTROL_LINE_PATTERN.matcher(trimmed).matches()
                || JAVA_ANNOTATION_LINE_PATTERN.matcher(trimmed).matches()
                || isCodeLikeConfigLine(trimmed)
                || isDenseCodeLine(trimmed);
    }

    private static boolean isCodeLikeConfigLine(String trimmed) {
        if (!CONFIG_LINE_PATTERN.matcher(trimmed).matches()) {
            return false;
        }
        return trimmed.contains("${")
                || trimmed.contains("jdbc:")
                || trimmed.contains("classpath:")
                || trimmed.matches("(?i).*(datasource|redis|mqtt|server|spring|mybatis|sa-token|oss|storage)\\..*")
                || trimmed.matches("(?i).*(datasource|redis|mqtt|server|spring|mybatis|sa-token|oss|storage)\\s*[:=].*");
    }

    private static boolean isDenseCodeLine(String trimmed) {
        if (trimmed.startsWith("- ") || trimmed.startsWith("* ") || trimmed.matches("^\\d+\\.\\s+.*")) {
            return false;
        }
        int codeSignals = 0;
        if (trimmed.contains("{") || trimmed.contains("}")) {
            codeSignals++;
        }
        if (trimmed.endsWith(";")) {
            codeSignals++;
        }
        if (trimmed.contains(" = ") || trimmed.contains("==") || trimmed.contains("!=")) {
            codeSignals++;
        }
        if (trimmed.contains(");") || trimmed.contains("()")) {
            codeSignals++;
        }
        if (trimmed.contains("->") || trimmed.contains("::")) {
            codeSignals++;
        }
        return codeSignals >= 2;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static String defaultString(String value) {
        return value == null ? "" : value;
    }

    public static final class SanitizeResult {
        private final String content;
        private final boolean changed;
        private final List<String> hitTypes;

        private SanitizeResult(String content, boolean changed, List<String> hitTypes) {
            this.content = content;
            this.changed = changed;
            this.hitTypes = hitTypes == null ? List.of() : List.copyOf(hitTypes);
        }

        public String getContent() {
            return content;
        }

        public boolean isChanged() {
            return changed;
        }

        public List<String> getHitTypes() {
            return hitTypes;
        }
    }

    private record LineSanitizeResult(String content, boolean changed, String hitType) {
    }
}
