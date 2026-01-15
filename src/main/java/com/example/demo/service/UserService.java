package com.example.demo.service;

import com.example.demo.dto.AuthResponseDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.mapper.UsersMapper;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.UserRoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@AllArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepo;
    private final UsersMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleRepository userRoleRepository;


    @Transactional
    public AuthResponseDTO createUser(UserDTO dto) {
        // Check if email already exists
        if (userRepo.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "User with email " + dto.getEmail() + " already exists");
        }

        User user = userMapper.toEntity(dto);

        if (dto.getRole() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role ID is required");
        }
        
        Role role = userRoleRepository.findById(dto.getRole())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role " + dto.getRole() + " not found"));
        user.setRole(role);
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));

        User savedUser = userRepo.save(user);
        return userMapper.toAuthResponseDTO(savedUser);
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID is required");
        }
        return userRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "User not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "User not found with email: " + email));
    }

//    @Transactional(readOnly = true)
//    public List<UserDTO> getAllUsers() {
//        return userRepo.findAll().stream()
//                .map(this::toDto)
//                .collect(Collectors.toList());
//    }
//
//    @Transactional(readOnly = true)
//    public List<UserDTO> getUsersByRole(com.example.demo.enums.Role role) {
//        return userRepo.findByRole(role).stream()
//                .map(this::toDto)
//                .collect(Collectors.toList());
//    }

//    @Transactional
//    public UserDTO updateUser(Long id, UserDTO dto) {
//        User user = userRepo.findById(id)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
//                        "User not found with ID: " + id));
//
//        // Check email uniqueness if changed
//        if (!user.getEmail().equals(dto.getEmail()) && userRepo.existsByEmail(dto.getEmail())) {
//            throw new ResponseStatusException(HttpStatus.CONFLICT,
//                    "User with email " + dto.getEmail() + " already exists");
//        }
//
//        user.setEmail(dto.getEmail());
//        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
//            user.setPasswordHash(dto.getPassword());
//        }
//        user.setRole(dto.getRole());
//        if (dto.getActive() != null) {
//            user.setActive(dto.getActive());
//        }
//
//        User updatedUser = userRepo.save(user);
//        return toDto(updatedUser);
//    }
//
//    @Transactional
//    public void deactivateUser(Long id) {
//        User user = userRepo.findById(id)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
//                        "User not found with ID: " + id));
//
//        user.setActive(false);
//        userRepo.save(user);
//    }

//   user private UserDTO toDto(User user) {
//        return UserDTO.builder()
//                .id(user.getId())
//                .email(user.getEmail())
//                .password(null) // Don't return password
//                .active(user.getActive())
//                .build();
//    }
}
