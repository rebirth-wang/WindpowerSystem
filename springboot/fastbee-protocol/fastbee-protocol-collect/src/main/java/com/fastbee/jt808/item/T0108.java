package com.fastbee.jt808.item;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.fastbee.jt808.JT808Message;
import com.fastbee.protocol.base.annotation.Column;
import com.fastbee.protocol.base.annotation.Protocol;

@ToString
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Protocol(JT808.UPGRADE_RESULT)
public class T0108 extends JT808Message {

    /** 终端 */
    public static final int Terminal = 0;
    /** 道路运输证IC卡 读卡器 */
    public static final int CardReader = 12;
    /** 北斗卫星定位模块 */
    public static final int Beidou = 52;

    @Column(length = 1, desc = "升级类型：0.终端 12.道路运输证IC卡读卡器 52.北斗卫星定位模块")
    private int type;
    @Column(length = 1, desc = "升级结果：0.成功 1.失败 2.取消")
    private int result;

}
