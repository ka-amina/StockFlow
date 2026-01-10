package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.model.RefreshToken;
import com.example.demo.model.User;
import com.example.demo.service.AuthService;
import com.example.demo.service.CustomUserDetailsService;
import com.example.demo.service.RefreshTokenService;
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

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
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

    @PostMapping("/refresh")
    public ResponseEntity<ApiTokenResponse<TokenResponseDTO>> refreshToken(@RequestBody RefreshTokenRequestDTO req) {
        String refreshToken = req.getRefreshToken();
        RefreshToken token = refreshTokenService.getByToken(refreshToken);

        if (token == null) {
            throw new RuntimeException("Refresh token not found");
        }

        if (token.isRevoked() || token.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token expired or revoked");
        }
        refreshTokenService.revokeToken(req.getRefreshToken());
        UserDetails userDetails = userDetailsService.loadUserByUsername(token.getUser().getEmail());
        String newAccessToken = jwtUtil.generateAccessToken(userDetails);
        RefreshToken newRefreshToken = jwtUtil.generateRefreshToken(userDetails);
        TokenResponseDTO tokenResponse = TokenResponseDTO.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(15 * 60)
                .build();
        ApiTokenResponse<TokenResponseDTO> response = ApiTokenResponse.success(tokenResponse, "your token is this : ");
        return ResponseEntity.ok(response);
    }
}
