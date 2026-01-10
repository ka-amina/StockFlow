package com.example.demo.service;


import com.example.demo.model.RefreshToken;
import com.example.demo.model.User;
import com.example.demo.repository.JwtTokenRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor

public class RefreshTokenService {
    private final JwtTokenRepository jwtTokenRepository;
    private final UserRepository userRepository;

    public RefreshToken saveRefreshToken(RefreshToken refreshToken, String username) {
        User user = userRepository.findByEmail(username).orElseThrow(()->new RuntimeException("user not found : "));

        refreshToken.setUser(user);
        return jwtTokenRepository.save(refreshToken);
    }

    public void revokeToken(String token) {
        RefreshToken refreshToken = jwtTokenRepository.findByToken(token);

        refreshToken.setRevoked(true);
        jwtTokenRepository.save(refreshToken);
    }

    public RefreshToken getByToken(String token) {
        return jwtTokenRepository.findByToken(token);
    }
}
