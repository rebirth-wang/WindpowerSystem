package com.fastbee.media.client;

import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.http.ForestRequest;

public interface ZlmForestClient {

    @Post("/index/api/getServerConfig")
    ForestRequest<?> getMediaServerConfig();

    @Post("/index/api/setServerConfig")
    ForestRequest<?> setServerConfig();

    @Post("/index/api/restartServer")
    ForestRequest<?> restartServer();

    @Post("/index/api/getMediaList")
    ForestRequest<?> getMediaList();

    @Post("/index/api/getMediaInfo")
    ForestRequest<?> getMediaInfo();

    @Post("/index/api/startRecord")
    ForestRequest<?> startRecord();

    @Post("/index/api/stopRecord")
    ForestRequest<?> stopRecord();

    @Post("/index/api/isRecording")
    ForestRequest<?> isRecording();

    @Post("/index/api/getMP4RecordFile")
    ForestRequest<?> getMP4RecordFile();

    @Post("/index/api/setRecordSpeed")
    ForestRequest<?> setRecordSpeed();

    @Post("/index/api/seekRecordStamp")
    ForestRequest<?> seekRecordStamp();

    @Post("/index/api/getRtpInfo")
    ForestRequest<?> getRtpInfo();

    @Post("/index/api/openRtpServer")
    ForestRequest<?> openRtpServer();

    @Post("/index/api/closeRtpServer")
    ForestRequest<?> closeRtpServer();

    @Post("/index/api/listRtpServer")
    ForestRequest<?> listRtpServer();

    @Post("/index/api/startSendRtp")
    ForestRequest<?> startSendRtp();

    @Post("/index/api/startSendRtpPassive")
    ForestRequest<?> startSendRtpPassive();

    @Post("/index/api/stopSendRtp")
    ForestRequest<?> stopSendRtp();

    @Post("/index/api/getSnap")
    ForestRequest<?> getSnap();

    // 动态添加 rtsp/rtmp/hls/http-ts/http-flv 拉流代理(只支持 H264/H265/aac/G711/opus 负载)
    @Post("/index/api/addStreamProxy")
    ForestRequest<?> addStreamProxy();
    // 关闭拉流代理
    @Post("/index/api/delStreamProxy")
    ForestRequest<?> delStreamProxy();

    // 通过 fork FFmpeg 进程的方式拉流代理，支持任意协议
    @Post("/index/api/addFFmpegSource")
    ForestRequest<?> addFFmpegSource();
    // 关闭 ffmpeg 拉流代理
    @Post("/index/api/delFFmpegSource")
    ForestRequest<?> delFFmpegSource();

    @Post("/index/api/close_streams")
    ForestRequest<?> closeStreams();
}
