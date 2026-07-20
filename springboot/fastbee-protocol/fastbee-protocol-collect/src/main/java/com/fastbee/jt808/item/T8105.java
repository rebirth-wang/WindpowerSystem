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
@Protocol(JT808.终端控制)
public class T8105 extends JT808Message {

    @Column(length = 1, desc = "命令字：" +
            " 1.无线升级" +
            " 2.控制终端连接指定服务器" +
            " 3.终端关机" +
            " 4.终端复位" +
            " 5.终端恢复出厂设置" +
            " 6.关闭数据通信" +
            " 7.关闭所有无线通信")
    private int command;
    @Column(desc = "命令参数")
    private String parameter;

}
