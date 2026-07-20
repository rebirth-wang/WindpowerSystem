package com.fastbee.rule.builder.node;

import com.fastbee.rule.parser.entity.node.*;
import com.fastbee.rule.parser.enums.RuleEnums;

public interface NodeBuilder {

    NodeBuilder initData(RuleEnums.NodeEnum nodeEnum);
    NodeBuilder initData(String type, String subType);

    NodeBuilder createNode(RuleEnums.NodeEnum nodeEnum);

    NodeBuilder id(String id);

    NodeBuilder name(String name);

    NodeBuilder baseData(NodeDataBase data);
    NodeBuilder chainData(NodeDataChain data);
    NodeBuilder contextData(NodeDataContext data);
    NodeBuilder ifData(NodeDataIf data);
    NodeBuilder loopData(NodeDataLoop data);
    NodeBuilder routerData(NodeDataRouter data);
    NodeBuilder subFlowData(NodeDataSubFlow data);
    NodeBuilder subVarData(NodeDataSubVar data);
    NodeBuilder switchData(NodeDataSwitch data);
    NodeBuilder thenData(NodeDataThen data);
    NodeBuilder whenData(NodeDataWhen data);

    Node build();

}
