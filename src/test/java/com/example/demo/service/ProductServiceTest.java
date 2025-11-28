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

    @Test
    void createProduct_NullSku_ThrowsException() {
        ProductDTO request = new ProductDTO();
        request.setSku(null);
        request.setName("Test");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> productService.createProduct(request));

        assertTrue(exception.getReason().contains("SKU must not be blank"));
        verify(productRepository, never()).save(any());
    }

    @Test
    void createProduct_BlankSku_ThrowsException() {
        ProductDTO request = new ProductDTO();
        request.setSku("   ");
        request.setName("Test");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> productService.createProduct(request));

        assertTrue(exception.getReason().contains("SKU must not be blank"));
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateProduct_Success() {
        Product product = new Product();
        product.setId(1L);
        product.setSku("SKU-001");

        ProductDTO updateDTO = new ProductDTO();
        updateDTO.setSku("SKU-001");
        updateDTO.setName("Updated");

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.findBySku("SKU-001")).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toDto(product)).thenReturn(updateDTO);

        ProductDTO result = productService.updateProduct(1L, updateDTO);

        assertNotNull(result);
        verify(productRepository).save(product);
    }

    @Test
    void updateProduct_NotFound_ThrowsException() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> productService.updateProduct(999L, new ProductDTO()));

        verify(productRepository, never()).save(any());
    }

    @Test
    void updateProduct_DuplicateSku_ThrowsException() {
        Product product1 = new Product();
        product1.setId(1L);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setSku("SKU-002");

        ProductDTO updateDTO = new ProductDTO();
        updateDTO.setSku("SKU-002");

        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.findBySku("SKU-002")).thenReturn(Optional.of(product2));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> productService.updateProduct(1L, updateDTO));

        assertTrue(exception.getReason().contains("SKU already exists"));
        verify(productRepository, never()).save(any());
    }

    @Test
    void setActive_NotFound_ThrowsException() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> productService.setActive(999L, true));

        verify(productRepository, never()).save(any());
    }

    @Test
    void activateProduct_NotFound_ThrowsException() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> productService.activateProduct(999L));
    }

    @Test
    void deactivateProduct_NotFound_ThrowsException() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> productService.deactivateProduct(999L));
    }

    @Test
    void getProducts_EmptyList() {
        when(productRepository.findAll()).thenReturn(List.of());

        List<ProductDTO> result = productService.getProducts();

        assertTrue(result.isEmpty());
    }
}
