# 本地 MySQL

MySQL 在 Docker 中运行，inventory-service 在本机运行。先安装并启动 Docker Desktop，使用 Linux containers。

编辑项目根目录 `.env` 中的两个密码，直接填写值，不要加引号，然后运行：

```powershell
docker compose up -d --wait mysql
docker compose ps
```

首次初始化会创建 inventory 数据库和 inventory 用户。修改 .env 不会修改已初始化数据库中的密码。

先确保 MySQL 已运行，再在项目根目录执行以下命令。应用支持从项目根目录或 inventory-service 目录启动，自动读取根目录 `.env`；若两个目录都有 `.env`，启动工作目录中的文件优先：

```powershell
mvn -f inventory-service/pom.xml spring-boot:run
```

IDEA：在 inventory-service 的 Run Configuration 中设置 Working directory 为项目根目录 `D:\Study Data\ICC\inventory`。无需设置 Active profiles，可清除先前的 `mysql` 或 `--spring.profiles.active=mysql` 程序参数。
如果 Environment variables 中已有 MYSQL_PASSWORD，请删除以避免覆盖 `.env` 中的值。
Compose 和 Spring Boot 都读取根目录 `.env`。Spring Boot 将其加载到 Spring 配置环境，不会修改 Windows 全局环境变量。应用默认连接 MySQL。
服务端口为 8084，数据库已包含 stores、products、inventory 表及虚拟数据。
启动后可访问 `GET /api/stores/{storeId}/products/{productId}` 查询门店商品库存。
数据访问使用 Spring Data JPA；`spring.jpa.hibernate.ddl-auto=validate` 仅校验现有表结构，不自动创建或修改表。
URL 中关闭 TLS 和允许获取公钥的参数仅适用于本机开发。

数据库客户端：Host localhost，Port 3306，Database inventory，User inventory，密码同 .env 中的 MYSQL_PASSWORD。
也可以运行以下命令，输入密码后执行 SELECT 1; 验证连接：

```powershell
docker compose exec mysql mysql -u inventory -p inventory
```

运行 `docker compose down` 停止容器并保留命名卷中的数据。
不要使用 `docker compose down -v`，除非确定要删除数据库数据。
.env 已被 Git 忽略，仅保存在本机；compose.yaml 可以提交。
