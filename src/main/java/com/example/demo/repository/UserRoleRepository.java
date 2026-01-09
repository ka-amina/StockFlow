package com.example.demo.repository;

import com.example.demo.model.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<Roles, Long> {
    Optional<Roles> findByRoleName(String roleName);
}
