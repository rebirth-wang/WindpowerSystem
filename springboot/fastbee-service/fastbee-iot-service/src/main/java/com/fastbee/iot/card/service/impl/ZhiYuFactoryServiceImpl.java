package com.fastbee.iot.card.service.impl;

import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.osgi.framework.ServiceException;
import org.springframework.stereotype.Service;

import com.fastbee.common.extend.constant.CardConstants;
import com.fastbee.common.extend.core.card.CardPlatformConfigContent;
import com.fastbee.common.extend.core.card.ZhiYuCardInfoResult;
import com.fastbee.common.extend.core.card.ZhiYuCardQueryParams;
import com.fastbee.common.extend.enums.CardOperatorEnum;
import com.fastbee.common.extend.enums.CardStatusEnum;
import com.fastbee.common.extend.utils.ForestHttpUtils;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.card.service.ICardPlatformFactoryService;
import com.fastbee.iot.domain.Card;
import com.fastbee.iot.model.dto.TrafficInfoDTO;

/**
 * 智宇物联服务类
 *
 * @author fastbee
 * @date 2025/12/17
 */
@Slf4j
@Service
public class ZhiYuFactoryServiceImpl implements ICardPlatformFactoryService {

    @Override
    public Card syncCardInfo(String iccid, CardPlatformConfigContent configModel) throws Exception {
        ZhiYuCardQueryParams queryParams = this.buildQueryParams(iccid, configModel);
        queryParams.setName(CardConstants.ZHI_YU_INFO_QUERY);
        String result = ForestHttpUtils.post(configModel.getApiBaseUrl(), null, JSON.toJSONString(queryParams));
        if (StringUtils.isEmpty(result)) {
            throw new Exception("获取智宇物联卡信息失败");
        }
        ZhiYuCardInfoResult cardInfoResult = JSON.parseObject(result, ZhiYuCardInfoResult.class);
        if (!CardConstants.SUCCESS_STATUS_NUM.equals(cardInfoResult.getCode())) {
            throw new ServiceException("获取智宇物联卡信息失败: " + cardInfoResult.getMsg());
        }
        return this.parseCard(cardInfoResult.getData().get(0));
    }

    private Card parseCard(ZhiYuCardInfoResult.CardInfo cardInfo) {
        Card card = new Card();
        card.setIccid(cardInfo.getIccid());
        card.setImsi(cardInfo.getImsi());
        card.setMsisdn(cardInfo.getMsisdn());
        if (StringUtils.isNotEmpty(cardInfo.getActiveTime())) {
            card.setActivateTime(DateUtils.dateTime(DateUtils.YYYY_MM_DD, cardInfo.getActiveTime()));
        }
        if (StringUtils.isNotEmpty(cardInfo.getCardEndTime())) {
            card.setExpireTime(DateUtils.dateTime(DateUtils.YYYY_MM_DD, cardInfo.getCardEndTime()));
        }
        card.setCardStatus(this.matchStatus(cardInfo.getCardStatus()));
        card.setTotalData(cardInfo.getPackageCanUsage());
        card.setDataUsed(cardInfo.getPackageHasUsage());
        if (null != card.getTotalData() && null != card.getDataUsed()) {
            card.setDataRemaining(card.getTotalData().subtract(card.getDataUsed()).setScale(2, RoundingMode.HALF_UP));
        }
        card.setDataPlan(cardInfo.getPackageName());
        if (StringUtils.isNotEmpty(cardInfo.getSendCardTime())) {
            card.setOpenDate(DateUtils.dateTime(DateUtils.YYYY_MM_DD, cardInfo.getSendCardTime()));
        }
        if (StringUtils.isNotEmpty(card.getDataPlan())) {
            card.setOperator(this.matchOperator(card.getDataPlan()));
        }
        return card;
    }

    private String matchOperator(String dataPlan) {
        if (dataPlan.contains("移动")) {
            return CardOperatorEnum.CHINA_MOBILE.getOperator();
        } else if (dataPlan.contains("电信")) {
            return CardOperatorEnum.CHINA_TELECOM.getOperator();
        } else if (dataPlan.contains("联通")) {
            return CardOperatorEnum.CHINA_UNICOM.getOperator();
        }
        return null;
    }

    private Integer matchStatus(Integer cardStatus) {
        switch (cardStatus) {
            case 2:
            case 8:
                return CardStatusEnum.PENDING_ACTIVATION.getStatus();
            case 4:
            case 5:
                return CardStatusEnum.SHUTDOWN.getStatus();
            case 9:
                return CardStatusEnum.NORMAL.getStatus();
            case 20:
                return CardStatusEnum.ACCOUNT_TERMINATION.getStatus();
            default:
                return CardStatusEnum.UNKNOWN.getStatus();
        }
    }

    private ZhiYuCardQueryParams buildQueryParams(String iccid, CardPlatformConfigContent configModel) {
        ZhiYuCardQueryParams queryParams = new ZhiYuCardQueryParams();
        queryParams.setIccid(iccid);
        queryParams.setAppid(configModel.getAppid());
        queryParams.setAppsecret(configModel.getAppSecret());
        return queryParams;
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

}
