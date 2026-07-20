package com.fastbee.common.extend.core.domin.notify.msg;

import lombok.Data;

/**
 * @author admin
 * @version 1.0
 * @description: MQTT推送参数
 * @date 2024-12-30 10:41
 */
@Data
public class HttpMsgParams {

    private String method;
    private String hostUrl;
    private String requestHeaders;
    private String requestParams;
    private String requestBody;
}
