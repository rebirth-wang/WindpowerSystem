package com.fastbee.jt808.item;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.fastbee.jt808.JT808Message;
import com.fastbee.jt808.transform.StatusMarkConverter;
import com.fastbee.jt808.transform.WarningMarkConverter;
import com.fastbee.protocol.base.annotation.Column;
import com.fastbee.protocol.base.annotation.Protocol;

@EqualsAndHashCode(callSuper = true)
@ToString
@Data
@Accessors(chain = true)
@Protocol(JT808.LOCALTION)
public class T0200 extends JT808Message {


    @Column(converter = WarningMarkConverter.class, length = 4, desc = "报警标志")
    private WarningMark warningMark;
    @Column(converter = StatusMarkConverter.class, length = 4, desc = "状态")
    private StatusMark statusMark;
    @Column(length = 4, desc = "纬度")
    private int latitude;
    @Column(length = 4, desc = "经度")
    private int longitude;
    @Column(length = 2, desc = "高程(米)")
    private int altitude;
    @Column(length = 2, desc = "速度(1/10公里每小时)")
    private int speed;
    @Column(length = 2, desc = "方向")
    private int direction;
    @Column(length = 6, charset = "BCD", desc = "时间(YYMMDDHHMMSS)")
    private LocalDateTime deviceTime;


    public double getLng() {
        return longitude / 1000000d;
    }

    public double getLat() {
        return latitude / 1000000d;
    }

    public float getSpeedKph() {
        return latitude / 10f;
    }
}
