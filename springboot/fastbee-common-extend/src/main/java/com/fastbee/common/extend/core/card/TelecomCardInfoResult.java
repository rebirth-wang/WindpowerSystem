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
public class TelecomCardInfoResult {

    private Integer code;

    private String message;

    private List<DataResult> data;

    @Data
    public static class DataResult {

        private String msisdn;

        private String iccid;

        private String imsi;
    }
}
