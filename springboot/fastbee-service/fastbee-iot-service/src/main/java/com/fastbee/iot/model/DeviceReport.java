package com.fastbee.iot.model;

import java.util.List;

import lombok.Data;

import com.fastbee.iot.domain.DeviceLog;

@Data
public class DeviceReport {
    String serialNumber;
    List<DeviceLog> reportList;
    List<String> unReportList;
}
