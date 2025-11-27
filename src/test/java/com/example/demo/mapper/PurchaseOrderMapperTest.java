package com.example.demo.mapper;

import com.example.demo.dto.PurchaseOrderDTO;
import com.example.demo.dto.PurchaseOrderItemDTO;
import com.example.demo.enums.PurchaseOrderStatus;
import com.example.demo.model.Product;
import com.example.demo.model.PurchaseOrder;
import com.example.demo.model.PurchaseOrderItem;
import com.example.demo.model.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PurchaseOrderMapperTest {

    private PurchaseOrderMapper purchaseOrderMapper;
    private PurchaseOrder purchaseOrder;
    private Supplier supplier;
    private Product product;

    @BeforeEach
    void setUp() {
        purchaseOrderMapper = Mappers.getMapper(PurchaseOrderMapper.class);

        supplier = new Supplier();
        supplier.setId(1L);
        supplier.setName("Test Supplier");

        product = new Product();
        product.setId(1L);
        product.setSku("SKU001");

        purchaseOrder = new PurchaseOrder();
        purchaseOrder.setId(1L);
        purchaseOrder.setPoNumber("PO-001");
        purchaseOrder.setSupplier(supplier);
        purchaseOrder.setStatus(PurchaseOrderStatus.CREATED);
        purchaseOrder.setIssuedDate(LocalDate.now());
        purchaseOrder.setExpectedDeliveryDate(LocalDate.now().plusDays(7));

        PurchaseOrderItem item = new PurchaseOrderItem();
        item.setId(1L);
        item.setProduct(product);
        item.setPrice(BigDecimal.valueOf(100.00));
        item.setQuantityOrdered(10);
        item.setQuantityReceived(0);
        item.setPurchaseOrder(purchaseOrder);

        List<PurchaseOrderItem> items = new ArrayList<>();
        items.add(item);
        purchaseOrder.setItems(items);
    }

    @Test
    void toDto_ShouldMapAllFields() {
        // ARRANGE - Already set up in beforeEach

        // ACT
        PurchaseOrderDTO dto = purchaseOrderMapper.toDto(purchaseOrder);

        // ASSERT
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("PO-001", dto.getPoNumber());
        assertEquals(1L, dto.getSupplierId());
        assertEquals("Test Supplier", dto.getSupplierName());
        assertEquals(PurchaseOrderStatus.CREATED, dto.getStatus());
        assertNotNull(dto.getIssuedDate());
        assertNotNull(dto.getExpectedDeliveryDate());
        assertNotNull(dto.getItems());
        assertEquals(1, dto.getItems().size());
    }

    @Test
    void toDto_WithNullSupplier_ShouldHandleGracefully() {
        // ARRANGE
        purchaseOrder.setSupplier(null);

        // ACT
        PurchaseOrderDTO dto = purchaseOrderMapper.toDto(purchaseOrder);

        // ASSERT
        assertNotNull(dto);
        assertNull(dto.getSupplierId());
        assertNull(dto.getSupplierName());
    }

    @Test
    void toItemDto_ShouldMapAllFields() {
        // ARRANGE
        PurchaseOrderItem item = purchaseOrder.getItems().get(0);

        // ACT
        PurchaseOrderItemDTO dto = purchaseOrderMapper.toDto(item);

        // ASSERT
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals(1L, dto.getProductId());
        assertEquals("SKU001", dto.getProductSku());
        assertEquals(10, dto.getQuantityOrdered());
        assertEquals(0, dto.getQuantityReceived());
        assertEquals(BigDecimal.valueOf(100.00), dto.getPrice());
    }

    @Test
    void toItemDto_WithQuantities_ShouldMapCorrectly() {
        // ARRANGE
        PurchaseOrderItem item = purchaseOrder.getItems().get(0);
        item.setQuantityOrdered(20);
        item.setQuantityReceived(15);

        // ACT
        PurchaseOrderItemDTO dto = purchaseOrderMapper.toDto(item);

        // ASSERT
        assertEquals(20, dto.getQuantityOrdered());
        assertEquals(15, dto.getQuantityReceived());
    }

    @Test
    void toDto_WithEmptyItems_ShouldHandleGracefully() {
        // ARRANGE
        purchaseOrder.setItems(new ArrayList<>());

        // ACT
        PurchaseOrderDTO dto = purchaseOrderMapper.toDto(purchaseOrder);

        // ASSERT
        assertNotNull(dto);
        assertNotNull(dto.getItems());
        assertTrue(dto.getItems().isEmpty());
    }

    @Test
    void toDto_WithNullProduct_ShouldHandleGracefully() {
        // ARRANGE
        purchaseOrder.getItems().get(0).setProduct(null);

        // ACT
        PurchaseOrderDTO dto = purchaseOrderMapper.toDto(purchaseOrder);

        // ASSERT
        assertNotNull(dto);
        assertNull(dto.getItems().get(0).getProductId());
        assertNull(dto.getItems().get(0).getProductSku());
    }
}

