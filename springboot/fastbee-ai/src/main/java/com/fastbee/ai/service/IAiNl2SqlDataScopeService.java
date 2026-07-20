package com.fastbee.ai.service;

import com.fastbee.ai.model.dto.AiNl2SqlQueryRequest;

/**
 * AI 智能问数数据范围注入服务接口。
 */
public interface IAiNl2SqlDataScopeService {

    /**
     * 应用默认租户字段数据范围。
     *
     * @param request 查询请求
     */
    void applyTenantScope(AiNl2SqlQueryRequest request);

    /**
     * 应用 user_id 字段数据范围。
     *
     * @param request 查询请求
     */
    void applyUserScope(AiNl2SqlQueryRequest request);
}
