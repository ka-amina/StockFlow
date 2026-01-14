package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    private final DataSource dataSource;

    public HealthController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/db")
    public ResponseEntity<ApiResponse<Map<String, String>>> checkDatabaseHealth() {
        Map<String, String> healthStatus = new HashMap<>();
        
        try (Connection connection = dataSource.getConnection()) {
            boolean isValid = connection.isValid(1);
            healthStatus.put("status", isValid ? "UP" : "DOWN");
            healthStatus.put("database", connection.getMetaData().getDatabaseProductName());
            
            return ResponseEntity.ok(ApiResponse.success(healthStatus, "Database health check completed"));
        } catch (Exception e) {
            healthStatus.put("status", "DOWN");
            healthStatus.put("error", e.getMessage());
            return ResponseEntity.status(503)
                    .body(new ApiResponse<>("Database health check failed", healthStatus));
        }
    }
}
