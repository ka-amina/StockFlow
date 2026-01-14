package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminUserController {

    private final UserRepository userRepository;

    public AdminUserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllUsers() {
        List<User> users = userRepository.findAll();
        
        List<Map<String, Object>> userDTOs = users.stream()
                .map(user -> {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("id", user.getId());
                    userMap.put("email", user.getEmail());
                    userMap.put("role", user.getRole().getRoleName());
                    userMap.put("active", user.getActive());
                    return userMap;
                })
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success(userDTOs, "Users retrieved successfully"));
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserById(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", user.getId());
        userMap.put("email", user.getEmail());
        userMap.put("role", user.getRole().getRoleName());
        userMap.put("active", user.getActive());
        
        return ResponseEntity.ok(ApiResponse.success(userMap, "User retrieved successfully"));
    }
}
