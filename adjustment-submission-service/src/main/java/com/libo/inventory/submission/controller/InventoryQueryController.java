package com.libo.inventory.submission.controller;

import com.libo.inventory.submission.dto.InventoryResponse;
import com.libo.inventory.submission.service.InventoryQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stores/{storeId}/products")
@RequiredArgsConstructor
public class InventoryQueryController {
    private final InventoryQueryService inventoryQueryService;

    @GetMapping("/{productId}/inventory")
    public InventoryResponse getProductById(@PathVariable("storeId") Long storeId,
                                            @PathVariable("productId") Long productId) {
        return inventoryQueryService.getProductById(storeId, productId);
    }
}
