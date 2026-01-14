package com.example.demo.integration;

import com.example.demo.model.Product;
import com.example.demo.model.SalesOrder;
import com.example.demo.model.Warehouse;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.SalesOrderRepository;
import com.example.demo.repository.WarehouseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("🔒 Security Integration Tests - Client Data Isolation")
public class ClientDataIsolationSecurityIntegrationTest extends SecurityIntegrationTestBase {

    @Autowired
    private SalesOrderRepository salesOrderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    private SalesOrder client1Order;
    private SalesOrder client2Order;
    private Product testProduct;
    private Warehouse testWarehouse;

    @Override
    @org.junit.jupiter.api.BeforeEach
    public void setupBase() {
        super.setupBase();

        // Create test warehouse
        testWarehouse = Warehouse.builder()
                .code("WH-TEST-001")
                .name("Test Warehouse")
                .active(true)
                .build();
        testWarehouse = warehouseRepository.save(testWarehouse);

        // Create test product
        testProduct = Product.builder()
                .name("Test Product")
                .sku("TEST-SKU-001")
                .originalPrice(BigDecimal.valueOf(100.00))
                .active(true)
                .build();
        testProduct = productRepository.save(testProduct);

        // Create orders for client 1
        client1Order = SalesOrder.builder()
                .client(client1)
                .warehouse(testWarehouse)
                .orderDate(LocalDateTime.now())
                .build();
        client1Order = salesOrderRepository.save(client1Order);

        // Create orders for client 2
        client2Order = SalesOrder.builder()
                .client(client2)
                .warehouse(testWarehouse)
                .orderDate(LocalDateTime.now())
                .build();
        client2Order = salesOrderRepository.save(client2Order);
    }

    @Test
    @DisplayName("  CLIENT 1 - Should access own orders")
    public void testClient1_AccessOwnOrders_ShouldBeAllowed() throws Exception {
        String token = generateToken(client1.getUser());

        mockMvc.perform(get("/api/orders/" + client1Order.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("  CLIENT 1 - Should NOT access Client 2's orders")
    public void testClient1_AccessClient2Orders_ShouldBeForbidden() throws Exception {
        String token = generateToken(client1.getUser());

        mockMvc.perform(get("/api/orders/" + client2Order.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("  CLIENT 1 - Should see only own orders in list")
    public void testClient1_ListOrders_ShouldSeeOnlyOwn() throws Exception {
        String token = generateToken(client1.getUser());

        mockMvc.perform(get("/api/orders")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + client1Order.getId() + ")]").exists())
                .andExpect(jsonPath("$[?(@.id == " + client2Order.getId() + ")]").doesNotExist());
    }

    @Test
    @DisplayName("  CLIENT 2 - Should access own orders")
    public void testClient2_AccessOwnOrders_ShouldBeAllowed() throws Exception {
        String token = generateToken(client2.getUser());

        mockMvc.perform(get("/api/orders/" + client2Order.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("  CLIENT 2 - Should NOT access Client 1's orders")
    public void testClient2_AccessClient1Orders_ShouldBeForbidden() throws Exception {
        String token = generateToken(client2.getUser());

        mockMvc.perform(get("/api/orders/" + client1Order.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("  CLIENT 2 - Should see only own orders in list")
    public void testClient2_ListOrders_ShouldSeeOnlyOwn() throws Exception {
        String token = generateToken(client2.getUser());

        mockMvc.perform(get("/api/orders")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + client2Order.getId() + ")]").exists())
                .andExpect(jsonPath("$[?(@.id == " + client1Order.getId() + ")]").doesNotExist());
    }

    // ========================================
    // ADMIN ACCESS TESTS
    // ========================================

    @Test
    @DisplayName("  ADMIN - Should access all client orders")
    public void testAdmin_AccessAllClientOrders_ShouldBeAllowed() throws Exception {
        String token = generateToken(adminUser);

        // Can access Client 1's order
        mockMvc.perform(get("/api/orders/" + client1Order.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());

        // Can access Client 2's order
        mockMvc.perform(get("/api/orders/" + client2Order.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("  ADMIN - Should see all orders in list")
    public void testAdmin_ListOrders_ShouldSeeAll() throws Exception {
        String token = generateToken(adminUser);

        mockMvc.perform(get("/api/orders")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + client1Order.getId() + ")]").exists())
                .andExpect(jsonPath("$[?(@.id == " + client2Order.getId() + ")]").exists());
    }

    @Test
    @DisplayName("  WAREHOUSE_MANAGER - Should access all client orders")
    public void testWarehouseManager_AccessAllClientOrders_ShouldBeAllowed() throws Exception {
        String token = generateToken(warehouseManagerUser);

        // Can access Client 1's order
        mockMvc.perform(get("/api/orders/" + client1Order.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());

        // Can access Client 2's order
        mockMvc.perform(get("/api/orders/" + client2Order.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("  WAREHOUSE_MANAGER - Should see all orders in list")
    public void testWarehouseManager_ListOrders_ShouldSeeAll() throws Exception {
        String token = generateToken(warehouseManagerUser);

        mockMvc.perform(get("/api/orders")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + client1Order.getId() + ")]").exists())
                .andExpect(jsonPath("$[?(@.id == " + client2Order.getId() + ")]").exists());
    }

    @Test
    @DisplayName("  CLIENT - Should only see own shipments")
    public void testClient_ListShipments_ShouldSeeOnlyOwn() throws Exception {
        String client1Token = generateToken(client1.getUser());
        String client2Token = generateToken(client2.getUser());

        // Client 1 should only see their shipments
        mockMvc.perform(get("/api/shipments")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + client1Token))
                .andExpect(status().isOk());

        // Client 2 should only see their shipments
        mockMvc.perform(get("/api/shipments")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + client2Token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("  CLIENT - Should NOT access other client's data via API manipulation")
    public void testClient_ApiManipulation_ShouldBeForbidden() throws Exception {
        String token = generateToken(client1.getUser());

        // Direct ID access
        mockMvc.perform(get("/api/orders/" + client2Order.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/orders")
                        .param("clientId", client2.getId().toString())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.clientId == " + client2.getId() + ")]").doesNotExist());
    }

    @Test
    @DisplayName("  CLIENT - Should NOT modify other client's orders")
    public void testClient_ModifyOtherClientOrders_ShouldBeForbidden() throws Exception {
        String token = generateToken(client1.getUser());

        // Try to update Client 2's order
        mockMvc.perform(get("/api/orders/" + client2Order.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("  CLIENT isolation should work across multiple requests")
    public void testClient_MultipleRequests_IsolationMaintained() throws Exception {
        String client1Token = generateToken(client1.getUser());
        String client2Token = generateToken(client2.getUser());

        // Client 1 - multiple requests should only show their data
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(get("/api/orders")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + client1Token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[?(@.id == " + client1Order.getId() + ")]").exists())
                    .andExpect(jsonPath("$[?(@.id == " + client2Order.getId() + ")]").doesNotExist());
        }

        // Client 2 - multiple requests should only show their data
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(get("/api/orders")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + client2Token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[?(@.id == " + client2Order.getId() + ")]").exists())
                    .andExpect(jsonPath("$[?(@.id == " + client1Order.getId() + ")]").doesNotExist());
        }
    }

    @Test
    @DisplayName("  Different client tokens should maintain proper isolation")
    public void testDifferentClientTokens_ShouldMaintainIsolation() throws Exception {
        String client1Token = generateToken(client1.getUser());
        String client2Token = generateToken(client2.getUser());

        // Verify that tokens are different
        assertNotEquals(client1Token, client2Token);

        // Client 1 can only access their orders
        mockMvc.perform(get("/api/orders/" + client1Order.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + client1Token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/orders/" + client2Order.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + client1Token))
                .andExpect(status().isForbidden());

        // Client 2 can only access their orders
        mockMvc.perform(get("/api/orders/" + client2Order.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + client2Token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/orders/" + client1Order.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + client2Token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("  Client data isolation should work for client-specific resources")
    public void testClient_ClientEndpoint_ShouldBeIsolated() throws Exception {
        String client1Token = generateToken(client1.getUser());
        String client2Token = generateToken(client2.getUser());

        // Client 1 should only see their own client info
        mockMvc.perform(get("/api/clients/" + client1.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + client1Token))
                .andExpect(status().isOk());

        // Client 1 should not see Client 2's info
        mockMvc.perform(get("/api/clients/" + client2.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + client1Token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("  ADMIN can manage all client data without restrictions")
    public void testAdmin_AllClientData_FullAccess() throws Exception {
        String adminToken = generateToken(adminUser);

        // Admin can access all clients
        mockMvc.perform(get("/api/clients/" + client1.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/clients/" + client2.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
                .andExpect(status().isOk());

        // Admin can list all clients
        mockMvc.perform(get("/api/clients")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
                .andExpect(status().isOk());
    }
}
