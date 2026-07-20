//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.utils.sign;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Md5Utils {
    private static final Logger cJ = LoggerFactory.getLogger(Md5Utils.class);

    private static byte[] q(String var0) {
        try {
            MessageDigest var1 = MessageDigest.getInstance("MD5");
            var1.reset();
            var1.update(var0.getBytes("UTF-8"));
            byte[] var2 = var1.digest();
            return var2;
        } catch (Exception var3) {
            cJ.error("MD5 Error...", var3);
            return null;
        }
    }

    private static final String d(byte[] var0) {
        if (var0 == null) {
            return null;
        } else {
            StringBuffer var1 = new StringBuffer(var0.length * 2);

            for(int var2 = 0; var2 < var0.length; ++var2) {
                if ((var0[var2] & 255) < 16) {
                    var1.append("0");
                }

                var1.append(Long.toString((long)(var0[var2] & 255), 16));
            }

            return var1.toString();
        }
    }

    public static String hash(String s) {
        try {
            return new String(d(q(s)).getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        } catch (Exception var2) {
            cJ.error("not supported charset...{}", var2);
            return s;
        }
    }
}
