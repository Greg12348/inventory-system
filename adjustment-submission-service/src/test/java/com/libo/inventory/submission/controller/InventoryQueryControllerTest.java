package com.libo.inventory.submission.controller;

import com.libo.inventory.submission.dto.InventoryResponse;
import com.libo.inventory.submission.exception.GlobalExceptionHandler;
import com.libo.inventory.submission.exception.InventoryQueryException;
import com.libo.inventory.submission.service.InventoryQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** MockMvc API 测试：真实 Controller 和异常处理器，模拟 Service，无需启动服务器。 */
@ExtendWith(MockitoExtension.class)
class InventoryQueryControllerTest {
    @Mock
    private InventoryQueryService service;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // standaloneSetup 不扫描 Spring Bean，统一异常处理器需要手动注册。
        mockMvc = MockMvcBuilders.standaloneSetup(new InventoryQueryController(service))
                .setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @Test
    @DisplayName("查询成功返回 200 和页面需要的七个字段")
    void getProductById_whenFound_returnsJson() throws Exception {
        InventoryResponse response = new InventoryResponse();
        response.setStoreId(2L);
        response.setProductId(7L);
        response.setSku("TEST-P007");
        response.setName("Cotton T-Shirt Medium");
        response.setUnit("piece");
        response.setQuantityOnHand(18);
        response.setReorderPoint(5);
        when(service.getProductById(2L, 7L)).thenReturn(response);

        mockMvc.perform(get("/api/stores/2/products/7/inventory"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.storeId").value(2))
                .andExpect(jsonPath("$.productId").value(7))
                .andExpect(jsonPath("$.sku").value("TEST-P007"))
                .andExpect(jsonPath("$.name").value("Cotton T-Shirt Medium"))
                .andExpect(jsonPath("$.unit").value("piece"))
                .andExpect(jsonPath("$.quantityOnHand").value(18))
                .andExpect(jsonPath("$.reorderPoint").value(5))
                .andExpect(jsonPath("$.category").doesNotExist());
        verify(service).getProductById(2L, 7L);
    }

    @ParameterizedTest
    @CsvSource({"400,INVALID_INPUT", "404,INVENTORY_NOT_FOUND", "503,INVENTORY_SERVICE_UNAVAILABLE"})
    @DisplayName("Service 的业务异常转换为对应状态码与统一错误 JSON")
    void getProductById_whenServiceFails_returnsProblemDetail(int statusCode, String code) throws Exception {
        // 输入规则在 Service 校验；本测试模拟校验失败或下游调用失败。
        when(service.getProductById(2L, 7L)).thenThrow(new InventoryQueryException(
                HttpStatus.valueOf(statusCode), code, "测试错误说明"));

        mockMvc.perform(get("/api/stores/2/products/7/inventory"))
                .andExpect(status().is(statusCode))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(statusCode))
                .andExpect(jsonPath("$.code").value(code))
                .andExpect(jsonPath("$.detail").value("测试错误说明"))
                .andExpect(jsonPath("$.instance").value("/api/stores/2/products/7/inventory"));
        verify(service).getProductById(2L, 7L);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/api/stores/abc/products/7/inventory",
            "/api/stores/2/products/abc/inventory",
            "/api/stores/9223372036854775808/products/7/inventory",
            "/api/stores/2/products/9223372036854775808/inventory"
    })
    @DisplayName("非数字或超出 Long 范围的参数返回 400，不进入 Service")
    void getProductById_whenTypeMismatch_returnsBadRequest(String path) throws Exception {
        // 这类错误发生在 Spring MVC 参数转换阶段，由 Advice 统一处理。
        mockMvc.perform(get(path))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("INVALID_INPUT"))
                .andExpect(jsonPath("$.detail").isNotEmpty())
                .andExpect(jsonPath("$.instance").value(path));
        verifyNoInteractions(service);
    }
}
