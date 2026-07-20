package com.fastbee.common.extend.core.card;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

/**
 * unicom联通卡基础信息结果
 *
 * @author fastbee
 * @date 2025/11/26
 */
@Data
public class TelecomCardImeiInfoResult {

    @JSONField(name = "IMEI")
    private String imei;

    @JSONField(name = "RESULT")
    private String result;

    @JSONField(name = "MESSAGE")
    private String message;

}
