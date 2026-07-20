package com.fastbee.rule.cmp.node;

import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeForComponent;

@LiteflowComponent("ForCmp")
public class ForCmp extends NodeForComponent {
    @Override
    public int processFor() throws Exception {
        System.out.println("ForCmp executed!");
        return 3;
    }
}
