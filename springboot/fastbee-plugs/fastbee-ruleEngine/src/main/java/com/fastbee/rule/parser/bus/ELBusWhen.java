package com.fastbee.rule.parser.bus;

import cn.hutool.core.util.StrUtil;
import com.yomahub.liteflow.builder.el.CatchELWrapper;
import com.yomahub.liteflow.builder.el.ELBus;
import com.yomahub.liteflow.builder.el.ELWrapper;
import com.yomahub.liteflow.builder.el.WhenELWrapper;

import com.fastbee.rule.parser.entity.node.Node;
import com.fastbee.rule.parser.entity.node.NodeDataWhen;

public class ELBusWhen extends BaseELBus {

    public static WhenELWrapper node(Object... objects){
        return ELBus.when(objects);
    }

    public static ELWrapper when(WhenELWrapper whenELWrapper, Node whenNode) {
        NodeDataWhen nodeDataWhen = whenNode.getNodeData().getNodeDataWhen();
        setId(whenELWrapper, whenNode.getId());
        setTag(whenELWrapper, whenNode.getLabel());
        if(nodeDataWhen != null){
            if(nodeDataWhen.getIgnoreError() != null && nodeDataWhen.getIgnoreError()){
                whenELWrapper.ignoreError(nodeDataWhen.getIgnoreError());
            }
            String ignoreType = nodeDataWhen.getIgnoreType();
            if(StrUtil.isNotBlank(ignoreType)){
                switch (ignoreType){
                    case "any":
                        whenELWrapper.any(true);
                        break;
                    case "must":
                        if(StrUtil.isNotBlank(nodeDataWhen.getMustId())){
                            whenELWrapper.must(nodeDataWhen.getMustId().split(","));
                        }
                        break;
                }
            }
            if(StrUtil.isNotBlank(nodeDataWhen.getCustomThreadExecutor())){
                whenELWrapper.customThreadExecutor(nodeDataWhen.getCustomThreadExecutor());
            }

            if(nodeDataWhen.getIsCatch() != null && nodeDataWhen.getIsCatch()){
                CatchELWrapper catchELWrapper = ELBusCatch.catchException(whenELWrapper);
                if(StrUtil.isNotBlank(nodeDataWhen.getCatchDo())){
                    catchELWrapper.doOpt(nodeDataWhen.getCatchDo());
                }
                return catchELWrapper;
            }
        }
        return whenELWrapper;
    }
}
