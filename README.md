#一共三个文件
##1.“tupu”是整个后端springboot项目，里面有readme文档包含api，使用说明，以及测试说明
##2.“mile.txt”是关于postman测试三元组的文件，请注意如果postman测试import无法传键（file）值（file）的时候，你有两种方法解决
###（1）临时将你的.txt文件重命名为.jpg或其他允许的格式，上传后再在服务器端处理
（2）急需测试，可以将三元组数据直接通过API请求体发送，而不是作为文件上传。你可以修改后端代码，添加一个新的API端点，接受JSON格式的三元组数据
```java
@PostMapping("/import/json")
public ResponseEntity<ImportResult> importTriplesFromJson(@RequestBody List<String> triples) {
    // 处理三元组数据
    // ...
    return new ResponseEntity<>(result, HttpStatus.OK);
}
```
```json
[
  "http://military.com/equipment/tank99 http://military.com/relation/type 军事装备",
  "http://military.com/equipment/tank99 http://military.com/relation/name 99式坦克",
  "http://military.com/equipment/tank99 http://military.com/relation/belongsTo 陆军装备"
]
```
当然可能有别的方法，自己试试吧
##3.不用解释了
