package com.fastbee.iot.domain;

import lombok.Data;

@Data
public class IotDBDeviceLog
{

    private Long time;

    private String serialNumber;

    private Integer logType;

    private String logValue;

    private Long deviceId;

    private String deviceName;

    private String identify;

    private String createBy;

    private Integer isMonitor;

    private Integer mode;

    private String remark;

    private Long tenantId;

}
