package com.fastbee.rule.parser.entity.style;

import java.util.List;

import lombok.Data;

@Data
public class NodeStyle {

    private NodeStyleToolbar toolbar;
    private List<NodeStyleHandles> handles;
    private List<NodeStyleHandles> extendHandles;

}
