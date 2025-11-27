package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.ClientDTO;
import com.example.demo.service.ClientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientControllerTest {

    @Mock
    private ClientService clientService;

    @InjectMocks
    private ClientController clientController;

    @Test
    void createClient_Success() {
        // ARRANGE
        ClientDTO requestDto = new ClientDTO();
        requestDto.setName("Test Client");

        ClientDTO createdDto = new ClientDTO();
        createdDto.setId(1L);
        createdDto.setName("Test Client");

        when(clientService.createClient(any(ClientDTO.class))).thenReturn(createdDto);

        // ACT
        ResponseEntity<ApiResponse<ClientDTO>> response = clientController.createClient(requestDto);

        // ASSERT
        assertNotNull(response);
        assertEquals(201, response.getStatusCodeValue());
        assertEquals("Client created successfully", response.getBody().getMessage());
        verify(clientService, times(1)).createClient(requestDto);
    }

    @Test
    void getClientById_Success() {
        // ARRANGE
        ClientDTO client = new ClientDTO();
        client.setId(1L);
        client.setName("Test Client");

        when(clientService.getClientById(1L)).thenReturn(client);

        // ACT
        ResponseEntity<ApiResponse<ClientDTO>> response = clientController.getClientById(1L);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Client retrieved successfully", response.getBody().getMessage());
        verify(clientService, times(1)).getClientById(1L);
    }

    @Test
    void getAllClients_Success() {
        // ARRANGE
        ClientDTO client1 = new ClientDTO();
        client1.setId(1L);

        ClientDTO client2 = new ClientDTO();
        client2.setId(2L);

        List<ClientDTO> clients = Arrays.asList(client1, client2);
        when(clientService.getAllClients()).thenReturn(clients);

        // ACT
        ResponseEntity<ApiResponse<List<ClientDTO>>> response = clientController.getAllClients();

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().getData().size());
        verify(clientService, times(1)).getAllClients();
    }

    @Test
    void updateClient_Success() {
        // ARRANGE
        ClientDTO requestDto = new ClientDTO();
        requestDto.setName("Updated Client");

        ClientDTO updatedDto = new ClientDTO();
        updatedDto.setId(1L);
        updatedDto.setName("Updated Client");

        when(clientService.updateClient(anyLong(), any(ClientDTO.class))).thenReturn(updatedDto);

        // ACT
        ResponseEntity<ApiResponse<ClientDTO>> response = clientController.updateClient(1L, requestDto);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Client updated successfully", response.getBody().getMessage());
        verify(clientService, times(1)).updateClient(1L, requestDto);
    }

}
