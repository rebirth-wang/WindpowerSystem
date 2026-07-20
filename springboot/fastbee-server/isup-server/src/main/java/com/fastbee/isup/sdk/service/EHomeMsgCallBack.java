package com.fastbee.isup.sdk.service;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

import com.fastbee.isup.sdk.structure.NET_EHOME_ALARM_MSG;

public interface EHomeMsgCallBack extends Callback {
    boolean invoke(int iHandle, NET_EHOME_ALARM_MSG pAlarmMsg, Pointer pUser);
}
