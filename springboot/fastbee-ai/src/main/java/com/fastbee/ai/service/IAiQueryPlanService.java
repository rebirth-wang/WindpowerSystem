package com.fastbee.ai.service;

import com.fastbee.ai.model.vo.AiQueryPlanVO;

/**
 * AI 多源问数执行计划服务。
 */
public interface IAiQueryPlanService {

    /**
     * 根据自然语言问题构建多源问数执行计划。
     *
     * @param question 用户问题
     * @return 执行计划
     */
    AiQueryPlanVO buildPlan(String question);
}
