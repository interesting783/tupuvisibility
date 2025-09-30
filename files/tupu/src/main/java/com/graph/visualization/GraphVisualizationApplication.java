package com.graph.visualization;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "图谱可视化编辑API",
        version = "1.0",
        description = "图谱可视化编辑系统的REST API接口文档"
    )
)
public class GraphVisualizationApplication {
    public static void main(String[] args) {
        SpringApplication.run(GraphVisualizationApplication.class, args);
    }
}