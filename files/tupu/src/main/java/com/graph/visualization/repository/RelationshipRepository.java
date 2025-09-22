package com.graph.visualization.repository;

import com.graph.visualization.entity.Relationship;
import com.graph.visualization.enums.RelationshipTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 关系数据访问接口
 */
@Repository
public interface RelationshipRepository extends JpaRepository<Relationship, Long> {
    /**
     * 根据源节点ID查找关系
     */
    List<Relationship> findBySourceNodeId(Long sourceNodeId);
    
    /**
     * 根据目标节点ID查找关系
     */
    List<Relationship> findByTargetNodeId(Long targetNodeId);
    
    /**
     * 根据源节点ID和目标节点ID查找关系
     */
    List<Relationship> findBySourceNodeIdAndTargetNodeId(Long sourceNodeId, Long targetNodeId);
    
    /**
     * 根据关系类型查找关系
     */
    List<Relationship> findByRelationshipType(RelationshipTypeEnum relationshipType);
    
    /**
     * 根据关系名称模糊查询关系
     */
    @Query("SELECT r FROM Relationship r WHERE r.relationshipName LIKE %:keyword% OR r.description LIKE %:keyword%")
    List<Relationship> findByKeyword(@Param("keyword") String keyword);
    
    /**
     * 查找与指定节点相关的所有关系
     */
    @Query("SELECT r FROM Relationship r WHERE r.sourceNodeId = :nodeId OR r.targetNodeId = :nodeId")
    List<Relationship> findRelationshipsByNodeId(@Param("nodeId") Long nodeId);
}
