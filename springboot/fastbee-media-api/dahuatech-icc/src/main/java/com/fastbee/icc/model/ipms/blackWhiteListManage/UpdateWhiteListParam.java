package com.fastbee.icc.model.ipms.blackWhiteListManage;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @className UpdateWhiteListRequest
 * @Author 355079
 * @Date 2024/12/9
 * @Description 下发设备白名单请求参数
 */
@Data
@Slf4j
public class UpdateWhiteListParam {

    /**
     * 车辆id
     */
    private String carId;
    /**
     * 设备编码，多个编码间用半角逗号隔开
     */
    private String deviceCodes;
    /**
     * 有效期开始时间（格式：yyyy-MM-dd）
     */
    private String validStartDay;
    /**
     * 有效期结束时间（格式：yyyy-MM-dd）
     */
    private String validEndDay;
}
