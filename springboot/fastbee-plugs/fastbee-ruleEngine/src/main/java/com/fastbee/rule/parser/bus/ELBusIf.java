package com.fastbee.rule.parser.bus;

import java.util.List;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.yomahub.liteflow.builder.el.*;

import com.fastbee.rule.parser.entity.node.Node;
import com.fastbee.rule.parser.entity.node.NodeData;
import com.fastbee.rule.parser.entity.node.NodeDataIf;
import com.fastbee.rule.parser.enums.RuleEnums;
import com.fastbee.rule.parser.execption.LiteFlowELException;
import com.fastbee.rule.parser2.GraphEL;
import com.fastbee.rule.parser2.util.RuleGraphUtil;

public class ELBusIf extends BaseELBus {

    public static IfELWrapper node(String node, String trueOpt, String falseOpt){
        IfELWrapper ifELWrapper = ELBus.ifOpt(ELBusNode.node(node), trueOpt, falseOpt);
        return ifELWrapper;
    }

    public static IfELWrapper node(Node node) throws LiteFlowELException {
        try {
            NodeData data = node.getNodeData();
            NodeDataIf nodeDataIf = data.getNodeDataIf();
            IfELWrapper wrapper = null;
            if(nodeDataIf != null){
                wrapper = ELBus.ifOpt(ELBusNode.node(node), nodeDataIf.getTrueOpt(), nodeDataIf.getFalseOpt());
            }
            return wrapper;
        }catch (Exception e){
            e.printStackTrace();
            throw new LiteFlowELException(
                    "param is error！未设置参数或路径！"+System.lineSeparator()+
                    "节点ID = "+node.getId()+System.lineSeparator()+
                    "组件ID = "+node.getNodeData().getId()
            );
        }
    }

    public static IfELWrapper node(Node node, ELWrapper trueOpt, ELWrapper falseOpt) throws LiteFlowELException {
        IfELWrapper ifELWrapper = null;
        if(falseOpt != null){
            ifELWrapper = ELBus.ifOpt(ELBusNode.node(node), trueOpt, falseOpt);
        }else{
            ifELWrapper = ELBus.ifOpt(ELBusNode.node(node), trueOpt);
        }
        return ifELWrapper;
    }

    public static IfELWrapper node(String object, ELWrapper trueOpt, ELWrapper falseOpt){
        IfELWrapper ifELWrapper = null;
        if(falseOpt != null){
            ifELWrapper = ELBus.ifOpt((String) object, trueOpt, falseOpt);
        }else{
            ifELWrapper = ELBus.ifOpt((String) object, trueOpt);
        }
        return ifELWrapper;
    }

    public static IfELWrapper node(Node node, Object object, ELWrapper trueOpt, ELWrapper falseOpt) throws LiteFlowELException {
        IfELWrapper wrapper = null;
        if(object == null){
            wrapper = node(node, trueOpt, falseOpt);
        }else{
            if(object instanceof String){
                if(falseOpt != null){
                    wrapper = ELBus.ifOpt((String) object, trueOpt, falseOpt);
                }else{
                    wrapper = ELBus.ifOpt((String) object, trueOpt);
                }
            }else if(object instanceof NodeELWrapper){
                if(falseOpt != null) {
                    wrapper = ELBus.ifOpt((NodeELWrapper) object, trueOpt, falseOpt);
                }else{
                    wrapper = ELBus.ifOpt((NodeELWrapper) object, trueOpt);
                }
            }else if(object instanceof AndELWrapper){
                if(falseOpt != null) {
                    wrapper = ELBus.ifOpt((AndELWrapper) object, trueOpt, falseOpt);
                }else{
                    wrapper = ELBus.ifOpt((AndELWrapper) object, trueOpt);
                }
            }else if(object instanceof OrELWrapper){
                if(falseOpt != null) {
                    wrapper = ELBus.ifOpt((OrELWrapper) object, trueOpt, falseOpt);
                }else{
                    wrapper = ELBus.ifOpt((OrELWrapper) object, trueOpt);
                }
            }else if(object instanceof NotELWrapper){
                if(falseOpt != null) {
                    wrapper = ELBus.ifOpt((NotELWrapper) object, trueOpt, falseOpt);
                }else{
                    wrapper = ELBus.ifOpt((NotELWrapper) object, trueOpt);
                }
            }
        }

         setId(wrapper, node);
         setTag(wrapper, node);
        // setMaxWaitSeconds(wrapper, node);
        // setRetry(wrapper, node);
        return wrapper;
    }

    public static IfELWrapper node(Node node, Node specialJoinNode, GraphEL graph) throws LiteFlowELException {
        if(specialJoinNode == null){
            specialJoinNode = graph.findSpecialJoinNode(node);
        }
        List<Node> specialJoinNodeList = null;
        if(specialJoinNode != null){
            specialJoinNodeList = graph.path2List(graph.findAllPaths(specialJoinNode));
        }

        List<Node> trueNodeList = graph.getSpecialPathNodes(node, RuleEnums.PATH_ENUM.true_path);
        List<Node> falseNodeList = graph.getSpecialPathNodes(node, RuleEnums.PATH_ENUM.false_path);
        List<Node> booleanNodeList = graph.getSpecialPathNodes(node, RuleEnums.PATH_ENUM.boolean_path);
        if(CollUtil.isNotEmpty(specialJoinNodeList)){
            trueNodeList.removeAll(specialJoinNodeList);
            falseNodeList.removeAll(specialJoinNodeList);
            booleanNodeList.removeAll(specialJoinNodeList);
        }

        IfELWrapper elWrapper = null;
        ELWrapper booleanElWrapper = null;
        if(CollUtil.isNotEmpty(booleanNodeList)){
            booleanElWrapper = ELBusBoolean.getBooleanELWrapper(graph, booleanNodeList);
        }else if(StrUtil.isBlank(node.getNodeData().getNodeDataBase().getClazz())){
            throw new LiteFlowELException("未设置boolean路径或组件类！");
        }
        if(CollUtil.isNotEmpty(trueNodeList) && CollUtil.isNotEmpty(falseNodeList)){
            ELWrapper trueELWrapper = null;
            if(trueNodeList.size() == 1){
                trueELWrapper = ELBusNode.node(trueNodeList.get(0));
            }else{
                List<Node> nodes = graph.path2List(RuleGraphUtil.findAllPathByNodes(trueNodeList, graph));
                GraphEL trueGraph = new GraphEL(nodes, graph.getLines(nodes));
                trueELWrapper = trueGraph.toELWrapper();
            }
            ELWrapper falseELWrapper = null;
            if(falseNodeList.size() == 1){
                falseELWrapper = ELBusNode.node(falseNodeList.get(0));
            }else{
                List<Node> nodes = graph.path2List(RuleGraphUtil.findAllPathByNodes(falseNodeList, graph));
                GraphEL falseGraph = new GraphEL(nodes, graph.getLines(nodes));
                falseELWrapper = falseGraph.toELWrapper();
            }
            IfELWrapper ifELWrapper = ELBusIf.node(node, booleanElWrapper, trueELWrapper, falseELWrapper);
            return ifELWrapper;
        }else if(CollUtil.isNotEmpty(trueNodeList) && CollUtil.isEmpty(falseNodeList)){
            ELWrapper trueELWrapper = null;
            if(trueNodeList.size() == 1){
                trueELWrapper = ELBusNode.node(trueNodeList.get(0));
            }else{
                List<Node> nodes = graph.path2List(RuleGraphUtil.findAllPathByNodes(trueNodeList, graph));
                GraphEL trueGraph = new GraphEL(nodes, graph.getLines(nodes));
                trueELWrapper = trueGraph.toELWrapper();
            }
            IfELWrapper ifELWrapper = ELBusIf.node(node, booleanElWrapper, trueELWrapper, null);
            return ifELWrapper;
        }else if(CollUtil.isEmpty(trueNodeList)){
            throw new LiteFlowELException("未设置true路径！");
        }else if(CollUtil.isEmpty(falseNodeList)){
            throw new LiteFlowELException("未设置false路径！");
        }
        return elWrapper;
    }
}
