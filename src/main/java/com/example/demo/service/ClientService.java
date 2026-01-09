package com.example.demo.service;

import com.example.demo.dto.ClientDTO;
import com.example.demo.dto.ClientAccountDTO;
import com.example.demo.dto.RegisterClientDTO;
import com.example.demo.model.Client;
import com.example.demo.model.User;
import com.example.demo.model.Roles;
import com.example.demo.repository.ClientRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.UserRoleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientService {

    private final ClientRepository clientRepo;
    private final UserRepository userRepo;
    private final UserRoleRepository userRoleRepository;

    public ClientService(ClientRepository clientRepo, UserRepository userRepo, UserRoleRepository userRoleRepository) {
        this.clientRepo = clientRepo;
        this.userRepo = userRepo;
        this.userRoleRepository = userRoleRepository;
    }

    @Transactional
    public ClientAccountDTO registerClient(RegisterClientDTO dto) {
        // Check if email already exists in User table
        if (userRepo.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "An account with email " + dto.getEmail() + " already exists");
        }

        // Check if email already exists in Client table
        if (clientRepo.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Client with email " + dto.getEmail() + " already exists");
        }

        // Find CLIENT role
        Roles clientRole = userRoleRepository.findByRoleName("CLIENT")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "CLIENT role not found in system"));

        // Create User account with CLIENT role
        User user = User.builder()
                .email(dto.getEmail())
                .passwordHash(dto.getPassword())
                .role(clientRole)
                .active(true)
                .build();

        User savedUser = userRepo.save(user);

        // Create Client profile linked to User
        Client client = Client.builder()
                .user(savedUser)
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .active(true)
                .build();

        Client savedClient = clientRepo.save(client);
        return toAccountDto(savedClient);
    }

    @Transactional
    public ClientDTO createClient(ClientDTO dto) {
        // Check if email already exists in User table
        if (userRepo.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "An account with email " + dto.getEmail() + " already exists");
        }

        // Check if email already exists in Client table
        if (clientRepo.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Client with email " + dto.getEmail() + " already exists");
        }

        // Find CLIENT role
        Roles clientRole = userRoleRepository.findByRoleName("CLIENT")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "CLIENT role not found in system"));

        // Create User account with CLIENT role
        User user = User.builder()
                .email(dto.getEmail())
                .passwordHash(dto.getPassword()) 
                .role(clientRole)
                .active(true)
                .build();

        User savedUser = userRepo.save(user);

        // Create Client profile linked to User
        Client client = Client.builder()
                .user(savedUser)
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .active(true)
                .build();

        Client savedClient = clientRepo.save(client);
        return toDto(savedClient);
    }

    @Transactional(readOnly = true)
    public ClientDTO getClientById(Long id) {
        Client client = clientRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Client not found with ID: " + id));
        return toDto(client);
    }

    @Transactional(readOnly = true)
    public List<ClientDTO> getAllClients() {
        return clientRepo.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ClientDTO updateClient(Long id, ClientDTO dto) {
        Client client = clientRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Client not found with ID: " + id));

        // Check email uniqueness if changed
        if (!client.getEmail().equals(dto.getEmail()) && clientRepo.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Client with email " + dto.getEmail() + " already exists");
        }

        client.setName(dto.getName());
        client.setEmail(dto.getEmail());
        client.setPhone(dto.getPhone());
        client.setAddress(dto.getAddress());

        Client updatedClient = clientRepo.save(client);
        return toDto(updatedClient);
    }

    @Transactional
    public void deactivateClient(Long id) {
        Client client = clientRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Client not found with ID: " + id));

        client.setActive(false);
        clientRepo.save(client);
    }

    private ClientDTO toDto(Client client) {
        return ClientDTO.builder()
                .id(client.getId())
                .name(client.getName())
                .email(client.getEmail())
                .phone(client.getPhone())
                .address(client.getAddress())
                .active(client.isActive())
                .createdAt(client.getCreatedAt())
                .build();
    }

    private ClientAccountDTO toAccountDto(Client client) {
        ClientAccountDTO dto = ClientAccountDTO.builder()
                .id(client.getId())
                .name(client.getName())
                .email(client.getEmail())
                .phone(client.getPhone())
                .address(client.getAddress())
                .active(client.isActive())
                .createdAt(client.getCreatedAt())
                .build();

        if (client.getUser() != null) {
            dto.setUserId(client.getUser().getId());
            dto.setUserEmail(client.getUser().getEmail());
            dto.setUserRole(client.getUser().getRole().getRoleName());
        }

        return dto;
    }
}
