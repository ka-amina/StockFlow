package com.example.demo.dto;

import com.example.demo.enums.AdjustmentType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordAdjustmentDTO {

    @NotNull(message = "Product ID must not be null")
    private Long productId;

    @NotNull(message = "Warehouse ID must not be null")
    private Long warehouseId;

    @NotNull(message = "Quantity adjustment must not be null")
    @Min(value = 1, message = "Quantity adjustment must be at least 1")
    private Integer quantityAdjustment;

    @NotNull(message = "Adjustment type must not be null")
    private AdjustmentType adjustmentType;

    private String referenceDoc;

    @NotNull(message = "Reason for adjustment must not be null")
    @NotBlank(message = "Reason cannot be blank")
    private String reason;
}
