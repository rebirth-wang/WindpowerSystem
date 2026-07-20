package com.fastbee.jt808.item;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.fastbee.base.core.model.Response;
import com.fastbee.protocol.base.annotation.Column;
import com.fastbee.protocol.base.annotation.MergeSubClass;
import com.fastbee.protocol.base.annotation.Protocol;

@EqualsAndHashCode(callSuper = true)
@ToString
@Data
@Accessors(chain = true)
@MergeSubClass
@Protocol(JT808.位置信息查询应答)
public class T0201 extends T0200 implements Response {

    @Column(length = 2, desc = "应答流水号")
    private int responseSerialNo;

}
