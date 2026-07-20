package com.fastbee.rule.parser.entity.node;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NodeDataThen {

    private String id;//表达式ID
    private String tag;//标签
    private Boolean isCatch;
    private String catchDo;

}
