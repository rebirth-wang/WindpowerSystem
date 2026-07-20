package com.fastbee.common.extend.core.domin.mq.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Command {
    /**
     * 寄存器
     */
    private int register;
    private int code;
    /**
     * 从机地址
     */
    private int address;
    private int quantity;

    //写操作增加
    private int value; // 寄存器值
    private boolean coilValue; //线圈值
}
