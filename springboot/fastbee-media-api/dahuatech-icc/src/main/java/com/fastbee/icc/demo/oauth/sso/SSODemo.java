package com.fastbee.icc.demo.oauth.sso;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.dahuatech.icc.exception.ClientException;
import com.dahuatech.icc.oauth.http.IccTokenResponse;
import com.dahuatech.icc.oauth.model.v202010.OauthConfigUserPwdInfo;
import com.dahuatech.icc.oauth.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;

import com.fastbee.icc.config.OauthConfigUtil;

/**
 * @className SSOLoginDemo
 * @Author 355079
 * @Date 2025/9/17
 * @Description 单点登录
 */
@Slf4j
public class SSODemo {

    /**
     * 获取平台token方式嵌入的HTTPS单点登录地址
     * @param redirectUrl 需嵌入的icc平台页面url
     * @return HTTPS单点登录地址
     */
    public String getHttpsSSOUrlByIccToken(String redirectUrl) {
        String ssoLoginUrl = null;
        try {
            //获取oauth配置信息
            OauthConfigUserPwdInfo oauthConfig = OauthConfigUtil.getOauthConfig();
            //client_id和client_secret需设置为固定值web_client,非申请的访问凭证
            oauthConfig.setClientId("web_client");
            oauthConfig.setClientSecret("web_client");
            IccTokenResponse.IccToken tokenInfo =HttpUtils.getToken(oauthConfig);
            String accessToken = tokenInfo.getAccess_token();
            String refreshToken = tokenInfo.getRefresh_token();
            ssoLoginUrl = "https://"+oauthConfig.getHttpConfigInfo().getHost()+":"+oauthConfig.getHttpConfigInfo().getHttpsPort()+"/#/thirdAccess?accessToken="+accessToken+"&refreshToken="+refreshToken+"&redirect="+ URLEncoder.encode(redirectUrl,"UTF-8");
            log.info("SSODemo,getHttpsSSOUrlByIccToken,ssoLoginUrl：{}",ssoLoginUrl);
        } catch (ClientException | UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        }
        return ssoLoginUrl;
    }

    /**
     * 获取平台token方式嵌入的HTTP单点登录地址
     * @param redirectUrl 需嵌入的icc平台页面url
     * @return HTTP单点登录地址
     */
    public String getHttpSSOUrlByIccToken(String redirectUrl) {
        String ssoLoginUrl = null;
        try {
            //获取oauth配置信息
            OauthConfigUserPwdInfo oauthConfig = OauthConfigUtil.getOauthConfig();
            //client_id和client_secret需设置为固定值web_client,非申请的访问凭证
            oauthConfig.setClientId("web_client");
            oauthConfig.setClientSecret("web_client");
            IccTokenResponse.IccToken tokenInfo =HttpUtils.getToken(oauthConfig);
            String accessToken = tokenInfo.getAccess_token();
            String refreshToken = tokenInfo.getRefresh_token();
            ssoLoginUrl = "http://"+oauthConfig.getHttpConfigInfo().getHost()+":"+oauthConfig.getHttpConfigInfo().getHttpPort()+"/#/thirdAccess?accessToken="+accessToken+"&refreshToken="+refreshToken+"&redirect="+ URLEncoder.encode(redirectUrl,"UTF-8");
            log.info("SSODemo,getHttpSSOUrlByIccToken,ssoLoginUrl：{}",ssoLoginUrl);
        } catch (ClientException | UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        }
        return ssoLoginUrl;
    }

    /**
     * 获取第三方token方式嵌入的HTTPS单点登录地址
     * @param redirectUrl 需嵌入的icc平台页面url
     * @return HTTPS单点登录地址
     */
    public String getHttpsSSOUrlByThirdPartyToken(String username, String password, String redirectUrl) {
        String ssoLoginUrl = null;
        try {
            //获取oauth配置信息
            OauthConfigUserPwdInfo oauthConfig = OauthConfigUtil.getOauthConfig();
            ssoLoginUrl = "https://"+oauthConfig.getHttpConfigInfo().getHost()+":"+oauthConfig.getHttpConfigInfo().getHttpsPort()+"/#/thirdOAuth?username="+username+"&password="+password+"&redirect="+ URLEncoder.encode(redirectUrl,"UTF-8");
            log.info("SSODemo,getHttpsSSOUrlByThirdPartyToken,ssoLoginUrl：{}",ssoLoginUrl);
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        }
        return ssoLoginUrl;
    }

    /**
     * 获取第三方token方式嵌入的HTTP单点登录地址
     * @param redirectUrl 需嵌入的icc平台页面url
     * @return HTTP单点登录地址
     */
    public String getHttpSSOUrlByThirdPartyToken(String username, String password, String redirectUrl) {
        String ssoLoginUrl = null;
        try {
            //获取oauth配置信息
            OauthConfigUserPwdInfo oauthConfig = OauthConfigUtil.getOauthConfig();
            ssoLoginUrl = "http://"+oauthConfig.getHttpConfigInfo().getHost()+":"+oauthConfig.getHttpConfigInfo().getHttpPort()+"/#/thirdOAuth?username="+username+"&password="+password+"&redirect="+ URLEncoder.encode(redirectUrl,"UTF-8");
            log.info("SSODemo,getHttpSSOUrlByThirdPartyToken,ssoLoginUrl：{}",ssoLoginUrl);
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        }
        return ssoLoginUrl;
    }

}
