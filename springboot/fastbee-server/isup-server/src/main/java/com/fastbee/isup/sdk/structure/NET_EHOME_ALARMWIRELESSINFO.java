package com.fastbee.isup.sdk.structure;

import static com.fastbee.isup.sdk.service.constant.EHOME_ALARM_TYPE.MAX_DEVICE_ID_LEN;

import com.fastbee.isup.sdk.HIKSDKStructure;

/**
 * 车载客流统计的定位信息
 */
public class NET_EHOME_ALARMWIRELESSINFO extends HIKSDKStructure {
    public byte[] byDeviceID = new byte[MAX_DEVICE_ID_LEN];
    public int dwDataTraffic;
    public byte bySignalIntensity;
    public byte[] byRes = new byte[127];
}
