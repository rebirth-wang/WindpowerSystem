package com.fastbee.rule.parser.bus;

import cn.hutool.core.util.StrUtil;
import com.yomahub.liteflow.builder.el.CatchELWrapper;
import com.yomahub.liteflow.builder.el.ELBus;
import com.yomahub.liteflow.builder.el.ELWrapper;
import com.yomahub.liteflow.builder.el.ThenELWrapper;

import com.fastbee.rule.parser.entity.line.Line;
import com.fastbee.rule.parser.entity.node.Node;
import com.fastbee.rule.parser.entity.node.NodeDataThen;
import com.fastbee.rule.parser.execption.LiteFlowELException;

public class ELBusThen extends BaseELBus {

    public static ThenELWrapper node(Object... objects){
        return ELBus.then(objects);
    }

    public static ThenELWrapper node(Node node) throws LiteFlowELException {
        return ELBus.then(ELBusNode.node(node));
    }

    public static ThenELWrapper scriptNode(Node node) throws LiteFlowELException {
        return ELBus.then(ELBusNode.scriptNode(node));
    }

    public static ThenELWrapper then() {
        return ELBus.then();
    }

    public static ThenELWrapper then(Line edge) {
        ThenELWrapper wrapper = ELBus.then();
        if(edge != null && edge.getData() != null){
            setId(wrapper, edge.getId());
            setTag(wrapper, edge.getLabel());
        }
        return wrapper;
    }

    public static ELWrapper then(ThenELWrapper thenELWrapper, Node thenNode) {
        setId(thenELWrapper, thenNode.getId());
        setTag(thenELWrapper, thenNode.getLabel());
        NodeDataThen nodeDataThen = thenNode.getNodeData().getNodeDataThen();
        if (nodeDataThen != null) {
            if(nodeDataThen.getIsCatch() != null && nodeDataThen.getIsCatch()){
                CatchELWrapper catchELWrapper = ELBusCatch.catchException(thenELWrapper);
                if(StrUtil.isNotBlank(nodeDataThen.getCatchDo())){
                    catchELWrapper.doOpt(nodeDataThen.getCatchDo());
                }
                return catchELWrapper;
            }
        }
        return thenELWrapper;
    }

//    public static ThenELWrapper node(Node node){
//        ThenELWrapper wrapper = ELBus.then(ELBusNode.node(node));
//        if(node.getPreEL() != null){
//            wrapper.pre(node.getPreEL());
//        }else if(StrUtil.isNotBlank(node.getCmpPre())){
//            wrapper.pre(node.getCmpPre());
//        }
//        if(node.getFinallyEL() != null) {
//            wrapper.finallyOpt(node.getFinallyEL());
//        }else if(StrUtil.isNotBlank(node.getCmpFinallyOpt())){
//            wrapper.finallyOpt(node.getCmpFinallyOpt());
//        }
//        setId(wrapper, node);
//        setTag(wrapper, node);
//        setMaxWaitSeconds(wrapper, node);
//        return wrapper;
//    }

}
