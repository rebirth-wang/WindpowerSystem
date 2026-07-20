package com.fastbee.jt808.item;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.fastbee.base.core.model.Response;
import com.fastbee.jt808.JT808Message;
import com.fastbee.protocol.base.annotation.Column;
import com.fastbee.protocol.base.annotation.Protocol;

@ToString
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Protocol({JT808.SPLIT_BACKAGE_UP, JT808.终端补传分包请求})
public class T8003 extends JT808Message implements Response {

    @Column(length = 2, desc = "原始消息流水号")
    private int responseSerialNo;
    @Column(totalUnit = 1, desc = "重传包ID列表", version = {-1, 0})
    @Column(totalUnit = 2, desc = "重传包ID列表", version = 1)
    private short[] id;

}
