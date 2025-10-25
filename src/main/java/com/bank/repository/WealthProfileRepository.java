package com.bank.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bank.model.WealthProfile;

public interface WealthProfileRepository extends JpaRepository<WealthProfile, Long> {
    
    Optional<WealthProfile> findByCustomerId(Long customerId);
    
    @Query("SELECT w FROM WealthProfile w WHERE w.customer.user.username = :username")
    Optional<WealthProfile> findByUsername(@Param("username") String username);
    
    boolean existsByCustomerId(Long customerId);
}
