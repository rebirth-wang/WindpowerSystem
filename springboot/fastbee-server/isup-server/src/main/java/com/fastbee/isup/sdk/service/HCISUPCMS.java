package com.fastbee.isup.sdk.service;

import com.sun.jna.Library;
import com.sun.jna.Pointer;

import com.fastbee.isup.sdk.structure.*;

public interface HCISUPCMS extends Library {

    //初始化，反初始化
    boolean NET_ECMS_Init();

    boolean NET_ECMS_Fini();

    boolean NET_ECMS_SetSDKInitCfg(int enumType, Pointer lpInBuff);

    boolean NET_ECMS_SetSDKLocalCfg(int enumType, Pointer lpInBuff);

    boolean NET_ECMS_SetLogToFile(int iLogLevel, String strLogDir, boolean bAutoDel);

    boolean NET_ECMS_SetDeviceSessionKey(Pointer pDeviceKey);

    boolean  NET_ECMS_ISAPIPassThrough(int lUserID, NET_EHOME_PTXML_PARAM lpParam);

    boolean NET_ECMS_StartGetRealStream(int lUserID, NET_EHOME_PREVIEWINFO_IN pPreviewInfoIn, NET_EHOME_PREVIEWINFO_OUT pPreviewInfoOut); //lUserID由SDK分配的用户ID，由设备注册回调时fDeviceRegisterCallBack返回

    boolean NET_ECMS_StartGetRealStreamV11(int lUserID, NET_EHOME_PREVIEWINFO_IN_V11 pPreviewInfoIn, NET_EHOME_PREVIEWINFO_OUT NET_EHOME_PREVIEWINFO_OUT); //lUserID由SDK分配的用户ID，由设备注册回调时fDeviceRegisterCallBack返回

    boolean NET_ECMS_StartPushRealStream(int lUserID, NET_EHOME_PUSHSTREAM_IN pPushInfoIn, NET_EHOME_PUSHSTREAM_OUT pPushInfoOut);

    boolean NET_ECMS_XMLRemoteControl(int lUserID, NET_EHOME_XML_REMOTE_CTRL_PARAM lpCtrlParam, int dwCtrlSize);

    //开启关闭监听
    int NET_ECMS_StartListen(NET_EHOME_CMS_LISTEN_PARAM lpCMSListenPara);

    boolean NET_ECMS_StopGetRealStream(int lUserID, int lSessionID);

    //获取错误码
    int NET_ECMS_GetLastError();

    boolean NET_ECMS_GetDevConfig(int lUserID, int dwCommand, Pointer lpConfig, int dwConfigSize);

    boolean NET_ECMS_RemoteControl(int lUserID, int dwCommand, NET_EHOME_REMOTE_CTRL_PARAM lpCtrlParam);

    int NET_ECMS_StartFindFile_V11(int lUserID, int lSearchType, Pointer pFindCond, int dwCondSize);

    int NET_ECMS_FindNextFile_V11(int lHandle, Pointer pFindData, int dwDataSize);

    boolean NET_ECMS_StopFindFile(int lHandle);

    boolean NET_ECMS_StartPlayBack(int lUserID, NET_EHOME_PLAYBACK_INFO_IN pPlaybackInfoIn, NET_EHOME_PLAYBACK_INFO_OUT pPlaybackInfoOut);

    boolean NET_ECMS_StartPushPlayBack(int lUserID, NET_EHOME_PUSHPLAYBACK_IN struPushPlayBackIn, NET_EHOME_PUSHPLAYBACK_OUT struPushPlayBackOut);

    boolean NET_ECMS_StopPlayBack(int lUserID, int lSessionID);

    boolean NET_ECMS_StartVoiceWithStmServer(int lUserID, NET_EHOME_VOICE_TALK_IN lpVoiceTalkIn, NET_EHOME_VOICE_TALK_OUT lpVoiceTalkOut);

    boolean NET_ECMS_StartPushVoiceStream(int lUserID, NET_EHOME_PUSHVOICE_IN lpPushParamIn, NET_EHOME_PUSHVOICE_OUT lpPushParamOut);

    boolean NET_ECMS_StopVoiceTalkWithStmServer(int lUserID, int lSessionID);

}
