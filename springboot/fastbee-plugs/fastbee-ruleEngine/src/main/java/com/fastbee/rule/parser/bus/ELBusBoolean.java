package com.fastbee.rule.parser.bus;

import java.util.List;

import com.yomahub.liteflow.builder.el.AndELWrapper;
import com.yomahub.liteflow.builder.el.ELBus;
import com.yomahub.liteflow.builder.el.ELWrapper;
import com.yomahub.liteflow.builder.el.OrELWrapper;

import com.fastbee.rule.parser.entity.node.Node;
import com.fastbee.rule.parser.execption.LiteFlowELException;
import com.fastbee.rule.parser.graph.GraphUtil;
import com.fastbee.rule.parser2.GraphEL;
import com.fastbee.rule.parser2.util.RuleGraphUtil;

public class ELBusBoolean extends BaseELBus {

    public static ELWrapper getBooleanELWrapper(GraphEL graph, List<Node> booleanNodeList) throws LiteFlowELException {
        List<List<Node>> allPaths = RuleGraphUtil.findAllPathByNodes(booleanNodeList, graph);
        List<Node> segmentationPoints = GraphUtil.findSegmentationPoints(allPaths, graph.getNodes());
        List<List<Node>> processSegments = GraphUtil.getProcessSegments(allPaths, segmentationPoints);
        List<List<Node>> segments = GraphUtil.mergeSingleElementSegments(processSegments);
        if(!segments.isEmpty() && (segments.get(0).size() == 1 || "not".equalsIgnoreCase(segments.get(0).get(0).getType()))){
            return buildBooleanELWrapper(graph, segments.get(0).get(0));
        }else{
            throw new LiteFlowELException("只能存在一条boolean路径！");
        }
    }

    public static ELWrapper buildBooleanELWrapper(GraphEL graph, Node node) throws LiteFlowELException {
        switch (node.getType()){
            case "and":
                AndELWrapper andELWrapper = ELBusAnd.node();
                List<Node> andList = graph.getNeighbors(node);
                for (Node n : andList){
                    andELWrapper.and(buildBooleanELWrapper(graph, n));
                }
                return andELWrapper;
            case "or":
                OrELWrapper orELWrapper = ELBus.or();
                List<Node> orList = graph.getNeighbors(node);
                for (Node n : orList){
                    orELWrapper.or(buildBooleanELWrapper(graph, n));
                }
                return orELWrapper;
            case "not":
                List<Node> notList = graph.getNeighbors(node);
                ELWrapper elWrapper = buildBooleanELWrapper(graph, notList.get(0));
                return ELBusNot.not(elWrapper);
            case "boolean":
            case "boolean_script":
                return ELBusNode.node(node);
        }
        throw new LiteFlowELException("boolean路径仅支持【与、或、非、布尔组件】！");
    }
}
