package com.fastbee.isup.sdk.structure;

import static com.fastbee.isup.sdk.service.constant.EHOME_ALARM_TYPE.*;

import com.fastbee.isup.sdk.HIKSDKStructure;

public class NET_EHOME_NOTICE_PICURL extends HIKSDKStructure {
    public int dwSize;
    public byte[] byDeviceID = new byte[MAX_DEVICE_ID_LEN];
    public short wPicType;
    public short wAlarmType;
    public int dwAlarmChan;
    public byte[] byAlarmTime = new byte[MAX_TIME_LEN];
    public int dwCaptureChan;
    public byte[] byPicTime = new byte[MAX_TIME_LEN];
    public byte[] byPicUrl = new byte[MAX_URL_LEN];
    public int dwManualSnapSeq;
    public byte byRetransFlag;
    public byte byTimeDiffH;
    public byte byTimeDiffM;
    public byte[] byRes = new byte[29];
}
