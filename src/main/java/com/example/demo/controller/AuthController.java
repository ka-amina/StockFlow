package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.AuthResponseDTO;
import com.example.demo.dto.LoginDTO;
import com.example.demo.model.User;
import com.example.demo.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> login(
            @Valid @RequestBody LoginDTO dto,
            HttpSession session) {
        AuthResponseDTO response = authService.login(dto, session);
        return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpSession session) {
        authService.logout(session);
        return ResponseEntity.ok(ApiResponse.success(null, "Logout successful"));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> getCurrentUser(HttpSession session) {
        User user = authService.getCurrentUser(session);
        
        AuthResponseDTO response = AuthResponseDTO.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .message("Current user retrieved")
                .build();
        
        return ResponseEntity.ok(ApiResponse.success(response, "Current user retrieved"));
    }

    @GetMapping("/check")
    public ResponseEntity<ApiResponse<Boolean>> checkAuthentication(HttpSession session) {
        boolean isAuthenticated = authService.isAuthenticated(session);
        return ResponseEntity.ok(ApiResponse.success(isAuthenticated, 
                isAuthenticated ? "Authenticated" : "Not authenticated"));
    }
}
