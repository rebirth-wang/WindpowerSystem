//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.utils.gateway;

import org.apache.commons.lang3.ArrayUtils;

import com.fastbee.common.utils.CaculateUtils;
import com.fastbee.common.utils.gateway.protocol.ByteUtils;

public class CRC16Utils {
    private static int aY = 255;
    private static int aZ = 1;
    private static final int ba = 4;
    private static final int bb = 255;

    public static String getCRC(byte[] bytes) {
        return getCRC(bytes, true);
    }

    public static String getCRC(byte[] bytes, boolean lb) {
        int var2 = 65535;
        char var3 = 'ꀁ';

        for(int var4 = 0; var4 < bytes.length; ++var4) {
            var2 ^= bytes[var4] & 255;

            for(int var5 = 0; var5 < 8; ++var5) {
                if ((var2 & 1) != 0) {
                    var2 >>= 1;
                    var2 ^= var3;
                } else {
                    var2 >>= 1;
                }
            }
        }

        String var6 = Integer.toHexString(var2).toUpperCase();
        if (var6.length() != 4) {
            StringBuffer var7 = new StringBuffer("0000");
            var6 = var7.replace(4 - var6.length(), 4, var6).toString();
        }

        if (lb) {
            var6 = var6.substring(2, 4) + var6.substring(0, 2);
        }

        return var6;
    }

    public static byte[] getCrc16Byte(byte[] bytes) {
        int var1 = 65535;
        char var2 = 'ꀁ';

        for(byte var6 : bytes) {
            var1 ^= var6 & aY;

            for(int var7 = 0; var7 < 8; ++var7) {
                if ((var1 & aZ) != 0) {
                    var1 >>= 1;
                    var1 ^= var2;
                } else {
                    var1 >>= 1;
                }
            }
        }

        return new byte[]{(byte)(var1 & 255), (byte)(var1 >> 8 & 255)};
    }

    public static byte[] AddCRC(byte[] source) {
        byte[] var1 = new byte[source.length + 2];
        byte[] var2 = getCrc16Byte(source);
        System.arraycopy(source, 0, var1, 0, source.length);
        System.arraycopy(var2, 0, var1, var1.length - 2, 2);
        return var1;
    }

    public static byte[] AddPakCRC(byte[] source) {
        byte[] var1 = ArrayUtils.subarray(source, 11, source.length);
        byte[] var2 = new byte[source.length + 2];
        byte[] var3 = getCrc16Byte(var1);
        System.arraycopy(source, 0, var2, 0, source.length);
        System.arraycopy(var3, 0, var2, var2.length - 2, 2);
        return var2;
    }

    public static byte[] CRC(byte[] source) {
        source[2] = (byte)(source[2] * 2);
        byte[] var1 = new byte[source.length + 2];
        byte[] var2 = getCrc16Byte(source);
        System.arraycopy(source, 0, var1, 0, source.length);
        System.arraycopy(var2, 0, var1, var1.length - 2, 2);
        return var1;
    }

    public static byte CRC8(byte[] buffer) {
        int var1 = 255;

        for(int var2 = 0; var2 < buffer.length; ++var2) {
            var1 ^= buffer[var2] & 255;

            for(int var3 = 0; var3 < 8; ++var3) {
                if ((var1 & 1) != 0) {
                    var1 >>= 1;
                    var1 ^= 184;
                } else {
                    var1 >>= 1;
                }
            }
        }

        return (byte)var1;
    }

    public static void main(String[] args) throws Exception {
        String var1 = "0103028000";
        byte[] var2 = ByteUtils.hexToByte(var1);
        String var3 = getCRC(var2);
        System.out.println(var3);
        String var4 = "680868333701120008C100";
        byte[] var5 = ByteUtils.hexToByte(var4);
        byte var6 = CRC8(var5);
        System.out.println(var6);
        float var7 = CaculateUtils.toFloat32_CDAB(var2);
        System.out.println(var7);
    }
}
