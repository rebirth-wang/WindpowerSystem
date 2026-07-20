package com.fastbee.ai.service;

import com.fastbee.ai.model.vo.AiChatObservabilityStatsVO;

/**
 * AI 会话观测服务接口。
 */
public interface IAiChatObservabilityService {

    /**
     * 查询当前用户会话观测统计。
     *
     * @param userId 用户 ID
     * @param days   统计窗口天数
     * @return 观测统计
     */
    AiChatObservabilityStatsVO getCurrentUserStats(Long userId, Integer days);
}
