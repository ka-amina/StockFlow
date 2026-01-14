package com.example.demo.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;


@Suite
@SelectClasses({
    AuthenticationSecurityIntegrationTest.class,
    TokenAccessSecurityIntegrationTest.class,
    RefreshTokenSecurityIntegrationTest.class,
    RoleBasedAuthorizationSecurityIntegrationTest.class,
    ClientDataIsolationSecurityIntegrationTest.class
})
@DisplayName(" Security Integration Test Suite - Complete")
public class SecurityIntegrationTestSuite {


}
