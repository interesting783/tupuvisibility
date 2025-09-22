package com.graph.visualization.controller;

import com.graph.visualization.dto.FilterRequest;
import com.graph.visualization.entity.Node;
import com.graph.visualization.entity.Relationship;
import com.graph.visualization.service.FilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 过滤控制器
 */
@RestController
@RequestMapping("/api/v1/graph/filter")
@RequiredArgsConstructor
public class FilterController {
    
    private final FilterService filterService;
    
    /**
     * 过滤节点
     */
    @PostMapping("/nodes")
    public ResponseEntity<List<Node>> filterNodes(@RequestBody FilterRequest filterRequest) {
        List<Node> filteredNodes = filterService.filterNodes(filterRequest);
        return new ResponseEntity<>(filteredNodes, HttpStatus.OK);
    }
    
    /**
     * 过滤关系
     */
    @PostMapping("/relationships")
    public ResponseEntity<List<Relationship>> filterRelationships(@RequestBody FilterRequest filterRequest) {
        List<Relationship> filteredRelationships = filterService.filterRelationships(filterRequest);
        return new ResponseEntity<>(filteredRelationships, HttpStatus.OK);
    }
}
