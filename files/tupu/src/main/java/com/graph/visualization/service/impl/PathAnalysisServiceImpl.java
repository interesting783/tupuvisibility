package com.graph.visualization.service.impl;

import com.graph.visualization.dto.PathQueryParam;
import com.graph.visualization.dto.PathResult;
import com.graph.visualization.entity.Node;
import com.graph.visualization.entity.Relationship;
import com.graph.visualization.repository.NodeRepository;
import com.graph.visualization.repository.RelationshipRepository;
import com.graph.visualization.service.PathAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 路径分析服务实现类
 */
@Service
@RequiredArgsConstructor
public class PathAnalysisServiceImpl implements PathAnalysisService {
    
    private final NodeRepository nodeRepository;
    private final RelationshipRepository relationshipRepository;
    
    @Override
    public PathResult findShortestPath(PathQueryParam pathQueryParam) {
        Long sourceNodeId = pathQueryParam.getSourceNodeId();
        Long targetNodeId = pathQueryParam.getTargetNodeId();
        Integer maxDepth = pathQueryParam.getMaxDepth();
        
        // 验证源节点和目标节点是否存在
        if (!nodeRepository.existsById(sourceNodeId)) {
            throw new IllegalArgumentException("源节点不存在: " + sourceNodeId);
        }
        if (!nodeRepository.existsById(targetNodeId)) {
            throw new IllegalArgumentException("目标节点不存在: " + targetNodeId);
        }
        
        // 使用广度优先搜索查找最短路径
        Map<Long, Long> parentMap = new HashMap<>(); // 记录节点的父节点
        Map<Long, Relationship> relationshipMap = new HashMap<>(); // 记录节点之间的关系
        
        Queue<Long> queue = new LinkedList<>();
        Set<Long> visited = new HashSet<>();
        
        queue.offer(sourceNodeId);
        visited.add(sourceNodeId);
        
        boolean found = false;
        while (!queue.isEmpty() && !found) {
            Long currentNodeId = queue.poll();
            
            // 获取当前节点的所有关系
            List<Relationship> relationships = relationshipRepository.findRelationshipsByNodeId(currentNodeId);
            
            for (Relationship relationship : relationships) {
                Long nextNodeId;
                
                // 确定关系的另一端节点
                if (relationship.getSourceNodeId().equals(currentNodeId)) {
                    nextNodeId = relationship.getTargetNodeId();
                } else if (relationship.getTargetNodeId().equals(currentNodeId)) {
                    nextNodeId = relationship.getSourceNodeId();
                } else {
                    continue; // 跳过不相关的关系
                }
                
                // 如果节点未访问过，则加入队列
                if (!visited.contains(nextNodeId)) {
                    visited.add(nextNodeId);
                    queue.offer(nextNodeId);
                    parentMap.put(nextNodeId, currentNodeId);
                    relationshipMap.put(nextNodeId, relationship);
                    
                    // 如果找到目标节点，结束搜索
                    if (nextNodeId.equals(targetNodeId)) {
                        found = true;
                        break;
                    }
                }
            }
        }
        
        // 如果未找到路径，返回空结果
        if (!found) {
            return null;
        }
        
        // 重建路径
        List<Node> pathNodes = new ArrayList<>();
        List<Relationship> pathRelationships = new ArrayList<>();
        
        Long currentNodeId = targetNodeId;
        while (!currentNodeId.equals(sourceNodeId)) {
            final Long nodeIdForLambda = currentNodeId; // 创建一个final变量用于lambda表达式
            Node node = nodeRepository.findById(nodeIdForLambda)
                    .orElseThrow(() -> new IllegalStateException("节点不存在: " + nodeIdForLambda));
            pathNodes.add(0, node); // 添加到列表开头
            
            Relationship relationship = relationshipMap.get(currentNodeId);
            pathRelationships.add(0, relationship); // 添加到列表开头
            
            currentNodeId = parentMap.get(currentNodeId);
        }
        
        // 添加源节点
        Node sourceNode = nodeRepository.findById(sourceNodeId)
                .orElseThrow(() -> new IllegalStateException("源节点不存在: " + sourceNodeId));
        pathNodes.add(0, sourceNode);
        
        // 创建结果
        PathResult result = new PathResult();
        result.setPathLength(pathRelationships.size());
        result.setNodes(pathNodes);
        result.setRelationships(pathRelationships);
        
        return result;
    }
    
    @Override
    public List<PathResult> findAllPaths(PathQueryParam pathQueryParam) {
        Long sourceNodeId = pathQueryParam.getSourceNodeId();
        Long targetNodeId = pathQueryParam.getTargetNodeId();
        Integer maxDepth = pathQueryParam.getMaxDepth();
        Integer maxPathCount = pathQueryParam.getMaxPathCount();
        
        // 验证源节点和目标节点是否存在
        if (!nodeRepository.existsById(sourceNodeId)) {
            throw new IllegalArgumentException("源节点不存在: " + sourceNodeId);
        }
        if (!nodeRepository.existsById(targetNodeId)) {
            throw new IllegalArgumentException("目标节点不存在: " + targetNodeId);
        }
        
        List<PathResult> allPaths = new ArrayList<>();
        Set<Long> visited = new HashSet<>();
        List<Node> currentPath = new ArrayList<>();
        List<Relationship> currentRelationships = new ArrayList<>();
        
        // 获取源节点
        Node sourceNode = nodeRepository.findById(sourceNodeId)
                .orElseThrow(() -> new IllegalStateException("源节点不存在: " + sourceNodeId));
        
        // 开始深度优先搜索
        visited.add(sourceNodeId);
        currentPath.add(sourceNode);
        
        findAllPathsDFS(sourceNodeId, targetNodeId, maxDepth, maxPathCount, visited, currentPath, 
                currentRelationships, allPaths);
        
        return allPaths;
    }
    
    /**
     * 使用深度优先搜索查找所有路径
     */
    private void findAllPathsDFS(Long currentNodeId, Long targetNodeId, Integer maxDepth, Integer maxPathCount,
                                Set<Long> visited, List<Node> currentPath, List<Relationship> currentRelationships,
                                List<PathResult> allPaths) {
        // 如果达到最大路径数，停止搜索
        if (maxPathCount != null && allPaths.size() >= maxPathCount) {
            return;
        }
        
        // 如果达到目标节点，添加路径到结果
        if (currentNodeId.equals(targetNodeId)) {
            PathResult pathResult = new PathResult();
            pathResult.setPathLength(currentRelationships.size());
            pathResult.setNodes(new ArrayList<>(currentPath));
            pathResult.setRelationships(new ArrayList<>(currentRelationships));
            allPaths.add(pathResult);
            return;
        }
        
        // 如果达到最大深度，停止搜索
        if (maxDepth != null && currentPath.size() > maxDepth) {
            return;
        }
        
        // 获取当前节点的所有关系
        List<Relationship> relationships = relationshipRepository.findRelationshipsByNodeId(currentNodeId);
        
        for (Relationship relationship : relationships) {
            Long nextNodeId;
            
            // 确定关系的另一端节点
            if (relationship.getSourceNodeId().equals(currentNodeId)) {
                nextNodeId = relationship.getTargetNodeId();
            } else if (relationship.getTargetNodeId().equals(currentNodeId)) {
                nextNodeId = relationship.getSourceNodeId();
            } else {
                continue; // 跳过不相关的关系
            }
            
            // 如果节点未访问过，则继续搜索
            if (!visited.contains(nextNodeId)) {
                Node nextNode = nodeRepository.findById(nextNodeId)
                        .orElseThrow(() -> new IllegalStateException("节点不存在: " + nextNodeId));
                
                // 添加到当前路径
                visited.add(nextNodeId);
                currentPath.add(nextNode);
                currentRelationships.add(relationship);
                
                // 继续深度优先搜索
                findAllPathsDFS(nextNodeId, targetNodeId, maxDepth, maxPathCount, visited, currentPath,
                        currentRelationships, allPaths);
                
                // 回溯
                visited.remove(nextNodeId);
                currentPath.remove(currentPath.size() - 1);
                currentRelationships.remove(currentRelationships.size() - 1);
            }
        }
    }
}
