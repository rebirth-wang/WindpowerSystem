package com.fastbee.isup.sdk.structure;

import com.fastbee.isup.sdk.HIKSDKStructure;

public class NET_EHOME_REC_FILE_COND extends HIKSDKStructure {
    public int dwChannel; // 通道号
    public int dwRecType; // 录像类型：0xff-全部类型，0-定时录像，1-移动报警，2-报警触发，3-报警|动测，4-报警&动测，5-命令触发，6-手动录像，7-震动报警，8-环境报警，9-智能报警（或者取证录像），10（0x0a）-PIR报警，11（0x0b）-无线报警，12（0x0c）-呼救报警，13（0x0d）-全部报警
    public NET_EHOME_TIME struStartTime = new NET_EHOME_TIME(); //开始时间
    public NET_EHOME_TIME struStopTime = new NET_EHOME_TIME(); //结束时间
    public int dwStartIndex;//查询起始位置，从0开始
    public int dwMaxFileCountPer;//单次搜索最大文件个数，最大文件个数，需要确定实际网络环境，建议最大个数为8
    public byte byLocalOrUTC; // 时间类型：0-设备本地时间，即设备OSD时间；1-UTC时间。
    public byte[] byRes = new byte[63];
}
