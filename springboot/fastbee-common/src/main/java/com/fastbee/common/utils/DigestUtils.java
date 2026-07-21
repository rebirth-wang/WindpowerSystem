//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.utils;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;

import org.apache.commons.lang3.Validate;

import com.fastbee.common.utils.uuid.IdUtils;

public class DigestUtils {
    private static SecureRandom at = new SecureRandom();
    private static IdUtils au = new IdUtils(0L, 0L);

    public static String getId() {
        return String.valueOf(Math.abs(at.nextLong()));
    }

    public static String nextId() {
        return String.valueOf(au.nextId());
    }

    public static byte[] genSalt(int numBytes) {
        Validate.isTrue(numBytes > 0, "numBytes argument must be a positive integer (1 or larger)", (long)numBytes);
        byte[] var1 = new byte[numBytes];
        at.nextBytes(var1);
        return var1;
    }

    public static byte[] digest(byte[] input, String algorithm, byte[] salt, int iterations) {
        try {
            MessageDigest var4 = MessageDigest.getInstance(algorithm);
            if (salt != null) {
                var4.update(salt);
            }

            byte[] var5 = var4.digest(input);

            for(int var6 = 1; var6 < iterations; ++var6) {
                var4.reset();
                var5 = var4.digest(var5);
            }

            return var5;
        } catch (GeneralSecurityException var7) {
            throw ExceptionUtils.unchecked(var7);
        }
    }

    public static byte[] digest(InputStream input, String algorithm) throws IOException {
        try {
            MessageDigest var2 = MessageDigest.getInstance(algorithm);
            short var3 = 8192;
            byte[] var4 = new byte[var3];

            for(int var5 = input.read(var4, 0, var3); var5 > -1; var5 = input.read(var4, 0, var3)) {
                var2.update(var4, 0, var5);
            }

            return var2.digest();
        } catch (GeneralSecurityException var6) {
            throw ExceptionUtils.unchecked(var6);
        }
    }

    public static void main(String[] args) {
        for(int var1 = 0; var1 < 10; ++var1) {
            System.out.println(nextId());
        }

    }
}
