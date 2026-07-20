package com.fastbee.isup.sdk.service;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

import com.fastbee.isup.sdk.structure.NET_EHOME_PREVIEW_CB_MSG;

public interface PREVIEW_DATA_CB extends Callback {
    void invoke(int iPreviewHandle, NET_EHOME_PREVIEW_CB_MSG pPreviewCBMsg, Pointer pUserData);
}
