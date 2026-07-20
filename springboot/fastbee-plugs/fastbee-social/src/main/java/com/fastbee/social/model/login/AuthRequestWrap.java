package com.fastbee.social.model.login;

import me.zhyd.oauth.request.AuthRequest;

import com.fastbee.social.domain.SocialPlatform;

public class AuthRequestWrap {
    private AuthRequest authRequest;

    private SocialPlatform socialPlatform;

    public AuthRequest getAuthRequest() {
        return authRequest;
    }

    public void setAuthRequest(AuthRequest authRequest) {
        this.authRequest = authRequest;
    }

    public SocialPlatform getSocialPlatform() {
        return socialPlatform;
    }

    public void setSocialPlatform(SocialPlatform socialPlatform) {
        this.socialPlatform = socialPlatform;
    }
}
