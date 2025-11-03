package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.ProductDTO;
import com.example.demo.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductDTO>> createProduct(@Valid @RequestBody ProductDTO dto) {
        ProductDTO created = service.createProduct(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(created.getId()).toUri();
        ApiResponse<ProductDTO> body = new ApiResponse<>("Product created successfully", created);
        return ResponseEntity.created(location).body(body);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDTO>> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDTO dto) {
        ProductDTO updated = service.updateProduct(id, dto);
        ApiResponse<ProductDTO> body = new ApiResponse<>("Product updated successfully", updated);
        return ResponseEntity.ok(body);
    }

    @PatchMapping("/{id}/activate")
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
}
