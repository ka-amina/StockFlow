package com.example.demo.service;

import com.example.demo.dto.UserDTO;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepo;

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Transactional
    public UserDTO createUser(UserDTO dto) {
        // Check if email already exists
        if (userRepo.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "User with email " + dto.getEmail() + " already exists");
        }

        User user = User.builder()
                .email(dto.getEmail())
                .passwordHash(dto.getPassword()) // Store password as-is for now (no Spring Security yet)
                .role(dto.getRole())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .build();

        User savedUser = userRepo.save(user);
        return toDto(savedUser);
    }

    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "User not found with ID: " + id));
        return toDto(user);
    }

    @Transactional(readOnly = true)
    public UserDTO getUserByEmail(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "User not found with email: " + email));
        return toDto(user);
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepo.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getUsersByRole(com.example.demo.enums.Role role) {
        return userRepo.findByRole(role).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDTO updateUser(Long id, UserDTO dto) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "User not found with ID: " + id));

        // Check email uniqueness if changed
        if (!user.getEmail().equals(dto.getEmail()) && userRepo.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "User with email " + dto.getEmail() + " already exists");
        }

        user.setEmail(dto.getEmail());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPasswordHash(dto.getPassword());
        }
        user.setRole(dto.getRole());
        if (dto.getActive() != null) {
            user.setActive(dto.getActive());
        }

        User updatedUser = userRepo.save(user);
        return toDto(updatedUser);
    }

    @Transactional
    public void deactivateUser(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "User not found with ID: " + id));

        user.setActive(false);
        userRepo.save(user);
    }

    private UserDTO toDto(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(null) // Don't return password
                .role(user.getRole())
                .active(user.getActive())
                .build();
    }
}
