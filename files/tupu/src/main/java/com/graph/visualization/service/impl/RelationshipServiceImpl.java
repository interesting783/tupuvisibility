package com.graph.visualization.service.impl;

import com.graph.visualization.entity.Relationship;
import com.graph.visualization.enums.RelationshipTypeEnum;
import com.graph.visualization.repository.NodeRepository;
import com.graph.visualization.repository.RelationshipRepository;
import com.graph.visualization.service.RelationshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 关系服务实现类
 */
@Service
@RequiredArgsConstructor
public class RelationshipServiceImpl implements RelationshipService {
    
    private final RelationshipRepository relationshipRepository;
    private final NodeRepository nodeRepository;
    
    @Override
    @Transactional
    public Relationship createRelationship(Relationship relationship) {
        // 验证源节点和目标节点是否存在
        if (!nodeRepository.existsById(relationship.getSourceNodeId())) {
            throw new IllegalArgumentException("源节点不存在: " + relationship.getSourceNodeId());
        }
        if (!nodeRepository.existsById(relationship.getTargetNodeId())) {
            throw new IllegalArgumentException("目标节点不存在: " + relationship.getTargetNodeId());
        }
        return relationshipRepository.save(relationship);
    }
    
    @Override
    public Optional<Relationship> findRelationshipById(Long id) {
        return relationshipRepository.findById(id);
    }
    
    @Override
    @Transactional
    public Relationship updateRelationship(Relationship relationship) {
        // 检查关系是否存在
        if (!relationshipRepository.existsById(relationship.getId())) {
            throw new IllegalArgumentException("关系不存在: " + relationship.getId());
        }
        
        // 验证源节点和目标节点是否存在
        if (!nodeRepository.existsById(relationship.getSourceNodeId())) {
            throw new IllegalArgumentException("源节点不存在: " + relationship.getSourceNodeId());
        }
        if (!nodeRepository.existsById(relationship.getTargetNodeId())) {
            throw new IllegalArgumentException("目标节点不存在: " + relationship.getTargetNodeId());
        }
        
        return relationshipRepository.save(relationship);
    }
    
    @Override
    @Transactional
    public void deleteRelationship(Long id) {
        // 检查关系是否存在
        if (!relationshipRepository.existsById(id)) {
            throw new IllegalArgumentException("关系不存在: " + id);
        }
        relationshipRepository.deleteById(id);
    }
    
    @Override
    public List<Relationship> findRelationshipsBySourceNodeId(Long sourceNodeId) {
        return relationshipRepository.findBySourceNodeId(sourceNodeId);
    }
    
    @Override
    public List<Relationship> findRelationshipsByTargetNodeId(Long targetNodeId) {
        return relationshipRepository.findByTargetNodeId(targetNodeId);
    }
    
    @Override
    public List<Relationship> findRelationshipsBySourceAndTargetNodeIds(Long sourceNodeId, Long targetNodeId) {
        return relationshipRepository.findBySourceNodeIdAndTargetNodeId(sourceNodeId, targetNodeId);
    }
    
    @Override
    public List<Relationship> findRelationshipsByType(RelationshipTypeEnum relationshipType) {
        return relationshipRepository.findByRelationshipType(relationshipType);
    }
    
    @Override
    public List<Relationship> searchRelationshipsByKeyword(String keyword) {
        return relationshipRepository.findByKeyword(keyword);
    }
    
    @Override
    public List<Relationship> findRelationshipsByNodeId(Long nodeId) {
        return relationshipRepository.findRelationshipsByNodeId(nodeId);
    }
}
