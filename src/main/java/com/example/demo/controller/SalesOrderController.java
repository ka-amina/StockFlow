package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.CreateSalesOrderDTO;
import com.example.demo.dto.SalesOrderDTO;
import com.example.demo.enums.SalesOrderStatus;
import com.example.demo.service.SalesOrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales-orders")
public class SalesOrderController {

    private final SalesOrderService salesOrderService;

    public SalesOrderController(SalesOrderService salesOrderService) {
        this.salesOrderService = salesOrderService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SalesOrderDTO>> createOrder(@Valid @RequestBody CreateSalesOrderDTO dto) {
        SalesOrderDTO createdOrder = salesOrderService.createOrder(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdOrder, "Sales order created successfully"));
    }

    @PostMapping("/{id}/reserve")
    public ResponseEntity<ApiResponse<SalesOrderDTO>> reserveOrder(@PathVariable Long id) {
        SalesOrderDTO reservedOrder = salesOrderService.reserveOrder(id);
        return ResponseEntity.ok(ApiResponse.success(reservedOrder, "Sales order reserved successfully"));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<SalesOrderDTO>> cancelOrder(@PathVariable Long id) {
        SalesOrderDTO canceledOrder = salesOrderService.cancelOrder(id);
        return ResponseEntity.ok(ApiResponse.success(canceledOrder, "Sales order canceled successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SalesOrderDTO>> getOrderById(@PathVariable Long id) {
        SalesOrderDTO order = salesOrderService.getOrderById(id);
        return ResponseEntity.ok(ApiResponse.success(order, "Sales order retrieved successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SalesOrderDTO>>> getAllOrders(
            @RequestParam(required = false) SalesOrderStatus status,
            @RequestParam(required = false) Long clientId) {
        
        List<SalesOrderDTO> orders;
        String message;

        if (status != null) {
            orders = salesOrderService.getOrdersByStatus(status);
            message = "Sales orders retrieved by status: " + status;
        } else if (clientId != null) {
            orders = salesOrderService.getOrdersByClient(clientId);
            message = "Sales orders retrieved for client ID: " + clientId;
        } else {
            orders = salesOrderService.getAllOrders();
            message = "All sales orders retrieved successfully";
        }

        return ResponseEntity.ok(ApiResponse.success(orders, message));
    }
}
