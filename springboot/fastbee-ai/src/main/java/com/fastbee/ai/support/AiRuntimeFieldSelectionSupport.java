package com.fastbee.ai.support;

import static com.fastbee.ai.support.AiI18nMessageUtils.message;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fastbee.ai.model.vo.AiSemanticFieldVO;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.utils.StringUtils;

/**
 * 运行时物模型候选选择支撑。
 * <p>
 * 当问句中已经通过澄清续跑明确追加了 {@code identifier=xxx} 时，
 * 执行链路必须优先命中该物模型，而不是再次退回候选列表首项。
 */
public final class AiRuntimeFieldSelectionSupport {

    private static final Pattern IDENTIFIER_PATTERN =
            Pattern.compile("(?im)(?:^|[\\s\\r\\n])(?:identifier|标识符)\\s*[=:：]\\s*([^\\s,，;；]+)");

    private AiRuntimeFieldSelectionSupport() {
    }

    /**
     * 从问句中提取用户明确确认的物模型标识符。
     */
    public static String extractExplicitIdentifier(String question) {
        if (StringUtils.isBlank(question)) {
            return null;
        }
        Matcher matcher = IDENTIFIER_PATTERN.matcher(question);
        if (!matcher.find()) {
            return null;
        }
        String identifier = matcher.group(1);
        return StringUtils.isBlank(identifier) ? null : identifier.trim();
    }

    /**
     * 解析优先运行时物模型。
     * <p>
     * 若问句中带有明确的 {@code identifier=}，则优先按该标识符命中候选；
     * 否则回退到当前排序后的首个候选。
     */
    public static AiSemanticFieldVO findPreferredRuntimeField(String question, List<AiSemanticFieldVO> runtimeFields) {
        if (runtimeFields == null || runtimeFields.isEmpty()) {
            return null;
        }
        String explicitIdentifier = extractExplicitIdentifier(question);
        if (StringUtils.isBlank(explicitIdentifier)) {
            return runtimeFields.get(0);
        }
        for (AiSemanticFieldVO runtimeField : runtimeFields) {
            if (runtimeField == null || StringUtils.isBlank(runtimeField.getSourceCode())) {
                continue;
            }
            if (sameIdentifier(runtimeField.getSourceCode(), explicitIdentifier)) {
                return runtimeField;
            }
        }
        throw new ServiceException(buildSelectedIdentifierNotFoundMessage(explicitIdentifier));
    }

    /**
     * 解析并校验运行时物模型。
     */
    public static AiSemanticFieldVO resolveRuntimeField(String question, List<AiSemanticFieldVO> runtimeFields,
                                                        String emptySemanticMessage, String executionLabel) {
        if (runtimeFields == null || runtimeFields.isEmpty()) {
            String explicitIdentifier = extractExplicitIdentifier(question);
            if (StringUtils.isNotBlank(explicitIdentifier)) {
                throw new ServiceException(buildSelectedIdentifierNotFoundMessage(explicitIdentifier));
            }
            throw new ServiceException(emptySemanticMessage);
        }
        AiSemanticFieldVO runtimeField = findPreferredRuntimeField(question, runtimeFields);
        if (runtimeField == null || StringUtils.isBlank(runtimeField.getSourceCode())) {
            throw new ServiceException(message("ai.runtime.field.identifier.required", executionLabel));
        }
        return runtimeField;
    }

    private static boolean sameIdentifier(String left, String right) {
        if (StringUtils.isBlank(left) || StringUtils.isBlank(right)) {
            return false;
        }
        return left.trim().toLowerCase(Locale.ROOT).equals(right.trim().toLowerCase(Locale.ROOT));
    }

    private static String buildSelectedIdentifierNotFoundMessage(String identifier) {
        return message("ai.query.router.runtime.identifier.not.matched", identifier);
    }
}
