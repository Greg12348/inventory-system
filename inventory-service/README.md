# inventory-service

Spring Boot 4.1.2 inventory service using Maven, Java 21 and Spring Data JPA.

Codex development context: [PROJECT_CONTEXT.md](PROJECT_CONTEXT.md).
Local MySQL setup: [MYSQL_SETUP.md](../MYSQL_SETUP.md).

Run from this directory:

```shell
mvn spring-boot:run
```

Build:

```shell
mvn clean verify
```

Start MySQL before running this service. Database configuration is loaded by default; no active profile is required.

Port: 8084. Endpoint: `GET /api/stores/{storeId}/products/{productId}`.

