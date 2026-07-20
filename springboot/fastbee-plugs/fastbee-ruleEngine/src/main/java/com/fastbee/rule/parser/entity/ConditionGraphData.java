package com.fastbee.rule.parser.entity;

import java.util.List;

import lombok.Data;

import com.fastbee.rule.parser.entity.node.Node;

@Data
public class ConditionGraphData {
    Node trigger;
    Node target;
    List<Node> condition;
}
