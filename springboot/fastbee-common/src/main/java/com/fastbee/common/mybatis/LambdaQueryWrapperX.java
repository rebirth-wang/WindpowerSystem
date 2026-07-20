//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.mybatis;

import java.util.Collection;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.StringUtils;

public class LambdaQueryWrapperX<T> extends LambdaQueryWrapper<T> {
    public LambdaQueryWrapperX<T> likeIfPresent(SFunction<T, ?> column, String val) {
        return StringUtils.hasText(val) ? (LambdaQueryWrapperX)super.like(column, val) : this;
    }

    public LambdaQueryWrapperX<T> inIfPresent(SFunction<T, ?> column, Collection<?> values) {
        return !CollectionUtils.isEmpty(values) ? (LambdaQueryWrapperX)super.in(column, values) : this;
    }

    public LambdaQueryWrapperX<T> inIfPresent(SFunction<T, ?> column, Object... values) {
        return !ArrayUtil.isEmpty(values) ? (LambdaQueryWrapperX)super.in(column, values) : this;
    }

    public LambdaQueryWrapperX<T> eqIfPresent(SFunction<T, ?> column, Object val) {
        return ObjectUtil.isNotEmpty(val) ? (LambdaQueryWrapperX)super.eq(column, val) : this;
    }

    public LambdaQueryWrapperX<T> neIfPresent(SFunction<T, ?> column, Object val) {
        return ObjectUtil.isNotEmpty(val) ? (LambdaQueryWrapperX)super.ne(column, val) : this;
    }

    public LambdaQueryWrapperX<T> gtIfPresent(SFunction<T, ?> column, Object val) {
        return val != null ? (LambdaQueryWrapperX)super.gt(column, val) : this;
    }

    public LambdaQueryWrapperX<T> geIfPresent(SFunction<T, ?> column, Object val) {
        return val != null ? (LambdaQueryWrapperX)super.ge(column, val) : this;
    }

    public LambdaQueryWrapperX<T> ltIfPresent(SFunction<T, ?> column, Object val) {
        return val != null ? (LambdaQueryWrapperX)super.lt(column, val) : this;
    }

    public LambdaQueryWrapperX<T> leIfPresent(SFunction<T, ?> column, Object val) {
        return val != null ? (LambdaQueryWrapperX)super.le(column, val) : this;
    }

    public LambdaQueryWrapperX<T> betweenIfPresent(SFunction<T, ?> column, Object val1, Object val2) {
        if (val1 != null && val2 != null) {
            return (LambdaQueryWrapperX)super.between(column, val1, val2);
        } else if (val1 != null) {
            return (LambdaQueryWrapperX)this.ge(column, val1);
        } else {
            return val2 != null ? (LambdaQueryWrapperX)this.le(column, val2) : this;
        }
    }

    public LambdaQueryWrapperX<T> betweenIfPresent(SFunction<T, ?> column, Object[] values) {
        Object var3 = ArrayUtils.get(values, 0);
        Object var4 = ArrayUtils.get(values, 1);
        return this.betweenIfPresent(column, var3, var4);
    }

    public LambdaQueryWrapperX<T> eq(boolean condition, SFunction<T, ?> column, Object val) {
        super.eq(condition, column, val);
        return this;
    }

    public LambdaQueryWrapperX<T> eq(SFunction<T, ?> column, Object val) {
        super.eq(column, val);
        return this;
    }

    public LambdaQueryWrapperX<T> orderByDesc(SFunction<T, ?> column) {
        super.orderByDesc(true, column);
        return this;
    }

    public LambdaQueryWrapperX<T> last(String lastSql) {
        super.last(lastSql);
        return this;
    }

    public LambdaQueryWrapperX<T> in(SFunction<T, ?> column, Collection<?> coll) {
        super.in(column, coll);
        return this;
    }
}
