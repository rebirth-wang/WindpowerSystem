package com.fastbee.ezviz.model;

import lombok.Data;

/**
 * 萤石云录像记录信息
 *
 * @author fastbee
 */
@Data
public class EzvizRecordInfo {

    /** 设备序列号 */
    private String deviceSerial;

    /** 通道号 */
    private Integer channelNo;

    /** 录像开始时间（毫秒时间戳） */
    private Long startTime;

    /** 录像结束时间（毫秒时间戳） */
    private Long stopTime;

    /** 录像类型：1-全时录像，2-移动侦测，3-告警触发，4-定时录像 */
    private Integer recordType;

    /** 录像大小（字节） */
    private Long size;

    /** 录像文件 ID */
    private String fileId;

}
