package com.fastbee.rule.parser2.util;

import com.fastbee.rule.parser.entity.line.Line;
import com.fastbee.rule.parser.entity.node.Node;
import com.fastbee.rule.parser2.GraphEL;

public class RuleLineUtil {

    //是否是特殊路径
    public static boolean isSpecialPath(GraphEL graphEL, Node start, Node end) {
        Line edge = graphEL.getLine(start, end);
        return edge.getData().isSpecialPath();
    }
}
