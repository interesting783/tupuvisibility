package com.graph.visualization.repository;

import com.graph.visualization.entity.Node;
import com.graph.visualization.enums.NodeTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 节点数据访问接口
 */
@Repository
public interface NodeRepository extends JpaRepository<Node, Long> {
    /**
     * 根据IRI查找节点
     */
    Optional<Node> findByIri(String iri);
    
    /**
     * 根据节点类型查找节点
     */
    List<Node> findByNodeType(NodeTypeEnum nodeType);
    
    /**
     * 根据本体名称查找节点
     */
    List<Node> findByOntologyName(String ontologyName);
    
    /**
     * 根据名称或描述模糊查询节点
     */
    @Query("SELECT n FROM Node n WHERE n.name LIKE %:keyword% OR n.description LIKE %:keyword%")
    List<Node> findByKeyword(@Param("keyword") String keyword);
}
