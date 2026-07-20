package com.fastbee.ezviz.client;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import com.fastbee.ezviz.config.EzvizConfig;
import com.fastbee.ezviz.exception.EzvizApiException;
import com.fastbee.framework.core.ut.BaseMockitoUnitTest;

/**
 * EzvizTokenHolder 单元测试
 * 测试 AccessToken 获取、缓存、过期刷新及线程安全性
 *
 * @author fastbee
 */
@DisplayName("萤石云 TokenHolder 单元测试")
public class EzvizTokenHolderTest extends BaseMockitoUnitTest {

    @Mock
    private RestTemplate restTemplate;

    private EzvizConfig config;
    private EzvizTokenHolder tokenHolder;

    @BeforeEach
    public void setUp() {
        config = new EzvizConfig();
        config.setAppKey("testAppKey");
        config.setAppSecret("testAppSecret");
        config.setApiBase("https://open.ys7.com");
        config.setTokenExpireSeconds(604800L);
        tokenHolder = new EzvizTokenHolder(config, restTemplate);
    }

    // ======================== 正向测试用例 ========================

    @Test
    @DisplayName("首次获取 Token - 成功返回并缓存")
    public void testGetAccessToken_firstTime_success() {
        // Mock HTTP 返回值
        mockTokenResponseSuccess("test-access-token-001", System.currentTimeMillis() + 7 * 24 * 3600 * 1000L);

        String token = tokenHolder.getAccessToken();

        assertNotNull(token);
        assertEquals("test-access-token-001", token);
        // 验证只调用了一次 HTTP 请求
        verify(restTemplate, times(1)).postForObject(anyString(), any(HttpEntity.class), eq(String.class));
    }

    @Test
    @DisplayName("Token 在有效期内 - 直接返回缓存，不重新请求")
    public void testGetAccessToken_cached_noNewRequest() {
        mockTokenResponseSuccess("cached-token", System.currentTimeMillis() + 7 * 24 * 3600 * 1000L);

        // 第一次获取，触发 HTTP 请求
        String token1 = tokenHolder.getAccessToken();
        // 第二次获取，应走缓存
        String token2 = tokenHolder.getAccessToken();
        String token3 = tokenHolder.getAccessToken();

        assertEquals(token1, token2);
        assertEquals(token1, token3);
        // 验证 HTTP 请求只调用一次
        verify(restTemplate, times(1)).postForObject(anyString(), any(HttpEntity.class), eq(String.class));
    }

    @Test
    @DisplayName("强制刷新 Token - 返回新 Token")
    public void testRefreshToken_success() {
        mockTokenResponseSuccess("refreshed-token", System.currentTimeMillis() + 7 * 24 * 3600 * 1000L);

        String token = tokenHolder.refreshToken();

        assertNotNull(token);
        assertEquals("refreshed-token", token);
        verify(restTemplate, times(1)).postForObject(anyString(), any(HttpEntity.class), eq(String.class));
    }

    @Test
    @DisplayName("Token 过期后 - 重新请求获取新 Token")
    public void testGetAccessToken_expired_refreshed() {
        // 第一次返回很短过期时间（已过期：expireTime = 当前时间，提前5分钟后必然已过期）
        long expiredTime = System.currentTimeMillis() - 1000L; // 过去时间
        mockTokenResponseSuccess("old-token", expiredTime);

        // 先刷新一次，拿到 "old-token"（但 expireAt 设为过去时间，下次必定过期）
        tokenHolder.refreshToken();

        // 更换 mock 返回值
        mockTokenResponseSuccess("new-token-after-expire", System.currentTimeMillis() + 7 * 24 * 3600 * 1000L);

        // 因为 Token 已过期，应重新获取
        String token = tokenHolder.getAccessToken();

        assertEquals("new-token-after-expire", token);
        verify(restTemplate, times(2)).postForObject(anyString(), any(HttpEntity.class), eq(String.class));
    }

    @Test
    @DisplayName("多线程并发获取 Token - 线程安全，HTTP 仅请求一次")
    public void testGetAccessToken_concurrentAccess_threadSafe() throws InterruptedException {
        mockTokenResponseSuccess("concurrent-token", System.currentTimeMillis() + 7 * 24 * 3600 * 1000L);

        int threadCount = 10;
        Thread[] threads = new Thread[threadCount];
        String[] results = new String[threadCount];
        Exception[] exceptions = new Exception[threadCount];

        for (int i = 0; i < threadCount; i++) {
            final int idx = i;
            threads[i] = new Thread(() -> {
                try {
                    results[idx] = tokenHolder.getAccessToken();
                } catch (Exception e) {
                    exceptions[idx] = e;
                }
            });
        }

        for (Thread t : threads) {
            t.start();
        }
        for (Thread t : threads) {
            t.join();
        }

        // 验证所有线程均获取到相同的 Token，无异常
        for (int i = 0; i < threadCount; i++) {
            assertNull(exceptions[i], "线程 " + i + " 抛出了异常");
            assertEquals("concurrent-token", results[i]);
        }
        // 由于双重检查锁，HTTP 请求最多只发出一次
        verify(restTemplate, times(1)).postForObject(anyString(), any(HttpEntity.class), eq(String.class));
    }

    // ======================== 负向测试用例 ========================

    @Test
    @DisplayName("API 返回错误码 - 抛出 EzvizApiException")
    public void testGetAccessToken_apiErrorCode_throwsException() {
        String errorJson = "{\"code\":\"10002\",\"msg\":\"appKey非法\",\"data\":null}";
        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(errorJson);

        EzvizApiException ex = assertThrows(EzvizApiException.class, () -> tokenHolder.getAccessToken());
        assertTrue(ex.getMessage().contains("10002") || ex.getMessage().contains("AccessToken"));
    }

    @Test
    @DisplayName("网络请求异常 - 抛出 EzvizApiException")
    public void testGetAccessToken_networkError_throwsException() {
        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RuntimeException("连接超时"));

        EzvizApiException ex = assertThrows(EzvizApiException.class, () -> tokenHolder.getAccessToken());
        assertTrue(ex.getMessage().contains("网络异常") || ex.getMessage().contains("连接超时"));
    }

    @Test
    @DisplayName("API 返回 null 响应 - 抛出异常")
    public void testGetAccessToken_nullResponse_throwsException() {
        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(null);

        assertThrows(Exception.class, () -> tokenHolder.getAccessToken());
    }

    // ======================== 工具方法 ========================

    /**
     * Mock 成功的 Token 响应
     *
     * @param accessToken 令牌值
     * @param expireTime  过期时间戳（毫秒）
     */
    private void mockTokenResponseSuccess(String accessToken, long expireTime) {
        String json = String.format(
                "{\"code\":\"200\",\"msg\":\"操作成功\",\"data\":{\"accessToken\":\"%s\",\"expireTime\":%d}}",
                accessToken, expireTime);
        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(json);
    }
}
