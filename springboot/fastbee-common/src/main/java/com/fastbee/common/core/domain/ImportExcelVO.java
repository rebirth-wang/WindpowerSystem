//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.core.domain;

import com.fastbee.common.annotation.Excel;

public class ImportExcelVO {
    @Excel(
            name = "ID"
    )
    private Long id;
    @Excel(
            name = "城市ID"
    )
    private String code;
    @Excel(
            name = "行政归属"
    )
    private String city;
    @Excel(
            name = "城市简称"
    )
    private String simCity;
    @Excel(
            name = "拼音"
    )
    private String cn;
    @Excel(
            name = "lat"
    )
    private String lat;
    @Excel(
            name = "lon"
    )
    private String lon;

    public Long getId() {
        return this.id;
    }

    public String getCode() {
        return this.code;
    }

    public String getCity() {
        return this.city;
    }

    public String getSimCity() {
        return this.simCity;
    }

    public String getCn() {
        return this.cn;
    }

    public String getLat() {
        return this.lat;
    }

    public String getLon() {
        return this.lon;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setSimCity(String simCity) {
        this.simCity = simCity;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof ImportExcelVO)) {
            return false;
        } else {
            ImportExcelVO var2 = (ImportExcelVO)o;
            if (!var2.canEqual(this)) {
                return false;
            } else {
                Long var3 = this.getId();
                Long var4 = var2.getId();
                if (var3 == null) {
                    if (var4 != null) {
                        return false;
                    }
                } else if (!var3.equals(var4)) {
                    return false;
                }

                String var5 = this.getCode();
                String var6 = var2.getCode();
                if (var5 == null) {
                    if (var6 != null) {
                        return false;
                    }
                } else if (!var5.equals(var6)) {
                    return false;
                }

                String var7 = this.getCity();
                String var8 = var2.getCity();
                if (var7 == null) {
                    if (var8 != null) {
                        return false;
                    }
                } else if (!var7.equals(var8)) {
                    return false;
                }

                String var9 = this.getSimCity();
                String var10 = var2.getSimCity();
                if (var9 == null) {
                    if (var10 != null) {
                        return false;
                    }
                } else if (!var9.equals(var10)) {
                    return false;
                }

                String var11 = this.getCn();
                String var12 = var2.getCn();
                if (var11 == null) {
                    if (var12 != null) {
                        return false;
                    }
                } else if (!var11.equals(var12)) {
                    return false;
                }

                String var13 = this.getLat();
                String var14 = var2.getLat();
                if (var13 == null) {
                    if (var14 != null) {
                        return false;
                    }
                } else if (!var13.equals(var14)) {
                    return false;
                }

                String var15 = this.getLon();
                String var16 = var2.getLon();
                if (var15 == null) {
                    if (var16 != null) {
                        return false;
                    }
                } else if (!var15.equals(var16)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof ImportExcelVO;
    }

    public int hashCode() {
        boolean var1 = true;
        int var2 = 1;
        Long var3 = this.getId();
        var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
        String var4 = this.getCode();
        var2 = var2 * 59 + (var4 == null ? 43 : var4.hashCode());
        String var5 = this.getCity();
        var2 = var2 * 59 + (var5 == null ? 43 : var5.hashCode());
        String var6 = this.getSimCity();
        var2 = var2 * 59 + (var6 == null ? 43 : var6.hashCode());
        String var7 = this.getCn();
        var2 = var2 * 59 + (var7 == null ? 43 : var7.hashCode());
        String var8 = this.getLat();
        var2 = var2 * 59 + (var8 == null ? 43 : var8.hashCode());
        String var9 = this.getLon();
        var2 = var2 * 59 + (var9 == null ? 43 : var9.hashCode());
        return var2;
    }

    public String toString() {
        return "ImportExcelVO(id=" + this.getId() + ", code=" + this.getCode() + ", city=" + this.getCity() + ", simCity=" + this.getSimCity() + ", cn=" + this.getCn() + ", lat=" + this.getLat() + ", lon=" + this.getLon() + ")";
    }
}
