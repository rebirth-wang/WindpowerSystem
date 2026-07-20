package com.fastbee.ai.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * NL2SQL 数据范围语义解析器。
 */
public interface IAiNl2SqlScopeSemanticResolver {

    /**
     * 根据已发布问数语义库解析表的数据范围类型。
     *
     * @param tableNames SQL 中识别出的表名
     * @return 表名与范围类型映射
     */
    Map<String, SemanticScopeMode> resolveScopeModes(List<String> tableNames);

    /**
     * 根据已发布问数语义库解析包含逻辑删除字段的表。
     *
     * @param tableNames SQL 中识别出的表名
     * @return 需要自动追加 del_flag = 0 的表名集合
     */
    Set<String> resolveLogicDeleteTables(List<String> tableNames);

    /**
     * 数据范围类型。
     */
    enum SemanticScopeMode {
        /**
         * 未命中语义库配置。
         */
        UNKNOWN,

        /**
         * 当前用户范围。
         */
        USER,

        /**
         * 当前租户范围。
         */
        TENANT
    }
}
