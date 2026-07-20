package com.fastbee.ai.model.vo;

import lombok.Data;

/**
 * TSDB 历史点位结果。
 */
@Data
public class AiTsdbHistoryPointVO {

    /**
     * 时间点。
     */
    private String time;

    /**
     * 指标值。
     */
    private String value;

    /**
     * 指标标识符。
     */
    private String identifier;
}
