package com.fastbee.ezviz.model;

import lombok.Data;

/**
 * 萤石云摄像头通道信息
 *
 * @author fastbee
 */
@Data
public class EzvizCameraInfo {

    /** 设备序列号 */
    private String deviceSerial;

    /** 通道号，从 1 开始 */
    private Integer channelNo;

    /** 通道名称 */
    private String channelName;

    /** 通道状态：0-离线，1-在线 */
    private Integer status;

    /** 是否支持云台：0-不支持，1-支持 */
    private Integer supportPtz;

    /** 是否支持对讲：0-不支持，1-支持 */
    private Integer supportTalk;

    /** 视频质量 */
    private Integer videoQuality;

    /** 是否加密：0-未加密，1-加密 */
    private Integer isEncrypt;

    /** 设备纬度 */
    private Double latitude;

    /** 设备经度 */
    private Double longitude;

}
