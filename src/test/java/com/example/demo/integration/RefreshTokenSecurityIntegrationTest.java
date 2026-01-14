package com.example.demo.integration;

import com.example.demo.dto.LoginDTO;
import com.example.demo.dto.RefreshTokenRequestDTO;
import com.example.demo.model.RefreshToken;
import com.example.demo.repository.JwtTokenRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@DisplayName("🔄 Security Integration Tests - Refresh Token")
public class RefreshTokenSecurityIntegrationTest extends SecurityIntegrationTestBase {

    @Autowired
    private JwtTokenRepository jwtTokenRepository;

    @Test
    @DisplayName("  Valid refresh token should generate new tokens")
    public void testValidRefreshToken_ShouldGenerateNewTokens() throws Exception {
        // ARRANGE - Login to get refresh token
        LoginDTO loginRequest = new LoginDTO();
        loginRequest.setEmail(ADMIN_EMAIL);
        loginRequest.setPassword(ADMIN_PASSWORD);

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String loginResponse = loginResult.getResponse().getContentAsString();
        JsonNode loginJson = objectMapper.readTree(loginResponse);
        String refreshToken = loginJson.get("data").get("refreshToken").asText();
        String oldAccessToken = loginJson.get("data").get("accessToken").asText();

        // ACT - Use refresh token to get new tokens
        RefreshTokenRequestDTO refreshRequest = RefreshTokenRequestDTO.builder()
                .refreshToken(refreshToken)
                .build();

        MvcResult refreshResult = mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists())
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.refreshToken").isNotEmpty())
                .andReturn();

        // ASSERT - New tokens should be different from old ones
        String refreshResponse = refreshResult.getResponse().getContentAsString();
        JsonNode refreshJson = objectMapper.readTree(refreshResponse);
        String newAccessToken = refreshJson.get("data").get("accessToken").asText();
        String newRefreshToken = refreshJson.get("data").get("refreshToken").asText();

        assertNotEquals(oldAccessToken, newAccessToken, "New access token should be different");
        assertNotEquals(refreshToken, newRefreshToken, "New refresh token should be different (token rotation)");
    }

    @Test
    @DisplayName("  Invalid refresh token should be rejected")
    public void testInvalidRefreshToken_ShouldBeRejected() throws Exception {
        // ARRANGE
        RefreshTokenRequestDTO refreshRequest = RefreshTokenRequestDTO.builder()
                .refreshToken("invalid-refresh-token-12345")
                .build();

        // ACT & ASSERT
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("  Empty refresh token should be rejected")
    public void testEmptyRefreshToken_ShouldBeRejected() throws Exception {
        // ARRANGE
        RefreshTokenRequestDTO refreshRequest = RefreshTokenRequestDTO.builder()
                .refreshToken("")
                .build();

        // ACT & ASSERT
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("  Null refresh token should be rejected")
    public void testNullRefreshToken_ShouldBeRejected() throws Exception {
        // ARRANGE
        RefreshTokenRequestDTO refreshRequest = RefreshTokenRequestDTO.builder()
                .refreshToken(null)
                .build();

        // ACT & ASSERT
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("  Expired refresh token should be rejected")
    public void testExpiredRefreshToken_ShouldBeRejected() throws Exception {
        // ARRANGE - Create an expired refresh token
        RefreshToken expiredToken = RefreshToken.builder()
                .token("expired-token-" + System.currentTimeMillis())
                .user(adminUser)
                .expiryDate(LocalDateTime.now().minusDays(1))  // Expired yesterday
                .revoked(false)
                .build();
        jwtTokenRepository.save(expiredToken);

        RefreshTokenRequestDTO refreshRequest = RefreshTokenRequestDTO.builder()
                .refreshToken(expiredToken.getToken())
                .build();

        // ACT & ASSERT
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("  Revoked refresh token should be rejected")
    public void testRevokedRefreshToken_ShouldBeRejected() throws Exception {
        // ARRANGE - Login to get refresh token
        LoginDTO loginRequest = new LoginDTO();
        loginRequest.setEmail(ADMIN_EMAIL);
        loginRequest.setPassword(ADMIN_PASSWORD);

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String loginResponse = loginResult.getResponse().getContentAsString();
        JsonNode loginJson = objectMapper.readTree(loginResponse);
        String refreshToken = loginJson.get("data").get("refreshToken").asText();

        // Revoke the token
        RefreshToken token = jwtTokenRepository.findByToken(refreshToken);
        token.setRevoked(true);
        jwtTokenRepository.save(token);

        RefreshTokenRequestDTO refreshRequest = RefreshTokenRequestDTO.builder()
                .refreshToken(refreshToken)
                .build();

        // ACT & ASSERT
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("  Token rotation - Old refresh token should be revoked after use")
    public void testTokenRotation_OldRefreshTokenShouldBeRevoked() throws Exception {
        // ARRANGE - Login to get refresh token
        LoginDTO loginRequest = new LoginDTO();
        loginRequest.setEmail(WAREHOUSE_MANAGER_EMAIL);
        loginRequest.setPassword(WAREHOUSE_MANAGER_PASSWORD);

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String loginResponse = loginResult.getResponse().getContentAsString();
        JsonNode loginJson = objectMapper.readTree(loginResponse);
        String refreshToken = loginJson.get("data").get("refreshToken").asText();

        // ACT - Use refresh token once
        RefreshTokenRequestDTO refreshRequest = RefreshTokenRequestDTO.builder()
                .refreshToken(refreshToken)
                .build();

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk());

        // ASSERT - Try to use the same refresh token again (should fail)
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().is4xxClientError());

        // Verify the token is marked as revoked in database
        RefreshToken usedToken = jwtTokenRepository.findByToken(refreshToken);
        assertTrue(usedToken.isRevoked(), "Old refresh token should be revoked after use");
    }

    @Test
    @DisplayName("  Multiple users can refresh tokens independently")
    public void testMultipleUsersRefreshTokens_ShouldWorkIndependently() throws Exception {
        // ARRANGE - Login as admin
        LoginDTO adminLogin = new LoginDTO();
        adminLogin.setEmail(ADMIN_EMAIL);
        adminLogin.setPassword(ADMIN_PASSWORD);

        MvcResult adminLoginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminLogin)))
                .andExpect(status().isOk())
                .andReturn();

        String adminRefreshToken = objectMapper.readTree(adminLoginResult.getResponse().getContentAsString())
                .get("data").get("refreshToken").asText();

        // ARRANGE - Login as client
        LoginDTO clientLogin = new LoginDTO();
        clientLogin.setEmail(CLIENT1_EMAIL);
        clientLogin.setPassword(CLIENT1_PASSWORD);

        MvcResult clientLoginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientLogin)))
                .andExpect(status().isOk())
                .andReturn();

        String clientRefreshToken = objectMapper.readTree(clientLoginResult.getResponse().getContentAsString())
                .get("data").get("refreshToken").asText();

        // ACT & ASSERT - Both users can refresh independently
        RefreshTokenRequestDTO adminRefreshRequest = RefreshTokenRequestDTO.builder()
                .refreshToken(adminRefreshToken)
                .build();

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminRefreshRequest)))
                .andExpect(status().isOk());

        RefreshTokenRequestDTO clientRefreshRequest = RefreshTokenRequestDTO.builder()
                .refreshToken(clientRefreshToken)
                .build();

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientRefreshRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("  Refresh token should generate valid access token")
    public void testRefreshToken_NewAccessTokenShouldBeValid() throws Exception {
        // ARRANGE - Login to get refresh token
        LoginDTO loginRequest = new LoginDTO();
        loginRequest.setEmail(CLIENT1_EMAIL);
        loginRequest.setPassword(CLIENT1_PASSWORD);

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String refreshToken = objectMapper.readTree(loginResult.getResponse().getContentAsString())
                .get("data").get("refreshToken").asText();

        // ACT - Refresh to get new access token
        RefreshTokenRequestDTO refreshRequest = RefreshTokenRequestDTO.builder()
                .refreshToken(refreshToken)
                .build();

        MvcResult refreshResult = mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String newAccessToken = objectMapper.readTree(refreshResult.getResponse().getContentAsString())
                .get("data").get("accessToken").asText();

        // ASSERT - Use new access token to access protected resource (CLIENT can GET products)
        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer " + newAccessToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("  Malformed refresh token JSON should be rejected")
    public void testMalformedRefreshTokenJson_ShouldBeRejected() throws Exception {
        // ACT & ASSERT
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"invalidField\": \"value\"}"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("  Logout should revoke refresh token")
    public void testLogout_ShouldRevokeRefreshToken() throws Exception {
        // ARRANGE - Login to get refresh token
        LoginDTO loginRequest = new LoginDTO();
        loginRequest.setEmail(ADMIN_EMAIL);
        loginRequest.setPassword(ADMIN_PASSWORD);

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String refreshToken = objectMapper.readTree(loginResult.getResponse().getContentAsString())
                .get("data").get("refreshToken").asText();

        // ACT - Logout
        RefreshTokenRequestDTO logoutRequest = RefreshTokenRequestDTO.builder()
                .refreshToken(refreshToken)
                .build();

        mockMvc.perform(post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logoutRequest)))
                .andExpect(status().isOk());

        // ASSERT - Try to use the refresh token (should fail)
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logoutRequest)))
                .andExpect(status().is4xxClientError());

        // Verify the token is marked as revoked in database
        RefreshToken revokedToken = jwtTokenRepository.findByToken(refreshToken);
        assertTrue(revokedToken.isRevoked(), "Refresh token should be revoked after logout");
    }
}
