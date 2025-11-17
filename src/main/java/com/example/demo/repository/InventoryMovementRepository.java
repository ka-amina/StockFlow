package com.example.demo.repository;

import com.example.demo.enums.MovementType;
import com.example.demo.model.InventoryMovement;
import com.example.demo.model.Product;
import com.example.demo.model.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {


    // Find all movements for a specific product
    List<InventoryMovement> findByProduct(Product product);

    // Find all movements for a specific warehouse
    List<InventoryMovement> findByWarehouse(Warehouse warehouse);

    // Find all movements for a specific product in a specific warehouse
    List<InventoryMovement> findByProductAndWarehouse(Product product, Warehouse warehouse);

    // Find movements by type

    List<InventoryMovement> findByType(MovementType type);

    // Find movements within a date range
    @Query("SELECT im FROM InventoryMovement im WHERE im.occurredAt BETWEEN :startDate AND :endDate " +
            "ORDER BY im.occurredAt DESC")
    List<InventoryMovement> findByDateRange(@Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate);

    // Find movements by reference document
    List<InventoryMovement> findByReferenceDoc(String referenceDoc);

    // Find recent movements (ordered by date descending)
    List<InventoryMovement> findTop50ByOrderByOccurredAtDesc();
}
