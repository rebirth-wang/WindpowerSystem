package com.fastbee.jt808.item;

import lombok.Data;
import lombok.ToString;

/**
 * 报警标志位解析
 */
@Data
@ToString
public class WarningMark {
    /** 1:紧急报警（SOS报警） */
    private boolean sos;
    /** 1：超速报警 */
    private boolean overSpeed;
    /** 1：疲劳驾驶 */
    private boolean fatigueDriving;
    /** 1：终端主电源欠压 */
    private boolean mainPowerUnderVoltage;
    /** 1：主电源断开报警 */
    private boolean mainPowerDisconnected;
    /** 电池低电报警（无线设备） */
    private boolean batteryLow;
    /** 震动报警 */
    private boolean vibration;
    /** 拆除报警 */
    private boolean removal;
    /** 1：超时停车 */
    private boolean timeoutParking;
    /** 1：车辆非法位移 */
    private boolean illegalMovement;

    private int rawValue;
}
