package com.fastbee.oauth.satoken;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.oauth2.data.model.loader.SaClientModel;
import cn.dev33.satoken.oauth2.processor.SaOAuth2ServerProcessor;
import cn.dev33.satoken.sso.exception.SaSsoException;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.alibaba.fastjson2.JSON;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.fastbee.oauth.controller.ConfirmAccessController;
import com.fastbee.oauth.controller.FastbeeSsoController;
import com.fastbee.oauth.controller.SaOAuth2ServerController;
import com.fastbee.oauth.domain.OauthClientDetails;
import com.fastbee.oauth.service.IOauthClientDetailsService;
import com.fastbee.system.service.sys.AuthTokenFacade;

@DisplayName("Sa-Token 客户端绑定集成测试")
@Tag("dev")
public class SaTokenConfigureClientIdTest {

    @Test
    @DisplayName("请求 clientid 与 token extra 一致时放行")
    void checkClientIdShouldPassWhenHeaderMatchesTokenExtra() {
        SaTokenConfigure configure = new SaTokenConfigure();
        SaRequest request = mock(SaRequest.class);
        when(request.getHeader(AuthTokenFacade.CLIENT_ID_EXTRA)).thenReturn("pc-client");

        try (MockedStatic<StpUtil> stpUtil = mockStatic(StpUtil.class);
             MockedStatic<SaHolder> saHolder = mockStatic(SaHolder.class)) {
            stpUtil.when(() -> StpUtil.getExtra(AuthTokenFacade.CLIENT_ID_EXTRA)).thenReturn("pc-client");
            saHolder.when(SaHolder::getRequest).thenReturn(request);

            assertDoesNotThrow(() -> ReflectionTestUtils.invokeMethod(configure, "checkClientId"));
        }
    }

    @Test
    @DisplayName("请求 clientid 与 token extra 不一致时拒绝")
    void checkClientIdShouldRejectWhenHeaderDoesNotMatchTokenExtra() {
        SaTokenConfigure configure = new SaTokenConfigure();
        SaRequest request = mock(SaRequest.class);
        when(request.getHeader(AuthTokenFacade.CLIENT_ID_EXTRA)).thenReturn("app-client");
        when(request.getParam(AuthTokenFacade.CLIENT_ID_EXTRA)).thenReturn(null);

        try (MockedStatic<StpUtil> stpUtil = mockStatic(StpUtil.class);
             MockedStatic<SaHolder> saHolder = mockStatic(SaHolder.class)) {
            stpUtil.when(() -> StpUtil.getExtra(AuthTokenFacade.CLIENT_ID_EXTRA)).thenReturn("pc-client");
            stpUtil.when(StpUtil::getLoginType).thenReturn("login");
            saHolder.when(SaHolder::getRequest).thenReturn(request);

            assertThrows(NotLoginException.class, () -> ReflectionTestUtils.invokeMethod(configure, "checkClientId"));
        }
    }

    @Test
    @DisplayName("历史 token 没有 clientid extra 时保持兼容")
    void checkClientIdShouldPassWhenTokenHasNoClientIdExtra() {
        SaTokenConfigure configure = new SaTokenConfigure();

        try (MockedStatic<StpUtil> stpUtil = mockStatic(StpUtil.class)) {
            stpUtil.when(() -> StpUtil.getExtra(AuthTokenFacade.CLIENT_ID_EXTRA)).thenReturn(null);

            assertDoesNotThrow(() -> ReflectionTestUtils.invokeMethod(configure, "checkClientId"));
        }
    }

    @Test
    @DisplayName("Legacy Authorization header token should be bridged into Sa-Token storage")
    void legacyAuthorizationHeaderShouldBeSetToSaTokenStorage() {
        SaTokenConfigure configure = new SaTokenConfigure();
        AuthTokenFacade authTokenFacade = mock(AuthTokenFacade.class);
        ReflectionTestUtils.setField(configure, "authTokenFacade", authTokenFacade);
        SaRequest request = mock(SaRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer legacy-jwt-token");

        try (MockedStatic<StpUtil> stpUtil = mockStatic(StpUtil.class);
             MockedStatic<SaHolder> saHolder = mockStatic(SaHolder.class)) {
            stpUtil.when(StpUtil::getTokenValue).thenReturn(null);
            stpUtil.when(StpUtil::isLogin).thenReturn(false);
            saHolder.when(SaHolder::getRequest).thenReturn(request);

            ReflectionTestUtils.invokeMethod(configure, "useLegacyAuthorizationTokenIfNecessary");

            stpUtil.verify(() -> StpUtil.setTokenValueToStorage("legacy-jwt-token"));
            verify(authTokenFacade).bindLoginUserFromLegacyToken("legacy-jwt-token");
        }
    }

    @Test
    @DisplayName("OAuth2 client loader should expose four standard grant types")
    void dataLoaderShouldExposeFourStandardGrantTypes() {
        IOauthClientDetailsService clientDetailsService = mock(IOauthClientDetailsService.class);
        OauthClientDetails clientDetails = new OauthClientDetails();
        clientDetails.setClientSecret("secret");
        clientDetails.setScope("openid,userinfo");
        clientDetails.setAuthorizedGrantTypes("authorization_code,password,client_credentials,refresh_token");
        clientDetails.setWebServerRedirectUri("http://localhost/callback");
        clientDetails.setAccessTokenValidity(3600L);
        clientDetails.setRefreshTokenValidity(604800L);
        clientDetails.setAutoapprove("true");
        when(clientDetailsService.validOAuthClientFromCache("oauth-client")).thenReturn(clientDetails);

        SaOAuth2DataLoaderImpl dataLoader = new SaOAuth2DataLoaderImpl();
        ReflectionTestUtils.setField(dataLoader, "oauthClientDetailsService", clientDetailsService);

        SaClientModel model = dataLoader.getClientModel("oauth-client");

        assertEquals("oauth-client", model.getClientId());
        assertEquals("secret", model.getClientSecret());
        assertEquals(List.of("authorization_code", "password", "client_credentials", "refresh_token"),
                model.getAllowGrantTypes());
        assertEquals(List.of("openid", "userinfo"), model.getContractScopes());
        assertEquals(List.of("http://localhost/callback"), model.getAllowRedirectUris());
        assertEquals(3600L, model.getAccessTokenTimeout());
        assertEquals(604800L, model.getRefreshTokenTimeout());
        assertTrue(model.getIsAutoConfirm());
    }

    @Test
    @DisplayName("OIDC 发现文档按当前域名暴露标准端点")
    @SuppressWarnings("unchecked")
    void openidConfigurationShouldExposeStandardEndpoints() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("https");
        request.setServerName("sso.fastbee.local");
        request.setServerPort(443);

        SaOAuth2ServerController controller = new SaOAuth2ServerController();
        Map<String, Object> metadata = (Map<String, Object>) controller.openidConfiguration(request);

        assertEquals("https://sso.fastbee.local", metadata.get("issuer"));
        assertEquals("https://sso.fastbee.local/oauth2/authorize", metadata.get("authorization_endpoint"));
        assertEquals("https://sso.fastbee.local/oauth2/token", metadata.get("token_endpoint"));
        assertEquals("https://sso.fastbee.local/oauth2/userinfo", metadata.get("userinfo_endpoint"));
        assertEquals("https://sso.fastbee.local/oauth2/jwks", metadata.get("jwks_uri"));
        assertEquals("https://sso.fastbee.local/oauth2/logout", metadata.get("end_session_endpoint"));
        assertTrue(((List<String>) metadata.get("grant_types_supported")).contains("authorization_code"));
        assertTrue(((List<String>) metadata.get("scopes_supported")).contains("openid"));
        assertTrue(((List<String>) metadata.get("id_token_signing_alg_values_supported")).contains("HS256"));
    }

    @Test
    @DisplayName("OIDC 发现文档优先使用反向代理转发头")
    @SuppressWarnings("unchecked")
    void openidConfigurationShouldPreferForwardedHeaders() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");
        request.setServerName("127.0.0.1");
        request.setServerPort(8080);
        request.addHeader("X-Forwarded-Proto", "https");
        request.addHeader("X-Forwarded-Host", "login.example.com");

        SaOAuth2ServerController controller = new SaOAuth2ServerController();
        Map<String, Object> metadata = (Map<String, Object>) controller.openidConfiguration(request);

        assertEquals("https://login.example.com", metadata.get("issuer"));
    }

    @Test
    @DisplayName("JWKS 端点在对称密钥模式下不泄露签名密钥")
    @SuppressWarnings("unchecked")
    void jwksShouldNotExposeSymmetricSecret() {
        SaOAuth2ServerController controller = new SaOAuth2ServerController();
        Map<String, Object> jwks = (Map<String, Object>) controller.jwks();

        assertTrue(((List<Object>) jwks.get("keys")).isEmpty());
        assertTrue(String.valueOf(jwks.get("note")).contains("HS256"));
    }

    @Test
    @DisplayName("OAuth2 服务端应兼容 Spring Security OAuth 历史端点")
    void oauth2ServerShouldExposeLegacyOAuthEndpoints() throws NoSuchMethodException {
        assertMappingContains("authorize", GetMapping.class, "/oauth/authorize");
        assertMappingContains("authorizeByPost", PostMapping.class, "/oauth/authorize");
        assertMappingContains("token", PostMapping.class, "/oauth/token");
        assertMappingContains("doLogin", PostMapping.class, "/oauth2/doLogin");
        assertMappingContains("doLogin", PostMapping.class, "/oauth/doLogin");
        assertMappingContains("doConfirm", PostMapping.class, "/oauth2/doConfirm");
        assertMappingContains("doConfirm", PostMapping.class, "/oauth/doConfirm");
        assertMappingContains("checkToken", PostMapping.class, "/oauth/check_token", HttpServletRequest.class, String.class, String.class);
        assertMappingContains("revoke", PostMapping.class, "/oauth/revoke", HttpServletRequest.class, String.class, String.class);
        assertMappingContains("revokeByDelete", DeleteMapping.class, "/oauth/revoke", HttpServletRequest.class, String.class, String.class);
        assertMappingContains("legacyLoginView", GetMapping.class, "/oauth/login");
        assertMappingContains("logout", GetMapping.class, "/oauth/logout", HttpServletRequest.class, HttpServletResponse.class, String.class, String.class, String.class);
        assertMappingContains("logoutByPost", PostMapping.class, "/oauth/logout", HttpServletRequest.class, String.class);
    }

    @Test
    @DisplayName("OAuth 授权入口应直接进入授权处理器避免旧路径分发失败")
    void oauthAuthorizeShouldCallAuthorizeProcessorDirectly() {
        SaOAuth2ServerProcessor originalProcessor = SaOAuth2ServerProcessor.instance;
        SaOAuth2ServerProcessor processor = mock(SaOAuth2ServerProcessor.class);
        Object expected = new Object();
        when(processor.authorize()).thenReturn(expected);
        SaOAuth2ServerProcessor.instance = processor;
        try {
            SaOAuth2ServerController controller = new SaOAuth2ServerController();

            Object result = controller.authorize();

            assertSame(expected, result);
            verify(processor).authorize();
        } finally {
            SaOAuth2ServerProcessor.instance = originalProcessor;
        }
    }

    @Test
    @DisplayName("OAuth 登录提交应直接进入登录处理器")
    void oauthLoginShouldCallLoginProcessorDirectly() {
        SaOAuth2ServerProcessor originalProcessor = SaOAuth2ServerProcessor.instance;
        SaOAuth2ServerProcessor processor = mock(SaOAuth2ServerProcessor.class);
        Object expected = new Object();
        when(processor.doLogin()).thenReturn(expected);
        SaOAuth2ServerProcessor.instance = processor;
        try {
            SaOAuth2ServerController controller = new SaOAuth2ServerController();

            Object result = controller.doLogin();

            assertSame(expected, result);
            verify(processor).doLogin();
        } finally {
            SaOAuth2ServerProcessor.instance = originalProcessor;
        }
    }

    @Test
    @DisplayName("OAuth 授权确认提交应直接进入确认处理器")
    void oauthConfirmShouldCallConfirmProcessorDirectly() {
        SaOAuth2ServerProcessor originalProcessor = SaOAuth2ServerProcessor.instance;
        SaOAuth2ServerProcessor processor = mock(SaOAuth2ServerProcessor.class);
        Object expected = new Object();
        when(processor.doConfirm()).thenReturn(expected);
        SaOAuth2ServerProcessor.instance = processor;
        try {
            SaOAuth2ServerController controller = new SaOAuth2ServerController();

            Object result = controller.doConfirm();

            assertSame(expected, result);
            verify(processor).doConfirm();
        } finally {
            SaOAuth2ServerProcessor.instance = originalProcessor;
        }
    }

    @Test
    @DisplayName("OAuth 授权确认旧入口应复用当前确认页")
    void legacyConfirmAccessShouldUseCurrentConfirmTemplate() {
        ConfirmAccessController controller = new ConfirmAccessController();

        String viewName = controller.getAccessConfirmation(Map.of(), null);

        assertEquals("confirm.html", viewName);
    }

    @Test
    @DisplayName("OAuth 回调 state 应兼容旧业务重复测试值")
    void oauthRedirectStateShouldAllowLegacyRepeatedValue() {
        SaOAuth2ServerController.LegacyStateCompatibleDataGenerate dataGenerate =
                new SaOAuth2ServerController.LegacyStateCompatibleDataGenerate();

        String first = dataGenerate.buildRedirectUri("http://127.0.0.1:18080/dueros/callback", "code-1", "dueros-state");
        String second = dataGenerate.buildRedirectUri("http://127.0.0.1:18080/dueros/callback", "code-2", "dueros-state");

        assertEquals("http://127.0.0.1:18080/dueros/callback?code=code-1&state=dueros-state", first);
        assertEquals("http://127.0.0.1:18080/dueros/callback?code=code-2&state=dueros-state", second);
    }

    @Test
    @DisplayName("SLO 客户端会话 key 对同一回调保持稳定")
    void sloSessionKeyShouldBeStable() {
        SaOAuth2ServerController.ClientLogoutSession session = new SaOAuth2ServerController.ClientLogoutSession();
        session.clientId = "client-a";
        session.clientSessionId = "session-1";
        session.logoutCallbackUrl = "https://client.example.com/slo";

        String firstKey = session.sessionKey();
        String secondKey = session.sessionKey();

        assertEquals(firstKey, secondKey);
        assertTrue(firstKey.startsWith("client-a:"));
    }

    @Test
    @DisplayName("SSO ticket 回跳地址应追加 ticket 与 state")
    void ssoTicketRedirectShouldAppendTicketAndState() {
        FastbeeSsoController controller = new FastbeeSsoController();

        String redirect = ReflectionTestUtils.invokeMethod(
                controller,
                "buildTicketRedirect",
                "https://client.example.com/callback?from=fastbee",
                "ST-abc",
                "state 1");

        assertEquals("https://client.example.com/callback?from=fastbee&ticket=ST-abc&state=state+1", redirect);
    }

    @Test
    @DisplayName("SSO ticket 应保存到 Sa-Token DAO 并使用短期有效期")
    void ssoTicketShouldBeSavedWithShortTimeout() {
        FastbeeSsoController controller = new FastbeeSsoController();
        SaTokenDao dao = mock(SaTokenDao.class);

        try (MockedStatic<SaManager> saManager = mockStatic(SaManager.class)) {
            saManager.when(SaManager::getSaTokenDao).thenReturn(dao);

            String ticket = ReflectionTestUtils.invokeMethod(
                    controller,
                    "createTicket",
                    "client-a",
                    "https://client.example.com/callback",
                    "state-1",
                    "1001",
                    "token-1");

            assertTrue(ticket.startsWith("ST-"));
            verify(dao).set(eq(FastbeeSsoController.TICKET_KEY_PREFIX + ticket), anyString(), eq(FastbeeSsoController.TICKET_TIMEOUT));
        }
    }

    @Test
    @DisplayName("SSO ticket 校验后应立即删除且校验 client")
    void ssoTicketShouldBeConsumedOnceAndValidateClient() {
        FastbeeSsoController controller = new FastbeeSsoController();
        SaTokenDao dao = mock(SaTokenDao.class);
        FastbeeSsoController.FastbeeSsoTicket ticket = new FastbeeSsoController.FastbeeSsoTicket();
        ticket.ticket = "ST-1";
        ticket.client = "client-a";
        ticket.redirect = "https://client.example.com/callback";
        ticket.loginId = "1001";
        ticket.tokenValue = "token-1";

        when(dao.get(FastbeeSsoController.TICKET_KEY_PREFIX + "ST-1")).thenReturn(JSON.toJSONString(ticket));
        when(dao.get(FastbeeSsoController.TICKET_KEY_PREFIX + "ST-2")).thenReturn(JSON.toJSONString(ticket));

        try (MockedStatic<SaManager> saManager = mockStatic(SaManager.class)) {
            saManager.when(SaManager::getSaTokenDao).thenReturn(dao);

            FastbeeSsoController.FastbeeSsoTicket result = ReflectionTestUtils.invokeMethod(
                    controller,
                    "consumeTicket",
                    "ST-1",
                    "client-a");

            assertEquals("1001", result.loginId);
            verify(dao).delete(FastbeeSsoController.TICKET_KEY_PREFIX + "ST-1");
            assertThrows(SaSsoException.class, () -> ReflectionTestUtils.invokeMethod(
                    controller,
                    "consumeTicket",
                    "ST-2",
                    "client-b"));
            verify(dao).delete(FastbeeSsoController.TICKET_KEY_PREFIX + "ST-2");
        }
    }

    @Test
    @DisplayName("SSO checkTicket 应兼容官方 Client 顶层字段读取")
    void ssoCheckTicketShouldExposeOfficialTopLevelFields() {
        FastbeeSsoController controller = new FastbeeSsoController();
        SaTokenDao dao = mock(SaTokenDao.class);
        FastbeeSsoController.FastbeeSsoTicket ticket = new FastbeeSsoController.FastbeeSsoTicket();
        ticket.ticket = "ST-3";
        ticket.client = "client-a";
        ticket.redirect = "https://client.example.com/callback";
        ticket.loginId = "1001";
        ticket.tokenValue = "token-1";

        when(dao.get(FastbeeSsoController.TICKET_KEY_PREFIX + "ST-3")).thenReturn(JSON.toJSONString(ticket));

        try (MockedStatic<SaManager> saManager = mockStatic(SaManager.class)) {
            saManager.when(SaManager::getSaTokenDao).thenReturn(dao);

            SaResult result = (SaResult) controller.checkTicket("ST-3", "client-a", null, null, null);

            assertEquals(200, result.getCode());
            assertEquals("1001", result.get("loginId"));
            assertEquals("token-1", result.get("tokenValue"));
            verify(dao).delete(FastbeeSsoController.TICKET_KEY_PREFIX + "ST-3");
        }
    }

    @Test
    @DisplayName("SSO SLO 客户端会话 key 对同一回调保持稳定")
    void ssoClientLogoutSessionKeyShouldBeStable() {
        FastbeeSsoController.ClientLogoutSession session = new FastbeeSsoController.ClientLogoutSession();
        session.client = "client-a";
        session.clientSessionId = "session-1";
        session.ssoLogoutCall = "https://client.example.com/sso/logoutCall";

        String firstKey = session.sessionKey();
        String secondKey = session.sessionKey();

        assertEquals(firstKey, secondKey);
        assertTrue(firstKey.startsWith("client-a:"));
    }

    private <T extends java.lang.annotation.Annotation> void assertMappingContains(String methodName,
                                                                                  Class<T> annotationClass,
                                                                                  String expectedPath,
                                                                                  Class<?>... parameterTypes) throws NoSuchMethodException {
        Method method = SaOAuth2ServerController.class.getMethod(methodName, parameterTypes);
        T annotation = method.getAnnotation(annotationClass);
        Set<String> paths;
        if (annotation instanceof GetMapping getMapping) {
            paths = Arrays.stream(getMapping.value()).collect(Collectors.toSet());
        } else if (annotation instanceof PostMapping postMapping) {
            paths = Arrays.stream(postMapping.value()).collect(Collectors.toSet());
        } else if (annotation instanceof DeleteMapping deleteMapping) {
            paths = Arrays.stream(deleteMapping.value()).collect(Collectors.toSet());
        } else {
            throw new AssertionError("Unsupported mapping annotation: " + annotationClass.getName());
        }
        assertTrue(paths.contains(expectedPath), methodName + " should map " + expectedPath);
    }
}
