package com.libo.inventory.dto;

import lombok.Data;

@Data
public class StoreProductResponse {
    private Long storeId;
    private Long productId;
    private String sku;
    private String name;
    private String category;
    private String unit;
    private int quantityOnHand;
    private int reorderPoint;
}
