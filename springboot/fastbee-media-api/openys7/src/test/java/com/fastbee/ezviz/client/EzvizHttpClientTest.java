package com.fastbee.ezviz.client;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fastbee.ezviz.config.EzvizConfig;
import com.fastbee.ezviz.exception.EzvizApiException;
import com.fastbee.ezviz.model.EzvizBaseResponse;
import com.fastbee.framework.core.ut.BaseMockitoUnitTest;

/**
 * EzvizHttpClient 单元测试
 * 测试 HTTP 请求封装、Token 注入、错误码处理
 *
 * @author fastbee
 */
@DisplayName("萤石云 HttpClient 单元测试")
public class EzvizHttpClientTest extends BaseMockitoUnitTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private EzvizTokenHolder tokenHolder;

    private EzvizConfig config;
    private EzvizHttpClient httpClient;

    @BeforeEach
    public void setUp() {
        config = new EzvizConfig();
        config.setApiBase("https://open.ys7.com");
        httpClient = new EzvizHttpClient(config, restTemplate, tokenHolder);
    }

    // ======================== postForm 测试 ========================

    @Test
    @DisplayName("postForm - 成功请求并解析响应")
    public void testPostForm_success() {
        String json = "{\"code\":\"200\",\"msg\":\"操作成功\"}";
        ResponseEntity<String> response = new ResponseEntity<>(json, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(response);

        Map<String, String> params = new HashMap<>();
        params.put("appKey", "testKey");
        params.put("appSecret", "testSecret");

        EzvizBaseResponse result = httpClient.postForm("/api/lapp/token/get", params, EzvizBaseResponse.class);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("200", result.getCode());
    }

    @Test
    @DisplayName("postForm - API 返回错误码，抛出 EzvizApiException")
    public void testPostForm_errorCode_throwsException() {
        String json = "{\"code\":\"10002\",\"msg\":\"appKey非法\"}";
        ResponseEntity<String> response = new ResponseEntity<>(json, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(response);

        EzvizApiException ex = assertThrows(EzvizApiException.class,
                () -> httpClient.postForm("/api/lapp/token/get", null, EzvizBaseResponse.class));
        assertTrue(ex.getMessage().contains("10002") || ex.getMessage().contains("业务失败"));
    }

    @Test
    @DisplayName("postForm - 网络请求抛出异常，包装为 EzvizApiException")
    public void testPostForm_networkException_throwsEzvizApiException() {
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RuntimeException("Connection refused"));

        EzvizApiException ex = assertThrows(EzvizApiException.class,
                () -> httpClient.postForm("/api/lapp/token/get", null, EzvizBaseResponse.class));
        assertTrue(ex.getMessage().contains("HTTP请求失败"));
    }

    @Test
    @DisplayName("postForm - 响应 body 为 null，抛出 EzvizApiException")
    public void testPostForm_nullBody_throwsException() {
        ResponseEntity<String> response = new ResponseEntity<>(null, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(response);

        // null JSON 解析后 result 为 null，checkResponse 应抛出异常
        assertThrows(EzvizApiException.class,
                () -> httpClient.postForm("/api/lapp/token/get", null, EzvizBaseResponse.class));
    }

    // ======================== postWithToken 测试 ========================

    @Test
    @DisplayName("postWithToken - 成功注入 Token 并获取响应")
    public void testPostWithToken_success() {
        when(tokenHolder.getAccessToken()).thenReturn("valid-access-token");

        String json = "{\"code\":\"200\",\"msg\":\"操作成功\"}";
        ResponseEntity<String> response = new ResponseEntity<>(json, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(response);

        Map<String, String> params = new HashMap<>();
        params.put("deviceSerial", "D12345678");

        EzvizBaseResponse result = httpClient.postWithToken("/api/lapp/device/info", params, EzvizBaseResponse.class);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        // 验证 tokenHolder.getAccessToken() 被调用
        verify(tokenHolder, times(1)).getAccessToken();
    }

    @Test
    @DisplayName("postWithToken - Token 获取失败，抛出 EzvizApiException")
    public void testPostWithToken_tokenFailed_throwsException() {
        when(tokenHolder.getAccessToken()).thenThrow(new EzvizApiException("Token获取失败", "10002"));

        EzvizApiException ex = assertThrows(EzvizApiException.class,
                () -> httpClient.postWithToken("/api/lapp/device/info", null, EzvizBaseResponse.class));
        assertTrue(ex.getMessage().contains("Token获取失败") || ex.getMessage().contains("10002"));
    }

    @Test
    @DisplayName("postWithToken - 业务接口返回错误码，抛出 EzvizApiException")
    public void testPostWithToken_businessError_throwsException() {
        when(tokenHolder.getAccessToken()).thenReturn("valid-access-token");

        String json = "{\"code\":\"20006\",\"msg\":\"设备不存在\"}";
        ResponseEntity<String> response = new ResponseEntity<>(json, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(response);

        EzvizApiException ex = assertThrows(EzvizApiException.class,
                () -> httpClient.postWithToken("/api/lapp/device/info", null, EzvizBaseResponse.class));
        assertEquals("20006", ex.getErrorCode());
    }

    @Test
    @DisplayName("postWithToken - 网络异常，包装为 EzvizApiException")
    public void testPostWithToken_networkError_wrapsException() {
        when(tokenHolder.getAccessToken()).thenReturn("valid-access-token");
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RuntimeException("Read timed out"));

        EzvizApiException ex = assertThrows(EzvizApiException.class,
                () -> httpClient.postWithToken("/api/lapp/device/info", null, EzvizBaseResponse.class));
        assertTrue(ex.getMessage().contains("HTTP请求失败"));
    }

    @Test
    @DisplayName("postWithToken - null 参数时，仅携带 accessToken 发送请求")
    public void testPostWithToken_nullParams_onlyAccessToken() {
        when(tokenHolder.getAccessToken()).thenReturn("only-token");

        String json = "{\"code\":\"200\",\"msg\":\"操作成功\"}";
        ResponseEntity<String> response = new ResponseEntity<>(json, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(response);

        EzvizBaseResponse result = httpClient.postWithToken("/api/lapp/live/address/get", null, EzvizBaseResponse.class);
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
}
