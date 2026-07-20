package com.fastbee.rule.cmp.node;

import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeBooleanComponent;

@LiteflowComponent("BooleanCmp")
public class BooleanCmp extends NodeBooleanComponent {
	@Override
	public boolean processBoolean() {
		System.out.println("BooleanCmp executed!");
		return true;
	}
}
