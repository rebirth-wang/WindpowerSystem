package com.fastbee.isup.sdk.structure;

import com.fastbee.isup.sdk.HIKSDKStructure;

public class NET_EHOME_BLACKLIST_SEVER extends HIKSDKStructure {
    public NET_EHOME_IPADDRESS struAdd = new NET_EHOME_IPADDRESS();
    public byte[] byServerName = new byte[32];
    public byte[] byUserName = new byte[32];
    public byte[] byPassWord = new byte[32];
    public byte[] byRes = new byte[64];
}
