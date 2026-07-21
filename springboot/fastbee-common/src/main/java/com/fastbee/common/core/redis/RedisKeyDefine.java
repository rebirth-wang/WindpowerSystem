//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.core.redis;

import java.time.Duration;

import com.fasterxml.jackson.annotation.JsonValue;

public class RedisKeyDefine {
    private final String l;
    private final KeyTypeEnum m;
    private final Class<?> n;
    private final TimeoutTypeEnum o;
    private final Duration p;
    private final String q;

    private RedisKeyDefine(String memo, String keyTemplate, KeyTypeEnum keyType, Class<?> valueType, TimeoutTypeEnum timeoutType, Duration timeout) {
        this.q = memo;
        this.l = keyTemplate;
        this.m = keyType;
        this.n = valueType;
        this.p = timeout;
        this.o = timeoutType;
        RedisKeyRegistry.add(this);
    }

    public RedisKeyDefine(String memo, String keyTemplate, KeyTypeEnum keyType, Class<?> valueType, Duration timeout) {
        this(memo, keyTemplate, keyType, valueType, RedisKeyDefine.TimeoutTypeEnum.FIXED, timeout);
    }

    public RedisKeyDefine(String memo, String keyTemplate, KeyTypeEnum keyType, Class<?> valueType, TimeoutTypeEnum timeoutType) {
        this(memo, keyTemplate, keyType, valueType, timeoutType, Duration.ZERO);
    }

    public String formatKey(Object... args) {
        return String.format(this.l, args);
    }

    public String getKeyTemplate() {
        return this.l;
    }

    public KeyTypeEnum getKeyType() {
        return this.m;
    }

    public Class<?> getValueType() {
        return this.n;
    }

    public TimeoutTypeEnum getTimeoutType() {
        return this.o;
    }

    public Duration getTimeout() {
        return this.p;
    }

    public String getMemo() {
        return this.q;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof RedisKeyDefine)) {
            return false;
        } else {
            RedisKeyDefine var2 = (RedisKeyDefine)o;
            if (!var2.canEqual(this)) {
                return false;
            } else {
                String var3 = this.getKeyTemplate();
                String var4 = var2.getKeyTemplate();
                if (var3 == null) {
                    if (var4 != null) {
                        return false;
                    }
                } else if (!var3.equals(var4)) {
                    return false;
                }

                KeyTypeEnum var5 = this.getKeyType();
                KeyTypeEnum var6 = var2.getKeyType();
                if (var5 == null) {
                    if (var6 != null) {
                        return false;
                    }
                } else if (!var5.equals(var6)) {
                    return false;
                }

                Class var7 = this.getValueType();
                Class var8 = var2.getValueType();
                if (var7 == null) {
                    if (var8 != null) {
                        return false;
                    }
                } else if (!var7.equals(var8)) {
                    return false;
                }

                TimeoutTypeEnum var9 = this.getTimeoutType();
                TimeoutTypeEnum var10 = var2.getTimeoutType();
                if (var9 == null) {
                    if (var10 != null) {
                        return false;
                    }
                } else if (!var9.equals(var10)) {
                    return false;
                }

                Duration var11 = this.getTimeout();
                Duration var12 = var2.getTimeout();
                if (var11 == null) {
                    if (var12 != null) {
                        return false;
                    }
                } else if (!var11.equals(var12)) {
                    return false;
                }

                String var13 = this.getMemo();
                String var14 = var2.getMemo();
                if (var13 == null) {
                    if (var14 != null) {
                        return false;
                    }
                } else if (!var13.equals(var14)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof RedisKeyDefine;
    }

    public int hashCode() {
        boolean var1 = true;
        int var2 = 1;
        String var3 = this.getKeyTemplate();
        var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
        KeyTypeEnum var4 = this.getKeyType();
        var2 = var2 * 59 + (var4 == null ? 43 : var4.hashCode());
        Class var5 = this.getValueType();
        var2 = var2 * 59 + (var5 == null ? 43 : var5.hashCode());
        TimeoutTypeEnum var6 = this.getTimeoutType();
        var2 = var2 * 59 + (var6 == null ? 43 : var6.hashCode());
        Duration var7 = this.getTimeout();
        var2 = var2 * 59 + (var7 == null ? 43 : var7.hashCode());
        String var8 = this.getMemo();
        var2 = var2 * 59 + (var8 == null ? 43 : var8.hashCode());
        return var2;
    }

    public String toString() {
        return "RedisKeyDefine(keyTemplate=" + this.getKeyTemplate() + ", keyType=" + this.getKeyType() + ", valueType=" + this.getValueType() + ", timeoutType=" + this.getTimeoutType() + ", timeout=" + this.getTimeout() + ", memo=" + this.getMemo() + ")";
    }

    public static enum KeyTypeEnum {
        STRING("String"),
        LIST("List"),
        HASH("Hash"),
        SET("Set"),
        ZSET("Sorted Set"),
        STREAM("Stream"),
        PUBSUB("Pub/Sub");

        @JsonValue
        private final String type;

        public String getType() {
            return this.type;
        }

        private KeyTypeEnum(String type) {
            this.type = type;
        }
    }

    public static enum TimeoutTypeEnum {
        FOREVER(1),
        DYNAMIC(2),
        FIXED(3);

        @JsonValue
        private final Integer type;

        public Integer getType() {
            return this.type;
        }

        private TimeoutTypeEnum(Integer type) {
            this.type = type;
        }
    }
}
