package com.fastbee.iot.data.ruleEngine;

import java.util.List;
import java.util.Objects;

import cn.hutool.core.util.ObjectUtil;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;

import com.fastbee.common.extend.core.domin.thingsModel.ThingsModelSimpleItem;
import com.fastbee.rule.cmp.data.DevTriggerData;
import com.fastbee.rule.context.RuleContext;

@LiteflowComponent("devTrigger")
public class DevTriggerCmp extends NodeComponent {

    @Override
    public boolean isAccess() {
        DevTriggerData data = this.getCmpData(DevTriggerData.class);
        RuleContext cxt = this.getContextBean(RuleContext.class);
        List<ThingsModelSimpleItem> thingsModelSimpleItems = cxt.getThingsModelSimpleItems();
        if (ObjectUtil.isEmpty(thingsModelSimpleItems)) {
            return false;
        }
        if (!Objects.equals(data.getDeviceId(), cxt.getSerialNumber())) {
            return false;
        }
        boolean find = false;
        for(ThingsModelSimpleItem thingsModelSimpleItem : thingsModelSimpleItems) {
            if(thingsModelSimpleItem.getId().equals(data.getModelId())) {
                find = true;
            }
        }
        return find;
    }

//    @Override
//    public boolean isEnd() {
//        Boolean isEnd = this.getRefNode().getIsEnd();
//        return ObjectUtil.isNull(isEnd) ? false : isEnd;
//    }

    @Override
    public void process() throws Exception {
        DevTriggerData data = this.getCmpData(DevTriggerData.class);

    }
}
