package com.graph.visualization.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 模式匹配参数DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatternMatchParam {
    /**
     * 选中的节点ID列表
     */
    private List<Long> selectedNodeIds;
    
    /**
     * 选中的关系ID列表
     */
    private List<Long> selectedRelationships;
}
