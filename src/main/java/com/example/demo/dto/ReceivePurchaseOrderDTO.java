package com.example.demo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceivePurchaseOrderDTO {

    @NotNull(message = "Purchase order item ID must not be null")
    private Long purchaseOrderItemId;

    @Min(value = 1, message = "Quantity received must be at least 1")
    private int quantityReceived;
}
