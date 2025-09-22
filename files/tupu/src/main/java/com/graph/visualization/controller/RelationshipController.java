package com.graph.visualization.controller;

import com.graph.visualization.entity.Relationship;
import com.graph.visualization.enums.RelationshipTypeEnum;
import com.graph.visualization.service.RelationshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 关系管理控制器
 */
@RestController
@RequestMapping("/api/v1/graph/relationships")
@RequiredArgsConstructor
public class RelationshipController {
    
    private final RelationshipService relationshipService;
    
    /**
     * 创建关系
     */
    @PostMapping
    public ResponseEntity<Relationship> createRelationship(@RequestBody Relationship relationship) {
        Relationship createdRelationship = relationshipService.createRelationship(relationship);
        return new ResponseEntity<>(createdRelationship, HttpStatus.CREATED);
    }
    
    /**
     * 根据ID查询关系
     */
    @GetMapping("/{id}")
    public ResponseEntity<Relationship> getRelationshipById(@PathVariable Long id) {
        return relationshipService.findRelationshipById(id)
                .map(relationship -> new ResponseEntity<>(relationship, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    /**
     * 更新关系
     */
    @PutMapping("/{id}")
    public ResponseEntity<Relationship> updateRelationship(@PathVariable Long id, @RequestBody Relationship relationship) {
        if (!id.equals(relationship.getId())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Relationship updatedRelationship = relationshipService.updateRelationship(relationship);
        return new ResponseEntity<>(updatedRelationship, HttpStatus.OK);
    }
    
    /**
     * 删除关系
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRelationship(@PathVariable Long id) {
        relationshipService.deleteRelationship(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    /**
     * 根据源节点ID查询关系
     */
    @GetMapping("/source/{sourceNodeId}")
    public ResponseEntity<List<Relationship>> getRelationshipsBySourceNodeId(@PathVariable Long sourceNodeId) {
        List<Relationship> relationships = relationshipService.findRelationshipsBySourceNodeId(sourceNodeId);
        return new ResponseEntity<>(relationships, HttpStatus.OK);
    }
    
    /**
     * 根据目标节点ID查询关系
     */
    @GetMapping("/target/{targetNodeId}")
    public ResponseEntity<List<Relationship>> getRelationshipsByTargetNodeId(@PathVariable Long targetNodeId) {
        List<Relationship> relationships = relationshipService.findRelationshipsByTargetNodeId(targetNodeId);
        return new ResponseEntity<>(relationships, HttpStatus.OK);
    }
    
    /**
     * 根据源节点ID和目标节点ID查询关系
     */
    @GetMapping("/between")
    public ResponseEntity<List<Relationship>> getRelationshipsBetweenNodes(
            @RequestParam Long sourceNodeId, @RequestParam Long targetNodeId) {
        List<Relationship> relationships = relationshipService.findRelationshipsBySourceAndTargetNodeIds(sourceNodeId, targetNodeId);
        return new ResponseEntity<>(relationships, HttpStatus.OK);
    }
    
    /**
     * 根据关系类型查询关系
     */
    @GetMapping("/type/{relationshipType}")
    public ResponseEntity<List<Relationship>> getRelationshipsByType(@PathVariable RelationshipTypeEnum relationshipType) {
        List<Relationship> relationships = relationshipService.findRelationshipsByType(relationshipType);
        return new ResponseEntity<>(relationships, HttpStatus.OK);
    }
    
    /**
     * 根据关键词搜索关系
     */
    @GetMapping("/search")
    public ResponseEntity<List<Relationship>> searchRelationships(@RequestParam String keyword) {
        List<Relationship> relationships = relationshipService.searchRelationshipsByKeyword(keyword);
        return new ResponseEntity<>(relationships, HttpStatus.OK);
    }
    
    /**
     * 查找与指定节点相关的所有关系
     */
    @GetMapping("/node/{nodeId}")
    public ResponseEntity<List<Relationship>> getRelationshipsByNodeId(@PathVariable Long nodeId) {
        List<Relationship> relationships = relationshipService.findRelationshipsByNodeId(nodeId);
        return new ResponseEntity<>(relationships, HttpStatus.OK);
    }
}
