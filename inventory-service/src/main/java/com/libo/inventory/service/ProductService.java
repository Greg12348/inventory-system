package com.libo.inventory.service;

import com.libo.inventory.dto.StoreProductResponse;

import java.util.Optional;

public interface ProductService {

    Optional<StoreProductResponse> getProductById(Long storeId, Long productId);
}

