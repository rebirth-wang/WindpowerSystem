package com.fastbee.ai.service;

import com.fastbee.ai.model.vo.AiHybridQueryResultVO;

/**
 * Hybrid 多源问数服务。
 */
public interface IAiHybridQueryService {

    /**
     * 执行多源问数。
     *
     * @param question 用户问题
     * @return 多源问数结果
     */
    AiHybridQueryResultVO query(String question);
}
