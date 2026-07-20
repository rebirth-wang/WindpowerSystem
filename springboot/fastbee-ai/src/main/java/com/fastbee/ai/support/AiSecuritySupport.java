package com.fastbee.ai.support;

import static com.fastbee.ai.support.AiI18nMessageUtils.message;

import java.util.Date;
import java.util.UUID;

import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.extend.core.domin.model.LoginUser;
import com.fastbee.common.extend.utils.SecurityUtils;

/**
 * AI 模块安全与租户上下文辅助工具。
 */
public final class AiSecuritySupport {

    private static final String ADMIN_USERNAME = "admin";

    private AiSecuritySupport() {
    }

    /**
     * 获取当前登录用户。
     *
     * @return 登录用户
     */
    public static LoginUser getLoginUser() {
        return SecurityUtils.getLoginUser();
    }

    /**
     * 获取当前业务用户。
     *
     * @return 业务用户
     */
    public static SysUser getCurrentUser() {
        LoginUser loginUser = getLoginUser();
        return loginUser == null ? null : loginUser.getUser();
    }

    /**
     * 解析当前租户 ID。
     *
     * <p>项目里租户数据范围默认按 {@code tenant_id} 过滤，机构用户优先取
     * {@code deptUserId}，系统管理员等场景回退到用户自身 ID。</p>
     *
     * @param user 当前用户
     * @return 租户 ID
     */
    public static Long resolveTenantId(SysUser user) {
        if (user == null) {
            return null;
        }
        if (user.getDept() != null && user.getDept().getDeptUserId() != null) {
            return user.getDept().getDeptUserId();
        }
        return user.getUserId();
    }

    /**
     * 解析当前租户名称。
     *
     * @param user 当前用户
     * @return 租户名称
     */
    public static String resolveTenantName(SysUser user) {
        if (user == null) {
            return "";
        }
        if (user.getDept() != null && user.getDept().getDeptName() != null) {
            return user.getDept().getDeptName();
        }
        return user.getUserName();
    }

    /**
     * 获取当前操作用户名。
     *
     * @return 用户名
     */
    public static String resolveUsername() {
        try {
            LoginUser loginUser = getLoginUser();
            if (loginUser == null || loginUser.getUsername() == null || loginUser.getUsername().isBlank()) {
                return "system";
            }
            return loginUser.getUsername();
        } catch (Exception ex) {
            return "system";
        }
    }

    /**
     * 判断当前登录账号是否为平台内置 admin。
     *
     * @return true 表示当前账号为 admin
     */
    public static boolean isAdminAccount() {
        LoginUser loginUser = getLoginUser();
        if (loginUser == null) {
            return false;
        }
        if (ADMIN_USERNAME.equals(loginUser.getUsername())) {
            return true;
        }
        SysUser user = loginUser.getUser();
        return user != null && ADMIN_USERNAME.equals(user.getUserName());
    }

    /**
     * 要求当前登录账号必须为 admin。
     *
     * @param actionName 操作名称
     */
    public static void requireAdminAccount(String actionName) {
        if (!isAdminAccount()) {
            throw new ServiceException(message("ai.security.admin.account.required", actionName));
        }
    }

    /**
     * 生成 AI 会话编码。
     *
     * @return 会话编码
     */
    public static String newSessionCode() {
        return "CHAT_" + UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 简单脱敏密钥，避免列表页直接返回完整凭据。
     *
     * @param secret 原始密钥
     * @return 脱敏后的展示值
     */
    public static String maskSecret(String secret) {
        if (secret == null || secret.isBlank()) {
            return "";
        }
        if (secret.length() <= 8) {
            return "****";
        }
        return secret.substring(0, 4) + "****" + secret.substring(secret.length() - 4);
    }

    /**
     * 获取当前时间。
     *
     * @return 当前时间
     */
    public static Date now() {
        return new Date();
    }
}
