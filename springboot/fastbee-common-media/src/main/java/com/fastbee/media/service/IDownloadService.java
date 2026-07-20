package com.fastbee.media.service;

import com.fastbee.media.domain.CommonChannel;

/**
 * 资源能力接入-录像下载
 */
public interface IDownloadService {

    void download(CommonChannel channel, Long startTime, Long stopTime, Integer downloadSpeed);

    void stopDownload(CommonChannel channel, String stream);
}
