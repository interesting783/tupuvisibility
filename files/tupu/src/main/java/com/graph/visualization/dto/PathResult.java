package com.graph.visualization.dto;

import com.graph.visualization.entity.Node;
import com.graph.visualization.entity.Relationship;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 路径结果DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PathResult {
    /**
     * 路径长度
     */
    private Integer pathLength;
    
    /**
     * 路径中的节点列表
     */
    private List<Node> nodes;
    
    /**
     * 路径中的关系列表
     */
    private List<Relationship> relationships;
}
