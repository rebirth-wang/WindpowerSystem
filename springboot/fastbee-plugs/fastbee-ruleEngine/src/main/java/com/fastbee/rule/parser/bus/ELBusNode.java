package com.fastbee.rule.parser.bus;

import com.google.gson.internal.LinkedTreeMap;
import com.yomahub.liteflow.builder.LiteFlowNodeBuilder;
import com.yomahub.liteflow.builder.el.ELBus;
import com.yomahub.liteflow.builder.el.NodeELWrapper;
import com.yomahub.liteflow.flow.FlowBus;
import com.yomahub.liteflow.meta.LiteflowMetaOperator;
import lombok.extern.slf4j.Slf4j;

import com.fastbee.rule.parser.entity.node.Node;
import com.fastbee.rule.parser.execption.LiteFlowELException;

@Slf4j
public class ELBusNode extends BaseELBus {

    public static NodeELWrapper node(String nodeId) {
        return ELBus.node(nodeId);
    }

    public static NodeELWrapper node(Node node) throws LiteFlowELException {
        NodeELWrapper wrapper = ELBus.node(node.getType());
        // 设置节点参数 node-> nodeData
        // setId(wrapper, node);
        setTag(wrapper, node);
        // 设置节点属性
        setData(wrapper, node.getId(), node.getData());
        // setData(wrapper, node);
        // setMaxWaitSeconds(wrapper, node);
        // setRetry(wrapper, node);
        return wrapper;
    }

    public static NodeELWrapper scriptNode(Node node) throws LiteFlowELException {
        NodeELWrapper wrapper = ELBus.node(node.getId());
        LinkedTreeMap map = (LinkedTreeMap) node.getData();
        if (FlowBus.getNode(node.getId()) == null) {
            LiteFlowNodeBuilder.createScriptNode().setId(node.getId())
                    .setName(map.get("scriptName").toString())
                    .setScript(map.get("scriptData").toString())
                    .build();
            log.info("重新加载脚本：{}", node.getId());
        } else {
            LiteflowMetaOperator.reloadScript(node.getId(), map.get("scriptData").toString());
        }
        // 设置节点参数 node-> nodeData
        // setId(wrapper, node);
        setTag(wrapper, node);

        setData(wrapper, node.getId()+"_data", node.getData());
        return wrapper;
    }

}
