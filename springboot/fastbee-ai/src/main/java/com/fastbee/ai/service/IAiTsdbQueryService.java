package com.fastbee.ai.service;

import com.fastbee.ai.model.vo.AiTsdbQueryResultVO;

/**
 * TSDB 问数服务。
 */
public interface IAiTsdbQueryService {

    /**
     * 查询设备历史值、最近值或统计值。
     *
     * @param question 用户问题
     * @return TSDB 问数结果
     */
    AiTsdbQueryResultVO query(String question);
}
