package com.libo.inventory.exception;

public class InventoryNotFoundException extends RuntimeException {
    public InventoryNotFoundException(Long storeId, Long productId) {
        super("门店 " + storeId + " 中不存在商品 " + productId + " 的库存记录");
    }
}
