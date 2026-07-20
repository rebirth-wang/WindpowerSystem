package com.fastbee.iot.card.service.impl;

import java.math.BigDecimal;
import java.util.*;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.fastbee.common.extend.constant.CardConstants;
import com.fastbee.common.extend.core.card.CardPlatformConfigContent;
import com.fastbee.common.extend.core.card.UnicomCardInfoResult;
import com.fastbee.common.extend.core.card.UnicomCardQueryParams;
import com.fastbee.common.extend.enums.CardOperatorEnum;
import com.fastbee.common.extend.enums.CardStatusEnum;
import com.fastbee.common.extend.utils.SM3Util;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.common.utils.http.HttpUtils;
import com.fastbee.iot.card.service.ICardPlatformFactoryService;
import com.fastbee.iot.domain.Card;
import com.fastbee.iot.model.dto.TrafficInfoDTO;

/**
 * @author zzy
 * @description: 中国联通物联网卡服务类
 * @date 2025-11-13 17:14
 */
@Slf4j
@Service
public class ChinaUnicomFactoryServiceImpl implements ICardPlatformFactoryService {
    @Override
    public Card syncCardInfo(String iccid, CardPlatformConfigContent configModel) throws Exception {
        return this.getCardInfo(iccid, configModel);
    }

    private Card getCardInfo(String iccid, CardPlatformConfigContent configModel) {
        String timestamp = DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss SSS", new Date());
        String transId = this.getTransId(timestamp);
        String token = this.getToken(configModel, timestamp, transId);

        UnicomCardQueryParams unicomCardQueryParams = new UnicomCardQueryParams();
        unicomCardQueryParams.setApp_id(configModel.getAppid());
        unicomCardQueryParams.setTimestamp(timestamp);
        unicomCardQueryParams.setTrans_id(transId);
        unicomCardQueryParams.setToken(token);
        UnicomCardQueryParams.DataParams dataParams = new UnicomCardQueryParams.DataParams();
        dataParams.setMessageId("unicom");
        dataParams.setOpenId(configModel.getOpenId());
        dataParams.setVersion(configModel.getVersion());
        dataParams.setIccids(Collections.singletonList(iccid));
        unicomCardQueryParams.setData(dataParams);

        String url = configModel.getApiBaseUrl() + CardConstants.UNICOM_BATCH_INFO_QUERY;

        try {
            String result = HttpUtils.sendPost(url, JSONObject.toJSONString(unicomCardQueryParams));
            if (StringUtils.isEmpty(result)) {
                return null;
            }
            UnicomCardInfoResult infoResult = JSON.parseObject(result, UnicomCardInfoResult.class);
            if (!CardConstants.UNICOM_SUCCESS_STATUS.equals(infoResult.getStatus()) || Objects.isNull(infoResult.getData())) {
                throw new Exception("获取联通卡信息失败: " + infoResult.getMessage());
            }

            Card card = this.parseCard(infoResult);
            assert card != null;
            card.setOperator(CardOperatorEnum.CHINA_UNICOM.getOperator());
            return card;
        } catch (Exception e) {

        }

        return null;
    }

    private Card parseCard(UnicomCardInfoResult baseInfoResult) {
        UnicomCardInfoResult.terminal terminal = baseInfoResult.getData().getTerminals().get(0);
        if (Objects.isNull(terminal)) {
            return null;
        }
        Card card = new Card();
        card.setIccid(terminal.getIccid());
        card.setImsi(terminal.getImsi());
        card.setMsisdn(terminal.getMsisdn());
        card.setImei(terminal.getImei());
        card.setDataPlan(terminal.getRatePlan());
        card.setTotalData(new BigDecimal(terminal.getMonthToDateDataUsage()));
        card.setDataUsed(new BigDecimal(terminal.getMonthToDateUsage()));
        card.setActivateTime(DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS, terminal.getDateActivated()));
        Integer cardStatus = this.matchStatus(terminal.getSimStatus());
        card.setCardStatus(cardStatus);
        return card;
    }

    private Integer matchStatus(String simStatus) {
        switch (simStatus) {
            case "0":
            case "1":
                return CardStatusEnum.PENDING_ACTIVATION.getStatus();
            case "2":
                return CardStatusEnum.NORMAL.getStatus();
            case "3":
                return CardStatusEnum.SHUTDOWN.getStatus();
            case "4":
            case "5":
                return CardStatusEnum.ACCOUNT_TERMINATION.getStatus();
            default:
                return CardStatusEnum.UNKNOWN.getStatus();
        }
    }

    private String getToken(CardPlatformConfigContent configModel, String timestamp, String transId) {
        String data = "app_id" + configModel.getAppid() + "timestamp" + timestamp + "trans_id" + transId + configModel.getAppSecret();
        return SM3Util.hashWithSM3Digest(data);
    }

    private String getTransId(String timestamp) {
        return timestamp + new Random().nextInt(999999);
    }

    @Override
    public List<Card> getBatchCardDetail(String iccids, CardPlatformConfigContent configModel) throws Exception {
        return Collections.emptyList();
    }

    @Override
    public TrafficInfoDTO syncTrafficInfo(String iccid, CardPlatformConfigContent configModel) throws Exception {
        Card cardDetail = this.getCardInfo(iccid, configModel);
        if (Objects.isNull(cardDetail)) {
            return null;
        }
        TrafficInfoDTO trafficInfoDTO = new TrafficInfoDTO();
        trafficInfoDTO.setIccid(iccid);
        trafficInfoDTO.setTotalData(cardDetail.getTotalData());
        trafficInfoDTO.setDataUsed(cardDetail.getDataUsed());
        trafficInfoDTO.setDataRemaining(cardDetail.getDataRemaining());
        trafficInfoDTO.setDataPlan(cardDetail.getDataPlan());
        return trafficInfoDTO;
    }

    @Override
    public Integer syncStatus(String iccid, CardPlatformConfigContent configModel) {
        Card cardInfo = this.getCardInfo(iccid, configModel);
        if (Objects.isNull(cardInfo)) {
            return null;
        }
        return cardInfo.getCardStatus();
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
