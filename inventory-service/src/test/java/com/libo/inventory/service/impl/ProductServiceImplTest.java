package com.libo.inventory.service.impl;

import com.libo.inventory.dto.StoreProductResponse;
import com.libo.inventory.entity.Inventory;
import com.libo.inventory.entity.Product;
import com.libo.inventory.repository.InventoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Service 单元测试：只测试业务方法，不启动 Spring，也不连接 MySQL。
 * Mockito 模拟 Repository 的返回结果，因此不需要 Docker 或数据库配置。
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    // 创建模拟的 Repository，不执行任何真实数据库查询。
    @Mock
    private InventoryRepository inventoryRepository;

    // 创建真实的 Service，并通过构造方法注入上面的模拟 Repository。
    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    @DisplayName("找到门店商品库存时，正确组合商品资料与库存数据")
    void getProductById_whenFound_returnsStoreProductResponse() {
        // Arrange：准备独立于数据库的测试数据，门店 ID 和商品 ID 使用不同值。
        Long storeId = 2L;
        Long productId = 7L;

        Product product = new Product();
        product.setId(productId);
        product.setSku("TEST-P007");
        product.setName("Cotton T-Shirt Medium");
        product.setCategory("Clothing");
        product.setUnit("piece");

        Inventory inventory = new Inventory();
        inventory.setStoreId(storeId);
        inventory.setProduct(product);
        inventory.setQuantityOnHand(18);
        inventory.setReorderPoint(5);

        // 规定只有使用这组门店和商品参数查询时，模拟仓库才返回该记录。
        when(inventoryRepository.findByStoreIdAndProduct_Id(storeId, productId))
                .thenReturn(Optional.of(inventory));

        // Act：执行真实的 Service 方法。
        Optional<StoreProductResponse> result = productService.getProductById(storeId, productId);

        // Assert：先确认有结果，再逐一核对 DTO 的全部业务字段。
        assertTrue(result.isPresent(), "找到库存时应返回商品库存信息");
        StoreProductResponse response = result.orElseThrow();
        assertAll(
                () -> assertEquals(storeId, response.getStoreId()),
                () -> assertEquals(productId, response.getProductId()),
                () -> assertEquals("TEST-P007", response.getSku()),
                () -> assertEquals("Cotton T-Shirt Medium", response.getName()),
                () -> assertEquals("Clothing", response.getCategory()),
                () -> assertEquals("piece", response.getUnit()),
                () -> assertEquals(18, response.getQuantityOnHand()),
                () -> assertEquals(5, response.getReorderPoint())
        );

        // 确认查询时正确传递了两个 ID，避免遗漏门店条件或颠倒参数。
        verify(inventoryRepository).findByStoreIdAndProduct_Id(storeId, productId);
    }

    @Test
    @DisplayName("找不到门店商品库存时，返回空 Optional")
    void getProductById_whenNotFound_returnsEmpty() {
        // Arrange：模拟该门店没有对应商品库存记录。
        Long storeId = 2L;
        Long productId = 999L;
        when(inventoryRepository.findByStoreIdAndProduct_Id(storeId, productId))
                .thenReturn(Optional.empty());

        // Act：调用真实 Service。
        Optional<StoreProductResponse> result = productService.getProductById(storeId, productId);

        // Assert：Service 保留“未找到”的结果，由 Controller 决定返回 HTTP 404。
        assertTrue(result.isEmpty(), "没有库存记录时不应构造商品响应");
        verify(inventoryRepository).findByStoreIdAndProduct_Id(storeId, productId);
    }
}
