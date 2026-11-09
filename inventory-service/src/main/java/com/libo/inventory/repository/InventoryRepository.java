package com.libo.inventory.repository;

import com.libo.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    @EntityGraph(attributePaths = "product")
    Optional<Inventory> findByStoreIdAndProduct_Id(Long storeId, Long productId);
}
