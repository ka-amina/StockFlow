package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.ProductDTO;
import com.example.demo.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @Test
    void getProducts_Success() {
        // ARRANGE
        ProductDTO product1 = new ProductDTO();
        product1.setId(1L);
        product1.setSku("SKU-001");
        product1.setName("Product 1");

        ProductDTO product2 = new ProductDTO();
        product2.setId(2L);
        product2.setSku("SKU-002");
        product2.setName("Product 2");

        List<ProductDTO> products = Arrays.asList(product1, product2);
        when(productService.getProducts()).thenReturn(products);

        // ACT
        ResponseEntity<ApiResponse<List<ProductDTO>>> response = productController.getProducts();

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Products retrieved successfully", response.getBody().getMessage());
        assertEquals(2, response.getBody().getData().size());
        verify(productService, times(1)).getProducts();
    }

    @Test
    void getProductBySku_Success() {
        // ARRANGE
        ProductDTO product = new ProductDTO();
        product.setId(1L);
        product.setSku("SKU-001");
        product.setName("Test Product");

        when(productService.getProductBySku("SKU-001")).thenReturn(product);

        // ACT
        ResponseEntity<ApiResponse<ProductDTO>> response = productController.getProductBySku("SKU-001");

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Product found", response.getBody().getMessage());
        assertEquals("SKU-001", response.getBody().getData().getSku());
        verify(productService, times(1)).getProductBySku("SKU-001");
    }

    @Test
    void updateProduct_Success() {
        // ARRANGE
        ProductDTO requestDto = new ProductDTO();
        requestDto.setSku("SKU-001");
        requestDto.setName("Updated Product");
        requestDto.setOriginalPrice(BigDecimal.valueOf(150));
        requestDto.setProfit(BigDecimal.valueOf(30));

        ProductDTO updatedDto = new ProductDTO();
        updatedDto.setId(1L);
        updatedDto.setSku("SKU-001");
        updatedDto.setName("Updated Product");

        when(productService.updateProduct(anyLong(), any(ProductDTO.class))).thenReturn(updatedDto);

        // ACT
        ResponseEntity<ApiResponse<ProductDTO>> response = productController.updateProduct(1L, requestDto);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Product updated successfully", response.getBody().getMessage());
        assertEquals("Updated Product", response.getBody().getData().getName());
        verify(productService, times(1)).updateProduct(1L, requestDto);
    }

    @Test
    void activateProduct_Success() {
        // ARRANGE
        ProductDTO activatedDto = new ProductDTO();
        activatedDto.setId(1L);
        activatedDto.setSku("SKU-001");
        activatedDto.setActive(true);

        when(productService.activateProduct(1L)).thenReturn(activatedDto);

        // ACT
        ResponseEntity<ApiResponse<ProductDTO>> response = productController.activateProduct(1L);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Product activated successfully", response.getBody().getMessage());
        assertTrue(response.getBody().getData().isActive());
        verify(productService, times(1)).activateProduct(1L);
    }

    @Test
    void deactivateProduct_Success() {
        // ARRANGE
        ProductDTO deactivatedDto = new ProductDTO();
        deactivatedDto.setId(1L);
        deactivatedDto.setSku("SKU-001");
        deactivatedDto.setActive(false);

        when(productService.deactivateProduct(1L)).thenReturn(deactivatedDto);

        // ACT
        ResponseEntity<ApiResponse<ProductDTO>> response = productController.deactivateProduct(1L);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Product deactivated successfully", response.getBody().getMessage());
        assertFalse(response.getBody().getData().isActive());
        verify(productService, times(1)).deactivateProduct(1L);
    }
}
