package com.graph.visualization.service.impl;

import com.graph.visualization.entity.Node;
import com.graph.visualization.entity.Relationship;
import com.graph.visualization.repository.NodeRepository;
import com.graph.visualization.repository.RelationshipRepository;
import com.graph.visualization.service.GraphSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 图谱搜索服务实现类
 */
@Service
@RequiredArgsConstructor
public class GraphSearchServiceImpl implements GraphSearchService {
    
    private final NodeRepository nodeRepository;
    private final RelationshipRepository relationshipRepository;
    
    @Override
    public Map<String, Object> searchByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("搜索关键词不能为空");
        }
        
        // 搜索节点和关系
        List<Node> nodes = searchNodes(keyword);
        List<Relationship> relationships = searchRelationships(keyword);
        
        // 构建结果
        Map<String, Object> result = new HashMap<>();
        result.put("nodes", nodes);
        result.put("relationships", relationships);
        
        return result;
    }
    
    @Override
    public List<Node> searchNodes(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("搜索关键词不能为空");
        }
        
        // 使用仓库中定义的查询方法搜索节点
        return nodeRepository.findByKeyword(keyword);
    }
    
    @Override
    public List<Relationship> searchRelationships(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("搜索关键词不能为空");
        }
        
        // 使用仓库中定义的查询方法搜索关系
        return relationshipRepository.findByKeyword(keyword);
    }
}
