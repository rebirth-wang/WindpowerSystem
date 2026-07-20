package com.fastbee.oauth.satoken;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fastbee.common.extend.security.AuthPermitAllUrls;
import com.fastbee.framework.config.properties.PermitAllUrlProperties;
import com.fastbee.system.service.sys.AuthTokenFacade;

/**
 * Sa-Token request authentication and annotation authorization config.
 */
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {

    @Autowired
    private PermitAllUrlProperties permitAllUrl;

    @Autowired
    private AuthTokenFacade authTokenFacade;

    @Bean
    public StpLogic getStpLogicJwt() {
        return new StpLogicJwtForSimple();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor()).addPathPatterns("/**");
    }

    @Bean
    public SaServletFilter getSaServletFilter() {
        return new SaServletFilter()
                .addInclude("/**")
                .addExclude("/favicon.ico")
                .addExclude(AuthPermitAllUrls.API_PATHS)
                .addExclude(AuthPermitAllUrls.STATIC_RESOURCE_PATHS)
                .addExclude(AuthPermitAllUrls.DEV_TOOL_PATHS)
                .addExclude("/getInfo", "/getRouters", "/logout", "/refreshToken")
                .addExclude("/oauth2/**", "/oauth/**", "/sso/**", "/.well-known/**")
                .addExclude(permitAllUrl.getUrls().toArray(new String[0]))
                .setAuth(obj -> {
                    useLegacyAuthorizationTokenIfNecessary();
                    SaManager.getLog().debug("Sa-Token path={} token={}", SaHolder.getRequest().getRequestPath(), StpUtil.getTokenValue());
                    StpUtil.checkLogin();
                    checkClientId();
                })
                .setError(e -> {
                    SaHolder.getResponse().setStatus(401);
                    return SaResult.error(e.getMessage());
                })
                .setBeforeAuth(obj -> {
                    SaHolder.getResponse()
                            .setHeader("Access-Control-Allow-Origin", "*")
                            .setHeader("Access-Control-Allow-Methods", "*")
                            .setHeader("Access-Control-Allow-Headers", "*")
                            .setHeader("Access-Control-Max-Age", "3600");

                    SaRouter.match(SaHttpMethod.OPTIONS)
                            .free(r -> SaHolder.getResponse().setStatus(204))
                            .back();
                });
    }

    private void useLegacyAuthorizationTokenIfNecessary() {
        String token = StpUtil.getTokenValue();
        if (isBlank(token)) {
            token = resolveLegacyAuthorizationToken();
            if (!isBlank(token)) {
                StpUtil.setTokenValueToStorage(token);
            }
        }
        if (!isBlank(token)) {
            restoreLegacyLoginStateIfNecessary(token);
        }
    }

    private String resolveLegacyAuthorizationToken() {
        String token = SaHolder.getRequest().getHeader("Authorization");
        if (isBlank(token)) {
            return null;
        }
        token = token.trim();
        if (token.regionMatches(true, 0, "Bearer ", 0, "Bearer ".length())) {
            token = token.substring("Bearer ".length()).trim();
        }
        return token;
    }

    private void restoreLegacyLoginStateIfNecessary(String token) {
        try {
            if (StpUtil.isLogin()) {
                return;
            }
        } catch (Exception ignored) {
        }
        authTokenFacade.bindLoginUserFromLegacyToken(token);
    }

    private void checkClientId() {
        Object tokenClientId;
        try {
            tokenClientId = StpUtil.getExtra(AuthTokenFacade.CLIENT_ID_EXTRA);
        } catch (Exception e) {
            return;
        }
        String clientId = tokenClientId == null ? null : String.valueOf(tokenClientId);
        if (isBlank(clientId)) {
            return;
        }
        String requestClientId = firstNotBlank(
                SaHolder.getRequest().getHeader(AuthTokenFacade.CLIENT_ID_EXTRA),
                SaHolder.getRequest().getHeader("clientId"),
                SaHolder.getRequest().getParam(AuthTokenFacade.CLIENT_ID_EXTRA),
                SaHolder.getRequest().getParam("clientId"));
        if (!clientId.equals(requestClientId)) {
            throw new NotLoginException("客户端ID与Token不匹配", StpUtil.getLoginType(), NotLoginException.INVALID_TOKEN);
        }
    }

    private String firstNotBlank(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (!isBlank(value)) {
                return value;
            }
        }
        return null;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
