package com.example.demo.model;

import com.example.demo.enums.SalesOrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sales_orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", nullable = false, unique = true)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private SalesOrderStatus status = SalesOrderStatus.CREATED;

    @OneToMany(mappedBy = "salesOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SalesOrderLine> orderLines = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    private LocalDateTime reservedAt;

    private LocalDateTime shippedAt;

    private LocalDateTime deliveredAt;

    private LocalDateTime canceledAt;

    private String notes;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (orderDate == null) {
            orderDate = LocalDateTime.now();
        }
        if (orderNumber == null) {
            orderNumber = "SO-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 10000);
        }
    }

    // Helper method to add order line
    public void addOrderLine(SalesOrderLine line) {
        orderLines.add(line);
        line.setSalesOrder(this);
    }

    // Helper method to remove order line
    public void removeOrderLine(SalesOrderLine line) {
        orderLines.remove(line);
        line.setSalesOrder(null);
    }

    // Business method: transition to RESERVED
    public void reserve() {
        if (this.status != SalesOrderStatus.CREATED) {
            throw new IllegalStateException("Can only reserve orders in CREATED status. Current status: " + this.status);
        }
        this.status = SalesOrderStatus.RESERVED;
        this.reservedAt = LocalDateTime.now();
    }

    // Business method: transition to SHIPPED
    public void ship() {
        if (this.status != SalesOrderStatus.RESERVED) {
            throw new IllegalStateException("Can only ship orders in RESERVED status. Current status: " + this.status);
        }
        this.status = SalesOrderStatus.SHIPPED;
        this.shippedAt = LocalDateTime.now();
    }

    // Business method: transition to DELIVERED
    public void deliver() {
        if (this.status != SalesOrderStatus.SHIPPED) {
            throw new IllegalStateException("Can only deliver orders in SHIPPED status. Current status: " + this.status);
        }
        this.status = SalesOrderStatus.DELIVERED;
        this.deliveredAt = LocalDateTime.now();
    }

    // Business method: cancel order
    public void cancel() {
        if (this.status == SalesOrderStatus.SHIPPED || this.status == SalesOrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot cancel orders that are SHIPPED or DELIVERED. Current status: " + this.status);
        }
        if (this.status == SalesOrderStatus.CANCELED) {
            throw new IllegalStateException("Order is already canceled");
        }
        this.status = SalesOrderStatus.CANCELED;
        this.canceledAt = LocalDateTime.now();
    }
}
