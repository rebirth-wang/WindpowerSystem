package com.fastbee.isup.sdk.service.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sun.jna.Pointer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.fastbee.isup.sdk.service.IHikISUPStream;
import com.fastbee.isup.sdk.service.PREVIEW_NEWLINK_CB;
import com.fastbee.isup.sdk.structure.NET_EHOME_NEWLINK_CB_MSG;
import com.fastbee.isup.sdk.structure.NET_EHOME_PREVIEW_DATA_CB_PARAM;
import com.fastbee.media.model.VideoSessionInfo;
import com.fastbee.media.service.impl.VideoSessionManager;

/**
 * 实时预览数据回调（预览数据存储到文件）
 */
@Slf4j
@Component
@Profile("isup")
@RequiredArgsConstructor
public class FPREVIEW_NEWLINK_CB_FILE implements PREVIEW_NEWLINK_CB {

    private final IHikISUPStream hikISUPStream;
    @Autowired
    private VideoSessionManager sessionManager;

    // 存储每个预览句柄对应的回调处理器实例
    private final Map<Integer, PreviewStreamHandler> handlerMap = new ConcurrentHashMap<>();

    public boolean invoke(int lPreviewHandle, NET_EHOME_NEWLINK_CB_MSG pNewLinkCBMsg, Pointer pUserData) {
        //预览数据回调参数
        log.info("预览数据回调参数 lPreviewHandle: {}", lPreviewHandle);
        Integer iSessionID = pNewLinkCBMsg.iSessionID;
        // 获取会话信息
        VideoSessionInfo info = sessionManager.getSessionInfoBySSRC(iSessionID.toString());
        if (info != null && info.getPreviewHandle() == null) {
            // 更新下预览句柄
            info.setPreviewHandle(lPreviewHandle);
            sessionManager.putBySessionId(info);
        }
        // 为每个预览会话创建独立的回调处理器实例
        PreviewStreamHandler previewStreamHandler = handlerMap.computeIfAbsent(lPreviewHandle, handle -> {
            log.info("创建新的PreviewStreamHandler实例，句柄: {}", handle);
            return new PreviewStreamHandler(info);
        });
        NET_EHOME_PREVIEW_DATA_CB_PARAM struDataCB = new NET_EHOME_PREVIEW_DATA_CB_PARAM();
        struDataCB.fnPreviewDataCB = previewStreamHandler;
        if (!this.hikISUPStream.NET_ESTREAM_SetPreviewDataCB(lPreviewHandle, struDataCB)) {
            log.info("NET_ESTREAM_SetPreviewDataCB failed err: {}", this.hikISUPStream.NET_ESTREAM_GetLastError());
            return false;
        }
        return true;
    }

    /**
     * 关闭指定句柄的预览流处理器
     *
     * @param lPreviewHandle 预览句柄
     */
    public void closePreviewHandler(int lPreviewHandle) {
        PreviewStreamHandler handler = handlerMap.remove(lPreviewHandle);
        if (handler != null) {
            handler.closeAllConnections();
            log.info("已关闭PreviewStreamHandler，句柄: {}", lPreviewHandle);
        }
    }

    /**
     * 关闭所有预览流处理器
     */
    public void closeAllPreviewHandlers() {
        handlerMap.forEach((handle, handler) -> {
            handler.closeAllConnections();
            log.info("已关闭PreviewStreamHandler，句柄: {}", handle);
        });
        handlerMap.clear();
        log.info("已关闭所有PreviewStreamHandler");
    }
}
