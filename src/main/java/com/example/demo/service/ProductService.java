package com.example.demo.service;

import com.example.demo.dto.ProductDTO;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProductService {

    private final ProductRepository repo;
    private final ProductMapper mapper;

    public ProductService(ProductRepository repo, ProductMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Transactional
    public ProductDTO createProduct(ProductDTO dto) {
        if (dto.getSku() == null || dto.getSku().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "SKU must not be blank");
        }
        if (repo.existsBySku(dto.getSku())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "SKU already exists");
        }
        Product entity = mapper.toEntity(dto);
        Product saved = repo.save(entity);
        return mapper.toDto(saved);
    }

    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO dto) {
        Product entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        if (dto.getSku() != null && !dto.getSku().isBlank()) {
            repo.findBySku(dto.getSku()).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "SKU already exists");
                }
            });
        }

        mapper.updateEntityFromDto(dto, entity);
        Product saved = repo.save(entity);
        return mapper.toDto(saved);
    }

    @Transactional
    public ProductDTO setActive(Long id, boolean active) {
        Product entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        entity.setActive(active);
        Product saved = repo.save(entity);
        return mapper.toDto(saved);
    }

    @Transactional
    public ProductDTO activateProduct(Long id) {
        return setActive(id, true);
    }

    @Transactional
    public ProductDTO deactivateProduct(Long id) {
        return setActive(id, false);
    }
}
