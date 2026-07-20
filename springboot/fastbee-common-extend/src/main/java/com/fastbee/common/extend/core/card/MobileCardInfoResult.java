package com.fastbee.common.extend.core.card;

import java.util.List;

import lombok.Data;

/**
 * @author zzy
 * @description: 中国移动获取token返回结果类
 * @date 2025-11-19 11:17
 */
@Data
public class MobileCardInfoResult {

    private String status;

    private String message;

    private List<CardInfo> result;

    @Data
    public static class CardInfo {

        private String msisdn;
        private String iccid;
        private String imsi;
        /**
         * 激活日期
         */
        private String activeDate;
        /**
         * 开卡时间
         */
        private String openDate;

        /**
         * 状态
         */
        private String status;

        /**
         * 消息
         */
        private String message;

        /**
         * 平台类型
         */
        private String platformType;

        /**
         * 卡状态
         */
        private String cardStatus;

        /**
         * 上次更改日期
         */
        private String lastChangeDate;

        private List<MonthlyData> accmMarginList;
    }

    @Data
    public static class MonthlyData {
        private String offeringId;
        private String offeringName;
        private String totalAmount;
        private String useAmount;
        private String remainAmount;
    }
}
