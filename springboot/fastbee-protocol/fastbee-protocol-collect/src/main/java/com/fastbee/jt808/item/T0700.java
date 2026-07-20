package com.fastbee.jt808.item;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.fastbee.base.core.model.Response;
import com.fastbee.jt808.JT808Message;
import com.fastbee.protocol.base.annotation.Column;
import com.fastbee.protocol.base.annotation.Protocol;

@EqualsAndHashCode(callSuper = true)
@ToString
@Data
@Accessors(chain = true)
@Protocol(JT808.行驶记录数据上传)
public class T0700 extends JT808Message implements Response {

    @Column(length = 2, desc = "应答流水号")
    private int responseSerialNo;
    @Column(length = 1, desc = "命令字")
    private int command;
    @Column(desc = "数据块")
    private byte[] data;

}
