package com.fastbee.rule.builder.node;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fastbee.rule.parser.entity.node.*;
import com.fastbee.rule.parser.entity.style.NodeStyle;
import com.fastbee.rule.parser.entity.style.NodeStyleHandles;
import com.fastbee.rule.parser.entity.style.NodeStyleToolbar;
import com.fastbee.rule.parser.enums.RuleEnums;

public class EasyFlowNodeBuilder implements NodeBuilder {

    private Node node = new Node();

    public EasyFlowNodeBuilder() {
    }

    public EasyFlowNodeBuilder(Node node) {
        this.node = node;
    }

    @Override
    public NodeBuilder initData(RuleEnums.NodeEnum nodeEnum) {
        initData(nodeEnum.getType(), nodeEnum.getSubType());
        return this;
    }

    @Override
    public NodeBuilder initData(String type, String subType) {
        node.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        node.setType(type);

        node.setPosition(new Position(0d,0d));

        NodeData nodeData = new NodeData();
        nodeData.setType(subType);
        nodeData.setMode("default");
        NodeDataBase dataBase = new NodeDataBase();
        dataBase.setClassType(1);
        dataBase.setRetryExceptions(new String[]{});
        nodeData.setNodeDataBase(dataBase);

        //样式设置
        NodeStyle nodeStyle = new NodeStyle();
        nodeStyle.setToolbar(new NodeStyleToolbar("top",false,10));
        List<NodeStyleHandles> handles = new ArrayList<>();
        handles.add(new NodeStyleHandles(UUID.randomUUID().toString(),"left",1d,"target",""));
        handles.add(new NodeStyleHandles(UUID.randomUUID().toString(),"right",1d,"source",""));
        handles.add(new NodeStyleHandles(UUID.randomUUID().toString(),"top",0d,"target",""));
        handles.add(new NodeStyleHandles(UUID.randomUUID().toString(),"bottom",0d,"source",""));
        nodeStyle.setHandles(handles);
        nodeStyle.setExtendHandles(new ArrayList<>());
        nodeData.setStyle(nodeStyle);

        node.setNodeData(nodeData);
        return this;
    }

    @Override
    public NodeBuilder createNode(RuleEnums.NodeEnum nodeEnum) {
        this.initData(nodeEnum);
        return this;
    }

    @Override
    public NodeBuilder id(String id) {
        node.getNodeData().setId(id);
        return this;
    }

    @Override
    public NodeBuilder name(String name) {
        node.getNodeData().setName(name);
        node.setLabel(name);
        return this;
    }

    @Override
    public NodeBuilder baseData(NodeDataBase data) {
        node.getNodeData().setNodeDataBase(data);
        return this;
    }

    @Override
    public NodeBuilder chainData(NodeDataChain data) {
        node.getNodeData().setNodeDataChain(data);
        return this;
    }

    @Override
    public NodeBuilder contextData(NodeDataContext data) {
        node.getNodeData().setNodeDataContext(data);
        return this;
    }

    @Override
    public NodeBuilder ifData(NodeDataIf data) {
        node.getNodeData().setNodeDataIf(data);
        return this;
    }

    @Override
    public NodeBuilder loopData(NodeDataLoop data) {
        node.getNodeData().setNodeDataLoop(data);
        return this;
    }

    @Override
    public NodeBuilder routerData(NodeDataRouter data) {
        node.getNodeData().setNodeDataRouter(data);
        return this;
    }

    @Override
    public NodeBuilder subFlowData(NodeDataSubFlow data) {
        node.getNodeData().setNodeDataSubflow(data);
        return this;
    }

    @Override
    public NodeBuilder subVarData(NodeDataSubVar data) {
        node.getNodeData().setNodeDataSubvar(data);
        return this;
    }

    @Override
    public NodeBuilder switchData(NodeDataSwitch data) {
        node.getNodeData().setNodeDataSwitch(data);
        return this;
    }

    @Override
    public NodeBuilder thenData(NodeDataThen data) {
        node.getNodeData().setNodeDataThen(data);
        return this;
    }

    @Override
    public NodeBuilder whenData(NodeDataWhen data) {
        node.getNodeData().setNodeDataWhen(data);
        return this;
    }


    @Override
    public Node build() {
        return node;
    }
}
