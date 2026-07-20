//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.utils.sign;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class SignUtils {
    public static boolean checkSignature(String token, String signature, String timestamp, String nonce) {
        String[] var4 = new String[]{token, timestamp, nonce};
        Arrays.sort(var4);
        StringBuilder var5 = new StringBuilder();

        for(int var6 = 0; var6 < var4.length; ++var6) {
            var5.append(var4[var6]);
        }

        Object var11 = null;
        String var7 = null;

        try {
            MessageDigest var12 = MessageDigest.getInstance("SHA-1");
            byte[] var8 = var12.digest(var5.toString().getBytes());
            var7 = e(var8);
        } catch (NoSuchAlgorithmException var9) {
            var9.printStackTrace();
        }

        Object var10 = null;
        return var7 != null ? var7.equals(signature.toUpperCase()) : false;
    }

    private static String e(byte[] var0) {
        String var1 = "";

        for(int var2 = 0; var2 < var0.length; ++var2) {
            var1 = var1 + a(var0[var2]);
        }

        return var1;
    }

    private static String a(byte var0) {
        char[] var1 = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] var2 = new char[]{var1[var0 >>> 4 & 15], var1[var0 & 15]};
        String var3 = new String(var2);
        return var3;
    }
}
