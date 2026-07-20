package com.fastbee.icc.model.accesscontrol.auth;

import java.util.List;

import lombok.Data;

/**
 * program:java-sdk-demo
 *
 * @Author: 355079
 * @Date:2024-04-22 17:30
 * @Description: 按部门新增授权请求参数
 */
@Data
public class DepartmentAuthRequest {
    /** 部门ID */
    private List<Long> deptIds;
    /** 通道编码 */
    private List<String> channelCodes;
    /** 门组ID */
    private List<Long> doorGroupIds;
    /** 时间计划ID */
    private Long timeQuantumId;
}
