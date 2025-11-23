package com.example.demo.repository;

import com.example.demo.model.Carrier;
import com.example.demo.enums.CarrierStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarrierRepository extends JpaRepository<Carrier, Long> {
    
    Optional<Carrier> findByCode(String code);
    
    List<Carrier> findByStatus(CarrierStatus status);
    
    boolean existsByCode(String code);
}
