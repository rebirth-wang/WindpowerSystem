//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.core.domain;

import java.io.Serializable;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import lombok.Generated;

public class PageParam implements Serializable {
    private static final Integer PAGE_NO = 1;
    private static final Integer PAGE_SIZE = 10;
    private @NotNull(
            message = "页码不能为空"
    ) @Min(
            value = 1L,
            message = "页码最小值为 1"
    ) Integer pageNo;
    private @NotNull(
            message = "每页条数不能为空"
    ) @Min(
            value = 1L,
            message = "每页条数最小值为 1"
    ) @Max(
            value = 100L,
            message = "每页条数最大值为 100"
    ) Integer pageSize;

    @Generated
    public PageParam() {
        this.pageNo = PAGE_NO;
        this.pageSize = PAGE_SIZE;
    }

    @Generated
    public Integer getPageNo() {
        return this.pageNo;
    }

    @Generated
    public Integer getPageSize() {
        return this.pageSize;
    }

    @Generated
    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    @Generated
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof PageParam)) {
            return false;
        } else {
            PageParam other = (PageParam)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$pageNo = this.getPageNo();
                Object other$pageNo = other.getPageNo();
                if (this$pageNo == null) {
                    if (other$pageNo != null) {
                        return false;
                    }
                } else if (!this$pageNo.equals(other$pageNo)) {
                    return false;
                }

                Object this$pageSize = this.getPageSize();
                Object other$pageSize = other.getPageSize();
                if (this$pageSize == null) {
                    if (other$pageSize != null) {
                        return false;
                    }
                } else if (!this$pageSize.equals(other$pageSize)) {
                    return false;
                }

                return true;
            }
        }
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof PageParam;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $pageNo = this.getPageNo();
        result = result * 59 + ($pageNo == null ? 43 : $pageNo.hashCode());
        Object $pageSize = this.getPageSize();
        result = result * 59 + ($pageSize == null ? 43 : $pageSize.hashCode());
        return result;
    }

    @Generated
    public String toString() {
        Integer var10000 = this.getPageNo();
        return "PageParam(pageNo=" + var10000 + ", pageSize=" + this.getPageSize() + ")";
    }
}
