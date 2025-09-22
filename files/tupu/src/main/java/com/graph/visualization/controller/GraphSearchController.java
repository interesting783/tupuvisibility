package com.graph.visualization.controller;

import com.graph.visualization.entity.Node;
import com.graph.visualization.entity.Relationship;
import com.graph.visualization.service.GraphSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 图谱搜索控制器
 */
@RestController
@RequestMapping("/api/v1/graph/search")
@RequiredArgsConstructor
public class GraphSearchController {
    
    private final GraphSearchService graphSearchService;
    
    /**
     * 根据关键词搜索节点和关系
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> searchByKeyword(@RequestParam String keyword) {
        Map<String, Object> result = graphSearchService.searchByKeyword(keyword);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    
    /**
     * 搜索节点
     */
    @GetMapping("/nodes")
    public ResponseEntity<List<Node>> searchNodes(@RequestParam String keyword) {
        List<Node> nodes = graphSearchService.searchNodes(keyword);
        return new ResponseEntity<>(nodes, HttpStatus.OK);
    }
    
    /**
     * 搜索关系
     */
    @GetMapping("/relationships")
    public ResponseEntity<List<Relationship>> searchRelationships(@RequestParam String keyword) {
        List<Relationship> relationships = graphSearchService.searchRelationships(keyword);
        return new ResponseEntity<>(relationships, HttpStatus.OK);
    }
}
