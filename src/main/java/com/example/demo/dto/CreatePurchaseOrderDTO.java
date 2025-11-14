package com.example.demo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePurchaseOrderDTO {

    @NotNull(message = "Supplier ID must not be null")
    private Long supplierId;

    @FutureOrPresent(message = "Expected delivery date must be in the future or present")
    private LocalDate expectedDeliveryDate;

    @NotEmpty(message = "Purchase order must have at least one item")
    @Valid
    private List<PurchaseOrderItemDTO> items;
}
