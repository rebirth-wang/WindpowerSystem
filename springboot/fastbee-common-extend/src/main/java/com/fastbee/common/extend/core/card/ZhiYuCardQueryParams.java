package com.fastbee.common.extend.core.card;

import lombok.Data;

/**
 * 智宇卡查询参数
 *
 * @author fastbee
 * @date 2025/12/18
 */
@Data
public class ZhiYuCardQueryParams {

    private String appid;

    private String appsecret;

    /**
     * 接口路径
     */
    private String name;

    private String msisdn;

    private String iccid;
}
