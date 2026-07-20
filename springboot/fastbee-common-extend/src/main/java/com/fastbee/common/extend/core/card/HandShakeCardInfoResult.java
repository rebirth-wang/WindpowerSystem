package com.fastbee.common.extend.core.card;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 握手卡片信息结果
 *
 * @author fastbee
 * @date 2025/12/19
 */
@Data
public class HandShakeCardInfoResult {

    private Integer status;
    private String msg;
    private String code;
    private CardInfo data;

    @Data
    public static class CardInfo {
        private String msisdn;
        private String iccid;
        private String imsi;
        /**
         * 运营商
         */
        @JsonProperty("card_yys")
        private String cardYys;
        /**
         * 套餐名称
         */
        @JsonProperty("cate_name")
        private String cateName;
        /**
         * 激活时间
         */
        @JsonProperty("activate_time")
        private String activateTime;
        /**
         * 到期时间
         */
        @JsonProperty("expire_time")
        private String expireTime;
        /**
         * 卡状态 00 正常 01 单向停机 02 停机 03 预销户 07 待激活
         */
        @JsonProperty("card_status")
        private String cardStatus;
        /**
         * 套餐总流量
         */
        @JsonProperty("gprs_all")
        private BigDecimal gprsAll;
        /**
         * 已用流量
         */
        @JsonProperty("gprs_use")
        private BigDecimal gprsUse;
    }
}
