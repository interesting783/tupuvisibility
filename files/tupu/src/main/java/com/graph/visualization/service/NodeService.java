package com.graph.visualization.service;

import com.graph.visualization.entity.Node;
import com.graph.visualization.enums.NodeTypeEnum;

import java.util.List;
import java.util.Optional;

/**
 * 节点服务接口
 */
public interface NodeService {
    /**
     * 创建节点
     */
    Node createNode(Node node);
    
    /**
     * 根据ID查找节点
     */
    Optional<Node> findNodeById(Long id);
    
    /**
     * 更新节点
     */
    Node updateNode(Node node);
    
    /**
     * 删除节点
     */
    void deleteNode(Long id);
    
    /**
     * 根据节点类型查找节点
     */
    List<Node> findNodesByType(NodeTypeEnum nodeType);
    
    /**
     * 根据本体名称查找节点
     */
    List<Node> findNodesByOntology(String ontologyName);
    
    /**
     * 根据关键词搜索节点
     */
    List<Node> searchNodesByKeyword(String keyword);
}
