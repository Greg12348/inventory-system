package com.libo.inventory.submission.client;

import com.libo.inventory.submission.dto.InventoryResponse;
import com.libo.inventory.submission.exception.InventoryQueryException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.http.HttpClient;
import java.time.Duration;

@Component
public class InventoryClient {
    private final RestClient restClient;

    public InventoryClient(@Value("${inventory-service.base-url}") String baseUrl) {
        // 设置连接与读取超时，避免下游不可用时请求一直等待。
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3)).build();
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(Duration.ofSeconds(5));
        restClient = RestClient.builder().baseUrl(baseUrl).requestFactory(factory).build();
    }

    public InventoryResponse getProductById(Long storeId, Long productId) {
        try {
            InventoryResponse response = restClient.get()
                    .uri("/api/stores/{storeId}/products/{productId}", storeId, productId)
                    .retrieve()
                    .onStatus(status -> status.value() == 404, (request, result) -> {
                        throw new InventoryQueryException(HttpStatus.NOT_FOUND, "INVENTORY_NOT_FOUND",
                                "该门店没有对应商品的库存记录");
                    })
                    .onStatus(status -> status.value() == 400, (request, result) -> {
                        throw new InventoryQueryException(HttpStatus.BAD_REQUEST, "INVALID_INPUT",
                                "库存查询参数无效");
                    })
                    .body(InventoryResponse.class);
            if (response == null) {
                throw unavailable();
            }
            return response;
        } catch (RestClientException exception) {
            // 网络故障、超时、下游5xx和无法解析的响应都不能当成“商品不存在”。
            throw unavailable();
        }
    }

    private InventoryQueryException unavailable() {
        return new InventoryQueryException(HttpStatus.SERVICE_UNAVAILABLE,
                "INVENTORY_SERVICE_UNAVAILABLE", "库存服务暂时不可用，请稍后重试");
    }
}
