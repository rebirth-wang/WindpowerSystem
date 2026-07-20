package com.fastbee.isup.sdk.structure;

import com.fastbee.isup.sdk.HIKSDKStructure;

public class NET_EHOME_REC_FILE extends HIKSDKStructure {
    public int dwSize;
    public byte[] sFileName = new byte[100];
    public NET_EHOME_TIME struStartTime = new NET_EHOME_TIME();
    public NET_EHOME_TIME struStopTime = new NET_EHOME_TIME();
    public int dwFileSize;
    public int dwFileMainType;
    public int dwFileSubType;
    public int dwFileIndex;
    public byte[] byRes = new byte[128];

    /** 开始时间毫秒时间戳 */
    public long getStartTimestamp() {
        return struStartTime.toTimestamp();
    }

    /** 结束时间毫秒时间戳 */
    public long getStopTimestamp() {
        return struStopTime.toTimestamp();
    }
}
