package com.graph.visualization.service;

import com.graph.visualization.dto.PathQueryParam;
import com.graph.visualization.dto.PathResult;

import java.util.List;

/**
 * 路径分析服务接口
 */
public interface PathAnalysisService {
    /**
     * 计算两个节点之间的最短路径
     */
    PathResult findShortestPath(PathQueryParam pathQueryParam);
    
    /**
     * 查找两个节点之间的所有路径
     */
    List<PathResult> findAllPaths(PathQueryParam pathQueryParam);
}
