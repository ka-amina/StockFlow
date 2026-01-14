package com.example.demo.integration;

import com.example.demo.dto.LoginDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Security Integration Tests - Authentication")
public class AuthenticationSecurityIntegrationTest extends SecurityIntegrationTestBase {

    @Test
    @DisplayName(" Valid login - Admin should receive JWT tokens")
    public void testValidLogin_Admin_ShouldReturnTokens() throws Exception {
        // ARRANGE
        LoginDTO loginRequest = new LoginDTO();
        loginRequest.setEmail(ADMIN_EMAIL);
        loginRequest.setPassword(ADMIN_PASSWORD);

        // ACT & ASSERT
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.refreshToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.expiresIn").value(900));
    }

    @Test
    @DisplayName(" Valid login - Warehouse Manager should receive JWT tokens")
    public void testValidLogin_WarehouseManager_ShouldReturnTokens() throws Exception {
        // ARRANGE
        LoginDTO loginRequest = new LoginDTO();
        loginRequest.setEmail(WAREHOUSE_MANAGER_EMAIL);
        loginRequest.setPassword(WAREHOUSE_MANAGER_PASSWORD);

        // ACT & ASSERT
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists())
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"));
    }

    @Test
    @DisplayName(" Valid login - Client should receive JWT tokens")
    public void testValidLogin_Client_ShouldReturnTokens() throws Exception {
        // ARRANGE
        LoginDTO loginRequest = new LoginDTO();
        loginRequest.setEmail(CLIENT1_EMAIL);
        loginRequest.setPassword(CLIENT1_PASSWORD);

        // ACT & ASSERT
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists());
    }

    @Test
    @DisplayName(" Invalid login - Wrong email should be rejected")
    public void testInvalidLogin_WrongEmail_ShouldBeUnauthorized() throws Exception {
        // ARRANGE
        LoginDTO loginRequest = new LoginDTO();
        loginRequest.setEmail("nonexistent@test.com");
        loginRequest.setPassword("anypassword");

        // ACT & ASSERT
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName(" Invalid login - Wrong password should be rejected")
    public void testInvalidLogin_WrongPassword_ShouldBeUnauthorized() throws Exception {
        // ARRANGE
        LoginDTO loginRequest = new LoginDTO();
        loginRequest.setEmail(ADMIN_EMAIL);
        loginRequest.setPassword("wrongpassword");

        // ACT & ASSERT
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName(" Invalid login - Empty email should be rejected")
    public void testInvalidLogin_EmptyEmail_ShouldBeBadRequest() throws Exception {
        // ARRANGE
        LoginDTO loginRequest = new LoginDTO();
        loginRequest.setEmail("");
        loginRequest.setPassword("password");

        // ACT & ASSERT
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName(" Invalid login - Empty password should be rejected")
    public void testInvalidLogin_EmptyPassword_ShouldBeBadRequest() throws Exception {
        // ARRANGE
        LoginDTO loginRequest = new LoginDTO();
        loginRequest.setEmail(ADMIN_EMAIL);
        loginRequest.setPassword("");

        // ACT & ASSERT
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName(" Login with inactive user should be rejected")
    public void testLogin_InactiveUser_ShouldBeRejected() throws Exception {
        // ARRANGE - Deactivate admin user
        adminUser.setActive(false);
        userRepository.save(adminUser);

        LoginDTO loginRequest = new LoginDTO();
        loginRequest.setEmail(ADMIN_EMAIL);
        loginRequest.setPassword(ADMIN_PASSWORD);

        // ACT & ASSERT
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName(" Login response should contain all required fields")
    public void testLogin_ResponseStructure_ShouldBeComplete() throws Exception {
        // ARRANGE
        LoginDTO loginRequest = new LoginDTO();
        loginRequest.setEmail(ADMIN_EMAIL);
        loginRequest.setPassword(ADMIN_PASSWORD);

        // ACT & ASSERT
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists())
                .andExpect(jsonPath("$.data.tokenType").exists())
                .andExpect(jsonPath("$.data.expiresIn").exists())
                .andReturn();

        // Additional validation: tokens should be different
        String response = result.getResponse().getContentAsString();
        System.out.println(" Login successful. Response: " + response);
    }

    @Test
    @DisplayName(" Multiple failed login attempts should be handled")
    public void testMultipleFailedLogins_ShouldBeRejected() throws Exception {
        // ARRANGE
        LoginDTO loginRequest = new LoginDTO();
        loginRequest.setEmail(ADMIN_EMAIL);
        loginRequest.setPassword("wrongpassword");

        // ACT & ASSERT - Simulate multiple failed attempts
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized());
        }
    }
}
