package com.fastbee.common.utils.wechat;

import java.util.ArrayList;

/**
 * @author lei lei
 * @date 2025-09-09 16:23
 * @description:
 */

class a {
    ArrayList<Byte> dk = new ArrayList();

    a() {
    }

    public byte[] i() {
        byte[] var1 = new byte[this.dk.size()];

        for(int var2 = 0; var2 < this.dk.size(); ++var2) {
            var1[var2] = (Byte)this.dk.get(var2);
        }

        return var1;
    }

    public a f(byte[] var1) {
        for(byte var5 : var1) {
            this.dk.add(var5);
        }

        return this;
    }

    public int size() {
        return this.dk.size();
    }
}
