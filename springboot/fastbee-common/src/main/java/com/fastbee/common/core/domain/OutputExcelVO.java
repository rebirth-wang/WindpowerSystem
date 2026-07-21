//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.core.domain;

import java.util.List;

public class OutputExcelVO {
    private String code;
    private String name;
    private String lat;
    private String lon;
    private List<OutputExcelVO> children;

    public String getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }

    public String getLat() {
        return this.lat;
    }

    public String getLon() {
        return this.lon;
    }

    public List<OutputExcelVO> getChildren() {
        return this.children;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public void setChildren(List<OutputExcelVO> children) {
        this.children = children;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof OutputExcelVO)) {
            return false;
        } else {
            OutputExcelVO var2 = (OutputExcelVO)o;
            if (!var2.canEqual(this)) {
                return false;
            } else {
                String var3 = this.getCode();
                String var4 = var2.getCode();
                if (var3 == null) {
                    if (var4 != null) {
                        return false;
                    }
                } else if (!var3.equals(var4)) {
                    return false;
                }

                String var5 = this.getName();
                String var6 = var2.getName();
                if (var5 == null) {
                    if (var6 != null) {
                        return false;
                    }
                } else if (!var5.equals(var6)) {
                    return false;
                }

                String var7 = this.getLat();
                String var8 = var2.getLat();
                if (var7 == null) {
                    if (var8 != null) {
                        return false;
                    }
                } else if (!var7.equals(var8)) {
                    return false;
                }

                String var9 = this.getLon();
                String var10 = var2.getLon();
                if (var9 == null) {
                    if (var10 != null) {
                        return false;
                    }
                } else if (!var9.equals(var10)) {
                    return false;
                }

                List var11 = this.getChildren();
                List var12 = var2.getChildren();
                if (var11 == null) {
                    if (var12 != null) {
                        return false;
                    }
                } else if (!var11.equals(var12)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof OutputExcelVO;
    }

    public int hashCode() {
        boolean var1 = true;
        int var2 = 1;
        String var3 = this.getCode();
        var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
        String var4 = this.getName();
        var2 = var2 * 59 + (var4 == null ? 43 : var4.hashCode());
        String var5 = this.getLat();
        var2 = var2 * 59 + (var5 == null ? 43 : var5.hashCode());
        String var6 = this.getLon();
        var2 = var2 * 59 + (var6 == null ? 43 : var6.hashCode());
        List var7 = this.getChildren();
        var2 = var2 * 59 + (var7 == null ? 43 : var7.hashCode());
        return var2;
    }

    public String toString() {
        return "OutputExcelVO(code=" + this.getCode() + ", name=" + this.getName() + ", lat=" + this.getLat() + ", lon=" + this.getLon() + ", children=" + this.getChildren() + ")";
    }
}
