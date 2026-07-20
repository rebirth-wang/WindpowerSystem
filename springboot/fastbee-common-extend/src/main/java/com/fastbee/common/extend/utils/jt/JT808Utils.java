package com.fastbee.common.extend.utils.jt;

import io.netty.buffer.ByteBuf;

/**
 * @author gsb
 * @date 2025/10/24 9:04
 */
public class JT808Utils {


    public static int headerLength(int version, boolean isSubpackage) {
        if (version > 0)
            return isSubpackage ? 21 : 17;
        else
            return isSubpackage ? 16 : 12;
    }

    /**
     * BCC校验(异或校验)
     */
    public static byte bcc(ByteBuf byteBuf, int tailOffset) {
        byte cs = 0;
        int readerIndex = byteBuf.readerIndex();
        int writerIndex = byteBuf.writerIndex() + tailOffset;
        while (readerIndex < writerIndex)
            cs ^= byteBuf.getByte(readerIndex++);
        return cs;
    }

    /**
     * 判断n的第i位
     * @param n int32
     * @param i 取值范围0~31
     */
    public static boolean isTrue(int n, int i) {
        return ((n >> i) & 1) == 1;
    }

    /**
     * 读取n的第i位
     * @param n int32
     * @param i 取值范围0~31
     */
    public static int get(int n, int i) {
        return (n >> i) & 1;
    }

    /**
     * 设置n的第i位为1
     * @param n int32
     * @param i 取值范围0~31
     */
    public static int set1(int n, int i) {
        return n | (1 << i);
    }

    /**
     * 设置n的第i位为0
     * @param n int32
     * @param i 取值范围0~31
     */
    public static int set0(int n, int i) {
        return n & ~(1 << i);
    }

    /**
     * 写入bool到n的第i位
     * @param n int32
     * @param i 取值范围0~31
     */
    public static int set(int n, int i, boolean bool) {
        return bool ? set1(n, i) : set0(n, i);
    }

    /**
     * 按位写入int
     * 数组第一个int为首位
     */
    public static int writeInt(int... bit) {
        int n = 0;
        for (int i = 0; i < bit.length; i++)
            n = set(n, i, bit[i] > 0);
        return n;
    }

}
