package com.fastbee.rule.builder.line;

import com.fastbee.rule.parser.entity.line.Line;
import com.fastbee.rule.parser.entity.line.LineData;
import com.fastbee.rule.parser.entity.node.Node;
import com.fastbee.rule.parser.enums.RuleEnums;

public interface LineBuilder {

    LineBuilder createLine(Node sourceNode, Node targetNode);

    LineBuilder lineType(RuleEnums.PATH_ENUM pathEnum);
    LineBuilder label(String label);

    LineBuilder data(LineData lineData);

    Line build();

}
