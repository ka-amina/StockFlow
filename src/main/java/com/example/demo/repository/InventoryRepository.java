package com.example.demo.repository;

import com.example.demo.model.Inventory;
import com.example.demo.model.Product;
import com.example.demo.model.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByProductAndWarehouse(Product product, Warehouse warehouse);

    List<Inventory> findByProduct(Product product);

    List<Inventory> findByWarehouse(Warehouse warehouse);

    @Query("SELECT i FROM Inventory i WHERE i.product.id = :productId AND i.warehouse.id = :warehouseId")
    Optional<Inventory> findByProductIdAndWarehouseId(@Param("productId") Long productId,
                                                      @Param("warehouseId") Long warehouseId);

    // Find products with low stock (available quantity below threshold)
    @Query("SELECT i FROM Inventory i WHERE (i.qtyOnHand - i.qtyReserved) < :threshold")
    List<Inventory> findLowStockItems(@Param("threshold") Integer threshold);

    // Check if sufficient stock is available
    @Query("SELECT CASE WHEN (i.qtyOnHand - i.qtyReserved) >= :quantity THEN true ELSE false END " +
            "FROM Inventory i WHERE i.product.id = :productId AND i.warehouse.id = :warehouseId")
    boolean hasAvailableStock(@Param("productId") Long productId,
                              @Param("warehouseId") Long warehouseId,
                              @Param("quantity") Integer quantity);
}
