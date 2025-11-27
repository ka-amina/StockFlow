package com.example.demo.model;

import com.example.demo.enums.CarrierStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CarrierTest {

    @Test
    void builder_ShouldCreateCarrierWithAllFields() {
        // ARRANGE
        LocalDateTime now = LocalDateTime.now();

        // ACT
        Carrier carrier = Carrier.builder()
                .id(1L)
                .code("CARR-001")
                .name("Test Carrier")
                .contactInfo("test@carrier.com")
                .status(CarrierStatus.ACTIVE)
                .createdAt(now)
                .build();

        // ASSERT
        assertEquals(1L, carrier.getId());
        assertEquals("CARR-001", carrier.getCode());
        assertEquals("Test Carrier", carrier.getName());
        assertEquals("test@carrier.com", carrier.getContactInfo());
        assertEquals(CarrierStatus.ACTIVE, carrier.getStatus());
        assertEquals(now, carrier.getCreatedAt());
    }

    @Test
    void builder_ShouldSetStatusToActiveByDefault() {
        // ACT
        Carrier carrier = Carrier.builder()
                .code("CARR-001")
                .name("Test Carrier")
                .build();

        // ASSERT
        assertEquals(CarrierStatus.ACTIVE, carrier.getStatus());
    }

    @Test
    void onCreate_ShouldSetCreatedAtTimestamp() {
        // ARRANGE
        Carrier carrier = new Carrier();
        carrier.setCode("CARR-001");
        carrier.setName("Test Carrier");

        // ACT
        carrier.onCreate();

        // ASSERT
        assertNotNull(carrier.getCreatedAt());
        assertTrue(carrier.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void setters_ShouldUpdateFields() {
        // ARRANGE
        Carrier carrier = new Carrier();
        LocalDateTime now = LocalDateTime.now();

        // ACT
        carrier.setId(1L);
        carrier.setCode("CARR-002");
        carrier.setName("Updated Carrier");
        carrier.setContactInfo("updated@carrier.com");
        carrier.setStatus(CarrierStatus.INACTIVE);
        carrier.setCreatedAt(now);

        // ASSERT
        assertEquals(1L, carrier.getId());
        assertEquals("CARR-002", carrier.getCode());
        assertEquals("Updated Carrier", carrier.getName());
        assertEquals("updated@carrier.com", carrier.getContactInfo());
        assertEquals(CarrierStatus.INACTIVE, carrier.getStatus());
        assertEquals(now, carrier.getCreatedAt());
    }

    @Test
    void allArgsConstructor_ShouldCreateCarrierWithAllFields() {
        // ARRANGE
        LocalDateTime now = LocalDateTime.now();

        // ACT
        Carrier carrier = new Carrier(1L, "CARR-001", "Test Carrier", 
                "test@carrier.com", CarrierStatus.ACTIVE, now);

        // ASSERT
        assertEquals(1L, carrier.getId());
        assertEquals("CARR-001", carrier.getCode());
        assertEquals("Test Carrier", carrier.getName());
        assertEquals("test@carrier.com", carrier.getContactInfo());
        assertEquals(CarrierStatus.ACTIVE, carrier.getStatus());
        assertEquals(now, carrier.getCreatedAt());
    }
}
