package com.fastbee.icc.demo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.dahuatech.hutool.http.Method;
import com.dahuatech.icc.exception.ClientException;
import com.dahuatech.icc.oauth.http.IccTokenResponse;
import com.dahuatech.icc.oauth.model.v202010.GeneralResponse;
import com.dahuatech.icc.oauth.model.v202010.OauthConfigUserPwdInfo;
import com.dahuatech.icc.oauth.utils.HttpUtils;
import org.junit.Test;

import com.fastbee.icc.config.OauthConfigUtil;

public class DemoTest {

    /**
     * post json请求示例代码
     * @throws ClientException
     */
    @Test
    public void testPost() throws ClientException {
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        config.getHttpConfigInfo().setConnectionTimeout(30000l);//设置连接超时
        config.getHttpConfigInfo().setReadTimeout(30000l);//设置读取超时
        Map<String,Object> body = new HashMap<>();
        body.put("pageNum","1");
        body.put("pageSize","10");
        body.put("statusList",new ArrayList<String>(){
            {
                add("1");
            }
        });
        GeneralResponse response = HttpUtils.executeJson("/evo-apigw/evo-brm/1.2.0/person/subsystem/page", body, null, Method.POST, config, GeneralResponse.class);
        System.out.println(response.getResult());
    }

    /**
     * 上传图片测试代码
     * @throws ClientException
     */
    @Test
    public void testHTTPPostFormData() throws ClientException {
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        config.getHttpConfigInfo().setConnectionTimeout(30000l);//设置连接超时
        config.getHttpConfigInfo().setReadTimeout(30000l);//设置读取超时
        Map<String,Object> formdata = new HashMap<>();
        formdata.put("file",new File("真实文件路径"));
        formdata.put("param1","111");
        GeneralResponse response = HttpUtils.executeForm("/evo-apigw/evo-brm/1.2.0/person/upload/img", formdata, null, Method.POST, config, GeneralResponse.class);
        System.out.println(response.getResult());
    }

    /**
     * GET请求测试代码
     * @throws ClientException
     */
    @Test
    public void testGet() throws ClientException {
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        config.getHttpConfigInfo().setConnectionTimeout(30000l);//设置连接超时
        config.getHttpConfigInfo().setReadTimeout(30000l);//设置读取超时
        Map<String,Object> formdata = new HashMap<>();
        formdata.put("paperNumber","TEST01100006");
        GeneralResponse response = HttpUtils.executeForm("/evo-apigw/evo-brm/1.2.0/person/subsystem/get-by-paper-number", formdata, null, Method.GET, config, GeneralResponse.class);
        System.out.println(response.getResult());
    }

    /**
     * 获取token测试代码
     * @throws ClientException
     */
    @Test
    public void getToken() throws ClientException {
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        IccTokenResponse.IccToken token = HttpUtils.getToken(config);
        System.out.println("token=" + token.getAccess_token());
        String authorization = token.getToken_type() + " " +token.getAccess_token();
        System.out.println("业务接口请求时请求头设置Authorization=" + authorization);
    }
}
