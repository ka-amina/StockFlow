package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.ClientDTO;
import com.example.demo.model.Client;
import com.example.demo.model.User;
import com.example.demo.repository.ClientRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;

    public ClientController(ClientService clientService, UserRepository userRepository, ClientRepository clientRepository) {
        this.clientService = clientService;
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ClientDTO>> createClient(@Valid @RequestBody ClientDTO dto) {
        ClientDTO createdClient = clientService.createClient(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdClient, "Client created successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<ApiResponse<ClientDTO>> getClientById(@PathVariable Long id, Authentication authentication) {
        // Extract email from JWT
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        
        // Check if user is a CLIENT
        boolean isClient = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENT"));
        
        if (isClient) {
            // CLIENT users can only see their own client info
            Client userClient = clientRepository.findByUser(user).orElse(null);
            if (userClient == null || !userClient.getId().equals(id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        
        ClientDTO client = clientService.getClientById(id);
        return ResponseEntity.ok(ApiResponse.success(client, "Client retrieved successfully"));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ClientDTO>>> getAllClients() {
        List<ClientDTO> clients = clientService.getAllClients();
        return ResponseEntity.ok(ApiResponse.success(clients, "All clients retrieved successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ClientDTO>> updateClient(
            @PathVariable Long id,
            @Valid @RequestBody ClientDTO dto) {
        ClientDTO updatedClient = clientService.updateClient(id, dto);
        return ResponseEntity.ok(ApiResponse.success(updatedClient, "Client updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivateClient(@PathVariable Long id) {
        clientService.deactivateClient(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Client deactivated successfully"));
    }
}
