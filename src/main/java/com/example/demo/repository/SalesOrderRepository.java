package com.example.demo.repository;

import com.example.demo.enums.SalesOrderStatus;
import com.example.demo.model.Client;
import com.example.demo.model.SalesOrder;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> {
    
    List<SalesOrder> findByClient(Client client);
    
    List<SalesOrder> findByStatus(SalesOrderStatus status);
    
    @Query("SELECT so FROM SalesOrder so WHERE so.client.user = :user")
    List<SalesOrder> findByClientUser(@Param("user") User user);
    
    @Query("SELECT so FROM SalesOrder so WHERE so.status = :status AND so.createdAt BETWEEN :startDate AND :endDate")
    List<SalesOrder> findByStatusAndDateRange(
            @Param("status") SalesOrderStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT COUNT(so) FROM SalesOrder so WHERE so.status = :status")
    long countByStatus(@Param("status") SalesOrderStatus status);
}
