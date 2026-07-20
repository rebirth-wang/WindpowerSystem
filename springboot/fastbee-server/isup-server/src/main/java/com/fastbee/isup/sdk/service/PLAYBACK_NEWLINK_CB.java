package com.fastbee.isup.sdk.service;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

import com.fastbee.isup.sdk.structure.NET_EHOME_PLAYBACK_NEWLINK_CB_INFO;

public interface PLAYBACK_NEWLINK_CB extends Callback {
    boolean invoke(int lPlayBackLinkHandle, NET_EHOME_PLAYBACK_NEWLINK_CB_INFO pNewLinkCBMsg, Pointer pUserData);
}
