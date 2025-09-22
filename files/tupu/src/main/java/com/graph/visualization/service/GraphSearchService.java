package com.graph.visualization.service;

import com.graph.visualization.entity.Node;
import com.graph.visualization.entity.Relationship;

import java.util.List;
import java.util.Map;

/**
 * 图谱搜索服务接口
 */
public interface GraphSearchService {
    /**
     * 根据关键词搜索节点和关系
     * 
     * @param keyword 搜索关键词
     * @return 包含匹配的节点和关系的结果
     */
    Map<String, Object> searchByKeyword(String keyword);
    
    /**
     * 搜索节点
     * 
     * @param keyword 搜索关键词
     * @return 匹配的节点列表
     */
    List<Node> searchNodes(String keyword);
    
    /**
     * 搜索关系
     * 
     * @param keyword 搜索关键词
     * @return 匹配的关系列表
     */
    List<Relationship> searchRelationships(String keyword);
}
