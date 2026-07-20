package com.fastbee.oauth.controller;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.jwt.SaJwtUtil;
import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.config.SaOAuth2ServerConfig;
import cn.dev33.satoken.oauth2.consts.SaOAuth2Consts;
import cn.dev33.satoken.oauth2.data.generate.SaOAuth2DataGenerate;
import cn.dev33.satoken.oauth2.data.generate.SaOAuth2DataGenerateDefaultImpl;
import cn.dev33.satoken.oauth2.data.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.data.model.CodeModel;
import cn.dev33.satoken.oauth2.data.model.loader.SaClientModel;
import cn.dev33.satoken.oauth2.data.model.request.RequestAuthModel;
import cn.dev33.satoken.oauth2.error.SaOAuth2ErrorCode;
import cn.dev33.satoken.oauth2.exception.SaOAuth2Exception;
import cn.dev33.satoken.oauth2.processor.SaOAuth2ServerProcessor;
import cn.dev33.satoken.oauth2.scope.handler.OidcScopeHandler;
import cn.dev33.satoken.oauth2.scope.handler.OpenIdScopeHandler;
import cn.dev33.satoken.oauth2.strategy.SaOAuth2Strategy;
import cn.dev33.satoken.oauth2.template.SaOAuth2Template;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.dev33.satoken.util.SaResult;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.system.service.ISysUserService;
import com.fastbee.system.service.sys.SysLoginService;

/**
 * Sa-Token-OAuth2 Server 认证端 Controller
 *
 * @author click33
 */
@RestController
public class SaOAuth2ServerController {

	private static final String SLO_SESSION_KEY_PREFIX = "oauth2:slo:sessions:";
	private static final long SLO_SESSION_TIMEOUT = 30 * 24 * 60 * 60L;
	private static final Duration SLO_PUSH_TIMEOUT = Duration.ofSeconds(3);
	private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
			.connectTimeout(SLO_PUSH_TIMEOUT)
			.build();

	@Resource
	private ISysUserService sysUserService;

	@Resource
	private SysLoginService sysLoginService;

	// OAuth2-Server 端：处理所有 OAuth2 相关请求
	@RequestMapping("/oauth2/*")
	public Object request() {
		return SaOAuth2ServerProcessor.instance.dister();
	}

	@GetMapping({"/oauth2/authorize", "/oauth/authorize"})
	public Object authorize() {
		return SaOAuth2ServerProcessor.instance.authorize();
	}

	@PostMapping({"/oauth2/authorize", "/oauth/authorize"})
	public Object authorizeByPost() {
		return SaOAuth2ServerProcessor.instance.authorize();
	}

	@PostMapping({"/oauth2/token", "/oauth/token"})
	public Object token() {
		return SaOAuth2ServerProcessor.instance.token();
	}

	@PostMapping({"/oauth2/doLogin", "/oauth/doLogin"})
	public Object doLogin() {
		return SaOAuth2ServerProcessor.instance.doLogin();
	}

	@PostMapping({"/oauth2/doConfirm", "/oauth/doConfirm"})
	public Object doConfirm() {
		return SaOAuth2ServerProcessor.instance.doConfirm();
	}

	@PostMapping("/oauth2/refresh")
	public Object refresh() {
		return SaOAuth2ServerProcessor.instance.refresh();
	}

	@PostMapping({"/oauth2/check-token", "/oauth/check_token"})
	public Object checkToken(HttpServletRequest request,
							 @RequestParam(value = "token", required = false) String token,
							 @RequestParam(value = "access_token", required = false) String accessToken) {
		try {
			AccessTokenModel model = SaOAuth2Manager.getTemplate().checkAccessToken(obtainAccessToken(request, token, accessToken));
			return SaResult.ok()
					.set("client_id", model.getClientId())
					.set("login_id", model.getLoginId())
					.set("scope", joinScopes(model.getScopes()))
					.set("expires_in", model.getExpiresIn())
					.set("grant_type", model.getGrantType());
		} catch (Exception e) {
			return SaResult.error(e.getMessage());
		}
	}

	@PostMapping({"/oauth2/revoke", "/oauth/revoke"})
	public Object revoke(HttpServletRequest request,
						 @RequestParam(value = "token", required = false) String token,
						 @RequestParam(value = "access_token", required = false) String accessToken) {
		return revokeToken(request, token, accessToken);
	}

	@DeleteMapping({"/oauth2/revoke", "/oauth/revoke"})
	public Object revokeByDelete(HttpServletRequest request,
								 @RequestParam(value = "token", required = false) String token,
								 @RequestParam(value = "access_token", required = false) String accessToken) {
		return revokeToken(request, token, accessToken);
	}

	@GetMapping({"/oauth/login", "/oauth/index"})
	public ModelAndView legacyLoginView() {
		return new ModelAndView("login.html");
	}

	@GetMapping({"/.well-known/openid-configuration", "/oauth2/.well-known/openid-configuration"})
	public Object openidConfiguration(HttpServletRequest request) {
		String issuer = buildIssuer(request);
		Map<String, Object> metadata = new LinkedHashMap<>();
		metadata.put("issuer", issuer);
		metadata.put("authorization_endpoint", issuer + "/oauth2/authorize");
		metadata.put("token_endpoint", issuer + "/oauth2/token");
		metadata.put("userinfo_endpoint", issuer + "/oauth2/userinfo");
		metadata.put("revocation_endpoint", issuer + "/oauth2/revoke");
		metadata.put("introspection_endpoint", issuer + "/oauth2/check-token");
		metadata.put("jwks_uri", issuer + "/oauth2/jwks");
		metadata.put("end_session_endpoint", issuer + "/oauth2/logout");
		metadata.put("response_types_supported", List.of("code", "token"));
		metadata.put("grant_types_supported", List.of("authorization_code", "refresh_token", "password", "client_credentials", "implicit"));
		metadata.put("subject_types_supported", List.of("public"));
		metadata.put("scopes_supported", List.of("openid", "oidc", "userinfo", "user_info"));
		metadata.put("token_endpoint_auth_methods_supported", List.of("client_secret_basic", "client_secret_post"));
		metadata.put("id_token_signing_alg_values_supported", List.of("HS256"));
		metadata.put("claims_supported", List.of("sub", "client_id", "userId", "userName", "nickName", "deptId", "phonenumber", "email"));
		return metadata;
	}

	@GetMapping("/oauth2/jwks")
	public Object jwks() {
		Map<String, Object> jwks = new LinkedHashMap<>();
		jwks.put("keys", List.of());
		jwks.put("note", "当前使用 HS256 对称密钥签发 id_token，不公开 JWKS；生产建议切换为 RSA/EC 非对称签名。");
		return jwks;
	}

	@GetMapping("/oauth2/userinfo")
	public Object userinfo(HttpServletRequest request,
						   @RequestParam(value = "access_token", required = false) String accessToken) {
		try {
			AccessTokenModel model = SaOAuth2Manager.getTemplate().checkAccessToken(obtainAccessToken(request, null, accessToken));
			List<String> scopes = model.getScopes();
			if (scopes != null && !scopes.isEmpty()
					&& !scopes.contains("userinfo") && !scopes.contains("user_info") && !scopes.contains("openid")) {
				return SaResult.error("access_token 未授权 userinfo scope");
			}
			Map<String, Object> userInfo = new HashMap<>();
			userInfo.put("sub", String.valueOf(model.getLoginId()));
			userInfo.put("client_id", model.getClientId());
			if (model.getLoginId() != null) {
				SysUser user = getUser(model.getLoginId());
				if (user != null) {
					userInfo.put("userId", user.getUserId());
					userInfo.put("userName", user.getUserName());
					userInfo.put("nickName", user.getNickName());
					userInfo.put("deptId", user.getDeptId());
					userInfo.put("phonenumber", user.getPhonenumber());
					userInfo.put("email", user.getEmail());
				}
			}
			return SaResult.ok().setData(userInfo);
		} catch (Exception e) {
			return SaResult.error(e.getMessage());
		}
	}

	@PostMapping("/oauth2/slo/register")
	public Object registerSloClient(HttpServletRequest request,
									@RequestParam(value = "access_token", required = false) String accessToken,
									@RequestParam(value = "logout_callback_url") String logoutCallbackUrl,
									@RequestParam(value = "client_session_id", required = false) String clientSessionId) {
		try {
			AccessTokenModel model = SaOAuth2Manager.getTemplate().checkAccessToken(obtainAccessToken(request, null, accessToken));
			validateLogoutCallback(model.getClientId(), logoutCallbackUrl);
			ClientLogoutSession session = new ClientLogoutSession();
			session.clientId = model.getClientId();
			session.loginId = String.valueOf(model.getLoginId());
			session.clientSessionId = clientSessionId;
			session.logoutCallbackUrl = logoutCallbackUrl;
			session.registerTime = System.currentTimeMillis();
			saveLogoutSession(session);
			return SaResult.ok();
		} catch (Exception e) {
			return SaResult.error(e.getMessage());
		}
	}

	@GetMapping({"/oauth2/logout", "/oauth/logout"})
	public Object logout(HttpServletRequest request,
						 HttpServletResponse response,
						 @RequestParam(value = "access_token", required = false) String accessToken,
						 @RequestParam(value = "client_id", required = false) String clientId,
						 @RequestParam(value = "post_logout_redirect_uri", required = false) String postLogoutRedirectUri) throws Exception {
		logoutCurrentSession(request, accessToken);
		if (postLogoutRedirectUri != null && !postLogoutRedirectUri.isBlank()) {
			if (clientId == null || clientId.isBlank()) {
				return SaResult.error("client_id 不能为空");
			}
			SaOAuth2Manager.getTemplate().checkRedirectUri(clientId, postLogoutRedirectUri);
			response.sendRedirect(postLogoutRedirectUri);
			return null;
		}
		return SaResult.ok();
	}

	@PostMapping({"/oauth2/logout", "/oauth/logout"})
	public Object logoutByPost(HttpServletRequest request,
							   @RequestParam(value = "access_token", required = false) String accessToken) {
		logoutCurrentSession(request, accessToken);
		return SaResult.ok();
	}

	// Sa-Token OAuth2 定制化配置
	@Autowired
	public void configOAuth2Server(SaOAuth2ServerConfig oauth2Server) {
		SaOAuth2Manager.setDataGenerate(new LegacyStateCompatibleDataGenerate());
		SaOAuth2Strategy.instance.registerScopeHandler(new OidcScopeHandler());
		SaOAuth2Strategy.instance.registerScopeHandler(new OpenIdWithIdTokenScopeHandler());
		// 未登录的视图
		SaOAuth2Strategy.instance.notLoginView = ()->{
			return new ModelAndView("login.html");
		};

		SaOAuth2Strategy.instance.doLoginHandle = (name, pwd) -> {
			try {
				String token = sysLoginService.ssoLogin(name, pwd, null);
				return SaResult.ok()
						.set("Admin-Token", token)
						.set("expireTime", sysLoginService.getTokenExpireTime(token));
			} catch (Exception e) {
				return SaResult.error(e.getMessage());
			}
		};

		// 授权确认视图
		SaOAuth2Strategy.instance.confirmView = (clientId, scopes)->{
			Map<String, Object> map = new HashMap<>();
			map.put("clientId", clientId);
			map.put("scope", scopes);
			return new ModelAndView("confirm.html", map);
		};

	}

	private Object revokeToken(HttpServletRequest request, String token, String accessToken) {
		try {
			SaOAuth2Manager.getTemplate().revokeAccessToken(obtainAccessToken(request, token, accessToken));
			return SaResult.ok();
		} catch (Exception e) {
			return SaResult.error(e.getMessage());
		}
	}

	private void logoutCurrentSession(HttpServletRequest request, String accessToken) {
		String token = obtainAccessToken(request, null, accessToken);
		String loginId = null;
		if (token != null && !token.isBlank()) {
			AccessTokenModel accessTokenModel = SaOAuth2Manager.getTemplate().getAccessToken(token);
			if (accessTokenModel != null && accessTokenModel.getLoginId() != null) {
				loginId = String.valueOf(accessTokenModel.getLoginId());
			}
			SaOAuth2Manager.getTemplate().revokeAccessToken(token);
		}
		if (StpUtil.isLogin()) {
			loginId = String.valueOf(StpUtil.getLoginId());
			StpUtil.logout();
		}
		if (loginId != null && !loginId.isBlank()) {
			pushSingleLogout(loginId);
		}
	}

	private String obtainAccessToken(HttpServletRequest request, String token, String accessToken) {
		String value = accessToken != null && !accessToken.isBlank() ? accessToken : token;
		if (value == null || value.isBlank()) {
			value = request.getHeader("Authorization");
		}
		if (value != null && value.toLowerCase().startsWith("bearer ")) {
			value = value.substring(7);
		}
		return value;
	}

	private String buildIssuer(HttpServletRequest request) {
		String proto = firstHeader(request, "X-Forwarded-Proto", "X-Forwarded-Protocol");
		String host = firstHeader(request, "X-Forwarded-Host", "Host");
		if (proto == null || proto.isBlank()) {
			proto = request.getScheme();
		}
		if (host == null || host.isBlank()) {
			host = request.getServerName();
			int port = request.getServerPort();
			if (port > 0 && !(("http".equals(proto) && port == 80) || ("https".equals(proto) && port == 443))) {
				host = host + ":" + port;
			}
		}
		String contextPath = request.getContextPath();
		return proto + "://" + host + (contextPath == null ? "" : contextPath);
	}

	private String firstHeader(HttpServletRequest request, String... names) {
		for (String name : names) {
			String value = request.getHeader(name);
			if (value != null && !value.isBlank()) {
				return value.split(",")[0].trim();
			}
		}
		return null;
	}

	private String joinScopes(List<String> scopes) {
		return scopes == null ? "" : String.join(" ", scopes);
	}

	private void validateLogoutCallback(String clientId, String logoutCallbackUrl) {
		if (logoutCallbackUrl == null || logoutCallbackUrl.isBlank()) {
			throw new SaOAuth2Exception("logout_callback_url 不能为空");
		}
		URI callbackUri = URI.create(logoutCallbackUrl);
		if (!"https".equalsIgnoreCase(callbackUri.getScheme()) && !"http".equalsIgnoreCase(callbackUri.getScheme())) {
			throw new SaOAuth2Exception("logout_callback_url 仅支持 http/https");
		}
		SaClientModel client = SaOAuth2Manager.getTemplate().checkClientModel(clientId);
		List<String> redirectUris = client.getAllowRedirectUris();
		if (redirectUris == null || redirectUris.isEmpty() || redirectUris.contains("*")) {
			return;
		}
		boolean sameOrigin = redirectUris.stream()
				.filter(Objects::nonNull)
				.map(URI::create)
				.anyMatch(uri -> sameOrigin(uri, callbackUri));
		if (!sameOrigin) {
			throw new SaOAuth2Exception("logout_callback_url 不在客户端允许的回调域名内");
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
			HttpRequest request = HttpRequest.newBuilder(URI.create(session.logoutCallbackUrl))
					.timeout(SLO_PUSH_TIMEOUT)
					.header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
					.POST(HttpRequest.BodyPublishers.ofString(body))
					.build();
			HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.discarding());
		} catch (Exception e) {
			SaManager.getLog().warn("OAuth2 SLO push failed, clientId={}, url={}, error={}", session.clientId, session.logoutCallbackUrl, e.getMessage());
		}
	}

	private String buildLogoutBody(ClientLogoutSession session) {
		Map<String, Object> claims = new LinkedHashMap<>();
		claims.put("iss", session.clientId);
		claims.put("sub", session.loginId);
		claims.put("aud", session.clientId);
		claims.put("iat", System.currentTimeMillis() / 1000);
		claims.put("client_session_id", session.clientSessionId);
		String logoutToken = SaJwtUtil.createToken(claims, SaManager.getConfig().getJwtSecretKey());
		return "client_id=" + encode(session.clientId)
				+ "&login_id=" + encode(session.loginId)
				+ "&client_session_id=" + encode(session.clientSessionId)
				+ "&logout_token=" + encode(logoutToken);
	}

	private String encode(String value) {
		return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
	}

	private String logoutSessionKey(String loginId) {
		return SLO_SESSION_KEY_PREFIX + loginId;
	}

	public static class ClientLogoutSession {
		public String clientId;
		public String loginId;
		public String clientSessionId;
		public String logoutCallbackUrl;
		public long registerTime;

		public String sessionKey() {
			return clientId + ":" + Integer.toHexString(Objects.hash(logoutCallbackUrl, clientSessionId));
		}
	}

	public static class OpenIdWithIdTokenScopeHandler extends OidcScopeHandler {
		private final OpenIdScopeHandler openIdScopeHandler = new OpenIdScopeHandler();

		@Override
		public String getHandlerScope() {
			return "openid";
		}

		@Override
		public void workAccessToken(AccessTokenModel at) {
			openIdScopeHandler.workAccessToken(at);
			super.workAccessToken(at);
		}
	}

	public static class LegacyStateCompatibleDataGenerate extends SaOAuth2DataGenerateDefaultImpl {

		@Override
		public String buildRedirectUri(String redirectUri, String code, String state) {
			String url = SaFoxUtil.joinParam(redirectUri, SaOAuth2Consts.Param.code, code);
			return SaFoxUtil.isEmpty(state) ? url : SaFoxUtil.joinParam(url, SaOAuth2Consts.Param.state, state);
		}

		@Override
		public String buildImplicitRedirectUri(String redirectUri, String accessToken, String state) {
			String url = SaFoxUtil.joinSharpParam(redirectUri, SaOAuth2Consts.Param.token, accessToken);
			return SaFoxUtil.isEmpty(state) ? url : SaFoxUtil.joinSharpParam(url, SaOAuth2Consts.Param.state, state);
		}

		@Override
		public void checkState(String state) {
		}
	}

	private SysUser getUser(Object loginId) {
		try {
			return sysUserService.selectUserById(Long.valueOf(String.valueOf(loginId)));
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * 获取最终授权重定向地址，形如：http://xxx.com/xxx?code=xxxxx
	 *
	 * <p> 情况1：客户端未登录，返回 code=401，提示用户登录 <p/>
	 * <p> 情况2：请求的 scope 需要客户端手动确认授权，返回 code=411，提示用户手动确认 <p/>
	 * <p> 情况3：已登录且请求的 scope 已确认授权，返回 code=200，redirect_uri=最终重定向 url 地址(携带code码参数) <p/>
	 *
	 * @return /
	 */
	@PostMapping("/oauth2/getRedirectUri")
	public Object getRedirectUri() {

		// 获取变量
		SaRequest req = SaHolder.getRequest();
		SaOAuth2ServerConfig cfg = SaOAuth2Manager.getServerConfig();
		SaOAuth2DataGenerate dataGenerate = SaOAuth2Manager.getDataGenerate();
		SaOAuth2Template oauth2Template = SaOAuth2Manager.getTemplate();
		String responseType = req.getParamNotNull(SaOAuth2Consts.Param.response_type);

		// 1、先判断是否开启了指定的授权模式
		SaOAuth2ServerProcessor.instance.checkAuthorizeResponseType(responseType, req, cfg);

		// 2、如果尚未登录, 则先去登录
		long loginId = SaOAuth2Manager.getStpLogic().getLoginId(0L);
		if(loginId == 0L) {
			return SaResult.get(401, "need login", null);
		}

		// 3、构建请求 Model
		RequestAuthModel ra = SaOAuth2Manager.getDataResolver().readRequestAuthModel(req, loginId);

		// 4、开发者自定义的授权前置检查
		SaOAuth2Strategy.instance.userAuthorizeClientCheck.run(ra.loginId, ra.clientId);

		// 5、校验：重定向域名是否合法
		oauth2Template.checkRedirectUri(ra.clientId, ra.redirectUri);

		// 6、校验：此次申请的Scope，该Client是否已经签约
		oauth2Template.checkContractScope(ra.clientId, ra.scopes);

		// 7、判断：如果此次申请的Scope，该用户尚未授权，则转到授权页面
		boolean isNeedCarefulConfirm = oauth2Template.isNeedCarefulConfirm(ra.loginId, ra.clientId, ra.scopes);
		if(isNeedCarefulConfirm) {
			SaClientModel cm = oauth2Template.checkClientModel(ra.clientId);
			if( ! cm.getIsAutoConfirm()) {
				// code=411，需要用户手动确认授权
				return SaResult.get(411, "need confirm", null);
			}
		}

		// 8、判断授权类型，重定向到不同地址
		//         如果是 授权码式，则：开始重定向授权，下放code
		if(SaOAuth2Consts.ResponseType.code.equals(ra.responseType)) {
			CodeModel codeModel = dataGenerate.generateCode(ra);
			String redirectUri = dataGenerate.buildRedirectUri(ra.redirectUri, codeModel.code, ra.state);
			return SaResult.ok().set("redirect_uri", redirectUri);
		}

		//         如果是 隐藏式，则：开始重定向授权，下放 token
		if(SaOAuth2Consts.ResponseType.token.equals(ra.responseType)) {
			AccessTokenModel at = dataGenerate.generateAccessToken(ra, false, null);
			String redirectUri = dataGenerate.buildImplicitRedirectUri(ra.redirectUri, at.accessToken, ra.state);
			return SaResult.ok().set("redirect_uri", redirectUri);
		}

		// 默认返回
		throw new SaOAuth2Exception("无效 response_type: " + ra.responseType).setCode(SaOAuth2ErrorCode.CODE_30125);
	}

}
