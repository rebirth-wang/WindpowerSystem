package com.fastbee.rule.parser2.util;

import com.fastbee.rule.parser.entity.node.Node;
import com.fastbee.rule.parser.enums.RuleEnums;

public class RuleNodeUtil {

    public static boolean isNodeType(Node node, RuleEnums.NodeEnum nodeEnum) {
        if(nodeEnum.getSubType().equalsIgnoreCase(node.getType())){
            return true;
        }
        return false;
    }

    public static boolean isCommonNode(Node node) {
        return isNodeType(node, RuleEnums.NodeEnum.COMMON);
    }

    public static boolean isSwitchNode(Node node) {
        return isNodeType(node, RuleEnums.NodeEnum.SWITCH);
    }

    public static boolean isThenNode(Node node) {
        return isNodeType(node, RuleEnums.NodeEnum.THEN);
    }

    public static boolean isWhenNode(Node node) {
        return isNodeType(node, RuleEnums.NodeEnum.WHEN);
    }

    public static boolean isIfNode(Node node) {
        return isNodeType(node, RuleEnums.NodeEnum.IF);
    }

    public static boolean isWhileNode(Node node) {
        return isNodeType(node, RuleEnums.NodeEnum.WHILE);
    }

    public static boolean isForNode(Node node) {
        return isNodeType(node, RuleEnums.NodeEnum.FOR);
    }

    public static boolean isIteratorNode(Node node) {
        return isNodeType(node, RuleEnums.NodeEnum.ITERATOR);
    }

    public static boolean isBooleanNode(Node node) {
        return isNodeType(node, RuleEnums.NodeEnum.BOOLEAN);
    }

    public static boolean isSubflowNode(Node node) {
        return isNodeType(node, RuleEnums.NodeEnum.SUB_FLOW);
    }

    public static boolean isSubvarNode(Node node) {
        return isNodeType(node, RuleEnums.NodeEnum.SUB_VAR);
    }

    public static boolean isChainNode(Node node) {
        return isNodeType(node, RuleEnums.NodeEnum.CHAIN);
    }

    public static boolean isRouterNode(Node node) {
        return isNodeType(node, RuleEnums.NodeEnum.ROUTER);
    }
    public static boolean isAndNode(Node node) {
        return isNodeType(node, RuleEnums.NodeEnum.AND);
    }
    public static boolean isOrNode(Node node) {
        return isNodeType(node, RuleEnums.NodeEnum.OR);
    }
    public static boolean isNotNode(Node node) {
        return isNodeType(node, RuleEnums.NodeEnum.NOT);
    }
}
