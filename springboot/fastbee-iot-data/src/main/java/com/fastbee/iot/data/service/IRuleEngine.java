package com.fastbee.iot.data.service;

import java.util.concurrent.ScheduledExecutorService;

import com.fastbee.common.extend.core.domin.mq.message.ReportDataBo;
import com.fastbee.rule.context.MsgContext;

/**
 * 规则引擎处理数据方法集合
 * @author bill
 */
public interface IRuleEngine {
    /**
     * 规则匹配(告警和场景联动)
     *
     * @param bo 上报数据模型
     */
    public void ruleMatch(ReportDataBo bo);
    public MsgContext scriptMatch(ReportDataBo bo);
    public void addCheckTask(String key, ScheduledExecutorService task);
    public void removeCheckTask(String key);
    public void ruleMatchBySceneId(Long sceneId);
}
