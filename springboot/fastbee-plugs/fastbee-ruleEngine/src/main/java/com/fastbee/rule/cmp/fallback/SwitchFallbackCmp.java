package com.fastbee.rule.cmp.fallback;

import com.yomahub.liteflow.annotation.FallbackCmp;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeSwitchComponent;

@FallbackCmp
@LiteflowComponent("SwitchFallbackCmp")
public class SwitchFallbackCmp extends NodeSwitchComponent {

    @Override
    public String processSwitch() throws Exception {
        System.out.println("SwitchFallbackCmp executed!");
        return "a";
    }
}
