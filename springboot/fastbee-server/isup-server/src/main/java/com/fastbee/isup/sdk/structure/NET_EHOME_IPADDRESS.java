package com.fastbee.isup.sdk.structure;

import com.fastbee.isup.sdk.HIKSDKStructure;

public class NET_EHOME_IPADDRESS extends HIKSDKStructure {
    public byte[] szIP = new byte[128];
    public short wPort;     //端口
    public byte[] byRes = new byte[2];
}
