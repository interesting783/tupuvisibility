package com.graph.visualization.controller;

import com.graph.visualization.dto.PatternMatchParam;
import com.graph.visualization.service.PatternMatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 模式匹配控制器
 */
@RestController
@RequestMapping("/api/v1/graph/pattern")
@RequiredArgsConstructor
public class PatternMatchController {
    
    private final PatternMatchService patternMatchService;
    
    /**
     * 执行模式匹配
     */
    @PostMapping("/match")
    public ResponseEntity<List<Map<String, Object>>> findPatternMatches(@RequestBody PatternMatchParam patternMatchParam) {
        List<Map<String, Object>> matches = patternMatchService.findPatternMatches(patternMatchParam);
        return new ResponseEntity<>(matches, HttpStatus.OK);
    }
}
