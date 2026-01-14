package com.example.demo.integration;

import com.example.demo.model.*;
import com.example.demo.repository.ClientRepository;
import com.example.demo.repository.RolesRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class SecurityIntegrationTestBase {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected RolesRepository roleRepository;

    @Autowired
    protected ClientRepository clientRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected JwtUtil jwtUtil;

    // Test users
    protected User adminUser;
    protected User warehouseManagerUser;
    protected User clientUser1;
    protected User clientUser2;

    // Test roles
    protected Role adminRole;
    protected Role warehouseManagerRole;
    protected Role clientRole;

    // Test clients
    protected Client client1;
    protected Client client2;

    // Test credentials
    protected static final String ADMIN_EMAIL = "admin@test.com";
    protected static final String ADMIN_PASSWORD = "admin123";

    protected static final String WAREHOUSE_MANAGER_EMAIL = "warehouse@test.com";
    protected static final String WAREHOUSE_MANAGER_PASSWORD = "warehouse123";

    protected static final String CLIENT1_EMAIL = "client1@test.com";
    protected static final String CLIENT1_PASSWORD = "client123";

    protected static final String CLIENT2_EMAIL = "client2@test.com";
    protected static final String CLIENT2_PASSWORD = "client456";

    @BeforeEach
    public void setupBase() {
        // Clear repositories
        userRepository.deleteAll();
        roleRepository.deleteAll();
        clientRepository.deleteAll();

        // Create roles
        adminRole = createRole("ADMIN");
        warehouseManagerRole = createRole("WAREHOUSE_MANAGER");
        clientRole = createRole("CLIENT");

        // Create test users
        adminUser = createUser(ADMIN_EMAIL, ADMIN_PASSWORD, adminRole);
        warehouseManagerUser = createUser(WAREHOUSE_MANAGER_EMAIL, WAREHOUSE_MANAGER_PASSWORD, warehouseManagerRole);
        clientUser1 = createUser(CLIENT1_EMAIL, CLIENT1_PASSWORD, clientRole);
        clientUser2 = createUser(CLIENT2_EMAIL, CLIENT2_PASSWORD, clientRole);

        // Create test clients
        client1 = createClient("Client 1", CLIENT1_EMAIL, clientUser1);
        client2 = createClient("Client 2", CLIENT2_EMAIL, clientUser2);
    }

    protected Role createRole(String roleName) {
        Role role = Role.builder()
                .roleName(roleName)
                .build();
        return roleRepository.save(role);
    }

    protected User createUser(String email, String password, Role role) {
        User user = User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .active(true)
                .role(role)
                .build();
        return userRepository.save(user);
    }

    protected Client createClient(String name, String email, User user) {
        Client client = Client.builder()
                .name(name)
                .email(email)
                .user(user)
                .active(true)
                .build();
        return clientRepository.save(client);
    }


    protected String generateToken(User user) {
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPasswordHash())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().getRoleName())))
                .build();
        return jwtUtil.generateAccessToken(userDetails);
    }

}
