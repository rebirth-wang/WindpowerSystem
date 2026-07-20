package com.fastbee.icc.demo.oauth.sso;

import org.junit.Test;

/**
 * @className SSODemoTest
 * @Author 355079
 * @Date 2025/9/17
 * @Description 单点登录测试类
 */
public class SSODemoTest {
    /**
     * 测试获取平台token方式嵌入的HTTPS单点登录地址
     * @return HTTPS单点登录地址
     */
    @Test
    public void testGetHttpsSSOUrlByIccToken() {
        SSODemo ssoDemo = new SSODemo();
        String redirectUrl = "https://124.160.33.135:4077/#/home";
        ssoDemo.getHttpsSSOUrlByIccToken(redirectUrl);
    }

    /**
     * 测试获取平台token方式嵌入的HTTP单点登录地址
     * @return HTTP单点登录地址
     */
    @Test
    public void testGetHttpSSOUrlByIccToken() {
        SSODemo ssoDemo = new SSODemo();
        String redirectUrl = "http://124.160.33.135:4078/#/home";
        ssoDemo.getHttpSSOUrlByIccToken(redirectUrl);
    }

    /**
     * 测试获取第三方token方式嵌入的HTTPS单点登录地址
     * @return HTTPS单点登录地址
     */
    @Test
    public void testGetHttpsSSOUrlByThirdPartyToken() {
        SSODemo ssoDemo = new SSODemo();
        String redirectUrl = "https://124.160.33.135:4077/#/home";
        ssoDemo.getHttpsSSOUrlByThirdPartyToken("admin","admin123",redirectUrl);
    }

    /**
     * 测试获取第三方token方式嵌入的HTTP单点登录地址
     * @return HTTPS单点登录地址
     */
    @Test
    public void testGetHttpSSOUrlByThirdPartyToken() {
        SSODemo ssoDemo = new SSODemo();
        String redirectUrl = "http://124.160.33.135:4078/#/home";
        ssoDemo.getHttpSSOUrlByThirdPartyToken("admin","admin123",redirectUrl);
    }

}
