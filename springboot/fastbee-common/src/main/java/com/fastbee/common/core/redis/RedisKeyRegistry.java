//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.core.redis;

import java.util.ArrayList;
import java.util.List;

public class RedisKeyRegistry {
    private static final List<RedisKeyDefine> r = new ArrayList();

    public static void add(RedisKeyDefine define) {
        r.add(define);
    }

    public static List<RedisKeyDefine> list() {
        return r;
    }

    public static int size() {
        return r.size();
    }
}
