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

## 服务开发记录

submission 的库存查询功能及后续开发上下文见 [adjustment-submission-service/PROJECT_CONTEXT.md](adjustment-submission-service/PROJECT_CONTEXT.md)。

inventory-service 的 Codex 开发进度、配置变更、测试和历史记录已迁移至 [inventory-service/PROJECT_CONTEXT.md](inventory-service/PROJECT_CONTEXT.md)。

继续开发前先阅读对应服务记录，并检查 git status、git branch -vv、git log -5 --oneline。历史提交列表仅为早期快照，当前状态以 Git 为准。

后续 inventory-service 的记录更新到该服务目录下；共享的 MySQL 启动说明仍在根目录 [MYSQL_SETUP.md](MYSQL_SETUP.md)。未经用户要求，不自动提交、推送或合并。
