package com.fastbee.rule.parser.graph;

import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.google.gson.internal.LinkedTreeMap;
import com.yomahub.liteflow.builder.el.*;
import com.yomahub.liteflow.enums.NodeTypeEnum;
import lombok.extern.slf4j.Slf4j;

import com.fastbee.rule.parser.bus.*;
import com.fastbee.rule.parser.bus.ELBusCatch;
import com.fastbee.rule.parser.entity.line.Line;
import com.fastbee.rule.parser.entity.node.*;
import com.fastbee.rule.parser.enums.RuleEnums;
import com.fastbee.rule.parser.execption.LiteFlowELException;
import com.fastbee.rule.parser.wrapper.ELBusWrapper;

@Slf4j
public class GraphToEL {

    public static ELWrapper toEL(Graph graph) throws LiteFlowELException {
        //只有一个节点
        if (graph.getNodes().size() == 1) {
            Node node = graph.getAllNodes().get(0);
            String type = node.getType();
            switch (type) {
                case "script":
                    return ELBusThen.scriptNode(node);
                case "common":
                    return ELBusThen.node(node);
                case "switch":
                    return ELBusSwitch.node(node);
            }
        }
        List<List<List<Node>>> groupPaths = graph.getGroupFilterPaths();
        //只有一条路径
        if (groupPaths.size() == 1) {
            ELWrapper elWrapper = BaseELBus.buildELWrapper(graph.getStartNodes().get(0), graph);
            log.debug("toEL eLWrapper:{}", elWrapper.toEL());
            elWrapper = processSegments(elWrapper, graph, groupPaths.get(0), true);
            log.debug("toEL processSegments:{}", elWrapper.toEL());
            return elWrapper;
        } else if (groupPaths.size() > 1) {
            //多条路径
            WhenELWrapper whenELWrapper = ELBus.when();
            for (List<List<Node>> paths : groupPaths) {
                ELWrapper elWrapper = BaseELBus.buildELWrapper(paths.get(0).get(0), graph);
                processSegments(elWrapper, graph, paths, true);
                whenELWrapper.when(elWrapper);
            }
            return whenELWrapper;
        }
        return ELBus.then();
    }

    private static void groupPath(ELWrapper whenELWrapper, Graph graph, List<List<List<Node>>> groupPaths, boolean filterSpecialPath) throws LiteFlowELException {

    }

    //处理分段节点
    private static ELWrapper processSegments(ELWrapper eLWrapper, Graph graph, List<List<Node>> allPaths, boolean filterSpecialPath) throws LiteFlowELException {
        Set<Node> allNodeSet = allPaths.stream().flatMap(List::stream).collect(Collectors.toSet());
        List<Node> segmentationPoints = GraphUtil.findSegmentationPoints(allPaths, graph.getNodes());

        List<List<Node>> processSegments = GraphUtil.getProcessSegments(allPaths, segmentationPoints);
        List<List<Node>> segments = GraphUtil.mergeSingleElementSegments(processSegments);
        // 遍历多个分段
        for (List<Node> items : segments) {
            FlowInfo flowInfo = flow(eLWrapper, graph, items, false);
            log.debug("flow eLWrapper:{}", eLWrapper.toEL());
            if (flowInfo != null) {
                Node node = flowInfo.getNode();
                String type = node.getType();
                if ("then".equalsIgnoreCase(type)) {
                    break;
                } else if ("when".equalsIgnoreCase(type)) {
                    break;
                } else if ("router".equalsIgnoreCase(type)) {
                    return getBooleanELWrapper(graph, node);
                } else if (RuleEnums.NodeEnum.DEV_TRIGGER.getType().equalsIgnoreCase(type)) {
                    break;
                }
            }
        }

        // 是否存在 then when节点
        List<Node> thenNodeList = segmentationPoints.stream().filter(m -> "then".equalsIgnoreCase(m.getType())).collect(Collectors.toList());
        List<Node> whenNodeList = segmentationPoints.stream().filter(m -> "when".equalsIgnoreCase(m.getType())).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(thenNodeList)) {
            boolean isFirstNode = false;
            for (int i = 0; i < thenNodeList.size(); i++) {
                Node startNode = thenNodeList.get(i);
                List<Node> prev = graph.getPrev(startNode);
                if (allNodeSet.containsAll(prev)) {
                    isFirstNode = true;
                    break;
                }
                boolean flag = CollUtil.isEmpty(prev) || !allNodeSet.containsAll(prev);
                if (flag) {
                    isFirstNode = true;
                    break;
                }
            }

            for (int i = 0; i < thenNodeList.size(); i++) {
                Node startNode = thenNodeList.get(i);
                Integer endIndex = i + 1 >= thenNodeList.size() ? null : i + 1;
                Node endNode = endIndex != null ? thenNodeList.get(endIndex) : null;
                boolean isCatch = isCatchProcess(startNode);
                ThenELWrapper thenELWrapper = ELBus.then();
                if (eLWrapper instanceof ThenELWrapper && !isCatch && !isFirstNode) {
                    thenELWrapper = (ThenELWrapper) eLWrapper;
                }
                thenProcess(thenELWrapper, startNode);
                List<List<Node>> allPathList = null;
                if (endNode != null) {
                    allPathList = graph.findAllPaths(startNode, endNode, true, true);
                } else {
                    allPathList = graph.findAllPaths(startNode, true);
                    allPathList = allPathList.stream().map(m -> m.stream().filter(allNodeSet::contains).collect(Collectors.toList())).collect(Collectors.toList());
                }
                processSegments(thenELWrapper, graph, allPathList, true);

                if (eLWrapper instanceof ThenELWrapper && !isCatch && !isFirstNode) {
                    //BaseELBus.eLWrapperConvert(eLWrapper, thenELWrapper, null);
                } else {
                    CatchELWrapper catchELWrapper = catchProcess(thenELWrapper, startNode);
                    BaseELBus.eLWrapperConvert(eLWrapper, thenELWrapper, catchELWrapper);
                }
            }
        } else if (CollUtil.isNotEmpty(whenNodeList)) {
            for (int i = 0; i < whenNodeList.size(); i++) {
                Node startNode = whenNodeList.get(i);
                Integer endIndex = i + 1 >= whenNodeList.size() ? null : i + 1;
                Node endNode = endIndex != null ? whenNodeList.get(endIndex) : null;
                WhenELWrapper whenELWrapper = ELBus.when();
                whenProcess(whenELWrapper, startNode);
                List<List<Node>> allPathList = null;

                boolean flag = false;
                Node joinNode = graph.findJoinNode(startNode);
                if (joinNode != null && joinNode != endNode) {
                    endNode = joinNode;
                    flag = true;
                }

                if (endNode != null) {
                    allPathList = graph.findAllPaths(startNode, endNode, true, true);
                } else {
                    allPathList = graph.findAllPaths(startNode, true);
                }

                List<List<List<Node>>> groupPaths = GraphUtil.groupPathsByIntersection(allPathList);
                for (List<List<Node>> paths : groupPaths) {
                    if (GraphUtil.isSingleNode(paths)) {
                        BaseELBus.eLWrapperConvert(whenELWrapper, GraphUtil.getSingleNode(paths));
                    } else {
                        ThenELWrapper thenELWrapper = ELBus.then();
                        processSegments(thenELWrapper, graph, paths, true);
                        whenELWrapper.when(thenELWrapper);
                    }
                }

                CatchELWrapper catchELWrapper = catchProcess(whenELWrapper, startNode);
                BaseELBus.eLWrapperConvert(eLWrapper, whenELWrapper, catchELWrapper);

                if (flag) {
                    Node endNode1 = endIndex != null ? whenNodeList.get(endIndex) : null;
                    if (endNode1 != null) {
                        allPathList = graph.findAllPaths(joinNode, endNode1, false, true);
                    } else if (joinNode != null) {
                        allPathList = graph.findAllPaths(joinNode, false);
                        allPathList = allPathList.stream().map(m -> m.stream().filter(allNodeSet::contains).collect(Collectors.toList())).collect(Collectors.toList());
                    }
                    processSegments(eLWrapper, graph, allPathList, true);
                }
            }
        }
        return eLWrapper;
    }

    private static FlowInfo flow(ELWrapper eLWrapper, Graph graph, List<Node> nodes, boolean filterSpecialPath) throws LiteFlowELException {
        //过滤掉特殊路径的节点
        if (filterSpecialPath) {
            nodes = nodes.stream().filter(m -> !graph.specialNodes.contains(m)).collect(Collectors.toList());
        }

        List<List<Node>> allPathByNodes = GraphUtil.findAllPathByNodes(nodes, graph);
        if (allPathByNodes.size() == 1) {
            List<Node> path = allPathByNodes.get(0);
            for (Node node : path) {
                switch (node.getType()) {
                    case "switch_script":
                    case "switch":
                        BaseELBus.eLWrapperConvert(eLWrapper, getSwitchELWrapper(graph, node), null);
                        break;
                    case "if_script":
                    case "if":
                        BaseELBus.eLWrapperConvert(eLWrapper, getIfELWrapper(graph, node), null);
                        break;
                    case "devTrigger":
                    case "productTrigger":
                        BaseELBus.eLWrapperConvert(eLWrapper, getDevTriggerELWrapper(graph, node), null);
                        return FlowInfo.builder().node(node).isBreak(true).isContinue(false).build();
                    case "while_script":
                    case "while":
                        BaseELBus.eLWrapperConvert(eLWrapper, getWhileELWrapper(graph, node), null);
                        break;
                    case "for_script":
                    case "for":
                        BaseELBus.eLWrapperConvert(eLWrapper, getForELWrapper(graph, node), null);
                        break;
                    case "iterator_script":
                    case "iterator":
                        BaseELBus.eLWrapperConvert(eLWrapper, getIteratorELWrapper(graph, node), null);
                        break;
                    case "when":
                        return FlowInfo.builder().node(node).isBreak(true).isContinue(false).build();
                    case "then":
                        return FlowInfo.builder().node(node).isBreak(true).isContinue(false).build();
                    case "subflow":
                        break;
                    case "subvar":
                        break;
                    case "router":
                        return FlowInfo.builder().node(node).isBreak(true).isContinue(false).build();
                    case "chain":
                        BaseELBus.eLWrapperConvert(eLWrapper, getChainELWrapper(graph, node), null);
                        break;
                    default:
                        log.debug("node:{}", node.getType());
                        BaseELBus.eLWrapperConvert(eLWrapper, node);
                }
            }
        } else if (allPathByNodes.size() > 1) {
            // 判断多路径的其实node 是不是都为dev-trigger
            boolean isAllDevTrigger = true;
            for (List<Node> path : allPathByNodes) {
                if (!path.isEmpty()) {
                    Node node = path.get(0);
                    if (!RuleEnums.NodeEnum.DEV_TRIGGER.getType().equalsIgnoreCase(node.getType()) && !RuleEnums.NodeEnum.PRODUCT_TRIGGER.getType().equalsIgnoreCase(node.getType())) {
                        isAllDevTrigger = false;
                        break;
                    }
                }
            }
            if (isAllDevTrigger) {
                log.info("多条件判断路径:{}", eLWrapper);
                BaseELBus.eLWrapperConvert(eLWrapper, getDevTriggerELWrapper(graph, allPathByNodes.get(0).get(0)), null);
                return FlowInfo.builder().node(allPathByNodes.get(0).get(0)).isBreak(true).isContinue(false).build();
            } else {
                WhenELWrapper whenELWrapper = ELBus.when();
                List<List<List<Node>>> grouped = GraphUtil.groupPathsByIntersection(allPathByNodes);
                if (grouped.size() > 1) {
                    for (List<List<Node>> itemList : grouped) {
                        if (itemList.size() == 1) {
                            List<Node> nodeList = itemList.get(0);
                            if (nodeList.size() == 1) {
                                whenELWrapper.when(buildNode(graph, itemList.get(0).get(0)));
                            } else if (nodeList.size() > 1) {
                                List<Node> startSameNode = GraphUtil.startSameNode(itemList);
                                if (startSameNode.get(0).getType().equals("when")) {
                                    processSegments(whenELWrapper, graph, itemList, filterSpecialPath);
                                } else {
                                    ThenELWrapper thenELWrapper1 = ELBus.then();
                                    processSegments(thenELWrapper1, graph, itemList, filterSpecialPath);
                                    whenELWrapper.when(thenELWrapper1);
                                }
                            }
                        } else {
                            List<Node> startSameNode = GraphUtil.startSameNode(itemList);
                            if (!startSameNode.isEmpty()) {
                                if (startSameNode.get(0).getType().equals("when")) {
                                    processSegments(whenELWrapper, graph, itemList, filterSpecialPath);
                                } else {
                                    ThenELWrapper thenELWrapper1 = ELBus.then();
                                    //如果所有路径的第一个节点都相同
                                    processSegments(thenELWrapper1, graph, itemList, filterSpecialPath);
                                    whenELWrapper.when(thenELWrapper1);
                                }
                            } else {
                                ThenELWrapper thenELWrapper1 = ELBus.then();
                                flow(thenELWrapper1, graph, GraphUtil.excludeSameNode(itemList, new ArrayList<>()), filterSpecialPath);
                                whenELWrapper.when(thenELWrapper1);
                            }
                        }
                    }
                    BaseELBus.eLWrapperConvert(eLWrapper, whenELWrapper, null);
                } else {
                    List<Node> sameNodeList = GraphUtil.startSameNode(grouped.get(0));//存在相同节点
                    if (CollUtil.isNotEmpty(sameNodeList)) {
                        processSegments(eLWrapper, graph, grouped.get(0), filterSpecialPath);
                    } else {
                        for (List<Node> itemList : allPathByNodes) {
                            if (itemList.size() == 1) {
                                whenELWrapper.when(buildNode(graph, itemList.get(0)));
                            } else {
                                ThenELWrapper thenELWrapper1 = ELBus.then();
                                flow(thenELWrapper1, graph, itemList, filterSpecialPath);
                                whenELWrapper.when(thenELWrapper1);
                            }
                        }
                        BaseELBus.eLWrapperConvert(eLWrapper, whenELWrapper, null);
                    }
                }
            }
        }
        return null;
    }

    public static ELWrapper getBooleanELWrapper(Graph graph, Node booleanNode) throws LiteFlowELException {
        List<List<Node>> allPaths = graph.findAllPaths(booleanNode, true);
        List<Node> segmentationPoints = GraphUtil.findSegmentationPoints(allPaths, graph.getNodes());
        List<List<Node>> processSegments = GraphUtil.getProcessSegments(allPaths, segmentationPoints);
        List<List<Node>> segments = GraphUtil.mergeSingleElementSegments(processSegments);
        if (!segments.isEmpty() && (segments.get(0).size() == 1 || "not".equalsIgnoreCase(segments.get(0).get(0).getType()))) {
            return buildBooleanELWrapper(graph, segments.get(0).get(0));
        } else {
            throw new LiteFlowELException("只能存在一条boolean路径！");
        }
    }

    public static ELWrapper getBooleanELWrapper(Graph graph, List<Node> booleanNodeList) throws LiteFlowELException {
        List<List<Node>> allPaths = GraphUtil.findAllPathByNodes(booleanNodeList, graph);
        List<Node> segmentationPoints = GraphUtil.findSegmentationPoints(allPaths, graph.getNodes());
        List<List<Node>> processSegments = GraphUtil.getProcessSegments(allPaths, segmentationPoints);
        List<List<Node>> segments = GraphUtil.mergeSingleElementSegments(processSegments);
        if (!segments.isEmpty() && (segments.get(0).size() == 1 || "not".equalsIgnoreCase(segments.get(0).get(0).getType()))) {
            return buildBooleanELWrapper(graph, segments.get(0).get(0));
        } else {
            throw new LiteFlowELException("只能存在一条boolean路径！");
        }
    }

    public static ELWrapper buildBooleanELWrapper(Graph graph, Node node) throws LiteFlowELException {
        switch (node.getType()) {
            case "and":
                AndELWrapper andELWrapper = ELBusAnd.node();
                List<Node> andList = graph.getNeighbors(node);
                for (Node n : andList) {
                    andELWrapper.and(buildBooleanELWrapper(graph, n));
                }
                return andELWrapper;
            case "or":
                OrELWrapper orELWrapper = ELBus.or();
                List<Node> orList = graph.getNeighbors(node);
                for (Node n : orList) {
                    orELWrapper.or(buildBooleanELWrapper(graph, n));
                }
                return orELWrapper;
            case "not":
                List<Node> notList = graph.getNeighbors(node);
                ELWrapper elWrapper = buildBooleanELWrapper(graph, notList.get(0));
                return ELBusNot.not(elWrapper);
            case "boolean":
            case "condition":
            case "boolean_script":
                return ELBusNode.node(node);
        }
        throw new LiteFlowELException("boolean路径仅支持【与、或、非、布尔组件】！");
    }

    public static ELWrapper getWhileELWrapper(Graph graph, Node node) throws LiteFlowELException {
        List<Node> doNodeList = graph.getSpecialPathNodes(node, RuleEnums.PATH_ENUM.do_path);
        List<Node> breakNodeList = graph.getSpecialPathNodes(node, RuleEnums.PATH_ENUM.break_path);
        List<Node> booleanNodeList = graph.getSpecialPathNodes(node, RuleEnums.PATH_ENUM.boolean_path);
        LoopELWrapper elWrapper = null;
        if (CollUtil.isNotEmpty(booleanNodeList)) {
            ELWrapper booleanElWrapper = getBooleanELWrapper(graph, booleanNodeList);
            elWrapper = ELBusWhile.wrapper(node, booleanElWrapper);
        } else {
            throw new LiteFlowELException("未设置boolean路径！");
        }
        if (CollUtil.isNotEmpty(doNodeList)) {
            if (doNodeList.size() == 1) {
                if (elWrapper != null) {
                    elWrapper.doOpt(ELBusNode.node(doNodeList.get(0)));
                }
            } else {
                ThenELWrapper doELWrapper = ELBus.then();
                processSegments(doELWrapper, graph, GraphUtil.findAllPathByNodes(doNodeList, graph), false);
                if (elWrapper != null) {
                    elWrapper.doOpt(doELWrapper);
                }
            }

        } else {
            throw new LiteFlowELException("未设置do路径！");
        }
        if (CollUtil.isNotEmpty(breakNodeList)) {
            Node breakNode = breakNodeList.get(0);
            if (NodeTypeEnum.BOOLEAN.getCode().equals(breakNode.getType())) {
                if (elWrapper != null) {
                    elWrapper.breakOpt(ELBusNode.node(breakNode));
                }
            } else {
                throw new LiteFlowELException("break路径仅支持【布尔组件】！");
            }
        }
        return elWrapper;
    }

    public static ELWrapper getDevTriggerELWrapper(Graph graph, Node node) throws LiteFlowELException {
        List<Node> conditionList = graph.getNodes(RuleEnums.NodeEnum.CONDITION.getType());
        List<Node> trueNodeList = graph.getSpecialPathNodes(node, RuleEnums.PATH_ENUM.true_path);
        List<Node> falseNodeList = graph.getSpecialPathNodes(node, RuleEnums.PATH_ENUM.false_path);
        IfELWrapper elWrapper = null;
        ELWrapper booleanElWrapper = null;
        if (CollUtil.isNotEmpty(conditionList)) {
            booleanElWrapper = getConditionELWrapper(graph, conditionList);
            // 单条件处理
            if (conditionList.size() == 1) {
                List<Line> toLineList = graph.getLines(conditionList.get(0), RuleEnums.PATH_ENUM.to_path);
                if (CollUtil.isNotEmpty(toLineList) && CollUtil.isNotEmpty(trueNodeList)) {
                    throw new LiteFlowELException("to路径和true路径不能同时存在！");
                }
                if (CollUtil.isNotEmpty(toLineList) && CollUtil.isNotEmpty(falseNodeList)) {
                    throw new LiteFlowELException("to路径和false路径不能同时存在！");
                }

                if (CollUtil.isNotEmpty(toLineList)) {
                    SwitchELWrapper selWrapper = ELBusSwitch.node(conditionList.get(0));
                    int index = 0;
                    for (Line line : toLineList) {
                        Node startNode = graph.getNode(line.getTo());
                        if (!graph.hasNextNode(startNode)) {
                            if (StrUtil.isNotBlank(line.getData().getId()) || StrUtil.isNotBlank(line.getData().getTag())) {
                                ThenELWrapper thenELWrapper = ELBusThen.then(line);
                                thenELWrapper.then(ELBusNode.node(startNode));
                                selWrapper.to(thenELWrapper);
                            } else {
                                selWrapper.to(ELBusNode.node(startNode));
                            }
                        } else {
                            if (NodeTypeEnum.FOR.getCode().equals(startNode.getType())) {
                                ELWrapper wrapper = getForELWrapper(graph, startNode);
                                ELBusBuilder.setId(wrapper, line);
                                ELBusBuilder.setTag(wrapper, line);
                                selWrapper.to(wrapper);
                            } else {
                                Set<Node> allNodeSet = graph.findAllPaths(startNode).stream().flatMap(List::stream).collect(Collectors.toSet());
                                List<Node> allNodeList = new ArrayList<>(allNodeSet);
                                Graph graph1 = new Graph(allNodeList, graph.getLines(allNodeList));
                                ELWrapper wrapper = graph1.toELWrapper();
                                ELBusBuilder.setId(wrapper, line);
                                index++;
                                // 默认用条件下标来选择路径
                                ELBusBuilder.setTag(wrapper, line, index + "");
                                selWrapper.to(wrapper);
                            }
                        }
                    }
                    //获取 default节点集合
                    List<Node> defaultNodeList = graph.getSpecialPathNodes(node, RuleEnums.PATH_ENUM.default_path);
                    if (CollUtil.isNotEmpty(defaultNodeList)) {
                        if (defaultNodeList.size() == 1) {
                            selWrapper.defaultOpt(ELBusNode.node(defaultNodeList.get(0)));
                        } else {
                            ThenELWrapper thenELWrapper = ELBus.then();
                            processSegments(thenELWrapper, graph, GraphUtil.findAllPathByNodes(defaultNodeList, graph), false);
                            selWrapper.defaultOpt(thenELWrapper);
                        }
                    }
                    return selWrapper;
                } else {
                    if (CollUtil.isNotEmpty(trueNodeList) && CollUtil.isNotEmpty(falseNodeList)) {
                        ELWrapper trueELWrapper = null;
                        trueNodeList = trueNodeList.stream().filter(m -> !"condition".equalsIgnoreCase(m.getType())).collect(Collectors.toList());
                        if (trueNodeList.size() == 1) {
                            trueELWrapper = ELBusNode.node(trueNodeList.get(0));
                        } else {
                            trueELWrapper = ELBus.then();
                            processSegments(trueELWrapper, graph, GraphUtil.findAllPathByNodes(trueNodeList, graph), false);
                        }
                        log.debug("trueELWrapper:{}", trueELWrapper.toEL());
                        ELWrapper falseELWrapper = null;
                        falseNodeList = falseNodeList.stream().filter(m -> !"condition".equalsIgnoreCase(m.getType())).collect(Collectors.toList());
                        if (falseNodeList.size() == 1) {
                            falseELWrapper = ELBusNode.node(falseNodeList.get(0));
                        } else {
                            falseELWrapper = ELBus.then();
                            processSegments(falseELWrapper, graph, GraphUtil.findAllPathByNodes(falseNodeList, graph), false);
                        }
                        log.debug("falseELWrapper:{}", falseELWrapper.toEL());
                        IfELWrapper ifELWrapper = ELBusIf.node(node, booleanElWrapper, trueELWrapper, falseELWrapper);
                        log.debug("ifELWrapper:{}", ifELWrapper.toEL());
                        return ifELWrapper;
                    } else if (CollUtil.isNotEmpty(trueNodeList) && CollUtil.isEmpty(falseNodeList)) {
                        ELWrapper trueELWrapper = null;
                        trueNodeList = trueNodeList.stream().filter(m -> !"condition".equalsIgnoreCase(m.getType())).collect(Collectors.toList());
                        if (trueNodeList.size() == 1) {
                            trueELWrapper = ELBusNode.node(trueNodeList.get(0));
                        } else {
                            trueELWrapper = ELBus.then();
                            processSegments(trueELWrapper, graph, GraphUtil.findAllPathByNodes(trueNodeList, graph), false);
                        }
                        IfELWrapper ifELWrapper = ELBusIf.node(node, booleanElWrapper, trueELWrapper, null);
                        return ifELWrapper;
                    } else if (CollUtil.isEmpty(trueNodeList)) {
                        throw new LiteFlowELException("未设置true路径！");
                    } else if (CollUtil.isEmpty(falseNodeList)) {
                        throw new LiteFlowELException("未设置false路径！");
                    }
                    return elWrapper;
                }
            } else {
                for (Node c : conditionList) {
                    List<Node> neighborsList = graph.getNeighbors(c);
                    if (neighborsList.size() > 1) {
                        throw new LiteFlowELException("暂时不支持多条件多执行路径");
                    }
                }
                if (CollUtil.isNotEmpty(trueNodeList)) {
                    ELWrapper trueELWrapper = null;
                    trueNodeList = trueNodeList.stream().filter(m -> !"condition".equalsIgnoreCase(m.getType())).collect(Collectors.toList());
                    if (trueNodeList.size() == 1) {
                        trueELWrapper = ELBusNode.node(trueNodeList.get(0));
                    } else {
                        trueELWrapper = ELBus.then();
                        processSegments(trueELWrapper, graph, GraphUtil.findAllPathByNodes(trueNodeList, graph), false);
                    }
                    IfELWrapper ifELWrapper = ELBusIf.node(node, booleanElWrapper, trueELWrapper, null);
                    return ifELWrapper;
                } else if (CollUtil.isEmpty(trueNodeList)) {
                    throw new LiteFlowELException("未设置true路径！");
                }

            }
        } else {
            throw new LiteFlowELException("未设置conditon组件！");
        }
        return elWrapper;
    }

    public static ELWrapper getConditionELWrapper(Graph graph, List<Node> conditionList) throws LiteFlowELException {
        // 单条件
        if (conditionList.size() == 1) {
            return ELBusNode.node(conditionList.get(0));
            // 多条件
        } else {
            for (Node node : conditionList) {
                List<Node> tlist = graph.getNeighbors(node);
                for (Node tnode : tlist) {
                    LinkedTreeMap map = (LinkedTreeMap) tnode.getData();
                    int cond = ((Number) map.get("cond")).intValue();
                    if (cond == 2) {
                        // 所有条件符合
                        List<Node> clist = graph.getConditionMap().get(tnode).getCondition();
                        AndELWrapper andELWrapper = ELBusAnd.node();
                        for (Node cnode : clist) {
                            andELWrapper.and(buildBooleanELWrapper(graph, cnode));
                        }
                        return andELWrapper;
                    } else {
                        // 任意条件符合
                        List<Node> clist = graph.getConditionMap().get(tnode).getCondition();
                        OrELWrapper orELWrapper = ELBus.or();
                        for (Node cnode : clist) {
                            orELWrapper.or(buildBooleanELWrapper(graph, cnode));
                        }
                        return orELWrapper;
                    }
                }
            }
        }
        return null;
    }

    public static ELWrapper getIfELWrapper(Graph graph, Node node) throws LiteFlowELException {
        List<Node> trueNodeList = graph.getSpecialPathNodes(node, RuleEnums.PATH_ENUM.true_path);
        List<Node> falseNodeList = graph.getSpecialPathNodes(node, RuleEnums.PATH_ENUM.false_path);
        List<Node> booleanNodeList = graph.getSpecialPathNodes(node, RuleEnums.PATH_ENUM.boolean_path);
        IfELWrapper elWrapper = null;
        ELWrapper booleanElWrapper = null;
        if (CollUtil.isNotEmpty(booleanNodeList)) {
            booleanElWrapper = getBooleanELWrapper(graph, booleanNodeList);
        } else if (StrUtil.isBlank(node.getNodeData().getNodeDataBase().getClazz())) {
            throw new LiteFlowELException("未设置boolean路径或组件类！");
        }
        if (CollUtil.isNotEmpty(trueNodeList) && CollUtil.isNotEmpty(falseNodeList)) {
            ELWrapper trueELWrapper = null;
            if (trueNodeList.size() == 1) {
                trueELWrapper = ELBusNode.node(trueNodeList.get(0));
            } else {
                trueELWrapper = ELBus.then();
                processSegments(trueELWrapper, graph, GraphUtil.findAllPathByNodes(trueNodeList, graph), false);
            }
            ELWrapper falseELWrapper = null;
            if (falseNodeList.size() == 1) {
                falseELWrapper = ELBusNode.node(falseNodeList.get(0));
            } else {
                falseELWrapper = ELBus.then();
                processSegments(falseELWrapper, graph, GraphUtil.findAllPathByNodes(falseNodeList, graph), false);
            }
            IfELWrapper ifELWrapper = ELBusIf.node(node, booleanElWrapper, trueELWrapper, falseELWrapper);

            return ifELWrapper;
        } else if (CollUtil.isNotEmpty(trueNodeList) && CollUtil.isEmpty(falseNodeList)) {
            ELWrapper trueELWrapper = null;
            if (trueNodeList.size() == 1) {
                trueELWrapper = ELBusNode.node(trueNodeList.get(0));
            } else {
                trueELWrapper = ELBus.then();
                processSegments(trueELWrapper, graph, GraphUtil.findAllPathByNodes(trueNodeList, graph), false);
            }
            IfELWrapper ifELWrapper = ELBusIf.node(node, booleanElWrapper, trueELWrapper, null);
            return ifELWrapper;
        } else if (CollUtil.isEmpty(trueNodeList)) {
            throw new LiteFlowELException("未设置true路径！");
        } else if (CollUtil.isEmpty(falseNodeList)) {
            throw new LiteFlowELException("未设置false路径！");
        }
        return elWrapper;
    }

    public static ELWrapper getChainELWrapper(Graph graph, Node node) throws LiteFlowELException {
        return ELBusWrapper.chain(node);
    }

    public static ELWrapper getScriptELWrapper(Graph graph, Node node) throws LiteFlowELException {
        return ELBusWrapper.chain(node);
    }

    public static ELWrapper getIteratorELWrapper(Graph graph, Node node) throws LiteFlowELException {
        LoopELWrapper elWrapper = ELBusIterator.node(node);
        List<Node> doNodeList = graph.getSpecialPathNodes(node, RuleEnums.PATH_ENUM.do_path);
        List<Node> breakNodeList = graph.getSpecialPathNodes(node, RuleEnums.PATH_ENUM.break_path);
        if (CollUtil.isNotEmpty(doNodeList)) {
            if (doNodeList.size() == 1) {
                elWrapper.doOpt(ELBusNode.node(doNodeList.get(0)));
            } else {
                ThenELWrapper doELWrapper = ELBus.then();
                processSegments(doELWrapper, graph, GraphUtil.findAllPathByNodes(doNodeList, graph), false);
                elWrapper.doOpt(doELWrapper);
            }
        } else {
            throw new LiteFlowELException("未设置do路径！");
        }
        if (CollUtil.isNotEmpty(breakNodeList)) {
            Node breakNode = breakNodeList.get(0);
            String type = breakNode.getNodeData().getType();
            if (NodeTypeEnum.BOOLEAN.getCode().equals(type) || NodeTypeEnum.BOOLEAN_SCRIPT.getCode().equals(type)) {
                elWrapper.breakOpt(ELBusNode.node(breakNode));
            } else {
                throw new LiteFlowELException("break路径仅支持【布尔组件】！");
            }
        }
        return elWrapper;
    }

    public static ELWrapper getForELWrapper(Graph graph, Node node) throws LiteFlowELException {
        LoopELWrapper elWrapper = ELBusFor.node(node);
        List<Node> doNodeList = graph.getSpecialPathNodes(node, RuleEnums.PATH_ENUM.do_path);
        List<Node> breakNodeList = graph.getSpecialPathNodes(node, RuleEnums.PATH_ENUM.break_path);
        if (CollUtil.isNotEmpty(doNodeList)) {
            if (doNodeList.size() == 1) {
                elWrapper.doOpt(ELBusNode.node(doNodeList.get(0)));
            } else {
                ThenELWrapper doELWrapper = ELBus.then();
                processSegments(doELWrapper, graph, GraphUtil.findAllPathByNodes(doNodeList, graph), false);
                elWrapper.doOpt(doELWrapper);
            }
        } else {
            throw new LiteFlowELException("未设置do路径！");
        }
        if (CollUtil.isNotEmpty(breakNodeList)) {
            Node breakNode = breakNodeList.get(0);
            String type = breakNode.getNodeData().getType();
            if (NodeTypeEnum.BOOLEAN.getCode().equals(type) || NodeTypeEnum.BOOLEAN_SCRIPT.getCode().equals(type)) {
                elWrapper.breakOpt(ELBusNode.node(breakNode));
            } else {
                throw new LiteFlowELException("break路径仅支持【布尔组件】！");
            }
        }
        return elWrapper;
    }

    public static ELWrapper getSwitchELWrapper(Graph graph, Node node) throws LiteFlowELException {
        SwitchELWrapper elWrapper = ELBusSwitch.node(node);
        List<Line> toLineList = graph.getLines(node, RuleEnums.PATH_ENUM.to_path);
        for (Line line : toLineList) {
            Node startNode = graph.getNode(line.getTo());
            if (!graph.hasNextNode(startNode)) {
                if (StrUtil.isNotBlank(line.getData().getId()) || StrUtil.isNotBlank(line.getData().getTag())) {
                    ThenELWrapper thenELWrapper = ELBusThen.then(line);
                    thenELWrapper.then(ELBusNode.node(startNode));
                    elWrapper.to(thenELWrapper);
                } else {
                    elWrapper.to(ELBusNode.node(startNode));
                }
            } else {
                if (NodeTypeEnum.FOR.getCode().equals(startNode.getType())) {
                    ELWrapper wrapper = getForELWrapper(graph, startNode);
                    ELBusBuilder.setId(wrapper, line);
                    ELBusBuilder.setTag(wrapper, line);
                    elWrapper.to(wrapper);
                } else {
                    Set<Node> allNodeSet = graph.findAllPaths(startNode).stream().flatMap(List::stream).collect(Collectors.toSet());
                    List<Node> allNodeList = new ArrayList<>(allNodeSet);
                    Graph graph1 = new Graph(allNodeList, graph.getLines(allNodeList));
                    ELWrapper wrapper = graph1.toELWrapper();
                    ELBusBuilder.setId(wrapper, line);
                    ELBusBuilder.setTag(wrapper, line);
                    elWrapper.to(wrapper);
                }
            }
        }

        //获取 default节点集合
        List<Node> defaultNodeList = graph.getSpecialPathNodes(node, RuleEnums.PATH_ENUM.default_path);
        if (CollUtil.isNotEmpty(defaultNodeList)) {
            if (defaultNodeList.size() == 1) {
                elWrapper.defaultOpt(ELBusNode.node(defaultNodeList.get(0)));
            } else {
                ThenELWrapper thenELWrapper = ELBus.then();
                processSegments(thenELWrapper, graph, GraphUtil.findAllPathByNodes(defaultNodeList, graph), false);
                elWrapper.defaultOpt(thenELWrapper);
            }
        }
        return elWrapper;
    }

    public static boolean isCatchProcess(Node node) {
        if (node != null) {
            if ("then".equalsIgnoreCase(node.getType())) {
                NodeDataThen nodeDataThen = node.getNodeData().getNodeDataThen();
                if (nodeDataThen.getIsCatch() != null && nodeDataThen.getIsCatch()) {
                    return true;
                }
            } else if ("when".equalsIgnoreCase(node.getType())) {
                NodeDataWhen nodeDataWhen = node.getNodeData().getNodeDataWhen();
                if (nodeDataWhen.getIsCatch() != null && nodeDataWhen.getIsCatch()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static CatchELWrapper catchProcess(ELWrapper elWrapper, Node node) {
        CatchELWrapper catchELWrapper = null;
        if (node != null) {
            if (elWrapper instanceof ThenELWrapper && "then".equalsIgnoreCase(node.getType())) {
                NodeDataThen nodeDataThen = node.getNodeData().getNodeDataThen();
                if (nodeDataThen.getIsCatch() != null && nodeDataThen.getIsCatch()) {
                    catchELWrapper = ELBusCatch.catchException(elWrapper);
                    if (StrUtil.isNotBlank(nodeDataThen.getCatchDo())) {
                        catchELWrapper.doOpt(nodeDataThen.getCatchDo());
                    }
                }
            } else if (elWrapper instanceof WhenELWrapper && "when".equalsIgnoreCase(node.getType())) {
                NodeDataWhen nodeDataWhen = node.getNodeData().getNodeDataWhen();
                if (nodeDataWhen.getIsCatch() != null && nodeDataWhen.getIsCatch()) {
                    catchELWrapper = ELBusCatch.catchException(elWrapper);
                    if (StrUtil.isNotBlank(nodeDataWhen.getCatchDo())) {
                        catchELWrapper.doOpt(nodeDataWhen.getCatchDo());
                    }
                }
            }
        }
        return catchELWrapper;
    }

    public static void thenProcess(ThenELWrapper thenELWrapper, Node thenNode) {
        if (thenNode != null && "then".equalsIgnoreCase(thenNode.getType())) {
            NodeDataThen nodeData = thenNode.getNodeData().getNodeDataThen();
            if (StrUtil.isNotBlank(nodeData.getId())) {
                thenELWrapper.id(nodeData.getId());
            }
            if (StrUtil.isNotBlank(nodeData.getTag())) {
                thenELWrapper.tag(nodeData.getTag());
            }
        }
    }

    public static void whenProcess(WhenELWrapper whenELWrapper, Node whenNode) {
        if (whenNode != null && "when".equalsIgnoreCase(whenNode.getType())) {
            NodeDataWhen nodeDataWhen = whenNode.getNodeData().getNodeDataWhen();
            if (StrUtil.isNotBlank(nodeDataWhen.getId())) {
                whenELWrapper.id(nodeDataWhen.getId());
            }
            if (StrUtil.isNotBlank(nodeDataWhen.getTag())) {
                whenELWrapper.tag(nodeDataWhen.getTag());
            }

            if (nodeDataWhen.getIgnoreError() != null && nodeDataWhen.getIgnoreError()) {
                whenELWrapper.ignoreError(nodeDataWhen.getIgnoreError());
            }
            String ignoreType = nodeDataWhen.getIgnoreType();
            if (StrUtil.isNotBlank(ignoreType)) {
                switch (ignoreType) {
                    case "any":
                        whenELWrapper.any(true);
                        break;
                    case "must":
                        if (StrUtil.isNotBlank(nodeDataWhen.getMustId())) {
                            whenELWrapper.must(nodeDataWhen.getMustId().split(","));
                        }
                        break;
                }
            }
            if (StrUtil.isNotBlank(nodeDataWhen.getCustomThreadExecutor())) {
                whenELWrapper.customThreadExecutor(nodeDataWhen.getCustomThreadExecutor());
            }
        }
    }

    public static ELWrapper buildNode(Graph graph, Node node) throws LiteFlowELException {
        String type = node.getType();
        switch (type) {
            case "common":
                return ELBusNode.node(node);
            case "switch":
                return getSwitchELWrapper(graph, node);
            case "if":
                return getIfELWrapper(graph, node);
            case "while":
                return getWhileELWrapper(graph, node);
            case "for":
                return getForELWrapper(graph, node);
            case "iterator":
                return getIteratorELWrapper(graph, node);
            case "chain":
                return getChainELWrapper(graph, node);
            default:
                return ELBusNode.node(node);
        }
    }
}
