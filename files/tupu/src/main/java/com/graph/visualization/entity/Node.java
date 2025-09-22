package com.graph.visualization.entity;

import com.graph.visualization.enums.NodeTypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 节点实体类
 */
@Data
@Entity
@Table(name = "nodes")
@NoArgsConstructor
@AllArgsConstructor
public class Node {
    /**
     * 节点ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 节点名称
     */
    private String name;
    
    /**
     * 节点描述
     */
    private String description;
    
    /**
     * 唯一标识符IRI
     */
    @Column(unique = true)
    private String iri;
    
    /**
     * 节点类型
     */
    @Enumerated(EnumType.STRING)
    private NodeTypeEnum nodeType;
    
    /**
     * 节点状态（true为活跃，false为非活跃）
     */
    private Boolean status = true;
    
    /**
     * 所属本体名称
     */
    private String ontologyName;
}
