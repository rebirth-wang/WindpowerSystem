//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.utils.modbus;

public class BitUtils {
    public static int getBitFlag(long num, int bit) {
        return (int)num >> bit & 1;
    }

    public static long updateBitValue(long num, int bit, boolean flagValue) {
        return flagValue ? num | (long)(1 << bit) : num ^ (long)(getBitFlag(num, bit) << bit);
    }

    public static String toBinaryString(long num) {
        return Long.toBinaryString(num);
    }

    public static int deter(int num, int i) {
        ++i;
        return num >> i - 1 & 1;
    }

    public static int deterHex(String hex, int i) {
        return deter(Integer.parseInt(hex, 16), i);
    }

    public static void main(String[] args) {
        int var1 = deter(7, 0);
        int var2 = deterHex("10", 4);
        System.out.println(var1);
        System.out.println(var2);
    }
}
