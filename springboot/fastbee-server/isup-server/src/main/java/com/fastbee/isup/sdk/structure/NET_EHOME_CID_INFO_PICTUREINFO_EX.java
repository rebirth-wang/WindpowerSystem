package com.fastbee.isup.sdk.structure;

import static com.fastbee.isup.sdk.service.constant.EHOME_ALARM_TYPE.MAX_PICTURE_NUM;
import static com.fastbee.isup.sdk.service.constant.EHOME_ALARM_TYPE.MAX_URL_LEN;

import com.fastbee.isup.sdk.HIKSDKStructure;

public class NET_EHOME_CID_INFO_PICTUREINFO_EX extends HIKSDKStructure {
    public byte[][] byPictureURL = new byte[MAX_PICTURE_NUM][MAX_URL_LEN];//图片URL
    public byte[] byRes1 = new byte[512];
}
