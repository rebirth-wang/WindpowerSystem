//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.utils.gateway.protocol;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class ByteUtils {

    public static byte[] create(byte b) {
        return new byte[]{b};
    }

    public static byte[] getBytes(byte data) {
        return new byte[]{data};
    }

    public static byte[] getBytes(short data) {
        byte[] var1 = new byte[]{(byte)(data & 255), (byte)((data & '\uff00') >> 8)};
        return var1;
    }

    public static byte[] getBytesOfReverse(short data) {
        byte[] var1 = new byte[]{(byte)((data & '\uff00') >> 8), (byte)(data & 255)};
        return var1;
    }

    public static short bytesToShort(byte[] bytes, int offset) {
        return (short)(255 & bytes[0 + offset] | '\uff00' & bytes[1 + offset] << 8);
    }

    public static short bytesToShortOfReverse(byte[] bytes) {
        return bytesToShortOfReverse(bytes, 0);
    }

    public static short bytesToShortOfReverse(byte[] bytes, int offset) {
        return (short)(255 & bytes[1 + offset] | '\uff00' & bytes[0 + offset] << 8);
    }

    public static byte[] getBytes(int data) {
        byte[] var1 = new byte[]{(byte)(data & 255), (byte)(data >> 8 & 255), (byte)(data >> 16 & 255), (byte)(data >> 24 & 255)};
        return var1;
    }

    public static byte[] getBytesOfReverse(int data) {
        byte[] var1 = new byte[]{(byte)(data >> 24 & 255), (byte)(data >> 16 & 255), (byte)(data >> 8 & 255), (byte)(data & 255)};
        return var1;
    }

    public static byte[] getBytes(long data) {
        byte[] var2 = new byte[]{(byte)((int)(data & 255L)), (byte)((int)(data >> 8 & 255L)), (byte)((int)(data >> 16 & 255L)), (byte)((int)(data >> 24 & 255L)), (byte)((int)(data >> 32 & 255L)), (byte)((int)(data >> 40 & 255L)), (byte)((int)(data >> 48 & 255L)), (byte)((int)(data >> 56 & 255L))};
        return var2;
    }

    public static byte[] getBytesOfReverse(long data) {
        byte[] var2 = new byte[]{(byte)((int)(data >> 56 & 255L)), (byte)((int)(data >> 48 & 255L)), (byte)((int)(data >> 40 & 255L)), (byte)((int)(data >> 32 & 255L)), (byte)((int)(data >> 24 & 255L)), (byte)((int)(data >> 16 & 255L)), (byte)((int)(data >> 8 & 255L)), (byte)((int)(data & 255L))};
        return var2;
    }

    public static byte[] getBytes(float data) {
        int var1 = Float.floatToIntBits(data);
        return getBytes(var1);
    }

    public static byte[] getBytesOfReverse(float data) {
        int var1 = Float.floatToIntBits(data);
        return getBytesOfReverse(var1);
    }

    public static byte[] getBytes(double data) {
        long var2 = Double.doubleToLongBits(data);
        return getBytes(var2);
    }

    public static byte[] getBytesOfReverse(double data) {
        long var2 = Double.doubleToLongBits(data);
        return getBytesOfReverse(var2);
    }

    public static byte[] getBytes(String data) {
        return data.getBytes(StandardCharsets.UTF_8);
    }

    public static byte[] getBytes(String data, String charsetName) {
        Charset var2 = Charset.forName(charsetName);
        return data.getBytes(var2);
    }

    public static byte[] hexToByte(String hexStr) {
        if (StringUtils.isBlank(hexStr)) {
            return null;
        } else {
            if (hexStr.length() % 2 != 0) {
                hexStr = "0" + hexStr;
            }

            char[] var1 = hexStr.toCharArray();
            int var2 = var1.length / 2;
            byte[] var3 = new byte[var2];

            for(int var4 = 0; var4 < var2; ++var4) {
                int var5 = var4 * 2;
                var3[var4] = (byte)Integer.parseInt(String.valueOf(new char[]{var1[var5], var1[var5 + 1]}), 16);
            }

            return var3;
        }
    }

    public static byte getByte(byte[] src, int offset) {
        return src[offset];
    }

    public static final String bytesToHex(byte[] bArray) {
        StringBuffer var1 = new StringBuffer(bArray.length);

        for(int var3 = 0; var3 < bArray.length; ++var3) {
            String var2 = Integer.toHexString(255 & bArray[var3]);
            if (var2.length() < 2) {
                var1.append(0);
            }

            var1.append(var2.toUpperCase());
        }

        return var1.toString();
    }

    public static final String bytesToHexByFormat(byte[] bArray) {
        StringBuffer var1 = new StringBuffer(bArray.length);

        for(int var3 = 0; var3 < bArray.length; ++var3) {
            String var2 = Integer.toHexString(255 & bArray[var3]);
            if (var2.length() < 2) {
                var1.append(0);
            }

            var1.append(var2.toUpperCase()).append(' ');
        }

        return var1.toString();
    }

    public static final String bytesToHex(byte[] src, int offset, int length) {
        byte[] var3 = ArrayUtils.subarray(src, offset, offset + length);
        return bytesToHex(var3);
    }

    public static final String byteToHex(byte value) {
        String var1 = Integer.toHexString(255 & value);
        return var1.length() == 1 ? "0" + var1 : var1;
    }

    public static final String shortToHex(short value) {
        String var1 = Integer.toHexString(value);
        switch (var1.length()) {
            case 1:
                return "000" + var1;
            case 2:
                return "00" + var1;
            case 3:
                return "0" + var1;
            default:
                return var1;
        }
    }

    public static final String intToHex(int value) {
        StringBuilder var1 = new StringBuilder(Integer.toHexString(value));
        String var2 = var1.toString().replace("f", "");
        if (var2.length() >= 4) {
            return var1.toString().replace("f", "");
        } else {
            for(int var3 = 0; var3 < 4 - var2.length(); ++var3) {
                var1.insert(0, "0");
            }

            return var1.toString().replace("f", "");
        }
    }

    public static final String hexTo8Bit(int value, int index) {
        String var2 = Integer.toBinaryString(value);
        StringBuilder var3 = new StringBuilder(var2);
        if (var2.length() < index) {
            for(int var4 = 0; var4 < index - var2.length(); ++var4) {
                var3.insert(0, "0");
            }
        }

        return var3.toString();
    }

    public static String bcdToStr(byte[] bytes) {
        StringBuffer var1 = new StringBuffer(bytes.length * 2);

        for(int var2 = 0; var2 < bytes.length; ++var2) {
            var1.append((byte)((bytes[var2] & 240) >>> 4));
            var1.append((byte)(bytes[var2] & 15));
        }

        return var1.toString();
    }

    public static String bcdToStr(byte[] src, int offset, int length) {
        byte[] var3 = ArrayUtils.subarray(src, offset, offset + length);
        return bcdToStr(var3);
    }

    public static byte[] str2Bcd(String asc) {
        int var1 = asc.length();
        int var2 = var1 % 2;
        if (var2 != 0) {
            asc = "0" + asc;
            var1 = asc.length();
        }

        if (var1 >= 2) {
            var1 /= 2;
        }

        byte[] var4 = new byte[var1];
        byte[] var3 = asc.getBytes();

        for(int var7 = 0; var7 < asc.length() / 2; ++var7) {
            int var5;
            if (var3[2 * var7] >= 48 && var3[2 * var7] <= 57) {
                var5 = var3[2 * var7] - 48;
            } else if (var3[2 * var7] >= 97 && var3[2 * var7] <= 122) {
                var5 = var3[2 * var7] - 97 + 10;
            } else {
                var5 = var3[2 * var7] - 65 + 10;
            }

            int var6;
            if (var3[2 * var7 + 1] >= 48 && var3[2 * var7 + 1] <= 57) {
                var6 = var3[2 * var7 + 1] - 48;
            } else if (var3[2 * var7 + 1] >= 97 && var3[2 * var7 + 1] <= 122) {
                var6 = var3[2 * var7 + 1] - 97 + 10;
            } else {
                var6 = var3[2 * var7 + 1] - 65 + 10;
            }

            int var8 = (var5 << 4) + var6;
            byte var9 = (byte)var8;
            var4[var7] = var9;
        }

        return var4;
    }

    private static byte a(char var0) {
        return (byte)var0;
    }

    public static int bytesToUShort(byte[] bytes, int offset) {
        return 255 & bytes[0 + offset] | '\uff00' & bytes[1 + offset] << 8;
    }

    public static int bytesToUShortOfReverse(byte[] bytes) {
        return bytesToShortOfReverse(bytes, 0);
    }

    public static int bytesToUShortOfReverse(byte[] bytes, int offset) {
        return 255 & bytes[1 + offset] | '\uff00' & bytes[0 + offset] << 8;
    }

    public static int bytesToInt(byte[] src, int offset) {
        return src[offset] & 255 | (src[offset + 1] & 255) << 8 | (src[offset + 2] & 255) << 16 | (src[offset + 3] & 255) << 24;
    }

    public static int bytesToInt(byte[] src) {
        return bytesToInt(src, 0);
    }

    public static int bytesToIntOfReverse(byte[] bytes, int offset) {
        return 255 & bytes[3 + offset] | '\uff00' & bytes[2 + offset] << 8 | 16711680 & bytes[1 + offset] << 16 | -16777216 & bytes[0 + offset] << 24;
    }

    public static int bytesToIntOfReverse(byte[] bytes) {
        return bytesToIntOfReverse(bytes, 0);
    }

    public static long bytesToUInt(byte[] bytes, int offset) {
        int var2 = bytesToInt(bytes, offset);
        return var2 >= 0 ? (long)var2 : 4294967296L + (long)var2;
    }

    public static long bytesToUInt(byte[] bytes) {
        return bytesToUInt(bytes, 0);
    }

    public static long bytesToUIntOfReverse(byte[] bytes, int offset) {
        int var2 = bytesToIntOfReverse(bytes, offset);
        return var2 >= 0 ? (long)var2 : 4294967296L + (long)var2;
    }

    public static long bytesToUIntOfReverse(byte[] bytes) {
        return bytesToUIntOfReverse(bytes, 0);
    }

    public static float bytesToFloat(byte[] bytes) {
        return Float.intBitsToFloat(bytesToInt(bytes, 0));
    }

    public static float bytesToFloat(byte[] bytes, int offset) {
        return Float.intBitsToFloat(bytesToInt(bytes, offset));
    }

    public static float bytesToFloatOfReverse(byte[] bytes) {
        return bytesToFloatOfReverse(bytes, 0);
    }

    public static float bytesToFloatOfReverse(byte[] bytes, int offset) {
        return Float.intBitsToFloat(bytesToIntOfReverse(bytes, offset));
    }

    public static double bytesToDouble(byte[] src) {
        return Double.longBitsToDouble(bytesToLong(src));
    }

    public static double bytesToDouble(byte[] src, int offset) {
        return Double.longBitsToDouble(bytesToLong(src, offset));
    }

    public static double bytesToDoubleOfReverse(byte[] src) {
        return bytesToDoubleOfReverse(src, 0);
    }

    public static double bytesToDoubleOfReverse(byte[] src, int offset) {
        return Double.longBitsToDouble(bytesToLongOfReverse(src, offset));
    }

    public static String bytesToString(byte[] src, Charset charset) {
        int var2 = Arrays.binarySearch(src, (byte)0);
        return new String(Arrays.copyOf(src, var2), charset);
    }

    public static String bytesToString(byte[] src) {
        return new String(a(src));
    }

    public static String bytesToString(byte[] src, int startIndex, int endIndex) {
        return new String(a(subBytes(src, startIndex, endIndex)));
    }

    public static String bytesToString(byte[] src, int startIndex, int endIndex, Charset charset) {
        return new String(a(subBytes(src, startIndex, endIndex)), charset);
    }

    public static void bytesReverse(byte[] reverse) {
        if (reverse != null) {
            byte var1 = 0;

            for(int var2 = 0; var2 < reverse.length / 2; ++var2) {
                var1 = reverse[var2];
                reverse[var2] = reverse[reverse.length - 1 - var2];
                reverse[reverse.length - 1 - var2] = var1;
            }
        }

    }

    private static byte[] a(byte[] var0) {
        int var1 = 0;

        for(int var2 = 0; var2 < var0.length && var0[var2] != 0; ++var2) {
            ++var1;
        }

        return subBytes(var0, 0, var1);
    }

    public static long bytesToLong(byte[] bytes) {
        return bytesToLong(bytes, 0);
    }

    public static long bytesToLong(byte[] bytes, int offset) {
        return 255L & (long)bytes[0 + offset] | 65280L & (long)bytes[1 + offset] << 8 | 16711680L & (long)bytes[2 + offset] << 16 | 4278190080L & (long)bytes[3 + offset] << 24 | 1095216660480L & (long)bytes[4 + offset] << 32 | 280375465082880L & (long)bytes[5 + offset] << 40 | 71776119061217280L & (long)bytes[6 + offset] << 48 | -72057594037927936L & (long)bytes[7 + offset] << 56;
    }

    public static long bytesToLongOfReverse(byte[] bytes) {
        return bytesToLongOfReverse(bytes, 0);
    }

    public static long bytesToLongOfReverse(byte[] bytes, int offset) {
        return 255L & (long)bytes[7 + offset] | 65280L & (long)bytes[6 + offset] << 8 | 16711680L & (long)bytes[5 + offset] << 16 | 4278190080L & (long)bytes[4 + offset] << 24 | 1095216660480L & (long)bytes[3 + offset] << 32 | 280375465082880L & (long)bytes[2 + offset] << 40 | 71776119061217280L & (long)bytes[1 + offset] << 48 | -72057594037927936L & (long)bytes[0 + offset] << 56;
    }

    public static byte[] subBytes(byte[] bytes, int beginIndex, int endIndex) {
        byte[] var3 = new byte[endIndex - beginIndex];
        System.arraycopy(bytes, beginIndex, var3, 0, var3.length);
        return var3;
    }

    public static byte[] subBytes(byte[] bytes, int beginIndex) {
        return subBytes(bytes, beginIndex, bytes.length);
    }

    public static String subBytesToString(byte[] bytes, int beginIndex, int endIndex) {
        byte[] var3 = subBytes(bytes, beginIndex, endIndex);
        return bytesToString(var3);
    }

    public static byte[] addBytes(byte[] sourceBytes, byte[] targetBytes, int beginIndex) {
        if (targetBytes == null) {
            return sourceBytes;
        } else {
            int var3 = targetBytes.length;
            if (sourceBytes == null) {
                beginIndex = 0;
                sourceBytes = new byte[var3];
            }

            int var4 = sourceBytes.length;
            if (var4 - beginIndex < var3) {
                return sourceBytes;
            } else {
                for(int var5 = 0; var5 < var3; ++var5) {
                    sourceBytes[beginIndex + var5] = targetBytes[var5];
                }

                return sourceBytes;
            }
        }
    }

    public static byte[] UUID2Byte(UUID uuid) {
        ByteArrayOutputStream var1 = new ByteArrayOutputStream(16);
        DataOutputStream var2 = new DataOutputStream(var1);

        try {
            var2.writeLong(uuid.getMostSignificantBits());
            var2.writeLong(uuid.getLeastSignificantBits());
            var1.close();
            var2.close();
        } catch (IOException var5) {
            var5.printStackTrace();
        }

        byte[] var3 = var1.toByteArray();
        byte var4 = var3[0];
        var3[0] = var3[3];
        var3[3] = var4;
        var4 = var3[1];
        var3[1] = var3[2];
        var3[2] = var4;
        var4 = var3[4];
        var3[4] = var3[5];
        var3[5] = var4;
        var4 = var3[6];
        var3[6] = var3[7];
        var3[7] = var4;
        return var3;
    }

    public static byte bitAtByte(byte value, int index) {
        return (byte)((value & 1 << index) > 0 ? 1 : 0);
    }

    public static byte[] boolArrayToByte(boolean[] array) {
        if (array == null) {
            return null;
        } else {
            int var1 = array.length % 8 == 0 ? array.length / 8 : array.length / 8 + 1;
            byte[] var2 = new byte[var1];

            for(int var3 = 0; var3 < array.length; ++var3) {
                if (array[var3]) {
                    var2[var3 / 8] = (byte)(var2[var3 / 8] + (1 << var3 % 8));
                }
            }

            return var2;
        }
    }

    public static Integer cutMessageHexTo(byte[] source, int startIndex, int endIndex) {
        byte[] var3 = ArrayUtils.subarray(source, startIndex, endIndex);
        String var4 = bytesToHexString(var3);
        return Integer.parseInt(var4, 16);
    }

    public static String bytesToHexString(byte[] bArray) {
        StringBuilder var1 = new StringBuilder(bArray.length);

        for(int var2 = 0; var2 < bArray.length; ++var2) {
            String var3 = Integer.toHexString(255 & bArray[var2]);
            if (var3.length() < 2) {
                var1.append(0);
            }

            var1.append(var3.toUpperCase());
        }

        return var1.toString();
    }

    public static class Write10Build {
        public int num;
        public byte[] message;

        public Write10Build(int num, byte[] message) {
            this.num = num;
            this.message = message;
        }
    }
}
