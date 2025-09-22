package com.graph.visualization.dto;

import com.graph.visualization.enums.FilterOperatorEnum;
import com.graph.visualization.enums.PropertyTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 过滤条件DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterCondition {
    /**
     * 属性ID
     */
    private String propertyId;
    
    /**
     * 操作符
     */
    private FilterOperatorEnum operator;
    
    /**
     * 过滤值
     */
    private String value;
    
    /**
     * 值类型（数据属性/对象属性）
     */
    private PropertyTypeEnum valueType;
}
