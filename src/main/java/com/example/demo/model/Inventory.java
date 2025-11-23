package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventory", uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "warehouse_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(nullable = false)
    @Builder.Default
    private Integer qtyOnHand = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer qtyReserved = 0;

    // Calculate available quantity for reservation or sale     
    public Integer getAvailable() {
        return qtyOnHand - qtyReserved;
    }


    // Increase physical quantity (for INBOUND operations)

    public void increaseQtyOnHand(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.qtyOnHand += quantity;
    }

    // Decrease physical quantity (for OUTBOUND operations)
    public void decreaseQtyOnHand(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (this.qtyOnHand - quantity < this.qtyReserved) {
            throw new IllegalStateException("Cannot decrease quantity below reserved amount");
        }
        this.qtyOnHand -= quantity;
    }

    // Increase reserved quantity (for order reservations)
    public void increaseQtyReserved(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (getAvailable() < quantity) {
            throw new IllegalStateException("Insufficient available quantity for reservation");
        }
        this.qtyReserved += quantity;
    }

    // Decrease reserved quantity (when releasing or shipping)
    public void decreaseQtyReserved(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (this.qtyReserved < quantity) {
            throw new IllegalStateException("Cannot decrease reserved quantity below zero");
        }
        this.qtyReserved -= quantity;
    }
}
