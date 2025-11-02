package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/")
public class DbHealthController {

    private final DataSource dataSource;

    @Autowired
    public DbHealthController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Simple endpoint to check DB connectivity.
     * Returns 200 + { connected: true, latencyMs: <ms> } when SELECT 1 succeeds.
     * Returns 503 + { connected: false, error: <msg>, latencyMs: <ms> } on failure.
     */
    @GetMapping("/db")
    public ResponseEntity<Map<String, Object>> checkDb() {
        Instant start = Instant.now();
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT 1")) {

            boolean ok = rs.next();
            long ms = Duration.between(start, Instant.now()).toMillis();
            return ResponseEntity.ok(Map.of(
                    "connected", ok,
                    "latencyMs", ms
            ));

        } catch (Exception e) {
            long ms = Duration.between(start, Instant.now()).toMillis();
            Map<String, Object> body = Map.of(
                    "connected", false,
                    "error", e.getMessage(),
                    "latencyMs", ms
            );
            return ResponseEntity.status(503).body(body);
        }
    }
}
