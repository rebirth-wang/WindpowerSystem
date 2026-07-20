package com.fastbee.jsongateway.model;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

/**
 * @author fastbee
 * @date 2025年05月14日 15:58
 */
@Data
public class JsonGatewayPoint {

    /** 子设备编号 */
    private String serialNumber;

    /** 物模型数据 */
    private JSONObject properties;
}
