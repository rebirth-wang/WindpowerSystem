//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.utils.gateway.mq;

import java.util.Arrays;

public class TopicsPost {
    private String[] bf;
    private int[] bg;

    public String[] getTopics() {
        return this.bf;
    }

    public int[] getQos() {
        return this.bg;
    }

    public void setTopics(String[] topics) {
        this.bf = topics;
    }

    public void setQos(int[] qos) {
        this.bg = qos;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof TopicsPost)) {
            return false;
        } else {
            TopicsPost var2 = (TopicsPost)o;
            if (!var2.canEqual(this)) {
                return false;
            } else if (!Arrays.deepEquals(this.getTopics(), var2.getTopics())) {
                return false;
            } else {
                return Arrays.equals(this.getQos(), var2.getQos());
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof TopicsPost;
    }

    public int hashCode() {
        boolean var1 = true;
        int var2 = 1;
        var2 = var2 * 59 + Arrays.deepHashCode(this.getTopics());
        var2 = var2 * 59 + Arrays.hashCode(this.getQos());
        return var2;
    }

    public String toString() {
        return "TopicsPost(topics=" + Arrays.deepToString(this.getTopics()) + ", qos=" + Arrays.toString(this.getQos()) + ")";
    }
}
