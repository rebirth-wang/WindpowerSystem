package com.fastbee.common.extend.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 物联网卡平台枚举
 * @author fastb
 * @date 2024-05-22 10:01
 * @version 1.0
 */
@Getter
@AllArgsConstructor
public enum CardPlatformEnum {

    CHINA_MOBILE("mobile", "中国移动"),
    CHINA_TELECOM("telecom", "中国电信5GCMP"),
    CHINA_UNICOM("unicom", "中国联通"),
    USR_IOT("usr", "有人云物联卡"),
    HANDSHAKE_IOT("handshake", "握手物联卡"),
    ZHIYU_IOT("zhiyu", "智宇物联卡");

    private final String platform;

    private final String description;

    public static CardPlatformEnum getEnumByPlatform(String platform) {
        for (CardPlatformEnum cardPlatformEnum : CardPlatformEnum.values()) {
            if (cardPlatformEnum.getPlatform().equals(platform)) {
                return cardPlatformEnum;
            }
        }
        return null;
    }

    public static CardPlatformEnum getByIccid(String iccid) {
        if (iccid.startsWith("898605") || iccid.startsWith("898602") || iccid.startsWith("898603")
                || iccid.startsWith("898604") || iccid.startsWith("898605") || iccid.startsWith("898607") || iccid.startsWith("898608") || iccid.startsWith("898609")) {
            return CHINA_MOBILE;
        } else if (iccid.startsWith("898601") || iccid.startsWith("898606")) {
            return CHINA_TELECOM;
        } else if (iccid.startsWith("898600")) {
            return CHINA_UNICOM;
        }
        return null;
    }

}
