package com.graph.visualization.controller;

import com.graph.visualization.dto.PathQueryParam;
import com.graph.visualization.dto.PathResult;
import com.graph.visualization.service.PathAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 路径分析控制器
 */
@RestController
@RequestMapping("/api/v1/graph/path")
@RequiredArgsConstructor
public class PathAnalysisController {
    
    private final PathAnalysisService pathAnalysisService;
    
    /**
     * 查找最短路径
     */
    @PostMapping("/shortest")
    public ResponseEntity<PathResult> findShortestPath(@RequestBody PathQueryParam pathQueryParam) {
        PathResult result = pathAnalysisService.findShortestPath(pathQueryParam);
        if (result == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    
    /**
     * 查找所有路径
     */
    @PostMapping("/all")
    public ResponseEntity<List<PathResult>> findAllPaths(@RequestBody PathQueryParam pathQueryParam) {
        List<PathResult> results = pathAnalysisService.findAllPaths(pathQueryParam);
        return new ResponseEntity<>(results, HttpStatus.OK);
    }
}
