package com.fastbee.isup.sdk.service;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

import com.fastbee.isup.sdk.structure.NET_EHOME_VOICETALK_DATA_CB_INFO;

public interface VOICETALK_DATA_CB extends Callback {
    boolean invoke(int lHandle, NET_EHOME_VOICETALK_DATA_CB_INFO pNewLinkCBInfo, Pointer pUserData);
}
