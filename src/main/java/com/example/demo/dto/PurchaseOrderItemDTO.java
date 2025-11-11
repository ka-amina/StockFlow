package com.example.demo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderItemDTO {

    private Long id;

    @NotNull(message = "Product ID must not be null")
    private Long productId;

    private String productSku;

    @Min(value = 1, message = "Quantity ordered must be at least 1")
    private int quantityOrdered;

    private int quantityReceived;
}
