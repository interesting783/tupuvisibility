package com.graph.visualization.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 路径查询参数DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PathQueryParam {
    /**
     * 源节点ID
     */
    private Long sourceNodeId;
    
    /**
     * 目标节点ID
     */
    private Long targetNodeId;
    
    /**
     * 最大深度
     */
    private Integer maxDepth;
    
    /**
     * 最大路径数（仅用于全通路径查询）
     */
    private Integer maxPathCount;
}
