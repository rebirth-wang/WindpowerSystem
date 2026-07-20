//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.enums;

import java.util.Objects;

public enum ModbusDataType {
    U_SHORT("ushort", "16位 无符号"),
    SHORT("short", "16位 有符号"),
    LONG_ABCD("long-ABCD", "32位 有符号(ABCD)"),
    LONG_CDAB("long-CDAB", "32位 有符号(CDAB)"),
    U_LONG_ABCD("ulong-ABCD", "32位 无符号(ABCD)"),
    U_LONG_CDAB("ulong-CDAB", "32位 无符号(CDAB)"),
    FLOAT_ABCD("float-ABCD", "32位 浮点数(ABCD)"),
    FLOAT_CDAB("float-CDAB", "32位 浮点数(CDAB)"),
    HEX("hex", "16进制"),
    BIT("bit", "位");

    String type;
    String msg;

    public static ModbusDataType convert(String type) {
        for(ModbusDataType var4 : values()) {
            if (Objects.equals(var4.type, type)) {
                return var4;
            }
        }

        return U_SHORT;
    }

    public String getType() {
        return this.type;
    }

    public String getMsg() {
        return this.msg;
    }

    private ModbusDataType(String type, String msg) {
        this.type = type;
        this.msg = msg;
    }
}
