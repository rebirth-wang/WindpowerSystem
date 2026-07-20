package com.fastbee.rule.parser.entity.node;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NodeDataWhen {

    private String id;//表达式ID
    private String tag;//标签
    private Boolean ignoreError;
    private String ignoreType;
    private String mustId;
    private Boolean isCatch;
    private String catchDo;
    private String customThreadExecutor;

}
