package com.example.demo.service;

import com.example.demo.dto.SupplierDTO;
import com.example.demo.mapper.SupplierMapper;
import com.example.demo.model.Supplier;
import com.example.demo.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SupplierService {

    private final SupplierRepository repo;
    private final SupplierMapper mapper;

    public SupplierService(SupplierRepository repo, SupplierMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Transactional
    public SupplierDTO createSupplier(SupplierDTO dto) {
        Supplier entity = mapper.toEntity(dto);
        Supplier saved = repo.save(entity);
        return mapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<SupplierDTO> getSuppliers() {
        return repo.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }
}
