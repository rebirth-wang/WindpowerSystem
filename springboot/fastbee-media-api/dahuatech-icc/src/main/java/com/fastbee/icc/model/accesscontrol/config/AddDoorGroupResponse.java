package com.fastbee.icc.model.accesscontrol.config;

import com.dahuatech.icc.oauth.http.IccResponse;
import lombok.Data;

/**
 * program:java-sdk-demo
 *
 * @Author: 355079
 * @Date:2024-04-22 19:52
 * @Description: 添加门组返回参数
 */
@Data
public class AddDoorGroupResponse extends IccResponse {
    /** 门组ID */
    private Long data;
}
