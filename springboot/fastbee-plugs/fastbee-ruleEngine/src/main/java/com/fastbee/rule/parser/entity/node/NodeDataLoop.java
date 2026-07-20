package com.fastbee.rule.parser.entity.node;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NodeDataLoop {

    private Boolean parallel;//是否开启异步循环

    private Integer loopNumber;//for循环次数

}
