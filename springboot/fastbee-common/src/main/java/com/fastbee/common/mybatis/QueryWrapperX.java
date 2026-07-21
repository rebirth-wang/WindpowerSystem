//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.mybatis;

import java.util.Collection;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.springframework.util.StringUtils;

public class QueryWrapperX<T> extends QueryWrapper<T> {
    public QueryWrapperX<T> likeIfPresent(String column, String val) {
        return StringUtils.hasText(val) ? (QueryWrapperX)super.like(column, val) : this;
    }

    public QueryWrapperX<T> inIfPresent(String column, Collection<?> values) {
        return !CollectionUtils.isEmpty(values) ? (QueryWrapperX)super.in(column, values) : this;
    }

    public QueryWrapperX<T> inIfPresent(String column, Object... values) {
        return !ArrayUtils.isEmpty(values) ? (QueryWrapperX)super.in(column, values) : this;
    }

    public QueryWrapperX<T> eqIfPresent(String column, Object val) {
        return val != null ? (QueryWrapperX)super.eq(column, val) : this;
    }

    public QueryWrapperX<T> neIfPresent(String column, Object val) {
        return val != null ? (QueryWrapperX)super.ne(column, val) : this;
    }

    public QueryWrapperX<T> gtIfPresent(String column, Object val) {
        return val != null ? (QueryWrapperX)super.gt(column, val) : this;
    }

    public QueryWrapperX<T> geIfPresent(String column, Object val) {
        return val != null ? (QueryWrapperX)super.ge(column, val) : this;
    }

    public QueryWrapperX<T> ltIfPresent(String column, Object val) {
        return val != null ? (QueryWrapperX)super.lt(column, val) : this;
    }

    public QueryWrapperX<T> leIfPresent(String column, Object val) {
        return val != null ? (QueryWrapperX)super.le(column, val) : this;
    }

    public QueryWrapperX<T> betweenIfPresent(String column, Object val1, Object val2) {
        if (val1 != null && val2 != null) {
            return (QueryWrapperX)super.between(column, val1, val2);
        } else if (val1 != null) {
            return (QueryWrapperX)this.ge(column, val1);
        } else {
            return val2 != null ? (QueryWrapperX)this.le(column, val2) : this;
        }
    }

    public QueryWrapperX<T> betweenIfPresent(String column, Object[] values) {
        if (values != null && values.length != 0 && values[0] != null && values[1] != null) {
            return (QueryWrapperX)super.between(column, values[0], values[1]);
        } else if (values != null && values.length != 0 && values[0] != null) {
            return (QueryWrapperX)this.ge(column, values[0]);
        } else {
            return values != null && values.length != 0 && values[1] != null ? (QueryWrapperX)this.le(column, values[1]) : this;
        }
    }

    public QueryWrapperX<T> eq(boolean condition, String column, Object val) {
        super.eq(condition, column, val);
        return this;
    }

    public QueryWrapperX<T> eq(String column, Object val) {
        super.eq(column, val);
        return this;
    }

    public QueryWrapperX<T> orderByDesc(String column) {
        super.orderByDesc(true, column);
        return this;
    }

    public QueryWrapperX<T> last(String lastSql) {
        super.last(lastSql);
        return this;
    }

    public QueryWrapperX<T> in(String column, Collection<?> coll) {
        super.in(column, coll);
        return this;
    }
}
