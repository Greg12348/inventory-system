package com.libo.inventory.submission.service;

import com.libo.inventory.submission.dto.InventoryResponse;

public interface InventoryQueryService {
    InventoryResponse getProductById(Long storeId, Long productId);
}
