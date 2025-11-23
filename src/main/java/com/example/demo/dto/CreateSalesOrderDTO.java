package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSalesOrderDTO {

    @NotNull(message = "Client ID is required")
    private Long clientId;

    @NotNull(message = "Warehouse ID is required")
    private Long warehouseId;

    private Long userId; // Optional: User who created/manages the order

    @NotNull(message = "Order lines are required")
    private List<CreateSalesOrderLineDTO> orderLines;

    private String notes;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateSalesOrderLineDTO {
        @NotNull(message = "Product ID is required")
        private Long productId;

        @NotNull(message = "Quantity is required")
        private Integer quantity;

        @NotNull(message = "Unit price is required")
        private java.math.BigDecimal unitPrice;
    }
}
