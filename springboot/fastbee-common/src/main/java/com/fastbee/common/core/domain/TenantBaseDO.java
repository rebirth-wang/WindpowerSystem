//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.core.domain;

public abstract class TenantBaseDO extends BaseDO {
    private Long tenantId;

    public Long getTenantId() {
        return this.tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String toString() {
        return "TenantBaseDO(tenantId=" + this.getTenantId() + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof TenantBaseDO)) {
            return false;
        } else {
            TenantBaseDO var2 = (TenantBaseDO)o;
            if (!var2.canEqual(this)) {
                return false;
            } else if (!super.equals(o)) {
                return false;
            } else {
                Long var3 = this.getTenantId();
                Long var4 = var2.getTenantId();
                if (var3 == null) {
                    if (var4 != null) {
                        return false;
                    }
                } else if (!var3.equals(var4)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof TenantBaseDO;
    }

    public int hashCode() {
        boolean var1 = true;
        int var2 = super.hashCode();
        Long var3 = this.getTenantId();
        var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
        return var2;
    }
}
