package com.fastbee.rule.builder.flow;

import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;

import com.fastbee.rule.parser.entity.FlowData;
import com.fastbee.rule.parser.entity.Viewport;
import com.fastbee.rule.parser.entity.line.Line;
import com.fastbee.rule.parser.entity.node.*;
import com.fastbee.rule.util.RuleGsonUtil;

public class EasyFlowBuilder implements FlowBuilder {

    private FlowData flowData = new FlowData();

    @Override
    public FlowBuilder defaultData() {
        flowData.setNodes(new ArrayList<>());
        flowData.setLines(new ArrayList<>());
        flowData.setPosition(CollUtil.toList(0d,0d));
        flowData.setZoom(1d);
        flowData.setViewport(new Viewport(0d,0d,1d));
        return this;
    }

    @Override
    public FlowBuilder addNode(Node... nodes) {
        if(ArrayUtil.isNotEmpty(nodes)){
            for(Node node : nodes){
                if(node != null){
                    flowData.getNodes().add(node);
                }
            }
        }
        return this;
    }

    @Override
    public FlowBuilder addLine(Line... edges) {
        if(ArrayUtil.isNotEmpty(edges)){
            for(Line edge : edges){
                if(edge != null) {
                    flowData.getLines().add(edge);
                }
            }
        }
        return this;
    }

    @Override
    public FlowBuilder format(boolean format) {
        flowData.setFormat(format);
        return this;
    }

    @Override
    public String build() {
        return RuleGsonUtil.getGson(flowData.getFormat()).toJson(flowData);
    }

    @Override
    public FlowData getFlowData() {
        return flowData;
    }

    @Override
    public List<Node> getNodes() {
        return flowData.getNodes();
    }

    @Override
    public List<Line> getLines() {
        return flowData.getLines();
    }

    @Override
    public Node getNodeByCmpId(String id) {
        assert id != null;
        return getNodes().stream().filter(m->id.equals(m.getNodeData().getId())).findFirst().orElse(null);
    }
    @Override
    public Node getNodeById(String id) {
        assert id != null;
        return getNodes().stream().filter(m->id.equals(m.getId())).findFirst().orElse(null);
    }

    /*public static Node buildNode(CmpProperty property){
        String nodeId = property.getId();
        String type = property.getType();

        Node node = new Node();
        node.setId(UUID.randomUUID().toString());
        node.setType("common");
        node.setLabel(nodeId);
        node.setPosition(new Position(0d,0d));

        NodeData nodeData = new NodeData();
        nodeData.setId(nodeId);
        nodeData.setName(nodeId);
        nodeData.setType("common");
        nodeData.setMode("default");
        nodeData.setNodeDataBase(new NodeDataBase());
        nodeData.setNodeDataSwitch(new NodeDataSwitch());
        nodeData.setNodeDataIf(new NodeDataIf());

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

        node.setData(nodeData);
        node.setParentNode(null);
        node.setInitialized(false);

        property.setNode(node);
        return node;
    }*/

}
