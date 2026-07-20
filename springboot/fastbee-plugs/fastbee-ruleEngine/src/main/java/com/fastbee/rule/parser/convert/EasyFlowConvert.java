package com.fastbee.rule.parser.convert;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.util.StrUtil;
import com.yomahub.liteflow.flow.element.Condition;
import com.yomahub.liteflow.flow.element.Executable;
import com.yomahub.liteflow.flow.element.condition.ThenCondition;
import com.yomahub.liteflow.flow.element.condition.WhenCondition;
import lombok.extern.slf4j.Slf4j;

import com.fastbee.rule.builder.RuleBuilderUtil;
import com.fastbee.rule.builder.RuleParserUtil;
import com.fastbee.rule.builder.flow.FlowBuilder;
import com.fastbee.rule.builder.node.NodeBuilder;
import com.fastbee.rule.parser.entity.ELInfo;
import com.fastbee.rule.parser.entity.line.Line;
import com.fastbee.rule.parser.entity.node.Node;
import com.fastbee.rule.parser.entity.node.NodeDataThen;
import com.fastbee.rule.parser.execption.LiteFlowELException;
import com.fastbee.rule.parser.graph.GraphInfo;

@Slf4j
public class EasyFlowConvert implements FlowConvert {

    private static final String CHAIN_ID = "convert_chain";

    @Override
    public String el2Json(String el) {
        log.info("输入表达式："+el);
        ELInfo elInfo = new ELInfo(CHAIN_ID, el);
        ExpressGenerator expressGenerator = new ExpressGenerator();
        Condition condition = expressGenerator.getCondition(elInfo);

        FlowBuilder flowBuilder = RuleBuilderUtil.buildFormatFlow();
        parserCondition(flowBuilder, condition);

//        CmpProperty property = expressGenerator.generateJsonEL(elInfo);
//        buildNode(flowBuilder,property);

        String json = flowBuilder.build();
        try {
            GraphInfo graphInfo = RuleParserUtil.parserFlow(json);
            log.info("生成表达式："+graphInfo.toString());
            log.info("节点："+ flowBuilder.getNodes().stream().map(m-> m.getNodeData().getId()).collect(Collectors.toList()));
            log.info("路径："+ flowBuilder.getLines().stream().map(m-> m.getFromNode().getNodeData().getId()+"->"+m.getToNode().getNodeData().getId()).collect(Collectors.toList()));
        } catch (LiteFlowELException e) {
            throw new RuntimeException(e);
        }
        return flowBuilder.build();
    }

    public void parserCondition(FlowBuilder flowBuilder, Condition condition) {
        List<Executable> executableList = condition.getExecutableList();
        switch (condition.getConditionType().getType()){
            case "node": break;
            case "then":
                Node thenNode = buildNode(condition);
                flowBuilder.addNode(thenNode);
                for (Executable executable : executableList){
                    if(isNode(executable)) {
                        flowBuilder.addNode(buildNode(executable));
                    }else if(isThen(executable)){
                        parserCondition(flowBuilder, (ThenCondition) executable);
                    }else if(isWhen(executable)){
                        parserCondition(flowBuilder, (WhenCondition) executable);
                    }
                }
                if(thenNode != null){
                    flowBuilder.addLine(buildLine(flowBuilder, thenNode, flowBuilder.getNodeByCmpId(executableList.get(0).getId())));
                }
                List<List<Executable>> lists = convertToAdjacentPairs(executableList);
                for (List<Executable> executables : lists){
                    Executable sourceExecutable = executables.get(0);
                    Executable targetExecutable = executables.get(1);
                    if(isNode(sourceExecutable) && isNode(targetExecutable)) {
                        flowBuilder.addLine(buildLine(flowBuilder, sourceExecutable, targetExecutable));
                    }else if(isNode(sourceExecutable) && isThen(targetExecutable)){
                        List<Executable> executableLists = ((ThenCondition) targetExecutable).getExecutableList();
                        if(thenNode != null){
                            flowBuilder.addLine(buildLine(flowBuilder, flowBuilder.getNodeByCmpId(sourceExecutable.getId()), thenNode));
                            flowBuilder.addLine(buildLine(flowBuilder, thenNode, flowBuilder.getNodeByCmpId(executableLists.get(0).getId())));
                        }else{
                            flowBuilder.addLine(buildLine(flowBuilder, sourceExecutable, executableLists.get(0)));
                        }
                    }else if(isNode(targetExecutable) && isThen(sourceExecutable)){
                        List<Executable> executableLists = ((ThenCondition) sourceExecutable).getExecutableList();
                        if(thenNode != null){
                            //flowBuilder.addLine(buildLine(flowBuilder, flowBuilder.getNodeByCmpId(sourceExecutable.getId()), thenNode));
                            flowBuilder.addLine(buildLine(flowBuilder, executableLists.get(executableLists.size() - 1), targetExecutable));
                        }else {
                            flowBuilder.addLine(buildLine(flowBuilder, executableLists.get(executableLists.size() - 1), targetExecutable));
                        }
                    }else if(isNode(sourceExecutable) && isWhen(targetExecutable)){
                        WhenCondition whenCondition = (WhenCondition) targetExecutable;
                        for (Executable executable : whenCondition.getExecutableList()){
                            if(isNode(executable)) {
                                flowBuilder.addLine(buildLine(flowBuilder, sourceExecutable, executable));
                            }else if(isThen(executable)){
                                flowBuilder.addLine(buildLine(flowBuilder, sourceExecutable, ((ThenCondition)executable).getExecutableList().get(0)));
                            }else if(isWhen(executable)){
                                System.out.println("未知类型2");
                            }
                        }
                    }else if(isWhen(sourceExecutable) && isNode(targetExecutable)){
                        WhenCondition whenCondition = (WhenCondition) sourceExecutable;
                        for (Executable executable : whenCondition.getExecutableList()){
                            if(isNode(executable)) {
                                flowBuilder.addLine(buildLine(flowBuilder, executable, targetExecutable));
                            }else if(isThen(executable)){
                                List<Executable> executableList1 = ((ThenCondition) executable).getExecutableList();
                                flowBuilder.addLine(buildLine(flowBuilder, executableList1.get(executableList1.size()-1), targetExecutable));
                            }else if(isWhen(executable)){
                                System.out.println("未知类型22");
                            }
                        }
                    }else{
                        System.out.println("未知类型");
                    }
                }
                break;
            case "when":
                for (Executable executable : executableList){
                    if(isNode(executable)) {
                        flowBuilder.addNode(buildNode(executable));
                    }else if(isThen(executable)){
                        parserCondition(flowBuilder, (ThenCondition) executable);
                    }else if(isWhen(executable)){
                        parserCondition(flowBuilder, (WhenCondition) executable);
                    }
                }
                break;
        }
        System.out.println();
    }

    private static Node buildNode(Executable executable){
        NodeBuilder nodeBuilder = null;
        if(isThen(executable)){
            ThenCondition thenCondition = (ThenCondition) executable;
            if(!thenCondition.getId().contains("condition-then")){
                if(StrUtil.isNotBlank(thenCondition.getTag())){
                    nodeBuilder = RuleBuilderUtil.buildThenNode();
                    nodeBuilder.thenData(NodeDataThen.builder().id(thenCondition.getId()).tag(thenCondition.getTag()).build());
                }
            }
        }else{
            nodeBuilder = RuleBuilderUtil.buildCommonNode().id(executable.getId());
        }
        return nodeBuilder == null ? null : nodeBuilder.build();
    }

    private static Line buildLine(FlowBuilder flowBuilder, Executable sourceExecutable, Executable targetExecutable){
        Node sourceNode = flowBuilder.getNodeByCmpId(sourceExecutable.getId());
        Node targetNode = flowBuilder.getNodeByCmpId(targetExecutable.getId());
        return buildLine(flowBuilder, sourceNode, targetNode);
    }

    private static Line buildLine(FlowBuilder flowBuilder, String sourceNodeId, String targetNodeId){
        Node sourceNode = flowBuilder.getNodeById(sourceNodeId);
        Node targetNode = flowBuilder.getNodeById(targetNodeId);
        return RuleBuilderUtil.buildCommonLine(sourceNode, targetNode).build();
    }

    private static Line buildLine(FlowBuilder flowBuilder, Node sourceNode, Node targetNode){
        return RuleBuilderUtil.buildCommonLine(sourceNode, targetNode).build();
    }

    public static List<List<Executable>> convertToAdjacentPairs(List<Executable> list) {
        List<List<Executable>> result = new ArrayList<>();
        for (int i = 0; i < list.size() - 1; i++) {
            List<Executable> pair = new ArrayList<>();
            pair.add(list.get(i));
            pair.add(list.get(i + 1));
            result.add(pair);
        }
        return result;
    }

    private static boolean isNode(Executable executable){
        return executable instanceof com.yomahub.liteflow.flow.element.Node;
    }
    private static boolean isThen(Executable executable){
        return executable instanceof ThenCondition;
    }
    private static boolean isWhen(Executable executable){
        return executable instanceof WhenCondition;
    }

    /*private void buildNode(FlowBuilder builder,CmpProperty property){
        List<CmpProperty> children = property.getChildren();
        switch (property.getType()){
            case "THEN":
                for (int i = 0; i < children.size(); i++){
                    CmpProperty childCmpProperty = children.get(i);
                    buildNode(builder, childCmpProperty);
                }
                buildLine(builder, property);
                break;
            case "WHEN":
                for (int i = 0; i < children.size(); i++){
                    CmpProperty childCmpProperty = children.get(i);
                    buildNode(builder, childCmpProperty);
                }
                break;
            case "NodeComponent":
                Node node = IvyBuilderUtil.buildCommonNode().id(property.getId()).build();
                property.setNode(node);
                builder.addNode(node);
                break;
        }
    }

    private void buildLine(FlowBuilder builder,CmpProperty property){
        List<List<CmpProperty>> resultList = convertToAdjacentPairs(property.getChildren());
        for (List<CmpProperty> itemList : resultList){
            CmpProperty sourceCmpProperty = itemList.get(0);
            CmpProperty targetCmpProperty = itemList.get(1);
            Node sourceNode = sourceCmpProperty.getNode();
            Node targetNode = targetCmpProperty.getNode();
            if("NodeComponent".equalsIgnoreCase(sourceCmpProperty.getType()) && "NodeComponent".equalsIgnoreCase(targetCmpProperty.getType())){
                builder.addLine(IvyBuilderUtil.buildCommonLine(sourceNode, targetNode).build());
            }else if("WHEN".equalsIgnoreCase(targetCmpProperty.getType())){
                List<CmpProperty> children = targetCmpProperty.getChildren();
                for (CmpProperty cmpProperty : children){
                    if("NodeComponent".equalsIgnoreCase(sourceCmpProperty.getType()) && "NodeComponent".equalsIgnoreCase(cmpProperty.getType())){
                        builder.addLine(IvyBuilderUtil.buildCommonLine(sourceNode, cmpProperty.getNode()).build());
                    }else if("THEN".equalsIgnoreCase(cmpProperty.getType())){
                        //buildLine(builder, targetCmpProperty);
                        builder.addLine(IvyBuilderUtil.buildCommonLine(sourceNode, cmpProperty.getChildren().get(0).getNode()).build());
                    }
                }
            }else if("WHEN".equalsIgnoreCase(sourceCmpProperty.getType())){
                List<CmpProperty> children = sourceCmpProperty.getChildren();
                for (CmpProperty cmpProperty : children){
                    if("NodeComponent".equalsIgnoreCase(targetCmpProperty.getType()) && "NodeComponent".equalsIgnoreCase(cmpProperty.getType())) {
                        builder.addLine(IvyBuilderUtil.buildCommonLine(cmpProperty.getNode(), targetNode).build());
                    }else if("THEN".equalsIgnoreCase(cmpProperty.getType())){
                        //buildLine(builder, sourceCmpProperty);
                        builder.addLine(IvyBuilderUtil.buildCommonLine(cmpProperty.getChildren().get(sourceCmpProperty.getChildren().size()-1).getNode(), targetNode).build());
                    }
                }
            }else if("THEN".equalsIgnoreCase(targetCmpProperty.getType())){
                buildLine(builder, targetCmpProperty);
                builder.addLine(IvyBuilderUtil.buildCommonLine(sourceNode, targetCmpProperty.getChildren().get(0).getNode()).build());
            }else if("THEN".equalsIgnoreCase(sourceCmpProperty.getType())){
                buildLine(builder, sourceCmpProperty);
                builder.addLine(IvyBuilderUtil.buildCommonLine(sourceCmpProperty.getChildren().get(sourceCmpProperty.getChildren().size()-1).getNode(), targetNode).build());
            }
        }
    }*/


    //将每对相邻元素作为子列表添加到结果列表中
    /*public static List<List<CmpProperty>> convertToAdjacentPairs(List<CmpProperty> list) {
        List<List<CmpProperty>> result = new ArrayList<>();
        for (int i = 0; i < list.size() - 1; i++) {
            List<CmpProperty> pair = new ArrayList<>();
            pair.add(list.get(i));
            pair.add(list.get(i + 1));
            result.add(pair);
        }
        return result;
    }*/
}
