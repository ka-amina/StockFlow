package com.example.demo.model;

import com.example.demo.model.enums.ShipmentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "shipments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String trackingNumber;

    @Enumerated(EnumType.STRING)
    private ShipmentStatus status;

    private LocalDateTime plannedDate;

    private LocalDateTime shippedDate;

    private LocalDateTime deliveredDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_order_id")
    private SalesOrder salesOrder;
}
