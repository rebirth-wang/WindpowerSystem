//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.mybatis.mapper;

import java.util.Collection;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import org.apache.ibatis.annotations.Param;

import com.fastbee.common.core.domain.PageParam;
import com.fastbee.common.core.domain.PageResult;
import com.fastbee.common.mybatis.utils.MyBatisUtils;

public interface BaseMapperX<T> extends BaseMapper<T> {
    default PageResult<T> selectPage(PageParam pageParam, @Param("ew") Wrapper<T> queryWrapper) {
        Page var3 = MyBatisUtils.buildPage(pageParam);
        this.selectPage(var3, queryWrapper);
        return new PageResult(var3.getRecords(), var3.getTotal());
    }

    default T selectOne(String field, Object value) {
        return (T)this.selectOne((Wrapper)(new QueryWrapper()).eq(field, value));
    }

    default T selectOne(SFunction<T, ?> field, Object value) {
        return (T)this.selectOne((Wrapper)(new LambdaQueryWrapper()).eq(field, value));
    }

    default T selectOne(String field1, Object value1, String field2, Object value2) {
        return (T)this.selectOne((Wrapper)((QueryWrapper)(new QueryWrapper()).eq(field1, value1)).eq(field2, value2));
    }

    default T selectOne(SFunction<T, ?> field1, Object value1, SFunction<T, ?> field2, Object value2) {
        return (T)this.selectOne((Wrapper)((LambdaQueryWrapper)(new LambdaQueryWrapper()).eq(field1, value1)).eq(field2, value2));
    }

    default Long selectCount() {
        return this.selectCount(new QueryWrapper());
    }

    default Long selectCount(String field, Object value) {
        return this.selectCount((Wrapper)(new QueryWrapper()).eq(field, value));
    }

    default Long selectCount(SFunction<T, ?> field, Object value) {
        return this.selectCount((Wrapper)(new LambdaQueryWrapper()).eq(field, value));
    }

    default List<T> selectList() {
        return this.selectList(new QueryWrapper());
    }

    default List<T> selectList(String field, Object value) {
        return this.selectList((Wrapper)(new QueryWrapper()).eq(field, value));
    }

    default List<T> selectList(SFunction<T, ?> field, Object value) {
        return this.selectList((Wrapper)(new LambdaQueryWrapper()).eq(field, value));
    }

    default List<T> selectList(String field, Collection<?> values) {
        return this.selectList((Wrapper)(new QueryWrapper()).in(field, values));
    }

    default List<T> selectList(SFunction<T, ?> field, Collection<?> values) {
        return this.selectList((Wrapper)(new LambdaQueryWrapper()).in(field, values));
    }

    default List<T> selectList(SFunction<T, ?> leField, SFunction<T, ?> geField, Object value) {
        return this.selectList((Wrapper)((LambdaQueryWrapper)(new LambdaQueryWrapper()).le(leField, value)).ge(geField, value));
    }

    default boolean insertBatch(Collection<T> entities) {
        return Db.saveBatch(entities);
    }

    default boolean insertBatch(Collection<T> entities, int size) {
        return Db.saveBatch(entities, size);
    }

    default int updateBatch(T update) {
        return this.update(update, new QueryWrapper());
    }

    default boolean updateBatch(Collection<T> entities, int size) {
        return Db.updateBatchById(entities, size);
    }
}
