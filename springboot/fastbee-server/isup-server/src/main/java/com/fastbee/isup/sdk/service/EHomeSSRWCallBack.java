package com.fastbee.isup.sdk.service;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

public interface EHomeSSRWCallBack extends Callback {
    boolean invoke(int iHandle, byte byAct, String pFileName, Pointer pFileBuf, int dwFileLen, String pFileUrl,
                   Pointer pUser);
}
