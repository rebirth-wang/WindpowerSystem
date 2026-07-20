package com.fastbee.ai.service;

import com.fastbee.ai.model.vo.AiRedisRealtimeQueryResultVO;

/**
 * Redis 实时值问数服务。
 */
public interface IAiRedisRealtimeQueryService {

    /**
     * 执行实时值问数。
     *
     * @param question 用户问题
     */
    AiRedisRealtimeQueryResultVO queryCurrentValue(String question);
}
