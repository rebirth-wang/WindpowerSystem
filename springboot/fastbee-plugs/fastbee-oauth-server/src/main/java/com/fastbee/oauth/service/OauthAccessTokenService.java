package com.fastbee.oauth.service;

import com.fastbee.oauth.domain.OauthAccessToken;
import com.fastbee.oauth.domain.OauthClientDetails;

/**
 * @author fastb
 * @date 2023-09-01 17:20
 */
public interface OauthAccessTokenService {

    String selectUserNameByTokenId(String token);

    OauthAccessToken selectByTokenId(String tokenId);

    OauthAccessToken selectByRefreshToken(String refreshToken);

    void updateOpenIdByTokenId(String tokenId, String openUid);

    OauthAccessToken selectByUserName(String userName);

    OauthAccessToken grantAuthorizationCodeForAccessToken(OauthClientDetails client, String code, String redirectUri, String state);

    OauthAccessToken createAccessToken(OauthClientDetails client, Long userId, String userName, String scope);

    OauthAccessToken refreshAccessToken(OauthClientDetails client, String refreshToken);

    boolean revokeToken(String tokenId);
}
