package com.fastbee.jt808.item;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.fastbee.jt808.JT808Message;
import com.fastbee.protocol.base.annotation.Column;
import com.fastbee.protocol.base.annotation.Protocol;

@EqualsAndHashCode(callSuper = true)
@ToString
@Data
@Accessors(chain = true)
@Protocol(JT808.AUTHENTICATION)
public class T0102 extends JT808Message {

    /** 终端重连后上报鉴权码 */
    @Column(desc = "鉴权码", version = {-1, 0})
    @Column(lengthUnit = 1, desc = "鉴权码", version = 1)
    private String token;
    @Column(length = 15, desc = "终端IMEI", version = 1)
    private String imei;
    @Column(length = 20, desc = "软件版本号", version = 1)
    private String softwareVersion;

}
