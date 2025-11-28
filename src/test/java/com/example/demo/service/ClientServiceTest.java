package com.example.demo.service;

import com.example.demo.dto.ClientAccountDTO;
import com.example.demo.dto.ClientDTO;
import com.example.demo.dto.RegisterClientDTO;
import com.example.demo.enums.Role;
import com.example.demo.model.Client;
import com.example.demo.model.User;
import com.example.demo.repository.ClientRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ClientService clientService;

    private User user;
    private Client client;
    private ClientDTO clientDTO;
    private RegisterClientDTO registerDTO;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("client@test.com")
                .passwordHash("hashedPassword")
                .role(Role.CLIENT)
                .active(true)
                .build();

        client = Client.builder()
                .id(1L)
                .user(user)
                .name("Test Client")
                .email("client@test.com")
                .phone("+1234567890")
                .address("123 Client St")
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        clientDTO = ClientDTO.builder()
                .id(1L)
                .name("Test Client")
                .email("client@test.com")
                .phone("+1234567890")
                .address("123 Client St")
                .password("password123")
                .active(true)
                .build();

        registerDTO = RegisterClientDTO.builder()
                .name("New Client")
                .email("newclient@test.com")
                .phone("+9876543210")
                .address("456 New St")
                .password("password123")
                .build();
    }

    @Test
    void registerClient_Success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(clientRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        ClientAccountDTO result = clientService.registerClient(registerDTO);

        assertNotNull(result);
        assertEquals("Test Client", result.getName());
        verify(userRepository).existsByEmail("newclient@test.com");
        verify(clientRepository).existsByEmail("newclient@test.com");
        verify(userRepository).save(any(User.class));
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    void registerClient_EmailExistsInUserTable_ThrowsException() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> clientService.registerClient(registerDTO));

        assertTrue(exception.getReason().contains("An account with email"));
        assertTrue(exception.getReason().contains("already exists"));
        verify(userRepository, never()).save(any());
        verify(clientRepository, never()).save(any());
    }

    @Test
    void registerClient_EmailExistsInClientTable_ThrowsException() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(clientRepository.existsByEmail(anyString())).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> clientService.registerClient(registerDTO));

        assertTrue(exception.getReason().contains("Client with email"));
        assertTrue(exception.getReason().contains("already exists"));
        verify(userRepository, never()).save(any());
        verify(clientRepository, never()).save(any());
    }

    @Test
    void createClient_Success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(clientRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        ClientDTO result = clientService.createClient(clientDTO);

        assertNotNull(result);
        assertEquals("Test Client", result.getName());
        verify(userRepository).save(any(User.class));
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    void createClient_EmailExistsInUserTable_ThrowsException() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> clientService.createClient(clientDTO));

        assertTrue(exception.getReason().contains("An account with email"));
        verify(userRepository, never()).save(any());
        verify(clientRepository, never()).save(any());
    }

    @Test
    void createClient_EmailExistsInClientTable_ThrowsException() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(clientRepository.existsByEmail(anyString())).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> clientService.createClient(clientDTO));

        assertTrue(exception.getReason().contains("Client with email"));
        verify(userRepository, never()).save(any());
        verify(clientRepository, never()).save(any());
    }

    @Test
    void getClientById_Success() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        ClientDTO result = clientService.getClientById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Client", result.getName());
        verify(clientRepository).findById(1L);
    }

    @Test
    void getClientById_NotFound_ThrowsException() {
        when(clientRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> clientService.getClientById(999L));

        assertTrue(exception.getReason().contains("Client not found"));
        verify(clientRepository).findById(999L);
    }

    @Test
    void getAllClients_Success() {
        Client client2 = Client.builder()
                .id(2L)
                .name("Second Client")
                .email("client2@test.com")
                .active(true)
                .build();

        when(clientRepository.findAll()).thenReturn(Arrays.asList(client, client2));

        List<ClientDTO> result = clientService.getAllClients();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Test Client", result.get(0).getName());
        assertEquals("Second Client", result.get(1).getName());
        verify(clientRepository).findAll();
    }

    @Test
    void getAllClients_EmptyList_Success() {
        when(clientRepository.findAll()).thenReturn(Collections.emptyList());

        List<ClientDTO> result = clientService.getAllClients();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(clientRepository).findAll();
    }

    @Test
    void updateClient_Success() {
        ClientDTO updateDTO = ClientDTO.builder()
                .name("Updated Client")
                .email("client@test.com")
                .phone("+1111111111")
                .address("789 Updated St")
                .build();

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        ClientDTO result = clientService.updateClient(1L, updateDTO);

        assertNotNull(result);
        verify(clientRepository).findById(1L);
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    void updateClient_NotFound_ThrowsException() {
        when(clientRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> clientService.updateClient(999L, clientDTO));

        assertTrue(exception.getReason().contains("Client not found"));
        verify(clientRepository, never()).save(any());
    }

    @Test
    void updateClient_DuplicateEmail_ThrowsException() {
        ClientDTO updateDTO = ClientDTO.builder()
                .name("Updated Client")
                .email("other@test.com")
                .build();

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(clientRepository.existsByEmail("other@test.com")).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> clientService.updateClient(1L, updateDTO));

        assertTrue(exception.getReason().contains("Client with email"));
        assertTrue(exception.getReason().contains("already exists"));
        verify(clientRepository, never()).save(any());
    }

    @Test
    void updateClient_SameEmail_Success() {
        ClientDTO updateDTO = ClientDTO.builder()
                .name("Updated Name")
                .email("client@test.com")
                .phone("+1111111111")
                .address("New Address")
                .build();

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        ClientDTO result = clientService.updateClient(1L, updateDTO);

        assertNotNull(result);
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    void deactivateClient_Success() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        assertDoesNotThrow(() -> clientService.deactivateClient(1L));

        verify(clientRepository).findById(1L);
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    void deactivateClient_NotFound_ThrowsException() {
        when(clientRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> clientService.deactivateClient(999L));

        assertTrue(exception.getReason().contains("Client not found"));
        verify(clientRepository, never()).save(any());
    }

    @Test
    void deactivateClient_AlreadyInactive_Success() {
        client.setActive(false);
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        assertDoesNotThrow(() -> clientService.deactivateClient(1L));

        verify(clientRepository).save(any(Client.class));
    }
}
