package com.fastbee.social.service.impl;

import static com.fastbee.common.constant.HttpStatus.ERROR;
import static com.fastbee.common.constant.HttpStatus.NO_MESSAGE_ALERT;
import static com.fastbee.common.core.domain.AjaxResult.error;
import static com.fastbee.common.core.domain.AjaxResult.success;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import me.zhyd.oauth.exception.AuthException;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.utils.AuthStateUtils;
import me.zhyd.oauth.utils.RandomUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.fastbee.common.constant.CacheConstants;
import com.fastbee.common.constant.Constants;
import com.fastbee.common.constant.HttpStatus;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.core.redis.RedisCache;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.core.domin.entity.SysDept;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.extend.core.domin.model.BindLoginBody;
import com.fastbee.common.extend.core.domin.model.BindRegisterBody;
import com.fastbee.common.extend.core.domin.model.LoginBody;
import com.fastbee.common.extend.core.domin.model.LoginUser;
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.common.utils.sign.Md5Utils;
import com.fastbee.iot.service.IToolService;
import com.fastbee.social.domain.SocialPlatform;
import com.fastbee.social.domain.SocialUser;
import com.fastbee.social.model.RegisterUserInput;
import com.fastbee.social.model.RegisterUserOutput;
import com.fastbee.social.model.login.AuthRequestWrap;
import com.fastbee.social.model.login.BindIdValue;
import com.fastbee.social.model.login.LoginIdValue;
import com.fastbee.social.service.IAuthRequestFactory;
import com.fastbee.social.service.ISocialLoginService;
import com.fastbee.social.service.ISocialUserService;
import com.fastbee.system.service.ISysDeptService;
import com.fastbee.system.service.ISysUserService;
import com.fastbee.system.service.sys.SysLoginService;
import com.fastbee.system.service.sys.TokenService;

/**
 * 第三方登录Service业务层处理
 *
 * @author json
 * @date 2022-04-12
 */
@Service
public class SocialLoginServiceImpl implements ISocialLoginService {

    public static final Integer BIND_EXPIRE_TIME = 60 * 60;
    public static final Integer LOGIN_SOCIAL_EXPIRE_TIME = 60;
    public static final Integer ONLINE_STATUS = 0;
    public static final String DEL_FLAG = "0";
    public static final Integer WX_BIND_EXPIRE_TIME = 5;

    //redis key: uuid+source
    public static final String BIND_REDIS_KEY = "login:bind:user:";
    //redis key : userId+random 32
    public static final String LOGIN_SOCIAL_REDIS_KEY = "login:social:user:";
    //redis key : msg+ code+currentTime
    public static final String LOGIN_ERROR_MSG_REDIS_KEY = "login:error:msg:";
    //redis key : userId+random 32
    public static final String WX_BIND_REDIS_KEY = "wx:bind:user:";

    public static final String HTTPS = "https://";

    private static final Logger log = LoggerFactory.getLogger(SocialLoginServiceImpl.class);

    @Resource
    private RedisCache redisCache;

    @Resource
    private TokenService tokenService;

    @Resource
    private SysLoginService sysLoginService;

    @Resource
    private ISysUserService iSysUserService;

    @Resource
    private IAuthRequestFactory iAuthRequestFactory;

    @Resource
    private ISocialUserService iSocialUserService;

    @Resource
    private IToolService toolService;
    @Resource
    private ISysDeptService sysDeptService;


    @Override
    public String renderAuth(String source, HttpServletRequest httpServletRequest) {
        AuthRequestWrap authRequestWrap = null;
        try {
            authRequestWrap = iAuthRequestFactory.getAuthRequest(source);
            checkSocialPlatform(authRequestWrap.getSocialPlatform());
            return authRequestWrap.getAuthRequest().authorize(AuthStateUtils.createState());
        } catch (AuthException authException) {
            //返回错误信息
            log.error("", authException);
            if (authRequestWrap != null) {
                String errorId = genErrorId(authException.getMessage());
                return authRequestWrap.getSocialPlatform().getErrorMsgUri() + errorId;
            } else {
                return httpServletRequest.getProtocol() + httpServletRequest.getServerName() + httpServletRequest.getServerPort();
            }
        } catch (Exception exception) {
            //这类错误 直接不返回，重定向到主页
            log.error("", exception);
            return HTTPS + httpServletRequest.getServerName();
        }

    }

    @Override
    public String callback(String source, AuthCallback authCallback, HttpServletRequest httpServletRequest) {
        AuthRequestWrap authRequestWrap = null;
        try {
            authRequestWrap = iAuthRequestFactory.getAuthRequest(source);
            checkSocialPlatform(authRequestWrap.getSocialPlatform());
            AuthResponse<AuthUser> authResponse = authRequestWrap.getAuthRequest().login(authCallback);
            String bindId = null;
            String loginId = null;
            if (authResponse.ok()) {

                SocialUser socialUser = findSocialUser(authResponse.getData().getUuid(), authResponse.getData().getSource());
                // 同一开放平台下(unionid唯一)如果有其他应用已经微信登录绑定了账号，则直接绑定该账号登录
                Long bindSysUserId = null;
                String unionId = (String) authResponse.getData().getRawUserInfo().get("unionid");
                if ((socialUser == null || socialUser.getSysUserId() == null) && StringUtils.isNotEmpty(unionId)) {
                    bindSysUserId = iSocialUserService.selectSysUserIdByUnionId(unionId);
                }
                createOrUpdateSocialUser(socialUser, authResponse.getData(), source, bindSysUserId);
                if (bindSysUserId != null) {
                    socialUser = new SocialUser();
                    socialUser.setSysUserId(bindSysUserId);
                }
                if (socialUser == null) {
                    //第一次登录
                    bindId = genBindId(authResponse.getData());
                } else if (socialUser.getSysUserId() == null || socialUser.getSysUserId() <= 0) {
                    //初次绑定
                    bindId = genBindId(authResponse.getData());
                } else {
                    //查看是否已经绑定
                    SysUser sysUser = iSysUserService.selectUserById(socialUser.getSysUserId());
                    if (sysUser == null) {
                        bindId = genBindId(authResponse.getData());
                    } else {
                        //直接登录跳转
                        loginId = genLoginId(sysUser);
                    }
                }
                if (StringUtils.isNotEmpty(bindId)) {
                    return authRequestWrap.getSocialPlatform().getBindUri() + bindId;
                } else {
                    return authRequestWrap.getSocialPlatform().getRedirectLoginUri() + loginId;
                }
            } else {
                log.error("登录授权异常,code:{}, msg:{}", authResponse.getCode(), authResponse.getMsg());
                String errorId = genErrorId(authResponse.getMsg());
                return authRequestWrap.getSocialPlatform().getErrorMsgUri() + errorId;
            }
        } catch (AuthException authException) {
            //返回错误信息
            log.error("", authException);
            if (authRequestWrap != null) {
                String errorId = genErrorId(authException.getMessage());
                return authRequestWrap.getSocialPlatform().getErrorMsgUri() + errorId;
            } else {
                return httpServletRequest.getServerName() + httpServletRequest.getServerPort();
            }
        } catch (Exception exception) {
            log.error("", exception);
            return HTTPS + httpServletRequest.getServerName();
        }

    }

    @Override
    public AjaxResult checkBindId(String bindId) {
        AjaxResult ajax = AjaxResult.success();
        ajax.put("bindAccount", false);
        if (StringUtils.isEmpty(bindId)) {
            return ajax;
        }
        BindIdValue bindValue = redisCache.getCacheObject(BIND_REDIS_KEY + bindId);
        if (bindValue == null) {
            return ajax;
        }
        ajax.put("bindAccount", true);
        return ajax;
    }

    @Override
    public AjaxResult getErrorMsg(String errorId) {
        String errorMsg = redisCache.getCacheObject(LOGIN_ERROR_MSG_REDIS_KEY + errorId);
        if (StringUtils.isEmpty(errorMsg)) {
            return error(NO_MESSAGE_ALERT, "");
        } else {
            return error(errorMsg);
        }
    }

    @Override
    public AjaxResult socialLogin(String loginId, String language) {
        AjaxResult ajax = AjaxResult.success();
        String loginKey = LOGIN_SOCIAL_REDIS_KEY + loginId;
        LoginIdValue loginIdValue = redisCache.getCacheObject(loginKey);
        if (loginIdValue != null) {
            if (null == loginIdValue.getDeptId()) {
                return error(ERROR,"web端不允许终端用户登录");
            }
            String token = sysLoginService.redirectLogin(loginIdValue.getUsername(), loginIdValue.getPassword(), language);
            putLoginToken(ajax, token);
        } else {
            log.info("loginId:{} ", loginId);
            return error(NO_MESSAGE_ALERT, "数据错误");
        }
        return ajax;
    }

    @Override
    public AjaxResult bindLogin(BindLoginBody bindLoginBody, String language) {
        BindIdValue bindValue = redisCache.getCacheObject(BIND_REDIS_KEY + bindLoginBody.getBindId());
        SocialUser socialUser = findSocialUser(bindValue.getUuid(), bindValue.getSource());
        AjaxResult checkAjax = checkSocialUser(socialUser, bindLoginBody.getBindId());
        if (checkAjax != null) {
            return checkAjax;
        }
        AjaxResult ajax = AjaxResult.success();
        // 检查账号是否已经绑定微信
        SysUser sysUser = iSysUserService.selectUserByUserName(bindLoginBody.getUsername());
        if (sysUser == null) {
            // 单独返回code用户不存在,给前端处理
            return AjaxResult.error(HttpStatus.USER_NO_EXIST, MessageUtils.message("socialLogin.user.not.exist"));
        }
        if ("WECHAT_OPEN".equals(bindValue.getSource()) && null == sysUser.getDeptId()) {
            throw new ServiceException(MessageUtils.message("socialLogin.bind.end.user.cannot.login.web"));
        }
//        if ("wechat_open_mobile".equals(bindValue.getSource()) && null != sysUser.getDeptId()) {
//            throw new ServiceException("请勿绑定后台用户");
//        }
        // 自定义一下密码错误的提示
        if(!SecurityUtils.matchesPassword(bindLoginBody.getPassword(), sysUser.getPassword())){
            throw new ServiceException(MessageUtils.message("password.fail"));
        }
        List<SocialUser> socialUserList = iSocialUserService.selectBySysUserId(sysUser.getUserId());
        if (CollectionUtils.isNotEmpty(socialUserList)) {
            throw new ServiceException(MessageUtils.message("socialLogin.account.already.bind.other.wechat.please.unbind"));
        }
        // 生成令牌
        String token = sysLoginService.login(bindLoginBody.getUsername(), bindLoginBody.getPassword(), bindLoginBody.getCode(),
                bindLoginBody.getUuid(), null, language);
        LoginUser loginUser = tokenService.getLoginUserByToken(token);
        //绑定和更新
        SocialUser updateSocialUser = new SocialUser();
        updateSocialUser.setSysUserId(loginUser.getUserId());
        updateSocialUser.setStatus(1);
        updateSocialUser.setSocialUserId(socialUser.getSocialUserId());
        iSocialUserService.updateSocialUser(updateSocialUser);
        putLoginToken(ajax, token);
        redisCache.deleteObject(BIND_REDIS_KEY + bindLoginBody.getBindId());
        return ajax;
    }

    @Override
    public AjaxResult bindRegister(BindRegisterBody bindRegisterBody, String language) {

        BindIdValue bindValue = redisCache.getCacheObject(BIND_REDIS_KEY + bindRegisterBody.getBindId());
        SocialUser socialUser = findSocialUser(bindValue.getUuid(), bindValue.getSource());
        AjaxResult checkAjax = checkSocialUser(socialUser, bindRegisterBody.getBindId());
        if (checkAjax != null) {
            return checkAjax;
        }

        AjaxResult ajax = AjaxResult.success();
        RegisterUserInput registerUserInput = new RegisterUserInput();
        BeanUtils.copyProperties(bindRegisterBody, registerUserInput);
        if ("WECHAT_OPEN".equals(bindValue.getSource())) {
            registerUserInput.setSourceType(1);
        }
        if ("wechat_open_mobile".equals(bindValue.getSource())) {
            registerUserInput.setSourceType(3);
        }
        String msg = iSocialUserService.register(registerUserInput);
        if (StringUtils.isNotEmpty(msg)) {
            return error(msg);
        }
        SysUser sysUser = iSysUserService.selectUserByUserName(bindRegisterBody.getUsername());
        //绑定和更新
        SocialUser updateSocialUser = new SocialUser();
        updateSocialUser.setSysUserId(sysUser.getUserId());
        updateSocialUser.setStatus(1);
        updateSocialUser.setSocialUserId(socialUser.getSocialUserId());
        iSocialUserService.updateSocialUser(updateSocialUser);
        redisCache.deleteObject(BIND_REDIS_KEY + bindRegisterBody.getBindId());
        // 生成令牌
        String token = sysLoginService.redirectLogin(sysUser.getUserName(), sysUser.getPassword(), language);
        putLoginToken(ajax, token);
        return ajax;
    }

    private void checkSocialPlatform(SocialPlatform socialPlatform) {
        if (Objects.nonNull(socialPlatform)
                && (!ONLINE_STATUS.equals(socialPlatform.getStatus()) || !DEL_FLAG.equals(socialPlatform.getDelFlag()))) {
            throw new AuthException("当前第三方登录平台被禁用");
        }
    }

    @Override
    public SocialUser findSocialUser(String uuid, String source) {
        SocialUser socialUser = new SocialUser();
        socialUser.setSource(source);
        socialUser.setUuid(uuid);
        List<SocialUser> socialUserList = iSocialUserService.selectSocialUserList(socialUser);
        return socialUserList == null || socialUserList.isEmpty() ? null : socialUserList.get(0);

    }

    public void createOrUpdateSocialUser(SocialUser socialUser, AuthUser authUser, String source, Long bindSysUserId) {
        if (socialUser != null) {
            //更新数据
            SocialUser updateSocialUser = new SocialUser();
            updateSocialUser.setSocialUserId(socialUser.getSocialUserId());
            replaceSocialUser(updateSocialUser, authUser);
            updateSocialUser.setUpdateBy("System");
            updateSocialUser.setUpdateTime(DateUtils.getNowDate());
            updateSocialUser.setSourceClient(source);
            if (socialUser.getSysUserId() == null && bindSysUserId != null) {
                updateSocialUser.setSysUserId(bindSysUserId);
                updateSocialUser.setStatus(1);
            }
            iSocialUserService.updateSocialUser(updateSocialUser);
        } else {
            //创建
            SocialUser saveSocialUser = new SocialUser();
            replaceSocialUser(saveSocialUser, authUser);
            saveSocialUser.setDelFlag("0");
            saveSocialUser.setCreateBy("System");
            saveSocialUser.setCreateTime(DateUtils.getNowDate());
            saveSocialUser.setSourceClient(source);
            if (bindSysUserId != null) {
                saveSocialUser.setSysUserId(bindSysUserId);
                saveSocialUser.setStatus(1);
            } else {
                saveSocialUser.setStatus(0);
            }
            iSocialUserService.insertSocialUser(saveSocialUser);
        }
    }

    private void replaceSocialUser(SocialUser socialUser, AuthUser authUser) {

        socialUser.setUuid(authUser.getUuid());
        socialUser.setSource(authUser.getSource());
        socialUser.setAccessToken(authUser.getToken().getAccessToken());
        //nullable
        socialUser.setExpireIn((long) authUser.getToken().getExpireIn());
        socialUser.setRefreshToken(authUser.getToken().getRefreshToken());
        socialUser.setOpenId(authUser.getToken().getOpenId());
        socialUser.setUid(authUser.getToken().getUid());
        socialUser.setAccessCode(authUser.getToken().getAccessCode());
        socialUser.setUnionId(authUser.getToken().getUnionId());
        socialUser.setCode(authUser.getToken().getCode());
        socialUser.setAvatar(authUser.getAvatar());
        socialUser.setUsername(authUser.getUsername());
        socialUser.setNickname(authUser.getNickname());
    }

    @Override
    public String genBindId(AuthUser authUser) {
        String bindId = Md5Utils.hash(authUser.getUuid() + authUser.getSource());
        String key = BIND_REDIS_KEY + bindId;
        BindIdValue bindIdValue = new BindIdValue();
        bindIdValue.setSource(authUser.getSource());
        bindIdValue.setUuid(authUser.getUuid());
        redisCache.setCacheObject(key, bindIdValue, BIND_EXPIRE_TIME, TimeUnit.SECONDS);
        return bindId;
    }

    private String genLoginId(SysUser sysUser) {
        String loginId = Md5Utils.hash(sysUser.getUserId() + RandomUtil.randomString(32));
        String key = LOGIN_SOCIAL_REDIS_KEY + loginId;
        LoginIdValue loginIdValue = new LoginIdValue();
        loginIdValue.setPassword(sysUser.getPassword());
        loginIdValue.setUsername(sysUser.getUserName());
        loginIdValue.setDeptId(sysUser.getDeptId());
        redisCache.setCacheObject(key, loginIdValue, LOGIN_SOCIAL_EXPIRE_TIME, TimeUnit.SECONDS);
        return loginId;
    }

    @Override
    public String genErrorId(String msg) {
        String errorId = Md5Utils.hash(msg + RandomUtil.randomString(32));
        String key = LOGIN_ERROR_MSG_REDIS_KEY + errorId;
        redisCache.setCacheObject(key, msg, LOGIN_SOCIAL_EXPIRE_TIME, TimeUnit.SECONDS);
        return errorId;
    }

    @Override
    public String genWxBindId(Long userId) {
        String wxBindId = Md5Utils.hash(userId + RandomUtil.randomString(32));
        String key = WX_BIND_REDIS_KEY + wxBindId;
        redisCache.setCacheObject(key, userId, WX_BIND_EXPIRE_TIME, TimeUnit.MINUTES);
        return wxBindId;
    }

    @Override
    public AjaxResult checkSocialUser(SocialUser socialUser, String bindId) {
        if (socialUser == null) {
            log.info("bindId不存在, bindId: {}", bindId);
            return error(MessageUtils.message("bind.account.not.exist"));
        } else {
            return null;
        }
    }

    @Override
    public AjaxResult smsLogin(LoginBody loginBody, String language) {
        String captchaKey = CacheConstants.LOGIN_SMS_CAPTCHA_PHONE + loginBody.getPhonenumber();
        Object captcha = redisCache.getCacheObject(captchaKey);
        if (Objects.isNull(captcha)) {
            return AjaxResult.error(MessageUtils.message("socialLogin.verify.has.expired"));
        }
        if (!captcha.toString().equals(loginBody.getSmsCode())) {
            return AjaxResult.error(MessageUtils.message("captcha.fail"));
        }
        String phoneNumber = loginBody.getPhonenumber();
        SysUser sysUser = iSysUserService.selectUserByPhoneNumber(phoneNumber);
        String token;
        if (sysUser == null) {
            // 直接用手机号注册一个新用户，密码就是手机号
            RegisterUserInput registerUserInput = new RegisterUserInput();
            registerUserInput.setUsername(phoneNumber);
            registerUserInput.setPhonenumber(phoneNumber);
            registerUserInput.setPassword(phoneNumber);
            if (null != loginBody.getSourceType() && 1 == loginBody.getSourceType()) {
                Long roleId;
                if (StringUtils.isNotEmpty(loginBody.getInvitationCode())) {
                    SysDept sysDept = sysDeptService.selectDeptByInvitationCode(loginBody.getInvitationCode());
                    if (Objects.isNull(sysDept)) {
                        throw new ServiceException(MessageUtils.message("socialLogin.register.fail.please.check.invitationCode.exist"));
                    } else {
                        registerUserInput.setDeptId(sysDept.getDeptId());
                        roleId = sysDeptService.selectRegisterRoleByDeptId(sysDept.getDeptId());
                    }
                } else {
                    registerUserInput.setDeptId(100L);
                    roleId = sysDeptService.selectRoleIdByDeptId(100L);
                }
                if (null == roleId) {
                    throw new ServiceException(MessageUtils.message("socialLogin.register.fail.please.check.role.exist"));
                }
                registerUserInput.setRoleIds(new Long[]{roleId});
            }
            RegisterUserOutput registerUserOutput = iSocialUserService.registerNoCaptcha(registerUserInput);
            if (StringUtils.isNotEmpty(registerUserOutput.getMsg())) {
                throw new ServiceException(registerUserOutput.getMsg());
            }
            token = sysLoginService.redirectLogin(phoneNumber, registerUserOutput.getEncryptPassword(), language);
        } else {
            if (null != loginBody.getSourceType() && 1 == loginBody.getSourceType() && null == sysUser.getDeptId()) {
                throw new ServiceException(MessageUtils.message("socialLogin.web.not.allow.end.user.login"));
            }
            token = sysLoginService.redirectLogin(sysUser.getUserName(), sysUser.getPassword(), language);
        }
        redisCache.deleteObject(captchaKey);
        AjaxResult ajax = AjaxResult.success(MessageUtils.message("login.success"), token);
        putLoginToken(ajax, token);
        return ajax;
    }

    @Override
    public AjaxResult ssoLogin(LoginBody loginBody, String language) {
        String token = sysLoginService.ssoLogin(loginBody.getUsername(), loginBody.getPassword(), language);
        AjaxResult ajax = success(MessageUtils.message("login.success"), token);
        putLoginToken(ajax, token);
        return ajax;
    }

    private void putLoginToken(AjaxResult ajax, String token) {
        ajax.put(Constants.TOKEN, token);
        ajax.put("expireTime", sysLoginService.getTokenExpireTime(token));
    }
}
