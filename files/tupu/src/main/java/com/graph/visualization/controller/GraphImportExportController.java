package com.graph.visualization.controller;

import com.graph.visualization.dto.ImportResult;
import com.graph.visualization.enums.ExportFormatEnum;
import com.graph.visualization.service.GraphImportExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 图谱导入导出控制器
 */
@RestController
@RequestMapping("/api/v1/graph/io")
@RequiredArgsConstructor
public class GraphImportExportController {
    
    private final GraphImportExportService graphImportExportService;
    
    /**
     * 导入三元组文件
     */
    @PostMapping("/import")
    public ResponseEntity<ImportResult> importTriples(@RequestParam("file") MultipartFile file) {
        try {
            ImportResult result = graphImportExportService.importTriples(file);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 导出图谱数据
     */
    @GetMapping("/export")
    public ResponseEntity<Resource> exportGraph(@RequestParam ExportFormatEnum format) {
        try {
            Resource resource = graphImportExportService.exportGraph(format);
            
            String filename = "graph_export." + (format == ExportFormatEnum.JSON ? "json" : "rdf");
            String contentType = format == ExportFormatEnum.JSON ? 
                    MediaType.APPLICATION_JSON_VALUE : "application/rdf+xml";
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(resource);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
