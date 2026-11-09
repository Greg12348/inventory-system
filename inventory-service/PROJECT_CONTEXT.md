# inventory-service Codex 记录

更新日期：2026-11-09。本文集中保存本服务的 Codex 开发上下文，不是逐字对话导出。

## 当前有效状态

- Java 21、Spring Boot 4.1.2、Maven，包名 com.libo.inventory，端口8084。
- 已创建 controller、service/impl、repository、entity、dto、exception、config；Service 接口和实现分开，实体使用 Lombok。
- 数据访问为 Spring Data JPA；InventoryRepository 通过方法名按门店和商品查询，EntityGraph 加载 Product，Service 转为 StoreProductResponse。
- 接口：GET /api/stores/{storeId}/products/{productId}。正常200；GlobalExceptionHandler统一处理404/INVENTORY_NOT_FOUND、400/INVALID_INPUT，使用ProblemDetail。非法输入包括非数字、Long溢出、零和负数ID。
- 默认连接MySQL，不使用 @Profile。application.properties 导入 ../.env 和 ./.env；从项目根目录或服务目录启动均可。IDEA无需Active profiles，启动前先启动MySQL。ddl-auto=validate，不自动修改表。
- 根目录共享文件：compose.yaml、.env（Git忽略，不记录密码）、MYSQL_SETUP.md、sql/seed-inventory.sql。保持原路径。
- MySQL已有3个门店、10个商品、30条库存；用户要求统一库存20、阈值5。种子脚本保留原样例，但重复运行不覆盖现有数量；数据库实时值需查询确认。
- ProductServiceImplTest 与 ProductControllerTest 已添加中文注释，使用Mockito/MockMvc，无需数据库。用户希望自行手动运行测试，未经新要求不要代跑。
- 编译及真实MySQL API曾验证成功；用户已反馈单元测试及get-by-id测试完成。最新异常处理版本已通过test-compile，未代跑更新后的测试。
- 整理时当前分支feature/inventory-service，HEAD为bdb8523（feat: add store product inventory lookup and error handling）。未fetch，远程实时状态未核实；后续以git status/log为准。

## 后续协作

继续前先阅读本文和根目录PROJECT_CONTEXT.md，检查Git状态。后续本服务上下文更新到本文；未经要求不自动提交、推送或合并。

## 历史记录（以下包含已失效的旧方案）

以下保留迁移前的服务开发历史。其中profile、JDBC、无业务接口、无.env等描述属于早期阶段，以本文上方当前状态和实际代码为准。

## 当前进度

### 后续变更（2026-11-09）

- 按用户仅处理两类错误的要求新增 GlobalExceptionHandler（@RestControllerAdvice）、InventoryNotFoundException、InvalidInputException。统一使用 ProblemDetail，含 status/title/detail/instance 和 code：库存记录不存在404/INVENTORY_NOT_FOUND，非法输入400/INVALID_INPUT（非数字、Long溢出、ID<=0）。不添加500兜底处理。
- ProductController 检查正数ID，Service仍返回Optional；Controller遇空结果抛库存不存在异常，由Advice格式化404，成功返回200。Service/Repository契约不变。
- 更新 ProductControllerTest 注册Advice并核对统一错误JSON，覆盖正常200、404及8种非法ID；mvn -DskipTests test-compile通过，按用户自行运行要求未执行测试。

- 按用户要求移除 Controller、ServiceImpl、InventoryRepository 上的 @Profile("mysql")；将全部数据库/JPA/.env 配置合并到 application.properties，删除 application-mysql.properties 和数据源自动配置排除项。现在直接启动即连接 MySQL，IDEA 无需设置 Active profiles；MySQL 必须先启动。
- MYSQL_SETUP.md 及测试注释已同步更新。mvn -DskipTests clean package 成功（含测试编译，未运行测试）。
- 无 active profile 启动成功并连接真实 MySQL；临时端口18084的门店1商品1查询返回200及库存20/阈值5，验证后停止临时服务。

- 新增带中文注释的 ProductControllerTest，使用 standalone MockMvc + Mockito，覆盖 200/完整 JSON、404/空响应体、非数字门店与商品 ID 返回400且不调用Service（共4个测试用例）。不启动Spring/数据库/端口，不测试profile配置。沿用用户自行运行的要求，未执行；IDEA运行类或 mvn -f inventory-service/pom.xml -Dtest=ProductControllerTest test。

- 新增 ProductServiceImplTest（JUnit Jupiter + Mockito），中文注释说明 Arrange/Act/Assert、Mock、依赖注入；覆盖查到记录的全部 DTO 字段映射及查不到返回空，并核对门店/商品查询参数。无需 Spring、Docker 或 MySQL。用户要求自行手动运行，本次未运行测试；可运行 mvn -f inventory-service/pom.xml -Dtest=ProductServiceImplTest test。

- 已按用户要求从 JDBC 迁移至 Spring Data JPA：starter-data-jpa 替换 starter-jdbc，Product 增加 JPA 映射，新增 Inventory 实体（storeId 标量、Product 多对一关联、@Version 和数据库维护的 updatedAt）。无需 Store 实体。
- 删除手写 SQL 的 ProductRepository，新增 InventoryRepository extends JpaRepository，通过 findByStoreIdAndProduct_Id 派生查询，@EntityGraph 一并获取 product。ProductServiceImpl 使用只读事务，将实体转换为原 StoreProductResponse。
- mysql profile 设置 ddl-auto=validate、open-in-view=false；仅校验现有表，不自动建表或修改数据。API 路径与响应不变。
- clean verify 通过；真实 MySQL 映射校验成功，门店 1/2 查询返回 200（库存20、阈值5），不存在记录返回404，非数字ID返回400；临时验证进程已停止。

- 已实现 GET /api/stores/{storeId}/products/{productId}：Controller → ProductService → ProductServiceImpl → ProductRepository（JdbcTemplate 参数化联查 products/inventory）。返回 StoreProductResponse DTO，包含商品资料、storeId、quantityOnHand、reorderPoint；找不到门店商品库存记录时返回 404。旧的单商品 ID 路径已替换。
- Controller、Impl、Repository 使用 @Profile("mysql")，保留默认不启用数据库的启动方式。暂不创建 Store 类，因为此接口只需 storeId，未读取门店资料。
- mvn verify 通过；临时启动服务在 18084 端口连接真实 MySQL，门店 1/2 的商品 1 均返回 200 和库存 20/阈值 5，不存在的门店或商品返回 404。验证后已停止临时进程。

- 用户最终选择 Product 仅使用 Lombok @Data，替换此前四个注解；自动生成 getter/setter、equals/hashCode、toString 及当前字段定义下的无参构造，不再提供全参构造。

- 按用户要求在 inventory-service 添加 Lombok optional 依赖（版本由 Spring Boot 管理），Product 使用 @Getter、@Setter、@NoArgsConstructor、@AllArgsConstructor 替代手写方法；不添加 JPA 注解或 Service 实现。

- 按用户要求将 Product 从 record 改为普通 Java 类，保留五个字段并添加无参/全参构造及 getter/setter；ProductService 接口不变，仍无 impl。

- 新增 ProductService 接口，声明 Optional<Product> getProductById(Long id)，以及与 products 表字段对应的 Product record。按用户要求不创建 impl；Controller 暂不注入接口，避免缺少实现 Bean 导致启动失败，仍返回 501。

- 已按用户“暂不需要 impl”要求创建 ProductController 的 GET /api/products/{id} 入口，getProductById 接收 Long id，暂返回 501 Not Implemented；未添加 Service、Repository、Entity 或 DTO 实现。

- 按用户要求将空的 model 目录改名为 entity，专门存放数据库表对应对象；不保留 model 目录，尚未创建实体类。

- 用户明确要求 Service 接口与实现分开编写，已创建空目录 service/impl；后续接口放 service，实现类放 service.impl。

- 按用户要求在 inventory-service 的 com.libo.inventory 下仅创建空目录 controller、service、repository、model、dto、exception、config，尚未添加类或业务代码。空目录不会被 Git 跟踪。

- 按用户要求将现有全部 30 条 inventory 记录的 quantity_on_hand 设为 20、reorder_point 设为 5，同时递增 version；数据库查询确认 30/30 条符合。种子脚本仍保留原始样例，重复执行不会覆盖现有数量。

- 已成功通过 Docker Compose 连接 MySQL；执行 `sql/seed-inventory.sql` 创建 stores、products、inventory 三张表，并插入 3 个虚拟门店、10 个商品、30 条库存记录。查询已返回全部 30 条记录，包括缺货和低库存样例。
- 种子脚本重复运行会保留已有库存数量；尚未实现库存流水或 Java 数据库接口。数据库命令行连接已验证，Java 实际查询仍未验证。

- 按用户要求将根目录 `.env.example` 改名为 `.env`，仍保留占位密码，需要用户填写；`.env` 被 Git 忽略。
- `application-mysql.properties` 添加对 `../.env` 和 `./.env` 的 optional properties 导入，支持项目根目录或 inventory-service 工作目录；当前工作目录文件优先。启用 mysql profile 时加载到 Spring 配置环境。
- IDEA 使用 mysql profile，Working directory 设置为项目根目录；不再需要手动设置 MYSQL_PASSWORD，已有环境变量会覆盖文件值。
- 密码直接填写，不加引号。MYSQL_SETUP.md 已更新。下方早期记录中复制模板及手动设置环境变量的步骤已由此变更替代。
- 此变更尚未验证实际数据库连接。

- 当前分支：`feature/inventory-service`，HEAD 为 `c59ccb1`。
- 本地 main 位于 `e7cb445`，已包含清理分支，尚未包含 MySQL 配置提交。
- 此次更新本文前工作区干净，MySQL 配置已提交。
- 当前分支与本地保存的 `origin/feature/inventory-service` 引用一致；未执行 fetch，GitHub 的实时状态需要再确认。
- 此次文档更新尚未提交；未替用户执行提交或推送。

## 最近添加的 MySQL 配置

- 根目录 `compose.yaml`：MySQL 8.4，绑定本机 `127.0.0.1:3306`，数据库和普通用户均为 `inventory`，命名卷保存数据，并配置健康检查。
- `.env.example`：本地密码模板；实际 `.env` 已被 Git 忽略。此次检查 `.env` 尚不存在，未设置或记录真实密码。
- inventory-service 的 pom.xml 已添加 JDBC starter 和 MySQL Connector/J。
- `application.properties` 默认排除数据源自动配置；启用 `mysql` profile 后由 `application-mysql.properties` 恢复数据源自动配置，并读取环境变量 `MYSQL_PASSWORD`。
- JDBC URL 连接 localhost:3306/inventory，其中关闭 TLS 和允许获取公钥的参数只用于本机开发。
- 详细操作见 `MYSQL_SETUP.md`。当前没有实现业务表或数据库接口。
- 最近检查时当前终端找不到 docker 命令；尚未验证容器启动、数据库登录或应用实际连接数据库。构建通过不代表数据库连接已验证。

## 下次如何继续

对助手说：“请先阅读 PROJECT_CONTEXT.md，检查当前 Git 状态，然后继续。”

继续前先运行 `git status`、`git branch -vv` 和 `git log -5 --oneline`。
如果需要确认 GitHub 最新状态，再运行 `git fetch origin`。
重启后先完成 Docker 和数据库连接验证：

1. 安装或启动 Docker Desktop（Linux containers），重新打开终端，检查 `docker --version` 和 `docker compose version`。
2. 在项目根目录执行 `Copy-Item .env.example .env`（仅在 .env 不存在时），编辑两个本地密码。
3. 执行 `docker compose up -d --wait mysql`，用 `docker compose ps` 检查状态。
4. IDEA 的 inventory-service Run Configuration 设置 Active profiles 为 `mysql`，Environment variables 中设置 `MYSQL_PASSWORD`，值与 .env 中普通用户密码相同，然后启动服务。
5. 如用 PowerShell 启动，先设置 `$env:MYSQL_PASSWORD = '你的本地密码'`，再运行 `mvn -f inventory-service/pom.xml spring-boot:run "-Dspring-boot.run.profiles=mysql"`。重启后需要重新设置进程环境变量。
6. 使用 `docker compose exec mysql mysql -u inventory -p inventory` 登录，执行 `SELECT 1;` 验证数据库。

Compose 会读取根目录 .env；IDEA/Maven 启动的 Java 进程不会自动读取该文件。
修改 .env 不会修改已经初始化的数据库账户密码。`docker compose down` 保留数据；不要使用 `down -v`，除非明确要删除数据。
之后按用户需求开发 inventory-service；未授权自动合并或推送。
后续每次完成一段工作，更新本文的当前进度和下一步，再按需要提交保存。

