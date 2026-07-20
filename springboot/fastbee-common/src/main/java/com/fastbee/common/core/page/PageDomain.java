//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.core.page;

import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.common.utils.sql.SqlUtil;

public class PageDomain {
    private Integer pageNum;
    private Integer pageSize;
    private String b;
    private String c = "asc";
    private Boolean d = true;
    public static final int DEFAULT_PAGE_NUM = 1;
    public static final int DEFAULT_PAGE_SIZE = Integer.MAX_VALUE;

    public String getOrderBy() {
        return StringUtils.isEmpty(this.b) ? "" : StringUtils.toUnderScoreCase(this.b) + " " + this.c;
    }

    public <T> Page<T> build() {
        Integer var1 = (Integer)ObjectUtil.defaultIfNull(this.getPageNum(), 1);
        Integer var2 = (Integer)ObjectUtil.defaultIfNull(this.getPageSize(), Integer.MAX_VALUE);
        if (var1 <= 0) {
            var1 = 1;
        }

        Page var3 = new Page((long)var1, (long)var2);
        List var4 = this.a();
        if (CollUtil.isNotEmpty(var4)) {
            var3.addOrder(var4);
        }

        return var3;
    }

    private List<OrderItem> a() {
        if (!StringUtils.isBlank(this.b) && !StringUtils.isBlank(this.c)) {
            String var1 = SqlUtil.escapeOrderBySql(this.b);
            var1 = StringUtils.toUnderScoreCase(var1);
            this.c = StringUtils.replaceEach(this.c, new String[]{"ascending", "descending"}, new String[]{"asc", "desc"});
            String[] var2 = var1.split("/");
            String[] var3 = this.c.split("/");
            if (var3.length != 1 && var3.length != var2.length) {
                throw new ServiceException("排序参数有误");
            } else {
                ArrayList var4 = new ArrayList();

                for(int var5 = 0; var5 < var2.length; ++var5) {
                    String var6 = var2[var5];
                    String var7 = var3.length == 1 ? var3[0] : var3[var5];
                    if ("asc".equals(var7)) {
                        var4.add(OrderItem.asc(var6));
                    } else {
                        if (!"desc".equals(var7)) {
                            throw new ServiceException("排序参数有误");
                        }

                        var4.add(OrderItem.desc(var6));
                    }
                }

                return var4;
            }
        } else {
            return null;
        }
    }

    public Integer getPageNum() {
        return this.pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getOrderByColumn() {
        return this.b;
    }

    public void setOrderByColumn(String orderByColumn) {
        this.b = orderByColumn;
    }

    public String getIsAsc() {
        return this.c;
    }

    public void setIsAsc(String isAsc) {
        if (StringUtils.isNotEmpty(isAsc)) {
            if ("ascending".equals(isAsc)) {
                isAsc = "asc";
            } else if ("descending".equals(isAsc)) {
                isAsc = "desc";
            }

            this.c = isAsc;
        }

    }

    public Boolean getReasonable() {
        return StringUtils.isNull(this.d) ? Boolean.TRUE : this.d;
    }

    public void setReasonable(Boolean reasonable) {
        this.d = reasonable;
    }
}
