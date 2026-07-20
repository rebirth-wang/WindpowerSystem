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
@Protocol(JT808.查询指定终端参数)
public class T8106 extends JT808Message {

    @Column(totalUnit = 1, desc = "参数ID列表")
    private int[] id;

}
