package com.example.demo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordInboundDTO {

    @NotNull(message = "Product ID must not be null")
    private Long productId;

    @NotNull(message = "Warehouse ID must not be null")
    private Long warehouseId;

    @NotNull(message = "Quantity must not be null")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    private String referenceDoc;

    private String notes;
}
