package com.fastbee.icc.client;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.dahuatech.icc.exception.ClientException;
import com.dahuatech.icc.oauth.http.IccTokenResponse;
import com.dahuatech.icc.oauth.model.v202010.OauthConfigUserPwdInfo;
import com.dahuatech.icc.oauth.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;

import com.fastbee.icc.config.OauthConfigUtil;
import com.fastbee.icc.config.PlatformConfig;
import com.fastbee.icc.exception.IccApiException;

/**
 * 大华ICC平台 AccessToken 持有与自动刷新
 *
 * <p>线程安全，使用读写锁保护Token状态，自动在过期前刷新Token</p>
 *
 * @author fastbee
 */
@Slf4j
public class IccTokenHolder {

    private final PlatformConfig config;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    /** 当前AccessToken */
    private volatile String accessToken;
    /** Token过期时间戳（毫秒） */
    private volatile long expireAtMs = 0L;

    public IccTokenHolder(PlatformConfig config) {
        this.config = config;
    }

    /**
     * 获取有效的AccessToken，必要时自动刷新
     *
     * @return 有效的AccessToken
     */
    public String getAccessToken() {
        // 先尝试读锁快速路径
        lock.readLock().lock();
        try {
            if (isTokenValid()) {
                return accessToken;
            }
        } finally {
            lock.readLock().unlock();
        }

        // Token无效，加写锁刷新
        lock.writeLock().lock();
        try {
            // 双重检查，防止多线程重复刷新
            if (isTokenValid()) {
                return accessToken;
            }
            refreshToken();
            return accessToken;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 强制刷新Token（如API调用返回Token失效错误时调用）
     */
    public void invalidateToken() {
        lock.writeLock().lock();
        try {
            log.info("[大华ICC] 强制使Token失效，下次请求将重新获取");
            this.expireAtMs = 0L;
            this.accessToken = null;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 检查当前Token是否有效（未过期且不为空）
     * 预留30秒提前刷新缓冲
     */
    private boolean isTokenValid() {
        return accessToken != null
                && !accessToken.isEmpty()
                && System.currentTimeMillis() < (expireAtMs - 30_000L);
    }

    /**
     * 向ICC平台请求新Token
     */
    private void refreshToken() {
        log.info("[大华ICC] 正在获取新的AccessToken，host={}", config.getHost());
        try {
            OauthConfigUserPwdInfo oauthConfig = OauthConfigUtil.buildOauthConfig(config);
            IccTokenResponse.IccToken tokenInfo = HttpUtils.getToken(oauthConfig);
            if (tokenInfo == null || tokenInfo.getAccess_token() == null) {
                throw new IccApiException("[大华ICC] 获取Token失败：响应为空");
            }
            this.accessToken = tokenInfo.getAccess_token();
            // 根据配置的tokenExpireSeconds计算过期时间
            long expireSeconds = config.getTokenExpireSeconds();
            this.expireAtMs = System.currentTimeMillis() + expireSeconds * 1000L;
            log.info("[大华ICC] AccessToken获取成功，有效期{}秒", expireSeconds);
        } catch (ClientException e) {
            log.error("[大华ICC] 获取AccessToken失败: {}", e.getErrMsg(), e);
            throw new IccApiException("[大华ICC] 获取AccessToken失败: " + e.getErrMsg(), e);
        }
    }
}
