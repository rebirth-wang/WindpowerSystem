//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.utils.sign;

public final class Base64 {
    private static final int cz = 128;
    private static final int cA = 64;
    private static final int cB = 24;
    private static final int cC = 8;
    private static final int cD = 16;
    private static final int cE = 4;
    private static final int cF = -128;
    private static final char cG = '=';
    private static final byte[] cH = new byte[128];
    private static final char[] cI = new char[64];

    private static boolean b(char var0) {
        return var0 == ' ' || var0 == '\r' || var0 == '\n' || var0 == '\t';
    }

    private static boolean c(char var0) {
        return var0 == '=';
    }

    private static boolean d(char var0) {
        return var0 < 128 && cH[var0] != -1;
    }

    public static String encode(byte[] binaryData) {
        if (binaryData == null) {
            return null;
        } else {
            int var1 = binaryData.length * 8;
            if (var1 == 0) {
                return "";
            } else {
                int var2 = var1 % 24;
                int var3 = var1 / 24;
                int var4 = var2 != 0 ? var3 + 1 : var3;
                Object var5 = null;
                char[] var17 = new char[var4 * 4];
                byte var6 = 0;
                byte var7 = 0;
                byte var8 = 0;
                byte var9 = 0;
                byte var10 = 0;
                int var11 = 0;
                int var12 = 0;

                for(int var13 = 0; var13 < var3; ++var13) {
                    var8 = binaryData[var12++];
                    var9 = binaryData[var12++];
                    var10 = binaryData[var12++];
                    var7 = (byte)(var9 & 15);
                    var6 = (byte)(var8 & 3);
                    byte var14 = (var8 & -128) == 0 ? (byte)(var8 >> 2) : (byte)(var8 >> 2 ^ 192);
                    byte var15 = (var9 & -128) == 0 ? (byte)(var9 >> 4) : (byte)(var9 >> 4 ^ 240);
                    byte var16 = (var10 & -128) == 0 ? (byte)(var10 >> 6) : (byte)(var10 >> 6 ^ 252);
                    var17[var11++] = cI[var14];
                    var17[var11++] = cI[var15 | var6 << 4];
                    var17[var11++] = cI[var7 << 2 | var16];
                    var17[var11++] = cI[var10 & 63];
                }

                if (var2 == 8) {
                    var8 = binaryData[var12];
                    var6 = (byte)(var8 & 3);
                    byte var42 = (var8 & -128) == 0 ? (byte)(var8 >> 2) : (byte)(var8 >> 2 ^ 192);
                    var17[var11++] = cI[var42];
                    var17[var11++] = cI[var6 << 4];
                    var17[var11++] = '=';
                    var17[var11++] = '=';
                } else if (var2 == 16) {
                    var8 = binaryData[var12];
                    var9 = binaryData[var12 + 1];
                    var7 = (byte)(var9 & 15);
                    var6 = (byte)(var8 & 3);
                    byte var43 = (var8 & -128) == 0 ? (byte)(var8 >> 2) : (byte)(var8 >> 2 ^ 192);
                    byte var44 = (var9 & -128) == 0 ? (byte)(var9 >> 4) : (byte)(var9 >> 4 ^ 240);
                    var17[var11++] = cI[var43];
                    var17[var11++] = cI[var44 | var6 << 4];
                    var17[var11++] = cI[var7 << 2];
                    var17[var11++] = '=';
                }

                return new String(var17);
            }
        }
    }


    private static int a(char[] var0) {
        if (var0 == null) {
            return 0;
        } else {
            int var1 = 0;
            int var2 = var0.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                if (!b(var0[var3])) {
                    var0[var1++] = var0[var3];
                }
            }

            return var1;
        }
    }

    static {
        for(int var0 = 0; var0 < 128; ++var0) {
            cH[var0] = -1;
        }

        for(int var2 = 90; var2 >= 65; --var2) {
            cH[var2] = (byte)(var2 - 65);
        }

        for(int var3 = 122; var3 >= 97; --var3) {
            cH[var3] = (byte)(var3 - 97 + 26);
        }

        for(int var4 = 57; var4 >= 48; --var4) {
            cH[var4] = (byte)(var4 - 48 + 52);
        }

        cH[43] = 62;
        cH[47] = 63;

        for(int var5 = 0; var5 <= 25; ++var5) {
            cI[var5] = (char)(65 + var5);
        }

        int var6 = 26;

        for(int var1 = 0; var6 <= 51; ++var1) {
            cI[var6] = (char)(97 + var1);
            ++var6;
        }

        var6 = 52;

        for(int var8 = 0; var6 <= 61; ++var8) {
            cI[var6] = (char)(48 + var8);
            ++var6;
        }

        cI[62] = '+';
        cI[63] = '/';
    }
}
