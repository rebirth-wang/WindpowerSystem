package com.fastbee.rule.context;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * 规则引擎调试模式
 * 调试会话上下文
 * @date 2025/10/5
 */
@Data
public class RuleDebugContext {
    /**
     * 规则ID
     */
    private String chainId;

    /**
     * 开始时间
     */
    private Long startTime;

    /**
     * 结束时间
     */
    private Long endTime;

    /**
     * 执行耗时(ms)
     */
    private Long costTime;

    /**
     * 调试步骤记录
     */
    private List<DebugStep> steps = new ArrayList<>();

    private String debugMsg;
}


