package com.fastbee.rule.parser.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.yomahub.liteflow.builder.el.ELWrapper;
import lombok.Data;

import com.fastbee.rule.parser.entity.node.NodeDataContext;

@Data
public class GraphInfo {

    private List<NodeDataContext> contextList;

    //主流程
    private Graph mainGraph;
    private ELWrapper mainGraphELWrapper;
    private String mainELStr;

    //子流程
    private List<Graph> subFlowGraphs;
    private Map<String,ELWrapper> subFlowELWrapperMap;
    private Map<String,String> subFlowELMap;

    private Graph routerGraph;

    private static final String rn = System.lineSeparator();

    @Override
    public String toString() {
        List<String> elList = new ArrayList<>();
        if(CollUtil.isNotEmpty(contextList)){
            elList.add("【上下文】");
            List<String> cList = new ArrayList<>();
            contextList.forEach(m->{
                cList.add("["+m.getAsName()+"] "+m.getFullClassName());
            });
            elList.add(String.join(rn, cList) + (StrUtil.isNotBlank(mainELStr) ? rn : ""));
        }
        if(CollUtil.isNotEmpty(subFlowGraphs)){
            List<String> elSubFlowList = new ArrayList<>();
            elSubFlowList.add("【子流程】");
            subFlowELMap.entrySet().forEach(m->{
                elSubFlowList.add("["+m.getKey()+"] "+m.getValue());
            });
            elList.add(String.join(rn, elSubFlowList));
        }
        if(routerGraph != null || CollUtil.isNotEmpty(subFlowGraphs)){
            elList.add("【主流程】");
        }
        elList.add(mainELStr);
        return String.join(rn, elList);
    }

}
