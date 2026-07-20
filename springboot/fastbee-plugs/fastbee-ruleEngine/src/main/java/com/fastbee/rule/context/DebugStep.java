package com.fastbee.rule.context;

import java.util.Date;

import lombok.Data;

/**
 * DebugStep
 *
 * @date 2025/10/5
 */
@Data
public class DebugStep {
    /**
     * 组件名称
     */
    private String component;

    /**
     * 步骤类型：TRIGGER, CONDITION, ACTION, SILENCE, DELAY
     */
    private String type;


    private String tag;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 执行耗时(ms)
     */
    private Long costTime;

    /**
     * 是否成功
     */
    private Boolean success = true;

    /**
     * 错误信息
     */
    private String error;
    /**
     * 输入数据摘要
     */
    private String input;

    /**
     * 输出数据摘要
     */
    private String output;

}
