package com.fastbee.rule.parser2.util;

import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollUtil;
import com.yomahub.liteflow.builder.el.*;

import com.fastbee.rule.parser.bus.*;
import com.fastbee.rule.parser.entity.node.Node;
import com.fastbee.rule.parser.enums.RuleEnums;
import com.fastbee.rule.parser.execption.LiteFlowELException;
import com.fastbee.rule.parser.graph.GraphUtil;
import com.fastbee.rule.parser.wrapper.ELBusWrapper;
import com.fastbee.rule.parser2.GraphEL;

public class RuleGraphUtil {

    // 流程转EL表达式
    public static ELWrapper toEL(GraphEL graphEL) throws LiteFlowELException {
        if(RuleNodeUtil.isRouterNode(graphEL.getStartNodes().get(0))){
            Node node = graphEL.getStartNodes().get(0);
            List<Node> nodes = graphEL.path2List(graphEL.findAllPaths(node));
            nodes.remove(node);
            return ELBusBoolean.getBooleanELWrapper(graphEL, nodes);
        }
        if(isSingleNode(graphEL)){
            return buildSingleNode(graphEL);
        }
        return buildMultipleNode(graphEL);
    }

    // 多起点流程构建
    public static WhenELWrapper buildMultiplePath(GraphEL graphEL, List<Node> startNodes) throws LiteFlowELException {
        WhenELWrapper whenELWrapper = ELBus.when();
        Node joinNode = graphEL.findJoinNode(startNodes);
        if(joinNode == null){
            for (Node startNode : startNodes){
                if(graphEL.hasNextNode(startNode)){
                    whenELWrapper.when(buildSinglePath(graphEL, ELBus.then(), startNode));
                }else{
                    //whenELWrapper.when(ELBusNode.node(startNode));
                    //whenELWrapper.when(buildSinglePath(graphEL, ELBus.then(), startNode));
                    whenELWrapper.when(buildSingleNode(graphEL, startNode));
                }
            }
        }else{
            for (Node startNode : startNodes){
                List<List<Node>> allPaths = graphEL.findAllPaths(startNode, joinNode, false, true);
                List<Node> nodeList = graphEL.path2List(allPaths);
                if(nodeList.size() == 1){
                    whenELWrapper.when(ELBusNode.node(nodeList.get(0)));
                }else{
                    whenELWrapper.when(new GraphEL(nodeList, graphEL.getLines(nodeList), null).toELWrapper());
                }
            }
        }
        return whenELWrapper;
    }

    // 单起点流程构建
    public static ELWrapper buildSinglePath(GraphEL graphEL, ELWrapper elWrapper, Node startNode) throws LiteFlowELException {
        List<Node> neighbors = graphEL.getNeighbors(startNode);
        if(RuleNodeUtil.isCommonNode(startNode)){
            BaseELBus.eLWrapperConvert(elWrapper, startNode);
            if(neighbors.size() == 1){
                buildSinglePath(graphEL, elWrapper, neighbors.get(0));
            }else if(neighbors.size() > 1){
                WhenELWrapper whenELWrapper = buildMultiplePath(graphEL, neighbors);
                BaseELBus.eLWrapperConvert(elWrapper, whenELWrapper, null);

                Node joinNode = graphEL.findJoinNode(neighbors);
                if(joinNode != null){
                    buildSinglePath(graphEL, elWrapper, joinNode);
                }
            }
        }else if(RuleNodeUtil.isThenNode(startNode)){
            List<List<Node>> paths = graphEL.findAllPaths(startNode, true);
            List<Node> nodeList = graphEL.path2List(paths);
            ELWrapper wrapper = new GraphEL(nodeList, graphEL.getLines(nodeList), null).toELWrapper();
            if(wrapper instanceof ThenELWrapper){
                ThenELWrapper thenELWrapper = (ThenELWrapper) wrapper;
                ELWrapper wrapper1 = ELBusThen.then(thenELWrapper, startNode);
                if(graphEL.getStartNodes().contains(startNode)){
                    return wrapper1;
                }
                BaseELBus.eLWrapperConvert(elWrapper, wrapper1, null);
            }
        }else if(RuleNodeUtil.isWhenNode(startNode)){
            List<List<Node>> paths = graphEL.findAllPaths(startNode, true);
            Set<Node> startNodeSet = paths.stream().map(m -> m.get(0)).collect(Collectors.toCollection(LinkedHashSet::new));
            WhenELWrapper whenELWrapper = buildMultiplePath(graphEL, new ArrayList<>(startNodeSet));

            ELWrapper wrapper1 = ELBusWhen.when(whenELWrapper, startNode);
            if(graphEL.getStartNodes().contains(startNode)){
                return wrapper1;
            }
            BaseELBus.eLWrapperConvert(elWrapper, wrapper1, null);

            Node joinNode = graphEL.findJoinNode(startNode);
            if(joinNode != null){
                buildSinglePath(graphEL, elWrapper, joinNode);
            }
        }else if(RuleNodeUtil.isSwitchNode(startNode)){
            //获取全路径聚合点
            Node joinNode = graphEL.findJoinNode(startNode);
            //获取特殊路径聚合点
            Node specialJoinNode = graphEL.findSpecialJoinNode(startNode);

            SwitchELWrapper switchELWrapper = ELBusSwitch.node(startNode, specialJoinNode, graphEL);
            BaseELBus.eLWrapperConvert(elWrapper, switchELWrapper, null);

            //普通路径节点
            List<Node> commonNodeList = graphEL.getSpecialPathNodes(startNode, RuleEnums.PATH_ENUM.common_path);
            if(specialJoinNode != null){
                commonNodeList.add(specialJoinNode);
            }
            if(CollUtil.isNotEmpty(commonNodeList)){
                if(commonNodeList.size() == 1){
                    Node node = commonNodeList.get(0);
                    if(graphEL.hasNextNode(node)){
                        List<Node> nodes = graphEL.path2List(graphEL.findAllPaths(node));
                        GraphEL graphEL1 = new GraphEL(nodes, graphEL.getLines(nodes));
                        BaseELBus.eLWrapperConvert(elWrapper, graphEL1.toELWrapper(), null);
                    }else{
                        BaseELBus.eLWrapperConvert(elWrapper, ELBusNode.node(node), null);
                    }
                }else{
                    GraphEL graphEL1 = new GraphEL(commonNodeList, graphEL.getLines(commonNodeList));
                    List<Node> startNodes = graphEL1.getStartNodes();
                    if(startNodes.size() == 1){
                        buildSinglePath(graphEL, elWrapper, startNodes.get(0));
                    }else{
                        ELWrapper wrapper = buildSinglePath(graphEL, graphEL1.getAllPaths());
                        BaseELBus.eLWrapperConvert(elWrapper, wrapper, null);
                    }
                }
            }
        }else if(RuleNodeUtil.isIfNode(startNode)){
            //获取特殊路径聚合点
            Node specialJoinNode = graphEL.findSpecialJoinNode(startNode);

            IfELWrapper ifELWrapper = ELBusIf.node(startNode, specialJoinNode, graphEL);
            BaseELBus.eLWrapperConvert(elWrapper, ifELWrapper, null);

            //普通路径节点
            List<Node> commonNodeList = graphEL.getSpecialPathNodes(startNode, RuleEnums.PATH_ENUM.common_path);
            if(specialJoinNode != null){
                commonNodeList.add(specialJoinNode);
            }

            if(CollUtil.isNotEmpty(commonNodeList)){
                if(commonNodeList.size() == 1){
                    Node node = commonNodeList.get(0);
                    if(graphEL.hasNextNode(node)){
                        List<Node> nodes = graphEL.path2List(graphEL.findAllPaths(node));
                        GraphEL graphEL1 = new GraphEL(nodes, graphEL.getLines(nodes));
                        BaseELBus.eLWrapperConvert(elWrapper, graphEL1.toELWrapper(), null);
                    }else{
                        BaseELBus.eLWrapperConvert(elWrapper, ELBusNode.node(node), null);
                    }
                }else{
                    GraphEL graphEL1 = new GraphEL(commonNodeList, graphEL.getLines(commonNodeList));
                    List<Node> startNodes = graphEL1.getStartNodes();
                    if(startNodes.size() == 1){
                        buildSinglePath(graphEL, elWrapper, startNodes.get(0));
                    }else{
                        ELWrapper wrapper = buildSinglePath(graphEL, graphEL1.getAllPaths());
                        BaseELBus.eLWrapperConvert(elWrapper, wrapper, null);
                    }
                }
            }
        }else if(RuleNodeUtil.isWhileNode(startNode)){
            //获取特殊路径聚合点
            Node specialJoinNode = graphEL.findSpecialJoinNode(startNode);
            ELWrapper whileELWrapper = ELBusWhile.buildWhileELWrapper(graphEL, startNode, specialJoinNode);
            BaseELBus.eLWrapperConvert(elWrapper, whileELWrapper, null);
            //特殊路径聚合点处理
            specialJoinNodeHandler(graphEL, startNode, specialJoinNode, elWrapper);
        }else if(RuleNodeUtil.isForNode(startNode)){
            //获取特殊路径聚合点
            Node specialJoinNode = graphEL.findSpecialJoinNode(startNode);
            ELWrapper forELWrapper = ELBusFor.buildForELWrapper(graphEL, startNode, specialJoinNode);
            BaseELBus.eLWrapperConvert(elWrapper, forELWrapper, null);
            //特殊路径聚合点处理
            specialJoinNodeHandler(graphEL, startNode, specialJoinNode, elWrapper);
        }else if(RuleNodeUtil.isIteratorNode(startNode)){
            //获取特殊路径聚合点
            Node specialJoinNode = graphEL.findSpecialJoinNode(startNode);
            ELWrapper iteratorELWrapper = ELBusIterator.buildIteratorELWrapper(graphEL, startNode, specialJoinNode);
            BaseELBus.eLWrapperConvert(elWrapper, iteratorELWrapper, null);
            //特殊路径聚合点处理
            specialJoinNodeHandler(graphEL, startNode, specialJoinNode, elWrapper);
        }else if(RuleNodeUtil.isSubflowNode(startNode) || RuleNodeUtil.isSubvarNode(startNode) || RuleNodeUtil.isRouterNode(startNode)){
            List<Node> nodes = graphEL.path2List(graphEL.findAllPaths(startNode));
            nodes.remove(startNode);
            GraphEL graph = new GraphEL(nodes, graphEL.getLines(nodes));
            return graph.toELWrapper();
        }else if(RuleNodeUtil.isChainNode(startNode)){
            BaseELBus.eLWrapperConvert(elWrapper, ELBusWrapper.chain(startNode), null);

            List<Node> nodes = graphEL.path2List(graphEL.findAllPaths(startNode));
            nodes.remove(startNode);
            GraphEL graph = new GraphEL(nodes, graphEL.getLines(nodes));
            BaseELBus.eLWrapperConvert(elWrapper, graph.toELWrapper(), null);
        }else if(RuleNodeUtil.isAndNode(startNode) || RuleNodeUtil.isOrNode(startNode) || RuleNodeUtil.isNotNode(startNode)){
            List<Node> nodes = graphEL.path2List(graphEL.findAllPaths(startNode));
            return ELBusBoolean.getBooleanELWrapper(graphEL, nodes);
        }else{
            throw new LiteFlowELException("解析异常:"+startNode.getNodeData().getType());
        }
        return elWrapper;
    }

    // 单路径流程构建
    public static ELWrapper buildSinglePath(GraphEL graphEL, List<List<Node>> allPaths) throws LiteFlowELException {
        ThenELWrapper thenELWrapper = ELBus.then();
        Set<Node> startNodeSet = allPaths.stream().map(m -> m.get(0)).collect(Collectors.toCollection(LinkedHashSet::new));
        if(startNodeSet.size() == 1){
            return buildSinglePath(graphEL, thenELWrapper, startNodeSet.stream().findFirst().orElse(null));
        }else{
            List<Node> nodes = new ArrayList<>(startNodeSet);
            WhenELWrapper whenELWrapper = buildMultiplePath(graphEL, nodes);
            Node joinNode = graphEL.findJoinNode(nodes);
            if(joinNode == null){
                return whenELWrapper;
            }
            BaseELBus.eLWrapperConvert(thenELWrapper, whenELWrapper, null);
            buildSinglePath(graphEL, thenELWrapper, joinNode);
            return thenELWrapper;
        }
    }

    // 多节点流程构建
    public static ELWrapper buildMultipleNode(GraphEL graphEL) throws LiteFlowELException {
        List<List<List<Node>>> groupPaths = graphEL.getGroupFilterPaths();

        //只有一条路径
        if(groupPaths.size() == 1){
            return buildSinglePath(graphEL, groupPaths.get(0));
        }
        //多条路径
        WhenELWrapper whenELWrapper = ELBus.when();
        for (List<List<Node>> paths : groupPaths){
            List<Node> nodeList = graphEL.path2List(paths);
            if(nodeList.size() == 1){
                Node node = nodeList.get(0);
                if(graphEL.hasNextNode(node)){
                    ThenELWrapper thenELWrapper = ELBus.then();
                    buildSinglePath(graphEL, thenELWrapper, node);
                    whenELWrapper.when(thenELWrapper);
                }else{
                    whenELWrapper.when(ELBusNode.node(nodeList.get(0)));
                }
            }else{
                whenELWrapper.when(buildSinglePath(graphEL, paths));
            }
        }
        return whenELWrapper;
    }

    // 单节点流程构建
    public static ELWrapper buildSingleNode(GraphEL graphEL, Node node) throws LiteFlowELException {
        if(RuleNodeUtil.isCommonNode(node)){
            return ELBusThen.node(node);
        }
        if(RuleNodeUtil.isSwitchNode(node)){
            return ELBusSwitch.node(node, graphEL);
        }
        if(RuleNodeUtil.isIfNode(node)){
            return ELBusIf.node(node, null, graphEL);
        }
        if(RuleNodeUtil.isBooleanNode(node)){
            throw new LiteFlowELException("布尔组件【"+node.getNodeData().getName()+"】不能作为起点！");
        }
        if(RuleNodeUtil.isWhileNode(node)){
            return ELBusWhile.buildWhileELWrapper(graphEL, node, null);
        }
        if(RuleNodeUtil.isForNode(node)){
            return ELBusFor.buildForELWrapper(graphEL, node, null);
        }
        if(RuleNodeUtil.isIteratorNode(node)){
            return ELBusIterator.buildIteratorELWrapper(graphEL, node, null);
        }
        if(RuleNodeUtil.isChainNode(node)){
            return ELBusWrapper.chain(node);
        }
        return null;
    }

    // 单节点流程构建
    public static ELWrapper buildSingleNode(GraphEL graphEL) throws LiteFlowELException {
        List<Node> startNodes = graphEL.getStartNodes();
        Node node = startNodes.get(0);
        return buildSingleNode(graphEL, node);
    }

    // 判断是否是单节点
    public static boolean isSingleNode(GraphEL graphEL) {
        if(graphEL.getStartNodes().size() > 1){
            return false;
        }
        if(graphEL.getNodes().size() == 1){
            return true;
        }
        // 过滤特殊路径后，是单节点
        List<Node> nodeList = filterSpecialNode(graphEL);
        if(nodeList.size() == 1){
            return true;
        }
        return false;
    }

    // 获取单节点
    public static Node getSingleNode(GraphEL graphEL) {
        if(graphEL.getNodes().size() == 1){
            return graphEL.getNodes().values().stream().findFirst().orElse(null);
        }
        // 过滤特殊路径后，是单节点
        List<Node> nodeList = filterSpecialNode(graphEL);
        if(nodeList.size() == 1){
            return nodeList.get(0);
        }
        return null;
    }

    // 去除特殊路径上的节点
    public static List<Node> filterSpecialNode(GraphEL graphEL){
        List<List<Node>> allPaths = graphEL.getAllPaths();
        Set<Node> nodeSet = new LinkedHashSet<>();
        Map<Node, List<Node>> map = new LinkedHashMap<>();
        for (List<Node> nodes : allPaths){
            for (int i = 0; i < nodes.size(); i++) {
                if(i != 0){
                    Node start = nodes.get(i-1);
                    Node end = nodes.get(i);
                    boolean isSpecialPath = RuleLineUtil.isSpecialPath(graphEL, start, end);
                    nodeSet.add(start);
                    if(isSpecialPath){
                        if(map.containsKey(start)){
                            map.get(start).add(end);
                        }else{
                            map.put(start, CollUtil.toList(end));
                        }
                        break;
                    }
                    nodeSet.add(end);
                }
            }
        }
        // 如果特殊路径有聚合点，
        for (Map.Entry<Node, List<Node>> entry : map.entrySet()){
            List<List<Node>> filterList = allPaths.stream().filter(m -> m.contains(entry.getKey()) && CollUtil.containsAny(m, entry.getValue())).collect(Collectors.toList());
            List<Node> segmentationPoints = GraphUtil.findSegmentationPoints(filterList, graphEL.getNodes());
            nodeSet.addAll(segmentationPoints);
        }
        return new ArrayList<>(nodeSet);
    }

    // 判断是否是空画布
    public static boolean isEmptyGraph(GraphEL graphEL) {
        if(graphEL == null || graphEL.getAllNodes().isEmpty()){
            return true;
        }
        return false;
    }

    public static List<List<Node>> findAllPathByNodes(List<Node> nodes, GraphEL graph) {
        Set<Node> nodeSet = new HashSet<>(nodes); // 为了快速查找，将列表转换为集合
        List<List<Node>> filteredPaths = new ArrayList<>();
        Set<String> seenPaths = new HashSet<>(); // 用于避免添加重复的路径
        GraphEL newGraph = new GraphEL(nodes, graph.getLines(nodes), null);
        // 遍历所有路径
        for (List<Node> originalPath : newGraph.getAllPaths()) {
            List<Node> path = new ArrayList<>();
            for (Node node : originalPath) {
                if (nodeSet.contains(node)) {
                    path.add(node);
                }
            }
            // 将节点列表转换为字符串以检查重复
            String pathKey = path.stream().map(Node::getId).reduce("", (acc, id) -> acc + "-" + id);
            if (!path.isEmpty() && seenPaths.add(pathKey)) { // 检查路径是否为空，并且是否未被添加过
                filteredPaths.add(path);
            }
        }
        return filteredPaths;
    }

    public static List<List<Node>> findAllFilterPathByNodes(List<Node> nodes, GraphEL graph) {
        Set<Node> nodeSet = new HashSet<>(nodes); // 为了快速查找，将列表转换为集合
        List<List<Node>> filteredPaths = new ArrayList<>();
        Set<String> seenPaths = new HashSet<>(); // 用于避免添加重复的路径
        GraphEL newGraph = new GraphEL(nodes, graph.getLines(nodes), null);
        // 遍历所有路径
        for (List<Node> originalPath : newGraph.getAllFilterPaths()) {
            List<Node> path = new ArrayList<>();
            for (Node node : originalPath) {
                if (nodeSet.contains(node)) {
                    path.add(node);
                }
            }
            // 将节点列表转换为字符串以检查重复
            String pathKey = path.stream().map(Node::getId).reduce("", (acc, id) -> acc + "-" + id);
            if (!path.isEmpty() && seenPaths.add(pathKey)) { // 检查路径是否为空，并且是否未被添加过
                filteredPaths.add(path);
            }
        }
        return filteredPaths;
    }

    public static void specialJoinNodeHandler(GraphEL graphEL, Node startNode, Node specialJoinNode, ELWrapper elWrapper) throws LiteFlowELException {
        //获取全路径聚合点
        Node joinNode = graphEL.findJoinNode(startNode);
        //普通路径节点
        List<Node> commonNodeList = graphEL.getSpecialPathNodes(startNode, RuleEnums.PATH_ENUM.common_path);

        if(joinNode == null && specialJoinNode == null){
            if(!commonNodeList.isEmpty()){
                if(commonNodeList.size() == 1){
                    buildSinglePath(graphEL, elWrapper, commonNodeList.get(0));
                }else{
                    GraphEL graph = new GraphEL(commonNodeList, graphEL.getLines(commonNodeList));
                    BaseELBus.eLWrapperConvert(elWrapper, graph.toELWrapper(), null);
                }
            }
        }else if(joinNode == specialJoinNode){
            if(commonNodeList.isEmpty()){
                List<Node> nodes = graphEL.path2List(graphEL.findAllPaths(specialJoinNode));
                if(nodes.size() == 1){
                    buildSinglePath(graphEL, elWrapper, nodes.get(0));
                }else{
                    GraphEL graph = new GraphEL(nodes, graphEL.getLines(nodes));
                    BaseELBus.eLWrapperConvert(elWrapper, graph.toELWrapper(), null);
                }
            }else{
                //GraphEL graph = new GraphEL(commonNodeList, graphEL.getLines(commonNodeList));
                //BaseELBus.eLWrapperConvert(elWrapper, graph.toELWrapper(), null);
                throw new LiteFlowELException("流程错误：请删除【"+startNode.getNodeData().getName()+"】的普通路径节点！");
            }
        }else if(joinNode == null && specialJoinNode != null){
            List<Node> nodes = graphEL.path2List(graphEL.findAllPaths(specialJoinNode));
            nodes.addAll(commonNodeList);
            GraphEL graph = new GraphEL(nodes, graphEL.getLines(nodes));
            BaseELBus.eLWrapperConvert(elWrapper, graph.toELWrapper(), null);
        }else{
            List<Node> nodes = graphEL.path2List(graphEL.findAllPaths(specialJoinNode));
            nodes.addAll(commonNodeList);
            GraphEL graph = new GraphEL(nodes, graphEL.getLines(nodes));
            BaseELBus.eLWrapperConvert(elWrapper, graph.toELWrapper(), null);
        }
    }
}

