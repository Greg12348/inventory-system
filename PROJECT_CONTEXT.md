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
创建服务时四个构建均通过；本文更新时没有重新执行构建。
本机 JDK 路径为 `D:\Daily Tool\Java21`，IDEA 的 Project SDK 和语言级别应使用 Java 21。
根目录 `.gitignore` 已配置 Maven 输出、IDE 配置、环境文件等忽略规则。

## 已完成的 Git 操作

- 初始化了包含四个服务的单一 Git 仓库。
- 关联 GitHub： https://github.com/Greg12348/inventory-system
- 使用 `feature/submission-service` 分支进行 Git 练习，在 submission 服务中添加过 `GET /hello`，返回 `Hello World!`。
- PR #1 已合并到 main。
- 创建 `feature/submission-cleanup` 分支，删除了 Hello World 接口。

已核实的提交记录（从早到晚）：

```text
5865685 chore: initialize microservices project structure
ee06561 git test
fa11b9d Merge pull request #1 from Greg12348/feature/submission-service
0a11aeb refactor: remove hello world endpoint from submission service
```

## 当前进度

- 当前分支：`feature/submission-cleanup`，HEAD 为 `0a11aeb`。
- 本地 main 位于 `fa11b9d`，尚未包含清理提交。
- 创建本文前工作区干净。
- 当前分支与本地保存的 `origin/feature/submission-cleanup` 引用一致；未执行 fetch，GitHub 的实时状态需要再确认。
- 本文新建后尚未提交。

## 下次如何继续

对助手说：“请先阅读 PROJECT_CONTEXT.md，检查当前 Git 状态，然后继续。”

继续前先运行 `git status`、`git branch -vv` 和 `git log -5 --oneline`。
如果需要确认 GitHub 最新状态，再运行 `git fetch origin`。
下一步可继续练习通过 PR 将清理分支合并到 main；这只是候选步骤，尚未执行，也不是自动合并的授权。
后续每次完成一段工作，更新本文的当前进度和下一步，再按需要提交保存。
