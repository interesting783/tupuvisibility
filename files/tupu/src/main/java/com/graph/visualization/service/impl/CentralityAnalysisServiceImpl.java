package com.graph.visualization.service.impl;

import com.graph.visualization.dto.CentralityResult;
import com.graph.visualization.entity.Node;
import com.graph.visualization.entity.Relationship;
import com.graph.visualization.repository.NodeRepository;
import com.graph.visualization.repository.RelationshipRepository;
import com.graph.visualization.service.CentralityAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 中心度分析服务实现类
 */
@Service
@RequiredArgsConstructor
public class CentralityAnalysisServiceImpl implements CentralityAnalysisService {
    
    private final NodeRepository nodeRepository;
    private final RelationshipRepository relationshipRepository;
    
    @Override
    public CentralityResult analyzeCentrality() {
        // 默认使用度中心性分析
        return analyzeDegreeCentrality();
    }
    
    @Override
    public CentralityResult analyzeDegreeCentrality() {
        List<Node> nodes = nodeRepository.findAll();
        Map<Long, Double> centralityValues = new HashMap<>();
        
        // 计算每个节点的度（连接数）
        for (Node node : nodes) {
            Long nodeId = node.getId();
            List<Relationship> relationships = relationshipRepository.findRelationshipsByNodeId(nodeId);
            centralityValues.put(nodeId, (double) relationships.size());
        }
        
        // 找出最重要的节点（度最高的节点）
        Map<Long, String> importantNodes = findImportantNodes(centralityValues, nodes);
        
        CentralityResult result = new CentralityResult();
        result.setCentralityValues(centralityValues);
        result.setImportantNodes(importantNodes);
        
        return result;
    }
    
    @Override
    public CentralityResult analyzeClosenessCentrality() {
        List<Node> nodes = nodeRepository.findAll();
        Map<Long, Double> centralityValues = new HashMap<>();
        
        // 构建邻接表表示图
        Map<Long, List<Long>> adjacencyList = buildAdjacencyList();
        
        // 计算每个节点的接近中心性
        for (Node node : nodes) {
            Long nodeId = node.getId();
            
            // 使用广度优先搜索计算到所有其他节点的最短路径长度
            Map<Long, Integer> distances = calculateShortestPaths(nodeId, adjacencyList);
            
            // 计算接近中心性：1 / 平均最短路径长度
            int totalDistance = 0;
            int reachableNodes = 0;
            
            for (Map.Entry<Long, Integer> entry : distances.entrySet()) {
                if (!entry.getKey().equals(nodeId) && entry.getValue() < Integer.MAX_VALUE) {
                    totalDistance += entry.getValue();
                    reachableNodes++;
                }
            }
            
            double closenessCentrality = 0.0;
            if (reachableNodes > 0) {
                closenessCentrality = (double) reachableNodes / totalDistance;
            }
            
            centralityValues.put(nodeId, closenessCentrality);
        }
        
        // 找出最重要的节点（接近中心性最高的节点）
        Map<Long, String> importantNodes = findImportantNodes(centralityValues, nodes);
        
        CentralityResult result = new CentralityResult();
        result.setCentralityValues(centralityValues);
        result.setImportantNodes(importantNodes);
        
        return result;
    }
    
    @Override
    public CentralityResult analyzeBetweennessCentrality() {
        List<Node> nodes = nodeRepository.findAll();
        Map<Long, Double> centralityValues = new HashMap<>();
        
        // 初始化中介中心性为0
        for (Node node : nodes) {
            centralityValues.put(node.getId(), 0.0);
        }
        
        // 构建邻接表表示图
        Map<Long, List<Long>> adjacencyList = buildAdjacencyList();
        
        // 对每对节点计算最短路径，并统计每个节点作为中介的次数
        for (Node source : nodes) {
            Long sourceId = source.getId();
            
            // 使用广度优先搜索计算从源节点到所有其他节点的最短路径
            Map<Long, List<List<Long>>> shortestPaths = calculateAllShortestPaths(sourceId, adjacencyList);
            
            // 更新中介中心性
            for (Node target : nodes) {
                Long targetId = target.getId();
                
                if (!sourceId.equals(targetId)) {
                    List<List<Long>> paths = shortestPaths.getOrDefault(targetId, Collections.emptyList());
                    
                    if (!paths.isEmpty()) {
                        // 计算每个中介节点出现在最短路径中的次数
                        Map<Long, Integer> intermediateNodeCounts = new HashMap<>();
                        
                        for (List<Long> path : paths) {
                            // 排除路径的起点和终点
                            for (int i = 1; i < path.size() - 1; i++) {
                                Long intermediateNodeId = path.get(i);
                                intermediateNodeCounts.put(intermediateNodeId, 
                                        intermediateNodeCounts.getOrDefault(intermediateNodeId, 0) + 1);
                            }
                        }
                        
                        // 更新中介中心性
                        for (Map.Entry<Long, Integer> entry : intermediateNodeCounts.entrySet()) {
                            Long nodeId = entry.getKey();
                            double contribution = (double) entry.getValue() / paths.size();
                            centralityValues.put(nodeId, centralityValues.get(nodeId) + contribution);
                        }
                    }
                }
            }
        }
        
        // 找出最重要的节点（中介中心性最高的节点）
        Map<Long, String> importantNodes = findImportantNodes(centralityValues, nodes);
        
        CentralityResult result = new CentralityResult();
        result.setCentralityValues(centralityValues);
        result.setImportantNodes(importantNodes);
        
        return result;
    }
    
    /**
     * 构建图的邻接表表示
     */
    private Map<Long, List<Long>> buildAdjacencyList() {
        List<Node> nodes = nodeRepository.findAll();
        List<Relationship> relationships = relationshipRepository.findAll();
        Map<Long, List<Long>> adjacencyList = new HashMap<>();
        
        // 初始化邻接表
        for (Node node : nodes) {
            adjacencyList.put(node.getId(), new ArrayList<>());
        }
        
        // 添加边到邻接表
        for (Relationship relationship : relationships) {
            Long sourceId = relationship.getSourceNodeId();
            Long targetId = relationship.getTargetNodeId();
            
            // 添加双向边（无向图）
            adjacencyList.get(sourceId).add(targetId);
            adjacencyList.get(targetId).add(sourceId);
        }
        
        return adjacencyList;
    }
    
    /**
     * 使用广度优先搜索计算从源节点到所有其他节点的最短路径长度
     */
    private Map<Long, Integer> calculateShortestPaths(Long sourceId, Map<Long, List<Long>> adjacencyList) {
        Map<Long, Integer> distances = new HashMap<>();
        Queue<Long> queue = new LinkedList<>();
        Set<Long> visited = new HashSet<>();
        
        // 初始化距离为无穷大
        for (Long nodeId : adjacencyList.keySet()) {
            distances.put(nodeId, Integer.MAX_VALUE);
        }
        
        // 源节点到自身的距离为0
        distances.put(sourceId, 0);
        queue.offer(sourceId);
        visited.add(sourceId);
        
        while (!queue.isEmpty()) {
            Long currentId = queue.poll();
            int currentDistance = distances.get(currentId);
            
            for (Long neighborId : adjacencyList.get(currentId)) {
                if (!visited.contains(neighborId)) {
                    visited.add(neighborId);
                    distances.put(neighborId, currentDistance + 1);
                    queue.offer(neighborId);
                }
            }
        }
        
        return distances;
    }
    
    /**
     * 计算从源节点到所有其他节点的所有最短路径
     */
    private Map<Long, List<List<Long>>> calculateAllShortestPaths(Long sourceId, Map<Long, List<Long>> adjacencyList) {
        Map<Long, List<List<Long>>> allPaths = new HashMap<>();
        Map<Long, Integer> distances = calculateShortestPaths(sourceId, adjacencyList);
        
        // 对每个目标节点，使用回溯法找出所有最短路径
        for (Long targetId : adjacencyList.keySet()) {
            if (!sourceId.equals(targetId) && distances.get(targetId) < Integer.MAX_VALUE) {
                List<List<Long>> paths = new ArrayList<>();
                List<Long> currentPath = new ArrayList<>();
                currentPath.add(targetId);
                
                findAllShortestPathsDFS(sourceId, targetId, distances.get(targetId), adjacencyList, distances, currentPath, paths);
                
                allPaths.put(targetId, paths);
            }
        }
        
        return allPaths;
    }
    
    /**
     * 使用深度优先搜索找出所有最短路径
     */
    private void findAllShortestPathsDFS(Long sourceId, Long currentId, int targetDistance, 
                                        Map<Long, List<Long>> adjacencyList, Map<Long, Integer> distances, 
                                        List<Long> currentPath, List<List<Long>> paths) {
        // 如果到达源节点，添加路径到结果
        if (currentId.equals(sourceId)) {
            List<Long> completePath = new ArrayList<>(currentPath);
            Collections.reverse(completePath); // 反转路径，使其从源节点开始
            paths.add(completePath);
            return;
        }
        
        // 继续搜索
        for (Long neighborId : adjacencyList.get(currentId)) {
            if (distances.get(neighborId) == targetDistance - 1) {
                currentPath.add(neighborId);
                findAllShortestPathsDFS(sourceId, neighborId, targetDistance - 1, adjacencyList, distances, currentPath, paths);
                currentPath.remove(currentPath.size() - 1); // 回溯
            }
        }
    }
    
    /**
     * 找出最重要的节点（中心度值最高的节点）
     */
    private Map<Long, String> findImportantNodes(Map<Long, Double> centralityValues, List<Node> nodes) {
        // 创建节点ID到名称的映射
        Map<Long, String> nodeNames = nodes.stream()
                .collect(Collectors.toMap(Node::getId, Node::getName));
        
        // 按中心度值排序
        List<Map.Entry<Long, Double>> sortedEntries = new ArrayList<>(centralityValues.entrySet());
        sortedEntries.sort(Map.Entry.<Long, Double>comparingByValue().reversed());
        
        // 取前5个或更少
        int limit = Math.min(5, sortedEntries.size());
        Map<Long, String> importantNodes = new HashMap<>();
        
        for (int i = 0; i < limit; i++) {
            Map.Entry<Long, Double> entry = sortedEntries.get(i);
            Long nodeId = entry.getKey();
            importantNodes.put(nodeId, nodeNames.get(nodeId));
        }
        
        return importantNodes;
    }
}
