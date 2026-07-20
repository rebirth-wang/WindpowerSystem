package com.fastbee.iot.card.factory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.fastbee.common.extend.enums.CardPlatformEnum;
import com.fastbee.iot.card.service.ICardPlatformFactoryService;
import com.fastbee.iot.card.service.impl.*;

/**
 * @author zzy
 * @description: 物联网卡平台工厂类
 * @date 2025-11-13 17:12
 */
@Component
@RequiredArgsConstructor
public class CardPlatformFactory {

    private final ChinaMobileFactoryServiceImpl chinaMobileService;
    private final ChinaTelecomFactoryServiceImpl chinaTelecomService;
    private final ChinaUnicomFactoryServiceImpl chinaUnicomService;
    private final UsrFactoryServiceImpl usrFactoryService;
    private final ZhiYuFactoryServiceImpl zhiYuFactoryService;
    private final HandShakeFactoryServiceImpl handShakeFactoryService;

    public ICardPlatformFactoryService getService(CardPlatformEnum platform) {
        switch (platform) {
            case CHINA_MOBILE:
                return chinaMobileService;
            case CHINA_TELECOM:
                return chinaTelecomService;
            case CHINA_UNICOM:
                return chinaUnicomService;
            case USR_IOT:
                return usrFactoryService;
            case ZHIYU_IOT:
                return zhiYuFactoryService;
            case HANDSHAKE_IOT:
                return handShakeFactoryService;
            default:
                throw new IllegalArgumentException("不支持的平台: " + platform);
        }
    }

    public ICardPlatformFactoryService getServiceByIccid(String iccid) {
        CardPlatformEnum operator = CardPlatformEnum.getByIccid(iccid);
        if (operator == null) {
            throw new IllegalArgumentException("无法识别ICCID对应的运营商: " + iccid);
        }
        return getService(operator);
    }

}
