package com.fastbee.jt808.item;

import java.util.List;

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
@Protocol(JT808.定位数据批量上传)
public class T0704 extends JT808Message {

    @Column(length = 2, desc = "数据项个数")
    private int total;
    @Column(length = 1, desc = "位置数据类型：0.正常位置批量汇报 1.盲区补报")
    private int type;
    @Column(lengthUnit = 2, desc = "位置汇报数据项")
    private List<T0200> items;

}
