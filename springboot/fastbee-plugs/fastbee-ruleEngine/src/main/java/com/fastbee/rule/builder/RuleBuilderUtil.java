package com.fastbee.rule.builder;

import com.fastbee.rule.builder.flow.EasyFlowBuilder;
import com.fastbee.rule.builder.flow.FlowBuilder;
import com.fastbee.rule.builder.line.EasyFlowLineBuilder;
import com.fastbee.rule.builder.line.LineBuilder;
import com.fastbee.rule.builder.node.EasyFlowNodeBuilder;
import com.fastbee.rule.builder.node.NodeBuilder;
import com.fastbee.rule.parser.entity.node.Node;
import com.fastbee.rule.parser.enums.RuleEnums;

public class RuleBuilderUtil {

    // 构建默认流程（初始化数据）
    public static FlowBuilder buildFlow(){
        return new EasyFlowBuilder().defaultData();
    }

    // 构建默认流程（初始化数据）（格式化JSON）
    public static FlowBuilder buildFormatFlow(){
        return new EasyFlowBuilder().defaultData().format(true);
    }

    // 构建普通路径
    public static LineBuilder buildLine(Node sourceNode, Node targetNode){
        return new EasyFlowLineBuilder().createLine(sourceNode, targetNode).lineType(RuleEnums.PATH_ENUM.common_path);
    }

    // 构建普通路径
    public static LineBuilder buildCommonLine(Node sourceNode, Node targetNode){
        return buildLine(sourceNode, targetNode).lineType(RuleEnums.PATH_ENUM.common_path);
    }

    //构建switch的to路径
    public static LineBuilder buildToLine(Node sourceNode, Node targetNode){
        return buildLine(sourceNode, targetNode).lineType(RuleEnums.PATH_ENUM.to_path);
    }

    //构建switch的default路径
    public static LineBuilder buildDefaultLine(Node sourceNode, Node targetNode){
        return buildLine(sourceNode, targetNode).lineType(RuleEnums.PATH_ENUM.default_path);
    }

    //构建布尔路径
    public static LineBuilder buildBooleanLine(Node sourceNode, Node targetNode){
        return buildLine(sourceNode, targetNode).lineType(RuleEnums.PATH_ENUM.boolean_path);
    }
    //构建true路径
    public static LineBuilder buildTrueLine(Node sourceNode, Node targetNode){
        return buildLine(sourceNode, targetNode).lineType(RuleEnums.PATH_ENUM.true_path);
    }
    //构建false路径
    public static LineBuilder buildFalseLine(Node sourceNode, Node targetNode){
        return buildLine(sourceNode, targetNode).lineType(RuleEnums.PATH_ENUM.false_path);
    }
    //构建do路径
    public static LineBuilder buildDoLine(Node sourceNode, Node targetNode){
        return buildLine(sourceNode, targetNode).lineType(RuleEnums.PATH_ENUM.do_path);
    }
    //构建break路径
    public static LineBuilder buildBreakLine(Node sourceNode, Node targetNode){
        return buildLine(sourceNode, targetNode).lineType(RuleEnums.PATH_ENUM.break_path);
    }

    // 构建节点
    public static NodeBuilder buildNode(RuleEnums.NodeEnum nodeEnum){ return new EasyFlowNodeBuilder().createNode(nodeEnum); }
    // 构建节点
    public static NodeBuilder buildNode(Node node){ return new EasyFlowNodeBuilder(node); }
    // 构建普通节点
    public static NodeBuilder buildCommonNode(){ return buildNode(RuleEnums.NodeEnum.COMMON); }
    // 构建switch节点
    public static NodeBuilder buildSwitchNode(){ return buildNode(RuleEnums.NodeEnum.SWITCH); }
    // 构建if节点
    public static NodeBuilder buildIfNode(){ return buildNode(RuleEnums.NodeEnum.IF); }
    // 构建boolean节点
    public static NodeBuilder buildBooleanNode(){ return buildNode(RuleEnums.NodeEnum.BOOLEAN); }
    // 构建while节点
    public static NodeBuilder buildWhileNode(){ return buildNode(RuleEnums.NodeEnum.WHILE); }
    // 构建for节点
    public static NodeBuilder buildForNode(){ return buildNode(RuleEnums.NodeEnum.FOR); }
    // 构建iterator节点
    public static NodeBuilder buildIteratorNode(){ return buildNode(RuleEnums.NodeEnum.ITERATOR); }
    // 构建脚本节点
    public static NodeBuilder buildScriptNode(){ return buildNode(RuleEnums.NodeEnum.SCRIPT); }
    // 构建switch脚本节点
    public static NodeBuilder buildSwitchScriptNode(){ return buildNode(RuleEnums.NodeEnum.SWITCH_SCRIPT); }
    // 构建if脚本节点
    public static NodeBuilder buildIfScriptNode(){ return buildNode(RuleEnums.NodeEnum.IF_SCRIPT); }
    // 构建boolean脚本节点
    public static NodeBuilder buildBooleanScriptNode(){ return buildNode(RuleEnums.NodeEnum.BOOLEAN_SCRIPT); }
    // 构建条件循环脚本节点
    public static NodeBuilder buildWhileScriptNode(){ return buildNode(RuleEnums.NodeEnum.WHILE_SCRIPT); }
    // 构建次数循环脚本节点
    public static NodeBuilder buildForScriptNode(){ return buildNode(RuleEnums.NodeEnum.FOR_SCRIPT); }
    // 构建迭代循环脚本节点
    public static NodeBuilder buildIteratorScriptNode(){ return buildNode(RuleEnums.NodeEnum.ITERATOR_SCRIPT); }
    // 构建链路节点
    public static NodeBuilder buildChainNode(){ return buildNode(RuleEnums.NodeEnum.CHAIN); }
    // 构建上下文节点
    public static NodeBuilder buildContextNode(){ return buildNode(RuleEnums.NodeEnum.CONTEXT); }
    // 构建路由节点
    public static NodeBuilder buildRouterNode(){ return buildNode(RuleEnums.NodeEnum.ROUTER); }
    // 构建子流程节点
    public static NodeBuilder buildSubFlowNode(){ return buildNode(RuleEnums.NodeEnum.SUB_FLOW); }
    // 构建子变量节点
    public static NodeBuilder buildSubVarNode(){ return buildNode(RuleEnums.NodeEnum.SUB_VAR); }
    // 构建串行节点
    public static NodeBuilder buildThenNode(){ return buildNode(RuleEnums.NodeEnum.THEN); }
    // 构建并行节点
    public static NodeBuilder buildWhenNode(){ return buildNode(RuleEnums.NodeEnum.WHEN); }
    // 构建备注节点
    public static NodeBuilder buildNoteNode(){ return buildNode(RuleEnums.NodeEnum.NOTE); }
    // 构建【与】节点
    public static NodeBuilder buildAndNode(){ return buildNode(RuleEnums.NodeEnum.AND); }
    // 构建【或】节点
    public static NodeBuilder buildOrNode(){ return buildNode(RuleEnums.NodeEnum.OR); }
    // 构建【非】节点
    public static NodeBuilder buildNotNode(){ return buildNode(RuleEnums.NodeEnum.NOT); }

}
