package com.fastbee.generator.mapper;

import java.util.List;

import com.baomidou.dynamic.datasource.annotation.DS;

import com.fastbee.common.mybatis.mapper.BaseMapperX;
import com.fastbee.generator.domain.GenTable;

/**
 * 业务 数据层
 *
 * @author ruoyi
 */
public interface GenTableMapper extends BaseMapperX<GenTable> {

    /**
     * 查询据库列表
     *
     * @param tableNames 表名称组
     * @return 数据库表集合
     */
    public List<GenTable> selectDbTableListByNames(String[] tableNames);

    /**
     * 查询所有表信息
     *
     * @return 表信息集合
     */
    public List<GenTable> selectGenTableAll();

    /**
     * 查询表ID业务信息
     *
     * @param id 业务ID
     * @return 业务信息
     */
    public GenTable selectGenTableById(Long id);

    /**
     * 查询表名称业务信息
     *
     * @param tableName 表名称
     * @return 业务信息
     */
    public GenTable selectGenTableByName(String tableName);

    @DS("")
    List<String> selectTableNameList(String dataName);
}
