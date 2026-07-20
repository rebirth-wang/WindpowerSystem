package com.fastbee.isup.sdk.service;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

import com.fastbee.isup.sdk.structure.NET_EHOME_NEWLINK_CB_MSG;

public interface PREVIEW_NEWLINK_CB extends Callback {
    boolean invoke(int lLinkHandle, NET_EHOME_NEWLINK_CB_MSG pNewLinkCBMsg, Pointer pUserData);
}
