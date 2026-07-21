//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.mybatis.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;

import com.fastbee.common.core.domain.PageParam;
import com.fastbee.common.core.domain.SortingField;

public class MyBatisUtils {
    private static final String ai = "`";

    public static <T> Page<T> buildPage(PageParam pageParam) {
        return buildPage(pageParam, (Collection)null);
    }

    public static <T> Page<T> buildPage(PageParam pageParam, Collection<SortingField> sortingFields) {
        Page var2 = new Page((long)pageParam.getPageNo(), (long)pageParam.getPageSize());
        if (!CollectionUtil.isEmpty(sortingFields)) {
            var2.addOrder((List)sortingFields.stream().map((var0) -> "asc".equals(var0.getOrder()) ? OrderItem.asc(var0.getField()) : OrderItem.desc(var0.getField())).collect(Collectors.toList()));
        }

        return var2;
    }

    public static void addInterceptor(MybatisPlusInterceptor interceptor, InnerInterceptor inner, int index) {
        ArrayList var3 = new ArrayList(interceptor.getInterceptors());
        var3.add(index, inner);
        interceptor.setInterceptors(var3);
    }

    public static String getTableName(Table table) {
        String var1 = table.getName();
        if (var1.startsWith("`") && var1.endsWith("`")) {
            var1 = var1.substring(1, var1.length() - 1);
        }

        return var1;
    }

    public static Column buildColumn(String tableName, Alias tableAlias, String column) {
        if (tableAlias != null) {
            tableName = tableAlias.getName();
        }

        return new Column(tableName + "." + column);
    }
}
