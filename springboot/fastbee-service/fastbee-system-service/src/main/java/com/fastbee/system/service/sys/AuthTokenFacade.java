package com.fastbee.system.service.sys;

import java.util.Objects;

import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fastbee.common.extend.core.domin.entity.SysDept;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.extend.core.domin.model.LoginUser;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.system.service.ISysUserService;

/**
 * Bridges the existing JWT LoginUser cache to Sa-Token sessions.
 */
@Slf4j
@Component
public class AuthTokenFacade {

    public static final String LOGIN_USER_SESSION_KEY = "fastbee:loginUser";
    public static final String CLIENT_ID_EXTRA = "clientid";
    public static final String CLIENT_KEY_EXTRA = "clientKey";
    public static final String GRANT_TYPE_EXTRA = "grantType";
    public static final String DEVICE_TYPE_EXTRA = "deviceType";

    @Autowired
    private LoginUserService loginUserService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ISysUserService sysUserService;

    public String createLoginToken(LoginUser loginUser, long timeoutSeconds) {
        return createLoginToken(loginUser, timeoutSeconds, null);
    }

    public String createLoginToken(LoginUser loginUser, long timeoutSeconds, Long activeTimeoutSeconds) {
        if (loginUser == null || loginUser.getUserId() == null) {
            return null;
        }
        try {
            SaLoginParameter parameter = buildLoginParameter(loginUser, timeoutSeconds, activeTimeoutSeconds, null);
            StpUtil.login(loginUser.getUserId(), parameter);
            String token = StpUtil.getTokenValueByLoginId(loginUser.getUserId());
            if (StringUtils.isEmpty(token)) {
                token = StpUtil.getTokenValue();
            }
            bindTokenSession(loginUser, token);
            return token;
        } catch (SaTokenException | IllegalStateException e) {
            log.debug("Create Sa-Token login token failed: {}", e.getMessage());
        }
        return null;
    }

    public void bindLoginUser(LoginUser loginUser, String requestToken, long timeoutSeconds) {
        if (loginUser == null || loginUser.getUserId() == null || StringUtils.isEmpty(requestToken)) {
            return;
        }
        try {
            SaLoginParameter parameter = buildLoginParameter(loginUser, timeoutSeconds, null, requestToken);
            StpUtil.login(loginUser.getUserId(), parameter);
            bindTokenSession(loginUser, requestToken);
        } catch (SaTokenException | IllegalStateException e) {
            log.debug("Bind Sa-Token login state failed: {}", e.getMessage());
        }
    }

    public boolean bindLoginUserFromLegacyToken(String requestToken) {
        LoginUser loginUser = tokenService.getLoginUserByToken(requestToken);
        return bindLoginUserWithLegacyTimeout(loginUser, requestToken);
    }

    public boolean bindLoginUserWithLegacyTimeout(LoginUser loginUser, String requestToken) {
        if (loginUser == null || loginUser.getUserId() == null || StringUtils.isEmpty(requestToken)) {
            return false;
        }
        bindLoginUser(loginUser, requestToken, resolveTimeoutSeconds(loginUser));
        return true;
    }

    public LoginUser getLoginUserByToken(String requestToken) {
        if (StringUtils.isEmpty(requestToken)) {
            return null;
        }
        try {
            Object loginId = StpUtil.getLoginIdByToken(requestToken);
            if (Objects.isNull(loginId)) {
                return null;
            }
            SaSession tokenSession = StpUtil.getTokenSessionByToken(requestToken);
            Object cached = tokenSession.get(LOGIN_USER_SESSION_KEY);
            if (cached instanceof LoginUser loginUser) {
                loginUser.setRequestToken(requestToken);
                return loginUser;
            }
            SysUser sysUser = sysUserService.selectUserById(Long.valueOf(String.valueOf(loginId)));
            if (sysUser == null) {
                return null;
            }
            LoginUser loginUser = loginUserService.createLoginUser(sysUser);
            loginUser.setRequestToken(requestToken);
            loginUser.setToken(requestToken);
            fillClientInfoFromToken(loginUser);
            tokenSession.set(LOGIN_USER_SESSION_KEY, loginUser);
            return loginUser;
        } catch (Exception e) {
            log.debug("Restore login user from Sa-Token failed: {}", e.getMessage());
        }
        return null;
    }

    private SaLoginParameter buildLoginParameter(LoginUser loginUser, long timeoutSeconds, Long activeTimeoutSeconds, String requestToken) {
        SaLoginParameter parameter = StpUtil.createSaLoginParameter()
                .setTimeout(timeoutSeconds)
                .setIsWriteHeader(false)
                .setIsLastingCookie(false)
                .setExtra("tenantId", getTenantId(loginUser))
                .setExtra("userId", loginUser.getUserId())
                .setExtra("userName", loginUser.getUsername())
                .setExtra("username", loginUser.getUsername())
                .setExtra("deptId", loginUser.getDeptId())
                .setExtra("deptName", getDeptName(loginUser))
                .setExtra("deptCategory", getDeptCategory(loginUser))
                .setExtra("userType", getUserType(loginUser));
        if (StringUtils.isNotEmpty(loginUser.getClientId())) {
            parameter.setExtra(CLIENT_ID_EXTRA, loginUser.getClientId());
        }
        if (StringUtils.isNotEmpty(loginUser.getClientKey())) {
            parameter.setExtra(CLIENT_KEY_EXTRA, loginUser.getClientKey());
        }
        if (StringUtils.isNotEmpty(loginUser.getGrantType())) {
            parameter.setExtra(GRANT_TYPE_EXTRA, loginUser.getGrantType());
        }
        if (StringUtils.isNotEmpty(loginUser.getDeviceType())) {
            parameter.setDeviceType(loginUser.getDeviceType());
            parameter.setExtra(DEVICE_TYPE_EXTRA, loginUser.getDeviceType());
        }
        if (activeTimeoutSeconds != null && activeTimeoutSeconds >= 0) {
            parameter.setActiveTimeout(activeTimeoutSeconds);
        }
        if (StringUtils.isNotEmpty(requestToken)) {
            parameter.setToken(requestToken);
        }
        return parameter;
    }

    private long resolveTimeoutSeconds(LoginUser loginUser) {
        if (Boolean.TRUE.equals(loginUser.getNeverExpire())) {
            return -1;
        }
        Long expireTime = loginUser.getExpireTime();
        if (expireTime == null) {
            return -1;
        }
        long timeoutMillis = expireTime - System.currentTimeMillis();
        if (timeoutMillis <= 0) {
            return 1;
        }
        return Math.max(1, (timeoutMillis + 999) / 1000);
    }

    private void bindTokenSession(LoginUser loginUser, String requestToken) {
        if (StringUtils.isEmpty(requestToken)) {
            return;
        }
        loginUser.setRequestToken(requestToken);
        loginUser.setToken(requestToken);
        fillClientInfoFromToken(loginUser);
        SaSession tokenSession = StpUtil.getTokenSessionByToken(requestToken);
        tokenSession.set(LOGIN_USER_SESSION_KEY, loginUser);
    }

    private void fillClientInfoFromToken(LoginUser loginUser) {
        fillStringExtra(CLIENT_ID_EXTRA, loginUser::setClientId);
        fillStringExtra(CLIENT_KEY_EXTRA, loginUser::setClientKey);
        fillStringExtra(GRANT_TYPE_EXTRA, loginUser::setGrantType);
        fillStringExtra(DEVICE_TYPE_EXTRA, loginUser::setDeviceType);
    }

    private void fillStringExtra(String key, java.util.function.Consumer<String> setter) {
        try {
            Object value = StpUtil.getExtra(key);
            if (value != null && StringUtils.isNotEmpty(String.valueOf(value))) {
                setter.accept(String.valueOf(value));
            }
        } catch (Exception ignored) {
        }
    }

    private Long getTenantId(LoginUser loginUser) {
        if (loginUser.getDeptUserId() != null) {
            return loginUser.getDeptUserId();
        }
        SysDept dept = getDept(loginUser);
        return dept == null ? null : dept.getDeptUserId();
    }

    private String getDeptName(LoginUser loginUser) {
        SysDept dept = getDept(loginUser);
        return dept == null ? null : dept.getDeptName();
    }

    private Integer getDeptCategory(LoginUser loginUser) {
        SysDept dept = getDept(loginUser);
        return dept == null ? null : dept.getDeptType();
    }

    private String getUserType(LoginUser loginUser) {
        SysUser user = loginUser.getUser();
        return user == null ? null : user.getUserType();
    }

    private SysDept getDept(LoginUser loginUser) {
        SysUser user = loginUser.getUser();
        return user == null ? null : user.getDept();
    }

    public void logoutByToken(String requestToken) {
        if (StringUtils.isEmpty(requestToken)) {
            return;
        }
        try {
            StpUtil.logoutByTokenValue(requestToken);
        } catch (SaTokenException | IllegalStateException e) {
            log.debug("Logout Sa-Token login state failed: {}", e.getMessage());
        }
    }
}
