package com.fastbee.rule.cmp.node;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeIteratorComponent;

@LiteflowComponent("IteratorCmp")
public class IteratorCmp extends NodeIteratorComponent {
    @Override
    public Iterator<?> processIterator() throws Exception {
        System.out.println("IteratorCmp executed!");
        List<String> list = new ArrayList<String>(){{
            add("jack");add("mary");add("tom");
        }};
        return list.iterator();
    }
}
