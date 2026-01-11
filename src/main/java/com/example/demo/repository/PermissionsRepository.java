package com.example.demo.repository;

import com.example.demo.model.Permissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository


public interface PermissionsRepository extends JpaRepository<Permissions , Long> {

    Permissions findByName(String permission);


}