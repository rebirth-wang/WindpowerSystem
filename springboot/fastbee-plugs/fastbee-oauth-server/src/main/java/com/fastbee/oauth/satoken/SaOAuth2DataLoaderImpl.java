package com.fastbee.oauth.satoken;

import java.util.Arrays;

import jakarta.annotation.Resource;

import cn.dev33.satoken.oauth2.data.loader.SaOAuth2DataLoader;
import cn.dev33.satoken.oauth2.data.model.loader.SaClientModel;
import org.springframework.stereotype.Component;

import com.fastbee.oauth.domain.OauthClientDetails;
import com.fastbee.oauth.service.IOauthClientDetailsService;

/**
 * Sa-Token OAuth2：自定义数据加载器
 */
@Component
public class SaOAuth2DataLoaderImpl implements SaOAuth2DataLoader {

    // 根据 clientId 获取 Client 信息
    @Resource
    IOauthClientDetailsService oauthClientDetailsService;

    @Override
    public SaClientModel getClientModel(String clientId) {
        OauthClientDetails oauthClientDetails = oauthClientDetailsService.validOAuthClientFromCache(clientId);
        if (oauthClientDetails != null) {
            String[] scopes = split(oauthClientDetails.getScope());
            String[] grantTypes = split(oauthClientDetails.getAuthorizedGrantTypes());
            SaClientModel clientModel = new SaClientModel().setClientId(clientId)    // client id
                    .setClientSecret(oauthClientDetails.getClientSecret())    // client 秘钥
                    .addContractScopes(scopes)    // 所有签约的权限
                    .addAllowGrantTypes(grantTypes);     // 所有允许的授权模式
            clientModel.addAllowRedirectUris(split(oauthClientDetails.getWebServerRedirectUri()));
            if (oauthClientDetails.getAccessTokenValidity() != null) {
                clientModel.setAccessTokenTimeout(oauthClientDetails.getAccessTokenValidity());
            }
            if (oauthClientDetails.getRefreshTokenValidity() != null) {
                clientModel.setRefreshTokenTimeout(oauthClientDetails.getRefreshTokenValidity());
            }
            clientModel.setIsAutoConfirm(Boolean.parseBoolean(oauthClientDetails.getAutoapprove()));
            return clientModel;
        }
        return null;
    }

    private String[] split(String value) {
        if (value == null || value.isBlank()) {
            return new String[0];
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(item -> !item.isEmpty())
                .toArray(String[]::new);
    }

    // 根据 clientId 和 loginId 获取 openid
    @Override
    public String getOpenid(String clientId, Object loginId) {
        // 此处使用框架默认算法生成 openid，真实环境建议改为从数据库查询
        return SaOAuth2DataLoader.super.getOpenid(clientId, loginId);
    }
}
