package com.fastbee.rule.parser.entity;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fastbee.rule.parser.entity.line.Line;
import com.fastbee.rule.parser.entity.node.Node;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlowData {
    private String name;
    private List<Node> nodes;
    private List<Line> lines;

    private List<Double> position;
    private Double zoom;
    private Viewport viewport;
    private Boolean format = false;

    public FlowData(List<Node> nodes, List<Line> lines) {
        this.nodes = nodes;
        this.lines = lines;
    }
}
