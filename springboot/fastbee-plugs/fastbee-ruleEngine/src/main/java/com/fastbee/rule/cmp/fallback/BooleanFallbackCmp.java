
package com.fastbee.rule.cmp.fallback;

import com.yomahub.liteflow.annotation.FallbackCmp;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeBooleanComponent;

@FallbackCmp
@LiteflowComponent("BooleanFallbackCmp")
public class BooleanFallbackCmp extends NodeBooleanComponent {
	@Override
	public boolean processBoolean() {
		System.out.println("BooleanFallbackCmp executed!");
		return true;
	}
}
