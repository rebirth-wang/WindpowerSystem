package com.fastbee.ezviz.model;

import lombok.Data;

/**
 * 萤石云设备信息
 *
 * @author fastbee
 */
@Data
public class EzvizDeviceInfo {

    /** 设备序列号 */
    private String deviceSerial;

    /** 设备名称 */
    private String deviceName;

    /** 设备型号 */
    private String deviceType;

    /** 设备版本信息 */
    private String version;

    /** 设备状态：0-离线，1-在线 */
    private Integer status;

    /** 设备防护状态：0-睡眠，8-在家，16-外出 */
    private Integer defence;

    /** 告警声音模式 */
    private Integer alarmSoundMode;

    /** 是否加密：0-未加密，1-加密 */
    private Integer isEncrypt;

    /** 通道数量 */
    private Integer channelNumber;

    /** 通道列表 */
    private java.util.List<EzvizCameraInfo> cameraList;

}
