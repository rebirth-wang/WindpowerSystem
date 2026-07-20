package com.fastbee.icc.model.ipms.infoQuery;

import com.dahuatech.icc.oauth.http.IccResponse;
import lombok.Data;

/**
 * program:java-sdk-demo
 *
 * @Author: 355079
 * @Date:2024-06-07 10:16
 * @Description: 统计车辆进出总数返回结果
 */
@Data
public class InAndOutRecordCountResponse extends IccResponse {
    /** 车辆进出总数 */
    private Integer data;

}
