package com.example.demo.model;

import com.example.demo.enums.ShipmentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "shipments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_order_id", nullable = false)
    private SalesOrder salesOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrier_id")
    private Carrier carrier;

    @Column(unique = true)
    private String trackingNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ShipmentStatus status = ShipmentStatus.PLANNED;

    private LocalDateTime plannedDate;

    private LocalDateTime shippedDate;

    private LocalDateTime deliveredDate;

    private String notes;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Business methods for status transitions
    public void markInTransit() {
        if (this.status != ShipmentStatus.PLANNED) {
            throw new IllegalStateException("Can only mark PLANNED shipments as IN_TRANSIT");
        }
        this.status = ShipmentStatus.IN_TRANSIT;
        this.shippedDate = LocalDateTime.now();
    }

    public void markDelivered() {
        if (this.status != ShipmentStatus.IN_TRANSIT) {
            throw new IllegalStateException("Can only mark IN_TRANSIT shipments as DELIVERED");
        }
        this.status = ShipmentStatus.DELIVERED;
        this.deliveredDate = LocalDateTime.now();
    }
}
