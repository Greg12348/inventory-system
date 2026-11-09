# 项目交接记录

更新日期：2026-11-09。本文是当时的进度快照，继续前请先检查实际代码和 Git 状态。

## 项目结构

项目根目录：`D:\Study Data\ICC\inventory`。

四个独立的 Spring Boot 服务均使用 Maven、Java 21、Spring Boot 4.1.2：

| 服务 | 包名 | 端口 |
| --- | --- | --- |
| adjustment-submission-service | com.libo.inventory.submission | 8081 |
| adjustment-approval-service | com.libo.inventory.approval | 8082 |
| adjustment-aggregation-service | com.libo.inventory.aggregation | 8083 |
| inventory-service | com.libo.inventory | 8084 |

在各服务目录运行 `mvn spring-boot:run`，使用 `mvn clean verify` 构建。
创建服务时四个构建均通过；加入 MySQL 配置后，inventory-service 的 `mvn -B -q -f inventory-service/pom.xml verify` 也已通过。此次更新文档未重新构建。
本机 JDK 路径为 `D:\Daily Tool\Java21`，IDEA 的 Project SDK 和语言级别应使用 Java 21。
根目录 `.gitignore` 已配置 Maven 输出、IDE 配置、环境文件等忽略规则。

## 已完成的 Git 操作

- 初始化了包含四个服务的单一 Git 仓库。
- 关联 GitHub： https://github.com/Greg12348/inventory-system
- 使用 `feature/submission-service` 分支进行 Git 练习，在 submission 服务中添加过 `GET /hello`，返回 `Hello World!`。
- PR #1 已合并到 main。
- 创建 `feature/submission-cleanup` 分支，删除了 Hello World 接口。
- 清理分支和早期交接文档已通过 PR 合并到 main（PR #2、#3）。
- 已创建 `feature/inventory-service`，提交 MySQL 配置，提交为 `c59ccb1 SQL`。

已核实的提交记录（从早到晚）：

```text
5865685 chore: initialize microservices project structure
ee06561 git test
fa11b9d Merge pull request #1 from Greg12348/feature/submission-service
0a11aeb refactor: remove hello world endpoint from submission service
2e0b626 Merge pull request #2 from Greg12348/feature/submission-cleanup
cabb462 docs: save project progress and git context
e7cb445 Merge pull request #3 from Greg12348/feature/submission-cleanup
c59ccb1 SQL
```

## 当前进度

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
