package com.fastbee.icc.client;

import com.alibaba.fastjson.JSON;
import com.dahuatech.hutool.http.Method;
import com.dahuatech.icc.exception.ClientException;
import com.dahuatech.icc.oauth.http.IccResponse;
import com.dahuatech.icc.oauth.model.v202010.OauthConfigUserPwdInfo;
import com.dahuatech.icc.oauth.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;

import com.fastbee.icc.config.OauthConfigUtil;
import com.fastbee.icc.config.PlatformConfig;
import com.fastbee.icc.exception.IccApiException;

/**
 * 大华ICC平台 HTTP 客户端封装
 *
 * <p>基于大华ICC SDK HttpUtils封装，统一处理：
 * <ul>
 *   <li>Token失效自动重试（一次）</li>
 *   <li>异常统一转换为 {@link IccApiException}</li>
 * </ul>
 * </p>
 *
 * @author fastbee
 */
@Slf4j
public class IccHttpClient {

    /** Token失效错误码（需重新鉴权） */
    private static final String TOKEN_EXPIRED_CODE = "401";

    private final PlatformConfig config;
    private final IccTokenHolder tokenHolder;

    public IccHttpClient(PlatformConfig config, IccTokenHolder tokenHolder) {
        this.config = config;
        this.tokenHolder = tokenHolder;
    }

    /**
     * 发送POST请求（JSON格式），自动处理Token失效重试
     *
     * @param apiPath  接口路径，例如 /evo-apigw/admin/API/MTS/Video/StartVideo
     * @param request  请求对象（将被序列化为JSON）
     * @param clazz    响应类型（必须是 IccResponse 的子类）
     * @param <T>      响应类型泛型
     * @return 响应对象
     */
    public <T extends IccResponse> T post(String apiPath, Object request, Class<T> clazz) {
        return executeWithRetry(apiPath, request, clazz, false);
    }

    /**
     * 内部执行，支持Token失效一次重试
     */
    private <T extends IccResponse> T executeWithRetry(String apiPath, Object request, Class<T> clazz, boolean isRetry) {
        OauthConfigUserPwdInfo oauthConfig = OauthConfigUtil.buildOauthConfig(config);
        try {
            log.debug("[大华ICC] POST path={}, request={}", apiPath, JSON.toJSONString(request));
            T response = HttpUtils.executeJson(apiPath, request, null, Method.POST, oauthConfig, clazz);
            log.debug("[大华ICC] 响应: {}", JSON.toJSONString(response));
            return response;
        } catch (ClientException e) {
            String errCode = e.getErrCode() != null ? e.getErrCode() : "";
            // Token失效时尝试刷新后重试一次
            if (!isRetry && (errCode.contains(TOKEN_EXPIRED_CODE) || errCode.contains("1001"))) {
                log.warn("[大华ICC] Token失效，尝试刷新Token后重试, path={}", apiPath);
                tokenHolder.invalidateToken();
                return executeWithRetry(apiPath, request, clazz, true);
            }
            log.error("[大华ICC] 请求失败 path={}, errCode={}, errMsg={}", apiPath, errCode, e.getErrMsg());
            throw new IccApiException("[大华ICC] 请求失败: " + e.getErrMsg(), errCode, e);
        }
    }

    /**
     * 获取当前有效的Oauth配置
     *
     * @return Oauth配置信息（含AccessToken）
     */
    public OauthConfigUserPwdInfo getOauthConfig() {
        return OauthConfigUtil.buildOauthConfig(config);
    }

    /**
     * 获取平台基础URL前缀（协议://ip:port）
     */
    public String getPlatformBaseUrl() {
        return "https://" + config.getHost() + ":" + config.getHttpsPort();
    }
}
