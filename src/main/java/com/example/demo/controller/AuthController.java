package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.AuthResponseDTO;
import com.example.demo.dto.LoginDTO;
import com.example.demo.model.User;
import com.example.demo.service.AuthService;
import com.example.demo.utils.JwtUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> login(
            @Valid @RequestBody LoginDTO request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail() , request.getPassword()));
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);
        
        AuthResponseDTO response = AuthResponseDTO.builder()
                .token(token)
                .email(userDetails.getUsername())
                .build();
        
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
