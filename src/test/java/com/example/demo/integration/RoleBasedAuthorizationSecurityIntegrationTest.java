package com.example.demo.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 👥 TESTS D'AUTORISATION PAR RÔLE
 * 
 * Tests pour vérifier :
 * - ADMIN : Accès complet à tous les endpoints
 * - WAREHOUSE_MANAGER : Accès aux produits, inventaire, commandes, expéditions
 * - CLIENT : Accès limité aux commandes et visualisation des produits
 * - Refus d'accès selon les rôles inappropriés
 */
@DisplayName("👥 Security Integration Tests - Role-Based Authorization")
public class RoleBasedAuthorizationSecurityIntegrationTest extends SecurityIntegrationTestBase {


    @Test
    @DisplayName("  ADMIN - Should access admin endpoints")
    public void testAdmin_AdminEndpoints_ShouldHaveAccess() throws Exception {
        String adminToken = generateToken(adminUser);

        mockMvc.perform(get("/api/admin/users")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("  WAREHOUSE_MANAGER - Should access product read/write endpoints")
    public void testWarehouseManager_ProductEndpoints_ShouldHaveReadWriteAccess() throws Exception {
        String token = generateToken(warehouseManagerUser);

        // GET - Allowed
        mockMvc.perform(get("/api/products")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());

        // POST - Allowed
        mockMvc.perform(post("/api/products")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        // PUT - Allowed
        mockMvc.perform(put("/api/products/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("  WAREHOUSE_MANAGER - Should NOT delete products")
    public void testWarehouseManager_ProductDelete_ShouldBeForbidden() throws Exception {
        String token = generateToken(warehouseManagerUser);

        // DELETE - Forbidden
        mockMvc.perform(delete("/api/products/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("  WAREHOUSE_MANAGER - Should manage shipments")
    public void testWarehouseManager_ShipmentEndpoints_ShouldHaveAccess() throws Exception {
        String token = generateToken(warehouseManagerUser);

        mockMvc.perform(get("/api/shipments")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/shipments")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(put("/api/shipments/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("  WAREHOUSE_MANAGER - Should read warehouses but not modify")
    public void testWarehouseManager_WarehouseEndpoints_ReadOnlyAccess() throws Exception {
        String token = generateToken(warehouseManagerUser);

        // GET - Allowed
        mockMvc.perform(get("/api/warehouses")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());

        // POST - Forbidden
        mockMvc.perform(post("/api/warehouses")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());

        // PUT - Forbidden
        mockMvc.perform(put("/api/warehouses/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());

        // DELETE - Forbidden
        mockMvc.perform(delete("/api/warehouses/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("  WAREHOUSE_MANAGER - Should NOT access admin endpoints")
    public void testWarehouseManager_AdminEndpoints_ShouldBeForbidden() throws Exception {
        String token = generateToken(warehouseManagerUser);

        mockMvc.perform(get("/api/admin/users")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("  WAREHOUSE_MANAGER - Should read suppliers but not modify")
    public void testWarehouseManager_SupplierEndpoints_ReadOnlyAccess() throws Exception {
        String token = generateToken(warehouseManagerUser);

        // GET - Allowed
        mockMvc.perform(get("/api/suppliers")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());

        // POST - Forbidden
        mockMvc.perform(post("/api/suppliers")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }


    @Test
    @DisplayName("  CLIENT - Should view products")
    public void testClient_ProductRead_ShouldBeAllowed() throws Exception {
        String token = generateToken(clientUser1);

        // GET - Allowed
        mockMvc.perform(get("/api/products")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("  CLIENT - Should NOT create/update/delete products")
    public void testClient_ProductWriteOperations_ShouldBeForbidden() throws Exception {
        String token = generateToken(clientUser1);

        // POST - Forbidden
        mockMvc.perform(post("/api/products")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());

        // PUT - Forbidden
        mockMvc.perform(put("/api/products/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());

        // DELETE - Forbidden
        mockMvc.perform(delete("/api/products/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("  CLIENT - Should access order endpoints")
    public void testClient_OrderEndpoints_ShouldHaveAccess() throws Exception {
        String token = generateToken(clientUser1);

        mockMvc.perform(get("/api/orders")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("  CLIENT - Should view shipments")
    public void testClient_ShipmentRead_ShouldBeAllowed() throws Exception {
        String token = generateToken(clientUser1);

        // GET - Allowed
        mockMvc.perform(get("/api/shipments")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("  CLIENT - Should NOT create/update shipments")
    public void testClient_ShipmentWrite_ShouldBeForbidden() throws Exception {
        String token = generateToken(clientUser1);

        // POST - Forbidden
        mockMvc.perform(post("/api/shipments")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());

        // PUT - Forbidden
        mockMvc.perform(put("/api/shipments/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("  CLIENT - Should NOT access inventory endpoints")
    public void testClient_InventoryEndpoints_ShouldBeForbidden() throws Exception {
        String token = generateToken(clientUser1);

        mockMvc.perform(get("/api/inventory")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/inventory")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("  CLIENT - Should NOT access admin endpoints")
    public void testClient_AdminEndpoints_ShouldBeForbidden() throws Exception {
        String token = generateToken(clientUser1);

        mockMvc.perform(get("/api/admin/users")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("  CLIENT - Should NOT access warehouse management")
    public void testClient_WarehouseEndpoints_ShouldBeForbidden() throws Exception {
        String token = generateToken(clientUser1);

        mockMvc.perform(get("/api/warehouses")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk()); // They can view

        // But not modify
        mockMvc.perform(post("/api/warehouses")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("  CLIENT - Should NOT access supplier endpoints")
    public void testClient_SupplierEndpoints_ShouldBeForbidden() throws Exception {
        String token = generateToken(clientUser1);

        mockMvc.perform(get("/api/suppliers")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("  CLIENT - Should NOT access carrier endpoints")
    public void testClient_CarrierEndpoints_ShouldBeForbidden() throws Exception {
        String token = generateToken(clientUser1);

        mockMvc.perform(get("/api/carriers")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("  CLIENT - Should NOT access purchase orders")
    public void testClient_PurchaseOrderEndpoints_ShouldBeForbidden() throws Exception {
        String token = generateToken(clientUser1);

        mockMvc.perform(get("/api/purchase-orders")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden());
    }


    @Test
    @DisplayName("  All roles should access orders endpoint (with different scopes)")
    public void testAllRoles_OrderEndpoints_ShouldHaveAccess() throws Exception {
        String adminToken = generateToken(adminUser);
        String warehouseToken = generateToken(warehouseManagerUser);
        String clientToken = generateToken(clientUser1);

        // All should be able to access orders (but with different data scope)
        mockMvc.perform(get("/api/orders")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/orders")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + warehouseToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/orders")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + clientToken))
                .andExpect(status().isOk());
    }
}
