package com.example.demo.service;

import com.example.demo.dto.CarrierDTO;
import com.example.demo.enums.CarrierStatus;
import com.example.demo.mapper.CarrierMapper;
import com.example.demo.model.Carrier;
import com.example.demo.repository.CarrierRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarrierService {

    private final CarrierRepository carrierRepo;
    private final CarrierMapper mapper;

    public CarrierService(CarrierRepository carrierRepo, CarrierMapper mapper) {
        this.carrierRepo = carrierRepo;
        this.mapper = mapper;
    }

    @Transactional
    public CarrierDTO createCarrier(CarrierDTO dto) {
        // Check code uniqueness
        if (carrierRepo.existsByCode(dto.getCode())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Carrier with code " + dto.getCode() + " already exists");
        }

        Carrier carrier = mapper.toEntity(dto);
        Carrier savedCarrier = carrierRepo.save(carrier);
        return mapper.toDto(savedCarrier);
    }

    @Transactional(readOnly = true)
    public CarrierDTO getCarrierById(Long id) {
        Carrier carrier = carrierRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Carrier not found with ID: " + id));
        return mapper.toDto(carrier);
    }

    @Transactional(readOnly = true)
    public CarrierDTO getCarrierByCode(String code) {
        Carrier carrier = carrierRepo.findByCode(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Carrier not found with code: " + code));
        return mapper.toDto(carrier);
    }

    @Transactional(readOnly = true)
    public List<CarrierDTO> getAllCarriers() {
        return carrierRepo.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CarrierDTO> getCarriersByStatus(CarrierStatus status) {
        return carrierRepo.findByStatus(status).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CarrierDTO updateCarrier(Long id, CarrierDTO dto) {
        Carrier carrier = carrierRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Carrier not found with ID: " + id));

        // Check code uniqueness if changed
        if (!carrier.getCode().equals(dto.getCode()) && carrierRepo.existsByCode(dto.getCode())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Carrier with code " + dto.getCode() + " already exists");
        }

        carrier.setCode(dto.getCode());
        carrier.setName(dto.getName());
        carrier.setContactInfo(dto.getContactInfo());
        carrier.setStatus(dto.getStatus());

        Carrier updatedCarrier = carrierRepo.save(carrier);
        return mapper.toDto(updatedCarrier);
    }

    @Transactional
    public void updateCarrierStatus(Long id, CarrierStatus status) {
        Carrier carrier = carrierRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Carrier not found with ID: " + id));

        carrier.setStatus(status);
        carrierRepo.save(carrier);
    }
}
