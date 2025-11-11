package com.example.demo.service;

import com.example.demo.dto.WarehouseDTO;
import com.example.demo.mapper.WarehouseMapper;
import com.example.demo.model.Warehouse;
import com.example.demo.repository.WarehouseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WarehouseService {

    private final WarehouseRepository repo;
    private final WarehouseMapper mapper;

    public WarehouseService(WarehouseRepository repo, WarehouseMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Transactional
    public WarehouseDTO createWarehouse(WarehouseDTO dto) {
        if (dto.getCode() == null || dto.getCode().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Code must not be blank");
        }
        if (repo.existsByCode(dto.getCode())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Code already exists");
        }
        Warehouse entity = mapper.toEntity(dto);
        Warehouse saved = repo.save(entity);
        return mapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<WarehouseDTO> getAllWarehouses() {
        List<Warehouse> entities = repo.findAll();
        return entities.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public WarehouseDTO getWarehouseById(Long id) {
        Warehouse entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Warehouse not found"));
        return mapper.toDto(entity);
    }

    @Transactional
    public WarehouseDTO updateWarehouse(Long id, WarehouseDTO dto) {
        Warehouse entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Warehouse not found"));

        if (dto.getCode() != null && !dto.getCode().isBlank()) {
            repo.findByCode(dto.getCode()).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Code already exists");
                }
            });
        }

        mapper.updateEntityFromDto(dto, entity);
        Warehouse saved = repo.save(entity);
        return mapper.toDto(saved);
    }

    @Transactional
    public WarehouseDTO setActive(Long id, boolean active) {
        Warehouse entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Warehouse not found"));
        entity.setActive(active);
        Warehouse saved = repo.save(entity);
        return mapper.toDto(saved);
    }

    @Transactional
    public WarehouseDTO activateWarehouse(Long id) {
        return setActive(id, true);
    }

    @Transactional
    public WarehouseDTO deactivateWarehouse(Long id) {
        return setActive(id, false);
    }

    @Transactional
    public void deleteWarehouse(Long id) {
        Warehouse entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Warehouse not found"));
        // TODO: Add business logic to check if warehouse has inventory or orders before deletion
        repo.delete(entity);
    }
}