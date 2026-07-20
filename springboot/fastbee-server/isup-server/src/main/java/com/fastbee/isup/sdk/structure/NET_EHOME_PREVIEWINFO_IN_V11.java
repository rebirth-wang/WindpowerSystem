package com.fastbee.isup.sdk.structure;

import com.fastbee.isup.sdk.HIKSDKStructure;

public class NET_EHOME_PREVIEWINFO_IN_V11 extends HIKSDKStructure {
    public int iChannel;
    public int dwStreamType;
    public int dwLinkMode;
    public NET_EHOME_IPADDRESS struStreamSever;
    public byte byDelayPreview;
    public byte[] byRes = new byte[31];
}
