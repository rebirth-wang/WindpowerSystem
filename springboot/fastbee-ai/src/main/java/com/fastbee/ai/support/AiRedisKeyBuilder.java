package com.fastbee.ai.support;

import com.fastbee.common.utils.StringUtils;

/**
 * AI Redis Key 构建器。
 */
public final class AiRedisKeyBuilder {

    private static final String DEFAULT_PREFIX = "fastbee:ai:semantic";

    private AiRedisKeyBuilder() {
    }

    /**
     * 构建当前租户 NL2SQL 生效知识库集合 Key。
     *
     * @param prefix   前缀
     * @param tenantId 租户 ID
     * @return Redis Key
     */
    public static String buildNl2SqlActiveKbSetKey(String prefix, Long tenantId) {
        return buildBasePrefix(prefix, tenantId) + ":nl2sql:active:kb-set";
    }

    /**
     * 构建当前租户某知识库生效语义包 Key。
     *
     * @param prefix   前缀
     * @param tenantId 租户 ID
     * @param kbCode   知识库编码
     * @return Redis Key
     */
    public static String buildNl2SqlActiveBundleKey(String prefix, Long tenantId, String kbCode) {
        return buildBasePrefix(prefix, tenantId) + ":nl2sql:kb:" + normalizeSegment(kbCode) + ":active";
    }

    /**
     * 构建当前租户某知识库版本索引集合 Key。
     *
     * @param prefix   前缀
     * @param tenantId 租户 ID
     * @param kbCode   知识库编码
     * @return Redis Key
     */
    public static String buildNl2SqlVersionSetKey(String prefix, Long tenantId, String kbCode) {
        return buildBasePrefix(prefix, tenantId) + ":nl2sql:kb:" + normalizeSegment(kbCode) + ":version-set";
    }

    /**
     * 构建当前租户某知识库某版本语义包 Key。
     *
     * @param prefix    前缀
     * @param tenantId  租户 ID
     * @param kbCode    知识库编码
     * @param versionNo 版本号
     * @return Redis Key
     */
    public static String buildNl2SqlVersionBundleKey(String prefix, Long tenantId, String kbCode, String versionNo) {
        return buildBasePrefix(prefix, tenantId)
                + ":nl2sql:kb:" + normalizeSegment(kbCode)
                + ":version:" + normalizeSegment(versionNo);
    }

    private static String buildBasePrefix(String prefix, Long tenantId) {
        return normalizePrefix(prefix) + ":tenant:" + normalizeTenantId(tenantId);
    }

    private static String normalizePrefix(String prefix) {
        return StringUtils.isBlank(prefix) ? DEFAULT_PREFIX : prefix.trim();
    }

    private static long normalizeTenantId(Long tenantId) {
        return tenantId == null ? 0L : tenantId;
    }

    private static String normalizeSegment(String segment) {
        return StringUtils.isBlank(segment) ? "default" : segment.trim().toLowerCase();
    }
}
