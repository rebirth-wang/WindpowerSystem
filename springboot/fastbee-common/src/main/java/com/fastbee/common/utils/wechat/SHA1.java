package com.fastbee.common.utils.wechat;

import java.security.MessageDigest;
import java.util.Arrays;

/**
 * @author lei lei
 * @date 2025-09-09 16:23
 * @description:
 */

public class SHA1 {
    public SHA1() {
    }

    public static String getSHA1(String token, String timestamp, String nonce, String encrypt) throws AesException {
        try {
            String[] var4 = new String[]{token, timestamp, nonce, encrypt};
            StringBuffer var5 = new StringBuffer();
            Arrays.sort(var4);

            for(int var6 = 0; var6 < 4; ++var6) {
                var5.append(var4[var6]);
            }

            String var13 = var5.toString();
            MessageDigest var7 = MessageDigest.getInstance("SHA-1");
            var7.update(var13.getBytes());
            byte[] var8 = var7.digest();
            StringBuffer var9 = new StringBuffer();
            String var10 = "";

            for(int var11 = 0; var11 < var8.length; ++var11) {
                var10 = Integer.toHexString(var8[var11] & 255);
                if (var10.length() < 2) {
                    var9.append(0);
                }

                var9.append(var10);
            }

            return var9.toString();
        } catch (Exception var12) {
            var12.printStackTrace();
            throw new AesException(-40003);
        }
    }
}
