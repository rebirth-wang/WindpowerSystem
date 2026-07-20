//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.utils.uuid;

import java.util.concurrent.atomic.AtomicInteger;

import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.StringUtils;

public class Seq {
    public static final String commSeqType = "COMMON";
    public static final String uploadSeqType = "UPLOAD";
    private static AtomicInteger cT = new AtomicInteger(1);
    private static AtomicInteger cU = new AtomicInteger(1);
    private static String cV = "A";

    public static String getId() {
        return getId("COMMON");
    }

    public static String getId(String type) {
        AtomicInteger var1 = cT;
        if ("UPLOAD".equals(type)) {
            var1 = cU;
        }

        return getId(var1, 3);
    }

    public static String getId(AtomicInteger atomicInt, int length) {
        String var2 = DateUtils.dateTimeNow();
        var2 = var2 + cV;
        var2 = var2 + a(atomicInt, length);
        return var2;
    }

    private static synchronized String a(AtomicInteger var0, int var1) {
        int var2 = var0.getAndIncrement();
        int var3 = (int)Math.pow((double)10.0F, (double)var1);
        if (var0.get() >= var3) {
            var0.set(1);
        }

        return StringUtils.padl(var2, var1);
    }
}
