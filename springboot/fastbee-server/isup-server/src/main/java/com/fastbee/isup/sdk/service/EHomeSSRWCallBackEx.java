package com.fastbee.isup.sdk.service;

import com.sun.jna.Callback;

import com.fastbee.isup.sdk.structure.NET_EHOME_SS_EX_PARAM;
import com.fastbee.isup.sdk.structure.NET_EHOME_SS_RW_PARAM;

public interface EHomeSSRWCallBackEx extends Callback {
    public boolean invoke(int iHandle, NET_EHOME_SS_RW_PARAM pRwParam, NET_EHOME_SS_EX_PARAM pExStruct);
}
