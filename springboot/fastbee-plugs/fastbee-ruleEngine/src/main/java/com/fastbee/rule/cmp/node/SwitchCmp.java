package com.fastbee.rule.cmp.node;

import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeSwitchComponent;

@LiteflowComponent("SwitchCmp")
public class SwitchCmp extends NodeSwitchComponent {

    @Override
    public String processSwitch() throws Exception {
        System.out.println("SwitchCmp executed!");
        return "a";
    }
}
