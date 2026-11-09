package com.libo.inventory.controller;

import com.libo.inventory.dto.StoreProductResponse;
import com.libo.inventory.exception.InventoryNotFoundException;
import com.libo.inventory.exception.InvalidInputException;
import com.libo.inventory.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stores/{storeId}/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{productId}")
    public ResponseEntity<StoreProductResponse> getProductById(
            @PathVariable("storeId") Long storeId,
            @PathVariable("productId") Long productId) {
        // 能转换为 Long 的零和负数仍属于非法输入，不应进入数据库查询。
        if (storeId <= 0 || productId <= 0) {
            throw new InvalidInputException("storeId 和 productId 必须是大于 0 的整数");
        }
        StoreProductResponse response = productService.getProductById(storeId, productId)
                .orElseThrow(() -> new InventoryNotFoundException(storeId, productId));
        return ResponseEntity.ok(response);
    }
}
