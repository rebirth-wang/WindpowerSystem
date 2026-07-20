//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.enums;

import java.util.Arrays;
import java.util.List;

public enum SocialPlatformType {
    WECHAT_OPEN_WEB("wechat_open_web", "微信开放平台网站应用"),
    WECHAT_OPEN_WEB_BIND("wechat_open_web_bind", "微信开放平台网站应用个人中心绑定"),
    WECHAT_OPEN_MOBILE("wechat_open_mobile", "微信开放平台移动应用"),
    WECHAT_OPEN_MINI_PROGRAM("wechat_open_mini_program", "微信开放平台小程序"),
    WECHAT_OPEN_PUBLIC_ACCOUNT("wechat_open_public_account", "微信开放平台公众号"),
    QQ_OPEN_WEB("qq_open_web", "QQ互联网站应用"),
    QQ_OPEN_APP("qq_open_app", "QQ互联移动应用"),
    QQ_OPEN_MINI_PROGRAM("qq_open_mini_program", "QQ互联小程序");

    public String sourceClient;
    public String desc;
    public static final List<String> listWechatPlatform = Arrays.asList(WECHAT_OPEN_WEB.sourceClient, WECHAT_OPEN_MOBILE.sourceClient, WECHAT_OPEN_MINI_PROGRAM.sourceClient);

    private SocialPlatformType(String sourceClient, String desc) {
        this.sourceClient = sourceClient;
        this.desc = desc;
    }

    public static String getDesc(String sourceClient) {
        for(SocialPlatformType var4 : values()) {
            if (var4.getSourceClient().equals(sourceClient)) {
                return var4.getDesc();
            }
        }

        return null;
    }

    public static SocialPlatformType getSocialPlatformType(String sourceClient) {
        for(SocialPlatformType var4 : values()) {
            if (var4.getSourceClient().equals(sourceClient)) {
                return var4;
            }
        }

        return null;
    }

    public String getSourceClient() {
        return this.sourceClient;
    }

    public void setSourceClient(String sourceClient) {
        this.sourceClient = sourceClient;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
