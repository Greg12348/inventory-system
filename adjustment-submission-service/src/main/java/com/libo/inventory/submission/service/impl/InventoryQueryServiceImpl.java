package com.libo.inventory.submission.service.impl;

import com.libo.inventory.submission.client.InventoryClient;
import com.libo.inventory.submission.dto.InventoryResponse;
import com.libo.inventory.submission.exception.InventoryQueryException;
import com.libo.inventory.submission.service.InventoryQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryQueryServiceImpl implements InventoryQueryService {
    private final InventoryClient inventoryClient;

    @Override
    public InventoryResponse getProductById(Long storeId, Long productId) {
        if (storeId == null || productId == null || storeId <= 0 || productId <= 0) {
            throw new InventoryQueryException(HttpStatus.BAD_REQUEST, "INVALID_INPUT",
                    "storeId 和 productId 必须是大于 0 的整数");
        }
        return inventoryClient.getProductById(storeId, productId);
    }
}

