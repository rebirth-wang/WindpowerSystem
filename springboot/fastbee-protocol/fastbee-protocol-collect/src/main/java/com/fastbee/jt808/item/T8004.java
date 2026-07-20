package com.fastbee.jt808.item;

import java.time.LocalDateTime;

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
@Protocol(JT808.查询服务器时间应答)
public class T8004 extends JT808Message {

    @Column(length = 6, charset = "BCD", desc = "UTC时间")
    private LocalDateTime dateTime;

}
