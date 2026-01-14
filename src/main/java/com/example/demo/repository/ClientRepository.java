package com.example.demo.repository;

import com.example.demo.model.Client;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    
    Optional<Client> findByEmail(String email);
    
    Optional<Client> findByUser(User user);
    
    boolean existsByEmail(String email);
}
