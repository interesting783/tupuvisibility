package com.graph.visualization.service;

import com.graph.visualization.entity.Relationship;
import com.graph.visualization.enums.RelationshipTypeEnum;

import java.util.List;
import java.util.Optional;

/**
 * 关系服务接口
 */
public interface RelationshipService {
    /**
     * 创建关系
     */
    Relationship createRelationship(Relationship relationship);
    
    /**
     * 根据ID查找关系
     */
    Optional<Relationship> findRelationshipById(Long id);
    
    /**
     * 更新关系
     */
    Relationship updateRelationship(Relationship relationship);
    
    /**
     * 删除关系
     */
    void deleteRelationship(Long id);
    
    /**
     * 根据源节点ID查找关系
     */
    List<Relationship> findRelationshipsBySourceNodeId(Long sourceNodeId);
    
    /**
     * 根据目标节点ID查找关系
     */
    List<Relationship> findRelationshipsByTargetNodeId(Long targetNodeId);
    
    /**
     * 根据源节点ID和目标节点ID查找关系
     */
    List<Relationship> findRelationshipsBySourceAndTargetNodeIds(Long sourceNodeId, Long targetNodeId);
    
    /**
     * 根据关系类型查找关系
     */
    List<Relationship> findRelationshipsByType(RelationshipTypeEnum relationshipType);
    
    /**
     * 根据关键词搜索关系
     */
    List<Relationship> searchRelationshipsByKeyword(String keyword);
    
    /**
     * 查找与指定节点相关的所有关系
     */
    List<Relationship> findRelationshipsByNodeId(Long nodeId);
}
