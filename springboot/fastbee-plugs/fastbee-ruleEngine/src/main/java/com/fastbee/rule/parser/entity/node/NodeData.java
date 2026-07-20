package com.fastbee.rule.parser.entity.node;

import lombok.Data;

import com.fastbee.rule.parser.entity.style.NodeStyle;

@Data
public class NodeData {
    //组件ID
    private String id;
    //组件别名
    private String name;
    //组件类型
    private String type;
    //显示模式：default,simple
    private String mode;
    //基本属性
    private NodeDataBase nodeDataBase;
    //选择属性
    private NodeDataSwitch nodeDataSwitch;
    //条件属性
    private NodeDataIf nodeDataIf;
    //循环属性
    private NodeDataLoop nodeDataLoop;
    //并行属性
    private NodeDataWhen nodeDataWhen;
    //串行属性
    private NodeDataThen nodeDataThen;
    //链路属性
    private NodeDataChain nodeDataChain;
    //子流程属性
    private NodeDataSubFlow nodeDataSubflow;
    //子变量属性
    private NodeDataSubVar nodeDataSubvar;
    //路由属性
    private NodeDataRouter nodeDataRouter;
    //上下文属性
    private NodeDataContext nodeDataContext;

    private NodeStyle style;

}
