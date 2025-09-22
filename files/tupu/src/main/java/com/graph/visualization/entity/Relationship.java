package com.graph.visualization.entity;

import com.graph.visualization.enums.RelationshipTypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 关系实体类
 */
@Data
@Entity
@Table(name = "relationships")
@NoArgsConstructor
@AllArgsConstructor
public class Relationship {
    /**
     * 关系ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 源节点ID
     */
    private Long sourceNodeId;
    
    /**
     * 目标节点ID
     */
    private Long targetNodeId;
    
    /**
     * 关系名称
     */
    private String relationshipName;
    
    /**
     * 关系类型
     */
    @Enumerated(EnumType.STRING)
    private RelationshipTypeEnum relationshipType;
    
    /**
     * 关系描述
     */
    private String description;
    
    /**
     * 关系状态（true为活跃，false为非活跃）
     */
    private Boolean status = true;
}
