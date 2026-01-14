package com.example.demo.service;


import com.example.demo.exeption.ResourceNotFoundException;
import com.example.demo.exeption.TokenExpiredException;
import com.example.demo.exeption.UnauthorizedException;
import com.example.demo.model.RefreshToken;
import com.example.demo.model.User;
import com.example.demo.repository.JwtTokenRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class RefreshTokenService {
    private final JwtTokenRepository jwtTokenRepository;
    private final UserRepository userRepository;

    public RefreshToken saveRefreshToken(RefreshToken refreshToken, String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        refreshToken.setUser(user);
        return jwtTokenRepository.save(refreshToken);
    }

    public void revokeToken(String token) {
        RefreshToken refreshToken = jwtTokenRepository.findByToken(token);
        if (refreshToken != null) {
            refreshToken.setRevoked(true);
            jwtTokenRepository.save(refreshToken);
        }
    }

    public void revokeAllUserTokens(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        
        List<RefreshToken> userTokens = jwtTokenRepository.findByUserAndRevokedFalse(user);
        userTokens.forEach(token -> {
            token.setRevoked(true);
            jwtTokenRepository.save(token);
        });
    }

    public RefreshToken getByToken(String token) {
        return jwtTokenRepository.findByToken(token);
    }

    public RefreshToken validateAndGetToken(String token) {
        RefreshToken refreshToken = jwtTokenRepository.findByToken(token);
        
        if (refreshToken == null) {
            throw new ResourceNotFoundException("Refresh token not found");
        }
        
        if (refreshToken.isRevoked()) {
            throw new UnauthorizedException("Refresh token has been revoked");
        }
        
        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Refresh token has expired");
        }
        
        return refreshToken;
    }

    public void cleanupExpiredTokens() {
        List<RefreshToken> expiredTokens = jwtTokenRepository.findByExpiryDateBefore(LocalDateTime.now());
        jwtTokenRepository.deleteAll(expiredTokens);
    }
}
