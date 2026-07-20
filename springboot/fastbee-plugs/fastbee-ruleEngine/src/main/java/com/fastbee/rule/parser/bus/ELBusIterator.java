package com.fastbee.rule.parser.bus;

import java.util.List;

import cn.hutool.core.collection.CollUtil;
import com.yomahub.liteflow.builder.el.ELBus;
import com.yomahub.liteflow.builder.el.ELWrapper;
import com.yomahub.liteflow.builder.el.LoopELWrapper;
import com.yomahub.liteflow.builder.el.NodeELWrapper;
import com.yomahub.liteflow.enums.NodeTypeEnum;

import com.fastbee.rule.parser.entity.node.Node;
import com.fastbee.rule.parser.enums.RuleEnums;
import com.fastbee.rule.parser.execption.LiteFlowELException;
import com.fastbee.rule.parser2.GraphEL;
import com.fastbee.rule.parser2.util.RuleGraphUtil;

public class ELBusIterator extends BaseELBus {

    public static LoopELWrapper node(NodeELWrapper nodeElWrapper) {
        return ELBus.iteratorOpt(nodeElWrapper);
    }

    public static LoopELWrapper node(String nodeElWrapper) {
        return ELBus.iteratorOpt(nodeElWrapper);
    }

    public static LoopELWrapper node(Node node) throws LiteFlowELException {
        LoopELWrapper wrapper = ELBus.iteratorOpt(ELBusNode.node(node));

        setId(wrapper, node);
        setTag(wrapper, node);
        // setMaxWaitSeconds(wrapper, node);
        // setRetry(wrapper, node);
        // setParallel(wrapper, node);
        return wrapper;
    }

    public static ELWrapper buildIteratorELWrapper(GraphEL graphEL, Node node, Node specialJoinNode) throws LiteFlowELException {
        if(specialJoinNode == null){
            specialJoinNode = graphEL.findSpecialJoinNode(node);
        }
        List<Node> specialJoinNodeList = null;
        if(specialJoinNode != null){
            specialJoinNodeList = graphEL.path2List(graphEL.findAllPaths(specialJoinNode));
        }

        LoopELWrapper elWrapper = ELBusIterator.node(node);
        List<Node> doNodeList = graphEL.getSpecialPathNodes(node, RuleEnums.PATH_ENUM.do_path);
        List<Node> breakNodeList = graphEL.getSpecialPathNodes(node, RuleEnums.PATH_ENUM.break_path);
        if(CollUtil.isNotEmpty(specialJoinNodeList)){
            doNodeList.removeAll(specialJoinNodeList);
            breakNodeList.removeAll(specialJoinNodeList);
        }

        if(CollUtil.isNotEmpty(doNodeList)){
            if(doNodeList.size() == 1){
                elWrapper.doOpt(ELBusNode.node(doNodeList.get(0)));
            }else {
                List<Node> nodes = graphEL.path2List(RuleGraphUtil.findAllPathByNodes(doNodeList, graphEL));
                GraphEL doGraph = new GraphEL(nodes, graphEL.getLines(nodes));
                elWrapper.doOpt(doGraph.toELWrapper());
            }
        }
        if(CollUtil.isNotEmpty(breakNodeList)){
            if(breakNodeList.size() > 1){
                throw new LiteFlowELException("break路径仅支持单个【布尔组件】！");
            }
            Node breakNode = breakNodeList.get(0);
            String type = breakNode.getNodeData().getType();
            if(NodeTypeEnum.BOOLEAN.getCode().equals(type) || NodeTypeEnum.BOOLEAN_SCRIPT.getCode().equals(type)){
                elWrapper.breakOpt(ELBusNode.node(breakNode));
            }else{
                throw new LiteFlowELException("break路径仅支持【布尔组件】！");
            }
        }
        return elWrapper;
    }

//    public static IteratorELWrapper node(Node node){
//        Object doOpt = node.getCmpDoOptEL() != null ? node.getCmpDoOptEL() : node.getCmpDoOpt();
//        IteratorELWrapper wrapper = ELBus.iteratorOpt(ELBusNode.node(node));
//        wrapper.doOpt(doOpt);
//        wrapper.parallel(node.getCmpParallel());
//        setId(wrapper, node);
//        setTag(wrapper, node);
//        setMaxWaitSeconds(wrapper, node);
//        return wrapper;
//    }

}
