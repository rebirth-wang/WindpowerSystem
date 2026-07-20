//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.utils.uuid;

import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastbee.common.utils.Md5Utils;

public class IdUtils {
    private static final Logger cM = LoggerFactory.getLogger(IdUtils.class);
    private static long cN = -1L;
    private long cO = 0L;
    private final long cP;
    private final long cQ;
    private static Integer cR = 0;
    private static Integer cS = 6;

    public IdUtils(long workerId, long datacenterId) {
        if (workerId <= 31L && workerId >= 0L) {
            this.cP = workerId;
        } else {
            if (workerId != -1L) {
                throw new IllegalArgumentException("worker Id can't be greater than %d or less than 0");
            }

            this.cP = (long)(new Random()).nextInt(31);
        }

        if (datacenterId <= 31L && datacenterId >= 0L) {
            this.cQ = datacenterId;
        } else {
            if (datacenterId != -1L) {
                throw new IllegalArgumentException("datacenter Id can't be greater than %d or less than 0");
            }

            this.cQ = (long)(new Random()).nextInt(31);
        }

    }

    public synchronized long nextId() {
        long var1 = this.f();
        if (var1 < cN) {
            try {
                throw new Exception("Clock moved backwards.  Refusing to generate id for " + (cN - var1) + " milliseconds");
            } catch (Exception var4) {
                cM.warn("生成ID异常", var4);
            }
        }

        if (cN == var1) {
            this.cO = this.cO + 1L & 4095L;
            if (this.cO == 0L) {
                var1 = this.a(cN);
            }
        } else {
            this.cO = 0L;
        }

        cN = var1;
        return var1 - 1288834974657L << 22 | this.cQ << 17 | this.cP << 12 | this.cO;
    }

    private long a(long var1) {
        long var3;
        for(var3 = this.f(); var3 <= var1; var3 = this.f()) {
        }

        return var3;
    }

    private long f() {
        return System.currentTimeMillis();
    }

    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String getNextCode() {
        return Md5Utils.md5(uuid() + System.currentTimeMillis()).substring(cR, cS);
    }

    public static String randomUUID() {
        return com.fastbee.common.utils.uuid.UUID.randomUUID().toString();
    }

    public static String simpleUUID() {
        return com.fastbee.common.utils.uuid.UUID.randomUUID().toString(true);
    }

    public static String fastUUID() {
        return com.fastbee.common.utils.uuid.UUID.fastUUID().toString();
    }

    public static String fastSimpleUUID() {
        return com.fastbee.common.utils.uuid.UUID.fastUUID().toString(true);
    }
}
