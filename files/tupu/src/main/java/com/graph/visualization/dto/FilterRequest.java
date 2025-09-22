package com.graph.visualization.dto;

import com.graph.visualization.enums.LogicalOperatorEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 过滤请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterRequest {
    /**
     * 过滤条件列表
     */
    private List<FilterCondition> conditions;
    
    /**
     * 逻辑操作符（AND/OR）
     */
    private LogicalOperatorEnum logicalOperator;
}
