# 本地 MySQL

MySQL 在 Docker 中运行，inventory-service 在本机运行。先安装并启动 Docker Desktop，使用 Linux containers。

在项目根目录执行 `Copy-Item .env.example .env`，编辑 .env 中的两个密码，然后运行：

```powershell
docker compose up -d --wait mysql
docker compose ps
```

首次初始化会创建 inventory 数据库和 inventory 用户。修改 .env 不会修改已初始化数据库中的密码。

在启动服务的 PowerShell 中设置相同的 inventory 用户密码：

```powershell
$env:MYSQL_PASSWORD = '你在.env中设置的MYSQL_PASSWORD'
mvn -f inventory-service/pom.xml spring-boot:run "-Dspring-boot.run.profiles=mysql"
```

IDEA：在 inventory-service 的 Run Configuration 中设置 Active profiles 为 mysql，并在 Environment variables 中设置 MYSQL_PASSWORD。
Compose 自动读取 .env，但本机 Java 进程不会。默认不启用数据库；mysql profile 启用 JDBC 数据源。
服务端口仍为 8084，目前没有业务表或数据库接口。
URL 中关闭 TLS 和允许获取公钥的参数仅适用于本机开发。

数据库客户端：Host localhost，Port 3306，Database inventory，User inventory，密码同 .env 中的 MYSQL_PASSWORD。
也可以运行以下命令，输入密码后执行 SELECT 1; 验证连接：

```powershell
docker compose exec mysql mysql -u inventory -p inventory
```

运行 `docker compose down` 停止容器并保留命名卷中的数据。
不要使用 `docker compose down -v`，除非确定要删除数据库数据。
.env 已被 Git 忽略；compose.yaml 和 .env.example 可以提交。
