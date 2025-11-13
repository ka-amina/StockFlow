package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderItemDTO {

    private Long id;

    @NotNull(message = "Product ID must not be null")
    private Long productId;

    private String productSku;

    @Min(value = 1, message = "Quantity ordered must be at least 1")
    @JsonProperty("quantity")
    private int quantityOrdered;

    private int quantityReceived;

    @NotNull(message = "Price must not be null")
    @DecimalMin(value = "0.01", message = "Price must be positive")
    private BigDecimal price;
}