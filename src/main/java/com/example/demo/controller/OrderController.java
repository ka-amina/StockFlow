package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.SalesOrderDTO;
import com.example.demo.model.SalesOrder;
import com.example.demo.repository.SalesOrderRepository;
import com.example.demo.mapper.SalesOrderMapper;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final SalesOrderRepository salesOrderRepository;
    private final SalesOrderMapper salesOrderMapper;
    private final UserRepository userRepository;

    public OrderController(SalesOrderRepository salesOrderRepository, SalesOrderMapper salesOrderMapper, UserRepository userRepository) {
        this.salesOrderRepository = salesOrderRepository;
        this.salesOrderMapper = salesOrderMapper;
        this.userRepository = userRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'CLIENT')")
    public ResponseEntity<List<SalesOrderDTO>> getAllOrders(Authentication authentication) {
        // Extract email from JWT
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        
        List<SalesOrder> orders;
        
        // If CLIENT role, return only their orders
        if (user.getRole().getRoleName().equals("CLIENT")) {
            orders = salesOrderRepository.findByClientUser(user);
        } else {
            // ADMIN and WAREHOUSE_MANAGER can see all orders
            orders = salesOrderRepository.findAll();
        }
        
        List<SalesOrderDTO> orderDTOs = orders.stream()
                .map(salesOrderMapper::toDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(orderDTOs);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'CLIENT')")
    public ResponseEntity<SalesOrderDTO> getOrderById(@PathVariable Long id, Authentication authentication) {
        // Extract email from JWT
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        
        SalesOrder order = salesOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        // If CLIENT role, check if they own this order
        if (user.getRole().getRoleName().equals("CLIENT")) {
            if (!order.getClient().getUser().getId().equals(user.getId())) {
                throw new org.springframework.security.access.AccessDeniedException("Access denied");
            }
        }
        
        return ResponseEntity.ok(salesOrderMapper.toDto(order));
    }
}
