package com.libo.inventory.controller;

import com.libo.inventory.dto.StoreProductResponse;
import com.libo.inventory.exception.GlobalExceptionHandler;
import com.libo.inventory.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 使用独立 MockMvc 验证 Controller 的请求映射、参数解析、状态码和 JSON 序列化。
 * 不启动 Spring Boot 或 HTTP 端口，不连接数据库，Service 使用 Mockito 模拟。
 * 手动创建 Controller，不加载应用的数据库配置。
 */
@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // 使用真实 Controller，依赖替换为模拟 Service。
        // 独立 MockMvc 不自动扫描 Advice，需要显式注册统一异常处理器。
        mockMvc = MockMvcBuilders.standaloneSetup(new ProductController(productService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("找到门店商品时返回 200 和完整 JSON")
    void getProductById_whenFound_returnsOkAndJson() throws Exception {
        // Arrange：两个 ID 使用不同值，以检查路径参数是否正确传递。
        StoreProductResponse response = new StoreProductResponse();
        response.setStoreId(2L);
        response.setProductId(7L);
        response.setSku("TEST-P007");
        response.setName("Cotton T-Shirt Medium");
        response.setCategory("Clothing");
        response.setUnit("piece");
        response.setQuantityOnHand(18);
        response.setReorderPoint(5);
        when(productService.getProductById(2L, 7L)).thenReturn(Optional.of(response));

        // Act + Assert：模拟 GET 请求，核对状态码、内容类型和所有响应字段。
        mockMvc.perform(get("/api/stores/{storeId}/products/{productId}", 2L, 7L))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.storeId").value(2))
                .andExpect(jsonPath("$.productId").value(7))
                .andExpect(jsonPath("$.sku").value("TEST-P007"))
                .andExpect(jsonPath("$.name").value("Cotton T-Shirt Medium"))
                .andExpect(jsonPath("$.category").value("Clothing"))
                .andExpect(jsonPath("$.unit").value("piece"))
                .andExpect(jsonPath("$.quantityOnHand").value(18))
                .andExpect(jsonPath("$.reorderPoint").value(5));

        verify(productService).getProductById(2L, 7L);
    }

    @Test
    @DisplayName("找不到门店商品时返回 404 和统一错误响应")
    void getProductById_whenNotFound_returnsNotFound() throws Exception {
        // 模拟 Service 查不到记录，验证自定义异常被 Handler 转换为统一响应。
        when(productService.getProductById(2L, 999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/stores/{storeId}/products/{productId}", 2L, 999L))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("INVENTORY_NOT_FOUND"))
                .andExpect(jsonPath("$.detail").value("门店 2 中不存在商品 999 的库存记录"))
                .andExpect(jsonPath("$.instance").value("/api/stores/2/products/999"));

        verify(productService).getProductById(2L, 999L);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/api/stores/abc/products/7",
            "/api/stores/2/products/abc",
            "/api/stores/9223372036854775808/products/7",
            "/api/stores/2/products/9223372036854775808",
            "/api/stores/0/products/7",
            "/api/stores/-1/products/7",
            "/api/stores/2/products/0",
            "/api/stores/2/products/-1"
    })
    @DisplayName("非数字、超范围、零或负数 ID 返回统一 400，且不调用 Service")
    void getProductById_whenIdIsInvalid_returnsBadRequest(String path) throws Exception {
        // 覆盖 Spring 参数转换异常与 Controller 主动输入校验两条错误路径。
        mockMvc.perform(get(path))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("INVALID_INPUT"))
                .andExpect(jsonPath("$.detail").isNotEmpty())
                .andExpect(jsonPath("$.instance").value(path));

        verifyNoInteractions(productService);
    }
}
