package com.fastbee.oauth.service.impl;

import java.time.LocalDateTime;

import jakarta.annotation.Resource;

import cn.hutool.core.util.IdUtil;
import org.springframework.stereotype.Service;

import com.fastbee.oauth.domain.OauthAccessToken;
import com.fastbee.oauth.domain.OauthClientDetails;
import com.fastbee.oauth.domain.OauthCode;
import com.fastbee.oauth.mapper.OauthAccessTokenMapper;
import com.fastbee.oauth.service.IOauthClientDetailsService;
import com.fastbee.oauth.service.IOauthCodeService;
import com.fastbee.oauth.service.OauthAccessTokenService;

/**
 * @author fastb
 * @date 2023-09-01 17:20
 */
@Service
public class OauthAccessTokenServiceImpl implements OauthAccessTokenService {

    @Resource
    private OauthAccessTokenMapper oauthAccessTokenMapper;
    @Resource
    private IOauthCodeService oauthCodeService;
    @Resource
    private IOauthClientDetailsService oauthClientDetailsService;

    @Override
    public String selectUserNameByTokenId(String tokenId) {
        return oauthAccessTokenMapper.selectUserNameByTokenId(tokenId);
    }

    @Override
    public OauthAccessToken selectByTokenId(String tokenId) {
        return oauthAccessTokenMapper.selectByTokenId(tokenId);
    }

    @Override
    public OauthAccessToken selectByRefreshToken(String refreshToken) {
        return oauthAccessTokenMapper.selectByRefreshToken(refreshToken);
    }

    @Override
    public void updateOpenIdByTokenId(String tokenId, String openUid) {
        oauthAccessTokenMapper.updateOpenIdByTokenId(tokenId, openUid);
    }

    @Override
    public OauthAccessToken selectByUserName(String userName) {
        return oauthAccessTokenMapper.selectByUserName(userName);
    }

    @Override
    public OauthAccessToken grantAuthorizationCodeForAccessToken(OauthClientDetails client, String code, String redirectUri, String state) {
        OauthCode oauthCode = oauthCodeService.consumeAuthorizationCode(code);
        return createAccessToken(client, oauthCode.getUserId(), null, oauthCode.getAuthentication());
    }

    @Override
    public OauthAccessToken createAccessToken(OauthClientDetails client, Long userId, String userName, String scope) {
        OauthAccessToken oauthAccessToken = new OauthAccessToken();
        oauthAccessToken.setTokenId(generateToken());
        oauthAccessToken.setClientId(client.getClientId());
        oauthAccessToken.setUserId(userId);
        oauthAccessToken.setUserName(userName);
        oauthAccessToken.setAuthentication(scope);
        oauthAccessToken.setRefreshToken(generateToken());
        long accessTokenValidity = client.getAccessTokenValidity() == null ? 7200L : client.getAccessTokenValidity();
        oauthAccessToken.setExpiresTime(LocalDateTime.now().plusSeconds(accessTokenValidity));
        oauthAccessTokenMapper.insertOauthAccessToken(oauthAccessToken);
        return oauthAccessToken;
    }

    @Override
    public OauthAccessToken refreshAccessToken(OauthClientDetails client, String refreshToken) {
        OauthAccessToken oldToken = oauthAccessTokenMapper.selectByRefreshToken(refreshToken);
        if (oldToken == null || !client.getClientId().equals(oldToken.getClientId())) {
            return null;
        }
        oauthAccessTokenMapper.deleteByTokenId(oldToken.getTokenId());
        return createAccessToken(client, oldToken.getUserId(), oldToken.getUserName(), oldToken.getAuthentication());
    }

    @Override
    public boolean revokeToken(String tokenId) {
        return oauthAccessTokenMapper.deleteByTokenId(tokenId) > 0;
    }

    private static String generateToken() {
        return IdUtil.fastSimpleUUID();
    }

}
