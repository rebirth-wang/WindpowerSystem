package com.fastbee.iot.card.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.Resource;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.osgi.framework.ServiceException;
import org.springframework.stereotype.Service;

import com.fastbee.common.core.redis.RedisCache;
import com.fastbee.common.extend.constant.CardConstants;
import com.fastbee.common.extend.core.card.CardPlatformConfigContent;
import com.fastbee.common.extend.core.card.ChinaMobileTokenResult;
import com.fastbee.common.extend.core.card.MobileCardInfoResult;
import com.fastbee.common.extend.enums.CardOperatorEnum;
import com.fastbee.common.extend.enums.CardStatusEnum;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.common.utils.http.HttpUtils;
import com.fastbee.iot.card.service.ICardPlatformFactoryService;
import com.fastbee.iot.domain.Card;
import com.fastbee.iot.model.dto.TrafficInfoDTO;

/**
 * @author zzy
 * @description: 中国移动
 * @date 2025-11-13 14:14
 */
@Slf4j
@Service
public class ChinaMobileFactoryServiceImpl implements ICardPlatformFactoryService {

    @Resource
    private RedisCache redisCache;

    @Override
    public Card syncCardInfo(String iccid, CardPlatformConfigContent configContent) throws Exception {
        try {
            // 先获取token
            String token = this.getToken(configContent);
            // 沙箱环境测试使用
//            String url = configContent.getApiBaseUrl() + "/" + configContent.getApiVersion() + StringUtils.format(CardConstants.MOBILE_SINGLE_INFO_QUERY_DEV, token, iccid);
            String url = configContent.getApiBaseUrl() + "/" + configContent.getApiVersion() + StringUtils.format(CardConstants.MOBILE_SINGLE_INFO_QUERY, configContent.getTransId(), token, iccid);
            String s = HttpUtils.sendGet(url);
            if (StringUtils.isEmpty(s)) {
                throw new Exception("获取移动卡信息失败");
            }
            MobileCardInfoResult infoResult = JSON.parseObject(s, MobileCardInfoResult.class);
            if (!CardConstants.SUCCESS_STATUS.equals(infoResult.getStatus())) {
                throw new Exception("获取移动卡信息失败: " + infoResult.getMessage());
            }
            MobileCardInfoResult.CardInfo cardInfo = infoResult.getResult().get(0);
            Card card = this.parseCard(cardInfo);
            Integer cardStatus = this.getStatus(iccid, token, configContent);
            card.setCardStatus(cardStatus);
            // 查询当月流量使用情况
            MobileCardInfoResult.MonthlyData monthlyData = this.getMonthlyData(iccid, token, configContent);
            if (Objects.nonNull(monthlyData)) {
                BigDecimal dividend = new BigDecimal(1024);
                card.setTotalData(this.kbChangeMb(new BigDecimal(monthlyData.getTotalAmount()), dividend));
                card.setDataUsed(this.kbChangeMb(new BigDecimal(monthlyData.getUseAmount()), dividend));
                card.setDataRemaining(this.kbChangeMb(new BigDecimal(monthlyData.getRemainAmount()), dividend));
                card.setDataPlan(monthlyData.getOfferingName());
            }
            card.setOperator(CardOperatorEnum.CHINA_MOBILE.getOperator());
            return card;
        } catch (Exception e) {
            log.error("获取移动卡详情异常, ICCID: {}", iccid, e);
            throw e;
        }
    }

    private BigDecimal kbChangeMb(BigDecimal divisor, BigDecimal dividend) {
        if (divisor.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return divisor.divide(dividend, 10, RoundingMode.HALF_UP);
    }

    private MobileCardInfoResult.MonthlyData getMonthlyData(String iccid, String token, CardPlatformConfigContent configContent) {
        // 沙箱环境测试使用
//        String url = configContent.getApiBaseUrl() + "/" + configContent.getApiVersion() + StringUtils.format(CardConstants.MOBILE_MONTH_FLOW_QUERY_DEV, token, iccid);
        String url = configContent.getApiBaseUrl() + "/" + configContent.getApiVersion() + StringUtils.format(CardConstants.MOBILE_MONTH_FLOW_QUERY, configContent.getTransId(), token, iccid);
        String s = HttpUtils.sendGet(url);
        if (StringUtils.isEmpty(s)) {
            return null;
        }
        MobileCardInfoResult platformInfoResult = JSON.parseObject(s, MobileCardInfoResult.class);
        if (!CardConstants.SUCCESS_STATUS.equals(platformInfoResult.getStatus())) {
            log.error("获取中国移动卡月流量失败，iccid：{}，error：{}", iccid, platformInfoResult.getMessage());
            return null;
        }
        MobileCardInfoResult.CardInfo cardInfo = platformInfoResult.getResult().get(0);
        if (ObjectUtil.isNull(cardInfo)) {
            return null;
        }
        return cardInfo.getAccmMarginList().get(0);
    }

    private Integer getStatus(String iccid, String token, CardPlatformConfigContent configContent) {
        // 沙箱环境测试使用
//        String url = configContent.getApiBaseUrl() + "/" + configContent.getApiVersion() + StringUtils.format(CardConstants.MOBILE_STATUS_QUERY_DEV, token, iccid);
        String url = configContent.getApiBaseUrl() + "/" + configContent.getApiVersion() + StringUtils.format(CardConstants.MOBILE_STATUS_QUERY, configContent.getTransId(), token, iccid);
        String s = HttpUtils.sendGet(url);
        if (StringUtils.isEmpty(s)) {
            return CardStatusEnum.UNKNOWN.getStatus();
        }
        MobileCardInfoResult platformInfoResult = JSON.parseObject(s, MobileCardInfoResult.class);
        if (!CardConstants.SUCCESS_STATUS.equals(platformInfoResult.getStatus())) {
            log.error("获取中国移动卡状态失败，iccid：{}，error：{}", iccid, platformInfoResult.getMessage());
            return CardStatusEnum.UNKNOWN.getStatus();
        }
        MobileCardInfoResult.CardInfo cardInfo = platformInfoResult.getResult().get(0);
        if (ObjectUtil.isNotNull(cardInfo)) {
            return matchStatus(cardInfo.getCardStatus()).getStatus();
        }
        return CardStatusEnum.UNKNOWN.getStatus();
    }

    private MobileCardInfoResult getBatchPlatform(String iccids, String token, CardPlatformConfigContent configContent) {
//        String url = configContent.getApiBaseUrl() + "/" + configContent.getApiVersion() + StringUtils.format(CardConstants.MOBILE_PLATFORM_BATCH_QUERY_DEV, token, iccids);
        String url = configContent.getApiBaseUrl() + "/" + configContent.getApiVersion() + StringUtils.format(CardConstants.MOBILE_PLATFORM_BATCH_QUERY, configContent.getTransId(), token, iccids);
        String s = HttpUtils.sendGet(url);
        if (StringUtils.isEmpty(s)) {
            return null;
        }
        MobileCardInfoResult platformInfoResult = JSON.parseObject(s, MobileCardInfoResult.class);
        if (!CardConstants.SUCCESS_STATUS.equals(platformInfoResult.getStatus())) {
            log.error("批量获取中国移动卡平台失败，iccids：{}", iccids);
        }
        return platformInfoResult;
    }

    @Override
    public List<Card> getBatchCardDetail(String iccids, CardPlatformConfigContent configModel) throws Exception {
        return new ArrayList<>();
    }

    private Card parseCard(MobileCardInfoResult.CardInfo cardInfo) {
        Card card = new Card();
        card.setIccid(cardInfo.getIccid());
        card.setImsi(cardInfo.getImsi());
        card.setMsisdn(cardInfo.getMsisdn());
        card.setOpenDate(DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS, cardInfo.getOpenDate()));
        card.setActivateTime(DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS, cardInfo.getActiveDate()));
        return card;
    }

    private String getToken(CardPlatformConfigContent configModel) {
        // 加个缓存
        String cacheKey = CardConstants.MOBILE_TOKEN + configModel.getAppid();
        String token = redisCache.getCacheObject(cacheKey);
        if (StringUtils.isNotEmpty(token)) {
            return token;
        }
        String url = configModel.getApiBaseUrl() + "/" + configModel.getApiVersion() + StringUtils.format(CardConstants.MOBILE_AUTH_SERVER, configModel.getAppid(), configModel.getPassword(), configModel.getTransId());
        String s = HttpUtils.sendGet(url);
        if (StringUtils.isEmpty(s)) {
            throw new ServiceException("中国移动获取token失败");
        }
        ChinaMobileTokenResult mobileTokenResult = JSON.parseObject(s, ChinaMobileTokenResult.class);
        if (!"0".equals(mobileTokenResult.getStatus())) {
            throw new ServiceException("中国移动获取token失败: " + mobileTokenResult.getMessage());
        }
        ChinaMobileTokenResult.TokenResult tokenResult = mobileTokenResult.getResult().get(0);
        Integer ttl = 1800;
        if (StringUtils.isNotEmpty(tokenResult.getTtl())) {
            ttl = Integer.valueOf(tokenResult.getTtl());
        }
        redisCache.setCacheObject(cacheKey, tokenResult.getToken(), ttl, TimeUnit.SECONDS);
        return tokenResult.getToken();
    }

    @Override
    public TrafficInfoDTO syncTrafficInfo(String iccid, CardPlatformConfigContent configModel) throws Exception {
        try {
            String token = this.getToken(configModel);
            MobileCardInfoResult.MonthlyData monthlyData = this.getMonthlyData(iccid, token, configModel);
            if (Objects.isNull(monthlyData)) {
                return null;
            }
            TrafficInfoDTO trafficInfoDTO = this.parseTrafficInfo(monthlyData);
            trafficInfoDTO.setIccid(iccid);
            return trafficInfoDTO;
        } catch (Exception e) {
            log.error("查询移动卡流量异常, ICCID: {}", iccid, e);
            throw e;
        }
    }

    @Override
    public Integer syncStatus(String iccid, CardPlatformConfigContent configContent) {
        // 先获取token
        String token = this.getToken(configContent);
        return this.getStatus(iccid, token, configContent);
    }

    @Override
    public boolean activateCard(String iccid) throws Exception {
        log.info("激活移动卡: {}", iccid);
        // 调用移动激活API
        return true;
    }

    @Override
    public boolean suspendCard(String iccid) throws Exception {
        log.info("停用移动卡: {}", iccid);
        // 调用移动停用API
        return true;
    }

    @Override
    public boolean resumeCard(String iccid) throws Exception {
        log.info("恢复移动卡: {}", iccid);
        // 调用移动恢复API
        return true;
    }

    private TrafficInfoDTO parseTrafficInfo(MobileCardInfoResult.MonthlyData monthlyData) {
        TrafficInfoDTO trafficInfoDTO = new TrafficInfoDTO();
        // 假设移动返回的是MB
        trafficInfoDTO.setTotalData(new BigDecimal(monthlyData.getTotalAmount()));
        trafficInfoDTO.setDataUsed(new BigDecimal(monthlyData.getUseAmount()));
        trafficInfoDTO.setDataRemaining(new BigDecimal(monthlyData.getRemainAmount()));
        trafficInfoDTO.setDataPlan(monthlyData.getOfferingName());

        return trafficInfoDTO;
    }

    private CardStatusEnum matchStatus(String status) {
        // 当开卡平台为OneLink-PB时：00-正常、01-单向停机、02-停机、03-预销号、05-过户、06-休眠、07-待激活、99-号码不存在；当开卡平台为OneLink-CT时：1：待激活、2：已激活、4：停机、6：可测试、7：库存、8：预销户
        if (status.length() > 1) {
            switch (status) {
                case "00":
                    return CardStatusEnum.NORMAL;
                case "01":
                case "02":
                    return CardStatusEnum.SHUTDOWN;
                case "07":
                    return CardStatusEnum.PENDING_ACTIVATION;
                default:
                    return CardStatusEnum.UNKNOWN;
            }
        } else {
            switch (status) {
                case "1":
                    return CardStatusEnum.PENDING_ACTIVATION;
                case "2":
                    return CardStatusEnum.NORMAL;
                case "4":
                    return CardStatusEnum.SHUTDOWN;
                default:
                    return CardStatusEnum.UNKNOWN;
            }
        }
    }
}
