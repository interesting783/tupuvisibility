package com.graph.visualization.controller;

import com.graph.visualization.dto.CentralityResult;
import com.graph.visualization.service.CentralityAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 中心度分析控制器
 */
@RestController
@RequestMapping("/api/v1/graph/centrality")
@RequiredArgsConstructor
public class CentralityAnalysisController {
    
    private final CentralityAnalysisService centralityAnalysisService;
    
    /**
     * 执行默认中心度分析（度中心性）
     */
    @GetMapping
    public ResponseEntity<CentralityResult> analyzeCentrality() {
        CentralityResult result = centralityAnalysisService.analyzeCentrality();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    
    /**
     * 执行度中心性分析
     */
    @GetMapping("/degree")
    public ResponseEntity<CentralityResult> analyzeDegreeCentrality() {
        CentralityResult result = centralityAnalysisService.analyzeDegreeCentrality();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    
    /**
     * 执行接近中心性分析
     */
    @GetMapping("/closeness")
    public ResponseEntity<CentralityResult> analyzeClosenessCentrality() {
        CentralityResult result = centralityAnalysisService.analyzeClosenessCentrality();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    
    /**
     * 执行中介中心性分析
     */
    @GetMapping("/betweenness")
    public ResponseEntity<CentralityResult> analyzeBetweennessCentrality() {
        CentralityResult result = centralityAnalysisService.analyzeBetweennessCentrality();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
