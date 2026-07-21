//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.core.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;

@Api(
        tags = {"分页结果"}
)
public final class PageResult<T> implements Serializable {
    @ApiModelProperty(
            value = "数据",
            required = true
    )
    private List<T> list;
    @ApiModelProperty(
            value = "总量",
            required = true
    )
    private Long total;

    public PageResult() {
    }

    public PageResult(List<T> list, Long total) {
        this.list = list;
        this.total = total;
    }

    public PageResult(Long total) {
        this.list = new ArrayList();
        this.total = total;
    }

    public static <T> PageResult<T> empty() {
        return new PageResult<T>(0L);
    }

    public static <T> PageResult<T> empty(Long total) {
        return new PageResult<T>(total);
    }

    public List<T> getList() {
        return this.list;
    }

    public Long getTotal() {
        return this.total;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof PageResult)) {
            return false;
        } else {
            PageResult var2 = (PageResult)o;
            Long var3 = this.getTotal();
            Long var4 = var2.getTotal();
            if (var3 == null) {
                if (var4 != null) {
                    return false;
                }
            } else if (!var3.equals(var4)) {
                return false;
            }

            List var5 = this.getList();
            List var6 = var2.getList();
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

    public int hashCode() {
        boolean var1 = true;
        int var2 = 1;
        Long var3 = this.getTotal();
        var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
        List var4 = this.getList();
        var2 = var2 * 59 + (var4 == null ? 43 : var4.hashCode());
        return var2;
    }

    public String toString() {
        return "PageResult(list=" + this.getList() + ", total=" + this.getTotal() + ")";
    }
}
