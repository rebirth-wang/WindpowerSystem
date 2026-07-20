package com.fastbee.common.extend.utils;

/**
 * BCD编码工具类
 */
public class BCD {

    /** 将BCD字节数组转换为字符串 */
    public static String toString(byte[] bytes) {
        if (bytes == null)
            return null;
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append((b >> 4) & 0x0F);
            sb.append(b & 0x0F);
        }
        return sb.toString();
    }

    /** 将字符串转换为BCD字节数组 */
    public static byte[] toBytes(String value) {
        return toBytes(value, -1);
    }

    /** 将字符串转换为指定长度的BCD字节数组 */
    public static byte[] toBytes(String value, int length) {
        if (value == null)
            return null;

        String str = value.trim();
        int len = str.length();
        if (length > 0) {
            if (len > length * 2)
                str = str.substring(0, length * 2);
        } else {
            length = (len + 1) / 2;
        }

        byte[] bytes = new byte[length];
        for (int i = 0, j = 0; i < len; i++) {
            int digit = Character.digit(str.charAt(i), 10);
            if (digit < 0)
                throw new IllegalArgumentException("Invalid BCD digit: " + str.charAt(i));

            if ((i & 1) == 0) {
                bytes[j] = (byte) (digit << 4);
            } else {
                bytes[j] |= (byte) digit;
                j++;
            }
        }
        return bytes;
    }

    /** 将BCD字节转换为整数 */
    public static int toInt(byte b) {
        return ((b >> 4) & 0x0F) * 10 + (b & 0x0F);
    }

    /** 将整数转换为BCD字节 */
    public static byte toByte(int value) {
        if (value < 0 || value > 99)
            throw new IllegalArgumentException("Value must be between 0 and 99");
        return (byte) (((value / 10) << 4) | (value % 10));
    }
}
