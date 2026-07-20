package com.fastbee.common.extend.utils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import io.netty.buffer.ByteBuf;

/**
 * ByteBuf工具类
 */
public class ByteBufUtil {

    public static final Charset DEFAULT_CHARSET = StandardCharsets.ISO_8859_1;

    /** 读取剩余字节 */
    public static byte[] readRemain(ByteBuf input) {
        byte[] bytes = new byte[input.readableBytes()];
        input.readBytes(bytes);
        return bytes;
    }

    /** 读取指定长度字节 */
    public static byte[] readBytes(ByteBuf input, int length) {
        byte[] bytes = new byte[length];
        input.readBytes(bytes);
        return bytes;
    }

    /** 读取BCD编码的字符串 */
    public static String readBcd(ByteBuf input, int length) {
        byte[] bytes = new byte[length];
        input.readBytes(bytes);
        return BCD.toString(bytes);
    }

    /** 写入BCD编码的字符串 */
    public static void writeBcd(ByteBuf output, String value, int length) {
        byte[] bytes = BCD.toBytes(value, length);
        output.writeBytes(bytes);
    }

    /** 读取字符串 */
    public static String readString(ByteBuf input, int length, Charset charset) {
        if (length <= 0)
            return null;
        byte[] bytes = new byte[length];
        input.readBytes(bytes);
        return new String(bytes, charset).trim();
    }

    /** 写入字符串 */
    public static void writeString(ByteBuf output, String value, int length, Charset charset) {
        if (value == null)
            value = "";
        byte[] bytes = value.getBytes(charset);
        if (bytes.length > length) {
            output.writeBytes(bytes, 0, length);
        } else {
            output.writeBytes(bytes);
            for (int i = bytes.length; i < length; i++)
                output.writeByte(0);
        }
    }

    /** 读取以0结尾的字符串 */
    public static String readString0(ByteBuf input, Charset charset) {
        if (!input.isReadable())
            return null;
        int len = input.bytesBefore((byte) 0);
        if (len < 0)
            len = input.readableBytes();
        String value = input.readCharSequence(len, charset).toString();
        if (input.isReadable())
            input.skipBytes(1);
        return value;
    }

    /** 写入以0结尾的字符串 */
    public static void writeString0(ByteBuf output, String value, Charset charset) {
        if (value != null && value.length() > 0) {
            output.writeCharSequence(value, charset);
        }
        output.writeByte(0);
    }

    /** 读取整数（大端序） */
    public static long readInt(ByteBuf input, int length, boolean littleEndian) {
        long value = 0;
        if (littleEndian) {
            for (int i = 0; i < length; i++) {
                value |= (long) (input.readByte() & 0xFF) << (8 * i);
            }
        } else {
            for (int i = 0; i < length; i++) {
                value = (value << 8) | (input.readByte() & 0xFF);
            }
        }
        return value;
    }

    /** 写入整数（大端序） */
    public static void writeInt(ByteBuf output, long value, int length, boolean littleEndian) {
        if (littleEndian) {
            for (int i = 0; i < length; i++) {
                output.writeByte((int) (value >> (8 * i)) & 0xFF);
            }
        } else {
            for (int i = length - 1; i >= 0; i--) {
                output.writeByte((int) (value >> (8 * i)) & 0xFF);
            }
        }
    }

    /** 读取32位整数 */
    public static long readInt32(ByteBuf input, boolean littleEndian) {
        return readInt(input, 4, littleEndian);
    }

    /** 写入32位整数 */
    public static void writeInt32(ByteBuf output, long value, boolean littleEndian) {
        writeInt(output, value, 4, littleEndian);
    }

    /** 读取16位整数 */
    public static int readInt16(ByteBuf input, boolean littleEndian) {
        return (int) readInt(input, 2, littleEndian);
    }

    /** 写入16位整数 */
    public static void writeInt16(ByteBuf output, int value, boolean littleEndian) {
        writeInt(output, value, 2, littleEndian);
    }

    /** 跳过指定字节数 */
    public static void skip(ByteBuf input, int length) {
        if (length > 0)
            input.skipBytes(length);
    }

    /** 获取可读字节数 */
    public static int remain(ByteBuf input) {
        return input.readableBytes();
    }

    /** 检查是否有足够字节可读 */
    public static boolean isReadable(ByteBuf input, int length) {
        return input.readableBytes() >= length;
    }

    /** 读取布尔值 */
    public static boolean readBoolean(ByteBuf input) {
        return input.readByte() != 0;
    }

    /** 写入布尔值 */
    public static void writeBoolean(ByteBuf output, boolean value) {
        output.writeByte(value ? 1 : 0);
    }

    /** 读取字节数组（先读长度） */
    public static byte[] readBytes(ByteBuf input) {
        int length = input.readInt();
        if (length <= 0)
            return new byte[0];
        byte[] bytes = new byte[length];
        input.readBytes(bytes);
        return bytes;
    }

    /** 写入字节数组（先写长度） */
    public static void writeBytes(ByteBuf output, byte[] bytes) {
        if (bytes == null) {
            output.writeInt(0);
        } else {
            output.writeInt(bytes.length);
            output.writeBytes(bytes);
        }
    }

    /** 将ByteBuf转为十六进制字符串 */
    public static String toHexString(ByteBuf buf) {
        return toHexString(buf, buf.readerIndex(), buf.readableBytes());
    }

    /** 将ByteBuf指定范围转为十六进制字符串 */
    public static String toHexString(ByteBuf buf, int index, int length) {
        StringBuilder sb = new StringBuilder(length * 2);
        for (int i = index; i < index + length; i++) {
            sb.append(String.format("%02X", buf.getByte(i)));
        }
        return sb.toString();
    }

    /** 从十六进制字符串创建ByteBuf */
    public static ByteBuf fromHexString(String hex) {
        if (hex == null || hex.length() == 0)
            return null;

        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return io.netty.buffer.Unpooled.wrappedBuffer(data);
    }
}
