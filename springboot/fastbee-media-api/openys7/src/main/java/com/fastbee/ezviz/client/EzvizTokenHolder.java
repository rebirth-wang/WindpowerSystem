package com.fastbee.ezviz.client;

import java.util.concurrent.locks.ReentrantLock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fastbee.ezviz.config.EzvizConfig;
import com.fastbee.ezviz.exception.EzvizApiException;

/**
 * 萤石云 AccessToken 持有者
 * <p>负责 AccessToken 的获取、缓存与自动刷新，线程安全</p>
 *
 * @author fastbee
 */
@Slf4j
public class EzvizTokenHolder {

    /** 获取 AccessToken 接口路径 */
    private static final String TOKEN_URL = "/api/lapp/token/get";

    private final EzvizConfig config;
    private final RestTemplate restTemplate;
    private final ReentrantLock lock = new ReentrantLock();

    /** 缓存的 AccessToken */
    private volatile String cachedToken;
    /** Token 过期时间（毫秒时间戳） */
    private volatile long expireAt = 0L;

    public EzvizTokenHolder(EzvizConfig config, RestTemplate restTemplate) {
        this.config = config;
        this.restTemplate = restTemplate;
    }

    /**
     * 获取有效的 AccessToken，必要时自动刷新
     */
    public String getAccessToken() {
        if (isTokenValid()) {
            return cachedToken;
        }
        lock.lock();
        try {
            // 双重检查
            if (isTokenValid()) {
                return cachedToken;
            }
            return refreshToken();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 强制刷新 AccessToken
     */
    public String refreshToken() {
        log.info("[萤石云] 正在获取/刷新 AccessToken...");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("appKey", config.getAppKey());
        form.add("appSecret", config.getAppSecret());

        String url = config.getApiBase() + TOKEN_URL;
        try {
            String json = restTemplate.postForObject(url, new HttpEntity<>(form, headers), String.class);
            com.alibaba.fastjson2.JSONObject resp = com.alibaba.fastjson2.JSON.parseObject(json);
            String code = resp.getString("code");
            if (!"200".equals(code)) {
                throw new EzvizApiException("获取萤石云AccessToken失败: code=" + code + " msg=" + resp.getString("msg"), code);
            }
            com.alibaba.fastjson2.JSONObject data = resp.getJSONObject("data");
            this.cachedToken = data.getString("accessToken");
            Long expireTime = data.getLong("expireTime");
            if (expireTime != null && expireTime > 0) {
                // expireTime 是服务端返回的过期时间戳（毫秒），提前5分钟刷新
                this.expireAt = expireTime - 5 * 60 * 1000L;
            } else {
                // 默认缓存7天
                this.expireAt = System.currentTimeMillis() + config.getTokenExpireSeconds() * 1000L - 5 * 60 * 1000L;
            }
            log.info("[萤石云] AccessToken 已更新，过期时间戳: {}", this.expireAt);
            return this.cachedToken;
        } catch (EzvizApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("[萤石云] 获取 AccessToken 异常", e);
            throw new EzvizApiException("获取萤石云AccessToken网络异常: " + e.getMessage(), e);
        }
    }

    private boolean isTokenValid() {
        return cachedToken != null && System.currentTimeMillis() < expireAt;
    }

}
