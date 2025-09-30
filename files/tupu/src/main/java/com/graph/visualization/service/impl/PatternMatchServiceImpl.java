package com.graph.visualization.service.impl;

import com.graph.visualization.dto.PatternMatchParam;
import com.graph.visualization.entity.Node;
import com.graph.visualization.entity.Relationship;
import com.graph.visualization.repository.NodeRepository;
import com.graph.visualization.repository.RelationshipRepository;
import com.graph.visualization.service.PatternMatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 模式匹配服务实现类
 */
@Service
@RequiredArgsConstructor
public class PatternMatchServiceImpl implements PatternMatchService {
    
    private final NodeRepository nodeRepository;
    private final RelationshipRepository relationshipRepository;
    
    @Override
    public List<Map<String, Object>> findPatternMatches(PatternMatchParam patternMatchParam) {
        List<Long> selectedNodeIds = patternMatchParam.getSelectedNodeIds();
        List<Long> selectedRelationshipIds = patternMatchParam.getSelectedRelationships();
        
        System.out.println("模式匹配请求参数：节点IDs=" + selectedNodeIds + ", 关系IDs=" + selectedRelationshipIds);
        
        // 验证输入参数
        if (selectedNodeIds == null || selectedNodeIds.isEmpty() || 
            selectedRelationshipIds == null || selectedRelationshipIds.isEmpty()) {
            throw new IllegalArgumentException("必须至少选择一个节点和一个关系");
        }
        
        // 获取模式中的节点和关系
        List<Node> patternNodes = nodeRepository.findAllById(selectedNodeIds);
        List<Relationship> patternRelationships = relationshipRepository.findAllById(selectedRelationshipIds);
        
        System.out.println("找到节点数量：" + patternNodes.size() + "/" + selectedNodeIds.size());
        System.out.println("找到关系数量：" + patternRelationships.size() + "/" + selectedRelationshipIds.size());
        
        if (patternNodes.size() != selectedNodeIds.size() || 
            patternRelationships.size() != selectedRelationshipIds.size()) {
            throw new IllegalArgumentException("部分选定的节点或关系不存在");
        }
        
        // 验证关系是否连接所选节点
        boolean isConnectedSubgraph = true;
        for (Relationship relationship : patternRelationships) {
            if (!selectedNodeIds.contains(relationship.getSourceNodeId()) || 
                !selectedNodeIds.contains(relationship.getTargetNodeId())) {
                System.out.println("警告：关系 " + relationship.getId() + " 不连接所选节点。源节点ID=" + 
                                 relationship.getSourceNodeId() + ", 目标节点ID=" + relationship.getTargetNodeId());
                isConnectedSubgraph = false;
            }
        }
        
        if (!isConnectedSubgraph) {
            System.out.println("警告：所选节点和关系不构成连通子图，这可能导致模式匹配失败。");
        }
        
        // 打印节点类型信息
        System.out.println("节点类型信息：");
        for (Node node : patternNodes) {
            System.out.println("节点ID=" + node.getId() + ", 类型=" + node.getNodeType() + ", 名称=" + node.getName());
        }
        
        // 构建模式图
        Map<Long, List<PatternEdge>> patternGraph = buildPatternGraph(patternRelationships);
        System.out.println("模式图构建完成，节点数量：" + patternGraph.size());
        
        // 打印模式图结构
        for (Map.Entry<Long, List<PatternEdge>> entry : patternGraph.entrySet()) {
            System.out.println("模式节点ID=" + entry.getKey() + ", 邻居数量=" + entry.getValue().size());
            for (PatternEdge edge : entry.getValue()) {
                System.out.println("  -> 目标节点ID=" + edge.getTargetNodeId() + 
                                 ", 关系ID=" + edge.getRelationshipId() + 
                                 ", 方向=" + (edge.isForward() ? "正向" : "反向"));
            }
        }
        
        // 查找所有可能的起始节点（与模式中的节点具有相同类型的节点）
        List<Node> allNodes = nodeRepository.findAll();
        List<Relationship> allRelationships = relationshipRepository.findAll();
        
        System.out.println("数据库中总节点数量：" + allNodes.size());
        System.out.println("数据库中总关系数量：" + allRelationships.size());
        
        // 构建完整图
        Map<Long, List<GraphEdge>> completeGraph = buildCompleteGraph(allRelationships);
        System.out.println("完整图构建完成，节点数量：" + completeGraph.size());
        
        // 查找匹配
        List<Map<String, Object>> matches = new ArrayList<>();
        
        // 从模式中选择一个节点作为起点
        Long startNodeId = patternNodes.get(0).getId();
        Node startPatternNode = getNodeById(patternNodes, startNodeId);
        System.out.println("选择起始模式节点：ID=" + startNodeId + 
                         ", 类型=" + startPatternNode.getNodeType() + 
                         ", 名称=" + startPatternNode.getName());
        
        // 统计可能的起始节点
        int potentialStartNodeCount = 0;
        for (Node node : allNodes) {
            if (isSimilarNode(node, startPatternNode)) {
                potentialStartNodeCount++;
            }
        }
        System.out.println("找到可能的起始节点数量：" + potentialStartNodeCount);
        
        // 对于每个可能的起始节点，尝试匹配模式
        int attemptCount = 0;
        for (Node potentialStartNode : allNodes) {
            if (isSimilarNode(potentialStartNode, startPatternNode)) {
                attemptCount++;
                System.out.println("尝试匹配 #" + attemptCount + ": 起始节点ID=" + potentialStartNode.getId() + 
                                 ", 类型=" + potentialStartNode.getNodeType() + 
                                 ", 名称=" + potentialStartNode.getName());
                
                Map<Long, Long> nodeMapping = new HashMap<>(); // 模式节点ID到图中节点ID的映射
                Set<Long> visitedNodes = new HashSet<>();
                Set<Long> visitedEdges = new HashSet<>();
                
                nodeMapping.put(startNodeId, potentialStartNode.getId());
                visitedNodes.add(potentialStartNode.getId());
                
                if (matchPattern(startNodeId, potentialStartNode.getId(), patternGraph, completeGraph, 
                        nodeMapping, visitedNodes, visitedEdges, patternNodes, allNodes)) {
                    System.out.println("找到匹配！节点映射：" + nodeMapping);
                    
                    // 找到匹配，收集匹配的节点和关系
                    Map<String, Object> match = new HashMap<>();
                    
                    List<Node> matchedNodes = new ArrayList<>();
                    for (Long patternNodeId : nodeMapping.keySet()) {
                        matchedNodes.add(getNodeById(allNodes, nodeMapping.get(patternNodeId)));
                    }
                    
                    List<Relationship> matchedRelationships = new ArrayList<>();
                    for (Long relationshipId : visitedEdges) {
                        matchedRelationships.add(getRelationshipById(allRelationships, relationshipId));
                    }
                    
                    match.put("nodes", matchedNodes);
                    match.put("relationships", matchedRelationships);
                    matches.add(match);
                } else {
                    System.out.println("匹配失败");
                }
            }
        }
        
        System.out.println("模式匹配完成，找到匹配数量：" + matches.size());
        return matches;
    }
    
    /**
     * 构建模式图（邻接表表示）
     */
    private Map<Long, List<PatternEdge>> buildPatternGraph(List<Relationship> relationships) {
        Map<Long, List<PatternEdge>> graph = new HashMap<>();
        
        for (Relationship relationship : relationships) {
            Long sourceId = relationship.getSourceNodeId();
            Long targetId = relationship.getTargetNodeId();
            Long relationshipId = relationship.getId();
            
            // 添加从源节点到目标节点的边
            if (!graph.containsKey(sourceId)) {
                graph.put(sourceId, new ArrayList<>());
            }
            graph.get(sourceId).add(new PatternEdge(targetId, relationshipId, true));
            
            // 添加从目标节点到源节点的边（反向）
            if (!graph.containsKey(targetId)) {
                graph.put(targetId, new ArrayList<>());
            }
            graph.get(targetId).add(new PatternEdge(sourceId, relationshipId, false));
        }
        
        return graph;
    }
    
    /**
     * 构建完整图（邻接表表示）
     */
    private Map<Long, List<GraphEdge>> buildCompleteGraph(List<Relationship> relationships) {
        Map<Long, List<GraphEdge>> graph = new HashMap<>();
        
        for (Relationship relationship : relationships) {
            Long sourceId = relationship.getSourceNodeId();
            Long targetId = relationship.getTargetNodeId();
            Long relationshipId = relationship.getId();
            
            // 添加从源节点到目标节点的边
            if (!graph.containsKey(sourceId)) {
                graph.put(sourceId, new ArrayList<>());
            }
            graph.get(sourceId).add(new GraphEdge(targetId, relationshipId, relationship.getRelationshipType(), true));
            
            // 添加从目标节点到源节点的边（反向）
            if (!graph.containsKey(targetId)) {
                graph.put(targetId, new ArrayList<>());
            }
            graph.get(targetId).add(new GraphEdge(sourceId, relationshipId, relationship.getRelationshipType(), false));
        }
        
        return graph;
    }
    
    /**
     * 递归匹配模式
     */
    private boolean matchPattern(Long patternNodeId, Long graphNodeId, 
                                Map<Long, List<PatternEdge>> patternGraph, 
                                Map<Long, List<GraphEdge>> completeGraph,
                                Map<Long, Long> nodeMapping, 
                                Set<Long> visitedNodes,
                                Set<Long> visitedEdges,
                                List<Node> patternNodes,
                                List<Node> allNodes) {
        System.out.println("  递归匹配：模式节点ID=" + patternNodeId + ", 图节点ID=" + graphNodeId + 
                         ", 当前映射大小=" + nodeMapping.size() + "/" + patternGraph.size());
        
        // 如果已经访问了所有模式节点，则匹配成功
        if (nodeMapping.size() == patternGraph.size()) {
            System.out.println("  匹配成功：已映射所有模式节点");
            return true;
        }
        
        // 获取模式节点的邻居
        List<PatternEdge> patternNeighbors = patternGraph.getOrDefault(patternNodeId, Collections.emptyList());
        System.out.println("  模式节点邻居数量：" + patternNeighbors.size());
        
        // 获取图中节点的邻居
        List<GraphEdge> graphNeighbors = completeGraph.getOrDefault(graphNodeId, Collections.emptyList());
        System.out.println("  图节点邻居数量：" + graphNeighbors.size());
        
        // 尝试匹配每个模式邻居
        for (PatternEdge patternEdge : patternNeighbors) {
            Long patternNeighborId = patternEdge.getTargetNodeId();
            System.out.println("    尝试匹配模式邻居：ID=" + patternNeighborId + 
                             ", 关系ID=" + patternEdge.getRelationshipId() + 
                             ", 方向=" + (patternEdge.isForward() ? "正向" : "反向"));
            
            // 如果已经映射了这个模式节点，检查映射是否一致
            if (nodeMapping.containsKey(patternNeighborId)) {
                Long mappedGraphNodeId = nodeMapping.get(patternNeighborId);
                System.out.println("    模式节点已映射：" + patternNeighborId + " -> " + mappedGraphNodeId);
                
                // 检查图中是否存在从当前节点到已映射节点的边
                boolean edgeExists = false;
                for (GraphEdge graphEdge : graphNeighbors) {
                    if (graphEdge.getTargetNodeId().equals(mappedGraphNodeId) && 
                        !visitedEdges.contains(graphEdge.getRelationshipId()) &&
                        graphEdge.isForward() == patternEdge.isForward()) {
                        
                        // 找到匹配的边，标记为已访问
                        visitedEdges.add(graphEdge.getRelationshipId());
                        edgeExists = true;
                        System.out.println("    找到匹配的边：" + graphEdge.getRelationshipId());
                        break;
                    }
                }
                
                if (!edgeExists) {
                    System.out.println("    未找到匹配的边，匹配失败");
                    return false; // 不一致，匹配失败
                }
            } else {
                // 尝试为这个模式节点找到一个匹配
                Node patternNeighborNode = getNodeById(patternNodes, patternNeighborId);
                System.out.println("    模式节点未映射，尝试查找匹配：" + patternNeighborId + 
                                 ", 类型=" + patternNeighborNode.getNodeType());
                
                for (GraphEdge graphEdge : graphNeighbors) {
                    Long graphNeighborId = graphEdge.getTargetNodeId();
                    
                    // 如果这个图节点已经被映射到其他模式节点，跳过
                    if (visitedNodes.contains(graphNeighborId)) {
                        System.out.println("      跳过已访问的图节点：" + graphNeighborId);
                        continue;
                    }
                    
                    // 检查节点是否相似且边方向一致
                    Node graphNeighborNode = getNodeById(allNodes, graphNeighborId);
                    System.out.println("      检查图节点：" + graphNeighborId + 
                                     ", 类型=" + graphNeighborNode.getNodeType() + 
                                     ", 方向匹配=" + (graphEdge.isForward() == patternEdge.isForward()));
                    
                    if (isSimilarNode(graphNeighborNode, patternNeighborNode) && 
                        graphEdge.isForward() == patternEdge.isForward()) {
                        
                        System.out.println("      节点相似，尝试映射：" + patternNeighborId + " -> " + graphNeighborId);
                        
                        // 尝试这个映射
                        nodeMapping.put(patternNeighborId, graphNeighborId);
                        visitedNodes.add(graphNeighborId);
                        visitedEdges.add(graphEdge.getRelationshipId());
                        
                        // 递归匹配剩余部分
                        if (matchPattern(patternNeighborId, graphNeighborId, patternGraph, completeGraph, 
                                nodeMapping, visitedNodes, visitedEdges, patternNodes, allNodes)) {
                            return true;
                        }
                        
                        // 回溯
                        System.out.println("      回溯：移除映射 " + patternNeighborId + " -> " + graphNeighborId);
                        nodeMapping.remove(patternNeighborId);
                        visitedNodes.remove(graphNeighborId);
                        visitedEdges.remove(graphEdge.getRelationshipId());
                    } else {
                        System.out.println("      节点不匹配，跳过");
                    }
                }
                
                System.out.println("    未找到匹配的邻居节点");
            }
        }
        
        // 如果已经匹配了所有邻居，则当前节点匹配成功
        boolean success = nodeMapping.size() == patternGraph.size();
        System.out.println("  当前节点匹配" + (success ? "成功" : "失败") + 
                         "：映射大小=" + nodeMapping.size() + "/" + patternGraph.size());
        return success;
    }
    
    /**
     * 检查两个节点是否相似
     */
    private boolean isSimilarNode(Node node1, Node node2) {
        // 原始条件：只比较节点类型
        // return node1.getNodeType() == node2.getNodeType();
        
        // 放宽条件：
        // 1. 如果节点类型为null，则认为匹配任何类型
        // 2. 如果节点类型相同，则匹配
        // 3. 如果节点名称相同，也认为匹配
        
        boolean typeMatch = node1.getNodeType() == null || 
                           node2.getNodeType() == null || 
                           node1.getNodeType() == node2.getNodeType();
        
        boolean nameMatch = node1.getName() != null && 
                           node2.getName() != null && 
                           node1.getName().equals(node2.getName());
        
        boolean result = typeMatch || nameMatch;
        
        System.out.println("节点相似度比较：" + 
                         "节点1(ID=" + node1.getId() + 
                         ", 类型=" + node1.getNodeType() + 
                         ", 名称=" + node1.getName() + ") 与 " +
                         "节点2(ID=" + node2.getId() + 
                         ", 类型=" + node2.getNodeType() + 
                         ", 名称=" + node2.getName() + ") " +
                         (result ? "相似" : "不相似"));
        
        return result;
    }
    
    /**
     * 根据ID获取节点
     */
    private Node getNodeById(List<Node> nodes, Long id) {
        for (Node node : nodes) {
            if (node.getId().equals(id)) {
                return node;
            }
        }
        return null;
    }
    
    /**
     * 根据ID获取关系
     */
    private Relationship getRelationshipById(List<Relationship> relationships, Long id) {
        for (Relationship relationship : relationships) {
            if (relationship.getId().equals(id)) {
                return relationship;
            }
        }
        return null;
    }
    
    /**
     * 模式图中的边
     */
    private static class PatternEdge {
        private final Long targetNodeId;
        private final Long relationshipId;
        private final boolean forward;
        
        public PatternEdge(Long targetNodeId, Long relationshipId, boolean forward) {
            this.targetNodeId = targetNodeId;
            this.relationshipId = relationshipId;
            this.forward = forward;
        }
        
        public Long getTargetNodeId() {
            return targetNodeId;
        }
        
        public Long getRelationshipId() {
            return relationshipId;
        }
        
        public boolean isForward() {
            return forward;
        }
    }
    
    /**
     * 完整图中的边
     */
    private static class GraphEdge {
        private final Long targetNodeId;
        private final Long relationshipId;
        private final Object relationshipType;
        private final boolean forward;
        
        public GraphEdge(Long targetNodeId, Long relationshipId, Object relationshipType, boolean forward) {
            this.targetNodeId = targetNodeId;
            this.relationshipId = relationshipId;
            this.relationshipType = relationshipType;
            this.forward = forward;
        }
        
        public Long getTargetNodeId() {
            return targetNodeId;
        }
        
        public Long getRelationshipId() {
            return relationshipId;
        }
        
        public Object getRelationshipType() {
            return relationshipType;
        }
        
        public boolean isForward() {
            return forward;
        }
    }
}
