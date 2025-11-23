package com.example.demo.dto;

import com.example.demo.enums.SalesOrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesOrderDTO {
    private Long id;
    private Long clientId;
    private String clientName;
    private Long warehouseId;
    private String warehouseCode;
    private SalesOrderStatus status;
    private List<SalesOrderLineDTO> orderLines;
    private LocalDateTime createdAt;
    private LocalDateTime reservedAt;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime canceledAt;
    private String notes;
    private BigDecimal totalAmount;
}
