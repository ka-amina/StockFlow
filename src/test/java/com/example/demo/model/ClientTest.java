package com.example.demo.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ClientTest {

    @Test
    void builder_ShouldCreateClientWithAllFields() {
        // ARRANGE
        User user = User.builder().id(1L).email("user@test.com").build();
        LocalDateTime now = LocalDateTime.now();

        // ACT
        Client client = Client.builder()
                .id(1L)
                .user(user)
                .name("Test Client")
                .email("client@test.com")
                .phone("+1234567890")
                .address("123 Test St")
                .active(true)
                .createdAt(now)
                .build();

        // ASSERT
        assertEquals(1L, client.getId());
        assertEquals(user, client.getUser());
        assertEquals("Test Client", client.getName());
        assertEquals("client@test.com", client.getEmail());
        assertEquals("+1234567890", client.getPhone());
        assertEquals("123 Test St", client.getAddress());
        assertTrue(client.isActive());
        assertEquals(now, client.getCreatedAt());
    }

    @Test
    void builder_ShouldSetActiveToTrueByDefault() {
        // ACT
        Client client = Client.builder()
                .name("Test Client")
                .email("client@test.com")
                .build();

        // ASSERT
        assertTrue(client.isActive());
    }

    @Test
    void onCreate_ShouldSetCreatedAtTimestamp() {
        // ARRANGE
        Client client = new Client();
        client.setName("Test Client");
        client.setEmail("client@test.com");

        // ACT
        client.onCreate();

        // ASSERT
        assertNotNull(client.getCreatedAt());
        assertTrue(client.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void setters_ShouldUpdateAllFields() {
        // ARRANGE
        Client client = new Client();
        User user = User.builder().id(1L).email("user@test.com").build();

        // ACT
        client.setId(1L);
        client.setUser(user);
        client.setName("Updated Client");
        client.setEmail("updated@test.com");
        client.setPhone("+9876543210");
        client.setAddress("456 New St");
        client.setActive(false);

        // ASSERT
        assertEquals(1L, client.getId());
        assertEquals(user, client.getUser());
        assertEquals("Updated Client", client.getName());
        assertEquals("updated@test.com", client.getEmail());
        assertEquals("+9876543210", client.getPhone());
        assertEquals("456 New St", client.getAddress());
        assertFalse(client.isActive());
    }
}
