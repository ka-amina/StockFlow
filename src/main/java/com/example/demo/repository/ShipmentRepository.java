package com.example.demo.repository;

import com.example.demo.model.Shipment;
import com.example.demo.model.SalesOrder;
import com.example.demo.enums.ShipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    
    Optional<Shipment> findBySalesOrder(SalesOrder salesOrder);
    
    Optional<Shipment> findByTrackingNumber(String trackingNumber);
    
    List<Shipment> findByStatus(ShipmentStatus status);
    
    boolean existsByTrackingNumber(String trackingNumber);
}
