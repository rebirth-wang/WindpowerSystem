package com.fastbee.rule.parser.bus;

import com.yomahub.liteflow.builder.el.ELWrapper;

import com.fastbee.rule.parser.entity.line.Line;

public class ELBusBuilder extends BaseELBus {

    public static void setId(ELWrapper wrapper, Line edge) {
        if (edge != null && edge.getData() != null) {
            setId(wrapper, edge.getData().getId());
        }
    }

    public static void setTag(ELWrapper wrapper, Line edge) {
        if (edge != null && edge.getValue() != null) {
            setTag(wrapper, edge.getValue());
        }
    }

    public static void setTag(ELWrapper wrapper, Line edge, String index) {
        if (edge != null && edge.getValue() != null) {
            setTag(wrapper, edge.getValue());
        } else {
            setTag(wrapper, index);
        }
    }
}
