package com.fastbee.common.extend.core.card;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

/**
 * 智宇卡信息结果
 *
 * @author fastbee
 * @date 2025/12/18
 */
@Data
public class ZhiYuCardInfoResult {

    private Integer code;
    private String msg;
    private List<CardInfo> data;

    @Data
    public static class CardInfo {
        /**
         * 激活时间
         */
        private String activeTime;
        /**
         * 到期时间
         */
        private String cardEndTime;
        /**
         * 开始计费时间
         */
        private String cardFeeTime;
        /**
         * 卡状态
         */
        private Integer cardStatus;
        /**
         * iccid
         */
        private String iccid;
        /**
         * imsi
         */
        private String imsi;
        /**
         * 剩余周期
         */
        private Integer leftPeriod;
        private String msisdn;
        /**
         * 卡套餐可使用流量（M）
         */
        private BigDecimal packageCanUsage;
        /**
         * 套餐已使用流量(M)
         */
        private BigDecimal packageHasUsage;
        /**
         * 套餐ID
         */
        private Integer packageId;
        /**
         * 套餐名称
         */
        private String packageName;
        /**
         * 本期结束时间
         */
        private String periodEndTime;
        /**
         * 本期开始时间
         */
        private String periodStartTime;
        /**
         * 发卡时间
         */
        private String sendCardTime;
        private BigDecimal renewPrice;
    }
}
