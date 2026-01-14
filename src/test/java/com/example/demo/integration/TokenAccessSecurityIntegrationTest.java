package com.example.demo.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName(" Security Integration Tests - Token Access")
public class TokenAccessSecurityIntegrationTest extends SecurityIntegrationTestBase {

    @Test
    @DisplayName("  Access with valid token should be allowed")
    public void testAccessWithValidToken_ShouldBeAllowed() throws Exception {
        // ARRANGE
        String token = generateToken(adminUser);

        // ACT & ASSERT
        mockMvc.perform(get("/api/products")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("  Access without token should be forbidden")
    public void testAccessWithoutToken_ShouldBeForbidden() throws Exception {
        // ACT & ASSERT
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("  Access with malformed token should be rejected")
    public void testAccessWithMalformedToken_ShouldBeRejected() throws Exception {
        // ACT & ASSERT
        mockMvc.perform(get("/api/products")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer invalid.token.format"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("  Access with token without Bearer prefix should be rejected")
    public void testAccessWithoutBearerPrefix_ShouldBeRejected() throws Exception {
        // ARRANGE
        String token = generateToken(adminUser);

        // ACT & ASSERT
        mockMvc.perform(get("/api/products")
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("  Access with empty Authorization header should be rejected")
    public void testAccessWithEmptyAuthorizationHeader_ShouldBeRejected() throws Exception {
        // ACT & ASSERT
        mockMvc.perform(get("/api/products")
                        .header(HttpHeaders.AUTHORIZATION, ""))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("  Access with null token should be rejected")
    public void testAccessWithNullToken_ShouldBeRejected() throws Exception {
        // ACT & ASSERT
        mockMvc.perform(get("/api/products")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer null"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("  Public endpoints should be accessible without token - Auth")
    public void testPublicEndpoint_Auth_ShouldBeAccessible() throws Exception {
        // ACT & ASSERT
        mockMvc.perform(get("/api/auth/check"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("  Public endpoints should be accessible without token - Health")
    public void testPublicEndpoint_Health_ShouldBeAccessible() throws Exception {
        // ACT & ASSERT
        mockMvc.perform(get("/api/health/db"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("  Protected endpoints should reject access without token - Products")
    public void testProtectedEndpoint_Products_ShouldRejectWithoutToken() throws Exception {
        // ACT & ASSERT
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("  Protected endpoints should reject access without token - Warehouses")
    public void testProtectedEndpoint_Warehouses_ShouldRejectWithoutToken() throws Exception {
        // ACT & ASSERT
        mockMvc.perform(get("/api/warehouses"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("  Protected endpoints should reject access without token - Inventory")
    public void testProtectedEndpoint_Inventory_ShouldRejectWithoutToken() throws Exception {
        // ACT & ASSERT
        mockMvc.perform(get("/api/inventory"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("  Protected endpoints should reject access without token - Orders")
    public void testProtectedEndpoint_Orders_ShouldRejectWithoutToken() throws Exception {
        // ACT & ASSERT
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("  Protected endpoints should reject access without token - Shipments")
    public void testProtectedEndpoint_Shipments_ShouldRejectWithoutToken() throws Exception {
        // ACT & ASSERT
        mockMvc.perform(get("/api/shipments"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("  Protected endpoints should reject access without token - Suppliers")
    public void testProtectedEndpoint_Suppliers_ShouldRejectWithoutToken() throws Exception {
        // ACT & ASSERT
        mockMvc.perform(get("/api/suppliers"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("  Protected endpoints should reject access without token - Carriers")
    public void testProtectedEndpoint_Carriers_ShouldRejectWithoutToken() throws Exception {
        // ACT & ASSERT
        mockMvc.perform(get("/api/carriers"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("  Protected endpoints should reject access without token - Clients")
    public void testProtectedEndpoint_Clients_ShouldRejectWithoutToken() throws Exception {
        // ACT & ASSERT
        mockMvc.perform(get("/api/clients"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("  Admin endpoints should reject access without token")
    public void testAdminEndpoint_ShouldRejectWithoutToken() throws Exception {
        // ACT & ASSERT
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("  Valid token should work for multiple requests")
    public void testValidToken_MultipleRequests_ShouldWork() throws Exception {
        // ARRANGE
        String token = generateToken(warehouseManagerUser);

        // ACT & ASSERT - Multiple requests with same token
        mockMvc.perform(get("/api/products")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/inventory")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/warehouses")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("  Different users' tokens should work independently")
    public void testDifferentUsersTokens_ShouldWorkIndependently() throws Exception {
        // ARRANGE
        String adminToken = generateToken(adminUser);
        String warehouseToken = generateToken(warehouseManagerUser);
        String clientToken = generateToken(clientUser1);

        // ACT & ASSERT - Each user can access with their own token
        mockMvc.perform(get("/api/products")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/products")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + warehouseToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/products")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + clientToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("  Token with extra spaces should be rejected")
    public void testTokenWithExtraSpaces_ShouldBeRejected() throws Exception {
        // ARRANGE
        String token = generateToken(adminUser);

        // ACT & ASSERT
        mockMvc.perform(get("/api/products")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer  " + token + " "))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("  Token with wrong prefix should be rejected")
    public void testTokenWithWrongPrefix_ShouldBeRejected() throws Exception {
        // ARRANGE
        String token = generateToken(adminUser);

        // ACT & ASSERT
        mockMvc.perform(get("/api/products")
                        .header(HttpHeaders.AUTHORIZATION, "Basic " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("  Case sensitive Bearer prefix - lowercase should be rejected")
    public void testLowercaseBearerPrefix_ShouldBeRejected() throws Exception {
        // ARRANGE
        String token = generateToken(adminUser);

        // ACT & ASSERT
        mockMvc.perform(get("/api/products")
                        .header(HttpHeaders.AUTHORIZATION, "bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("  Token in Authorization header with correct format should work")
    public void testCorrectAuthorizationFormat_ShouldWork() throws Exception {
        // ARRANGE
        String token = generateToken(adminUser);

        // ACT & ASSERT
        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
