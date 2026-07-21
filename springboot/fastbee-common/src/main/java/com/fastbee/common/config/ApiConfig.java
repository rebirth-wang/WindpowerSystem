//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(
        prefix = "api"
)
public class ApiConfig {
    private String ipplus360Key;

    public String getIpplus360Key() {
        return this.ipplus360Key;
    }

    public void setIpplus360Key(String ipplus360Key) {
        this.ipplus360Key = ipplus360Key;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof ApiConfig)) {
            return false;
        } else {
            ApiConfig var2 = (ApiConfig)o;
            if (!var2.canEqual(this)) {
                return false;
            } else {
                String var3 = this.getIpplus360Key();
                String var4 = var2.getIpplus360Key();
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
        return other instanceof ApiConfig;
    }

    public int hashCode() {
        boolean var1 = true;
        int var2 = 1;
        String var3 = this.getIpplus360Key();
        var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
        return var2;
    }

    public String toString() {
        return "ApiConfig(ipplus360Key=" + this.getIpplus360Key() + ")";
    }
}
