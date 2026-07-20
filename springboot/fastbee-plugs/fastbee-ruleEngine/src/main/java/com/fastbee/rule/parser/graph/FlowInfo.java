package com.fastbee.rule.parser.graph;

import lombok.Builder;
import lombok.Data;

import com.fastbee.rule.parser.entity.node.Node;

@Data
@Builder
public class FlowInfo {

    private Node node;

    private Boolean isBreak;
    private Boolean isContinue;

}
