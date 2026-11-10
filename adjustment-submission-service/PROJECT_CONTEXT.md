# adjustment-submission-service Codex 记录

更新日期：2026-11-09。

## 当前功能

- 已添加带中文注释的 InventoryQueryServiceImplTest（9个用例：成功、6种空/零/负数ID、404/503异常传递）和 InventoryQueryControllerTest（8个用例：200字段、400/404/503错误格式、4种参数转换失败）。Mock Client或Service，不访问数据库/下游，不启动端口；未测试真实HTTP Client行为。
- mvn -DskipTests test-compile通过，按用户要求未运行测试，留给用户IDEA手动运行；根目录可执行 mvn -f adjustment-submission-service/pom.xml test。

- Java21、Spring Boot4.1.2、Maven，端口8081，包com.libo.inventory.submission。
- 用户仅要求独立库存查询；缺损上报本身无需先查询库存。本次未实现申请提交或持久化。
- GET /api/stores/{storeId}/products/{productId}/inventory。
- 调用链：InventoryQueryController → InventoryQueryService → InventoryQueryServiceImpl → InventoryClient → inventory-service HTTP GET /api/stores/{storeId}/products/{productId}。
- 使用RestClient，3秒连接超时、5秒读取超时。application.properties 中 inventory-service.base-url 默认http://localhost:8084，可通过INVENTORY_SERVICE_BASE_URL覆盖。
- 独立InventoryResponse DTO使用Lombok @Data，返回storeId、productId、sku、name、unit、quantityOnHand、reorderPoint，忽略下游其他字段，不依赖inventory服务Java类。
- GlobalExceptionHandler返回ProblemDetail：非法ID400/INVALID_INPUT，不存在404/INVENTORY_NOT_FOUND，下游超时、连接失败、5xx等503/INVENTORY_SERVICE_UNAVAILABLE。不透传下游错误正文。
- Maven package编译成功（跳过测试）。临时运行两服务并连接真实MySQL验证200/404，另验证非法ID400及下游不可用503；临时进程已停止。
- 后续遵循用户的接口/实现分离、Lombok、测试中文注释习惯。用户此前要求自动化测试留给自己手动运行。

## 启动与测试

1. 根目录 docker compose up -d --wait mysql。
2. 启动inventory-service（8084，无需profile）。
3. 启动AdjustmentSubmissionApplication（8081）。
4. Postman GET http://localhost:8081/api/stores/1/products/1/inventory。

继续开发前检查git状态；未经用户要求不提交、推送或合并。
