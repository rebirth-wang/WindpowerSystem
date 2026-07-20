package com.fastbee.iot.data.job.strategy;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Component;

import com.fastbee.common.extend.enums.DeviceJobTypeEnum;
import com.fastbee.iot.domain.DeviceJob;
import com.fastbee.iot.service.IRuleElService;
import com.fastbee.rule.domain.RuleEl;

@Component
public class RuleViewJobStrategy implements JobInvokeStrategy {
    @Resource
    private IRuleElService ruleElService;

    @Override
    public boolean supports(int jobType) {
        return DeviceJobTypeEnum.RULE_VIEW.getType() == jobType;
    }

    @Override
    public void invoke(DeviceJob job) {
        System.out.println("------------------[定时执行可视化规则]---------------------");
        RuleEl el = ruleElService.selectRuleElById(job.getSceneId());
        if (el != null && el.getEnable() == 1) {
            ruleElService.exec(el);
        } else if (el != null && el.getEnable() == 2) {
            System.out.println("规则不生效");
        } else {
            System.out.println("规则不存在");
        }
    }
}
