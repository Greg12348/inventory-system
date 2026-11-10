# adjustment-submission-service

Spring Boot 4.1.2 submission service using Maven and Java 21.

Development context: [PROJECT_CONTEXT.md](PROJECT_CONTEXT.md).

Run from this directory:

```shell
mvn spring-boot:run
```

Build:

```shell
mvn clean verify
```

Port: 8081.

Inventory lookup: `GET /api/stores/{storeId}/products/{productId}/inventory`.
Start MySQL and inventory-service first. The downstream base URL defaults to
`http://localhost:8084`; override it with `INVENTORY_SERVICE_BASE_URL` if needed.
Returns store/product identifiers, SKU, name, unit, quantity on hand and reorder point.
Errors use ProblemDetail: invalid input 400, missing inventory 404, downstream unavailable 503.
This lookup does not create a submission or modify inventory.

