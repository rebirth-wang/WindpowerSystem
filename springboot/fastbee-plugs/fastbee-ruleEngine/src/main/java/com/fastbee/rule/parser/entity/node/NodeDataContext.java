package com.fastbee.rule.parser.entity.node;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NodeDataContext {

    //别名
    private String asName;
    //bean
    private String fullClassName;
    //class
    private Class<?> clazz;
    //参数
    private Map<String,Object> params;

}
