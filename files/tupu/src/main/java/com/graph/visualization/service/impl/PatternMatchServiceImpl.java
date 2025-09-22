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
        
        // 验证输入参数
        if (selectedNodeIds == null || selectedNodeIds.isEmpty() || 
            selectedRelationshipIds == null || selectedRelationshipIds.isEmpty()) {
            throw new IllegalArgumentException("必须至少选择一个节点和一个关系");
        }
        
        // 获取模式中的节点和关系
        List<Node> patternNodes = nodeRepository.findAllById(selectedNodeIds);
        List<Relationship> patternRelationships = relationshipRepository.findAllById(selectedRelationshipIds);
        
        if (patternNodes.size() != selectedNodeIds.size() || 
            patternRelationships.size() != selectedRelationshipIds.size()) {
            throw new IllegalArgumentException("部分选定的节点或关系不存在");
        }
        
        // 构建模式图
        Map<Long, List<PatternEdge>> patternGraph = buildPatternGraph(patternRelationships);
        
        // 查找所有可能的起始节点（与模式中的节点具有相同类型的节点）
        List<Node> allNodes = nodeRepository.findAll();
        List<Relationship> allRelationships = relationshipRepository.findAll();
        
        // 构建完整图
        Map<Long, List<GraphEdge>> completeGraph = buildCompleteGraph(allRelationships);
        
        // 查找匹配
        List<Map<String, Object>> matches = new ArrayList<>();
        
        // 从模式中选择一个节点作为起点
        Long startNodeId = patternNodes.get(0).getId();
        
        // 对于每个可能的起始节点，尝试匹配模式
        for (Node potentialStartNode : allNodes) {
            if (isSimilarNode(potentialStartNode, getNodeById(patternNodes, startNodeId))) {
                Map<Long, Long> nodeMapping = new HashMap<>(); // 模式节点ID到图中节点ID的映射
                Set<Long> visitedNodes = new HashSet<>();
                Set<Long> visitedEdges = new HashSet<>();
                
                nodeMapping.put(startNodeId, potentialStartNode.getId());
                visitedNodes.add(potentialStartNode.getId());
                
                if (matchPattern(startNodeId, potentialStartNode.getId(), patternGraph, completeGraph, 
                        nodeMapping, visitedNodes, visitedEdges, patternNodes, allNodes)) {
                    // 找到匹配，收集匹配的节点和关系
                    Map<String, Object> match = new HashMap<>();
                    
                    List<Node> matchedNodes = new ArrayList<>();
                    for (Long patternNodeId : nodeMapping.keySet()) {
                        matchedNodes.add(getNodeById(allNodes, nodeMapping.get(patternNodeId)));
                    }
                    
                    List<Relationship> matchedRelationships = new ArrayList<>();
                    for (Long patternEdgeId : visitedEdges) {
                        matchedRelationships.add(getRelationshipById(allRelationships, patternEdgeId));
                    }
                    
                    match.put("nodes", matchedNodes);
                    match.put("relationships", matchedRelationships);
                    matches.add(match);
                }
            }
        }
        
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
        // 如果已经访问了所有模式节点，则匹配成功
        if (nodeMapping.size() == patternGraph.size()) {
            return true;
        }
        
        // 获取模式节点的邻居
        List<PatternEdge> patternNeighbors = patternGraph.getOrDefault(patternNodeId, Collections.emptyList());
        
        // 获取图中节点的邻居
        List<GraphEdge> graphNeighbors = completeGraph.getOrDefault(graphNodeId, Collections.emptyList());
        
        // 尝试匹配每个模式邻居
        for (PatternEdge patternEdge : patternNeighbors) {
            Long patternNeighborId = patternEdge.getTargetNodeId();
            
            // 如果已经映射了这个模式节点，检查映射是否一致
            if (nodeMapping.containsKey(patternNeighborId)) {
                Long mappedGraphNodeId = nodeMapping.get(patternNeighborId);
                
                // 检查图中是否存在从当前节点到已映射节点的边
                boolean edgeExists = false;
                for (GraphEdge graphEdge : graphNeighbors) {
                    if (graphEdge.getTargetNodeId().equals(mappedGraphNodeId) && 
                        !visitedEdges.contains(graphEdge.getRelationshipId()) &&
                        graphEdge.isForward() == patternEdge.isForward()) {
                        
                        // 找到匹配的边，标记为已访问
                        visitedEdges.add(graphEdge.getRelationshipId());
                        edgeExists = true;
                        break;
                    }
                }
                
                if (!edgeExists) {
                    return false; // 不一致，匹配失败
                }
            } else {
                // 尝试为这个模式节点找到一个匹配
                Node patternNeighborNode = getNodeById(patternNodes, patternNeighborId);
                
                for (GraphEdge graphEdge : graphNeighbors) {
                    Long graphNeighborId = graphEdge.getTargetNodeId();
                    
                    // 如果这个图节点已经被映射到其他模式节点，跳过
                    if (visitedNodes.contains(graphNeighborId)) {
                        continue;
                    }
                    
                    // 检查节点是否相似且边方向一致
                    Node graphNeighborNode = getNodeById(allNodes, graphNeighborId);
                    if (isSimilarNode(graphNeighborNode, patternNeighborNode) && 
                        graphEdge.isForward() == patternEdge.isForward()) {
                        
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
                        nodeMapping.remove(patternNeighborId);
                        visitedNodes.remove(graphNeighborId);
                        visitedEdges.remove(graphEdge.getRelationshipId());
                    }
                }
            }
        }
        
        // 如果已经匹配了所有邻居，则当前节点匹配成功
        return nodeMapping.size() == patternGraph.size();
    }
    
    /**
     * 检查两个节点是否相似（类型相同）
     */
    private boolean isSimilarNode(Node node1, Node node2) {
        return node1.getNodeType() == node2.getNodeType();
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
