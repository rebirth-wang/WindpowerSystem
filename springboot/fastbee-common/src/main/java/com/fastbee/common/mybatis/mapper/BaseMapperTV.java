//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.mybatis.mapper;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.reflect.GenericTypeUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

import com.fastbee.common.core.domain.PageParam;
import com.fastbee.common.core.domain.PageResult;
import com.fastbee.common.mybatis.utils.MyBatisUtils;
import com.fastbee.common.utils.MapstructUtils;

public interface BaseMapperTV<T, V> extends BaseMapper<T> {
    Log log = LogFactory.getLog(BaseMapperTV.class);

    default PageResult<T> selectPage(PageParam pageParam, @Param("ew") Wrapper<T> queryWrapper) {
        Page var3 = MyBatisUtils.buildPage(pageParam);
        this.selectPage(var3, queryWrapper);
        return new PageResult(var3.getRecords(), var3.getTotal());
    }

    default Class<V> currentVoClass() {
        return (Class<V>) GenericTypeUtils.resolveTypeArguments(this.getClass(), BaseMapperTV.class)[1];
    }

    default Class<T> currentModelClass() {
        return (Class<T>) GenericTypeUtils.resolveTypeArguments(this.getClass(), BaseMapperTV.class)[0];
    }

    default List<T> selectList() {
        return this.selectList(new QueryWrapper());
    }

    default boolean insertBatch(Collection<T> entityList) {
        Db.saveBatch(entityList);
        return true;
    }

    default boolean updateBatchById(Collection<T> entityList) {
        Db.updateBatchById(entityList);
        return true;
    }

    default boolean insertOrUpdateBatch(Collection<T> entityList) {
        Db.saveOrUpdateBatch(entityList);
        return true;
    }

    default boolean insertBatch(Collection<T> entityList, int batchSize) {
        Db.saveBatch(entityList, batchSize);
        return true;
    }

    default boolean updateBatchById(Collection<T> entityList, int batchSize) {
        Db.updateBatchById(entityList, batchSize);
        return true;
    }

    default boolean insertOrUpdateBatch(Collection<T> entityList, int batchSize) {
        Db.saveOrUpdateBatch(entityList, batchSize);
        return true;
    }

    default V selectVoById(Serializable id) {
        return (V)this.selectVoById(id, this.currentVoClass());
    }

    default <C> C selectVoById(Serializable id, Class<C> voClass) {
        Object var3 = this.selectById(id);
        return (C)(ObjectUtil.isNull(var3) ? null : MapstructUtils.convert(var3, voClass));
    }

    default List<V> selectVoBatchIds(Collection<? extends Serializable> idList) {
        return this.<V>selectVoBatchIds(idList, this.currentVoClass());
    }

    default <C> List<C> selectVoBatchIds(Collection<? extends Serializable> idList, Class<C> voClass) {
        List var3 = this.selectBatchIds(idList);
        return (List<C>)(CollUtil.isEmpty(var3) ? CollUtil.newArrayList(new Object[0]) : MapstructUtils.convert(var3, voClass));
    }

    default List<V> selectVoByMap(Map<String, Object> map) {
        return this.<V>selectVoByMap(map, this.currentVoClass());
    }

    default <C> List<C> selectVoByMap(Map<String, Object> map, Class<C> voClass) {
        List var3 = this.selectByMap(map);
        return (List<C>)(CollUtil.isEmpty(var3) ? CollUtil.newArrayList(new Object[0]) : MapstructUtils.convert(var3, voClass));
    }

    default V selectVoOne(Wrapper<T> wrapper) {
        return (V)this.selectVoOne(wrapper, this.currentVoClass());
    }

    default V selectVoOne(Wrapper<T> wrapper, boolean throwEx) {
        return (V)this.selectVoOne(wrapper, this.currentVoClass(), throwEx);
    }

    default <C> C selectVoOne(Wrapper<T> wrapper, Class<C> voClass) {
        return (C)this.selectVoOne(wrapper, voClass, true);
    }

    default <C> C selectVoOne(Wrapper<T> wrapper, Class<C> voClass, boolean throwEx) {
        Object var4 = this.selectOne(wrapper, throwEx);
        return (C)(ObjectUtil.isNull(var4) ? null : MapstructUtils.convert(var4, voClass));
    }

    default List<V> selectVoList() {
        return this.<V>selectVoList(new QueryWrapper(), this.currentVoClass());
    }

    default List<V> selectVoList(Wrapper<T> wrapper) {
        return this.<V>selectVoList(wrapper, this.currentVoClass());
    }

    default <C> List<C> selectVoList(Wrapper<T> wrapper, Class<C> voClass) {
        List var3 = this.selectList(wrapper);
        return (List<C>)(CollUtil.isEmpty(var3) ? CollUtil.newArrayList(new Object[0]) : MapstructUtils.convert(var3, voClass));
    }

    default <P extends IPage<V>> P selectVoPage(IPage<T> page, Wrapper<T> wrapper) {
        return (P)this.selectVoPage(page, wrapper, this.currentVoClass());
    }

    default <C, P extends IPage<C>> P selectVoPage(IPage<T> page, Wrapper<T> wrapper, Class<C> voClass) {
        List var4 = this.selectList(page, wrapper);
        Page var5 = new Page(page.getCurrent(), page.getSize(), page.getTotal());
        if (CollUtil.isEmpty(var4)) {
            return (P)var5;
        } else {
            var5.setRecords(MapstructUtils.convert(var4, voClass));
            return (P)var5;
        }
    }

    List<T> selectList(IPage<T> var1, @Param("ew") Wrapper<T> var2);

    default T selectOne(@Param("ew") Wrapper<T> queryWrapper, boolean throwEx) {
        List var3 = this.selectList(queryWrapper);
        int var4 = var3.size();
        if (var4 == 1) {
            return (T)var3.get(0);
        } else if (var4 > 1) {
            if (throwEx) {
                throw new TooManyResultsException("Expected one result (or null) to be returned by selectOne(), but found: " + var4);
            } else {
                return (T)var3.get(0);
            }
        } else {
            return null;
        }
    }
}
