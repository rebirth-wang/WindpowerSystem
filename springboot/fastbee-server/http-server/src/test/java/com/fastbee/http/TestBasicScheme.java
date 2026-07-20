package com.fastbee.http;

import java.util.List;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.*;
import org.junit.Before;

@Slf4j
public class TestBasicScheme {

    private HttpHost target;
    private CloseableHttpClient httpclient;
    private CredentialsProvider credsProvider;
    private BasicCookieStore cookieStore;

    @Before
    public void setUp() {
        this.target = new HttpHost("localhost", 8081,"http");
        // 设置认证账号密码
        this.credsProvider = new BasicCredentialsProvider();
        this.credsProvider.setCredentials(
                new AuthScope(this.target.getHostName(), this.target.getPort()),
                new UsernamePasswordCredentials("S-D1M267SH21TM-145-1:FastBee", "PB82DDUP88P8GRPI"));
        // 存储cookie
        this.cookieStore = new BasicCookieStore();
        this.httpclient = HttpClients.custom()
                .setDefaultCredentialsProvider(this.credsProvider)
                .setDefaultCookieStore(this.cookieStore)
                .build();
    }

    //@Test
    public void testRequest() throws Exception {
        try {
            // Create AuthCache instance
            AuthCache authCache = new BasicAuthCache();
            // Generate BASIC scheme object and add it to the local
            // auth cache
            BasicScheme basicAuth = new BasicScheme();
            authCache.put(target, basicAuth);

            // Add AuthCache to the execution context
            HttpClientContext localContext = HttpClientContext.create();
            localContext.setAuthCache(authCache);
            HttpPost httppost = new HttpPost("http://localhost:8081");
            log.info("Executing request " + httppost.getRequestLine() + " to target " + target);
            CloseableHttpResponse response = this.httpclient.execute(target, httppost, localContext);
            Header wwwAuthenticateHeader = response.getFirstHeader("WWW-Authenticate");
            if (wwwAuthenticateHeader != null) {
                String wwwAuthenticate = wwwAuthenticateHeader.getValue();
                try {
                    log.info("request WWW-Authenticate: {}", wwwAuthenticate);
                    log.info("request ----------------------------------------");
                    log.info("{}", response.getStatusLine());
                } finally {
                    response.close();
                }
            }
            log.info("Post cookies:");
            List<Cookie> cookies = cookieStore.getCookies();
            if (cookies.isEmpty()) {
                log.info("None cookies");
            } else {
                for (Cookie cookie : cookies) {
                    log.info("- " + cookie.toString());
                }
            }
            log.info("带cookie请求");
            HttpPost infoPost = new HttpPost("http://localhost:8081/info/post");

            JSONObject json = new JSONObject();
            json.put("productId", 41);
            json.put("serialNumber", "D1ELV3A5TOJS");

            // 设置请求体为 JSON 字符串，并设置 Content-Type 为 application/json
            StringEntity entity = new StringEntity(json.toString(), "UTF-8");
            entity.setContentType("application/json");
            entity.setContentEncoding("UTF-8");
            infoPost.setEntity(entity);

            response = this.httpclient.execute(target, infoPost, localContext);
            try {
                log.info("request ----------------------------------------");
                log.info("{}", response.getStatusLine());
            } finally {
                response.close();
            }

            HttpPost propertyPost = new HttpPost("http://localhost:8081/property/post");
            // 设置请求体为 JSON 字符串，并设置 Content-Type 为 application/json
            StringEntity propertyentity = new StringEntity("[{\n" +
                    "    \"id\": \"temperature\",\n" +
                    "    \"value\": 17,\n" +
                    "    \"remark\": \"test\"\n" +
                    "}]", "UTF-8");
            propertyentity.setContentType("application/json");
            propertyentity.setContentEncoding("UTF-8");
            propertyPost.setEntity(propertyentity);

            response = this.httpclient.execute(target, propertyPost, localContext);
            try {
                log.info("request ----------------------------------------");
                log.info("{}", response.getStatusLine());
            } finally {
                response.close();
            }

//                HttpPost eventPost = new HttpPost("http://localhost:8081/event/post");
//                // 设置请求体为 JSON 字符串，并设置 Content-Type 为 application/json
//                StringEntity evententity = new StringEntity("[{\n" +
//                        "    \"id\": \"alarm\",\n" +
//                        "    \"value\": 17,\n" +
//                        "    \"remark\": \"alarm\"\n" +
//                        "}]", "UTF-8");
//                evententity.setContentType("application/json");
//                evententity.setContentEncoding("UTF-8");
//                propertyPost.setEntity(evententity);
//
//                response = this.httpclient.execute(target, eventPost, localContext);
//                try {
//                    log.info("request ----------------------------------------");
//                    log.info("{}", response.getStatusLine());
//                } finally {
//                    response.close();
//                }
        } finally {
            this.httpclient.close();
        }
    }
}
