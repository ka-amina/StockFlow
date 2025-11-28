package com.example.demo.service;

import com.example.demo.dto.CreatePurchaseOrderDTO;
import com.example.demo.dto.PurchaseOrderDTO;
import com.example.demo.dto.PurchaseOrderItemDTO;
import com.example.demo.dto.ReceivePurchaseOrderDTO;
import com.example.demo.enums.PurchaseOrderStatus;
import com.example.demo.mapper.PurchaseOrderMapper;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderServiceTest {

    @Mock
    private PurchaseOrderRepository poRepo;

    @Mock
    private PurchaseOrderItemRepository poItemRepo;

    @Mock
    private SupplierRepository supplierRepo;

    @Mock
    private ProductRepository productRepo;

    @Mock
    private PurchaseOrderMapper mapper;

    @InjectMocks
    private PurchaseOrderService purchaseOrderService;

    private Supplier supplier;
    private Product product;
    private PurchaseOrder purchaseOrder;
    private PurchaseOrderItem purchaseOrderItem;
    private CreatePurchaseOrderDTO createDTO;
    private PurchaseOrderDTO purchaseOrderDTO;

    @BeforeEach
    void setUp() {
        supplier = new Supplier();
        supplier.setId(1L);
        supplier.setName("Test Supplier");

        product = new Product();
        product.setId(1L);
        product.setSku("PROD001");
        product.setName("Test Product");

        purchaseOrder = new PurchaseOrder();
        purchaseOrder.setId(1L);
        purchaseOrder.setPoNumber("PO-12345678");
        purchaseOrder.setSupplier(supplier);
        purchaseOrder.setStatus(PurchaseOrderStatus.CREATED);
        purchaseOrder.setIssuedDate(LocalDate.now());

        purchaseOrderItem = new PurchaseOrderItem();
        purchaseOrderItem.setId(1L);
        purchaseOrderItem.setPurchaseOrder(purchaseOrder);
        purchaseOrderItem.setProduct(product);
        purchaseOrderItem.setQuantityOrdered(100);
        purchaseOrderItem.setQuantityReceived(0);
        purchaseOrderItem.setPrice(BigDecimal.valueOf(50.00));

        purchaseOrder.setItems(Arrays.asList(purchaseOrderItem));

        PurchaseOrderItemDTO itemDTO = new PurchaseOrderItemDTO();
        itemDTO.setProductId(1L);
        itemDTO.setQuantityOrdered(100);
        itemDTO.setPrice(BigDecimal.valueOf(50.00));

        createDTO = new CreatePurchaseOrderDTO();
        createDTO.setSupplierId(1L);
        createDTO.setExpectedDeliveryDate(LocalDate.now().plusDays(7));
        createDTO.setItems(Arrays.asList(itemDTO));

        purchaseOrderDTO = new PurchaseOrderDTO();
        purchaseOrderDTO.setId(1L);
        purchaseOrderDTO.setPoNumber("PO-12345678");
    }

    @Test
    void createPurchaseOrder_Success() {
        when(supplierRepo.findById(1L)).thenReturn(Optional.of(supplier));
        when(productRepo.findById(1L)).thenReturn(Optional.of(product));
        when(poRepo.save(any(PurchaseOrder.class))).thenReturn(purchaseOrder);
        when(mapper.toDto(any(PurchaseOrder.class))).thenReturn(purchaseOrderDTO);

        PurchaseOrderDTO result = purchaseOrderService.createPurchaseOrder(createDTO);

        assertNotNull(result);
        assertEquals("PO-12345678", result.getPoNumber());
        verify(supplierRepo).findById(1L);
        verify(productRepo).findById(1L);
        verify(poRepo).save(any(PurchaseOrder.class));
    }

    @Test
    void createPurchaseOrder_SupplierNotFound_ThrowsException() {
        when(supplierRepo.findById(999L)).thenReturn(Optional.empty());

        CreatePurchaseOrderDTO invalidDTO = new CreatePurchaseOrderDTO();
        invalidDTO.setSupplierId(999L);
        invalidDTO.setExpectedDeliveryDate(LocalDate.now());
        invalidDTO.setItems(Arrays.asList());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> purchaseOrderService.createPurchaseOrder(invalidDTO));

        assertTrue(exception.getReason().contains("Supplier not found"));
        verify(poRepo, never()).save(any());
    }

    @Test
    void createPurchaseOrder_ProductNotFound_ThrowsException() {
        when(supplierRepo.findById(1L)).thenReturn(Optional.of(supplier));
        when(productRepo.findById(999L)).thenReturn(Optional.empty());

        PurchaseOrderItemDTO invalidItemDTO = new PurchaseOrderItemDTO();
        invalidItemDTO.setProductId(999L);
        invalidItemDTO.setQuantityOrdered(100);
        invalidItemDTO.setPrice(BigDecimal.valueOf(50.00));

        CreatePurchaseOrderDTO invalidDTO = new CreatePurchaseOrderDTO();
        invalidDTO.setSupplierId(1L);
        invalidDTO.setExpectedDeliveryDate(LocalDate.now());
        invalidDTO.setItems(Arrays.asList(invalidItemDTO));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> purchaseOrderService.createPurchaseOrder(invalidDTO));

        assertTrue(exception.getReason().contains("Product not found"));
        verify(poRepo, never()).save(any());
    }

    @Test
    void receivePurchaseOrder_Success() {
        ReceivePurchaseOrderDTO receiveDTO = new ReceivePurchaseOrderDTO();
        receiveDTO.setPurchaseOrderItemId(1L);
        receiveDTO.setQuantityReceived(50);

        when(poItemRepo.findById(1L)).thenReturn(Optional.of(purchaseOrderItem));
        when(poItemRepo.save(any(PurchaseOrderItem.class))).thenReturn(purchaseOrderItem);
        when(poRepo.save(any(PurchaseOrder.class))).thenReturn(purchaseOrder);
        when(mapper.toDto(any(PurchaseOrder.class))).thenReturn(purchaseOrderDTO);

        PurchaseOrderDTO result = purchaseOrderService.receivePurchaseOrder(receiveDTO);

        assertNotNull(result);
        verify(poItemRepo).findById(1L);
        verify(poItemRepo).save(any(PurchaseOrderItem.class));
    }

    @Test
    void receivePurchaseOrder_ItemNotFound_ThrowsException() {
        ReceivePurchaseOrderDTO receiveDTO = new ReceivePurchaseOrderDTO();
        receiveDTO.setPurchaseOrderItemId(999L);
        receiveDTO.setQuantityReceived(50);

        when(poItemRepo.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> purchaseOrderService.receivePurchaseOrder(receiveDTO));

        assertTrue(exception.getReason().contains("Purchase order item not found"));
        verify(poItemRepo, never()).save(any());
    }

    @Test
    void receivePurchaseOrder_NegativeQuantity_ThrowsException() {
        ReceivePurchaseOrderDTO receiveDTO = new ReceivePurchaseOrderDTO();
        receiveDTO.setPurchaseOrderItemId(1L);
        receiveDTO.setQuantityReceived(-10);

        when(poItemRepo.findById(1L)).thenReturn(Optional.of(purchaseOrderItem));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> purchaseOrderService.receivePurchaseOrder(receiveDTO));

        assertTrue(exception.getReason().contains("Quantity to receive must be positive"));
        verify(poItemRepo, never()).save(any());
    }

    @Test
    void receivePurchaseOrder_ZeroQuantity_ThrowsException() {
        ReceivePurchaseOrderDTO receiveDTO = new ReceivePurchaseOrderDTO();
        receiveDTO.setPurchaseOrderItemId(1L);
        receiveDTO.setQuantityReceived(0);

        when(poItemRepo.findById(1L)).thenReturn(Optional.of(purchaseOrderItem));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> purchaseOrderService.receivePurchaseOrder(receiveDTO));

        assertTrue(exception.getReason().contains("Quantity to receive must be positive"));
        verify(poItemRepo, never()).save(any());
    }

    @Test
    void receivePurchaseOrder_ExceedsOrderedQuantity_ThrowsException() {
        ReceivePurchaseOrderDTO receiveDTO = new ReceivePurchaseOrderDTO();
        receiveDTO.setPurchaseOrderItemId(1L);
        receiveDTO.setQuantityReceived(150);

        when(poItemRepo.findById(1L)).thenReturn(Optional.of(purchaseOrderItem));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> purchaseOrderService.receivePurchaseOrder(receiveDTO));

        assertTrue(exception.getReason().contains("Cannot receive more than ordered quantity"));
        verify(poItemRepo, never()).save(any());
    }

    @Test
    void receivePurchaseOrder_PartialReceived_ExceedsRemaining_ThrowsException() {
        purchaseOrderItem.setQuantityReceived(80);

        ReceivePurchaseOrderDTO receiveDTO = new ReceivePurchaseOrderDTO();
        receiveDTO.setPurchaseOrderItemId(1L);
        receiveDTO.setQuantityReceived(30);

        when(poItemRepo.findById(1L)).thenReturn(Optional.of(purchaseOrderItem));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> purchaseOrderService.receivePurchaseOrder(receiveDTO));

        assertTrue(exception.getReason().contains("Cannot receive more than ordered quantity"));
        verify(poItemRepo, never()).save(any());
    }

    @Test
    void cancelPurchaseOrder_Success() {
        when(poRepo.findById(1L)).thenReturn(Optional.of(purchaseOrder));
        when(poRepo.save(any(PurchaseOrder.class))).thenReturn(purchaseOrder);
        when(mapper.toDto(any(PurchaseOrder.class))).thenReturn(purchaseOrderDTO);

        PurchaseOrderDTO result = purchaseOrderService.cancelPurchaseOrder(1L);

        assertNotNull(result);
        verify(poRepo).findById(1L);
        verify(poRepo).save(any(PurchaseOrder.class));
    }

    @Test
    void cancelPurchaseOrder_NotFound_ThrowsException() {
        when(poRepo.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> purchaseOrderService.cancelPurchaseOrder(999L));

        assertTrue(exception.getReason().contains("Purchase order not found"));
        verify(poRepo, never()).save(any());
    }

    @Test
    void cancelPurchaseOrder_AlreadyReceived_ThrowsException() {
        purchaseOrder.setStatus(PurchaseOrderStatus.RECEIVED);
        when(poRepo.findById(1L)).thenReturn(Optional.of(purchaseOrder));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> purchaseOrderService.cancelPurchaseOrder(1L));

        assertTrue(exception.getReason().contains("Cannot cancel a purchase order that has been approved or received"));
        verify(poRepo, never()).save(any());
    }

    @Test
    void cancelPurchaseOrder_AlreadyApproved_ThrowsException() {
        purchaseOrder.setStatus(PurchaseOrderStatus.APPROVED);
        when(poRepo.findById(1L)).thenReturn(Optional.of(purchaseOrder));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> purchaseOrderService.cancelPurchaseOrder(1L));

        assertTrue(exception.getReason().contains("Cannot cancel a purchase order that has been approved or received"));
        verify(poRepo, never()).save(any());
    }

    @Test
    void getPurchaseOrderById_Success() {
        when(poRepo.findById(1L)).thenReturn(Optional.of(purchaseOrder));
        when(mapper.toDto(any(PurchaseOrder.class))).thenReturn(purchaseOrderDTO);

        PurchaseOrderDTO result = purchaseOrderService.getPurchaseOrderById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(poRepo).findById(1L);
    }

    @Test
    void getPurchaseOrderById_NotFound_ThrowsException() {
        when(poRepo.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> purchaseOrderService.getPurchaseOrderById(999L));

        assertTrue(exception.getReason().contains("Purchase order not found"));
        verify(poRepo).findById(999L);
    }
}
