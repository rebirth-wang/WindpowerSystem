package com.fastbee.common.extend.core.card;

import java.util.List;

import lombok.Data;

/**
 * unicom联通卡基础信息结果
 *
 * @author fastbee
 * @date 2025/11/26
 */
@Data
public class UnicomCardInfoResult {

    private String message;

    private String status;

    private DataParams data;

    @Data
    public static class DataParams {
        private String messageId;
        private String resultCode;
        private String version;
        private String timestamp;
        private String resultDesc;
        private List<terminal> terminals;
    }

    @Data
    public static class terminal {
        private String iccid;
        private String deviceId;
        private String ratePlan;

        /**
         * SIM卡状态: "0": 可测试, "1": 可激活,
         * "2": 已激活,"3": 已停用, "4": 已失效,
         * "5"": 已清除, "6": 已更换,"7": 库存,
         * "8": 开始, "9": 预清除。
         */
        private String simStatus;
        /**
         * 本月用量（MB）
         */
        private String monthToDateUsage;
        /**
         * 激活日期
         */
        private String dateActivated;
        /**
         * 本月数据用量（MB）
         */
        private String monthToDateDataUsage;
        /**
         * imsi
         */
        private String imsi;
        /**
         * msisdn
         */
        private String msisdn;
        /**
         * IMEI
         */
        private String imei;
    }

}
