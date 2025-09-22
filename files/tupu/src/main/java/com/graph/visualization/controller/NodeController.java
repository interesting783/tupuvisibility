package com.graph.visualization.controller;

import com.graph.visualization.entity.Node;
import com.graph.visualization.enums.NodeTypeEnum;
import com.graph.visualization.service.NodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 节点管理控制器
 */
@RestController
@RequestMapping("/api/v1/graph/nodes")
@RequiredArgsConstructor
@Tag(name = "节点管理", description = "节点的创建、查询、更新和删除操作")
public class NodeController {
    
    private final NodeService nodeService;
    
    /**
     * 创建节点
     */
    @Operation(summary = "创建节点", description = "创建一个新的节点实体")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "节点创建成功", 
                     content = @Content(schema = @Schema(implementation = Node.class))),
        @ApiResponse(responseCode = "400", description = "请求参数无效")
    })
    @PostMapping
    public ResponseEntity<Node> createNode(@RequestBody Node node) {
        Node createdNode = nodeService.createNode(node);
        return new ResponseEntity<>(createdNode, HttpStatus.CREATED);
    }
    
    /**
     * 根据ID查询节点
     */
    @Operation(summary = "根据ID查询节点", description = "通过节点ID查询节点详细信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取节点信息",
                     content = @Content(schema = @Schema(implementation = Node.class))),
        @ApiResponse(responseCode = "404", description = "节点不存在")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Node> getNodeById(@Parameter(description = "节点ID") @PathVariable Long id) {
        return nodeService.findNodeById(id)
                .map(node -> new ResponseEntity<>(node, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    /**
     * 更新节点
     */
    @Operation(summary = "更新节点", description = "更新指定ID的节点信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "节点更新成功",
                     content = @Content(schema = @Schema(implementation = Node.class))),
        @ApiResponse(responseCode = "400", description = "请求参数无效"),
        @ApiResponse(responseCode = "404", description = "节点不存在")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Node> updateNode(@Parameter(description = "节点ID") @PathVariable Long id, @RequestBody Node node) {
        if (!id.equals(node.getId())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Node updatedNode = nodeService.updateNode(node);
        return new ResponseEntity<>(updatedNode, HttpStatus.OK);
    }
    
    /**
     * 删除节点
     */
    @Operation(summary = "删除节点", description = "删除指定ID的节点")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "节点删除成功"),
        @ApiResponse(responseCode = "404", description = "节点不存在")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNode(@Parameter(description = "节点ID") @PathVariable Long id) {
        nodeService.deleteNode(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    /**
     * 根据节点类型查询节点
     */
    @Operation(summary = "根据节点类型查询节点", description = "查询指定类型的所有节点")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取节点列表")
    })
    @GetMapping("/type/{nodeType}")
    public ResponseEntity<List<Node>> getNodesByType(@Parameter(description = "节点类型") @PathVariable NodeTypeEnum nodeType) {
        List<Node> nodes = nodeService.findNodesByType(nodeType);
        return new ResponseEntity<>(nodes, HttpStatus.OK);
    }
    
    /**
     * 根据本体名称查询节点
     */
    @Operation(summary = "根据本体名称查询节点", description = "查询指定本体下的所有节点")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取节点列表")
    })
    @GetMapping("/ontology/{ontologyName}")
    public ResponseEntity<List<Node>> getNodesByOntology(@Parameter(description = "本体名称") @PathVariable String ontologyName) {
        List<Node> nodes = nodeService.findNodesByOntology(ontologyName);
        return new ResponseEntity<>(nodes, HttpStatus.OK);
    }
    
    /**
     * 根据关键词搜索节点
     */
    @Operation(summary = "根据关键词搜索节点", description = "搜索名称或描述中包含关键词的节点")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取搜索结果")
    })
    @GetMapping("/search")
    public ResponseEntity<List<Node>> searchNodes(@Parameter(description = "搜索关键词") @RequestParam String keyword) {
        List<Node> nodes = nodeService.searchNodesByKeyword(keyword);
        return new ResponseEntity<>(nodes, HttpStatus.OK);
    }
}
