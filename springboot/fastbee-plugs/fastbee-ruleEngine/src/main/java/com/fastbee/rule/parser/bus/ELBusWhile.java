package com.fastbee.rule.parser.bus;

import java.util.List;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.yomahub.liteflow.builder.el.*;
import com.yomahub.liteflow.enums.NodeTypeEnum;

import com.fastbee.rule.parser.entity.node.Node;
import com.fastbee.rule.parser.entity.node.NodeData;
import com.fastbee.rule.parser.entity.node.NodeDataLoop;
import com.fastbee.rule.parser.enums.RuleEnums;
import com.fastbee.rule.parser.execption.LiteFlowELException;
import com.fastbee.rule.parser2.GraphEL;
import com.fastbee.rule.parser2.util.RuleGraphUtil;

public class ELBusWhile extends BaseELBus {

    public static LoopELWrapper node(NodeELWrapper nodeElWrapper) {
        return ELBus.whileOpt(nodeElWrapper);
    }

    public static LoopELWrapper node(String nodeElWrapper) {
        return ELBus.whileOpt(nodeElWrapper);
    }

    public static LoopELWrapper node(AndELWrapper andElWrapper) {
        return ELBus.whileOpt(andElWrapper);
    }

    public static LoopELWrapper node(OrELWrapper orElWrapper) {
        return ELBus.whileOpt(orElWrapper);
    }

    public static LoopELWrapper node(NotELWrapper notElWrapper) {
        return ELBus.whileOpt(notElWrapper);
    }

    /*public static LoopELWrapper node(GraphEL graphEL, Node node) throws LiteFlowELException {
        NodeData data = node.getData();
        NodeDataLoop nodeDataLoop = data.getNodeDataLoop();
        Boolean parallel = nodeDataLoop.getParallel();

        //Object doOpt = node.getCmpDoOptEL() != null ? node.getCmpDoOptEL() : node.getCmpDoOpt();
        //Object breakOpt = node.getCmpBreakOptEL() != null ? node.getCmpBreakOptEL() : node.getCmpBreakOpt();
        LoopELWrapper wrapper = ELBus.whileOpt(ELBusNode.node(node));
        //wrapper.doOpt(doOpt);
        //wrapper.breakOpt(breakOpt);
        if(parallel != null){
            wrapper.parallel(parallel);
        }else{
            wrapper.parallel(false);
        }
        setId(wrapper, node);
        setTag(wrapper, node);
        setMaxWaitSeconds(wrapper, node);
        return wrapper;
    }*/

    public static LoopELWrapper wrapper(Node node, Object object){
        LoopELWrapper wrapper = null;
        if(object instanceof String){
            wrapper = ELBus.whileOpt((String) object);
        }else if(object instanceof NodeELWrapper){
            wrapper = ELBus.whileOpt((NodeELWrapper) object);
        }else if(object instanceof AndELWrapper){
            wrapper = ELBus.whileOpt((AndELWrapper) object);
        }else if(object instanceof OrELWrapper){
            wrapper = ELBus.whileOpt((OrELWrapper) object);
        }else if(object instanceof NotELWrapper){
            wrapper = ELBus.whileOpt((NotELWrapper) object);
        }
        setId(wrapper, node);
        setTag(wrapper, node);
        // setMaxWaitSeconds(wrapper, node);
        // setRetry(wrapper, node);
        // setParallel(wrapper, node);
        return wrapper;
    }

    public static ELWrapper buildWhileELWrapper(GraphEL graph, Node node, Node specialJoinNode) throws LiteFlowELException {
        if(specialJoinNode == null){
            specialJoinNode = graph.findSpecialJoinNode(node);
        }
        List<Node> specialJoinNodeList = null;
        if(specialJoinNode != null){
            specialJoinNodeList = graph.path2List(graph.findAllPaths(specialJoinNode));
        }

        NodeData data = node.getNodeData();
        NodeDataLoop nodeDataLoop = data.getNodeDataLoop();
        Boolean parallel = nodeDataLoop.getParallel();

        List<Node> doNodeList = graph.getSpecialPathNodes(node, RuleEnums.PATH_ENUM.do_path);
        List<Node> breakNodeList = graph.getSpecialPathNodes(node, RuleEnums.PATH_ENUM.break_path);
        List<Node> booleanNodeList = graph.getSpecialPathNodes(node, RuleEnums.PATH_ENUM.boolean_path);
        if(CollUtil.isNotEmpty(specialJoinNodeList)){
            doNodeList.removeAll(specialJoinNodeList);
            breakNodeList.removeAll(specialJoinNodeList);
            booleanNodeList.removeAll(specialJoinNodeList);
        }

        LoopELWrapper elWrapper = null;
        if(CollUtil.isNotEmpty(booleanNodeList)){
            ELWrapper booleanElWrapper = ELBusBoolean.getBooleanELWrapper(graph, booleanNodeList);
            elWrapper = ELBusWhile.wrapper(node, booleanElWrapper);
        }else if(StrUtil.isNotBlank(data.getNodeDataBase().getClazz())){
            elWrapper = ELBus.whileOpt(ELBusNode.node(node));
        }else{
            throw new LiteFlowELException("未设置boolean路径或组件类！");
        }
        if(CollUtil.isNotEmpty(doNodeList)){
            if(doNodeList.size() == 1){
                elWrapper.doOpt(ELBusNode.node(doNodeList.get(0)));
            }else{
                List<Node> nodes = graph.path2List(RuleGraphUtil.findAllPathByNodes(doNodeList, graph));
                GraphEL doGraph = new GraphEL(nodes, graph.getLines(nodes));
                elWrapper.doOpt(doGraph.toELWrapper());
            }
        }/*else{
            throw new LiteFlowELException("未设置do路径！");
        }*/

        if(CollUtil.isNotEmpty(breakNodeList)){
            if(breakNodeList.size() > 1){
                throw new LiteFlowELException("break路径仅支持单个【布尔组件】！");
            }
            Node breakNode = breakNodeList.get(0);
            if(NodeTypeEnum.BOOLEAN.getCode().equals(breakNode.getType())){
                elWrapper.breakOpt(ELBusNode.node(breakNode));
            }else{
                throw new LiteFlowELException("break路径仅支持【布尔组件】！");
            }
        }

        if(parallel != null){
            elWrapper.parallel(parallel);
        }else{
            elWrapper.parallel(false);
        }
        return elWrapper;
    }
}
