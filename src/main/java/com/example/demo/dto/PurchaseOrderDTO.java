package com.example.demo.dto;

import com.example.demo.enums.PurchaseOrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderDTO {

    private Long id;
    private String poNumber;
    private Long supplierId;
    private String supplierName;
    private PurchaseOrderStatus status;
    private LocalDate issuedDate;
    private LocalDate expectedDeliveryDate;
    private List<PurchaseOrderItemDTO> items;
}
