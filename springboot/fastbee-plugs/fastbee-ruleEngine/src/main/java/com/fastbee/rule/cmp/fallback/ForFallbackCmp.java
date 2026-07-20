package com.fastbee.rule.cmp.fallback;

import com.yomahub.liteflow.annotation.FallbackCmp;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeForComponent;

@FallbackCmp
@LiteflowComponent("ForFallbackCmp")
public class ForFallbackCmp extends NodeForComponent {
    @Override
    public int processFor() throws Exception {
        System.out.println("ForFallbackCmp executed!");
        return 3;
    }
}
