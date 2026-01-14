package com.example.demo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    private final CaseSensitiveBearerTokenFilter caseSensitiveBearerTokenFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(customiser -> customiser.disable())
                .authorizeHttpRequests(request -> request
                        // Public endpoints
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/users").permitAll()  // Allow user registration
                        .requestMatchers("/api/health/**").permitAll()  // Allow health checks
                        
                        // Product endpoints - granular access
                        .requestMatchers(HttpMethod.GET, "/api/products/**").hasAnyRole("ADMIN", "WAREHOUSE_MANAGER", "CLIENT")
                        .requestMatchers(HttpMethod.POST, "/api/products/**").hasAnyRole("ADMIN", "WAREHOUSE_MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/products/**").hasAnyRole("ADMIN", "WAREHOUSE_MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")
                        
                        // Warehouse endpoints
                        .requestMatchers(HttpMethod.GET, "/api/warehouses/**").hasAnyRole("ADMIN", "WAREHOUSE_MANAGER", "CLIENT")
                        .requestMatchers(HttpMethod.POST, "/api/warehouses/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/warehouses/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/warehouses/**").hasRole("ADMIN")
                        
                        // Inventory endpoints
                        .requestMatchers(HttpMethod.GET, "/api/inventory/**").hasAnyRole("ADMIN", "WAREHOUSE_MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/inventory/**").hasAnyRole("ADMIN", "WAREHOUSE_MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/inventory/**").hasAnyRole("ADMIN", "WAREHOUSE_MANAGER")
                        
                        // Supplier endpoints
                        .requestMatchers(HttpMethod.GET, "/api/suppliers/**").hasAnyRole("ADMIN", "WAREHOUSE_MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/suppliers/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/suppliers/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/suppliers/**").hasRole("ADMIN")
                        
                        // Purchase Order endpoints
                        .requestMatchers(HttpMethod.GET, "/api/purchase-orders/**").hasAnyRole("ADMIN", "WAREHOUSE_MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/purchase-orders/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/purchase-orders/**").hasRole("ADMIN")
                        
                        // Sales Order endpoints - clients can access their own
                        .requestMatchers("/api/orders/**").hasAnyRole("ADMIN", "WAREHOUSE_MANAGER", "CLIENT")
                        
                        // Shipment endpoints
                        .requestMatchers(HttpMethod.GET, "/api/shipments/**").hasAnyRole("ADMIN", "WAREHOUSE_MANAGER", "CLIENT")
                        .requestMatchers(HttpMethod.POST, "/api/shipments/**").hasRole("WAREHOUSE_MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/shipments/**").hasRole("WAREHOUSE_MANAGER")
                        
                        // Carrier endpoints
                        .requestMatchers(HttpMethod.GET, "/api/carriers/**").hasAnyRole("ADMIN", "WAREHOUSE_MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/carriers/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/carriers/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/carriers/**").hasRole("ADMIN")
                        
                        // Client endpoints
                        .requestMatchers("/api/clients/**").hasAnyRole("ADMIN", "WAREHOUSE_MANAGER", "CLIENT")
                        
                        // Admin endpoints
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(caseSensitiveBearerTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean


    public JwtDecoder jwtDecoder() {
        String SECRET_KEY = "my-super-secret-key-my-super-secret-key";
        SecretKey key = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(key).build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
            List<String> roles = jwt.getClaimAsStringList("roles");
            if (roles != null) {
                roles.forEach(role ->
                        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role))
                );
            }
            List<String> permissions = jwt.getClaimAsStringList("permissions");
            if (permissions != null) {
                permissions.forEach(permission ->
                        grantedAuthorities.add(new SimpleGrantedAuthority(permission))
                );
            }
            return grantedAuthorities;
        });
        return converter;
    }
}
