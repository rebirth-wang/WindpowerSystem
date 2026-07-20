package com.fastbee.isup.sdk.structure;

import com.fastbee.isup.sdk.HIKSDKStructure;

public class NET_EHOME_HD_TIMING_STATUS_SINGLE extends HIKSDKStructure {
    public int dwHDFreeSpace;  // 硬盘剩余空间，单位：MB
    public short wHDNo;  // 磁盘号
    public byte[] byRes = new byte[6];
}
