package com.graph.visualization.service.impl;

import com.graph.visualization.entity.Node;
import com.graph.visualization.enums.NodeTypeEnum;
import com.graph.visualization.repository.NodeRepository;
import com.graph.visualization.service.NodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 节点服务实现类
 */
@Service
@RequiredArgsConstructor
public class NodeServiceImpl implements NodeService {
    
    private final NodeRepository nodeRepository;
    
    @Override
    @Transactional
    public Node createNode(Node node) {
        // 检查IRI是否已存在
        if (nodeRepository.findByIri(node.getIri()).isPresent()) {
            throw new IllegalArgumentException("节点IRI已存在: " + node.getIri());
        }
        return nodeRepository.save(node);
    }
    
    @Override
    public Optional<Node> findNodeById(Long id) {
        return nodeRepository.findById(id);
    }
    
    @Override
    @Transactional
    public Node updateNode(Node node) {
        // 检查节点是否存在
        if (!nodeRepository.existsById(node.getId())) {
            throw new IllegalArgumentException("节点不存在: " + node.getId());
        }
        
        // 如果更改了IRI，检查新IRI是否与其他节点冲突
        nodeRepository.findByIri(node.getIri())
                .ifPresent(existingNode -> {
                    if (!existingNode.getId().equals(node.getId())) {
                        throw new IllegalArgumentException("节点IRI已存在: " + node.getIri());
                    }
                });
        
        return nodeRepository.save(node);
    }
    
    @Override
    @Transactional
    public void deleteNode(Long id) {
        // 检查节点是否存在
        if (!nodeRepository.existsById(id)) {
            throw new IllegalArgumentException("节点不存在: " + id);
        }
        nodeRepository.deleteById(id);
    }
    
    @Override
    public List<Node> findNodesByType(NodeTypeEnum nodeType) {
        return nodeRepository.findByNodeType(nodeType);
    }
    
    @Override
    public List<Node> findNodesByOntology(String ontologyName) {
        return nodeRepository.findByOntologyName(ontologyName);
    }
    
    @Override
    public List<Node> searchNodesByKeyword(String keyword) {
        return nodeRepository.findByKeyword(keyword);
    }
}
