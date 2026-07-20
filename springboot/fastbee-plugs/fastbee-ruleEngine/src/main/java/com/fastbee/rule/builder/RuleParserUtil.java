package com.fastbee.rule.builder;

import com.fastbee.rule.builder.flow.FlowBuilder;
import com.fastbee.rule.parser.entity.FlowData;
import com.fastbee.rule.parser.execption.LiteFlowELException;
import com.fastbee.rule.parser.graph.Graph;
import com.fastbee.rule.parser.graph.GraphInfo;

public class RuleParserUtil {


    public static GraphInfo parserFlow(FlowBuilder builder) throws LiteFlowELException {
        return parserFlow(builder.getFlowData());
    }


    public static GraphInfo parserFlow(String flowJson) throws LiteFlowELException {
        return new Graph(flowJson).toELInfo();
    }

    public static GraphInfo parserFlow(FlowData flowData) throws LiteFlowELException {
        return new Graph(flowData).toELInfo();
    }
}
