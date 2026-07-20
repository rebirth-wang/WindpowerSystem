package com.fastbee.icc.demo.oauth.oauth;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.dahuatech.icc.exception.ClientException;
import com.dahuatech.icc.oauth.http.IccTokenResponse;
import com.dahuatech.icc.oauth.utils.HttpUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fastbee.icc.config.OauthConfigUtil;
import com.fastbee.icc.config.PlatformConfig;
import com.fastbee.icc.model.oauth.GetTokenRequest;
import com.fastbee.icc.util.RSAUtil;
import com.fastbee.icc.util.RestTemplateUtil;

/**
 * program:java-sdk-demo
 *
 * @Author: 355079
 * @Date:2024-03-07 13:57
 * @Description: 鉴权
 */
@Slf4j
@Data
public class OauthDemo {

    /**
     * 获取公钥(用于调认证接口时对密码进行RSA加密)
     * 原生代码调用，不使用sdk
     *
     * @return 公钥
     * @throws Exception
     */
    public String getPublicKey() {
        PlatformConfig platformConfig = new PlatformConfig();
        String host = platformConfig.getHost();
        String port = platformConfig.getHttpsPort();
        String url = "https://" + host + ":" + port;
        url += "/evo-apigw/evo-oauth/1.0.0/oauth/public-key";
        log.info("OauthDemo,getPublicKey,url:{}", url);
        RestTemplate restTemplate = RestTemplateUtil.getRestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        log.info("OauthDemo,getPublicKey,response:{}", response.getBody());
        Map data = (Map) com.alibaba.fastjson.JSON.parseObject(response.getBody(), new TypeReference<Map<String, Object>>() {
        }).get("data");
        String publicKey = data.get("publicKey").toString();
        log.info("OauthDemo,getPublicKey,publicKey:{}", publicKey);
        return publicKey;
    }

    /**
     * 认证(获取token)
     * 原生代码调用，不使用sdk
     *
     * @return
     */
    public String getToken() {
        PlatformConfig platformConfig = new PlatformConfig();
        String host = platformConfig.getHost();
        String port = platformConfig.getHttpsPort();
        String username = platformConfig.getUsername();
        String password = platformConfig.getPassword();
        String clientId = platformConfig.getClientId();
        String clientSecret = platformConfig.getClientSecret();
        String url, token = null;
        try {
            url = "https://" + host + ":" + port;
            url += "/evo-apigw/evo-oauth/1.0.0/oauth/extend/token";
            GetTokenRequest getTokenRequest = new GetTokenRequest();
            String publicKey = getPublicKey();
            getTokenRequest.setGrant_type("password");
            getTokenRequest.setUsername(username);
            getTokenRequest.setPassword(RSAUtil.encrypt(password, publicKey));
            getTokenRequest.setClient_id(clientId);
            getTokenRequest.setClient_secret(clientSecret);
            getTokenRequest.setPublic_key(publicKey);
            log.info("OauthDemo,getToken,url:{}", url);
            log.info("OauthDemo,getToken,requestBody:{}", JSON.toJSONString(getTokenRequest));
            RestTemplate restTemplate = RestTemplateUtil.getRestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(url, getTokenRequest, String.class);
            log.info("OauthDemo,getToken,response:{}", response.getBody());
            Map data = (Map) com.alibaba.fastjson.JSON.parseObject(response.getBody(), new TypeReference<Map<String, Object>>() {
            }).get("data");
            token = data.get("access_token").toString();
            //String tokenType = data.get("token_type").toString();
            //token = tokenType + ' ' + token;
            //log.info("OauthDemo,getToken,token:{}", token);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return token;
    }


    /**
     * 获取token信息(调用sdk中的方法获取token信息)
     * @return token信息
     * @throws ClientException 客户端异常
     */
    public IccTokenResponse.IccToken getTokenInfo() throws ClientException {
        return HttpUtils.getToken(OauthConfigUtil.getOauthConfig());
    }

}
