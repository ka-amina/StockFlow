package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateShipmentDTO {

    @NotNull(message = "Sales Order ID is required")
    private Long salesOrderId;

    private Long carrierId; 

    private String trackingNumber;

    private LocalDateTime plannedDate;

    private String notes;
}
