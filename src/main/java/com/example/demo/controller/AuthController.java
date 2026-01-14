package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.model.RefreshToken;
import com.example.demo.service.CustomUserDetailsService;
import com.example.demo.service.RefreshTokenService;
import com.example.demo.utils.JwtUtil;
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

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final CustomUserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<ApiTokenResponse<TokenResponseDTO>> login(
            @RequestBody LoginDTO request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        String token = jwtUtil.generateAccessToken(userDetails);

        RefreshToken refreshToken = jwtUtil.generateRefreshToken(userDetails);

        TokenResponseDTO tokenResponse = TokenResponseDTO
                .builder()
                .accessToken(token)
                .refreshToken(refreshToken.getToken())
                .expiresIn(15 * 60)
                .tokenType("Bearer")
                .build();
        ApiTokenResponse<TokenResponseDTO> response = ApiTokenResponse.success(tokenResponse , " you token is being retrieved with success : ");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody RefreshTokenRequestDTO request) {
        // Revoke the refresh token
        refreshTokenService.revokeToken(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success(null, "Logout successful"));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> getCurrentUser() {
        // Get current user from security context
        org.springframework.security.core.Authentication authentication = 
            org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        
        String email = authentication.getName();
        // You would fetch user details from database using email
        
        AuthResponseDTO response = AuthResponseDTO.builder()
                .email(email)
                .build();

        return ResponseEntity.ok(ApiResponse.success(response, "Current user retrieved"));
    }

    @GetMapping("/check")
    public ResponseEntity<ApiResponse<Boolean>> checkAuthentication() {
        org.springframework.security.core.Authentication authentication = 
            org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() 
            && !authentication.getPrincipal().equals("anonymousUser");
            
        return ResponseEntity.ok(ApiResponse.success(isAuthenticated,
                isAuthenticated ? "Authenticated" : "Not authenticated"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiTokenResponse<TokenResponseDTO>> refreshToken(@RequestBody RefreshTokenRequestDTO req) {
        // Validate refresh token (throws exception if invalid)
        RefreshToken token = refreshTokenService.validateAndGetToken(req.getRefreshToken());
        
        // Revoke old refresh token (token rotation)
        refreshTokenService.revokeToken(req.getRefreshToken());
        
        // Generate new tokens
        UserDetails userDetails = userDetailsService.loadUserByUsername(token.getUser().getEmail());
        String newAccessToken = jwtUtil.generateAccessToken(userDetails);
        RefreshToken newRefreshToken = jwtUtil.generateRefreshToken(userDetails);
        
        TokenResponseDTO tokenResponse = TokenResponseDTO.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(15 * 60)
                .build();
        
        ApiTokenResponse<TokenResponseDTO> response = ApiTokenResponse.success(tokenResponse, "Token refreshed successfully");
        return ResponseEntity.ok(response);
    }
}
