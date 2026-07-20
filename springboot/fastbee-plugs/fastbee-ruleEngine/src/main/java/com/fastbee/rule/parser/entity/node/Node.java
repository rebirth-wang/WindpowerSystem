package com.fastbee.rule.parser.entity.node;

import lombok.Data;

@Data
public class Node {

    public String id;
    private String type;
    public String label;
    public String name;
    private Position position;
    private Object data;
    private NodeData nodeData;
    private String parentNode;
    private Boolean initialized;

    public Node() {
    }

    public Node(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Node(NodeData data) {
        this.nodeData = data;
    }
}
