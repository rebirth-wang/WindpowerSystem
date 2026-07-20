package com.fastbee.iot.model.dto;

import java.math.BigDecimal;

import lombok.Data;

/**
 * @author zzy
 * @description: 物联网卡流量统计类
 * @date 2025-11-13 11:59
 */
@Data
public class TrafficInfoDTO {

    private String iccid;
    private BigDecimal totalData;
    private BigDecimal dataUsed;
    private BigDecimal dataRemaining;
    private String dataPlan;
    // mb\gb
    private String unit;
    private String updateTime;
}
