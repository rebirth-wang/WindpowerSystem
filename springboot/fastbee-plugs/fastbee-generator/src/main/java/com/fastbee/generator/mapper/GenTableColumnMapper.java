package com.fastbee.generator.mapper;

import java.util.List;

import com.baomidou.mybatisplus.extension.toolkit.Db;

import com.fastbee.common.mybatis.mapper.BaseMapperX;
import com.fastbee.generator.domain.GenTableColumn;

/**
 * 业务字段 数据层
 *
 * @author ruoyi
 */
public interface GenTableColumnMapper extends BaseMapperX<GenTableColumn> {

    /**
     * 批量插入或更新实体对象集合
     *
     * @param entityList 实体对象集合
     * @return 插入或更新操作是否成功的布尔值
     */
    default boolean insertOrUpdateBatch(List<GenTableColumn> entityList) {
        Db.saveOrUpdateBatch(entityList);
        // 临时解决 新版本 mp 插入状态判断错误问题
        return true;
    }
    boolean batchInsert(List<GenTableColumn> list);
}
