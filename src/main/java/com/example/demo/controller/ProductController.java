package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.ProductDTO;
import com.example.demo.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'CLIENT')")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getProducts() {
        List<ProductDTO> products = service.getProducts();
        ApiResponse<List<ProductDTO>> body = new ApiResponse<>("Products retrieved successfully", products);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/sku/{sku}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'CLIENT')")
    public ResponseEntity<ApiResponse<ProductDTO>> getProductBySku(@PathVariable String sku) {
        ProductDTO product = service.getProductBySku(sku);
        ApiResponse<ProductDTO> body = new ApiResponse<>("Product found", product);
        return ResponseEntity.ok(body);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    public ResponseEntity<ApiResponse<ProductDTO>> createProduct(@Valid @RequestBody ProductDTO dto) {
        ProductDTO created = service.createProduct(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(created.getId()).toUri();
        ApiResponse<ProductDTO> body = new ApiResponse<>("Product created successfully", created);
        return ResponseEntity.created(location).body(body);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    public ResponseEntity<ApiResponse<ProductDTO>> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDTO dto) {
        ProductDTO updated = service.updateProduct(id, dto);
        ApiResponse<ProductDTO> body = new ApiResponse<>("Product updated successfully", updated);
        return ResponseEntity.ok(body);
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductDTO>> activateProduct(@PathVariable Long id) {
        ProductDTO updated = service.activateProduct(id);
        ApiResponse<ProductDTO> body = new ApiResponse<>("Product activated successfully", updated);
        return ResponseEntity.ok(body);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<ProductDTO>> deactivateProduct(@PathVariable Long id) {
        ProductDTO updated = service.deactivateProduct(id);
        ApiResponse<ProductDTO> body = new ApiResponse<>("Product deactivated successfully", updated);
        return ResponseEntity.ok(body);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        service.deactivateProduct(id); // Soft delete by deactivating
        ApiResponse<Void> body = new ApiResponse<>("Product deleted successfully", null);
        return ResponseEntity.ok(body);
    }
}
