package com.fastbee.common.utils.wechat;

import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * @author lei lei
 * @date 2025-09-09 16:23
 * @description:
 */

class b {
    static Charset dl = Charset.forName("utf-8");
    static int dm = 32;

    b() {
    }

    static byte[] b(int var0) {
        int var1 = dm - var0 % dm;
        if (var1 == 0) {
            var1 = dm;
        }

        char var2 = c(var1);
        String var3 = new String();

        for(int var4 = 0; var4 < var1; ++var4) {
            var3 = var3 + var2;
        }

        return var3.getBytes(dl);
    }

    static byte[] g(byte[] var0) {
        byte var1 = var0[var0.length - 1];
        if (var1 < 1 || var1 > 32) {
            var1 = 0;
        }

        return Arrays.copyOfRange(var0, 0, var0.length - var1);
    }

    static char c(int var0) {
        byte var1 = (byte)(var0 & 255);
        return (char)var1;
    }
}
