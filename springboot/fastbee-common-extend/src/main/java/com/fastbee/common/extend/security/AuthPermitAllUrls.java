package com.fastbee.common.extend.security;

/**
 * Shared anonymous URL definitions for Spring Security and Sa-Token.
 */
public final class AuthPermitAllUrls {

    private AuthPermitAllUrls() {
    }

    public static final String[] API_PATHS = {
            "/login", "/register", "/captchaImage",
            "/iot/tool/register", "/iot/tool/ntp",
            "/iot/tool/mqtt/auth", "/iot/tool/mqtt/authv5",
            "/iot/tool/mqtt/webhook", "/iot/tool/mqtt/webhookv5",
            "/auth/sms/login", "/auth/**", "/oauth2/**", "/logintest/**", "/assets/**",
            "/wechat/mobileLogin", "/wechat/miniLogin", "/wechat/wxBind/callback",
            "/notify/smsForgetPassword", "/system/user/forgetPwdReset", "/system/config/configKey/**",
            "/zlmhook/**", "/goview/sys/login", "/goview/project/getData",
            "/ruleengine/rulemanager/**", "/ruleview/**",
            "/notify/smsLoginCaptcha", "/notify/weComVerifyUrl",
            "/wechat/publicAccount/callback", "/notify/smsRegisterCaptcha",
            "/app/language/list", "/iot/news/bannerList", "/iot/news/topList", "/iot/news/getDetail",
            "/dueros", "/license/**", "/httptest/**"
    };

    public static final String[] STATIC_RESOURCE_PATHS = {
            "/", "/*.html", "/**/*.html", "/**/*.css", "/**/*.js", "/profile/**"
    };

    public static final String[] DEV_TOOL_PATHS = {
            "/swagger-ui.html", "/swagger-ui/**", "/webjars/**", "/druid/**"
    };
}
