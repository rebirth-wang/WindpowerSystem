package com.fastbee.oauth.controller;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.jwt.SaJwtUtil;
import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.data.model.loader.SaClientModel;
import cn.dev33.satoken.sso.exception.SaSsoException;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.system.service.ISysUserService;
import com.fastbee.system.service.sys.SysLoginService;

/**
 * Sa-Token SSO compatible ticket endpoints.
 */
@RestController
@RequestMapping("/sso")
public class FastbeeSsoController {

    public static final String TICKET_KEY_PREFIX = "sso:ticket:";
    public static final String SLO_SESSION_KEY_PREFIX = "sso:slo:sessions:";
    public static final long TICKET_TIMEOUT = 5 * 60L;
    public static final long SLO_SESSION_TIMEOUT = 30 * 24 * 60 * 60L;
    private static final Duration SLO_PUSH_TIMEOUT = Duration.ofSeconds(3);
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(SLO_PUSH_TIMEOUT)
            .build();

    @Resource
    private ISysUserService sysUserService;

    @Resource
    private SysLoginService sysLoginService;

    @GetMapping({"/auth", "/ssoAuth"})
    public Object auth(@RequestParam("client") String client,
                       @RequestParam("redirect") String redirect,
                       @RequestParam(value = "state", required = false) String state,
                       @RequestParam(value = "mode", defaultValue = "ticket") String mode) {
        try {
            if (!"ticket".equalsIgnoreCase(mode)) {
                throw new SaSsoException("仅支持 ticket 模式");
            }
            validateRedirectUri(client, redirect);
            if (!StpUtil.isLogin()) {
                return new ModelAndView("login.html");
            }
            String loginId = String.valueOf(StpUtil.getLoginId());
            String ticket = createTicket(client, redirect, state, loginId, StpUtil.getTokenValue());
            return SaHolder.getResponse().redirect(buildTicketRedirect(redirect, ticket, state));
        } catch (Exception e) {
            return SaResult.error(e.getMessage());
        }
    }

    @PostMapping({"/doLogin", "/ssoDoLogin"})
    public Object doLogin(@RequestParam("name") String name,
                          @RequestParam("pwd") String pwd) {
        try {
            String token = sysLoginService.ssoLogin(name, pwd, null);
            return SaResult.ok()
                    .set("Admin-Token", token)
                    .set("expireTime", sysLoginService.getTokenExpireTime(token));
        } catch (Exception e) {
            return SaResult.error(e.getMessage());
        }
    }

    @RequestMapping(value = {"/checkTicket", "/ssoCheckTicket"}, method = {RequestMethod.GET, RequestMethod.POST})
    public Object checkTicket(@RequestParam("ticket") String ticket,
                              @RequestParam(value = "client", required = false) String client,
                              @RequestParam(value = "ssoLogoutCall", required = false) String ssoLogoutCall,
                              @RequestParam(value = "sso_logout_call", required = false) String snakeSsoLogoutCall,
                              @RequestParam(value = "client_session_id", required = false) String clientSessionId) {
        try {
            FastbeeSsoTicket ticketModel = consumeTicket(ticket, client);
            String logoutCall = firstNotBlank(ssoLogoutCall, snakeSsoLogoutCall);
            if (logoutCall != null) {
                validateLogoutCallback(ticketModel.client, logoutCall);
                ClientLogoutSession session = new ClientLogoutSession();
                session.client = ticketModel.client;
                session.loginId = ticketModel.loginId;
                session.clientSessionId = clientSessionId;
                session.ssoLogoutCall = logoutCall;
                session.registerTime = System.currentTimeMillis();
                saveLogoutSession(session);
            }
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("loginId", ticketModel.loginId);
            data.put("tokenValue", ticketModel.tokenValue);
            data.put("client", ticketModel.client);
            data.put("redirect", ticketModel.redirect);
            SysUser user = getUser(ticketModel.loginId);
            if (user != null) {
                data.put("userId", user.getUserId());
                data.put("userName", user.getUserName());
                data.put("nickName", user.getNickName());
                data.put("deptId", user.getDeptId());
                data.put("phonenumber", user.getPhonenumber());
                data.put("email", user.getEmail());
            }
            return SaResult.ok()
                    .set("loginId", ticketModel.loginId)
                    .set("tokenValue", ticketModel.tokenValue)
                    .set("deviceId", getDeviceId(ticketModel.tokenValue))
                    .set("remainTokenTimeout", getTokenTimeout(ticketModel.tokenValue))
                    .set("remainSessionTimeout", getSessionTimeout(ticketModel.loginId))
                    .set("client", ticketModel.client)
                    .setData(data);
        } catch (Exception e) {
            return SaResult.error(e.getMessage());
        }
    }

    @GetMapping("/client/login")
    public Object clientLogin(@RequestParam("client") String client,
                              @RequestParam("redirect") String redirect,
                              @RequestParam(value = "state", required = false) String state) {
        validateRedirectUri(client, redirect);
        String authUrl = "/sso/auth?client=" + encode(client)
                + "&redirect=" + encode(redirect)
                + "&mode=ticket";
        if (state != null && !state.isBlank()) {
            authUrl += "&state=" + encode(state);
        }
        return SaHolder.getResponse().redirect(authUrl);
    }

    @GetMapping("/client/callback")
    public Object clientCallback(@RequestParam("ticket") String ticket,
                                 @RequestParam(value = "client", required = false) String client) {
        return checkTicket(ticket, client, null, null, null);
    }

    @PostMapping("/slo/register")
    public Object registerSloClient(@RequestParam("client") String client,
                                    @RequestParam("sso_logout_call") String ssoLogoutCall,
                                    @RequestParam(value = "client_session_id", required = false) String clientSessionId) {
        try {
            if (!StpUtil.isLogin()) {
                return SaResult.error("need login");
            }
            validateLogoutCallback(client, ssoLogoutCall);
            ClientLogoutSession session = new ClientLogoutSession();
            session.client = client;
            session.loginId = String.valueOf(StpUtil.getLoginId());
            session.clientSessionId = clientSessionId;
            session.ssoLogoutCall = ssoLogoutCall;
            session.registerTime = System.currentTimeMillis();
            saveLogoutSession(session);
            return SaResult.ok();
        } catch (Exception e) {
            return SaResult.error(e.getMessage());
        }
    }

    @GetMapping({"/logout", "/signout", "/ssoSignout"})
    public Object logout(HttpServletResponse response,
                         @RequestParam(value = "client", required = false) String client,
                         @RequestParam(value = "redirect", required = false) String redirect) throws Exception {
        try {
            String loginId = StpUtil.isLogin() ? String.valueOf(StpUtil.getLoginId()) : null;
            if (loginId != null) {
                pushSingleLogout(loginId);
                StpUtil.logout();
            }
            if (redirect != null && !redirect.isBlank()) {
                validateRedirectUri(client, redirect);
                response.sendRedirect(redirect);
                return null;
            }
            return SaResult.ok();
        } catch (Exception e) {
            return SaResult.error(e.getMessage());
        }
    }

    @PostMapping({"/logout", "/signout", "/ssoSignout"})
    public Object logoutByPost() {
        if (StpUtil.isLogin()) {
            String loginId = String.valueOf(StpUtil.getLoginId());
            pushSingleLogout(loginId);
            StpUtil.logout();
        }
        return SaResult.ok();
    }

    @PostMapping({"/logoutCall", "/ssoLogoutCall"})
    public Object logoutCall() {
        if (StpUtil.isLogin()) {
            StpUtil.logout();
        }
        return SaResult.ok();
    }

    String createTicket(String client, String redirect, String state, String loginId, String tokenValue) {
        FastbeeSsoTicket ticket = new FastbeeSsoTicket();
        ticket.ticket = "ST-" + IdUtil.fastSimpleUUID();
        ticket.client = client;
        ticket.redirect = redirect;
        ticket.state = state;
        ticket.loginId = loginId;
        ticket.tokenValue = tokenValue;
        ticket.createTime = System.currentTimeMillis();
        SaManager.getSaTokenDao().set(ticketKey(ticket.ticket), JSON.toJSONString(ticket), TICKET_TIMEOUT);
        return ticket.ticket;
    }

    FastbeeSsoTicket consumeTicket(String ticket, String client) {
        if (ticket == null || ticket.isBlank()) {
            throw new SaSsoException("ticket 不能为空");
        }
        String key = ticketKey(ticket);
        String value = SaManager.getSaTokenDao().get(key);
        if (value == null || value.isBlank()) {
            throw new SaSsoException("ticket 无效或已过期");
        }
        SaManager.getSaTokenDao().delete(key);
        FastbeeSsoTicket ticketModel = JSON.parseObject(value, FastbeeSsoTicket.class);
        if (client != null && !client.isBlank() && !Objects.equals(client, ticketModel.client)) {
            throw new SaSsoException("ticket 与 client 不匹配");
        }
        return ticketModel;
    }

    String buildTicketRedirect(String redirect, String ticket, String state) {
        String url = appendQuery(redirect, "ticket", ticket);
        if (state != null && !state.isBlank()) {
            url = appendQuery(url, "state", state);
        }
        return url;
    }

    String ticketKey(String ticket) {
        return TICKET_KEY_PREFIX + ticket;
    }

    String buildLogoutBody(ClientLogoutSession session) {
        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("iss", "fastbee-sso");
        claims.put("sub", session.loginId);
        claims.put("aud", session.client);
        claims.put("iat", System.currentTimeMillis() / 1000);
        claims.put("client_session_id", session.clientSessionId);
        String logoutToken = SaJwtUtil.createToken(claims, SaManager.getConfig().getJwtSecretKey());
        return "client=" + encode(session.client)
                + "&login_id=" + encode(session.loginId)
                + "&client_session_id=" + encode(session.clientSessionId)
                + "&logout_token=" + encode(logoutToken);
    }

    private void validateRedirectUri(String client, String redirect) {
        if (client == null || client.isBlank()) {
            throw new SaSsoException("client 不能为空");
        }
        if (redirect == null || redirect.isBlank()) {
            throw new SaSsoException("redirect 不能为空");
        }
        SaOAuth2Manager.getTemplate().checkRedirectUri(client, redirect);
    }

    private void validateLogoutCallback(String client, String logoutCallbackUrl) {
        if (logoutCallbackUrl == null || logoutCallbackUrl.isBlank()) {
            throw new SaSsoException("sso_logout_call 不能为空");
        }
        URI callbackUri = URI.create(logoutCallbackUrl);
        if (!"https".equalsIgnoreCase(callbackUri.getScheme()) && !"http".equalsIgnoreCase(callbackUri.getScheme())) {
            throw new SaSsoException("sso_logout_call 仅支持 http/https");
        }
        SaClientModel clientModel = SaOAuth2Manager.getTemplate().checkClientModel(client);
        List<String> redirectUris = clientModel.getAllowRedirectUris();
        if (redirectUris == null || redirectUris.isEmpty() || redirectUris.contains("*")) {
            return;
        }
        boolean sameOrigin = redirectUris.stream()
                .filter(Objects::nonNull)
                .map(URI::create)
                .anyMatch(uri -> sameOrigin(uri, callbackUri));
        if (!sameOrigin) {
            throw new SaSsoException("sso_logout_call 不在客户端允许的回调域名内");
        }
    }

    private boolean sameOrigin(URI left, URI right) {
        return Objects.equals(left.getScheme(), right.getScheme())
                && Objects.equals(left.getHost(), right.getHost())
                && normalizePort(left) == normalizePort(right);
    }

    private int normalizePort(URI uri) {
        if (uri.getPort() > 0) {
            return uri.getPort();
        }
        return "https".equalsIgnoreCase(uri.getScheme()) ? 443 : 80;
    }

    private String appendQuery(String url, String name, String value) {
        String separator;
        if (!url.contains("?")) {
            separator = "?";
        } else if (url.endsWith("?") || url.endsWith("&")) {
            separator = "";
        } else {
            separator = "&";
        }
        return url + separator + encode(name) + "=" + encode(value);
    }

    private String encode(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }

    private String firstNotBlank(String left, String right) {
        if (left != null && !left.isBlank()) {
            return left;
        }
        if (right != null && !right.isBlank()) {
            return right;
        }
        return null;
    }

    private String getDeviceId(String tokenValue) {
        try {
            return StpUtil.getLoginDeviceIdByToken(tokenValue);
        } catch (Exception e) {
            return null;
        }
    }

    private Long getTokenTimeout(String tokenValue) {
        try {
            return StpUtil.getTokenTimeout(tokenValue);
        } catch (Exception e) {
            return null;
        }
    }

    private Long getSessionTimeout(String loginId) {
        try {
            return StpUtil.getStpLogic().getSessionTimeoutByLoginId(loginId);
        } catch (Exception e) {
            return null;
        }
    }

    private SysUser getUser(String loginId) {
        try {
            return sysUserService.selectUserById(Long.valueOf(loginId));
        } catch (Exception e) {
            return null;
        }
    }

    private void saveLogoutSession(ClientLogoutSession session) {
        Map<String, ClientLogoutSession> sessions = getLogoutSessions(session.loginId);
        sessions.put(session.sessionKey(), session);
        SaManager.getSaTokenDao().set(logoutSessionKey(session.loginId), JSON.toJSONString(sessions), SLO_SESSION_TIMEOUT);
    }

    private Map<String, ClientLogoutSession> getLogoutSessions(String loginId) {
        String value = SaManager.getSaTokenDao().get(logoutSessionKey(loginId));
        if (value == null || value.isBlank()) {
            return new LinkedHashMap<>();
        }
        return JSON.parseObject(value, new TypeReference<LinkedHashMap<String, ClientLogoutSession>>() {});
    }

    private void pushSingleLogout(String loginId) {
        Map<String, ClientLogoutSession> sessions = getLogoutSessions(loginId);
        if (sessions.isEmpty()) {
            return;
        }
        sessions.values().forEach(this::pushSingleLogout);
        SaManager.getSaTokenDao().delete(logoutSessionKey(loginId));
    }

    private void pushSingleLogout(ClientLogoutSession session) {
        try {
            String body = buildLogoutBody(session);
            HttpRequest request = HttpRequest.newBuilder(URI.create(session.ssoLogoutCall))
                    .timeout(SLO_PUSH_TIMEOUT)
                    .header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.discarding());
        } catch (Exception e) {
            SaManager.getLog().warn("SSO SLO push failed, client={}, url={}, error={}", session.client, session.ssoLogoutCall, e.getMessage());
        }
    }

    private String logoutSessionKey(String loginId) {
        return SLO_SESSION_KEY_PREFIX + loginId;
    }

    public static class FastbeeSsoTicket {
        public String ticket;
        public String client;
        public String redirect;
        public String state;
        public String loginId;
        public String tokenValue;
        public long createTime;
    }

    public static class ClientLogoutSession {
        public String client;
        public String loginId;
        public String clientSessionId;
        public String ssoLogoutCall;
        public long registerTime;

        public String sessionKey() {
            return client + ":" + Integer.toHexString(Objects.hash(ssoLogoutCall, clientSessionId));
        }
    }
}
