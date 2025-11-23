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
    private final com.example.demo.service.ShipmentService shipmentService;

    public SalesOrderController(SalesOrderService salesOrderService, com.example.demo.service.ShipmentService shipmentService) {
        this.salesOrderService = salesOrderService;
        this.shipmentService = shipmentService;
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

    @PostMapping("/{id}/ship")
    public ResponseEntity<ApiResponse<SalesOrderDTO>> markOrderShipped(@PathVariable Long id) {
        // Find shipment for sales order and mark it as in-transit (shipped)
        com.example.demo.dto.ShipmentDTO shipment = shipmentService.getShipmentBySalesOrder(id);
        shipmentService.markInTransit(shipment.getId());
        SalesOrderDTO updatedOrder = salesOrderService.getOrderById(id);
        return ResponseEntity.ok(ApiResponse.success(updatedOrder, "Sales order marked as SHIPPED"));
    }

    @PostMapping("/{id}/deliver")
    public ResponseEntity<ApiResponse<SalesOrderDTO>> markOrderDelivered(@PathVariable Long id) {
        // Find shipment for sales order and mark it as delivered
        com.example.demo.dto.ShipmentDTO shipment = shipmentService.getShipmentBySalesOrder(id);
        shipmentService.markDelivered(shipment.getId());
        SalesOrderDTO updatedOrder = salesOrderService.getOrderById(id);
        return ResponseEntity.ok(ApiResponse.success(updatedOrder, "Sales order marked as DELIVERED"));
    }
}
