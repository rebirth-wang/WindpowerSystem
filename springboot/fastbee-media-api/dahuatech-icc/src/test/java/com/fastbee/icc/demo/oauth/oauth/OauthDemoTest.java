package com.fastbee.icc.demo.oauth.oauth;

import com.dahuatech.hutool.json.JSONUtil;
import com.dahuatech.icc.exception.ClientException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * program:java-sdk-demo
 *
 * @Author: 355079
 * @Date:2024-03-08 09:22
 * @Description:鉴权测试类
 */
@Slf4j
@Data
public class OauthDemoTest {

    private OauthDemo oauthDemo;

    public OauthDemoTest() {
        this.oauthDemo = new OauthDemo();
    }

    /**
     * 测试获取公钥
     */
    @Test
    public void testGetPublicKey(){
        oauthDemo.getPublicKey();
    }

    /**
     * 测试原生代码直接获取token信息
     */
    @Test
    public void testGetToken(){
        oauthDemo.getToken();
    }

    /**
     * 测试获取token信息(调用sdk中的方法获取token信息)
     */
    @Test
    public void testGetTokenInfo() throws ClientException {
        System.out.println(JSONUtil.toJsonStr(oauthDemo.getTokenInfo()));;
    }


}
