package com.graph.visualization.service;

import com.graph.visualization.dto.FilterRequest;
import com.graph.visualization.entity.Node;
import com.graph.visualization.entity.Relationship;

import java.util.List;

/**
 * 过滤服务接口
 */
public interface FilterService {
    /**
     * 根据过滤条件过滤节点
     */
    List<Node> filterNodes(FilterRequest filterRequest);
    
    /**
     * 根据过滤条件过滤关系
     */
    List<Relationship> filterRelationships(FilterRequest filterRequest);
}
