package com.fastbee.iot.card.service.impl;

import static com.fastbee.common.extend.constant.CardConstants.SUCCESS_STATUS_NUM;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.Resource;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.osgi.framework.ServiceException;
import org.springframework.stereotype.Service;

import com.fastbee.common.core.redis.RedisCache;
import com.fastbee.common.extend.constant.CardConstants;
import com.fastbee.common.extend.core.card.CardPlatformConfigContent;
import com.fastbee.common.extend.core.card.UsrCardDataPlanResult;
import com.fastbee.common.extend.core.card.UsrCardInfoResult;
import com.fastbee.common.extend.enums.CardOperatorEnum;
import com.fastbee.common.extend.enums.CardStatusEnum;
import com.fastbee.common.extend.utils.ForestHttpUtils;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.card.service.ICardPlatformFactoryService;
import com.fastbee.iot.domain.Card;
import com.fastbee.iot.model.dto.TrafficInfoDTO;

/**
 * 有人云服务类
 *
 * @author fastbeeK
 * @date 2025/12/17
 */
@Slf4j
@Service
public class UsrFactoryServiceImpl implements ICardPlatformFactoryService {

    @Resource
    private RedisCache redisCache;

    @Override
    public Card syncCardInfo(String iccid, CardPlatformConfigContent configModel) throws Exception {
        String token = this.getToken(configModel);
        UsrCardInfoResult baseInfoResult = this.getInfo(iccid, configModel, token);
        UsrCardDataPlanResult dataPlanResult = this.getDataPlan(iccid, configModel, token);
        return this.parseCard(baseInfoResult, dataPlanResult);
    }

    @Override
    public List<Card> getBatchCardDetail(String iccids, CardPlatformConfigContent configModel) throws Exception {
        return Collections.emptyList();
    }

    @Override
    public TrafficInfoDTO syncTrafficInfo(String iccid, CardPlatformConfigContent configModel) throws Exception {
        String token = this.getToken(configModel);
        UsrCardInfoResult infoResult = this.getInfo(iccid, configModel, token);
        UsrCardInfoResult.CardInfo data = infoResult.getData();
        TrafficInfoDTO trafficInfoDTO = new TrafficInfoDTO();
        trafficInfoDTO.setIccid(iccid);
        trafficInfoDTO.setTotalData(data.getTotalFlow());
        trafficInfoDTO.setDataUsed(data.getUseFlow());
        trafficInfoDTO.setDataRemaining(data.getSurplusFlow());
        return trafficInfoDTO;
    }

    @Override
    public Integer syncStatus(String iccid, CardPlatformConfigContent configModel) throws Exception {
        String token = this.getToken(configModel);
        UsrCardInfoResult baseInfoResult = this.getInfo(iccid, configModel, token);
        UsrCardInfoResult.CardInfo data = baseInfoResult.getData();
        return this.matchStatus(data.getCardStatusId());
    }

    @Override
    public boolean activateCard(String iccid) throws Exception {
        return false;
    }

    @Override
    public boolean suspendCard(String iccid) throws Exception {
        return false;
    }

    @Override
    public boolean resumeCard(String iccid) throws Exception {
        return false;
    }

    private String getToken(CardPlatformConfigContent configModel) throws Exception {
        // 加个缓存
        String cacheKey = CardConstants.USR_TOKEN_REDIS + configModel.getAppKey();
        String token = redisCache.getCacheObject(cacheKey);
        if (StringUtils.isNotEmpty(token)) {
            return token;
        }
        String url = configModel.getApiBaseUrl() + CardConstants.USR_TOKEN_QUERY;
        Map<String, Object> params = new HashMap<>(2);
        params.put("appKey", configModel.getAppKey());
        params.put("appSecret", configModel.getAppSecret());
        String result = ForestHttpUtils.post(url, null, JSON.toJSONString(params));
        if (StringUtils.isEmpty(result)) {
            throw new ServiceException("有人云获取token失败");
        }
        UsrCardInfoResult infoResult = JSON.parseObject(result, UsrCardInfoResult.class);
        if (!SUCCESS_STATUS_NUM.equals(infoResult.getStatus())) {
            throw new ServiceException("有人云获取token失败: " + infoResult.getDesc());
        }
        redisCache.setCacheObject(cacheKey, infoResult.getData().getToken(), 7000, TimeUnit.SECONDS);
        return infoResult.getData().getToken();
    }

    private int matchStatus(Integer cardStatusId) {
        switch (cardStatusId) {
            case 1:
                return CardStatusEnum.NORMAL.getStatus();
            case 5:
                return CardStatusEnum.ACCOUNT_TERMINATION.getStatus();
            case 7:
            case 8:
                return CardStatusEnum.SHUTDOWN.getStatus();
            default:
                return CardStatusEnum.UNKNOWN.getStatus();
        }
    }

    private UsrCardDataPlanResult getDataPlan(String iccid, CardPlatformConfigContent configModel, String token) throws Exception {
        Map<String, String> headers = new HashMap<>(2);
        headers.put("token", token);
        Map<String, Object> params = new HashMap<>(2);
        params.put("iccid", iccid);
        params.put("isMainPackage", true);
        String url = configModel.getApiBaseUrl() + CardConstants.USR_DATA_PLAN_QUERY;
        String result = ForestHttpUtils.post(url, headers, JSON.toJSONString(params));
        if (StringUtils.isEmpty(result)) {
            throw new ServiceException("有人云获取卡内套餐列表失败");
        }
        UsrCardDataPlanResult dataPlanResult = JSON.parseObject(result, UsrCardDataPlanResult.class);
        if (!SUCCESS_STATUS_NUM.equals(dataPlanResult.getStatus())) {
            throw new ServiceException("有人云获取卡内套餐列表失败: " + dataPlanResult.getDesc());
        }
        return dataPlanResult;
    }

    private UsrCardInfoResult getInfo(String iccid, CardPlatformConfigContent configModel, String token) throws Exception {
        Map<String, String> headers = new HashMap<>(2);
        headers.put("token", token);
        Map<String, Object> params = new HashMap<>(2);
        params.put("iccid", iccid);
        String url = configModel.getApiBaseUrl() + CardConstants.USR_INFO_QUERY;
        String result = ForestHttpUtils.post(url, headers, JSON.toJSONString(params));
        if (StringUtils.isEmpty(result)) {
            throw new ServiceException("有人云获取卡详情信息失败");
        }
        UsrCardInfoResult infoResult = JSON.parseObject(result, UsrCardInfoResult.class);
        if (!SUCCESS_STATUS_NUM.equals(infoResult.getStatus())) {
            throw new ServiceException("有人云获取卡详情信息失败: " + infoResult.getDesc());
        }
        return infoResult;
    }

    private Card parseCard(UsrCardInfoResult baseInfoResult, UsrCardDataPlanResult dataPlanResult) {
        Card card = new Card();
        UsrCardInfoResult.CardInfo data = baseInfoResult.getData();
        card.setIccid(data.getIccid());
        card.setMsisdn(data.getMsisdn());
        card.setCardStatus(this.matchStatus(data.getCardStatusId()));
        card.setOperator(CardOperatorEnum.getOperatorByName(data.getOperatorName()));
        card.setTotalData(data.getTotalFlow());
        card.setDataUsed(data.getUseFlow());
        card.setDataRemaining(data.getSurplusFlow());
        if (StringUtils.isNotEmpty(data.getCardEndDate())) {
            card.setExpireTime(DateUtils.dateTime(DateUtils.YYYY_MM_DD, data.getCardEndDate()));
        }
        UsrCardDataPlanResult.DataPlanResult dataPlan = dataPlanResult.getData().get(0);
        card.setDataPlan(dataPlan.getPackageName());
        return card;
    }
}
