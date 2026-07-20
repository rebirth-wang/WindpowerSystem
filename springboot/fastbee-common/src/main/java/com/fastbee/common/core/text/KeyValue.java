//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.core.text;

public class KeyValue<K, V> {
    private K s;
    private V t;

    public K getKey() {
        return this.s;
    }

    public V getValue() {
        return this.t;
    }

    public void setKey(K key) {
        this.s = key;
    }

    public void setValue(V value) {
        this.t = value;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof KeyValue)) {
            return false;
        } else {
            KeyValue var2 = (KeyValue)o;
            if (!var2.canEqual(this)) {
                return false;
            } else {
                Object var3 = this.getKey();
                Object var4 = var2.getKey();
                if (var3 == null) {
                    if (var4 != null) {
                        return false;
                    }
                } else if (!var3.equals(var4)) {
                    return false;
                }

                Object var5 = this.getValue();
                Object var6 = var2.getValue();
                if (var5 == null) {
                    if (var6 != null) {
                        return false;
                    }
                } else if (!var5.equals(var6)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof KeyValue;
    }

    public int hashCode() {
        boolean var1 = true;
        int var2 = 1;
        Object var3 = this.getKey();
        var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
        Object var4 = this.getValue();
        var2 = var2 * 59 + (var4 == null ? 43 : var4.hashCode());
        return var2;
    }

    public String toString() {
        return "KeyValue(key=" + this.getKey() + ", value=" + this.getValue() + ")";
    }

    public KeyValue() {
    }

    public KeyValue(K key, V value) {
        this.s = key;
        this.t = value;
    }
}
