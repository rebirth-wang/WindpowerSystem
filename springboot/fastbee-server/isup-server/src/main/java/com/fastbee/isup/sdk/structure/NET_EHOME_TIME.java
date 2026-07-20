package com.fastbee.isup.sdk.structure;

import java.util.Calendar;

import com.fastbee.isup.sdk.HIKSDKStructure;

public class NET_EHOME_TIME extends HIKSDKStructure {
    public short wYear;//年
    public byte byMonth;//月
    public byte byDay;//日
    public byte byHour;//时
    public byte byMinute;//分
    public byte bySecond;//秒
    public byte byRes1;//保留
    public short wMSecond;//毫秒
    public byte[] byRes2 = new byte[2];

    /**
     * 转换为毫秒时间戳
     */
    public long toTimestamp() {
        Calendar cal = Calendar.getInstance();
        cal.set(wYear, (byMonth & 0xFF) - 1, byDay & 0xFF,
                byHour & 0xFF, byMinute & 0xFF, bySecond & 0xFF);
        cal.set(Calendar.MILLISECOND, wMSecond & 0xFFFF);
        return cal.getTimeInMillis();
    }
}
