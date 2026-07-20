//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.core.page;

import java.io.Serializable;
import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;

public class TableDataInfo implements Serializable {
    private static final long i = 1L;
    private long f;
    private List<?> g;
    private int code;
    private String msg;

    public TableDataInfo() {
    }

    public TableDataInfo(List<?> list, int total) {
        this.g = list;
        this.f = (long)total;
    }

    public static <T> TableDataInfo build(IPage<T> page) {
        TableDataInfo var1 = new TableDataInfo();
        var1.setCode(200);
        var1.setMsg("查询成功");
        var1.setRows(page.getRecords());
        var1.setTotal(page.getTotal());
        return var1;
    }

    public static <T> TableDataInfo build(List<T> list) {
        TableDataInfo var1 = new TableDataInfo();
        var1.setCode(200);
        var1.setMsg("查询成功");
        var1.setRows(list);
        var1.setTotal((long)list.size());
        return var1;
    }

    public static <T> TableDataInfo build() {
        TableDataInfo var0 = new TableDataInfo();
        var0.setCode(200);
        var0.setMsg("查询成功");
        return var0;
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
