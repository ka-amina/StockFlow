package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.CreatePurchaseOrderDTO;
import com.example.demo.dto.PurchaseOrderDTO;
import com.example.demo.dto.ReceivePurchaseOrderDTO;
import com.example.demo.service.PurchaseOrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/purchase-orders")
public class PurchaseOrderController {

    private final PurchaseOrderService service;

    public PurchaseOrderController(PurchaseOrderService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PurchaseOrderDTO>> createPurchaseOrder(@Valid @RequestBody CreatePurchaseOrderDTO dto) {
        PurchaseOrderDTO created = service.createPurchaseOrder(dto);
        ApiResponse<PurchaseOrderDTO> body = new ApiResponse<>("Purchase order created successfully", created);
        return ResponseEntity.status(201).body(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PurchaseOrderDTO>> getPurchaseOrderById(@PathVariable Long id) {
        PurchaseOrderDTO po = service.getPurchaseOrderById(id);
        ApiResponse<PurchaseOrderDTO> body = new ApiResponse<>("Purchase order retrieved successfully", po);
        return ResponseEntity.ok(body);
    }

    @PostMapping("/{id}/receive")
    public ResponseEntity<ApiResponse<PurchaseOrderDTO>> receivePurchaseOrder(@PathVariable Long id, @Valid @RequestBody ReceivePurchaseOrderDTO dto) {
        PurchaseOrderDTO updated = service.receivePurchaseOrder(dto);
        ApiResponse<PurchaseOrderDTO> body = new ApiResponse<>("Purchase order received successfully", updated);
        return ResponseEntity.ok(body);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<PurchaseOrderDTO>> cancelPurchaseOrder(@PathVariable Long id) {
        PurchaseOrderDTO cancelled = service.cancelPurchaseOrder(id);
        ApiResponse<PurchaseOrderDTO> body = new ApiResponse<>("Purchase order cancelled successfully", cancelled);
        return ResponseEntity.ok(body);
    }
}
