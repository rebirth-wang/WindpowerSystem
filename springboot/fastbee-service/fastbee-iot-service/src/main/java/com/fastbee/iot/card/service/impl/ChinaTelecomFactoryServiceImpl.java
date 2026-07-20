package com.fastbee.iot.card.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.osgi.framework.ServiceException;
import org.springframework.stereotype.Service;

import com.fastbee.common.extend.constant.CardConstants;
import com.fastbee.common.extend.core.card.CardPlatformConfigContent;
import com.fastbee.common.extend.core.card.TelecomCardImeiInfoResult;
import com.fastbee.common.extend.core.card.TelecomCardInfoResult;
import com.fastbee.common.extend.enums.CardOperatorEnum;
import com.fastbee.common.extend.enums.CardStatusEnum;
import com.fastbee.common.extend.utils.ForestHttpUtils;
import com.fastbee.common.extend.utils.SignUtil;
import com.fastbee.common.extend.utils.XmlUtil;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.card.service.ICardPlatformFactoryService;
import com.fastbee.iot.domain.Card;
import com.fastbee.iot.model.dto.TrafficInfoDTO;

/**
 * @author zzy
 * @description: 中国电信服务类
 * @date 2025-11-13 16:29
 */
@Slf4j
@Service
public class ChinaTelecomFactoryServiceImpl implements ICardPlatformFactoryService {

    @Override
    public Card syncCardInfo(String iccid, CardPlatformConfigContent configModel) throws Exception {
        try {
            Card card = new Card();
            // 查网卡三码
            this.getThreeYards(iccid, configModel, card);
            // 查询接入号码
            String accessNumber = this.getAccessNumber(iccid, configModel);
            if (StringUtils.isNotEmpty(accessNumber)) {
                // 查卡主状态查询
                Integer status = this.getStatus(accessNumber, configModel);
                card.setCardStatus(status);
                // 查套餐
                String dataPlan = this.getDataPlan(accessNumber, configModel);
                card.setDataPlan(dataPlan);
            }
            // 查流量
            TrafficInfoDTO trafficInfo = this.syncTrafficInfo(iccid, configModel);
            if (Objects.nonNull(trafficInfo)) {
                card.setDataUsed(trafficInfo.getDataUsed());
            }
            card.setOperator(CardOperatorEnum.CHINA_TELECOM.getOperator());
            throw new RuntimeException("获取电信卡信息失败");

        } catch (Exception e) {
            log.error("获取电信卡详情异常, iccid: {}", iccid, e);
            throw e;
        }
    }

    private String getAccessNumber(String iccid, CardPlatformConfigContent configModel) throws Exception {
        String url = configModel.getApiBaseUrl() + CardConstants.TELECOM_ACCESS_NUMBER_QUERY;
        Map<String,String> params = new HashMap<>(2);
        params.put("iccid",  iccid);
        Map<String, String> headers = this.getHeaders(params, configModel);
        String result = ForestHttpUtils.get(url, params, headers);
        if (StringUtils.isEmpty(result)) {
            return "";
        }
        Map<String, Object> statusMap = JSON.parseObject(result, new TypeReference<Map<String, Object>>() {});
        String code = statusMap.get("RESULT").toString();
        if (!CardConstants.SUCCESS_STATUS.equals(code)) {
            return "";
        }
        return statusMap.get("SMSG").toString();
    }

    private String getDataPlan(String accessNumber, CardPlatformConfigContent configModel) throws Exception {
        String url = configModel.getApiBaseUrl() + StringUtils.format(CardConstants.TELECOM_COMBO_QUERY, accessNumber);
        Map<String,String> params = new HashMap<>(2);
        params.put("access_number",  accessNumber);
        Map<String, String> headers = this.getHeaders(params, configModel);
        String result = ForestHttpUtils.get(url, params, headers);
        if (StringUtils.isEmpty(result)) {
            return "";
        }
        Map<String, Object> statusMap = JSON.parseObject(result, new TypeReference<Map<String, Object>>() {});
        String code = statusMap.get("code").toString();
        if (CardConstants.SUCCESS_STATUS.equals(code)) {
            String body = statusMap.get("body").toString();
            if (StringUtils.isNotEmpty(body)) {
                Map<String, Object> bodyMap = JSON.parseObject(body, new TypeReference<Map<String, Object>>() {});
                Object prodInstInfo = bodyMap.get("prod_insts_info");
                if (Objects.nonNull(prodInstInfo)) {
                    List<Map<String, Object>> productInstInfoMap = JSON.parseObject(prodInstInfo.toString(), new TypeReference<List<Map<String, Object>>>() {
                    });
                    if (CollectionUtils.isNotEmpty(productInstInfoMap)) {
                        Map<String, Object> obj = productInstInfoMap.get(0);
                        return obj.get("prod_name").toString();
                    }
                }
            }
        }
        return "";
    }

    private void getThreeYards(String iccid, CardPlatformConfigContent configModel, Card card) throws Exception {
        Map<String,String> params = new HashMap<>(2);
        params.put("iccid",  iccid);
        Map<String, String> headers = this.getHeaders(params, configModel);
        // 设备识别码查询
        String url1 = configModel.getApiBaseUrl() + CardConstants.TELECOM_DEVICE_IDENTIFY_CODE_QUERY;
        String result1 = ForestHttpUtils.get(url1, params, headers);
        if (StringUtils.isEmpty(result1)) {
            return;
        }
        TelecomCardImeiInfoResult imeiInfoResult = com.alibaba.fastjson2.JSON.parseObject(result1, TelecomCardImeiInfoResult.class);
        if (CardConstants.SUCCESS_STATUS.equals(imeiInfoResult.getResult())) {

            String imei = imeiInfoResult.getImei();
            HashMap<String, String> imeiMap = new HashMap<>(2);
            imeiMap.put("imei", imei);
            Map<String, String> headers2 = this.getHeaders(params, configModel);
            String url2 = configModel.getApiBaseUrl() + CardConstants.TELECOM_THREE_YARDS_QUERY;
            String result2 = ForestHttpUtils.post(url2, headers2, JSONObject.toJSONString(imeiMap));
            TelecomCardInfoResult infoResult = com.alibaba.fastjson2.JSON.parseObject(result2, TelecomCardInfoResult.class);
            if (0 == infoResult.getCode()) {
                TelecomCardInfoResult.DataResult dataResult = infoResult.getData().get(0);
                card.setMsisdn(dataResult.getMsisdn());
                card.setIccid(dataResult.getIccid());
                card.setImsi(dataResult.getImsi());
                card.setImei(imei);
            }
        }
    }

    private Integer getStatus(String accessNumber, CardPlatformConfigContent configModel) throws Exception {
        Map<String,String> params = new HashMap<>(2);
        params.put("access_number",  accessNumber);
        Map<String, String> headers = this.getHeaders(params, configModel);
        String statusResult = ForestHttpUtils.get(configModel.getApiBaseUrl() + CardConstants.TELECOM_STATUS_QUERY, headers, params);
        if (StringUtils.isNotEmpty(statusResult)) {
            Map<String, Object> statusMap = JSON.parseObject(statusResult, new TypeReference<Map<String, Object>>() {});
            String result = statusMap.get("result").toString();
            if (CardConstants.SUCCESS_STATUS.equals(result)) {
                Object productInfo = statusMap.get("productInfo");
                if (Objects.nonNull(productInfo)) {
                    List<Map<String, Object>> productInfoMap = JSON.parseObject(productInfo.toString(), new TypeReference<List<Map<String, Object>>>() {});
                    if (CollectionUtils.isNotEmpty(productInfoMap)) {
                        Map<String, Object> obj = productInfoMap.get(0);
                        String status = obj.get("productMainStatusCd").toString();
                        return this.matchStatus(status);
                    }
                }
            }
        }
        return CardStatusEnum.UNKNOWN.getStatus();
    }

    private Integer matchStatus(String status) {
        switch (status) {
            case "1":
            case "3":
                return CardStatusEnum.PENDING_ACTIVATION.getStatus();
            case "2":
            case "4":
                return CardStatusEnum.NORMAL.getStatus();
            case "5":
                return CardStatusEnum.SHUTDOWN.getStatus();
            default:
                return CardStatusEnum.UNKNOWN.getStatus();
        }
    }

    @Override
    public List<Card> getBatchCardDetail(String iccids, CardPlatformConfigContent configModel) throws Exception {
        return Collections.emptyList();
    }

    @Override
    public TrafficInfoDTO syncTrafficInfo(String iccid, CardPlatformConfigContent configModel) throws Exception {
        Map<String,String> params = new HashMap<>(2);
        params.put("iccid",  iccid);
        Map<String, String> headers = this.getHeaders(params, configModel);
        Map<String, Object> resultMap = null;
        String url = configModel.getApiBaseUrl() + CardConstants.TELECOM_TRAFFIC_QUERY;
        // 电信API实现
        TrafficInfoDTO trafficInfoDTO = new TrafficInfoDTO();
        String result = ForestHttpUtils.get(url, params, headers);
        try {
            log.info(">>Query_DX5G - queryTraffic - :iccid {} | {}<<",iccid, JSON.toJSON(resultMap));
            resultMap =  XmlUtil.xmlToMap(result);
            String status = resultMap.get("iresult").toString();
            Object msg = Objects.nonNull(resultMap.get("smsg")) ? resultMap.get("smsg") : "";
            if (!CardConstants.SUCCESS_STATUS.equals(status)) {
                throw new ServiceException(msg.toString());
            }
            Object totalBytesCnt = resultMap.get("total_bytes_cnt");
            if (Objects.nonNull(totalBytesCnt) && totalBytesCnt.toString().length() > 2) {
                String totalBytesCntStr = totalBytesCnt.toString();
                String useSuffix = totalBytesCntStr.substring(totalBytesCntStr.length() - 2, totalBytesCntStr.length());
                totalBytesCntStr = totalBytesCntStr.substring(0, totalBytesCntStr.length() - 2);
                BigDecimal dataUsed = new BigDecimal(totalBytesCntStr);
                if (useSuffix.contains("KB")) {
                    dataUsed = this.kbChangeMb(dataUsed, BigDecimal.valueOf(1024));
                } else if (useSuffix.contains("GB")) {
                    dataUsed = this.gbChangeMb(dataUsed, BigDecimal.valueOf(1024));
                }
                trafficInfoDTO.setDataUsed(dataUsed);
            }
        } catch (Exception e) {
            log.info(">>Query_DX5G - queryTrafficERROR - :iccid {} | {} | {}<<",iccid, JSON.toJSON(resultMap), e.getMessage());
        }
        trafficInfoDTO.setUnit("MB");
        return trafficInfoDTO;
    }

    @Override
    public Integer syncStatus(String iccid, CardPlatformConfigContent configModel) throws Exception {
        // 查询接入号码
        String accessNumber = this.getAccessNumber(iccid, configModel);
        if (StringUtils.isEmpty(accessNumber)) {
            return null;
        }
        // 查卡主状态查询
        return this.getStatus(accessNumber, configModel);
    }

    private Map<String, String> getHeaders(Map<String, String> params, CardPlatformConfigContent configModel) {
        String timestamp = SignUtil.getCurTimestamp();
        Map<String, String> headers = new HashMap<>(2);
        headers.put("Content-Type", "application/json");
        headers.put("AppKey", configModel.getAppKey());
        headers.put("Timestamp", timestamp);
        String sign = this.getSign(params, configModel.getSecretKey(), timestamp);
        headers.put("Sign", sign);
        return headers;
    }

    private BigDecimal kbChangeMb(BigDecimal divisor, BigDecimal dividend) {
        if (divisor.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return divisor.divide(dividend, 10, RoundingMode.HALF_UP);
    }

    private BigDecimal gbChangeMb(BigDecimal multiplier, BigDecimal multiplicand) {
        if (multiplier.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return multiplier.multiply(multiplicand);
    }

    private String getSign(Map<String, String> map, String secretKey, String timestamp) {
        List<String> list=new ArrayList<>(map.keySet());
        Collections.sort(list);
        StringBuffer sb = new StringBuffer();
        for (String k : list) {
            String v = map.get(k);
            if (sb.length() == 0) {
                sb.append(k).append("=").append(v);
            } else {
                sb.append("&").append(k).append("=").append(v);
            }
        }
        String Sign = "";
        try {
            Sign = SignUtil.signMD5(sb + secretKey + timestamp);
        }catch (Exception e){
        }
        return Sign;
    }

    @Override
    public boolean activateCard(String iccid) throws Exception {
        log.info("激活电信卡: {}", iccid);
        // 实现电信激活逻辑
        return true;
    }

    @Override
    public boolean suspendCard(String iccid) throws Exception {
        log.info("停用电信卡: {}", iccid);
        // 实现电信停用逻辑
        return true;
    }

    @Override
    public boolean resumeCard(String iccid) throws Exception {
        log.info("恢复电信卡: {}", iccid);
        // 实现电信恢复逻辑
        return true;
    }

}
