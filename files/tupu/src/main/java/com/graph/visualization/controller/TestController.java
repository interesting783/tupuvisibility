package com.graph.visualization.controller;

import com.graph.visualization.entity.Node;
import com.graph.visualization.entity.Relationship;
import com.graph.visualization.repository.NodeRepository;
import com.graph.visualization.repository.RelationshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 测试控制器
 */
@RestController
@RequestMapping("/api/v1/graph/test")
@RequiredArgsConstructor
public class TestController {
    
    private final NodeRepository nodeRepository;
    private final RelationshipRepository relationshipRepository;
    
    /**
     * 验证节点ID是否存在
     */
    @GetMapping("/node/{id}")
    public ResponseEntity<Map<String, Object>> validateNode(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        Optional<Node> node = nodeRepository.findById(id);
        
        result.put("exists", node.isPresent());
        if (node.isPresent()) {
            result.put("node", node.get());
        }
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 验证关系ID是否存在
     */
    @GetMapping("/relationship/{id}")
    public ResponseEntity<Map<String, Object>> validateRelationship(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        Optional<Relationship> relationship = relationshipRepository.findById(id);
        
        result.put("exists", relationship.isPresent());
        if (relationship.isPresent()) {
            result.put("relationship", relationship.get());
        }
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 验证模式匹配参数
     */
    @GetMapping("/pattern/validate/{nodeIds}/{relationshipIds}")
    public ResponseEntity<Map<String, Object>> validatePatternParams(
            @PathVariable List<Long> nodeIds,
            @PathVariable List<Long> relationshipIds) {
        
        Map<String, Object> result = new HashMap<>();
        
        // 验证节点
        List<Node> nodes = nodeRepository.findAllById(nodeIds);
        result.put("nodes_requested", nodeIds.size());
        result.put("nodes_found", nodes.size());
        result.put("nodes", nodes);
        
        // 验证关系
        List<Relationship> relationships = relationshipRepository.findAllById(relationshipIds);
        result.put("relationships_requested", relationshipIds.size());
        result.put("relationships_found", relationships.size());
        result.put("relationships", relationships);
        
        // 检查关系是否连接所选节点
        List<Relationship> validRelationships = relationships.stream()
                .filter(r -> nodeIds.contains(r.getSourceNodeId()) && nodeIds.contains(r.getTargetNodeId()))
                .collect(Collectors.toList());
        
        result.put("valid_relationships", validRelationships.size());
        result.put("is_connected_subgraph", validRelationships.size() == relationships.size());
        
        return ResponseEntity.ok(result);
    }
}
