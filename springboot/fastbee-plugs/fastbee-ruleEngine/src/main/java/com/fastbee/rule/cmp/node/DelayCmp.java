package com.fastbee.rule.cmp.node;

import java.util.concurrent.TimeUnit;

import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;

import com.fastbee.common.utils.DateUtils;
import com.fastbee.rule.cmp.data.DelayData;
import com.fastbee.rule.context.BaseContext;

@LiteflowComponent("delay")
public class DelayCmp extends NodeComponent {
    @Override
    public void process() throws Exception {
        DelayData data = this.getCmpData(DelayData.class);
        BaseContext cxt = this.getContextBean(BaseContext.class);
        cxt.printDebugLog(this,"延时开始：%s", DateUtils.getTime());
        if (data.getDelayType() == 1) {
            TimeUnit.MILLISECONDS.sleep(data.getDelay());
        } else if (data.getDelayType() == 2) {
            TimeUnit.SECONDS.sleep(data.getDelay());
        } else if (data.getDelayType() == 3) {
            TimeUnit.MINUTES.sleep(data.getDelay());
        } else if (data.getDelayType() == 4) {
            TimeUnit.HOURS.sleep(data.getDelay());
        } else if (data.getDelayType() == 5) {
            TimeUnit.DAYS.sleep(data.getDelay());
        }
        cxt.printDebugLog(this,"延时结束：%s", DateUtils.getTime());
    }
}
