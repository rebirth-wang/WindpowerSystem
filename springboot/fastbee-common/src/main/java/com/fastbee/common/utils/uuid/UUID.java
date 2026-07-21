//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.utils.uuid;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import com.fastbee.common.exception.UtilException;

public final class UUID implements Serializable, Comparable<UUID> {
    private static final long cW = -1185015143654744140L;
    private final long cX;
    private final long cY;

    private UUID(byte[] data) {
        long var2 = 0L;
        long var4 = 0L;

        assert data.length == 16 : "data must be 16 bytes in length";

        for(int var6 = 0; var6 < 8; ++var6) {
            var2 = var2 << 8 | (long)(data[var6] & 255);
        }

        for(int var7 = 8; var7 < 16; ++var7) {
            var4 = var4 << 8 | (long)(data[var7] & 255);
        }

        this.cX = var2;
        this.cY = var4;
    }

    public UUID(long mostSigBits, long leastSigBits) {
        this.cX = mostSigBits;
        this.cY = leastSigBits;
    }

    public static UUID fastUUID() {
        return randomUUID(false);
    }

    public static UUID randomUUID() {
        return randomUUID(true);
    }

    public static UUID randomUUID(boolean isSecure) {
        Object var1 = isSecure ? UUID.a.da : getRandom();
        byte[] var2 = new byte[16];
        ((Random)var1).nextBytes(var2);
        var2[6] = (byte)(var2[6] & 15);
        var2[6] = (byte)(var2[6] | 64);
        var2[8] = (byte)(var2[8] & 63);
        var2[8] = (byte)(var2[8] | 128);
        return new UUID(var2);
    }

    public static UUID nameUUIDFromBytes(byte[] name) {
        MessageDigest var1;
        try {
            var1 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException var3) {
            throw new InternalError("MD5 not supported");
        }

        byte[] var2 = var1.digest(name);
        var2[6] = (byte)(var2[6] & 15);
        var2[6] = (byte)(var2[6] | 48);
        var2[8] = (byte)(var2[8] & 63);
        var2[8] = (byte)(var2[8] | 128);
        return new UUID(var2);
    }

    public static UUID fromString(String name) {
        String[] var1 = name.split("-");
        if (var1.length != 5) {
            throw new IllegalArgumentException("Invalid UUID string: " + name);
        } else {
            for(int var2 = 0; var2 < 5; ++var2) {
                var1[var2] = "0x" + var1[var2];
            }

            long var6 = Long.decode(var1[0]);
            var6 <<= 16;
            var6 |= Long.decode(var1[1]);
            var6 <<= 16;
            var6 |= Long.decode(var1[2]);
            long var4 = Long.decode(var1[3]);
            var4 <<= 48;
            var4 |= Long.decode(var1[4]);
            return new UUID(var6, var4);
        }
    }

    public long getLeastSignificantBits() {
        return this.cY;
    }

    public long getMostSignificantBits() {
        return this.cX;
    }

    public int version() {
        return (int)(this.cX >> 12 & 15L);
    }

    public int variant() {
        return (int)(this.cY >>> (int)(64L - (this.cY >>> 62)) & this.cY >> 63);
    }

    public long timestamp() throws UnsupportedOperationException {
        this.g();
        return (this.cX & 4095L) << 48 | (this.cX >> 16 & 65535L) << 32 | this.cX >>> 32;
    }

    public int clockSequence() throws UnsupportedOperationException {
        this.g();
        return (int)((this.cY & 4611404543450677248L) >>> 48);
    }

    public long node() throws UnsupportedOperationException {
        this.g();
        return this.cY & 281474976710655L;
    }

    public String toString() {
        return this.toString(false);
    }

    public String toString(boolean isSimple) {
        StringBuilder var2 = new StringBuilder(isSimple ? 32 : 36);
        var2.append(a(this.cX >> 32, 8));
        if (!isSimple) {
            var2.append('-');
        }

        var2.append(a(this.cX >> 16, 4));
        if (!isSimple) {
            var2.append('-');
        }

        var2.append(a(this.cX, 4));
        if (!isSimple) {
            var2.append('-');
        }

        var2.append(a(this.cY >> 48, 4));
        if (!isSimple) {
            var2.append('-');
        }

        var2.append(a(this.cY, 12));
        return var2.toString();
    }

    public int hashCode() {
        long var1 = this.cX ^ this.cY;
        return (int)(var1 >> 32) ^ (int)var1;
    }

    public boolean equals(Object obj) {
        if (null != obj && obj.getClass() == UUID.class) {
            UUID var2 = (UUID)obj;
            return this.cX == var2.cX && this.cY == var2.cY;
        } else {
            return false;
        }
    }

    public int compareTo(UUID val) {
        return this.cX < val.cX ? -1 : (this.cX > val.cX ? 1 : (this.cY < val.cY ? -1 : (this.cY > val.cY ? 1 : 0)));
    }

    private static String a(long var0, int var2) {
        long var3 = 1L << var2 * 4;
        return Long.toHexString(var3 | var0 & var3 - 1L).substring(1);
    }

    private void g() {
        if (this.version() != 1) {
            throw new UnsupportedOperationException("Not a time-based UUID");
        }
    }

    public static SecureRandom getSecureRandom() {
        try {
            return SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException var1) {
            throw new UtilException(var1);
        }
    }

    public static ThreadLocalRandom getRandom() {
        return ThreadLocalRandom.current();
    }

    private static class a {
        static final SecureRandom da = UUID.getSecureRandom();
    }
}
