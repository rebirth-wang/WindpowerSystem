package com.fastbee.isup.sdk.service.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sun.jna.Pointer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.fastbee.isup.sdk.service.IHikISUPStream;
import com.fastbee.isup.sdk.service.PLAYBACK_NEWLINK_CB;
import com.fastbee.isup.sdk.structure.NET_EHOME_PLAYBACK_DATA_CB_PARAM;
import com.fastbee.isup.sdk.structure.NET_EHOME_PLAYBACK_NEWLINK_CB_INFO;
import com.fastbee.media.model.VideoSessionInfo;
import com.fastbee.media.service.impl.VideoSessionManager;

@Slf4j
@Service("playbackNewlinkCallbackHandler")
@Profile("isup")
@RequiredArgsConstructor
public class PlaybackNewlinkCallbackHandler implements PLAYBACK_NEWLINK_CB {

    private final IHikISUPStream hikISUPStream;
    @Autowired
    private VideoSessionManager sessionManager;

    // 存储每个预览句柄对应的回调处理器实例
    private final Map<Integer, PlaybackDataCallback> handlerMap = new ConcurrentHashMap<>();

    public boolean invoke(int lPlayBackLinkHandle, NET_EHOME_PLAYBACK_NEWLINK_CB_INFO pNewLinkCBInfo, Pointer pUserData) {
        pNewLinkCBInfo.read();
        Integer lSessionID = pNewLinkCBInfo.lSessionID;
        log.info("PLAYBACK_NEWLINK_CB callback, szDeviceID: {},lSessionID: {},dwChannelNo: {}",
                new String(pNewLinkCBInfo.szDeviceID).trim(),
                lSessionID,
                pNewLinkCBInfo.dwChannelNo);
        // 获取会话信息
        VideoSessionInfo info = sessionManager.getSessionInfoBySSRC(lSessionID.toString());
        if (info != null && info.getPreviewHandle() == null) {
            // 更新下预览句柄
            info.setPreviewHandle(lPlayBackLinkHandle);
            sessionManager.putBySessionId(info);
        }

        // 为每个预览会话创建独立的回调处理器实例
        PlaybackDataCallback playbackDataCallback = handlerMap.computeIfAbsent(lPlayBackLinkHandle, handle -> {
            log.info("创建新的PreviewStreamHandler实例，句柄: {}", handle);
            return new PlaybackDataCallback(info);
        });

        NET_EHOME_PLAYBACK_DATA_CB_PARAM struCBParam = new NET_EHOME_PLAYBACK_DATA_CB_PARAM();
        //预览数据回调参数
        struCBParam.fnPlayBackDataCB = playbackDataCallback;
        struCBParam.byStreamFormat = 0;
        struCBParam.write();
        if (!hikISUPStream.NET_ESTREAM_SetPlayBackDataCB(lPlayBackLinkHandle, struCBParam)) {
            System.out.println("NET_ESTREAM_SetPlayBackDataCB failed");
            return false;
        }
        return true;
    }
}
