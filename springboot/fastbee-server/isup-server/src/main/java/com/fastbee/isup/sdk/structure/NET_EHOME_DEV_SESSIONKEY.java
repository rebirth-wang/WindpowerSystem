package com.fastbee.isup.sdk.structure;

import com.fastbee.isup.sdk.HIKSDKStructure;

public class NET_EHOME_DEV_SESSIONKEY extends HIKSDKStructure {
    public byte[] sDeviceID = new byte[256];        //设备ID/*256*/
    public byte[] sSessionKey = new byte[16];     //设备Sessionkey/*16*/

}
