package com.fastbee.rule.parser.entity.line;

import java.util.List;
import java.util.Map;

import lombok.Data;

import com.fastbee.rule.parser.entity.node.Node;
import com.fastbee.rule.parser.enums.RuleEnums;

@Data
public class Line {

    public String id;
    private String type;
    public String from;
    public String to;
    private String label;
    private String value;

    private String sourceHandle;
    private String targetHandle;
    private LineData data;
    private Boolean animated;
    private Boolean updatable;
    private String markerEnd;
    private Double sourceX;
    private Double sourceY;
    private Double targetX;
    private Double targetY;

    private Map<String,Object> style;
    private Map<String,Object> labelStyle;
    private List<Integer> labelBgPadding;
    private Integer labelBgBorderRadius;
    private Map<String,Object> labelBgStyle;

    private Node fromNode;
    private Node toNode;

    public Line() {
    }

    public Line(String from, String to) {
        this.from = from;
        this.to = to;
    }

    public boolean isCommonPath(){
        return RuleEnums.PATH_ENUM.common_path.value().equals(type);
    }

    //是否特殊路径
    public boolean isSpecialPath(){
        return !isCommonPath();
    }

    public boolean isPath(RuleEnums.PATH_ENUM pathEnum){
        return pathEnum.value().equals(type);
    }
}
