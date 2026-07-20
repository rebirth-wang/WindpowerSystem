package com.fastbee.rule.parser.entity.node;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NodeDataIf {

    private String trueOpt;
    private String falseOpt;

}
