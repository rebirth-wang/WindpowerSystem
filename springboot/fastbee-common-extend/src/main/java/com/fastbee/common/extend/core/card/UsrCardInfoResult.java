package com.fastbee.common.extend.core.card;

import java.math.BigDecimal;

import lombok.Data;

/**
 * 有人云卡基础信息结果
 *
 * @author fastbee
 * @date 2025/12/17
 */
@Data
public class UsrCardInfoResult {

    private Integer status;
    private String desc;
    private CardInfo data;

    @Data
    public static class CardInfo {
        private String token;

        private String iccid;
        private String msisdn;
        private Integer cardStatusId;
        /**
         * 卡片状态
         */
        private String cardStatusContent;
        /**
         * 	运营商名称
         */
        private String operatorName;
        /**
         * 	卡总流量（MB）
         */
        private BigDecimal totalFlow;
        /**
         * 卡已使用流量（MB）
         */
        private BigDecimal useFlow;
        /**
         * 	卡剩余流量（MB）
         */
        private BigDecimal surplusFlow;
        private String deliverGoodsNum;
        private Integer cardAuthStatus;
        private Integer cardCompliance;
        private String cardEndDate;
    }
}
