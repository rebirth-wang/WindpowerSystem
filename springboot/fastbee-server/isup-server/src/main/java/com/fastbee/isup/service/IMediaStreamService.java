package com.fastbee.isup.service;

import com.fastbee.isup.model.IsupDevInfo;
import com.fastbee.media.model.RecordList;
import com.fastbee.media.model.StreamInfo;

public interface IMediaStreamService {
    /**
     * 预览视频
     *
     * @param device    设备对象
     * @param channelId 通道号
     */
    StreamInfo preview(IsupDevInfo device, Integer channelId);

    Boolean stopPreview(IsupDevInfo device, Integer channelId);

    RecordList getRecored(IsupDevInfo device, Integer channelId, String startTime, String endTime);

    StreamInfo playbackByTime(IsupDevInfo device, Integer channelId, String startTime, String endTime);

    Boolean stopPlayBackByTime(IsupDevInfo device, Integer channelId, String streamKey);

    void voiceTrans(Integer loginId, String fileFullPath);
}
