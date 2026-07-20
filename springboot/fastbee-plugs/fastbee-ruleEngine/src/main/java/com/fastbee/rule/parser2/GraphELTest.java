package com.fastbee.rule.parser2;

import com.fastbee.rule.builder.RuleBuilderUtil;
import com.fastbee.rule.builder.flow.FlowBuilder;
import com.fastbee.rule.parser.entity.node.Node;
import com.fastbee.rule.parser.execption.LiteFlowELException;
import com.fastbee.rule.parser2.entity.GraphInfo;

public class GraphELTest {

    public static void main(String[] args) throws LiteFlowELException {
//        buildSingleCommonNode();
        buildSingleSwitchNode();
    }

    private static void buildSingleSwitchNode() throws LiteFlowELException {
        buildSingleNode(RuleBuilderUtil.buildSwitchNode().id("a").name("组件a").build());
    }

    private static void buildSingleCommonNode() throws LiteFlowELException {
        buildSingleNode(RuleBuilderUtil.buildCommonNode().id("a").name("组件a").build());
    }

    private static void buildSingleNode(Node node) throws LiteFlowELException {
        FlowBuilder builder = RuleBuilderUtil.buildFlow().format(true);
        builder.addNode(node);
        //输出流程数据
        System.out.println(builder.build());
        //获取流程解析相关信息
        GraphInfo graphInfo = new GraphEL(builder.build()).toELInfo();
        //输出EL表达式
        System.out.println(graphInfo.toString());
    }
}
