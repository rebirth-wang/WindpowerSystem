package com.fastbee.isup.sdk.structure;

import static com.fastbee.isup.sdk.service.constant.EHOME_ALARM_TYPE.MAX_DEVICE_ID_LEN;

import com.fastbee.isup.sdk.HIKSDKStructure;

public class NET_EHOME_NOTIFY_FAIL_INFO extends HIKSDKStructure {
    public int dwSize;
    public byte[] byDeviceID = new byte[MAX_DEVICE_ID_LEN];
    public short wFailedCommand;
    public short wPicType;
    public int dwManualSnapSeq;
    public byte byRetransFlag;
    public byte[] byRes = new byte[31];
}
