package com.example.demo.dto;

import com.example.demo.enums.ShipmentStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentDTO {
    private Long id;
    private Long salesOrderId;
    private String salesOrderReference;
    private Long carrierId;
    private String carrierName;
    private String carrierCode;
    private String trackingNumber;
    private ShipmentStatus status;
    private LocalDateTime plannedDate;
    private LocalDateTime shippedDate;
    private LocalDateTime deliveredDate;
    private LocalDateTime createdAt;
    private String notes;
}
