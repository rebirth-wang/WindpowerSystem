//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.core.page;

import java.io.Serializable;
import java.util.List;

public class TableDataExtendInfo implements Serializable {
    private static final long e = 1L;
    private long f;
    private List<?> g;
    private int code;
    private String msg;
    private Integer h;

    public Integer getAllEnable() {
        return this.h;
    }

    public void setAllEnable(Integer allEnable) {
        this.h = allEnable;
    }

    public TableDataExtendInfo() {
    }

    public TableDataExtendInfo(List<?> list, int total) {
        this.g = list;
        this.f = (long)total;
    }

    public long getTotal() {
        return this.f;
    }

    public void setTotal(long total) {
        this.f = total;
    }

    public List<?> getRows() {
        return this.g;
    }

    public void setRows(List<?> rows) {
        this.g = rows;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
