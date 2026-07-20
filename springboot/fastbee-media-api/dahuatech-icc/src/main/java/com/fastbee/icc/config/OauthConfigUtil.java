package com.fastbee.icc.config;

import com.dahuatech.icc.oauth.model.v202010.OauthConfigUserPwdInfo;

/**
 * ICC Oauth配置工具类
 *
 * @author fastbee
 */
public class OauthConfigUtil {

    /**
     * 根据 PlatformConfig 构建 Oauth配置信息
     *
     * @param config 平台配置
     * @return Oauth配置信息
     */
    public static OauthConfigUserPwdInfo buildOauthConfig(PlatformConfig config) {
        OauthConfigUserPwdInfo oauthConfigUserPwdInfo = new OauthConfigUserPwdInfo(
                config.getHost(),
                config.getClientId(),
                config.getClientSecret(),
                config.getUsername(),
                config.getPassword(),
                false,
                config.getHttpsPort(),
                config.getHttpPort()
        );
        oauthConfigUserPwdInfo.getHttpConfigInfo().setReadTimeout(config.getReadTimeout());
        oauthConfigUserPwdInfo.getHttpConfigInfo().setConnectionTimeout(config.getConnectionTimeout());
        return oauthConfigUserPwdInfo;
    }

    /**
     * @deprecated 使用 {@link #buildOauthConfig(PlatformConfig)} 替代，此方法依赖硬编码配置
     */
    @Deprecated
    public static OauthConfigUserPwdInfo getOauthConfig() {
        PlatformConfig platformConfig = new PlatformConfig();
        return buildOauthConfig(platformConfig);
    }
}
