-- 创建数据库
CREATE DATABASE IF NOT EXISTS graph_visualization DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE graph_visualization;

-- 创建节点表
CREATE TABLE IF NOT EXISTS nodes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    iri VARCHAR(255) UNIQUE NOT NULL,
    node_type VARCHAR(20) NOT NULL,
    status BOOLEAN DEFAULT TRUE,
    ontology_name VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建关系表
CREATE TABLE IF NOT EXISTS relationships (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    source_node_id BIGINT NOT NULL,
    target_node_id BIGINT NOT NULL,
    relationship_name VARCHAR(255) NOT NULL,
    relationship_type VARCHAR(20) NOT NULL,
    description TEXT,
    status BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (source_node_id) REFERENCES nodes(id) ON DELETE CASCADE,
    FOREIGN KEY (target_node_id) REFERENCES nodes(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建索引
CREATE INDEX idx_nodes_name ON nodes(name);
CREATE INDEX idx_nodes_iri ON nodes(iri);
CREATE INDEX idx_nodes_type ON nodes(node_type);
CREATE INDEX idx_nodes_ontology ON nodes(ontology_name);

CREATE INDEX idx_relationships_source ON relationships(source_node_id);
CREATE INDEX idx_relationships_target ON relationships(target_node_id);
CREATE INDEX idx_relationships_name ON relationships(relationship_name);
CREATE INDEX idx_relationships_type ON relationships(relationship_type);

-- 添加注释
ALTER TABLE nodes COMMENT '存储图谱中的节点数据，包括类节点和实体节点';
ALTER TABLE relationships COMMENT '存储图谱中的关系数据，包括类属性关系和对象属性关系';
