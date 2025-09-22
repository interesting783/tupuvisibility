package com.graph.visualization.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 导入结果DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportResult {
    /**
     * 导入的节点数量
     */
    private Integer nodeCount;
    
    /**
     * 导入的关系数量
     */
    private Integer relationshipCount;
}
