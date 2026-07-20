package com.fastbee.iot.aspect;

import java.util.Objects;

import com.yomahub.liteflow.aop.ICmpAroundAspect;
import com.yomahub.liteflow.core.NodeComponent;
import org.springframework.stereotype.Component;

import com.fastbee.common.extend.enums.EnableEnum;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.rule.cmp.data.BaseData;
import com.fastbee.rule.context.BaseContext;
import com.fastbee.rule.context.DebugStep;
import com.fastbee.rule.context.RuleDebugContext;

@Component
public class CmpAspect implements ICmpAroundAspect {

    private DebugStep debugStep = new DebugStep();

    private Integer debug = EnableEnum.DISABLE.getType();

    @Override
    public void beforeProcess(NodeComponent cmp) {
        BaseData data = cmp.getCmpData(BaseData.class);
        BaseContext context = cmp.getContextBean(BaseContext.class);
        if (data != null) {
            debug = data.getDebug();
            debugStep = new DebugStep();
            if (Objects.equals(debug, EnableEnum.ENABLE.getType())) {
                context.setDebugEnabled(true);
                if (context.getDebugContext() == null) {
                    RuleDebugContext debug = new RuleDebugContext();
                    debug.setStartTime(System.currentTimeMillis());
                    debug.setChainId(cmp.getChainId());
                    context.setDebugContext(debug);
                }
                debugStep.setComponent(cmp.getNodeId());
                debugStep.setType(cmp.getName());
                debugStep.setTag(cmp.getTag());
                debugStep.setStartTime(DateUtils.getNowDate());
            }
        }
    }

    @Override
    public void afterProcess(NodeComponent cmp) {
    }

    @Override
    public void onSuccess(NodeComponent cmp) {
        if (Objects.equals(debug, EnableEnum.ENABLE.getType())) {
            debugStep.setEndTime(DateUtils.getNowDate());
            debugStep.setCostTime(debugStep.getEndTime().getTime() - debugStep.getStartTime().getTime());
            debugStep.setSuccess(true);
            BaseContext context = cmp.getContextBean(BaseContext.class);
            context.getDebugContext().getSteps().add(debugStep);
            context.getDebugContext().setEndTime(System.currentTimeMillis());
            context.getDebugContext().setCostTime(context.getDebugContext().getEndTime() - context.getDebugContext().getStartTime());
            debug = EnableEnum.DISABLE.getType();
        }
    }

    @Override
    public void onError(NodeComponent cmp, Exception e) {
        if (Objects.equals(debug, EnableEnum.ENABLE.getType())) {
            debugStep.setEndTime(DateUtils.getNowDate());
            debugStep.setCostTime(debugStep.getEndTime().getTime() - debugStep.getStartTime().getTime());
            debugStep.setSuccess(false);
            debugStep.setError(e.getMessage());
            BaseContext context = cmp.getContextBean(BaseContext.class);
            context.getDebugContext().getSteps().add(debugStep);
            context.getDebugContext().setEndTime(System.currentTimeMillis());
            context.getDebugContext().setCostTime(context.getDebugContext().getEndTime() - context.getDebugContext().getStartTime());
            debug = EnableEnum.DISABLE.getType();
        }
    }
}

