package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "purchase_order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @DecimalMin(value = "0.01", message = "Price must be positive")
    @Column(nullable = false)
    private BigDecimal price;

    @Min(value = 1, message = "Quantity ordered must be at least 1")
    @Column(nullable = false)
    private int quantityOrdered;

    @Min(value = 0, message = "Quantity received must not be negative")
    @Column(nullable = false)
    private int quantityReceived = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_id", nullable = false)
    private PurchaseOrder purchaseOrder;
}