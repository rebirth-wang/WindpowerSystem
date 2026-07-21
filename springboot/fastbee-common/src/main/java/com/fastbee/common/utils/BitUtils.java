//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.utils;

import java.util.Arrays;

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
        return num >> i - 1 & 1;
    }

    public static String bin2hex(String input) {
        StringBuilder var1 = new StringBuilder();
        int var2 = input.length();
        System.out.println("原数据长度：" + var2 / 8 + "字节");

        for(int var3 = 0; var3 < var2 / 4; ++var3) {
            String var4 = input.substring(var3 * 4, (var3 + 1) * 4);
            int var5 = Integer.parseInt(var4, 2);
            String var6 = Integer.toHexString(var5).toUpperCase();
            var1.append(var6);
        }

        return var1.toString();
    }

    public static int bin2Dec(String binaryString) {
        int var1 = 0;

        for(int var2 = 0; var2 < binaryString.length(); ++var2) {
            char var3 = binaryString.charAt(var2);
            if (var3 > '2' || var3 < '0') {
                throw new NumberFormatException(String.valueOf(var2));
            }

            var1 = var1 * 2 + (binaryString.charAt(var2) - 48);
        }

        return var1;
    }

    public static int[] string2Ins(String input) {
        StringBuilder var1 = new StringBuilder(input);
        int var2 = var1.length() % 8;
        if (var2 > 0) {
            for(int var3 = 0; var3 < 8 - var2; ++var3) {
                var1.append("0");
            }
        }

        int[] var5 = new int[var1.length() / 8];

        for(int var4 = 0; var4 < var5.length; ++var4) {
            var5[var4] = Integer.parseInt(var1.substring(var4 * 8, var4 * 8 + 8), 2);
        }

        return var5;
    }

    public static byte[] string2bytes(String input) {
        StringBuilder var1 = new StringBuilder(input);
        int var2 = var1.length() % 8;
        if (var2 > 0) {
            for(int var3 = 0; var3 < 8 - var2; ++var3) {
                var1.insert(0, "0");
            }
        }

        byte[] var5 = new byte[var1.length() / 8];

        for(int var4 = 0; var4 < var5.length; ++var4) {
            var5[var4] = (byte)Integer.parseInt(var1.substring(var4 * 8, var4 * 8 + 8), 2);
        }

        return var5;
    }

    private static String a(byte[] var0, Object var1, Object... var2) {
        if (var0 != null && var0.length > 0 && var1 != null) {
            try {
                int var4 = Integer.parseInt(var1.toString());
                byte[] var3;
                if (var2 != null && var2.length > 0) {
                    int var5 = Integer.parseInt(var2[0].toString());
                    if (var4 < var5 && var5 > 0) {
                        var3 = Arrays.copyOfRange(var0, var4, var5 + 1);
                    } else {
                        var3 = Arrays.copyOfRange(var0, var4, var4 + 1);
                    }
                } else {
                    var3 = Arrays.copyOfRange(var0, var4, var4 + 1);
                }

                if (var3 != null && var3.length > 0) {
                    long var11 = 0L;
                    int var7 = -1;

                    for(int var8 = var3.length - 1; var8 >= 0; ++var7) {
                        int var9 = var3[var8];
                        if (var9 < 0) {
                            var9 += 256;
                        }

                        var11 += Math.round((double)var9 * Math.pow((double)16.0F, (double)(2 * var7 + 2)));
                        --var8;
                    }

                    return (new Long(var11)).toString();
                }
            } catch (Exception var10) {
                var10.printStackTrace();
            }
        }

        return null;
    }

    private static byte[] a(Integer var0, int... var1) {
        return a(Integer.toHexString(var0), var1);
    }

    private static byte[] a(String var0, int... var1) {
        var0 = var0.toLowerCase();
        if (var0.length() % 2 != 0) {
            var0 = "0" + var0;
        }

        int var2 = var0.length() / 2;
        if (var2 < 1) {
            var2 = 1;
        }

        int var3 = var2;
        if (var1 != null && var1.length > 0 && var1[0] >= var2) {
            var3 = var1[0];
        }

        byte[] var4 = new byte[var3];
        int var5 = 0;

        for(int var6 = 0; var6 < var3; ++var6) {
            if (var6 < var3 - var2) {
                var4[var6] = 0;
            } else {
                byte var7 = (byte)(Character.digit(var0.charAt(var5), 16) & 255);
                if (var5 + 1 < var0.length()) {
                    byte var8 = (byte)(Character.digit(var0.charAt(var5 + 1), 16) & 255);
                    var4[var6] = (byte)(var7 << 4 | var8);
                } else {
                    var4[var6] = var7;
                }

                var5 += 2;
            }
        }

        return var4;
    }

    private static byte[] a(byte[] var0, byte[] var1) {
        if (var0 == null) {
            return var1;
        } else {
            return var1 == null ? var0 : a(var0, var1);
        }
    }

    private static byte[] a(byte[]... var0) {
        if (var0 != null && var0.length > 0) {
            int var1 = 0;

            for(int var2 = 0; var2 < var0.length; ++var2) {
                var1 += var0[var2].length;
            }

            byte[] var7 = new byte[var1];
            int var3 = 0;

            for(int var4 = 0; var4 < var0.length; ++var4) {
                byte[] var5 = var0[var4];

                for(int var6 = 0; var6 < var5.length; ++var6) {
                    var7[var3++] = var5[var6];
                }
            }

            return var7;
        } else {
            return null;
        }
    }

    public static byte[] hexStringToByteArray(String s) {
        int var1 = s.length();
        byte[] var2 = new byte[var1 / 2];

        for(int var3 = 0; var3 < var1; var3 += 2) {
            var2[var3 / 2] = (byte)((Character.digit(s.charAt(var3), 16) << 4) + Character.digit(s.charAt(var3 + 1), 16));
        }

        return var2;
    }

    public static void main(String[] args) {
        String var1 = bin2hex("1111111000000000");
        int var2 = bin2Dec("1111111000000000");
        byte[] var3 = string2bytes("111111000000000");
        System.out.println(var1);
        System.out.println(var2);
    }
}
