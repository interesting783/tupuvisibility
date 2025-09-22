package com.graph.visualization.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 中心度分析结果DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CentralityResult {
    /**
     * 节点ID到中心度值的映射
     */
    private Map<Long, Double> centralityValues;
    
    /**
     * 最重要的节点ID列表
     */
    private Map<Long, String> importantNodes;
}
