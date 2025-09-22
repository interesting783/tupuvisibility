package com.graph.visualization.service;

import com.graph.visualization.dto.PatternMatchParam;
import com.graph.visualization.entity.Node;
import com.graph.visualization.entity.Relationship;

import java.util.List;
import java.util.Map;

/**
 * 模式匹配服务接口
 */
public interface PatternMatchService {
    /**
     * 根据模式参数查找匹配的子图实例
     * 
     * @param patternMatchParam 模式匹配参数
     * @return 匹配的子图实例列表，每个实例包含节点和关系的映射
     */
    List<Map<String, Object>> findPatternMatches(PatternMatchParam patternMatchParam);
}
