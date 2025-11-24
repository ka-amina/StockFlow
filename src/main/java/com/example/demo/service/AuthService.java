package com.example.demo.service;

import com.example.demo.dto.AuthResponseDTO;
import com.example.demo.dto.LoginDTO;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepo;
    private static final String SESSION_USER_KEY = "authenticated_user";

    public AuthService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Transactional(readOnly = true)
    public AuthResponseDTO login(LoginDTO dto, HttpSession session) {
        // Find user by email
        User user = userRepo.findByEmail(dto.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Invalid email or password"));

        // Check if user is active
        if (!user.getActive()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Account is deactivated");
        }

        if (!user.getPasswordHash().equals(dto.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Invalid email or password");
        }

        // Store user in session
        session.setAttribute(SESSION_USER_KEY, user.getId());

        return AuthResponseDTO.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .message("Login successful")
                .build();
    }

    public void logout(HttpSession session) {
        session.invalidate();
    }

    public User getCurrentUser(HttpSession session) {
        Long userId = (Long) session.getAttribute(SESSION_USER_KEY);
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Not authenticated. Please login first.");
        }

        return userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "User session is invalid"));
    }

    public boolean isAuthenticated(HttpSession session) {
        return session.getAttribute(SESSION_USER_KEY) != null;
    }
}
