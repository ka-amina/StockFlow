package com.example.demo.service;

import com.example.demo.dto.ProductDTO;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock private ProductRepository productRepository;
    @Mock private ProductMapper productMapper;

    @InjectMocks private ProductService productService;

    @Test
    void createProduct_Success() {
        // Given
        ProductDTO request = new ProductDTO();
        request.setSku("SKU-001");
        request.setName("Test Product");
        request.setOriginalPrice(BigDecimal.valueOf(100));
        request.setProfit(BigDecimal.valueOf(20));

        Product product = new Product();
        product.setId(1L);
        product.setSku("SKU-001");

        when(productRepository.existsBySku("SKU-001")).thenReturn(false);
        when(productMapper.toEntity(request)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toDto(product)).thenReturn(request);

        // When
        ProductDTO result = productService.createProduct(request);

        // Then
        assertNotNull(result);
        verify(productRepository).save(product);
    }

    @Test
    void createProduct_DuplicateSku() {
        // Given
        ProductDTO request = new ProductDTO();
        request.setSku("SKU-001");

        when(productRepository.existsBySku("SKU-001")).thenReturn(true);

        // When & Then
        assertThrows(ResponseStatusException.class, () ->
                productService.createProduct(request));
    }

    @Test
    void getProductBySku_Success() {
        // Given
        Product product = new Product();
        product.setSku("SKU-001");

        when(productRepository.findBySku("SKU-001")).thenReturn(Optional.of(product));
        when(productMapper.toDto(product)).thenReturn(new ProductDTO());

        // When
        ProductDTO result = productService.getProductBySku("SKU-001");

        // Then
        assertNotNull(result);
    }

    @Test
    void getProductBySku_NotFound() {
        // Given
        when(productRepository.findBySku("INVALID")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResponseStatusException.class, () ->
                productService.getProductBySku("INVALID"));
    }

    @Test
    void activateProduct_Success() {
        // Given
        Product product = new Product();
        product.setId(1L);
        product.setActive(false);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toDto(product)).thenReturn(new ProductDTO());

        // When
        ProductDTO result = productService.activateProduct(1L);

        // Then
        assertTrue(product.isActive());
        verify(productRepository).save(product);
    }

    @Test
    void deactivateProduct_Success() {
        // Given
        Product product = new Product();
        product.setId(1L);
        product.setActive(true);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toDto(product)).thenReturn(new ProductDTO());

        // When
        ProductDTO result = productService.deactivateProduct(1L);

        // Then
        assertFalse(product.isActive());
        verify(productRepository).save(product);
    }

    @Test
    void getProducts_Success() {
        // Given
        Product product1 = new Product();
        Product product2 = new Product();
        
        when(productRepository.findAll()).thenReturn(List.of(product1, product2));
        when(productMapper.toDto(any())).thenReturn(new ProductDTO());

        // When
        List<ProductDTO> result = productService.getProducts();

        // Then
        assertEquals(2, result.size());
    }
}
