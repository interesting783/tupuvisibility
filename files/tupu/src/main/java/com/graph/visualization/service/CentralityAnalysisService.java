package com.graph.visualization.service;

import com.graph.visualization.dto.CentralityResult;

/**
 * 中心度分析服务接口
 */
public interface CentralityAnalysisService {
    /**
     * 执行中心度分析
     */
    CentralityResult analyzeCentrality();
    
    /**
     * 获取度中心性（Degree Centrality）
     */
    CentralityResult analyzeDegreeCentrality();
    
    /**
     * 获取接近中心性（Closeness Centrality）
     */
    CentralityResult analyzeClosenessCentrality();
    
    /**
     * 获取中介中心性（Betweenness Centrality）
     */
    CentralityResult analyzeBetweennessCentrality();
}
