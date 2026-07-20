package com.fastbee.rule.builder.line;

import java.util.UUID;

import cn.hutool.core.util.StrUtil;

import com.fastbee.rule.parser.entity.line.Line;
import com.fastbee.rule.parser.entity.line.LineData;
import com.fastbee.rule.parser.entity.node.Node;
import com.fastbee.rule.parser.enums.RuleEnums;

public class EasyFlowLineBuilder implements LineBuilder {

    private Line line = new Line();

    private RuleEnums.PATH_ENUM pathEnum;

    @Override
    public LineBuilder createLine(Node fromNode, Node toNode) {
        LineData lineData = new LineData();
        line.setFromNode(fromNode);
        line.setToNode(toNode);
        line.setId(UUID.randomUUID().toString());
        line.setType("animation");
        line.setFrom(fromNode.getId());
        line.setTo(toNode.getId());
        line.setData(lineData);
        line.setLabel("");
        line.setAnimated(true);
        line.setUpdatable(true);
        line.setMarkerEnd("arrowclosed");
        this.lineType(RuleEnums.PATH_ENUM.common_path);
        return this;
    }

    @Override
    public LineBuilder lineType(RuleEnums.PATH_ENUM pathEnum) {
        this.pathEnum = pathEnum;
        LineData data = line.getData();
        if(data != null){
            data.setType(pathEnum.getValue());
        }else{
            data = new LineData();
            data.setType(pathEnum.getValue());
            line.setData(data);
        }
        return this;
    }

    @Override
    public LineBuilder label(String label) {
        line.setLabel(label);
        return this;
    }

    @Override
    public LineBuilder data(LineData lineData) {
        if(lineData != null && StrUtil.isBlank(lineData.getType()) && pathEnum != null){
            lineData.setType(pathEnum.getValue());
        }
        line.setData(lineData);
        return this;
    }

    @Override
    public Line build() {
        return line;
    }

}
