# 图谱可视化编辑后端

本项目是一个基于Spring Boot 3.x的图谱可视化编辑后端系统，提供完整的图谱管理、分析和可视化功能。

## 技术栈

- Spring Boot 3.x
- Spring Web (REST接口)
- Spring Data JPA (数据访问)
- MySQL (数据库)
- Lombok (简化代码)
- Commons-IO (处理导入导出文件)

## 功能特性

### 实体管理
- 添加实体（选择"类/实体"类型，填写名称、描述、IRI）
- 查看实体详情（按ID查询）
- 编辑实体属性
- 删除实体

### 关系管理
- 添加关系（选择源节点、目标节点，指定"类属性关系/对象属性关系"类型及关系名称）
- 查看关系详情（按ID查询）
- 编辑关系
- 删除关系

### 节点/边详情操作
- 点击节点/边可查看完整属性
- 支持在线编辑并保存更新

### 属性过滤
- 支持多过滤条件（选属性ID、操作符、填过滤值、选属性类型）
- 支持8种操作符：equals/contains/startsWith/endsWith/gt/lt/gte/lte
- 支持"且AND/或OR"逻辑组合多条件

### 路径分析
- 最短路径分析（选源/目标节点，设最大深度，返回路径长度+节点序列）
- 全通路径分析（选源/目标节点，设最大深度、最大路径数，返回所有路径列表）

### 中心度分析
- 执行分析后，返回并标记图谱中重要节点及中心度数值
- 支持度中心性、接近中心性和中介中心性分析

### 模式匹配
- 支持选择多个节点、节点间关系，搜索符合该模式的所有实例并高亮

### 图谱搜索
- 按关键词查找匹配的节点/关系，返回并高亮结果

### 图谱导入导出
- 支持导入三元组文件，解析后合并到当前图谱
- 支持导出JSON（图谱数据）、RDF（本体数据）格式文件

## API接口

所有接口路径统一前缀：`/api/v1/graph`

### 节点管理
- `POST /nodes` - 创建节点
- `GET /nodes/{id}` - 根据ID查询节点
- `PUT /nodes/{id}` - 更新节点
- `DELETE /nodes/{id}` - 删除节点
- `GET /nodes/type/{nodeType}` - 根据节点类型查询节点
- `GET /nodes/ontology/{ontologyName}` - 根据本体名称查询节点
- `GET /nodes/search?keyword={keyword}` - 根据关键词搜索节点

### 关系管理
- `POST /relationships` - 创建关系
- `GET /relationships/{id}` - 根据ID查询关系
- `PUT /relationships/{id}` - 更新关系
- `DELETE /relationships/{id}` - 删除关系
- `GET /relationships/source/{sourceNodeId}` - 根据源节点ID查询关系
- `GET /relationships/target/{targetNodeId}` - 根据目标节点ID查询关系
- `GET /relationships/between?sourceNodeId={sourceNodeId}&targetNodeId={targetNodeId}` - 根据源节点ID和目标节点ID查询关系
- `GET /relationships/type/{relationshipType}` - 根据关系类型查询关系
- `GET /relationships/search?keyword={keyword}` - 根据关键词搜索关系
- `GET /relationships/node/{nodeId}` - 查找与指定节点相关的所有关系

### 属性过滤
- `POST /filter/nodes` - 过滤节点
- `POST /filter/relationships` - 过滤关系

### 路径分析
- `POST /path/shortest` - 查找最短路径
- `POST /path/all` - 查找所有路径

### 中心度分析
- `GET /centrality` - 执行默认中心度分析（度中心性）
- `GET /centrality/degree` - 执行度中心性分析
- `GET /centrality/closeness` - 执行接近中心性分析
- `GET /centrality/betweenness` - 执行中介中心性分析

### 模式匹配
- `POST /pattern/match` - 执行模式匹配

### 图谱搜索
- `GET /search?keyword={keyword}` - 根据关键词搜索节点和关系
- `GET /search/nodes?keyword={keyword}` - 搜索节点
- `GET /search/relationships?keyword={keyword}` - 搜索关系

### 图谱导入导出
- `POST /io/import` - 导入三元组文件
- `GET /io/export?format={format}` - 导出图谱数据

## 配置说明

主要配置项（application.yml）：
```yaml
spring:
  datasource:
    url: jdbc:mysql://${MYSQL_HOST:localhost}:${MYSQL_PORT:3306}/${MYSQL_DATABASE:graph_visualization}?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: ${MYSQL_USERNAME:root}
    password: ${MYSQL_PASSWORD:password} //这是环境变量，去掉${}为硬编码
  jpa:
    hibernate:
      ddl-auto: update
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB

server:
  port: 8080

graph:
  export:
    path: ${EXPORT_PATH:./export-files} //你随便划个d盘路径就好，如果没有会自己创建的
```

## 运行说明

1. 确保已安装JDK 17或更高版本
2. 配置MySQL数据库
3. 修改application.yml中的数据库连接信息
4. 运行应用：`./mvnw spring-boot:run`或使用IDE直接运行
5. 访问API：`http://localhost:8080/api/v1/graph/...`

## API文档

本项目使用Swagger/OpenAPI自动生成API文档。启动应用后，可以通过以下方式访问API文档：

1. Swagger UI界面：http://localhost:8080/swagger-ui.html
2. OpenAPI JSON格式：http://localhost:8080/api-docs
3. OpenAPI YAML格式：http://localhost:8080/api-docs.yaml

通过Swagger UI界面，您可以：
- 浏览所有可用的API端点
- 查看请求和响应的详细说明
- 直接在界面上测试API
- 下载API文档用于离线查看或集成到其他工具

## Postman测试指南

以下是使用Postman测试各个API的示例请求和预期响应：

### 1. 节点管理

#### 创建节点
- **请求**：POST http://localhost:8080/api/v1/graph/nodes
- **请求体**：
```json
{
  "name": "军事装备",
  "description": "各类军事装备的总称",
  "iri": "http://military.com/equipment",
  "nodeType": "CLASS",
  "ontologyName": "军事后勤本体"
}
```
- **预期响应**：状态码201，返回创建的节点信息，包含自动生成的ID

#### 根据ID查询节点
- **请求**：GET http://localhost:8080/api/v1/graph/nodes/1
- **预期响应**：状态码200，返回节点详细信息
```json
{
  "id": 1,
  "name": "军事装备",
  "description": "各类军事装备的总称",
  "iri": "http://military.com/equipment",
  "nodeType": "CLASS",
  "status": true,
  "ontologyName": "军事后勤本体"
}
```

### 2. 关系管理

#### 创建关系
- **请求**：POST http://localhost:8080/api/v1/graph/relationships
- **请求体**：
```json
{
  "sourceNodeId": 1,
  "targetNodeId": 2,
  "relationshipName": "包含",
  "relationshipType": "CLASS_RELATION",
  "description": "表示一个类包含另一个类"
}
```
- **预期响应**：状态码201，返回创建的关系信息

### 3. 属性过滤

#### 过滤节点
- **请求**：POST http://localhost:8080/api/v1/graph/filter/nodes
- **请求体**：
```json
{
  "conditions": [
    {
      "propertyId": "name",
      "operator": "CONTAINS",
      "value": "军事",
      "valueType": "DATA"
    },
    {
      "propertyId": "ontologyName",
      "operator": "EQUALS",
      "value": "军事后勤本体",
      "valueType": "DATA"
    }
  ],
  "logicalOperator": "AND"
}
```
- **预期响应**：状态码200，返回符合条件的节点列表

### 4. 路径分析

#### 最短路径分析
- **请求**：POST http://localhost:8080/api/v1/graph/path/shortest
- **请求体**：
```json
{
  "sourceNodeId": 1,
  "targetNodeId": 5,
  "maxDepth": 5
}
```
- **预期响应**：状态码200，返回最短路径信息

#### 全通路径分析
- **请求**：POST http://localhost:8080/api/v1/graph/path/all
- **请求体**：
```json
{
  "sourceNodeId": 1,
  "targetNodeId": 5,
  "maxDepth": 5,
  "maxPathCount": 10
}
```
- **预期响应**：状态码200，返回所有路径列表

### 5. 中心度分析

#### 执行度中心性分析
- **请求**：GET http://localhost:8080/api/v1/graph/centrality/degree
- **预期响应**：状态码200，返回节点中心度值和重要节点

### 6. 模式匹配

#### 执行模式匹配
- **请求**：POST http://localhost:8080/api/v1/graph/pattern/match
- **请求体**：
```json
{
  "selectedNodeIds": [1, 2, 3],
  "selectedRelationships": [1, 2]
}
```
- **预期响应**：状态码200，返回匹配的子图实例列表

### 7. 图谱搜索

#### 关键词搜索
- **请求**：GET http://localhost:8080/api/v1/graph/search?keyword=军事
- **预期响应**：状态码200，返回匹配的节点和关系

### 8. 图谱导入导出

#### 导入图谱
- **请求**：POST http://localhost:8080/api/v1/graph/io/import
- **请求类型**：form-data，文件字段名为"file"
- **上传文件**：包含三元组的文本文件
- **预期响应**：状态码200，返回导入结果

#### 导出图谱
- **请求**：GET http://localhost:8080/api/v1/graph/io/export?format=JSON
- **预期响应**：状态码200，返回JSON格式的图谱数据文件

### 测试步骤建议
1. 先创建几个节点
2. 然后创建节点之间的关系
3. 测试查询和过滤功能
4. 测试路径分析和中心度分析
5. 最后测试模式匹配和导入导出功能

## 项目结构

```
src/main/java/com/graph/visualization/
├── config/               # 配置类
├── controller/           # 控制器
├── dto/                  # 数据传输对象
├── entity/               # 实体类
├── enums/                # 枚举类
├── repository/           # 数据访问接口
├── service/              # 服务接口
│   └── impl/             # 服务实现类
└── GraphVisualizationApplication.java  # 应用入口
```
