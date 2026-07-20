package com.fastbee.isup.sdk.structure;

import com.sun.jna.Pointer;

import com.fastbee.isup.sdk.HIKSDKStructure;
import com.fastbee.isup.sdk.service.PLAYBACK_DATA_CB;

public class NET_EHOME_PLAYBACK_DATA_CB_PARAM extends HIKSDKStructure {
    public PLAYBACK_DATA_CB fnPlayBackDataCB;   //数据回调函数
    public Pointer pUserData;          //用户参数, 在fnPlayBackDataCB回调出来
    public byte byStreamFormat;     //码流封装格式：0-PS 1-RTP
    public byte[] byRes = new byte[127];         //保留
}
