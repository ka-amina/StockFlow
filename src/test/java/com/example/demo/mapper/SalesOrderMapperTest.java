package com.example.demo.mapper;

import com.example.demo.dto.SalesOrderDTO;
import com.example.demo.dto.SalesOrderLineDTO;
import com.example.demo.enums.SalesOrderStatus;
import com.example.demo.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SalesOrderMapperTest {

    private SalesOrderMapper salesOrderMapper;
    private SalesOrder salesOrder;
    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        salesOrderMapper = Mappers.getMapper(SalesOrderMapper.class);
        
        Client client = Client.builder()
                .id(1L)
                .name("Test Client")
                .build();
                
        Warehouse warehouse = Warehouse.builder()
                .id(1L)
                .code("WH-001")
                .build();
        
        product1 = Product.builder()
                .id(1L)
                .sku("SKU-001")
                .name("Product 1")
                .build();
                
        product2 = Product.builder()
                .id(2L)
                .sku("SKU-002")
                .name("Product 2")
                .build();
        
        salesOrder = SalesOrder.builder()
                .id(1L)
                .orderNumber("SO-12345")
                .client(client)
                .warehouse(warehouse)
                .status(SalesOrderStatus.CREATED)
                .orderDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
        
        // Add order lines
        SalesOrderLine line1 = SalesOrderLine.builder()
                .id(1L)
                .salesOrder(salesOrder)
                .product(product1)
                .quantity(10)
                .unitPrice(BigDecimal.valueOf(50.00))
                .build();
                
        SalesOrderLine line2 = SalesOrderLine.builder()
                .id(2L)
                .salesOrder(salesOrder)
                .product(product2)
                .quantity(5)
                .unitPrice(BigDecimal.valueOf(100.00))
                .build();
        
        List<SalesOrderLine> orderLines = new ArrayList<>();
        orderLines.add(line1);
        orderLines.add(line2);
        salesOrder.setOrderLines(orderLines);
    }

    @Test
    void toDto_ShouldMapAllFields() {
        // ARRANGE - Already set up in beforeEach

        // ACT
        SalesOrderDTO dto = salesOrderMapper.toDto(salesOrder);

        // ASSERT
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals(1L, dto.getClientId());
        assertEquals("Test Client", dto.getClientName());
        assertEquals(1L, dto.getWarehouseId());
        assertEquals("WH-001", dto.getWarehouseCode());
        assertEquals(SalesOrderStatus.CREATED, dto.getStatus());
        assertEquals(2, dto.getOrderLines().size());
        assertEquals(BigDecimal.valueOf(1000.00), dto.getTotalAmount()); // (10*50) + (5*100)
    }

    @Test
    void toDto_WithEmptyOrderLines_ShouldReturnZeroTotal() {
        // ARRANGE
        salesOrder.setOrderLines(new ArrayList<>());

        // ACT
        SalesOrderDTO dto = salesOrderMapper.toDto(salesOrder);

        // ASSERT
        assertNotNull(dto);
        assertEquals(BigDecimal.ZERO, dto.getTotalAmount());
        assertEquals(0, dto.getOrderLines().size());
    }

    @Test
    void toDto_WithNullOrderLines_ShouldReturnZeroTotal() {
        // ARRANGE
        salesOrder.setOrderLines(null);

        // ACT
        SalesOrderDTO dto = salesOrderMapper.toDto(salesOrder);

        // ASSERT
        assertNotNull(dto);
        assertEquals(BigDecimal.ZERO, dto.getTotalAmount());
    }

    @Test
    void toLineDto_ShouldMapAllFields() {
        // ARRANGE
        SalesOrderLine line = salesOrder.getOrderLines().get(0);

        // ACT
        SalesOrderLineDTO lineDto = salesOrderMapper.toLineDto(line);

        // ASSERT
        assertNotNull(lineDto);
        assertEquals(1L, lineDto.getId());
        assertEquals(1L, lineDto.getProductId());
        assertEquals("SKU-001", lineDto.getProductSku());
        assertEquals("Product 1", lineDto.getProductName());
        assertEquals(10, lineDto.getQuantity());
        assertEquals(BigDecimal.valueOf(50.00), lineDto.getUnitPrice());
        assertEquals(BigDecimal.valueOf(500.00), lineDto.getLineTotal());
    }

    @Test
    void calculateTotalAmount_ShouldSumAllLineItems() {
        // ARRANGE - Already set up in beforeEach

        // ACT
        BigDecimal total = salesOrderMapper.calculateTotalAmount(salesOrder);

        // ASSERT
        assertEquals(BigDecimal.valueOf(1000.00), total);
    }

    @Test
    void calculateTotalAmount_WithEmptyLines_ShouldReturnZero() {
        // ARRANGE
        salesOrder.setOrderLines(new ArrayList<>());

        // ACT
        BigDecimal total = salesOrderMapper.calculateTotalAmount(salesOrder);

        // ASSERT
        assertEquals(BigDecimal.ZERO, total);
    }

    @Test
    void toDto_WithReservedStatus_ShouldMapCorrectly() {
        // ARRANGE
        salesOrder.setStatus(SalesOrderStatus.RESERVED);
        salesOrder.setReservedAt(LocalDateTime.now());

        // ACT
        SalesOrderDTO dto = salesOrderMapper.toDto(salesOrder);

        // ASSERT
        assertEquals(SalesOrderStatus.RESERVED, dto.getStatus());
        assertNotNull(dto.getReservedAt());
    }
}
