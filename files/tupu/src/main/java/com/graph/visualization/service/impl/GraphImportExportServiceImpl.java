package com.graph.visualization.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.graph.visualization.dto.ImportResult;
import com.graph.visualization.entity.Node;
import com.graph.visualization.entity.Relationship;
import com.graph.visualization.enums.ExportFormatEnum;
import com.graph.visualization.enums.NodeTypeEnum;
import com.graph.visualization.enums.RelationshipTypeEnum;
import com.graph.visualization.repository.NodeRepository;
import com.graph.visualization.repository.RelationshipRepository;
import com.graph.visualization.service.GraphImportExportService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 图谱导入导出服务实现类
 */
@Service
@RequiredArgsConstructor
public class GraphImportExportServiceImpl implements GraphImportExportService {
    
    private final NodeRepository nodeRepository;
    private final RelationshipRepository relationshipRepository;
    private final ObjectMapper objectMapper;
    
    @Value("${graph.export.path}")
    private String exportPath;
    
    @Override
    @Transactional
    public ImportResult importTriples(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("上传的文件为空");
        }
        
        // 读取文件内容
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line.trim());
                }
            }
        }
        
        // 解析三元组并导入到数据库
        int nodeCount = 0;
        int relationshipCount = 0;
        
        Map<String, Long> nodeIriToIdMap = new HashMap<>();
        
        // 第一遍扫描：创建所有节点
        for (String line : lines) {
            String[] parts = line.split("\\s+", 3);
            if (parts.length < 3) {
                continue; // 跳过格式不正确的行
            }
            
            String subjectIri = parts[0];
            String objectIri = parts[2];
            
            // 处理主语节点
            if (!nodeIriToIdMap.containsKey(subjectIri)) {
                Node subjectNode = new Node();
                subjectNode.setIri(subjectIri);
                subjectNode.setName(extractNameFromIri(subjectIri));
                subjectNode.setNodeType(NodeTypeEnum.INDIVIDUAL);
                subjectNode.setStatus(true);
                
                Node savedSubject = nodeRepository.save(subjectNode);
                nodeIriToIdMap.put(subjectIri, savedSubject.getId());
                nodeCount++;
            }
            
            // 处理宾语节点
            if (!nodeIriToIdMap.containsKey(objectIri)) {
                Node objectNode = new Node();
                objectNode.setIri(objectIri);
                objectNode.setName(extractNameFromIri(objectIri));
                objectNode.setNodeType(NodeTypeEnum.INDIVIDUAL);
                objectNode.setStatus(true);
                
                Node savedObject = nodeRepository.save(objectNode);
                nodeIriToIdMap.put(objectIri, savedObject.getId());
                nodeCount++;
            }
        }
        
        // 第二遍扫描：创建所有关系
        for (String line : lines) {
            String[] parts = line.split("\\s+", 3);
            if (parts.length < 3) {
                continue; // 跳过格式不正确的行
            }
            
            String subjectIri = parts[0];
            String predicateIri = parts[1];
            String objectIri = parts[2];
            
            Long sourceNodeId = nodeIriToIdMap.get(subjectIri);
            Long targetNodeId = nodeIriToIdMap.get(objectIri);
            
            if (sourceNodeId != null && targetNodeId != null) {
                Relationship relationship = new Relationship();
                relationship.setSourceNodeId(sourceNodeId);
                relationship.setTargetNodeId(targetNodeId);
                relationship.setRelationshipName(extractNameFromIri(predicateIri));
                relationship.setRelationshipType(RelationshipTypeEnum.OBJECT_RELATION);
                relationship.setDescription(predicateIri);
                relationship.setStatus(true);
                
                relationshipRepository.save(relationship);
                relationshipCount++;
            }
        }
        
        return new ImportResult(nodeCount, relationshipCount);
    }
    
    @Override
    public Resource exportGraph(ExportFormatEnum format) throws IOException {
        // 确保导出目录存在
        File exportDir = new File(exportPath);
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        
        // 生成导出文件名
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = "graph_export_" + timestamp + (format == ExportFormatEnum.JSON ? ".json" : ".rdf");
        Path filePath = Paths.get(exportPath, fileName);
        
        // 导出数据
        if (format == ExportFormatEnum.JSON) {
            exportAsJson(filePath);
        } else {
            exportAsRdf(filePath);
        }
        
        // 返回导出的文件资源
        return new FileSystemResource(filePath.toFile());
    }
    
    /**
     * 将图谱导出为JSON格式
     */
    private void exportAsJson(Path filePath) throws IOException {
        List<Node> nodes = nodeRepository.findAll();
        List<Relationship> relationships = relationshipRepository.findAll();
        
        Map<String, Object> graphData = new HashMap<>();
        graphData.put("nodes", nodes);
        graphData.put("relationships", relationships);
        
        String jsonContent = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(graphData);
        Files.write(filePath, jsonContent.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * 将图谱导出为RDF格式
     */
    private void exportAsRdf(Path filePath) throws IOException {
        List<Node> nodes = nodeRepository.findAll();
        List<Relationship> relationships = relationshipRepository.findAll();
        
        Map<Long, Node> nodeMap = new HashMap<>();
        for (Node node : nodes) {
            nodeMap.put(node.getId(), node);
        }
        
        StringBuilder rdfContent = new StringBuilder();
        rdfContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        rdfContent.append("<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n");
        
        // 导出节点
        for (Node node : nodes) {
            rdfContent.append("  <rdf:Description rdf:about=\"").append(node.getIri()).append("\">\n");
            rdfContent.append("    <rdf:type>").append(node.getNodeType()).append("</rdf:type>\n");
            rdfContent.append("    <rdfs:label>").append(node.getName()).append("</rdfs:label>\n");
            if (node.getDescription() != null && !node.getDescription().isEmpty()) {
                rdfContent.append("    <rdfs:comment>").append(node.getDescription()).append("</rdfs:comment>\n");
            }
            rdfContent.append("  </rdf:Description>\n");
        }
        
        // 导出关系
        for (Relationship relationship : relationships) {
            Node sourceNode = nodeMap.get(relationship.getSourceNodeId());
            Node targetNode = nodeMap.get(relationship.getTargetNodeId());
            
            if (sourceNode != null && targetNode != null) {
                rdfContent.append("  <rdf:Description rdf:about=\"").append(sourceNode.getIri()).append("\">\n");
                rdfContent.append("    <").append(relationship.getRelationshipName()).append(" rdf:resource=\"")
                        .append(targetNode.getIri()).append("\"/>\n");
                rdfContent.append("  </rdf:Description>\n");
            }
        }
        
        rdfContent.append("</rdf:RDF>");
        
        Files.write(filePath, rdfContent.toString().getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * 从IRI中提取名称
     */
    private String extractNameFromIri(String iri) {
        // 简单实现：取IRI最后一部分作为名称
        int lastSlashIndex = iri.lastIndexOf('/');
        int lastHashIndex = iri.lastIndexOf('#');
        
        int lastSeparatorIndex = Math.max(lastSlashIndex, lastHashIndex);
        
        if (lastSeparatorIndex >= 0 && lastSeparatorIndex < iri.length() - 1) {
            return iri.substring(lastSeparatorIndex + 1);
        } else {
            return iri;
        }
    }
}
