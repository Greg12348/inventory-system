package com.libo.inventory.submission.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/** 仅保留页面查询库存需要的字段，不依赖 inventory-service 的 Java 类。 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InventoryResponse {
    private Long storeId;
    private Long productId;
    private String sku;
    private String name;
    private String unit;
    private int quantityOnHand;
    private int reorderPoint;
}
