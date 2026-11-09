package com.libo.inventory.service.impl;

import com.libo.inventory.dto.StoreProductResponse;
import com.libo.inventory.entity.Product;
import com.libo.inventory.repository.InventoryRepository;
import com.libo.inventory.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final InventoryRepository inventoryRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<StoreProductResponse> getProductById(Long storeId, Long productId) {
        return inventoryRepository.findByStoreIdAndProduct_Id(storeId, productId)
                .map(inventory -> {
                    Product product = inventory.getProduct();
                    StoreProductResponse response = new StoreProductResponse();
                    response.setStoreId(inventory.getStoreId());
                    response.setProductId(product.getId());
                    response.setSku(product.getSku());
                    response.setName(product.getName());
                    response.setCategory(product.getCategory());
                    response.setUnit(product.getUnit());
                    response.setQuantityOnHand(inventory.getQuantityOnHand());
                    response.setReorderPoint(inventory.getReorderPoint());
                    return response;
                });
    }
}
