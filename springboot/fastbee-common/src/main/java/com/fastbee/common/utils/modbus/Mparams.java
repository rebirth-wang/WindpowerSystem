//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.utils.modbus;

public class Mparams {
    private int cu;
    private int code;
    private int cv;

    public int getSlaveId() {
        return this.cu;
    }

    public int getCode() {
        return this.code;
    }

    public int getAddress() {
        return this.cv;
    }

    public void setSlaveId(int slaveId) {
        this.cu = slaveId;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setAddress(int address) {
        this.cv = address;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Mparams)) {
            return false;
        } else {
            Mparams var2 = (Mparams)o;
            if (!var2.canEqual(this)) {
                return false;
            } else if (this.getSlaveId() != var2.getSlaveId()) {
                return false;
            } else if (this.getCode() != var2.getCode()) {
                return false;
            } else {
                return this.getAddress() == var2.getAddress();
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof Mparams;
    }

    public int hashCode() {
        boolean var1 = true;
        int var2 = 1;
        var2 = var2 * 59 + this.getSlaveId();
        var2 = var2 * 59 + this.getCode();
        var2 = var2 * 59 + this.getAddress();
        return var2;
    }

    public String toString() {
        return "Mparams(slaveId=" + this.getSlaveId() + ", code=" + this.getCode() + ", address=" + this.getAddress() + ")";
    }
}
