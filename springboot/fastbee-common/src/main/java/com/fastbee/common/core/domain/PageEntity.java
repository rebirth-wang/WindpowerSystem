//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.core.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import io.swagger.annotations.ApiModelProperty;

public class PageEntity implements Serializable {
    @JsonProperty(
            access = Access.WRITE_ONLY
    )
    @TableField(
            exist = false
    )
    private Integer pageNum;
    @JsonProperty(
            access = Access.WRITE_ONLY
    )
    @TableField(
            exist = false
    )
    private Integer pageSize;
    public static final int DEFAULT_PAGE_NUM = 1;
    public static final int DEFAULT_PAGE_SIZE = Integer.MAX_VALUE;
    @TableField(
            exist = false
    )
    @ApiModelProperty("请求参数")
    @JsonInclude(Include.NON_EMPTY)
    private Map<String, Object> params;

    public <T> Page<T> buildPage() {
        Integer var1 = (Integer)ObjectUtil.defaultIfNull(this.pageNum, 1);
        Integer var2 = (Integer)ObjectUtil.defaultIfNull(this.pageSize, Integer.MAX_VALUE);
        if (var1 <= 0) {
            var1 = 1;
        }

        Page var3 = new Page((long)var1, (long)var2);
        return var3;
    }

    public Map<String, Object> getParams() {
        if (this.params == null) {
            this.params = new HashMap();
        }

        return this.params;
    }

    public Integer getPageNum() {
        return (Integer)ObjectUtil.defaultIfNull(this.pageNum, 1);
    }

    public Integer getPageSize() {
        return (Integer)ObjectUtil.defaultIfNull(this.pageSize, Integer.MAX_VALUE);
    }

    @JsonProperty(
            access = Access.WRITE_ONLY
    )
    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    @JsonProperty(
            access = Access.WRITE_ONLY
    )
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof PageEntity)) {
            return false;
        } else {
            PageEntity var2 = (PageEntity)o;
            if (!var2.canEqual(this)) {
                return false;
            } else {
                Integer var3 = this.getPageNum();
                Integer var4 = var2.getPageNum();
                if (var3 == null) {
                    if (var4 != null) {
                        return false;
                    }
                } else if (!var3.equals(var4)) {
                    return false;
                }

                Integer var5 = this.getPageSize();
                Integer var6 = var2.getPageSize();
                if (var5 == null) {
                    if (var6 != null) {
                        return false;
                    }
                } else if (!var5.equals(var6)) {
                    return false;
                }

                Map var7 = this.getParams();
                Map var8 = var2.getParams();
                if (var7 == null) {
                    if (var8 != null) {
                        return false;
                    }
                } else if (!var7.equals(var8)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof PageEntity;
    }

    public int hashCode() {
        boolean var1 = true;
        int var2 = 1;
        Integer var3 = this.getPageNum();
        var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
        Integer var4 = this.getPageSize();
        var2 = var2 * 59 + (var4 == null ? 43 : var4.hashCode());
        Map var5 = this.getParams();
        var2 = var2 * 59 + (var5 == null ? 43 : var5.hashCode());
        return var2;
    }

    public String toString() {
        return "PageEntity(pageNum=" + this.getPageNum() + ", pageSize=" + this.getPageSize() + ", params=" + this.getParams() + ")";
    }
}
