package com.fastbee.common.extend.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * modbus任务指令枚举
 * @author zzy
 * @date 2024/9/5 16:04
 */
@Getter
@AllArgsConstructor
public enum ModbusJobCommantEnum {

    POLLING(1,"轮询指令"),
    SEND(2,"下发指令"),;

    Integer type;
    String desc;
}
