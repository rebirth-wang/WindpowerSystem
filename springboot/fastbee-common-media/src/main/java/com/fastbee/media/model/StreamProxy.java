package com.fastbee.media.model;

import lombok.Data;

@Data
public class StreamProxy {
    // @Schema(description = "类型，取值，default： 流媒体直接拉流（默认），ffmpeg： ffmpeg实现拉流")
    private String type;

    // @Schema(description = "应用名")
    private String app;

    // @Schema(description = "流ID")
    private String stream;

    // @Schema(description = "当前拉流使用的流媒体服务ID")
    private String mediaServerId;

    // @Schema(description = "固定选择的流媒体服务ID")
    private String relatesMediaServerId;

    // @Schema(description = "服务ID")
    private String serverId;

    // @Schema(description = "拉流地址")
    private String srcUrl;

    // @Schema(description = "超时时间:秒")
    private int timeout;

    // @Schema(description = "ffmpeg模板KEY")
    private String ffmpegCmdKey;

    // @Schema(description = "rtsp拉流时，拉流方式，0：tcp，1：udp，2：组播")
    private int rtspType;

    // @Schema(description = "是否启用")
    private boolean enable;

    // @Schema(description = "是否启用音频")
    private boolean enableAudio;

    // @Schema(description = "是否启用MP4")
    private boolean enableMp4;

    // @Schema(description = "是否 无人观看时删除")
    private boolean enableRemoveNoneReader;

    // @Schema(description = "是否 无人观看时自动停用")
    private boolean enableDisableNoneReader;

    // @Schema(description = "拉流代理时zlm返回的key，用于停止拉流代理")
    private String streamKey;

    // @Schema(description = "拉流状态")
    private Boolean pulling;
}
