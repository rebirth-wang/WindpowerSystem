package com.fastbee.iot.card.service.impl;

import java.math.RoundingMode;
import java.util.*;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.osgi.framework.ServiceException;
import org.springframework.stereotype.Service;

import com.fastbee.common.extend.constant.CardConstants;
import com.fastbee.common.extend.core.card.CardPlatformConfigContent;
import com.fastbee.common.extend.core.card.HandShakeCardInfoResult;
import com.fastbee.common.extend.enums.CardOperatorEnum;
import com.fastbee.common.extend.enums.CardStatusEnum;
import com.fastbee.common.extend.utils.ForestHttpUtils;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.card.service.ICardPlatformFactoryService;
import com.fastbee.iot.domain.Card;
import com.fastbee.iot.model.dto.TrafficInfoDTO;
import com.fastbee.iot.util.MD5Util;

/**
 * 握手物联服务类
 *
 * @author fastbee
 * @date 2025/12/19
 */
@Slf4j
@Service
public class HandShakeFactoryServiceImpl implements ICardPlatformFactoryService {

    @Override
    public Card syncCardInfo(String iccid, CardPlatformConfigContent configModel) throws Exception {
        Map<String, String> params = this.buildParams(iccid, configModel);
        String result = ForestHttpUtils.get(configModel.getApiBaseUrl() + CardConstants.HAND_SHAKE_INFO_QUERY, null, params);
        if (StringUtils.isEmpty(result)) {
            throw new Exception("获取握手物联卡信息失败");
        }
        HandShakeCardInfoResult cardInfoResult = JSON.parseObject(result, HandShakeCardInfoResult.class);
        if (!CardConstants.HAND_SHAKE_SUCCESS_STATUS.equals(cardInfoResult.getStatus())) {
            throw new ServiceException("获取握手物联卡信息失败: " + cardInfoResult.getMsg());
        }
        return this.parseCard(cardInfoResult.getData());
    }

    @Override
    public List<Card> getBatchCardDetail(String iccids, CardPlatformConfigContent configModel) throws Exception {
        return Collections.emptyList();
    }

    @Override
    public TrafficInfoDTO syncTrafficInfo(String iccid, CardPlatformConfigContent configModel) throws Exception {
        Card card = this.syncCardInfo(iccid, configModel);
        if (Objects.isNull(card)) {
            return null;
        }
        TrafficInfoDTO trafficInfoDTO = new TrafficInfoDTO();
        trafficInfoDTO.setTotalData(card.getTotalData());
        trafficInfoDTO.setDataUsed(card.getDataUsed());
        trafficInfoDTO.setDataRemaining(card.getDataRemaining());
        trafficInfoDTO.setDataPlan(card.getDataPlan());
        return trafficInfoDTO;
    }

    @Override
    public Integer syncStatus(String iccid, CardPlatformConfigContent configModel) throws Exception {
        Card card = this.syncCardInfo(iccid, configModel);
        if (Objects.isNull(card)) {
            return CardStatusEnum.UNKNOWN.getStatus();
        }
        return card.getCardStatus();
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

    private Card parseCard(HandShakeCardInfoResult.CardInfo cardInfo) {
        Card card = new Card();
        card.setIccid(cardInfo.getIccid());
        card.setImsi(cardInfo.getImsi());
        card.setMsisdn(cardInfo.getMsisdn());
        if (StringUtils.isNotEmpty(cardInfo.getActivateTime())) {
            card.setActivateTime(DateUtils.dateTime(DateUtils.YYYY_MM_DD, cardInfo.getActivateTime()));
        }
        if (StringUtils.isNotEmpty(cardInfo.getExpireTime())) {
            card.setExpireTime(DateUtils.dateTime(DateUtils.YYYY_MM_DD, cardInfo.getExpireTime()));
        }
        card.setCardStatus(this.matchStatus(cardInfo.getCardStatus()));
        card.setTotalData(cardInfo.getGprsAll());
        card.setDataUsed(cardInfo.getGprsUse());
        if (null != card.getTotalData() && null != card.getDataUsed()) {
            card.setDataRemaining(card.getTotalData().subtract(card.getDataUsed()).setScale(2, RoundingMode.HALF_UP));
        }
        card.setDataPlan(cardInfo.getCateName());
        if (StringUtils.isNotEmpty(cardInfo.getCardYys())) {
            card.setOperator(CardOperatorEnum.getOperatorByName(cardInfo.getCardYys()));
        }
        return card;
    }

    private Integer matchStatus(String cardStatus) {
        switch (cardStatus) {
            case "00":
                return CardStatusEnum.NORMAL.getStatus();
            case "01":
            case "02":
                return CardStatusEnum.SHUTDOWN.getStatus();
            case "03":
                return CardStatusEnum.ACCOUNT_TERMINATION.getStatus();
            case "07":
                return CardStatusEnum.PENDING_ACTIVATION.getStatus();
            default:
                return CardStatusEnum.UNKNOWN.getStatus();
        }
    }

    private Map<String, String> buildParams(String iccid, CardPlatformConfigContent configModel) {
        Map<String, String> params = new HashMap<>(2);
        params.put("iccid", iccid);
        params.put("amount", configModel.getAccount());
        String encryptionPassword = MD5Util.encryptToMD5(configModel.getPassword());
        params.put("key", encryptionPassword);
        return params;
    }
}
