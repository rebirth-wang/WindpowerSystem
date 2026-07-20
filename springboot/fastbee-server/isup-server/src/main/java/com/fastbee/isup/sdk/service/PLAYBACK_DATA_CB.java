package com.fastbee.isup.sdk.service;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

import com.fastbee.isup.sdk.structure.NET_EHOME_PLAYBACK_DATA_CB_INFO;

public interface PLAYBACK_DATA_CB extends Callback {
    boolean invoke(int iPlayBackLinkHandle, NET_EHOME_PLAYBACK_DATA_CB_INFO pDataCBInfo, Pointer pUserData);
}
