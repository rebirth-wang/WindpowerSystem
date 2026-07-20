package com.fastbee.ai.service;

import com.fastbee.ai.model.dto.AiNl2SqlQueryRequest;
import com.fastbee.ai.model.vo.AiNl2SqlQueryResultVO;

/**
 * AI 只读 SQL 查询服务接口。
 */
public interface IAiNl2SqlQueryService {

    /**
     * 执行只读 SQL 查询。
     *
     * @param request 查询请求
     * @return 查询结果
     */
    AiNl2SqlQueryResultVO executeReadOnlyQuery(AiNl2SqlQueryRequest request);
}
