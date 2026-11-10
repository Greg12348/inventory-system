package com.libo.inventory.submission.service.impl;

import com.libo.inventory.submission.client.InventoryClient;
import com.libo.inventory.submission.dto.InventoryResponse;
import com.libo.inventory.submission.exception.InventoryQueryException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** 只测试真实 Service，模拟 HTTP Client，不启动 Spring 或任何下游服务。 */
@ExtendWith(MockitoExtension.class)
class InventoryQueryServiceImplTest {
    @Mock
    private InventoryClient inventoryClient;

    @InjectMocks
    private InventoryQueryServiceImpl service;

    @Test
    @DisplayName("合法参数传给 Client，并返回 Client 提供的库存数据")
    void getProductById_whenValid_returnsInventory() {
        // Arrange：两个 ID 不同，便于发现参数传反的问题。
        InventoryResponse response = new InventoryResponse();
        response.setStoreId(2L);
        response.setProductId(7L);
        response.setQuantityOnHand(18);
        when(inventoryClient.getProductById(2L, 7L)).thenReturn(response);

        // Act + Assert：此 Service 不修改 Client 返回的数据。
        assertSame(response, service.getProductById(2L, 7L));
        verify(inventoryClient).getProductById(2L, 7L);
    }

    @ParameterizedTest
    @CsvSource(value = {"null,7", "2,null", "0,7", "-1,7", "2,0", "2,-1"}, nullValues = "null")
    @DisplayName("空值、零或负数 ID 被拒绝，不调用库存服务")
    void getProductById_whenInvalid_throwsBadRequest(Long storeId, Long productId) {
        // 非法输入应在发起网络请求前被拒绝。
        InventoryQueryException exception = assertThrows(InventoryQueryException.class,
                () -> service.getProductById(storeId, productId));

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus()),
                () -> assertEquals("INVALID_INPUT", exception.getCode())
        );
        verifyNoInteractions(inventoryClient);
    }

    @ParameterizedTest
    @CsvSource({"404,INVENTORY_NOT_FOUND", "503,INVENTORY_SERVICE_UNAVAILABLE"})
    @DisplayName("库存不存在与下游不可用保留各自的错误类型")
    void getProductById_whenClientFails_propagatesException(int status, String code) {
        // Service 不应把下游故障转换为“没有库存”，也不应吞掉异常。
        InventoryQueryException expected = new InventoryQueryException(
                HttpStatus.valueOf(status), code, "测试错误");
        when(inventoryClient.getProductById(2L, 7L)).thenThrow(expected);

        InventoryQueryException actual = assertThrows(InventoryQueryException.class,
                () -> service.getProductById(2L, 7L));
        assertSame(expected, actual);
        verify(inventoryClient).getProductById(2L, 7L);
    }
}
