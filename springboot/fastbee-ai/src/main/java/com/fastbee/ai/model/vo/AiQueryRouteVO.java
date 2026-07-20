package com.fastbee.ai.model.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * AI 问数执行路由结果。
 */
@Data
public class AiQueryRouteVO {

    /**
     * 执行模式。
     */
    private String queryMode;

    /**
     * 目标类型。
     */
    private String targetType;

    /**
     * 路由说明。
     */
    private String routeReason;

    /**
     * 路由置信度。
     */
    private Integer routeScore = 0;

    /**
     * 路由命中标签。
     */
    private List<String> routeTags = new ArrayList<>();
}
