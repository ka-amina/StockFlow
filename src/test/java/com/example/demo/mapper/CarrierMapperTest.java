package com.example.demo.mapper;

import com.example.demo.dto.CarrierDTO;
import com.example.demo.enums.CarrierStatus;
import com.example.demo.model.Carrier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CarrierMapperTest {

    private CarrierMapper carrierMapper;
    private Carrier carrier;

    @BeforeEach
    void setUp() {
        carrierMapper = Mappers.getMapper(CarrierMapper.class);
        
        carrier = Carrier.builder()
                .id(1L)
                .code("CARR-001")
                .name("Test Carrier")
                .contactInfo("test@carrier.com")
                .status(CarrierStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void toDto_ShouldMapAllFields() {
        // ARRANGE - Already set up in beforeEach

        // ACT
        CarrierDTO dto = carrierMapper.toDto(carrier);

        // ASSERT
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("CARR-001", dto.getCode());
        assertEquals("Test Carrier", dto.getName());
        assertEquals("test@carrier.com", dto.getContactInfo());
        assertEquals(CarrierStatus.ACTIVE, dto.getStatus());
    }

    @Test
    void toDto_WhenNullContactInfo_ShouldHandleGracefully() {
        // ARRANGE
        carrier.setContactInfo(null);

        // ACT
        CarrierDTO dto = carrierMapper.toDto(carrier);

        // ASSERT
        assertNotNull(dto);
        assertNull(dto.getContactInfo());
    }

    @Test
    void toEntity_ShouldMapAllFieldsExceptIgnored() {
        // ARRANGE
        CarrierDTO dto = new CarrierDTO();
        dto.setId(999L); // Should be ignored
        dto.setCode("CARR-002");
        dto.setName("New Carrier");
        dto.setContactInfo("new@carrier.com");
        dto.setStatus(CarrierStatus.INACTIVE);

        // ACT
        Carrier entity = carrierMapper.toEntity(dto);

        // ASSERT
        assertNotNull(entity);
        assertNull(entity.getId()); // ID is ignored
        assertNull(entity.getCreatedAt()); // createdAt is ignored
        assertEquals("CARR-002", entity.getCode());
        assertEquals("New Carrier", entity.getName());
        assertEquals("new@carrier.com", entity.getContactInfo());
        assertEquals(CarrierStatus.INACTIVE, entity.getStatus());
    }

    @Test
    void toDto_WithInactiveStatus_ShouldMapCorrectly() {
        // ARRANGE
        carrier.setStatus(CarrierStatus.INACTIVE);

        // ACT
        CarrierDTO dto = carrierMapper.toDto(carrier);

        // ASSERT
        assertNotNull(dto);
        assertEquals(CarrierStatus.INACTIVE, dto.getStatus());
    }
}
