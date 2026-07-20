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
public class UnicomCardQueryParams {

    private String app_id;
    private String timestamp;
    private String trans_id;
    private String token;
    private DataParams data;

    @Data
    public static class DataParams {
        private String messageId;
        private String openId;
        private String version;
        private List<String> iccids;
    }

}
