package com.fastbee.jt808.item;

import lombok.Data;
import lombok.ToString;

/**
 * 状态位解析
 */
@Data
@ToString
public class StatusMark {
    /** 0: ACC关；1:ACC开 */
    private boolean accOn;
    /** 0:未定位；1:定位 */
    private boolean located;
    /** 0:北纬；1:南纬 */
    private boolean southLatitude;
    /** 0:东经；1:西经 */
    private boolean westLongitude;
    /** 0: 撤防 1：设防 */
    private boolean armed;
    /** 油路状态：0:车辆油路正常；1:车辆油路断开 */
    private boolean oilCircuitDisconnected;
    /** 断电状态：0:主电供电正常:1:主电电路断开 */
    private boolean mainPowerDisconnected;
    /** 当定位采用GPS定位标志 */
    private boolean gpsLocated;
    /** 当前定位采用北斗定位标志 */
    private boolean beidouLocated;

    private int rawValue;
}
